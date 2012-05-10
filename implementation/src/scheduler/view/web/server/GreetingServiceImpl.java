package scheduler.view.web.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OptionalDataException;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.Vector;

import scheduler.model.Course;
import scheduler.model.Document;
import scheduler.model.Instructor;
import scheduler.model.Location;
import scheduler.model.Model;
import scheduler.model.ScheduleItem;
import scheduler.model.User;
import scheduler.model.algorithm.BadInstructorDataException;
import scheduler.model.algorithm.GenerateEntryPoint;
import scheduler.model.db.DatabaseException;
import scheduler.view.web.client.GreetingService;
import scheduler.view.web.client.InvalidLoginException;
import scheduler.view.web.shared.ClientChangesResponse;
import scheduler.view.web.shared.CompleteWorkingCopyDocumentGWT;
import scheduler.view.web.shared.CouldNotBeScheduledExceptionGWT;
import scheduler.view.web.shared.CourseGWT;
import scheduler.view.web.shared.DocumentGWT;
import scheduler.view.web.shared.InstructorGWT;
import scheduler.view.web.shared.LocationGWT;
import scheduler.view.web.shared.LoginResponse;
import scheduler.view.web.shared.OriginalDocumentGWT;
import scheduler.view.web.shared.ScheduleItemGWT;
import scheduler.view.web.shared.ServerResourcesResponse;
import scheduler.view.web.shared.SessionClosedFromInactivityExceptionGWT;
import scheduler.view.web.shared.SynchronizeRequest;
import scheduler.view.web.shared.SynchronizeResponse;
import scheduler.view.web.shared.WorkingDocumentGWT;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class GreetingServiceImpl extends RemoteServiceServlet implements GreetingService {
	private static final boolean LOG_ENTERING_AND_EXITING_CALLS = true;
	
	Properties readPropertiesFile() throws IOException {
		Properties properties = new Properties();
		InputStream in = GreetingServiceImpl.class.getResourceAsStream("scheduler.properties");
		if (in == null)
			throw new IOException("Couldnt load scheduler.properties (make sure its in GreetingServiceImpl's directory)");
		properties.load(in);
		in.close();
		return properties;
	}
	
	private boolean loadAndSaveFromFileSystem;
	public Model model;
	
	
	
	
	static class SessionList {
		static class Session {
			static class OpenDocument {
				Date lastActivity;
				
				public OpenDocument(Date lastActivity) {
					this.lastActivity = lastActivity;
				}
			}
			
			public final int id;
			public final User user;
			Date lastActivity;
			public Map<Integer, OpenDocument> openDocumentsByDocumentID = new HashMap<Integer, OpenDocument>();
			
			Session(int id, User user, Date lastActivity) {
				this.id = id;
				this.user = user;
				this.lastActivity = lastActivity;
			}
			
			void onOpenedDocument(int documentID) {
				openDocumentsByDocumentID.put(documentID, new OpenDocument(new Date()));
			}
			
			void onContacted(int documentID) throws SessionClosedFromInactivityExceptionGWT {
				OpenDocument ods = openDocumentsByDocumentID.get(documentID);
				if (ods == null)
					throw new SessionClosedFromInactivityExceptionGWT();
				ods.lastActivity = new Date();
			}
			
			void closeOpenDocumentsWithNoContactSince(Date since) {
				for (Integer openDocumentID : new HashSet<Integer>(openDocumentsByDocumentID.keySet())) {
					OpenDocument openDocument = openDocumentsByDocumentID.get(openDocumentID);
					assert(openDocument != null);
					
					if (openDocument.lastActivity.before(since))
						openDocumentsByDocumentID.remove(openDocumentID);
				}
			}
		}

		int nextSessionID = 1;
		Map<Integer, Session> sessionsByID = new HashMap<Integer, Session>();
		
		public int createSession(User user) {
			Session newSession = new Session(nextSessionID++, user, new Date());
			sessionsByID.put(newSession.id, newSession);
			
			System.out.println("Making new session: " + newSession.id + " for user " + user.getUsername());
			
			return newSession.id;
		}
		
		public void openDocument(int sessionID, int workingDocumentID) throws SessionClosedFromInactivityExceptionGWT {
			Session session = sessionsByID.get(sessionID);
			assert(session != null);
			
			session.openDocumentsByDocumentID.put(workingDocumentID, new Session.OpenDocument(new Date()));
			
			System.out.println("Opened document with id " + workingDocumentID);
		}
		
		public Session onActivity(int sessionID) throws SessionClosedFromInactivityExceptionGWT {
			Session session = sessionsByID.get(sessionID);
			if (session == null)
				throw new SessionClosedFromInactivityExceptionGWT();
			session.lastActivity = new Date();
			
			return session;
		}
		
		public Session onActivity(int sessionID, int workingCopyDocumentID) throws SessionClosedFromInactivityExceptionGWT {
			Session session = onActivity(sessionID);
			
			Session.OpenDocument openDocument = session.openDocumentsByDocumentID.get(workingCopyDocumentID);
			if (openDocument == null)
				throw new SessionClosedFromInactivityExceptionGWT();
			openDocument.lastActivity = new Date();
			
			return session;
		}

		public void closeDocumentsAndSessionsWithNoContactSince(Date since) {
			for (Integer sessionID : new HashSet<Integer>(sessionsByID.keySet())) {
				Session session = sessionsByID.get(sessionID);
				assert(session != null);
				
				session.closeOpenDocumentsWithNoContactSince(since);
				
				if (session.openDocumentsByDocumentID.isEmpty())
					sessionsByID.remove(sessionID);
			}
		}
		
		public Set<String> findAllUsersWithWorkingDocumentOpen(int workingDocumentID) {
			Set<String> result = new HashSet<String>();
			for (Session session : sessionsByID.values())
				if (session.openDocumentsByDocumentID.containsKey(workingDocumentID))
					result.add(session.user.getUsername());
			return result;
		}
	}
	
	SessionList sessions = new SessionList();
	
	public GreetingServiceImpl() {
		this(true);
	}
	
	private String getDatabaseStateFilepath() {
		String filepath;
		boolean applyServletPath;
		
		try {
			Properties properties = readPropertiesFile();
			assert(properties != null);
			
			filepath = properties.getProperty("databasefilepath");
			if (filepath == null)
				throw new Exception("filepath not set!");
			
			String useServletContextRealPathStr = properties.getProperty("useServletContextRealPath");
			if (useServletContextRealPathStr == null) {
				applyServletPath = false;
			}
			else {
				applyServletPath =
						useServletContextRealPathStr.equalsIgnoreCase("true") ||
						useServletContextRealPathStr.equalsIgnoreCase("yes") ||
						useServletContextRealPathStr.equalsIgnoreCase("1");
			}
		}
		catch (Exception e) {
			filepath = "DatabaseState.javaser";
			applyServletPath = true;
			
			e.printStackTrace();
			System.err.println("Couldnt load properties, continuing with defaults (filepath=\"" + filepath + "\" applyServletPath=" + applyServletPath + ")");
		}
		
		
		if (applyServletPath) {
			try {
				filepath = getServletContext().getRealPath(filepath);
				System.out.println("Applied servlet path, got: " + filepath);
			}
			catch (Exception e) {
				e.printStackTrace();
				System.err.println("Requested servlet context real path, but getServletContext().getRealPath() threw an exception. Continuing with filepath " + filepath);
			}
		}
		
		assert(filepath != null);
		return filepath;
	}
	
	public GreetingServiceImpl(boolean loadAndSaveFromFileSystem) {
		this.loadAndSaveFromFileSystem = loadAndSaveFromFileSystem;
		model = new Model();
		
		System.out.println("Loading and saving from file system?: " + loadAndSaveFromFileSystem);
		
		if (loadAndSaveFromFileSystem) {
			String filepath = getDatabaseStateFilepath();

			System.out.println("Using database state filepath: " + filepath);
			System.err.println("Using database state filepath: " + filepath);
			
			try {
				FileInputStream fos = new FileInputStream(filepath);
				ObjectInputStream ois = new ObjectInputStream(fos);
				
				model.readState(ois);
				
				ois.close();

				sanityCheck();
			}
			catch (FileNotFoundException e) {
				System.out.println("Database state file (" + filepath + ") doesn't exist, starting with a fresh model!");
				model = new Model();
			}
			catch (OptionalDataException e) {
				// TODO: Notify the user.
				System.out.println("Database state file (" + filepath + ") corrupted; starting with a fresh model!");
				model = new Model();
			}
			catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}
		
		try {
			// We dont use projector anymore. If this assert fails, you need to erase your database.
			assert(!model.getEquipmentTypes().contains("Projector"));
			
			Collection<String> equipmentTypes = model.getEquipmentTypes();
			if (equipmentTypes.size() == 0) {
				model.insertEquipmentType("Laptop Connectivity");
				model.insertEquipmentType("Overhead");
				model.insertEquipmentType("Smart Room");
			}
		}
		catch (DatabaseException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		
//		for (String equipmentType : model.getEquipmentTypes())
		
		sanityCheck();
	}
	
	private void sanityCheck() {
		try {
			for (Document doc : model.findAllDocuments()) {
				assert(doc.getTBALocation() != null);
				assert(doc.getTBALocation().getID() != null);
				assert(doc.getStaffInstructor() != null);
				assert(doc.getStaffInstructor().getID() != null);
				assert(doc.getChooseForMeInstructor() != null);
				assert(doc.getChooseForMeInstructor().getID() != null);
				assert(doc.getChooseForMeLocation() != null);
				assert(doc.getChooseForMeLocation().getID() != null);
			}
		}
		catch (DatabaseException e) {
			throw new RuntimeException(e);
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	// COURSES
	


	private CourseGWT addCourseToDocument(Document document, CourseGWT course) {
		if (LOG_ENTERING_AND_EXITING_CALLS)
			System.out.println("Begin GreetingServiceImpl.addCourseToDocument(doc " + document.getID() + ")");
		
		assert (course.getID() == null);
		
		try {
			Course resultCourse = Conversion.courseFromGWT(model, course).setDocument(document);
			
			int id = resultCourse.insert().getID();
			course.setID(id);
			
			sanityCheck();
			flushToFileSystem();

			if (LOG_ENTERING_AND_EXITING_CALLS)
				System.out.println("End GreetingServiceImpl.addCourseToDocument(" + document.getID() + ")");
			
			return course;
		}
		catch (DatabaseException e) {
			throw new RuntimeException(e);
		}
	}
	
	private void editCourse(CourseGWT source) {
		if (LOG_ENTERING_AND_EXITING_CALLS)
			System.out.println("Begin GreetingServiceImpl.editCourse with id " + source.getID() + ": " + source.getDept() + " " + source.getCatalogNum() + " with tethered " + source.getTetheredToLecture());
		try {
			Course course = model.findCourseByID(source.getID());
			assert (course.getID() > 0);
			
			assert (course.getDocument().getOriginal() != null);
			
			Conversion.readCourseFromGWT(source, course, model);
			
			course.update();
			
			sanityCheck();
			flushToFileSystem();

			if (LOG_ENTERING_AND_EXITING_CALLS)
				System.out.println("End GreetingServiceImpl.editCourse");
		}
		catch (DatabaseException e) {
			throw new RuntimeException(e);
		}
	}
	
	private ServerResourcesResponse<CourseGWT> getCoursesForDocument(int documentID) {
		try {
			if (LOG_ENTERING_AND_EXITING_CALLS)
				System.out.println("Begin GreetingServiceImpl.getCoursesForDocument(doc " + documentID + ")");
			
			Document document = model.findDocumentByID(documentID);
			assert(document.getOriginal() != null);
			assert(document.getStaffInstructor() != null);
			assert(document.getTBALocation() != null);
			assert(document.getChooseForMeInstructor() != null);
			assert(document.getChooseForMeLocation() != null);
			List<CourseGWT> result = new LinkedList<CourseGWT>();
			for (Course course : model.findCoursesForDocument(document)) {
//				System.out.println("for doc id " + documentID + " returning course name " + course.getName());
				result.add(Conversion.courseToGWT(course));
			}
			
			sanityCheck();

			if (LOG_ENTERING_AND_EXITING_CALLS)
				System.out.println("End GreetingServiceImpl.getCoursesForDocument(" + documentID + ")");
			
			return new ServerResourcesResponse<CourseGWT>(result);
		}
		catch (DatabaseException e) {
			throw new RuntimeException(e);
		}
	}
	
	private void removeCourse(Integer courseID) {
		try {
			if (LOG_ENTERING_AND_EXITING_CALLS)
				System.out.println("Begin GreetingServiceImpl.removeCourse(course " + courseID + ")");
			
			Course course = model.findCourseByID(courseID);
			assert (course.getDocument().getOriginal() != null);
			course.delete();
			
			sanityCheck();
			flushToFileSystem();

			if (LOG_ENTERING_AND_EXITING_CALLS)
				System.out.println("End GreetingServiceImpl.removeCourse(course " + courseID + ")");
		}
		catch (DatabaseException e) {
			throw new RuntimeException(e);
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	private InstructorGWT addInstructorToDocument(int documentID, InstructorGWT instructor) {
		assert (instructor.getID() == null);
		
		try {
			if (LOG_ENTERING_AND_EXITING_CALLS)
				System.out.println("Begin GreetingServiceImpl.addIntructorToDocument(doc " + documentID + " instructor username " + instructor.getUsername() + ")");
			
			Document document = model.findDocumentByID(documentID);
			assert (document.getOriginal() != null);
			assert(document.getStaffInstructor() != null);
			assert(document.getTBALocation() != null);
			assert(document.getChooseForMeInstructor() != null);
			assert(document.getChooseForMeLocation() != null);
			int id = Conversion.instructorFromGWT(model, instructor).setDocument(document).insert().getID();
			instructor.setID(id);
			
			sanityCheck();
			flushToFileSystem();

			if (LOG_ENTERING_AND_EXITING_CALLS)
				System.out.println("End GreetingServiceImpl.addIntructorToDocument(doc " + documentID + " instructor username " + instructor.getUsername() + ")");
			
			return instructor;
		}
		catch (DatabaseException e) {
			throw new RuntimeException(e);
		}
	}
	
	private void editInstructor(InstructorGWT source) {
		try {
			if (LOG_ENTERING_AND_EXITING_CALLS)
				System.out.println("Begin GreetingServiceImpl.editInstructor(instructor username " + source.getUsername() + ")");
			
			Instructor result = model.findInstructorByID(source.getID());
			assert (result.getDocument().getOriginal() != null);
			assert (result.getID() > 0);
			Conversion.readInstructorFromGWT(source, result);
			result.update();
			
			sanityCheck();
			flushToFileSystem();

			if (LOG_ENTERING_AND_EXITING_CALLS)
				System.out.println("End GreetingServiceImpl.editInstructor(instructor username " + source.getUsername() + ")");
		}
		catch (DatabaseException e) {
			throw new RuntimeException(e);
		}
	}
	
	private ServerResourcesResponse<InstructorGWT> getInstructorsForDocument(int documentID) {
		try {
			if (LOG_ENTERING_AND_EXITING_CALLS)
				System.out.println("Begin GreetingServiceImpl.getInstructorsForDocument(doc " + documentID +")");
			
			List<InstructorGWT> result = new LinkedList<InstructorGWT>();
			Document document = model.findDocumentByID(documentID);
			assert (document.getOriginal() != null);
			assert(document.getStaffInstructor() != null);
			assert(document.getTBALocation() != null);
			assert(document.getChooseForMeInstructor() != null);
			assert(document.getChooseForMeLocation() != null);
			for (Instructor instructor : model.findInstructorsForDocument(document, false)) {
				result.add(Conversion.instructorToGWT(instructor));
			}
			
			sanityCheck();
			
			if (LOG_ENTERING_AND_EXITING_CALLS)
				System.out.println("End GreetingServiceImpl.getInstructorsForDocument(doc " + documentID +")");
			
			return new ServerResourcesResponse<InstructorGWT>(result);
		}
		catch (DatabaseException e) {
			throw new RuntimeException(e);
		}
	}
	
	private void removeInstructor(Integer instructorID) {
		try {
			if (LOG_ENTERING_AND_EXITING_CALLS)
				System.out.println("Begin GreetingServiceImpl.removeInstructor(instructor " + instructorID +")");
			
			Instructor instructor = model.findInstructorByID(instructorID);
			assert (instructor.getDocument().getOriginal() != null);
			instructor.delete();
			
			sanityCheck();
			flushToFileSystem();

			if (LOG_ENTERING_AND_EXITING_CALLS)
				System.out.println("End GreetingServiceImpl.removeInstructor(instructor " + instructorID +")");
		}
		catch (DatabaseException e) {
			throw new RuntimeException(e);
		}
	}
	
	private LocationGWT addLocationToDocument(int documentID, LocationGWT location) {
		assert (location.getID() == null);
		
		try {
			if (LOG_ENTERING_AND_EXITING_CALLS)
				System.out.println("Begin GreetingServiceImpl.addLocationToDocument(doc " + documentID +" location room " + location.getRoom() + ")");
			
			Document document = model.findDocumentByID(documentID);
			assert (document.getOriginal() != null);
			assert(document.getStaffInstructor() != null);
			assert(document.getTBALocation() != null);
			assert(document.getChooseForMeInstructor() != null);
			assert(document.getChooseForMeLocation() != null);
			Location modelLocation = model.createTransientLocation(
					location.getRoom(), location.getType(), location.getRawMaxOccupancy(), true);
			modelLocation.setProvidedEquipment(location.getEquipment());
			int id = modelLocation.setDocument(document).insert().getID();
			location.setID(id);
			
			sanityCheck();
			flushToFileSystem();

			if (LOG_ENTERING_AND_EXITING_CALLS)
				System.out.println("End GreetingServiceImpl.addLocationToDocument(doc " + documentID +" location room " + location.getRoom() + ")");
			
			return location;
		}
		catch (DatabaseException e) {
			throw new RuntimeException(e);
		}
	}
	
	private void editLocation(LocationGWT source) {
		try {
			if (LOG_ENTERING_AND_EXITING_CALLS)
				System.out.println("Begin GreetingServiceImpl.editLocation(location room " + source.getRoom() + ")");
			
			Location result = model.findLocationByID(source.getID());
			assert (result.getID() > 0);
			assert (result.getDocument().getOriginal() != null);
			Conversion.readLocationFromGWT(source, result);
			result.update();
			
			sanityCheck();
			flushToFileSystem();

			if (LOG_ENTERING_AND_EXITING_CALLS)
				System.out.println("End GreetingServiceImpl.editLocation(location room " + source.getRoom() + ")");
		}
		catch (DatabaseException e) {
			throw new RuntimeException(e);
		}
	}
	
	private ServerResourcesResponse<LocationGWT> getLocationsForDocument(int documentID) {
		try {
			if (LOG_ENTERING_AND_EXITING_CALLS)
				System.out.println("Begin GreetingServiceImpl.getLocationsForDocument(doc " + documentID + ")");
			
			Document document = model.findDocumentByID(documentID);
			assert (document.getOriginal() != null);
			assert(document.getStaffInstructor() != null);
			assert(document.getTBALocation() != null);
			assert(document.getChooseForMeInstructor() != null);
			assert(document.getChooseForMeLocation() != null);
			List<LocationGWT> result = new LinkedList<LocationGWT>();
			for (Location location : model.findLocationsForDocument(document, false))
				result.add(Conversion.locationToGWT(location));
			
			sanityCheck();

			if (LOG_ENTERING_AND_EXITING_CALLS)
				System.out.println("End GreetingServiceImpl.getLocationsForDocument(doc " + documentID + ")");
			
			return new ServerResourcesResponse<LocationGWT>(result);
		}
		catch (DatabaseException e) {
			throw new RuntimeException(e);
		}
	}
	
	private void removeLocation(Integer locationID) {
		try {
			if (LOG_ENTERING_AND_EXITING_CALLS)
				System.out.println("Begin GreetingServiceImpl.removeLocation(location " + locationID + ")");
			
			Location location = model.findLocationByID(locationID);
			assert (location.getDocument().getOriginal() != null);
			location.delete();
			
			sanityCheck();
			flushToFileSystem();
		}
		catch (DatabaseException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public LoginResponse loginAndGetAllOriginalDocuments(String username) throws InvalidLoginException {
		User user = null;

		if (LOG_ENTERING_AND_EXITING_CALLS)
			System.out.println("Begin GreetingServiceImpl.loginAndGetAllOriginalDocuments(username " + username + ")");
		
		try {
			user = model.findUserByUsername(username);
		}
		catch (DatabaseException e) {
			try {
				user = model.createTransientUser(username, !username.equals("jjuszak")).insert();
			}
			catch (DatabaseException e2) {
				throw new RuntimeException(e2);
			}
		}

		int sessionID = sessions.createSession(user);
		
		sanityCheck();

		try {
			LoginResponse result = new LoginResponse(sessionID, user.isAdmin(), getAllOriginalDocuments(sessionID));

			if (LOG_ENTERING_AND_EXITING_CALLS)
				System.out.println("End GreetingServiceImpl.loginAndGetAllOriginalDocuments(username " + username + ")");
			
			return result;
		}
		catch (SessionClosedFromInactivityExceptionGWT e) {
			// We just made it, how can it be closed already?
			assert(false);
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public ServerResourcesResponse<OriginalDocumentGWT> getAllOriginalDocuments(int sessionID) throws SessionClosedFromInactivityExceptionGWT {
		SessionList.Session session = sessions.onActivity(sessionID);
		
		try {
			if (LOG_ENTERING_AND_EXITING_CALLS)
				System.out.println("Begin GreetingServiceImpl.getAllOriginalDocuments(session " + sessionID + ")");
			
			Collection<OriginalDocumentGWT> result = new LinkedList<OriginalDocumentGWT>();
			for (Document doc : model.findAllDocuments()) {
				assert(doc.getStaffInstructor() != null);
				assert(doc.getTBALocation() != null);
				assert(doc.getChooseForMeInstructor() != null);
				assert(doc.getChooseForMeLocation() != null);
				
				if (doc.isWorkingCopy()) {
					continue;
				}
				
				if (!session.user.isAdmin()) {
					boolean instructorPresent = false;
					for (Instructor instructor : doc.getInstructors(true))
						if (instructor.getUsername().equals(session.user.getUsername()))
							instructorPresent = true;
					if (!instructorPresent)
						continue;
				}
				
				OriginalDocumentGWT gwt = Conversion.originalDocumentToGWT(doc, summarizeWorkingChanges(doc));
				result.add(gwt);
			}
			
			sanityCheck();

			if (LOG_ENTERING_AND_EXITING_CALLS) {
				System.out.println("End GreetingServiceImpl.getAllOriginalDocuments(" + sessionID + ")");
			}
			
			return new ServerResourcesResponse<OriginalDocumentGWT>(result);
		}
		catch (DatabaseException e) {
			throw new RuntimeException(e);
		}
	}
	
	private String summarizeWorkingChanges(Document doc) throws DatabaseException {
//		System.out.println("doc id " + doc.getID() + " will have summary " + (doc.getWorkingCopyOrNull() == null ? "no working copy" : "has working copy"));
		return doc.getWorkingCopyOrNull() == null ? null : "has working copy";
	}

	private OriginalDocumentGWT createOriginalDocument(OriginalDocumentGWT newDocumentGWT) {
		try {
			if (LOG_ENTERING_AND_EXITING_CALLS)
				System.out.println("Begin GreetingServiceImpl.createOriginalDocument(newdocname " + newDocumentGWT.getName() + ")");
			
			assert(newDocumentGWT.getID() == null);
			assert(newDocumentGWT.getStaffInstructorID() == 0);
			assert(newDocumentGWT.getTBALocationID() == 0);
			assert(newDocumentGWT.getChooseForMeInstructorID() == 0);
			assert(newDocumentGWT.getChooseForMeLocationID() == 0);
			assert(newDocumentGWT.getWorkingChangesSummary() == null);
			Document newOriginalDocument = model.createAndInsertDocumentWithSpecialInstructorsAndLocations(newDocumentGWT.getName(), newDocumentGWT.getStartHalfHour(), newDocumentGWT.getEndHalfHour());
			
			OriginalDocumentGWT result = Conversion.originalDocumentToGWT(newOriginalDocument, null);

			sanityCheck();
			flushToFileSystem();

			if (LOG_ENTERING_AND_EXITING_CALLS)
				System.out.println("Begin GreetingServiceImpl.createOriginalDocument(newdocname " + newDocumentGWT.getName() + ")");
			
			return result;
		}
		catch (DatabaseException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public CompleteWorkingCopyDocumentGWT createAndOpenWorkingCopyForOriginalDocument(int sessionID, int originalDocumentID) throws SessionClosedFromInactivityExceptionGWT {
		try {
			if (LOG_ENTERING_AND_EXITING_CALLS)
				System.out.println("Begin GreetingServiceImpl.createAndOpenWorkingCopyForOriginalDocument(doc " + originalDocumentID + ")");
			
			sessions.onActivity(sessionID);
			
			Document originalDocument = model.findDocumentByID(originalDocumentID);
			assert(originalDocument.getStaffInstructor() != null);
			assert(originalDocument.getTBALocation() != null);
			assert(originalDocument.getChooseForMeInstructor() != null);
			assert(originalDocument.getChooseForMeLocation() != null);
			
			Document workingCopyDocument = originalDocument.getWorkingCopyOrNull();
			
			if (workingCopyDocument == null) {
				workingCopyDocument = model.createTransientDocument(originalDocument.getName(), originalDocument.getStartHalfHour(), originalDocument.getEndHalfHour()).insert();
			}
			else {
				System.out.println("working copy already exists for document " + originalDocumentID + ", writing over it!");
				// This is where we theoretically could "restore their working copy"
				
				workingCopyDocument.deleteContents(false);
			}
			
			model.copyDocument(originalDocument, workingCopyDocument);
			
			assert (workingCopyDocument.getTBALocation() != null);
			assert (workingCopyDocument.getStaffInstructor() != null);
			assert(workingCopyDocument.getChooseForMeInstructor() != null);
			assert(workingCopyDocument.getChooseForMeLocation() != null);
			
			workingCopyDocument.setOriginal(originalDocument);
			workingCopyDocument.update();
			
			originalDocument.update();
			WorkingDocumentGWT result = Conversion.workingDocumentToGWT(workingCopyDocument);

			assert(result.getRealID() == workingCopyDocument.getID());
			
			sanityCheck();
			flushToFileSystem();
			
			System.out.println("working document gwt id " + result.getRealID());
			
			sessions.openDocument(sessionID, workingCopyDocument.getID());
			
			CompleteWorkingCopyDocumentGWT derp = new CompleteWorkingCopyDocumentGWT(
					result,
					getCoursesForDocument(workingCopyDocument.getID()),
					getInstructorsForDocument(workingCopyDocument.getID()),
					getLocationsForDocument(workingCopyDocument.getID()),
					getScheduleItemsForDocument(workingCopyDocument.getID()));

			if (LOG_ENTERING_AND_EXITING_CALLS)
				System.out.println("End GreetingServiceImpl.createAndOpenWorkingCopyForOriginalDocument(doc " + originalDocumentID + ")");
			
			return derp;
		}
		catch (DatabaseException e) {
			throw new RuntimeException(e);
		}
	}
	
	private void flushToFileSystem() {
		sanityCheck();
		
		try {
			if (this.loadAndSaveFromFileSystem) {
				String filepath = getDatabaseStateFilepath();
				
				File file = new File(filepath);
				System.out.println("Saving state to " + filepath + " (" + file.getAbsolutePath() + ")");
				
				if (!file.exists()) {
					System.out.println("Creating file " + filepath);
					if (!file.createNewFile())
						throw new RuntimeException("Couldnt make file " + filepath);
				}
				
				assert(file.exists());
				FileOutputStream fos = new FileOutputStream(file);
				ObjectOutputStream oos = new ObjectOutputStream(fos);
				
				model.writeState(oos);
				
				oos.close();
	
				sanityCheck();
			}
		}
		catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public void saveWorkingCopyToOriginalDocument(int sessionID, int workingCopyDocumentID) throws SessionClosedFromInactivityExceptionGWT {
		try {
			if (LOG_ENTERING_AND_EXITING_CALLS)
				System.out.println("Begin GreetingServiceImpl.saveWorkingCopyToOriginalDocument(workingdocid " + workingCopyDocumentID + ")");
			
			Document workingCopyDocument = model.findDocumentByID(workingCopyDocumentID);
			assert(workingCopyDocument.getStaffInstructor() != null);
			assert(workingCopyDocument.getTBALocation() != null);
			assert(workingCopyDocument.getChooseForMeInstructor() != null);
			assert(workingCopyDocument.getChooseForMeLocation() != null);
			
			Document originalDocument = workingCopyDocument.getOriginal();
			
			originalDocument.deleteContents(false);
			
			model.copyDocument(workingCopyDocument, originalDocument);
			
			originalDocument.update();
			workingCopyDocument.update();
			
			assert(originalDocument.getStaffInstructor() != null);
			assert(originalDocument.getTBALocation() != null);
			assert(originalDocument.getChooseForMeInstructor() != null);
			assert(originalDocument.getChooseForMeLocation() != null);

			sanityCheck();
			flushToFileSystem();

			if (LOG_ENTERING_AND_EXITING_CALLS)
				System.out.println("End GreetingServiceImpl.saveWorkingCopyToOriginalDocument(workingdocid " + workingCopyDocumentID + ")");
		}
		catch (Exception e) {
			System.out.println("Couldnt save state!");
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public void deleteWorkingCopyDocument(int sessionID, int workingCopyDocumentID) throws SessionClosedFromInactivityExceptionGWT {
		sessions.onActivity(sessionID, workingCopyDocumentID);
		
		try {
			if (LOG_ENTERING_AND_EXITING_CALLS)
				System.out.println("Begin GreetingServiceImpl.deleteWorkingCopyDocument(workingdocid " + workingCopyDocumentID + ")");
			
			Document workingCopyDocument = model.findDocumentByID(workingCopyDocumentID);
			assert(workingCopyDocument.getStaffInstructor() != null);
			assert(workingCopyDocument.getTBALocation() != null);
			assert(workingCopyDocument.getChooseForMeInstructor() != null);
			assert(workingCopyDocument.getChooseForMeLocation() != null);
			
			// Document originalDocument =
			// model.getOriginalForWorkingCopyDocument(workingCopyDocument);
			workingCopyDocument.setOriginal(null);
			
			workingCopyDocument.delete();

			sanityCheck();
			flushToFileSystem();

			if (LOG_ENTERING_AND_EXITING_CALLS)
				System.out.println("End GreetingServiceImpl.deleteWorkingCopyDocument(workingdocid " + workingCopyDocumentID + ")");
		}
		catch (DatabaseException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public void associateWorkingCopyWithNewOriginalDocument(
			int sessionID,
			int workingCopyDocumentID, String newOriginalDocumentName,
			boolean allowOverwrite) throws SessionClosedFromInactivityExceptionGWT {
		sessions.onActivity(sessionID, workingCopyDocumentID);
		
		try {
			if (LOG_ENTERING_AND_EXITING_CALLS)
				System.out.println("Begin GreetingServiceImpl.associateWorkingCopyWithNewOriginalDocument(workingdocid " + workingCopyDocumentID + ")");
			
			Document workingCopyDocument = model.findDocumentByID(workingCopyDocumentID);
			assert(workingCopyDocument.getStaffInstructor() != null);
			assert(workingCopyDocument.getTBALocation() != null);
			assert(workingCopyDocument.getChooseForMeInstructor() != null);
			assert(workingCopyDocument.getChooseForMeLocation() != null);
			
			Document originalDocumentWithNewName = model.findDocumentByNameOrNull(newOriginalDocumentName);
			
			if (originalDocumentWithNewName == null) {
				originalDocumentWithNewName = model.createTransientDocument(newOriginalDocumentName, workingCopyDocument.getStartHalfHour(), workingCopyDocument.getEndHalfHour()).insert();
			}
			else {
				if (!allowOverwrite) {
					throw new RuntimeException("Document by name " + newOriginalDocumentName + " already exists!");
				}
			}
			
			model.copyDocument(workingCopyDocument, originalDocumentWithNewName);
			
			originalDocumentWithNewName.setOriginal(null);
			workingCopyDocument.setOriginal(originalDocumentWithNewName);
			
			originalDocumentWithNewName.update();
			workingCopyDocument.update();

			sanityCheck();
			flushToFileSystem();
			
			if (LOG_ENTERING_AND_EXITING_CALLS)
				System.out.println("End GreetingServiceImpl.associateWorkingCopyWithNewOriginalDocument(workingdocid " + workingCopyDocumentID + ")");
		}
		catch (DatabaseException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public ServerResourcesResponse<ScheduleItemGWT> generateRestOfSchedule(int sessionID, int documentID) throws CouldNotBeScheduledExceptionGWT, SessionClosedFromInactivityExceptionGWT {
		try {
			if (LOG_ENTERING_AND_EXITING_CALLS)
				System.out.println("Begin GreetingServiceImpl.generateRestOfSchedule(doc " + documentID + ")");
			
			Document document = model.findDocumentByID(documentID);
			
			assert(document.isWorkingCopy());
			
			sessions.onActivity(sessionID, document.getID());
			
			assert (document.getStaffInstructor() != null);
			assert (document.getTBALocation() != null);
			assert(document.getChooseForMeInstructor() != null);
			assert(document.getChooseForMeLocation() != null);
			
			assert (document.getOriginal() != null);
			
			Collection<Course> schedulableCourses = new LinkedList<Course>();
			for (Course course : document.getCourses())
				if (course.isSchedulable())
					schedulableCourses.add(course);
			
			Collection<Instructor> schedulableInstructors = new LinkedList<Instructor>();
			for (Instructor instructor : document.getInstructors(true))
				if (instructor.isSchedulable())
					schedulableInstructors.add(instructor);
			
			Collection<Location> schedulableLocations = new LinkedList<Location>();
			for (Location location : document.getLocations(true))
				if (location.isSchedulable())
					schedulableLocations.add(location);
			
			Vector<ScheduleItem> generated = GenerateEntryPoint.generate(model, document, document.getScheduleItems(), schedulableCourses, schedulableInstructors, schedulableLocations);
			for (ScheduleItem item : generated) {
				item.setDocument(document);
				item.insert();
			}
			
			ServerResourcesResponse<ScheduleItemGWT> derp = getScheduleItemsForDocument(document.getID());

			if (LOG_ENTERING_AND_EXITING_CALLS)
				System.out.println("End GreetingServiceImpl.generateRestOfSchedule(doc " + documentID + ")");
			
			return derp;
		}
		catch (DatabaseException e) {
			throw new RuntimeException(e);
		}
		catch (BadInstructorDataException e) {
			throw new RuntimeException(e);
		}
		
	}
	
	private void updateDocument(DocumentGWT documentGWT) {
		try {
			if (LOG_ENTERING_AND_EXITING_CALLS)
				System.out.println("Begin GreetingServiceImpl.updateDocument(doc " + documentGWT.getName() + ")");
			
			sanityCheck();
			
			System.out.println("got gwt doc " + documentGWT.isTrashed());
			Document document = Conversion.readDocumentFromGWT(model, documentGWT);

			assert (document.getStaffInstructor() != null);
			assert (document.getTBALocation() != null);
			assert(document.getChooseForMeInstructor() != null);
			assert(document.getChooseForMeLocation() != null);
			
			System.out.println("updating model doc " + document.isTrashed());
			document.update();

			sanityCheck();
			flushToFileSystem();
			
			if (LOG_ENTERING_AND_EXITING_CALLS)
				System.out.println("End GreetingServiceImpl.updateDocument(doc " + documentGWT.getName() + ")");
		}
		catch (DatabaseException e) {
			throw new RuntimeException(e);
		}
	}
//	
//	@Override
//	public DocumentGWT findDocumentByID(int automaticOpenDocumentID) {
//		try {
//			Document doc = model.findDocumentByID(automaticOpenDocumentID);
//
//			assert (doc.getStaffInstructor() != null);
//			assert (doc.getTBALocation() != null);
//			
//			Schedule schedule = doc.getSchedules().iterator().next();
//			DocumentGWT result = Conversion.documentToGWT(doc, schedule.getID());
//
//			sanityCheck();
//			
//			return result;
//		}
//		catch (DatabaseException e) {
//			throw new RuntimeException(e);
//		}
//	}

	private void removeOriginalDocument(Integer id) {
		try {
			model.findDocumentByID(id).delete();

			sanityCheck();
			flushToFileSystem();
		}
		catch (DatabaseException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public SynchronizeResponse<OriginalDocumentGWT> synchronizeOriginalDocuments(int sessionID, SynchronizeRequest<OriginalDocumentGWT> request) throws SessionClosedFromInactivityExceptionGWT {
		if (LOG_ENTERING_AND_EXITING_CALLS)
			System.out.println("Begin GreetingServiceImpl.synchronizeOriginalDocuments(session " + sessionID + ")");
		
		sessions.onActivity(sessionID);
		
		List<Integer> addedDocumentIDs = new LinkedList<Integer>();	
		for (OriginalDocumentGWT newDocument : request.clientChanges.addedResources)
			addedDocumentIDs.add(createOriginalDocument(newDocument).getID());
		for (OriginalDocumentGWT editedDocument : request.clientChanges.editedResources)
			updateDocument(editedDocument);
		for (int deletedDocumentID : request.clientChanges.deletedResourceIDs)
			removeOriginalDocument(deletedDocumentID);
		
		ClientChangesResponse clientChangesResponse = new ClientChangesResponse(addedDocumentIDs);
		
		SynchronizeResponse<OriginalDocumentGWT> result = new SynchronizeResponse<OriginalDocumentGWT>(clientChangesResponse, getAllOriginalDocuments(sessionID));
		
		if (LOG_ENTERING_AND_EXITING_CALLS)
			System.out.println("End GreetingServiceImpl.synchronizeOriginalDocuments(" + sessionID + ")");
		
		return result;
	}

	@Override
	public SynchronizeResponse<CourseGWT> synchronizeDocumentCourses(
			int sessionID,
			int documentID,
			SynchronizeRequest<CourseGWT> request) throws SessionClosedFromInactivityExceptionGWT {
		
		try {
			if (LOG_ENTERING_AND_EXITING_CALLS)
				System.out.println("Begin GreetingServiceImpl.synchronizeDocumentCourses(session " + sessionID + " doc " + documentID + ")");

			sessions.onActivity(sessionID, documentID);
			
			Document document = model.findDocumentByID(documentID);
			assert (document.getOriginal() != null);
			assert(document.getStaffInstructor() != null);
			assert(document.getTBALocation() != null);
			assert(document.getChooseForMeInstructor() != null);
			assert(document.getChooseForMeLocation() != null);
			
			List<Integer> addedCourseIDs = new LinkedList<Integer>();
			for (CourseGWT newCourse : request.clientChanges.addedResources)
				addedCourseIDs.add(addCourseToDocument(document, newCourse).getID());
			for (CourseGWT editedCourse : request.clientChanges.editedResources)
				editCourse(editedCourse);
			for (int deletedCourseID : request.clientChanges.deletedResourceIDs)
				removeCourse(deletedCourseID);
			
			ServerResourcesResponse<CourseGWT> result = getCoursesForDocument(documentID);
			
			if (LOG_ENTERING_AND_EXITING_CALLS)
				System.out.println("End GreetingServiceImpl.synchronizeDocumentCourses(" + documentID + ")");
			
			return new SynchronizeResponse<CourseGWT>(new ClientChangesResponse(addedCourseIDs), result);
		}
		catch (DatabaseException e) {
			throw new RuntimeException(e);
		}
	}
	@Override
	public SynchronizeResponse<InstructorGWT> synchronizeDocumentInstructors(
			int sessionID,
			int documentID,
			SynchronizeRequest<InstructorGWT> request) throws SessionClosedFromInactivityExceptionGWT {

		try {
			if (LOG_ENTERING_AND_EXITING_CALLS)
				System.out.println("Begin GreetingServiceImpl.synchronizeDocumentInstructors(" + sessionID + ", " + documentID + ")");

			sessions.onActivity(sessionID, documentID);
			
			Document document = model.findDocumentByID(documentID);
			assert (document.getOriginal() != null);
			assert(document.getStaffInstructor() != null);
			assert(document.getTBALocation() != null);
			assert(document.getChooseForMeInstructor() != null);
			assert(document.getChooseForMeLocation() != null);
			
			List<Integer> addedInstructorIDs = new LinkedList<Integer>();
			for (InstructorGWT newInstructor : request.clientChanges.addedResources)
				addedInstructorIDs.add(addInstructorToDocument(document.getID(), newInstructor).getID());
			for (InstructorGWT editedInstructor : request.clientChanges.editedResources)
				editInstructor(editedInstructor);
			for (int deletedInstructorID : request.clientChanges.deletedResourceIDs)
				removeInstructor(deletedInstructorID);
			
			ClientChangesResponse response = new ClientChangesResponse(addedInstructorIDs);
			
			if (LOG_ENTERING_AND_EXITING_CALLS)
				System.out.println("End GreetingServiceImpl.synchronizeDocumentInstructors(" + sessionID + ")");
			
			return new SynchronizeResponse<InstructorGWT>(response, getInstructorsForDocument(documentID));
		}
		catch (DatabaseException e) {
			throw new RuntimeException(e);
		}
	}
	@Override
	public SynchronizeResponse<LocationGWT> synchronizeDocumentLocations(
			int sessionID,
			int documentID,
			SynchronizeRequest<LocationGWT> request) throws SessionClosedFromInactivityExceptionGWT {

		sessions.onActivity(sessionID, documentID);
		
		try {
			if (LOG_ENTERING_AND_EXITING_CALLS)
				System.out.println("Begin GreetingServiceImpl.synchronizeDocumentLocations(" + documentID + ")");
	
			Document document = model.findDocumentByID(documentID);
			assert (document.getOriginal() != null);
			assert(document.getStaffInstructor() != null);
			assert(document.getTBALocation() != null);
			assert(document.getChooseForMeInstructor() != null);
			assert(document.getChooseForMeLocation() != null);
			
			List<Integer> addedLocationIDs = new LinkedList<Integer>();
			for (LocationGWT newLocation : request.clientChanges.addedResources)
				addedLocationIDs.add(addLocationToDocument(document.getID(), newLocation).getID());
			for (LocationGWT editedLocation : request.clientChanges.editedResources)
				editLocation(editedLocation);
			for (int deletedLocationID : request.clientChanges.deletedResourceIDs)
				removeLocation(deletedLocationID);
			
			ClientChangesResponse changesResponse = new ClientChangesResponse(addedLocationIDs);
			
			ServerResourcesResponse<LocationGWT> locations = getLocationsForDocument(documentID);
			
			if (LOG_ENTERING_AND_EXITING_CALLS)
				System.out.println("End GreetingServiceImpl.synchronizeDocumentLocations(" + documentID + ")");
			
			return new SynchronizeResponse<LocationGWT>(changesResponse, locations);
		}
		catch (DatabaseException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public SynchronizeResponse<ScheduleItemGWT> synchronizeDocumentScheduleItems(
			int sessionID,
			int documentID,
			SynchronizeRequest<ScheduleItemGWT> request) throws SessionClosedFromInactivityExceptionGWT {

		try {
			if (LOG_ENTERING_AND_EXITING_CALLS)
				System.out.println("Begin GreetingServiceImpl.synchronizeDocumentScheduleItems(session " + sessionID + " doc" + documentID + ")");

			sessions.onActivity(sessionID, documentID);
				
			Document document = model.findDocumentByID(documentID);
			assert (document.getOriginal() != null);
			assert(document.getStaffInstructor() != null);
			assert(document.getTBALocation() != null);
			assert(document.getChooseForMeInstructor() != null);
			assert(document.getChooseForMeLocation() != null);
			
			List<Integer> addedScheduleItemIDs = new LinkedList<Integer>();
			for (ScheduleItemGWT newScheduleItem : request.clientChanges.addedResources)
				addedScheduleItemIDs.add(addScheduleItemToDocument(document, newScheduleItem).getID());
			for (ScheduleItemGWT editedScheduleItem : request.clientChanges.editedResources)
				editScheduleItem(editedScheduleItem);
			for (int deletedScheduleItemID : request.clientChanges.deletedResourceIDs)
				removeScheduleItem(deletedScheduleItemID);
			
			ClientChangesResponse response = new ClientChangesResponse(addedScheduleItemIDs);
			
			ServerResourcesResponse<ScheduleItemGWT> scheduleItems = getScheduleItemsForDocument(documentID);
			
			if (LOG_ENTERING_AND_EXITING_CALLS)
				System.out.println("End GreetingServiceImpl.synchronizeDocumentScheduleItems(" + documentID + ")");
			
			return new SynchronizeResponse<ScheduleItemGWT>(response, scheduleItems);
		}
		catch (DatabaseException e) {
			throw new RuntimeException(e);
		}
	}
	
	

	private ScheduleItemGWT addScheduleItemToDocument(Document document, ScheduleItemGWT scheduleItem) {
		if (LOG_ENTERING_AND_EXITING_CALLS)
			System.out.println("Begin GreetingServiceImpl.addScheduleItemToDocument(" + document.getID() + ")");
		assert (scheduleItem.getID() == null);
		
		try {
			assert (document.getOriginal() != null);
			assert(document.getStaffInstructor() != null);
			assert(document.getTBALocation() != null);
			assert(document.getChooseForMeInstructor() != null);
			assert(document.getChooseForMeLocation() != null);
			
			assert (scheduleItem.getCourseID() >= 0);
			
			// TODO these need to be don't care location/instructor
			if (scheduleItem.getLocationID() < 0)
				scheduleItem.setLocationID(document.getTBALocation().getID());
			if (scheduleItem.getInstructorID() < 0)
				scheduleItem.setInstructorID(document.getStaffInstructor().getID());
			
			ScheduleItem newItem = Conversion.scheduleItemFromGWT(model, scheduleItem);
			newItem.setDocument(document);
			newItem.setLocation(model.findLocationByID(scheduleItem.getLocationID()));
			newItem.setCourse(model.findCourseByID(scheduleItem.getCourseID()));
			newItem.setInstructor(model.findInstructorByID(scheduleItem.getInstructorID()));

			int id = newItem.insert().getID();
			
			System.out.println("Inserted new item: " + newItem.getID());

			// Generate.generate(model, schedule, s_items, c_list, i_list, l_list)
			//
			// GenerationAlgorithm.insertNewScheduleItem(model, schedule, newItem);
			//
			// Generate.generate(model, schedule, s_items, c_list, i_list, l_list)
			
			scheduleItem.setID(id);
			
			sanityCheck();
			flushToFileSystem();

			if (LOG_ENTERING_AND_EXITING_CALLS)
				System.out.println("End GreetingServiceImpl.addScheduleItemToDocument(" + document.getID() + ")");
			
			return scheduleItem;
		}
		catch (DatabaseException e) {
			throw new RuntimeException(e);
		}
	}
	
	private void editScheduleItem(ScheduleItemGWT itemGWT) {
		if (LOG_ENTERING_AND_EXITING_CALLS)
			System.out.println("Begin GreetingServiceImpl.editScheduleItem");
		try {
			ScheduleItem item = model.findScheduleItemByID(itemGWT.getID());
			Document document = item.getDocument();
			
			assert (document.getOriginal() != null);
			assert (itemGWT.getCourseID() >= 0);
			
			// TODO these need to be don't care location/instructor
			if (itemGWT.getLocationID() < 0)
				itemGWT.setLocationID(document.getTBALocation().getID());
			if (itemGWT.getInstructorID() < 0)
				itemGWT.setInstructorID(document.getStaffInstructor().getID());
			
			Conversion.readScheduleItemFromGWT(model, itemGWT, item);
			item.update();
			
			sanityCheck();
			flushToFileSystem();

			if (LOG_ENTERING_AND_EXITING_CALLS)
				System.out.println("End GreetingServiceImpl.editScheduleItem");
		}
		catch (DatabaseException e) {
			throw new RuntimeException(e);
		}
	}

	private ServerResourcesResponse<ScheduleItemGWT> getScheduleItemsForDocument(int documentID) {
		try {
			if (LOG_ENTERING_AND_EXITING_CALLS)
				System.out.println("Begin GreetingServiceImpl.getScheduleItemsForDocument(" + documentID + ")");
			
			Document document = model.findDocumentByID(documentID);
			assert (document.getOriginal() != null);
			assert(document.getStaffInstructor() != null);
			assert(document.getTBALocation() != null);
			assert(document.getChooseForMeInstructor() != null);
			assert(document.getChooseForMeLocation() != null);
			List<ScheduleItemGWT> result = new LinkedList<ScheduleItemGWT>();
			for (ScheduleItem scheduleItem : document.getScheduleItems()) {
//				System.out.println("for doc id " + documentID + " returning scheduleItem name " + scheduleItem.getName());
				result.add(Conversion.scheduleItemToGWT(scheduleItem));
			}
			
			sanityCheck();

			if (LOG_ENTERING_AND_EXITING_CALLS)
				System.out.println("End GreetingServiceImpl.getScheduleItemsForDocument(" + documentID + ")");
			
			return new ServerResourcesResponse<ScheduleItemGWT>(result);
		}
		catch (DatabaseException e) {
			throw new RuntimeException(e);
		}
	}

	private void removeScheduleItem(Integer scheduleItemID) {
		try {
			ScheduleItem scheduleItem = model.findScheduleItemByID(scheduleItemID);
			assert (scheduleItem.getDocument().getOriginal() != null);
			scheduleItem.delete();
			
			sanityCheck();
			flushToFileSystem();
		}
		catch (DatabaseException e) {
			throw new RuntimeException(e);
		}
	}
	
}
