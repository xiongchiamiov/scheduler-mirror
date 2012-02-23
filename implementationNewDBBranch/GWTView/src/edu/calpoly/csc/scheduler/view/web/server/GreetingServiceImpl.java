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
import edu.calpoly.csc.scheduler.view.web.shared.NotFoundExceptionGWT;
import edu.calpoly.csc.scheduler.view.web.shared.OldScheduleItemGWT;
import edu.calpoly.csc.scheduler.view.web.shared.ScheduleItemGWT;
import edu.calpoly.csc.scheduler.view.web.shared.ScheduleItemList;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class GreetingServiceImpl extends RemoteServiceServlet implements GreetingService {

	private Model model;	

	public GreetingServiceImpl() {
		model = new Model();

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

	@Override
	public CourseGWT addCourseToDocument(int documentID, CourseGWT course) throws NotFoundExceptionGWT {
		assert(course.getID() == -1);

		try {
			int id = Conversion.courseFromGWT(model, course).setDocument(model.findDocumentByID(documentID)).insert().getID();
			course.setID(id);
			return course;
		} catch (NotFoundException e) {
			e.printStackTrace();
			throw new NotFoundExceptionGWT();
		} catch (DatabaseException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void editCourse(CourseGWT source) throws NotFoundExceptionGWT {
		Course result;
		try {
			result = model.findCourseByID(source.getID());
		} catch (NotFoundException e) {
			e.printStackTrace();
			throw new NotFoundExceptionGWT();
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
	public List<CourseGWT> getCoursesForDocument(int documentID) throws NotFoundExceptionGWT {
		List<CourseGWT> result = new LinkedList<CourseGWT>();
		try {
			for (Course course : model.findCoursesForDocument(model.findDocumentByID(documentID))) {
				System.out.println("for doc id " + documentID + " returning course name " + course.getName());
				result.add(Conversion.courseToGWT(course));
			}
		} catch (NotFoundException e) {
			e.printStackTrace();
			throw new NotFoundExceptionGWT();
		} catch (DatabaseException e) {
			throw new RuntimeException(e);
		}
		return result;
	}

	@Override
	public void removeCourse(Integer courseID) throws NotFoundExceptionGWT {
		try {
			model.findCourseByID(courseID).delete();
		} catch (NotFoundException e) {
			e.printStackTrace();
			throw new NotFoundExceptionGWT();
		} catch (DatabaseException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public InstructorGWT addInstructorToDocument(int documentID, InstructorGWT instructor) throws NotFoundExceptionGWT {
		assert(instructor.getID() == -1);
		
		try {
			Document document = model.findDocumentByID(documentID);
			int id = Conversion.instructorFromGWT(model, instructor).setDocument(document).insert().getID();
			instructor.setID(id);
			return instructor;
		} catch (NotFoundException e) {
			e.printStackTrace();
			throw new NotFoundExceptionGWT();
		} catch (DatabaseException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void editInstructor(InstructorGWT source) throws NotFoundExceptionGWT {
		Instructor result;
		try {
			result = model.findInstructorByID(source.getID());
		} catch (NotFoundException e) {
			e.printStackTrace();
			throw new NotFoundExceptionGWT();
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
	public List<InstructorGWT> getInstructorsForDocument(int documentID) throws NotFoundExceptionGWT {
		List<InstructorGWT> result = new LinkedList<InstructorGWT>();
		try {
			for (Instructor instructor : model.findInstructorsForDocument(model.findDocumentByID(documentID))) {
				System.out.println("got from model, wtu: " + instructor.getMaxWTU());
				result.add(Conversion.instructorToGWT(instructor));
			}
		} catch (NotFoundException e) {
			e.printStackTrace();
			throw new NotFoundExceptionGWT();
		} catch (DatabaseException e) {
			throw new RuntimeException(e);
		}
		return result;
	}

	@Override
	public void removeInstructor(Integer instructorID) throws NotFoundExceptionGWT {
		try {
			model.findInstructorByID(instructorID).delete();
		} catch (NotFoundException e) {
			e.printStackTrace();
			throw new NotFoundExceptionGWT();
		} catch (DatabaseException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public LocationGWT addLocationToDocument(int documentID, LocationGWT location) throws NotFoundExceptionGWT {
		assert(location.getID() == -1);

		try {
			Location modelLocation = model.createTransientLocation(
					location.getRoom(), location.getType(), location.getRawMaxOccupancy(), true);
			modelLocation.setProvidedEquipment(location.getEquipment());
			int id = modelLocation.setDocument(model.findDocumentByID(documentID)).insert().getID();
			location.setID(id);
			return location;
		} catch (NotFoundException e) {
			e.printStackTrace();
			throw new NotFoundExceptionGWT();
		} catch (DatabaseException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void editLocation(LocationGWT source) throws NotFoundExceptionGWT {
		System.out.println("editLocation in impl called!");
		
		Location result;
		try {
			result = model.findLocationByID(source.getID());
		} catch (NotFoundException e) {
			e.printStackTrace();
			throw new NotFoundExceptionGWT();
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
	public List<LocationGWT> getLocationsForDocument(int documentID) throws NotFoundExceptionGWT {
		List<LocationGWT> result = new LinkedList<LocationGWT>();
		try {
			for (Location location : model.findLocationsForDocument(model.findDocumentByID(documentID)))
				result.add(Conversion.locationToGWT(location));
		} catch (NotFoundException e) {
			e.printStackTrace();
			throw new NotFoundExceptionGWT();
		} catch (DatabaseException e) {
			throw new RuntimeException(e);
		}
		return result;
	}

	@Override
	public void removeLocation(Integer locationID) throws NotFoundExceptionGWT {
		try {
			model.findLocationByID(locationID).delete();
		} catch (NotFoundException e) {
			e.printStackTrace();
			throw new NotFoundExceptionGWT();
		} catch (DatabaseException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public Integer login(String username) throws InvalidLoginException {
		try {
			return model.findUserByUsername(username).getID();
		}
		catch (NotFoundException e) {
			try {
				return model.createTransientUser(username, true).insert().getID();
			} catch (NotFoundException e1) {
				throw new RuntimeException(e1);
			} catch (DatabaseException e2) {
				throw new RuntimeException(e2);
			}
		} catch (DatabaseException e) {
			try {
				return model.createTransientUser(username, true).insert().getID();
			} catch (NotFoundException e1) {
				throw new RuntimeException(e1);
			} catch (DatabaseException e2) {
				throw new RuntimeException(e2);
			}
		}
	}

	@Override
	public Collection<DocumentGWT> getAllOriginalDocumentsByID()  {
		Collection<DocumentGWT> result = new LinkedList<DocumentGWT>();
		try {
			for (Document doc : model.findAllDocuments()) {
				if (model.isOriginalDocument(doc)) {
					int scheduleID = model.findSchedulesForDocument(doc).iterator().next().getID();
					result.add(Conversion.documentToGWT(doc, scheduleID));
				}
			}
		} catch (DatabaseException e) {
			throw new RuntimeException(e);
		}
		return result;
	}

	@Override
	public DocumentGWT createDocumentAndGetWorkingCopy(String newDocName) throws NotFoundExceptionGWT {
		try {
			Document newOriginalDocument = model.createTransientDocument(newDocName, 14, 44);
			newOriginalDocument.insert();
			Schedule schedule = model.createTransientSchedule();
			schedule.setDocument(newOriginalDocument).insert();
			int scheduleID = schedule.getID();
			
			return createWorkingCopyForOriginalDocument(newOriginalDocument.getID());
		}
		catch (NotFoundException e) {
			throw new RuntimeException(e);
		} catch (DatabaseException e) {
			throw new RuntimeException(e);
		}
		
	}

	@Override
	public DocumentGWT createWorkingCopyForOriginalDocument(Integer originalDocumentID) throws NotFoundExceptionGWT {
		Document originalDocument;
		try {
			originalDocument = model.findDocumentByID(originalDocumentID);
		
			Document workingCopyDocument = model.getWorkingCopyForOriginalDocumentOrNull(originalDocument);
			assert(workingCopyDocument == null);
			
			workingCopyDocument = model.copyDocument(originalDocument, originalDocument.getName());
			model.associateWorkingCopyWithOriginal(workingCopyDocument, originalDocument);
			workingCopyDocument.update();
			originalDocument.update();
			return Conversion.documentToGWT(workingCopyDocument, model.findSchedulesForDocument(workingCopyDocument).iterator().next().getID());
		} catch (NotFoundException e) {
			e.printStackTrace();
			throw new NotFoundExceptionGWT();
		} catch (DatabaseException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public void saveWorkingCopyToOriginalDocument(Integer workingCopyDocumentID) throws NotFoundExceptionGWT {
		try {
			Document workingCopyDocument = model.findDocumentByID(workingCopyDocumentID);
		
			Document originalDocument = model.getOriginalForWorkingCopyDocument(workingCopyDocument);
			model.disassociateWorkingCopyFromOriginal(workingCopyDocument, originalDocument);
			
			String originalDocumentName = originalDocument.getName();
			
			originalDocument.delete();
			
			originalDocument = model.copyDocument(workingCopyDocument, originalDocumentName);
			model.associateWorkingCopyWithOriginal(workingCopyDocument, originalDocument);
			workingCopyDocument.update();
			originalDocument.update();
			
			
			
			System.out.println("Saving state!");
			FileOutputStream fos = new FileOutputStream("DatabaseState.javaser");
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			
			model.writeState(oos);
			
			oos.close();
		}
		catch (Exception e) {
			System.out.println("Couldnt save state!");
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	@Override
	public void deleteWorkingCopyDocument(Integer workingCopyDocumentID) throws NotFoundExceptionGWT {
		try {
			Document workingCopyDocument = model.findDocumentByID(workingCopyDocumentID);
			
			Document originalDocument = model.getOriginalForWorkingCopyDocument(workingCopyDocument);
			model.disassociateWorkingCopyFromOriginal(workingCopyDocument, originalDocument);
			
			workingCopyDocument.delete();
		}
		catch (NotFoundException e) {
			e.printStackTrace();
			throw new NotFoundExceptionGWT();
		} catch (DatabaseException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public DocumentGWT saveWorkingCopyToNewOriginalDocument(
			DocumentGWT existingDocument, String scheduleName,
			boolean allowOverwrite) {
		assert(false);
		return null;
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
	public Collection<OldScheduleItemGWT> intermediateGetScheduleItems(int documentID) throws NotFoundExceptionGWT {
		throw new UnsupportedOperationException();
	}

	@Override
	public void intermediateInsertScheduleItem(int documentID, OldScheduleItemGWT itemOldGWT) throws NotFoundExceptionGWT {
		throw new UnsupportedOperationException();
	}

	@Override
	public void intermediateUpdateScheduleItem(int documentID, OldScheduleItemGWT oldItemOldGWT, OldScheduleItemGWT newItemOldGWT) throws NotFoundExceptionGWT {
		throw new UnsupportedOperationException();
	}

	@Override
	public void intermediateRemoveScheduleItem(int documentID, OldScheduleItemGWT oldItemOldGWT) throws NotFoundExceptionGWT {
		throw new UnsupportedOperationException();
	}

	@Override
	public Collection<OldScheduleItemGWT> intermediateGenerateRestOfSchedule(int documentID) {
		throw new UnsupportedOperationException();
	}

	
	
	
	@Override
	public Collection<ScheduleItemGWT> insertScheduleItem(int scheduleID, ScheduleItemGWT scheduleItem) throws NotFoundExceptionGWT {
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
	public Collection<ScheduleItemGWT> generateRestOfSchedule(int scheduleID) throws NotFoundExceptionGWT, CouldNotBeScheduledExceptionGWT {
		try {
			Schedule schedule = model.findScheduleByID(scheduleID);
			
			GenerationAlgorithm.generateRestOfSchedule(model, schedule);
			
			return getScheduleItems(scheduleID);
		}
		catch (NotFoundException e) {
			e.printStackTrace();
			throw new NotFoundExceptionGWT();
		}
		catch (CouldNotBeScheduledException e) {
			e.printStackTrace();
			throw new CouldNotBeScheduledExceptionGWT();
		} catch (DatabaseException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public Collection<ScheduleItemGWT> updateScheduleItem(ScheduleItemGWT itemGWT) throws NotFoundExceptionGWT {
		try {
			ScheduleItem item = model.findScheduleItemByID(itemGWT.getID());
			Conversion.readScheduleItemFromGWT(model, itemGWT, item);
			item.update();
			
			return getScheduleItems(item.getSchedule().getID());
		} catch (NotFoundException e) {
			e.printStackTrace();
			throw new NotFoundExceptionGWT();
		} catch (DatabaseException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public Collection<ScheduleItemGWT> newRemoveScheduleItem(ScheduleItemGWT itemGWT) throws NotFoundExceptionGWT {
		try {
			Schedule schedule = model.findScheduleItemByID(itemGWT.getID()).getSchedule();
			
			model.findScheduleItemByID(itemGWT.getID()).delete();

			return getScheduleItems(schedule.getID());
		} catch (NotFoundException e) {
			e.printStackTrace();
			throw new NotFoundExceptionGWT();
		} catch (DatabaseException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public Collection<ScheduleItemGWT> getScheduleItems(int scheduleID) throws NotFoundExceptionGWT {
		try {
			Collection<ScheduleItemGWT> result = new LinkedList<ScheduleItemGWT>();
			for (ScheduleItem item : model.findAllScheduleItemsForSchedule(model.findScheduleByID(scheduleID)))
				result.add(Conversion.scheduleItemToGWT(item));
			return result;
		} catch (NotFoundException e) {
			e.printStackTrace();
			throw new NotFoundExceptionGWT();
		} catch (DatabaseException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void updateDocument(DocumentGWT documentGWT) throws NotFoundExceptionGWT {
		try {
			Document document = Conversion.readDocumentFromGWT(model, documentGWT);
			document.update();
		} catch (NotFoundException e) {
			throw new RuntimeException(e);
		} catch (DatabaseException e) {
			throw new RuntimeException(e);
		}
	}
}
