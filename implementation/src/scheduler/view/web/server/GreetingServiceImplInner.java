package scheduler.view.web.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
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
import java.util.Set;
import java.util.TreeSet;
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
import scheduler.view.web.client.InvalidLoginException;
import scheduler.view.web.shared.ClientChangesResponse;
import scheduler.view.web.shared.CompleteWorkingCopyDocumentGWT;
import scheduler.view.web.shared.CouldNotBeScheduledExceptionGWT;
import scheduler.view.web.shared.CourseGWT;
import scheduler.view.web.shared.DocumentGWT;
import scheduler.view.web.shared.ExistingWorkingDocumentDoesntExistExceptionGWT;
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
import scheduler.view.web.shared.WorkingDocumentSynchronizeRequest;
import scheduler.view.web.shared.WorkingDocumentSynchronizeResponse;

/**
 * The server side implementation of the RPC service.
 */
public class GreetingServiceImplInner {
	
	
	static class SessionList {
		static class Session {
			static class OpenDocument {
				Date lastActivity;
				
				private OpenDocument(Date lastActivity) {
					this.lastActivity = lastActivity;
				}
			}
			
			private final int id;
			private final User user;
			Date lastActivity;
			private Map<Integer, OpenDocument> openDocumentsByDocumentID = new HashMap<Integer, OpenDocument>();
			
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
		
		private int createSession(User user) {
			Session newSession = new Session(nextSessionID++, user, new Date());
			sessionsByID.put(newSession.id, newSession);
			
			System.out.println("Making new session: " + newSession.id + " for user " + user.getUsername());
			
			return newSession.id;
		}
		
		private void openDocument(int sessionID, int workingDocumentID) throws SessionClosedFromInactivityExceptionGWT {
			Session session = sessionsByID.get(sessionID);
			assert(session != null);
			
			session.openDocumentsByDocumentID.put(workingDocumentID, new Session.OpenDocument(new Date()));
			
			System.out.println("Opened document with id " + workingDocumentID);
		}
		
		private Session onActivity(int sessionID) throws SessionClosedFromInactivityExceptionGWT {
			Session session = sessionsByID.get(sessionID);
			if (session == null)
				throw new SessionClosedFromInactivityExceptionGWT();
			session.lastActivity = new Date();
			
			return session;
		}
		
		private Session onActivity(int sessionID, int workingCopyDocumentID) throws SessionClosedFromInactivityExceptionGWT {
			Session session = onActivity(sessionID);
			
			Session.OpenDocument openDocument = session.openDocumentsByDocumentID.get(workingCopyDocumentID);
			if (openDocument == null)
				throw new SessionClosedFromInactivityExceptionGWT();
			openDocument.lastActivity = new Date();
			
			return session;
		}

		private void closeDocumentsAndSessionsWithNoContactSince(Date since) {
			for (Integer sessionID : new HashSet<Integer>(sessionsByID.keySet())) {
				Session session = sessionsByID.get(sessionID);
				assert(session != null);
				
				session.closeOpenDocumentsWithNoContactSince(since);
				
				if (session.lastActivity.before(since))
					sessionsByID.remove(sessionID);
			}
		}
		
		private Set<String> findAllUsersWithWorkingDocumentOpen(int workingDocumentID) {
			Set<String> result = new HashSet<String>();
			for (Session session : sessionsByID.values())
				if (session.openDocumentsByDocumentID.containsKey(workingDocumentID))
					result.add(session.user.getUsername());
			return result;
		}
	}
	
	SessionList sessions = new SessionList();
	private boolean loadAndSaveFromFileSystem;
	public Model model;
	private String filepath;
	
	private void closeDocumentsAndSessionsWithNoContact() {
		// put this back in when we start locking documents
		
//		sessions.closeDocumentsAndSessionsWithNoContactSince(new Date(new Date().getTime() - 10000));
	}
	
	GreetingServiceImplInner(boolean loadAndSaveFromFileSystem, String filepath) {
		this.loadAndSaveFromFileSystem = loadAndSaveFromFileSystem;
		this.filepath = filepath;
		model = new Model();
		
		System.out.println("Loading and saving from file system?: " + loadAndSaveFromFileSystem);
		
		if (loadAndSaveFromFileSystem) {
			System.out.println("Using database state filepath: " + filepath);
			System.err.println("Using database state filepath: " + filepath);
			
			try {
				FileInputStream fos = new FileInputStream(filepath);
				ObjectInputStream ois = new ObjectInputStream(fos);
				
				model.readState(ois);
				
				ois.close();

				
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
	}
	
	public void sanityCheck() {
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

				Set<Integer> coursesIDs = new HashSet<Integer>();
				for (Course course : doc.getCourses())
					coursesIDs.add(course.getID());

				Set<Integer> instructorsIDs = new HashSet<Integer>();
				for (Instructor instructor : doc.getInstructors(false))
					instructorsIDs.add(instructor.getID());

				Set<Integer> locationsIDs = new HashSet<Integer>();
				for (Location location : doc.getLocations(false))
					locationsIDs.add(location.getID());

				Set<Integer> scheduleItemsIDs = new HashSet<Integer>();
				for (ScheduleItem scheduleItem : doc.getScheduleItems())
					scheduleItemsIDs.add(scheduleItem.getID());
				
				for (Instructor instructor : doc.getInstructors(false)) {
					Set<Integer> coursesIDsInPreferences = instructor.getCoursePreferences().keySet();
					assert(coursesIDs.containsAll(coursesIDsInPreferences));
					assert(coursesIDsInPreferences.containsAll(coursesIDs));
				}
				
				for (ScheduleItem scheduleItem : doc.getScheduleItems()) {
					assert(coursesIDs.contains(scheduleItem.getCourse().getID()));
					assert(instructorsIDs.contains(scheduleItem.getInstructor().getID()));
					assert(locationsIDs.contains(scheduleItem.getLocation().getID()));
					if (scheduleItem.getLectureOrNull() != null)
						assert(scheduleItemsIDs.contains(scheduleItem.getLectureOrNull().getID()));
				}
			}
		}
		catch (DatabaseException e) {
			throw new RuntimeException(e);
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	// COURSES
	


	private CourseGWT addCourseToDocument(Document document, CourseGWT course) throws DatabaseException {
		assert (course.getID() == null);
		
		Course resultCourse = Conversion.courseFromGWT(model, course).setDocument(document);
		
		int id = resultCourse.insert().getID();
		course.setID(id);
		
		for (Instructor instructor : document.getInstructors(false)) {
			instructor.getCoursePreferences().put(resultCourse.getID(), Instructor.DEFAULT_PREF);
			instructor.update();
		}

		return course;
	}
	
	private void editCourse(CourseGWT source) throws DatabaseException {
		assert(source.getID() >= 0);
		
		Course course = model.findCourseByID(source.getID());
		assert (course.getID() > 0);
		
		assert (course.getDocument().getOriginal() != null);
		
		Conversion.readCourseFromGWT(source, course, model);
		
		course.update();
	}
	
	private ServerResourcesResponse<CourseGWT> getCoursesForDocument(int documentID) throws DatabaseException {
		assert(documentID >= 0);
		
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

		return new ServerResourcesResponse<CourseGWT>(result);
	}
	
	private void removeCourse(Document document, Integer courseID) throws DatabaseException {
		assert(courseID >= 0);
		
		for (Instructor instructor : document.getInstructors(false)) {
			instructor.getCoursePreferences().remove(courseID);
			instructor.update();
		}

		Course course = model.findCourseByID(courseID);
		assert (course.getDocument().getOriginal() != null);
		course.delete();
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	private InstructorGWT addInstructorToDocument(int documentID, InstructorGWT instructor) throws DatabaseException {
		assert (instructor.getID() == null);
		
		Document document = model.findDocumentByID(documentID);
		assert (document.getOriginal() != null);
		assert(document.getStaffInstructor() != null);
		assert(document.getTBALocation() != null);
		assert(document.getChooseForMeInstructor() != null);
		assert(document.getChooseForMeLocation() != null);
		int id = Conversion.instructorFromGWT(model, instructor).setDocument(document).insert().getID();
		instructor.setID(id);

		return instructor;
	}
	
	private void editInstructor(InstructorGWT source) throws DatabaseException {
		assert(source.getID() >= 0);
		
		Instructor result = model.findInstructorByID(source.getID());
		assert (result.getDocument().getOriginal() != null);
		assert (result.getID() > 0);
		Conversion.readInstructorFromGWT(source, result);
		result.update();
	}
	
	private ServerResourcesResponse<InstructorGWT> getInstructorsForDocument(int documentID) throws DatabaseException {
		assert(documentID >= 0);
		
		List<InstructorGWT> result = new LinkedList<InstructorGWT>();
		Document document = model.findDocumentByID(documentID);
		assert (document.getOriginal() != null);
		assert(document.getStaffInstructor() != null);
		assert(document.getTBALocation() != null);
		assert(document.getChooseForMeInstructor() != null);
		assert(document.getChooseForMeLocation() != null);
		for (Instructor instructor : model.findInstructorsForDocument(document, false))
			result.add(Conversion.instructorToGWT(instructor));
		
		return new ServerResourcesResponse<InstructorGWT>(result);
	}
	
	private void removeInstructor(Integer instructorID) throws DatabaseException {
		assert(instructorID >= 0);
		
		Instructor instructor = model.findInstructorByID(instructorID);
		assert (instructor.getDocument().getOriginal() != null);
		instructor.delete();
	}
	
	private LocationGWT addLocationToDocument(int documentID, LocationGWT location) throws DatabaseException {
		assert (location.getID() == null);
		
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

		return location;
	}
	
	private void editLocation(LocationGWT source) throws DatabaseException {
		assert(source.getID() >= 0);
		
		Location result = model.findLocationByID(source.getID());
		assert (result.getID() > 0);
		assert (result.getDocument().getOriginal() != null);
		Conversion.readLocationFromGWT(source, result);
		result.update();
	}
	
	private ServerResourcesResponse<LocationGWT> getLocationsForDocument(int documentID) throws DatabaseException {
		assert(documentID >= 0);
		
		Document document = model.findDocumentByID(documentID);
		assert (document.getOriginal() != null);
		assert(document.getStaffInstructor() != null);
		assert(document.getTBALocation() != null);
		assert(document.getChooseForMeInstructor() != null);
		assert(document.getChooseForMeLocation() != null);
		List<LocationGWT> result = new LinkedList<LocationGWT>();
		for (Location location : model.findLocationsForDocument(document, false))
			result.add(Conversion.locationToGWT(location));

		return new ServerResourcesResponse<LocationGWT>(result);
	}
	
	private void removeLocation(Integer locationID) throws DatabaseException {
		assert(locationID >= 0);
		
		Location location = model.findLocationByID(locationID);
		assert (location.getDocument().getOriginal() != null);
		location.delete();
	}
	
	private User login(String username) {
		try {
			return model.findUserByUsername(username);
		}
		catch (DatabaseException e) {
			try {
				return model.createTransientUser(username, username.equals("admin")).insert();
			}
			catch (DatabaseException e2) {
				throw new RuntimeException(e2);
			}
		}
	}
	
	public LoginResponse loginAndGetAllOriginalDocuments(String username) throws InvalidLoginException, DatabaseException {
		User user = login(username);

		int sessionID = sessions.createSession(user);

		ServerResourcesResponse<OriginalDocumentGWT> originalDocs;
		
		try { originalDocs = getAllOriginalDocuments(sessionID); }
		catch (SessionClosedFromInactivityExceptionGWT e) {
			assert(false); // we just made it, how can it be closed?
			throw new RuntimeException(e);
		}
		
		return new LoginResponse(sessionID, user.isAdmin(), originalDocs);
	}
	
	public ServerResourcesResponse<OriginalDocumentGWT> getAllOriginalDocuments(int sessionID) throws SessionClosedFromInactivityExceptionGWT, DatabaseException {		
		closeDocumentsAndSessionsWithNoContact();

		SessionList.Session session = sessions.onActivity(sessionID);
		
		Collection<OriginalDocumentGWT> resultDocs = new LinkedList<OriginalDocumentGWT>();
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
			resultDocs.add(gwt);
		}

		return new ServerResourcesResponse<OriginalDocumentGWT>(resultDocs);
	}
	
	private String summarizeWorkingChanges(Document doc) throws DatabaseException {
		// once we want to do summarizing, we'd put the logic here
		
		// returning null means the working and original are the same
		
		return null;
	}

	private OriginalDocumentGWT createOriginalDocument(OriginalDocumentGWT newDocumentGWT) {
		closeDocumentsAndSessionsWithNoContact();
		
		try {
			// SHUT UP
			assert(model != null);
			assert(newDocumentGWT != null);
			assert(newDocumentGWT.getID() == null);
			assert(newDocumentGWT.getStaffInstructorID() == 0);
			assert(newDocumentGWT.getTBALocationID() == 0);
			assert(newDocumentGWT.getChooseForMeInstructorID() == 0);
			assert(newDocumentGWT.getChooseForMeLocationID() == 0);
			assert(newDocumentGWT.getWorkingChangesSummary() == null);
			Document newOriginalDocument = model.createAndInsertDocumentWithSpecialInstructorsAndLocations(newDocumentGWT.getName(), newDocumentGWT.getStartHalfHour(), newDocumentGWT.getEndHalfHour());
			
			return Conversion.originalDocumentToGWT(newOriginalDocument, null);
		}
		catch (DatabaseException e) {
			throw new RuntimeException(e);
		}
	}
	
	public CompleteWorkingCopyDocumentGWT createAndOpenWorkingCopyForOriginalDocument(int sessionID, int originalDocumentID, boolean openExistingWorkingDocument) throws SessionClosedFromInactivityExceptionGWT, ExistingWorkingDocumentDoesntExistExceptionGWT {
		closeDocumentsAndSessionsWithNoContact();
		
		assert(originalDocumentID >= 0);
		
		try {
			sessions.onActivity(sessionID);
			
			Document originalDocument = model.findDocumentByID(originalDocumentID);
			assert(originalDocument.getStaffInstructor() != null);
			assert(originalDocument.getTBALocation() != null);
			assert(originalDocument.getChooseForMeInstructor() != null);
			assert(originalDocument.getChooseForMeLocation() != null);
			
			Document workingCopyDocument = originalDocument.getWorkingCopyOrNull();
			
			//assert(workingCopyDocument.isWorkingCopy());
			
			if (openExistingWorkingDocument) {
				if (workingCopyDocument == null) {
					throw new ExistingWorkingDocumentDoesntExistExceptionGWT();
				}
				else {
					// do nothing

					assert(workingCopyDocument.isWorkingCopy());
				}
			}
			else {
				if (workingCopyDocument == null) {
					workingCopyDocument = model.createTransientDocument(originalDocument.getName(), originalDocument.getStartHalfHour(), originalDocument.getEndHalfHour()).insert();
					model.copyDocument(originalDocument, workingCopyDocument);

					workingCopyDocument.setOriginal(originalDocument);
					workingCopyDocument.update();
					originalDocument.update();
				}
				else {
					System.out.println("working copy already exists for document " + originalDocumentID + ", writing over it!");

					assert(workingCopyDocument.isWorkingCopy());
					
					workingCopyDocument.deleteContents(false);
					model.copyDocument(originalDocument, workingCopyDocument);

					workingCopyDocument.setOriginal(originalDocument);
					workingCopyDocument.update();
					originalDocument.update();
					
					assert(workingCopyDocument.isWorkingCopy());
				}
			}
			
			assert(workingCopyDocument.getTBALocation() != null);
			assert(workingCopyDocument.getStaffInstructor() != null);
			assert(workingCopyDocument.getChooseForMeInstructor() != null);
			assert(workingCopyDocument.getChooseForMeLocation() != null);
			
			WorkingDocumentGWT workingDoc = Conversion.workingDocumentToGWT(workingCopyDocument);

			assert(workingDoc.getRealID() == workingCopyDocument.getID());
			
			
			
			
			System.out.println("working document gwt id " + workingDoc.getRealID());
			
			sessions.openDocument(sessionID, workingCopyDocument.getID());
			
			return new CompleteWorkingCopyDocumentGWT(
					workingDoc,
					getCoursesForDocument(workingCopyDocument.getID()),
					getInstructorsForDocument(workingCopyDocument.getID()),
					getLocationsForDocument(workingCopyDocument.getID()),
					getScheduleItemsForDocument(workingCopyDocument.getID()));

		}
		catch (DatabaseException e) {
			throw new RuntimeException(e);
		}
	}
	
	public void flushToFileSystem() {
		
		
		try {
			if (this.loadAndSaveFromFileSystem) {
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
	
				
			}
		}
		catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	
	public void saveWorkingCopyToOriginalDocument(int sessionID, int workingCopyDocumentID) throws SessionClosedFromInactivityExceptionGWT, DatabaseException {
	
		assert(workingCopyDocumentID >= 0);
		
		closeDocumentsAndSessionsWithNoContact();
		
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
	}
	
	public void deleteWorkingCopyDocument(int sessionID, int workingCopyDocumentID) throws SessionClosedFromInactivityExceptionGWT, DatabaseException {
		assert(workingCopyDocumentID >= 0);
		
		closeDocumentsAndSessionsWithNoContact();
		
		sessions.onActivity(sessionID, workingCopyDocumentID);
		
		Document workingCopyDocument = model.findDocumentByID(workingCopyDocumentID);
		assert(workingCopyDocument.getStaffInstructor() != null);
		assert(workingCopyDocument.getTBALocation() != null);
		assert(workingCopyDocument.getChooseForMeInstructor() != null);
		assert(workingCopyDocument.getChooseForMeLocation() != null);
		
		// Document originalDocument =
		// model.getOriginalForWorkingCopyDocument(workingCopyDocument);
		workingCopyDocument.setOriginal(null);
		
		workingCopyDocument.delete();
	}
	
	public void associateWorkingCopyWithNewOriginalDocument(
			int sessionID,
			int workingCopyDocumentID, String newOriginalDocumentName,
			boolean allowOverwrite) throws SessionClosedFromInactivityExceptionGWT, DatabaseException {

		assert(workingCopyDocumentID >= 0);
		
		closeDocumentsAndSessionsWithNoContact();
		
		sessions.onActivity(sessionID, workingCopyDocumentID);
		
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
	}
	
	public void generateRestOfSchedule(int sessionID, int documentID) throws CouldNotBeScheduledExceptionGWT, SessionClosedFromInactivityExceptionGWT, DatabaseException, BadInstructorDataException {
	
		assert(documentID >= 0);
	
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
	}
	
	private void updateDocument(DocumentGWT documentGWT) {
		try {
			
			
			System.out.println("got gwt doc " + documentGWT.isTrashed());
			Document document = Conversion.readDocumentFromGWT(model, documentGWT);

			assert (document.getStaffInstructor() != null);
			assert (document.getTBALocation() != null);
			assert(document.getChooseForMeInstructor() != null);
			assert(document.getChooseForMeLocation() != null);
			
			System.out.println("updating model doc " + document.isTrashed());
			document.update();

			
			
			
		}
		catch (DatabaseException e) {
			throw new RuntimeException(e);
		}
	}
//	
//	@Override
//	private DocumentGWT findDocumentByID(int automaticOpenDocumentID) {
//		try {
//			Document doc = model.findDocumentByID(automaticOpenDocumentID);
//
//			assert (doc.getStaffInstructor() != null);
//			assert (doc.getTBALocation() != null);
//			
//			Schedule schedule = doc.getSchedules().iterator().next();
//			DocumentGWT result = Conversion.documentToGWT(doc, schedule.getID());
//
//			
//			
//			return result;
//		}
//		catch (DatabaseException e) {
//			throw new RuntimeException(e);
//		}
//	}

	private void removeOriginalDocument(Integer id) {
		try {
			assert(id >= 0);
			
			model.findDocumentByID(id).delete();
		}
		catch (DatabaseException e) {
			throw new RuntimeException(e);
		}
	}

	public SynchronizeResponse<OriginalDocumentGWT> synchronizeOriginalDocuments(int sessionID, SynchronizeRequest<OriginalDocumentGWT> request) throws SessionClosedFromInactivityExceptionGWT, DatabaseException {
		sessions.onActivity(sessionID);
		
		List<Integer> addedDocumentIDs = new LinkedList<Integer>();	
		for (OriginalDocumentGWT newDocument : request.clientChanges.addedResources)
			addedDocumentIDs.add(createOriginalDocument(newDocument).getID());
		for (OriginalDocumentGWT editedDocument : request.clientChanges.editedResources)
			updateDocument(editedDocument);
		for (int deletedDocumentID : request.clientChanges.deletedResourceIDs)
			removeOriginalDocument(deletedDocumentID);
		
		ClientChangesResponse clientChangesResponse = new ClientChangesResponse(addedDocumentIDs);
		
		ServerResourcesResponse<OriginalDocumentGWT> originalDocs = getAllOriginalDocuments(sessionID);
		
		return new SynchronizeResponse<OriginalDocumentGWT>(clientChangesResponse, originalDocs);
	}
	

	private ScheduleItemGWT addScheduleItemToDocument(Document document, ScheduleItemGWT scheduleItem) throws DatabaseException {
		assert (scheduleItem.getID() == null);
	
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
		
		return scheduleItem;
	}
	
	private void editScheduleItem(ScheduleItemGWT itemGWT) throws DatabaseException {
		assert(itemGWT.getID() >= 0);
		
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
	}

	private ServerResourcesResponse<ScheduleItemGWT> getScheduleItemsForDocument(int documentID) throws DatabaseException {
		assert(documentID >= 0);
		
		Document document = model.findDocumentByID(documentID);
		assert (document.getOriginal() != null);
		assert(document.getStaffInstructor() != null);
		assert(document.getTBALocation() != null);
		assert(document.getChooseForMeInstructor() != null);
		assert(document.getChooseForMeLocation() != null);
		List<ScheduleItemGWT> result = new LinkedList<ScheduleItemGWT>();
		for (ScheduleItem scheduleItem : document.getScheduleItems()) {
			result.add(Conversion.scheduleItemToGWT(scheduleItem));
		}
		
		return new ServerResourcesResponse<ScheduleItemGWT>(result);
	}

	private void removeScheduleItem(Integer scheduleItemID) throws DatabaseException {
		assert(scheduleItemID >= 0);
		
		ScheduleItem scheduleItem = model.findScheduleItemByID(scheduleItemID);
		assert (scheduleItem.getDocument().getOriginal() != null);
		scheduleItem.delete();
	}

	public WorkingDocumentSynchronizeResponse synchronizeWorkingDocument(
			int sessionID,
			int documentID,
			WorkingDocumentSynchronizeRequest request) throws SessionClosedFromInactivityExceptionGWT, DatabaseException {
		
		sessions.onActivity(sessionID, documentID);
		
		Document document = model.findDocumentByID(documentID);
		assert (document.getOriginal() != null);
		assert(document.getStaffInstructor() != null);
		assert(document.getTBALocation() != null);
		assert(document.getChooseForMeInstructor() != null);
		assert(document.getChooseForMeLocation() != null);
		
		List<Integer> addedCourseIDs = new LinkedList<Integer>();
		for (CourseGWT newCourse : request.courses.clientChanges.addedResources)
			addedCourseIDs.add(addCourseToDocument(document, newCourse).getID());

		for (CourseGWT editedCourse : request.courses.clientChanges.editedResources)
			editCourse(editedCourse);

		List<Integer> addedInstructorIDs = new LinkedList<Integer>();
		for (InstructorGWT newInstructor : request.instructors.clientChanges.addedResources)
			addedInstructorIDs.add(addInstructorToDocument(document.getID(), newInstructor).getID());

		for (InstructorGWT editedInstructor : request.instructors.clientChanges.editedResources)
			editInstructor(editedInstructor);
		
		List<Integer> addedLocationIDs = new LinkedList<Integer>();
		for (LocationGWT newLocation : request.locations.clientChanges.addedResources)
			addedLocationIDs.add(addLocationToDocument(document.getID(), newLocation).getID());

		for (LocationGWT editedLocation : request.locations.clientChanges.editedResources)
			editLocation(editedLocation);
		
		List<Integer> addedScheduleItemIDs = new LinkedList<Integer>();
		for (ScheduleItemGWT newScheduleItem : request.scheduleItems.clientChanges.addedResources)
			addedScheduleItemIDs.add(addScheduleItemToDocument(document, newScheduleItem).getID());

		for (ScheduleItemGWT editedScheduleItem : request.scheduleItems.clientChanges.editedResources)
			editScheduleItem(editedScheduleItem);
		
		for (int deletedScheduleItemID : request.scheduleItems.clientChanges.deletedResourceIDs)
			removeScheduleItem(deletedScheduleItemID);

		for (int deletedLocationID : request.locations.clientChanges.deletedResourceIDs)
			removeLocation(deletedLocationID);

		for (int deletedInstructorID : request.instructors.clientChanges.deletedResourceIDs)
			removeInstructor(deletedInstructorID);
		
		for (int deletedCourseID : request.courses.clientChanges.deletedResourceIDs)
			removeCourse(document, deletedCourseID);
		
		return new WorkingDocumentSynchronizeResponse(
				new SynchronizeResponse<CourseGWT>(new ClientChangesResponse(addedCourseIDs), getCoursesForDocument(documentID)),
				new SynchronizeResponse<InstructorGWT>(new ClientChangesResponse(addedInstructorIDs), getInstructorsForDocument(documentID)),
				new SynchronizeResponse<LocationGWT>(new ClientChangesResponse(addedLocationIDs), getLocationsForDocument(documentID)),
				new SynchronizeResponse<ScheduleItemGWT>(new ClientChangesResponse(addedScheduleItemIDs), getScheduleItemsForDocument(documentID)));
	}
	

//
//	public SynchronizeResponse<CourseGWT> synchronizeDocumentCourses(
//			int sessionID,
//			int documentID,
//			SynchronizeRequest<CourseGWT> request) throws SessionClosedFromInactivityExceptionGWT {
//
//		SynchronizeResponse<CourseGWT> result;
//	
//		assert(documentID >= 0);
//		
//		try {
//			sessions.onActivity(sessionID, documentID);
//			
//			Document document = model.findDocumentByID(documentID);
//			assert (document.getOriginal() != null);
//			assert(document.getStaffInstructor() != null);
//			assert(document.getTBALocation() != null);
//			assert(document.getChooseForMeInstructor() != null);
//			assert(document.getChooseForMeLocation() != null);
//			
//			ServerResourcesResponse<CourseGWT> resultCourses = getCoursesForDocument(documentID);
//			
//			result = new SynchronizeResponse<CourseGWT>(new ClientChangesResponse(addedCourseIDs), resultCourses);
//		}
//		catch (DatabaseException e) {
//			throw new RuntimeException(e);
//		}
//		
//		return result;
//	}
//	
//	public SynchronizeResponse<InstructorGWT> synchronizeDocumentInstructors(
//			int sessionID,
//			int documentID,
//			SynchronizeRequest<InstructorGWT> request) throws SessionClosedFromInactivityExceptionGWT, DatabaseException {
//
//		assert(documentID >= 0);
//		
//		sessions.onActivity(sessionID, documentID);
//		
//		Document document = model.findDocumentByID(documentID);
//		assert (document.getOriginal() != null);
//		assert(document.getStaffInstructor() != null);
//		assert(document.getTBALocation() != null);
//		assert(document.getChooseForMeInstructor() != null);
//		assert(document.getChooseForMeLocation() != null);
//		
//		ClientChangesResponse responseInstructors = new ClientChangesResponse(addedInstructorIDs);
//		
//		return new SynchronizeResponse<InstructorGWT>(responseInstructors, getInstructorsForDocument(documentID));
//	}
//	
//	public SynchronizeResponse<LocationGWT> synchronizeDocumentLocations(
//			int sessionID,
//			int documentID,
//			SynchronizeRequest<LocationGWT> request) throws SessionClosedFromInactivityExceptionGWT, DatabaseException {
//
//		assert(documentID >= 0);
//		
//		sessions.onActivity(sessionID, documentID);
//		
//		Document document = model.findDocumentByID(documentID);
//		assert (document.getOriginal() != null);
//		assert(document.getStaffInstructor() != null);
//		assert(document.getTBALocation() != null);
//		assert(document.getChooseForMeInstructor() != null);
//		assert(document.getChooseForMeLocation() != null);
//
//		ClientChangesResponse changesResponse = new ClientChangesResponse(addedLocationIDs);
//		
//		ServerResourcesResponse<LocationGWT> locations = getLocationsForDocument(documentID);
//		
//		return new SynchronizeResponse<LocationGWT>(changesResponse, locations);
//	}
//
//	public SynchronizeResponse<ScheduleItemGWT> synchronizeDocumentScheduleItems(
//			int sessionID,
//			int documentID,
//			SynchronizeRequest<ScheduleItemGWT> request) throws SessionClosedFromInactivityExceptionGWT, DatabaseException {
//
//		assert(documentID >= 0);
//	
//		sessions.onActivity(sessionID, documentID);
//			
//		Document document = model.findDocumentByID(documentID);
//		assert (document.getOriginal() != null);
//		assert(document.getStaffInstructor() != null);
//		assert(document.getTBALocation() != null);
//		assert(document.getChooseForMeInstructor() != null);
//		assert(document.getChooseForMeLocation() != null);
//		
//		ClientChangesResponse response = new ClientChangesResponse(addedScheduleItemIDs);
//		
//		ServerResourcesResponse<ScheduleItemGWT> scheduleItems = getScheduleItemsForDocument(documentID);
//		
//		return new SynchronizeResponse<ScheduleItemGWT>(response, scheduleItems);
//	}
//	
}
