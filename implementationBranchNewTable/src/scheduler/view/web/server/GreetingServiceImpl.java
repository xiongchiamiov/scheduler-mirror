package scheduler.view.web.server;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import scheduler.model.Course;
import scheduler.model.Document;
import scheduler.model.Instructor;
import scheduler.model.Location;
import scheduler.model.Model;
import scheduler.model.Schedule;
import scheduler.model.ScheduleItem;
import scheduler.model.algorithm.Generate;
import scheduler.model.db.DatabaseException;
import scheduler.view.web.client.GreetingService;
import scheduler.view.web.client.InvalidLoginException;
import scheduler.view.web.shared.CouldNotBeScheduledExceptionGWT;
import scheduler.view.web.shared.CourseGWT;
import scheduler.view.web.shared.DocumentGWT;
import scheduler.view.web.shared.InstructorGWT;
import scheduler.view.web.shared.LocationGWT;
import scheduler.view.web.shared.OldScheduleItemGWT;
import scheduler.view.web.shared.ScheduleItemGWT;
import scheduler.view.web.shared.ScheduleItemList;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class GreetingServiceImpl extends RemoteServiceServlet implements GreetingService {

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
	private Model model;
	
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
		
		if (loadAndSaveFromFileSystem) {
			String filepath = getDatabaseStateFilepath();
			
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
			catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}
		
		try {
			Collection<String> equipmentTypes = model.getEquipmentTypes();
			if (equipmentTypes.size() == 0) {
				model.insertEquipmentType("Computers");
				model.insertEquipmentType("Projector");
			}
		}
		catch (DatabaseException e) {
			throw new RuntimeException(e);
		}
		
//		for (String equipmentType : model.getEquipmentTypes())
	}
	
	@Override
	public CourseGWT addCourseToDocument(int documentID, CourseGWT course) {
		assert (course.getID() == null);
		
		try {
			Document document = model.findDocumentByID(documentID);
			assert (document.getOriginal() != null);
			int id = Conversion.courseFromGWT(model, course).setDocument(document).insert().getID();
			course.setID(id);
			return course;
		}
		catch (DatabaseException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public void editCourse(CourseGWT source) {
		try {
			Course course = model.findCourseByID(source.getID());
			assert (course.getID() > 0);
			
			assert (course.getDocument().getOriginal() != null);
			
			Conversion.readCourseFromGWT(source, course);
			
			course.update();
		}
		catch (DatabaseException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public List<CourseGWT> getCoursesForDocument(int documentID) {
		try {
			Document document = model.findDocumentByID(documentID);
			assert (document.getOriginal() != null);
			List<CourseGWT> result = new LinkedList<CourseGWT>();
			for (Course course : model.findCoursesForDocument(document)) {
				System.out.println("for doc id " + documentID + " returning course name " + course.getName());
				result.add(Conversion.courseToGWT(course));
			}
			return result;
		}
		catch (DatabaseException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public void removeCourse(Integer courseID) {
		try {
			Course course = model.findCourseByID(courseID);
			assert (course.getDocument().getOriginal() != null);
			course.delete();
		}
		catch (DatabaseException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public InstructorGWT addInstructorToDocument(int documentID, InstructorGWT instructor) {
		assert (instructor.getID() == null);
		
		try {
			Document document = model.findDocumentByID(documentID);
			assert (document.getOriginal() != null);
			int id = Conversion.instructorFromGWT(model, instructor).setDocument(document).insert().getID();
			instructor.setID(id);
			return instructor;
		}
		catch (DatabaseException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public void editInstructor(InstructorGWT source) {
		try {
			Instructor result = model.findInstructorByID(source.getID());
			assert (result.getDocument().getOriginal() != null);
			assert (result.getID() > 0);
			Conversion.readInstructorFromGWT(source, result);
			result.update();
		}
		catch (DatabaseException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public List<InstructorGWT> getInstructorsForDocument(int documentID) {
		try {
			List<InstructorGWT> result = new LinkedList<InstructorGWT>();
			Document document = model.findDocumentByID(documentID);
			assert (document.getOriginal() != null);
			for (Instructor instructor : model.findInstructorsForDocument(document)) {
				result.add(Conversion.instructorToGWT(instructor));
			}
			return result;
		}
		catch (DatabaseException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public void removeInstructor(Integer instructorID) {
		try {
			Instructor instructor = model.findInstructorByID(instructorID);
			assert (instructor.getDocument().getOriginal() != null);
			instructor.delete();
		}
		catch (DatabaseException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public LocationGWT addLocationToDocument(int documentID, LocationGWT location) {
		assert (location.getID() == null);
		
		try {
			Document document = model.findDocumentByID(documentID);
			assert (document.getOriginal() != null);
			Location modelLocation = model.createTransientLocation(
					location.getRoom(), location.getType(), location.getRawMaxOccupancy(), true);
			modelLocation.setProvidedEquipment(location.getEquipment());
			int id = modelLocation.setDocument(document).insert().getID();
			location.setID(id);
			return location;
		}
		catch (DatabaseException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public void editLocation(LocationGWT source) {
		try {
			Location result = model.findLocationByID(source.getID());
			assert (result.getID() > 0);
			assert (result.getDocument().getOriginal() != null);
			Conversion.readLocationFromGWT(source, result);
			result.update();
		}
		catch (DatabaseException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public List<LocationGWT> getLocationsForDocument(int documentID) {
		try {
			Document document = model.findDocumentByID(documentID);
			assert (document.getOriginal() != null);
			List<LocationGWT> result = new LinkedList<LocationGWT>();
			for (Location location : model.findLocationsForDocument(document))
				result.add(Conversion.locationToGWT(location));
			return result;
		}
		catch (DatabaseException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public void removeLocation(Integer locationID) {
		try {
			Location location = model.findLocationByID(locationID);
			assert (location.getDocument().getOriginal() != null);
			location.delete();
		}
		catch (DatabaseException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public Integer login(String username) throws InvalidLoginException {
		try {
			return model.findUserByUsername(username).getID();
		}
		catch (DatabaseException e) {
			try {
				return model.createTransientUser(username, true).insert().getID();
			}
			catch (DatabaseException e2) {
				throw new RuntimeException(e2);
			}
		}
	}
	
	@Override
	public Collection<DocumentGWT> getAllOriginalDocuments() {
		try {
			Collection<DocumentGWT> result = new LinkedList<DocumentGWT>();
			for (Document doc : model.findAllDocuments()) {
				if (doc.isWorkingCopy())
					continue;
				System.out.println("found original doc " + doc.isTrashed());
				int scheduleID = model.findSchedulesForDocument(doc).iterator().next().getID();
				DocumentGWT gwt = Conversion.documentToGWT(doc, scheduleID);
				System.out.println("sending to client doc with istrashed " + gwt.isTrashed());
				result.add(gwt);
			}
			return result;
		}
		catch (DatabaseException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public DocumentGWT createOriginalDocument(String newDocName) {
		try {
			Document newOriginalDocument = model.createTransientDocument(newDocName, 14, 44);
			newOriginalDocument.insert();
			
			int scheduleID = model.createTransientSchedule().setDocument(newOriginalDocument).insert().getID();
			
			// TODO: have wtu and loc's max occ be 0
			
			newOriginalDocument.setStaffInstructor(
					model.createTransientInstructor("", "TBA", "TBA", "10000", true)
							.setDocument(newOriginalDocument)
							.insert());
			newOriginalDocument.setTBALocation(
					model.createTransientLocation("TBA", "LEC", "10000", true)
							.setDocument(newOriginalDocument)
							.insert());
			
			newOriginalDocument.update();
			
			return Conversion.documentToGWT(newOriginalDocument, scheduleID);
		}
		catch (DatabaseException e) {
			throw new RuntimeException(e);
		}
		
	}
	
	@Override
	public DocumentGWT createWorkingCopyForOriginalDocument(Integer originalDocumentID) {
		try {
			Document originalDocument = model.findDocumentByID(originalDocumentID);
			
			Document workingCopyDocument = originalDocument.getWorkingCopy();
			
			if (workingCopyDocument != null) {
				// This is where we theoretically could "restore their working copy"
				workingCopyDocument.delete();
				workingCopyDocument = null;
			}
			
			workingCopyDocument = model.copyDocument(originalDocument, originalDocument.getName());
			
			assert (workingCopyDocument.getTBALocation() != null);
			assert (workingCopyDocument.getStaffInstructor() != null);
			
			workingCopyDocument.setOriginal(originalDocument);
			workingCopyDocument.update();
			
			originalDocument.update();
			return Conversion.documentToGWT(workingCopyDocument, model.findSchedulesForDocument(workingCopyDocument)
					.iterator().next().getID());
		}
		catch (DatabaseException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public void saveWorkingCopyToOriginalDocument(Integer workingCopyDocumentID) {
		try {
			Document workingCopyDocument = model.findDocumentByID(workingCopyDocumentID);
			
			Document originalDocument = workingCopyDocument.getOriginal();
			assert (originalDocument != null);
			workingCopyDocument.setOriginal(null);
			workingCopyDocument.update();
			
			String originalDocumentName = originalDocument.getName();
			
			originalDocument.delete();
			
			originalDocument = model.copyDocument(workingCopyDocument, originalDocumentName);
			originalDocument.setOriginal(null);
			workingCopyDocument.setOriginal(originalDocument);
			originalDocument.update();
			workingCopyDocument.update();
			
			
			if (this.loadAndSaveFromFileSystem) {
				
				String filepath = getDatabaseStateFilepath();
				System.out.println("Saving state to " + filepath + "!");
				FileOutputStream fos = new FileOutputStream(filepath);
				ObjectOutputStream oos = new ObjectOutputStream(fos);
				
				model.writeState(oos);
				
				oos.close();
			}
		}
		catch (Exception e) {
			System.out.println("Couldnt save state!");
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public void deleteWorkingCopyDocument(Integer workingCopyDocumentID) {
		try {
			Document workingCopyDocument = model.findDocumentByID(workingCopyDocumentID);
			
			// Document originalDocument =
			// model.getOriginalForWorkingCopyDocument(workingCopyDocument);
			workingCopyDocument.setOriginal(null);
			
			workingCopyDocument.delete();
		}
		catch (DatabaseException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public void moveWorkingCopyToNewOriginalDocument(
			Integer workingCopyDocumentID, String scheduleName,
			boolean allowOverwrite) {
		
		try {
			Document workingCopyDocument = model.findDocumentByID(workingCopyDocumentID);
			
			Document newOriginal = model.copyDocument(workingCopyDocument, scheduleName);
			newOriginal.setOriginal(null);
			workingCopyDocument.setOriginal(newOriginal);
			
			newOriginal.update();
			workingCopyDocument.update();
		}
		catch (DatabaseException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	
	@Override
	@Deprecated
	public List<OldScheduleItemGWT> generateSchedule(
			List<CourseGWT> mAllCourses,
			HashMap<String, OldScheduleItemGWT> mSchedItems) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public List<OldScheduleItemGWT> getSchedule(
			HashMap<String, OldScheduleItemGWT> mSchedItems) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public ScheduleItemList rescheduleCourse(OldScheduleItemGWT scheduleItem,
			ArrayList<Integer> days, int startHour, boolean atHalfHour,
			boolean inSchedule, HashMap<String, OldScheduleItemGWT> mSchedItems) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public List<OldScheduleItemGWT> removeScheduleItem(
			OldScheduleItemGWT removed,
			HashMap<String, OldScheduleItemGWT> mSchedItems) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public Collection<OldScheduleItemGWT> intermediateGetScheduleItems(int documentID) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public void intermediateInsertScheduleItem(int documentID, OldScheduleItemGWT itemOldGWT) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public void intermediateUpdateScheduleItem(
			int documentID,
			OldScheduleItemGWT oldItemOldGWT,
			OldScheduleItemGWT newItemOldGWT) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public void intermediateRemoveScheduleItem(int documentID, OldScheduleItemGWT oldItemOldGWT) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public Collection<OldScheduleItemGWT> intermediateGenerateRestOfSchedule(int documentID) {
		throw new UnsupportedOperationException();
	}
	
	
	
	@Override
	public Collection<ScheduleItemGWT> insertScheduleItem(int scheduleID, ScheduleItemGWT scheduleItem) {
		try {
			Schedule schedule = model.findScheduleByID(scheduleID);
			assert (schedule.getDocument().getOriginal() != null);
			
			assert (scheduleItem.getCourseID() >= 0);
			// temporary, please hand in the staffinstructor id and tbalocation id
			if (scheduleItem.getLocationID() < 0)
				scheduleItem.setLocationID(schedule.getDocument().getTBALocation().getID());
			if (scheduleItem.getInstructorID() < 0)
				scheduleItem.setInstructorID(schedule.getDocument().getStaffInstructor().getID());
			
			ScheduleItem newItem = Conversion.scheduleItemFromGWT(model, scheduleItem);
			newItem.setSchedule(schedule);
			newItem.setLocation(model.findLocationByID(scheduleItem.getLocationID()));
			newItem.setCourse(model.findCourseByID(scheduleItem.getCourseID()));
			newItem.setInstructor(model.findInstructorByID(scheduleItem.getInstructorID()));
			newItem.insert();
			
			// Generate.generate(model, schedule, s_items, c_list, i_list, l_list)
			//
			// GenerationAlgorithm.insertNewScheduleItem(model, schedule, newItem);
			//
			// Generate.generate(model, schedule, s_items, c_list, i_list, l_list)
			
			return getScheduleItems(scheduleID);
			
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public Collection<ScheduleItemGWT> generateRestOfSchedule(int scheduleID) throws CouldNotBeScheduledExceptionGWT {
		try {
			Schedule schedule = model.findScheduleByID(scheduleID);
			
			Document document = schedule.getDocument();
			
			assert (document.getStaffInstructor() != null);
			assert (document.getTBALocation() != null);
			
			assert (document.getOriginal() != null);
			
			Vector<ScheduleItem> generated = Generate.generate(model, schedule, schedule.getItems(), schedule
					.getDocument().getCourses(), schedule.getDocument().getInstructors(), schedule.getDocument()
					.getLocations());
			for (ScheduleItem item : generated) {
				// assert(item.getSchedule() == null);
				item.setSchedule(schedule);
				item.insert();
			}
			
			return getScheduleItems(scheduleID);
		}
		catch (DatabaseException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public Collection<ScheduleItemGWT> updateScheduleItem(ScheduleItemGWT itemGWT) {
		try {
			ScheduleItem item = model.findScheduleItemByID(itemGWT.getID());
			assert (item.getSchedule().getDocument().getOriginal() != null);
			Conversion.readScheduleItemFromGWT(model, itemGWT, item);
			item.update();
			
			return getScheduleItems(item.getSchedule().getID());
		}
		catch (DatabaseException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public Collection<ScheduleItemGWT> newRemoveScheduleItem(ScheduleItemGWT itemGWT) {
		try {
			Schedule schedule = model.findScheduleItemByID(itemGWT.getID()).getSchedule();
			assert (schedule.getDocument().getOriginal() != null);
			
			model.findScheduleItemByID(itemGWT.getID()).delete();
			
			return getScheduleItems(schedule.getID());
		}
		catch (DatabaseException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public Collection<ScheduleItemGWT> getScheduleItems(int scheduleID) {
		try {
			Schedule schedule = model.findScheduleByID(scheduleID);
			assert (schedule.getDocument().getOriginal() != null);
			Collection<ScheduleItemGWT> result = new LinkedList<ScheduleItemGWT>();
			for (ScheduleItem item : model.findAllScheduleItemsForSchedule(schedule))
				result.add(Conversion.scheduleItemToGWT(item));
			return result;
		}
		catch (DatabaseException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public void updateDocument(DocumentGWT documentGWT) {
		try {
			System.out.println("got gwt doc " + documentGWT.isTrashed());
			Document document = Conversion.readDocumentFromGWT(model, documentGWT);
			System.out.println("updating model doc " + document.isTrashed());
			document.update();
		}
		catch (DatabaseException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public DocumentGWT findDocumentByID(int automaticOpenDocumentID) {
		try {
			Document doc = model.findDocumentByID(automaticOpenDocumentID);
			Schedule schedule = doc.getSchedules().iterator().next();
			return Conversion.documentToGWT(doc, schedule.getID());
		}
		catch (DatabaseException e) {
			throw new RuntimeException(e);
		}
	}
}