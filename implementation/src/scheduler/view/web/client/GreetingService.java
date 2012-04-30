package scheduler.view.web.client;



import java.util.Collection;
import java.util.List;

//import scheduler.model.algorithm.BadInstructorDataException;
import scheduler.view.web.shared.CouldNotBeScheduledExceptionGWT;
import scheduler.view.web.shared.CourseGWT;
import scheduler.view.web.shared.DocumentGWT;
import scheduler.view.web.shared.InstructorGWT;
import scheduler.view.web.shared.LocationGWT;
import scheduler.view.web.shared.ScheduleItemGWT;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("greet")
public interface GreetingService extends RemoteService {

	Integer login(String username) throws InvalidLoginException;

	// Documents
	DocumentGWT createOriginalDocument(String newDocName);	
	void updateDocument(DocumentGWT document);
	Collection<DocumentGWT> getAllOriginalDocuments();
	void saveWorkingCopyToOriginalDocument(Integer id);
	DocumentGWT createWorkingCopyForOriginalDocument(Integer originalDocumentID);
	void deleteWorkingCopyDocument(Integer documentID);
	// This is what we think of as "save as":
	void associateWorkingCopyWithNewOriginalDocument(
			Integer workingCopyID, String scheduleName,
			boolean allowOverwrite);
	DocumentGWT findDocumentByID(int automaticOpenDocumentID);
	void removeOriginalDocument(Integer id);

	// Courses
	CourseGWT addCourseToDocument(int documentID, CourseGWT realCourse);
	void editCourse(CourseGWT realCourse);
	List<CourseGWT> getCoursesForDocument(int documentID);
	void removeCourse(Integer realCourseID);

	// Instructors
	List<InstructorGWT> getInstructorsForDocument(int documentID);
	void removeInstructor(Integer realInstructorID);
	void editInstructor(InstructorGWT realInstructor);
	InstructorGWT addInstructorToDocument(int documentID,
			InstructorGWT realInstructor);

	// Locations
	LocationGWT addLocationToDocument(int documentID, LocationGWT location);
	void editLocation(LocationGWT source);
	List<LocationGWT> getLocationsForDocument(int documentID);
	void removeLocation(Integer locationID);

	

	// Generation (schedules and scheduleitems)
	Collection<ScheduleItemGWT> insertScheduleItem(int scheduleID,
			ScheduleItemGWT scheduleItem);
	public Collection<ScheduleItemGWT> generateRestOfSchedule(int scheduleID) throws CouldNotBeScheduledExceptionGWT;
	Collection<ScheduleItemGWT> updateScheduleItem(ScheduleItemGWT itemGWT);
	Collection<ScheduleItemGWT> newRemoveScheduleItem(ScheduleItemGWT itemGWT);
	Collection<ScheduleItemGWT> getScheduleItems(int scheduleID);

	List<Integer> updateCourses(
			int documentID,
			List<CourseGWT> addedResources,
			Collection<CourseGWT> editedResources,
			List<Integer> deletedResourcesIDs);

}
