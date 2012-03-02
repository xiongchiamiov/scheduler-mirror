package edu.calpoly.csc.scheduler.view.web.server;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import edu.calpoly.csc.scheduler.model.Course;
import edu.calpoly.csc.scheduler.model.Document;
import edu.calpoly.csc.scheduler.model.Instructor;
import edu.calpoly.csc.scheduler.model.Location;
import edu.calpoly.csc.scheduler.model.Model;
import edu.calpoly.csc.scheduler.model.Schedule;
import edu.calpoly.csc.scheduler.model.ScheduleItem;
import edu.calpoly.csc.scheduler.model.db.DatabaseException;
import edu.calpoly.csc.scheduler.model.db.IDatabase.NotFoundException;
import edu.calpoly.csc.scheduler.model.tempalgorithm.GenerationAlgorithm;
import edu.calpoly.csc.scheduler.model.tempalgorithm.GenerationAlgorithm.CouldNotBeScheduledException;
import edu.calpoly.csc.scheduler.view.web.client.GreetingService;
import edu.calpoly.csc.scheduler.view.web.client.InvalidLoginException;
import edu.calpoly.csc.scheduler.view.web.shared.CouldNotBeScheduledExceptionGWT;
import edu.calpoly.csc.scheduler.view.web.shared.CourseGWT;
import edu.calpoly.csc.scheduler.view.web.shared.DocumentGWT;
import edu.calpoly.csc.scheduler.view.web.shared.InstructorGWT;
import edu.calpoly.csc.scheduler.view.web.shared.LocationGWT;
import edu.calpoly.csc.scheduler.view.web.shared.OldScheduleItemGWT;
import edu.calpoly.csc.scheduler.view.web.shared.ScheduleItemGWT;
import edu.calpoly.csc.scheduler.view.web.shared.ScheduleItemList;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class GreetingServiceImpl extends RemoteServiceServlet implements GreetingService {

	private boolean loadAndSaveFromFileSystem;
	private Model model;	

	public GreetingServiceImpl() {
		this(true);
	}
	
	public GreetingServiceImpl(boolean loadAndSaveFromFileSystem) {
		this.loadAndSaveFromFileSystem = loadAndSaveFromFileSystem;
		model = new Model();

		if (loadAndSaveFromFileSystem) {
			try {
				FileInputStream fos = new FileInputStream("DatabaseState.javaser");
				ObjectInputStream ois = new ObjectInputStream(fos);
				
				model.readState(ois);
				
				ois.close();
			}
			catch (IOException e) {
				e.printStackTrace();
				System.out.println("Failed to restore model! Starting fresh!");
				model = new Model();
			}
		}
	}

	@Override
	public CourseGWT addCourseToDocument(int documentID, CourseGWT course) {
		assert(course.getID() == -1);

		try {
			int id = Conversion.courseFromGWT(model, course).setDocument(model.findDocumentByID(documentID)).insert().getID();
			course.setID(id);
			return course;
		} catch (DatabaseException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void editCourse(CourseGWT source) {
		Course result;
		try {
			result = model.findCourseByID(source.getID());
		} catch (DatabaseException e) {
			throw new RuntimeException(e);
		}
		assert(result.getID() > 0);

		Conversion.readCourseFromGWT(source, result);
		
		try {
			result.update();
		} catch (DatabaseException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public List<CourseGWT> getCoursesForDocument(int documentID) {
		List<CourseGWT> result = new LinkedList<CourseGWT>();
		try {
			for (Course course : model.findCoursesForDocument(model.findDocumentByID(documentID))) {
				System.out.println("for doc id " + documentID + " returning course name " + course.getName());
				result.add(Conversion.courseToGWT(course));
			}
		} catch (DatabaseException e) {
			throw new RuntimeException(e);
		}
		return result;
	}

	@Override
	public void removeCourse(Integer courseID) {
		try {
			model.findCourseByID(courseID).delete();
		} catch (DatabaseException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public InstructorGWT addInstructorToDocument(int documentID, InstructorGWT instructor) {
		assert(instructor.getID() == -1);
		
		try {
			Document document = model.findDocumentByID(documentID);
			int id = Conversion.instructorFromGWT(model, instructor).setDocument(document).insert().getID();
			instructor.setID(id);
			return instructor;
		} catch (DatabaseException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void editInstructor(InstructorGWT source) {
		Instructor result;
		try {
			result = model.findInstructorByID(source.getID());
		} catch (DatabaseException e) {
			throw new RuntimeException(e);
		}
		assert(result.getID() > 0);

		Conversion.readInstructorFromGWT(source, result);
		
		try {
			result.update();
		} catch (DatabaseException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public List<InstructorGWT> getInstructorsForDocument(int documentID) {
		List<InstructorGWT> result = new LinkedList<InstructorGWT>();
		try {
			for (Instructor instructor : model.findInstructorsForDocument(model.findDocumentByID(documentID))) {
				result.add(Conversion.instructorToGWT(instructor));
			}
		} catch (DatabaseException e) {
			throw new RuntimeException(e);
		}
		return result;
	}

	@Override
	public void removeInstructor(Integer instructorID) {
		try {
			model.findInstructorByID(instructorID).delete();
		} catch (DatabaseException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public LocationGWT addLocationToDocument(int documentID, LocationGWT location) {
		assert(location.getID() == -1);

		try {
			Location modelLocation = model.createTransientLocation(
					location.getRoom(), location.getType(), location.getRawMaxOccupancy(), true);
			modelLocation.setProvidedEquipment(location.getEquipment());
			int id = modelLocation.setDocument(model.findDocumentByID(documentID)).insert().getID();
			location.setID(id);
			return location;
		} catch (DatabaseException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void editLocation(LocationGWT source) {
		System.out.println("editLocation in impl called!");
		
		Location result;
		try {
			result = model.findLocationByID(source.getID());
		} catch (DatabaseException e) {
			throw new RuntimeException(e);
		}
		assert(result.getID() > 0);

		Conversion.readLocationFromGWT(source, result);
		
		System.out.println("updating w result!");
		
		try {
			result.update();
		} catch (DatabaseException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public List<LocationGWT> getLocationsForDocument(int documentID) {
		List<LocationGWT> result = new LinkedList<LocationGWT>();
		try {
			for (Location location : model.findLocationsForDocument(model.findDocumentByID(documentID)))
				result.add(Conversion.locationToGWT(location));
		} catch (DatabaseException e) {
			throw new RuntimeException(e);
		}
		return result;
	}

	@Override
	public void removeLocation(Integer locationID) {
		try {
			model.findLocationByID(locationID).delete();
		} catch (DatabaseException e) {
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
	public Collection<DocumentGWT> getAllOriginalDocumentsByID() {
		Collection<DocumentGWT> result = new LinkedList<DocumentGWT>();
		try {
			for (Document doc : model.findAllDocuments()) {
				if (doc.getOriginal() == null) {
					System.out.println("found original doc " + doc.isTrashed());
					int scheduleID = model.findSchedulesForDocument(doc).iterator().next().getID();
					DocumentGWT gwt = Conversion.documentToGWT(doc, scheduleID);
					System.out.println("sending to client doc with istrashed " + gwt.isTrashed());
					result.add(gwt);
				}
			}
		} catch (DatabaseException e) {
			throw new RuntimeException(e);
		}
		return result;
	}

	@Override
	public DocumentGWT createDocumentAndGetWorkingCopy(String newDocName) {
		try {
			Document newOriginalDocument = model.createTransientDocument(newDocName, 14, 44);
			newOriginalDocument.insert();
			
			model.createTransientSchedule().setDocument(newOriginalDocument).insert();

			newOriginalDocument.setStaffInstructor(
					model.createTransientInstructor("", "TBA", "TBA", "0", true)
						.setDocument(newOriginalDocument)
						.insert());
			newOriginalDocument.setTBALocation(
					model.createTransientLocation("TBA", "LEC", "0", true)
						.setDocument(newOriginalDocument)
						.insert());
			
			newOriginalDocument.update();
			
			return createWorkingCopyForOriginalDocument(newOriginalDocument.getID());
		}
		catch (NotFoundException e) {
			throw new RuntimeException(e);
		} catch (DatabaseException e) {
			throw new RuntimeException(e);
		}
		
	}

	@Override
	public DocumentGWT createWorkingCopyForOriginalDocument(Integer originalDocumentID) {
		Document originalDocument;
		try {
			originalDocument = model.findDocumentByID(originalDocumentID);
		
			Document workingCopyDocument = originalDocument.getWorkingCopy();
			assert(workingCopyDocument == null);
			
			workingCopyDocument = model.copyDocument(originalDocument, originalDocument.getName());
			workingCopyDocument.setOriginal(originalDocument);
			workingCopyDocument.update();
			
			originalDocument.update();
			return Conversion.documentToGWT(workingCopyDocument, model.findSchedulesForDocument(workingCopyDocument).iterator().next().getID());
		} catch (DatabaseException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public void saveWorkingCopyToOriginalDocument(Integer workingCopyDocumentID) {
		try {
			Document workingCopyDocument = model.findDocumentByID(workingCopyDocumentID);
		
			Document originalDocument = workingCopyDocument.getOriginal();
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
				String filepath = getServletContext().getRealPath("DatabaseState.javaser");
				System.out.println("Saving state to "+filepath+"!");
				FileOutputStream fos = new FileOutputStream("DatabaseState.javaser");
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
			
//			Document originalDocument = model.getOriginalForWorkingCopyDocument(workingCopyDocument);
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
	public void intermediateUpdateScheduleItem(int documentID, OldScheduleItemGWT oldItemOldGWT, OldScheduleItemGWT newItemOldGWT) {
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
			ScheduleItem newItem = Conversion.scheduleItemFromGWT(model, model.findScheduleByID(scheduleID), scheduleItem);

			GenerationAlgorithm.insertNewScheduleItem(model, schedule, newItem);
			
			return getScheduleItems(scheduleID);
			
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public Collection<ScheduleItemGWT> generateRestOfSchedule(int scheduleID) throws CouldNotBeScheduledExceptionGWT {
		try {
			Schedule schedule = model.findScheduleByID(scheduleID);
			
			GenerationAlgorithm.generateRestOfSchedule(model, schedule);
			
			return getScheduleItems(scheduleID);
		}
		catch (CouldNotBeScheduledException e) {
			e.printStackTrace();
			throw new CouldNotBeScheduledExceptionGWT();
		} catch (DatabaseException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public Collection<ScheduleItemGWT> updateScheduleItem(ScheduleItemGWT itemGWT) {
		try {
			ScheduleItem item = model.findScheduleItemByID(itemGWT.getID());
			Conversion.readScheduleItemFromGWT(model, itemGWT, item);
			item.update();
			
			return getScheduleItems(item.getSchedule().getID());
		} catch (DatabaseException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public Collection<ScheduleItemGWT> newRemoveScheduleItem(ScheduleItemGWT itemGWT) {
		try {
			Schedule schedule = model.findScheduleItemByID(itemGWT.getID()).getSchedule();
			
			model.findScheduleItemByID(itemGWT.getID()).delete();

			return getScheduleItems(schedule.getID());
		} catch (DatabaseException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public Collection<ScheduleItemGWT> getScheduleItems(int scheduleID) {
		try {
			Collection<ScheduleItemGWT> result = new LinkedList<ScheduleItemGWT>();
			for (ScheduleItem item : model.findAllScheduleItemsForSchedule(model.findScheduleByID(scheduleID)))
				result.add(Conversion.scheduleItemToGWT(item));
			return result;
		} catch (DatabaseException e) {
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
		} catch (NotFoundException e) {
			throw new RuntimeException(e);
		} catch (DatabaseException e) {
			throw new RuntimeException(e);
		}
	}
}
