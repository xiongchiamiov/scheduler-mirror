package scheduler.view.web.client.calendar;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.gwt.user.client.rpc.AsyncCallback;

import scheduler.view.web.client.GreetingServiceAsync;
import scheduler.view.web.shared.CourseGWT;
import scheduler.view.web.shared.DayGWT;
import scheduler.view.web.shared.DocumentGWT;
import scheduler.view.web.shared.InstructorGWT;
import scheduler.view.web.shared.LocationGWT;
import scheduler.view.web.shared.ScheduleItemGWT;

public class FakeCalendarGreetingService implements GreetingServiceAsync {

	private static final List<CourseGWT> COURSES = new ArrayList<CourseGWT>();
	private static final List<InstructorGWT> INSTRUCTORS = new ArrayList<InstructorGWT>();
	private static final List<LocationGWT> LOCATIONS = new ArrayList<LocationGWT>();
	private static final List<ScheduleItemGWT> SCHEDULE_ITEMS = new ArrayList<ScheduleItemGWT>();
	
	static {
		COURSES.add(new CourseGWT(
				true,  //schedulable
				"course1", //name
				"1", //catalog number
				"department1",//department
				"0", //wtu
				"0", //scu
				"1", //number of sections
				"LEC", //type
				"0", //max enrollment
				1, //lecutre id
				"0", //hours per week
				(Collection<Set<DayGWT>>)new ArrayList<Set<DayGWT>>(), // day combinations
				1, // id
				false, // is tethered
				new HashSet<String>() // equipment
				));
		COURSES.add(new CourseGWT(
				true,  //schedulable
				"this course has an unusually looooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooong name", //name
				"1", //catalog number
				"this course has an unusually looooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooong department",//department
				"0", //wtu
				"0", //scu
				"1", //number of sections
				"LEC", //type
				"0", //max enrollment
				2, //lecutre id
				"0", //hours per week
				(Collection<Set<DayGWT>>)new ArrayList<Set<DayGWT>>(), // day combinations
				2, // id
				false, // is tethered
				new HashSet<String>() // equipment
				));
		COURSES.add(new CourseGWT(
				true,  //schedulable
				"", //name
				"", //catalog number
				"",//department
				"", //wtu
				"", //scu
				"", //number of sections
				"", //type
				"", //max enrollment
				3, //lecutre id
				"", //hours per week
				(Collection<Set<DayGWT>>)new ArrayList<Set<DayGWT>>(), // day combinations
				3, // id
				false, // is tethered
				new HashSet<String>() // equipment
				));
		
		INSTRUCTORS.add(new InstructorGWT(
				1, //ID
				"instructor1",// name
				"first1", //first name
				"last1", //last name
				"0", //max wtu
				new int[DayGWT.values().length][48], // time Prefs,
				new HashMap<Integer, Integer>(), //??boolean
				true // schedulable
				));		
		INSTRUCTORS.add(new InstructorGWT(
				2, //ID
				"this instructor has an unusually looooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooong name",// name
				"looooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooongfirstname", //first name
				"looooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooonglastname", //last name
				"0", //max wtu
				new int[DayGWT.values().length][48], // time Prefs,
				new HashMap<Integer, Integer>(), //??boolean
				true // schedulable
				));
		INSTRUCTORS.add(new InstructorGWT(
				3, //ID
				"",// name
				"", //first name
				"", //last name
				"", //max wtu
				new int[DayGWT.values().length][48], // time Prefs,
				new HashMap<Integer, Integer>(), //??boolean
				true // schedulable
				));
		
		LOCATIONS.add(new LocationGWT(
				1, // ID
				"room1", // room
				"type1",//type
				"0",// maxOccupancy
				new HashSet<String>(), //equiptment
				true // isSchedulable
				));
		LOCATIONS.add(new LocationGWT(
				2, // ID
				"this room has an unusually looooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooong name", // room
				"type1",//type
				"0",// maxOccupancy
				new HashSet<String>(), //equiptment
				true // isSchedulable
				));
		LOCATIONS.add(new LocationGWT(
				3, // ID
				"", // room
				"",//type
				"",// maxOccupancy
				new HashSet<String>(), //equiptment
				true // isSchedulable
				));
		
// Test bounds
		SCHEDULE_ITEMS.add(new ScheduleItemGWT(
				1, //id 
				1, //course id 
				1,  // instructor id
				1,// location id
				1,  //section number
				createDaySet("SuMTuWThFSa"), //days
				14, //start half hour
				14, // end half hour
				true, // placed
				false // conflicted
				));
		SCHEDULE_ITEMS.add(new ScheduleItemGWT(
				1, //id 
				1, //course id 
				1,  // instructor id
				1,// location id
				1,  //section number
				createDaySet("SuMTuWThFSa"), //days
				14, //start half hour
				15, // end half hour
				true, // placed
				false // conflicted
				));
		SCHEDULE_ITEMS.add(new ScheduleItemGWT(
				1, //id 
				1, //course id 
				1,  // instructor id
				1,// location id
				1,  //section number
				createDaySet("SuMTuWThFSa"), //days
				43, //start half hour
				43, // end half hour
				true, // placed
				false // conflicted
				));
		SCHEDULE_ITEMS.add(new ScheduleItemGWT(
				1, //id 
				1, //course id 
				1,  // instructor id
				1,// location id
				1,  //section number
				createDaySet("SuSa"), //days
				14, //start half hour
				43, // end half hour
				true, // placed
				false // conflicted
				));
		
// Test long strings
		SCHEDULE_ITEMS.add(new ScheduleItemGWT(
				1, //id 
				2, //course id 
				2,  // instructor id
				2,// location id
				2,  //section number
				createDaySet("Th"), //days
				20, //start half hour
				21, // end half hour
				true, // placed
				false // conflicted
				));

// Test empty strings
		SCHEDULE_ITEMS.add(new ScheduleItemGWT(
				1, //id 
				3, //course id 
				3,  // instructor id
				3,// location id
				0,  //section number
				createDaySet("Th"), //days
				20, //start half hour
				21, // end half hour
				true, // placed
				false // conflicted
				));
		
// Test lots of overlaps
		SCHEDULE_ITEMS.add(new ScheduleItemGWT(
				1, //id 
				1, //course id 
				1,  // instructor id
				1,// location id
				1,  //section number
				createDaySet("Tu"), //days
				15, //start half hour
				17, // end half hour
				true, // placed
				false // conflicted
				));
		SCHEDULE_ITEMS.add(new ScheduleItemGWT(
				1, //id 
				1, //course id 
				1,  // instructor id
				1,// location id
				1,  //section number
				createDaySet("Tu"), //days
				16, //start half hour
				20, // end half hour
				true, // placed
				false // conflicted
				));
		SCHEDULE_ITEMS.add(new ScheduleItemGWT(
				1, //id 
				1, //course id 
				1,  // instructor id
				1,// location id
				1,  //section number
				createDaySet("Tu"), //days
				16, //start half hour
				19, // end half hour
				true, // placed
				false // conflicted
				));
		SCHEDULE_ITEMS.add(new ScheduleItemGWT(
				1, //id 
				1, //course id 
				1,  // instructor id
				1,// location id
				1,  //section number
				createDaySet("Tu"), //days
				17, //start half hour
				21, // end half hour
				true, // placed
				false // conflicted
				));
		SCHEDULE_ITEMS.add(new ScheduleItemGWT(
				1, //id 
				1, //course id 
				1,  // instructor id
				1,// location id
				1,  //section number
				createDaySet("Tu"), //days
				19, //start half hour
				19, // end half hour
				true, // placed
				false // conflicted
				));
		SCHEDULE_ITEMS.add(new ScheduleItemGWT(
				1, //id 
				1, //course id 
				1,  // instructor id
				1,// location id
				1,  //section number
				createDaySet("Tu"), //days
				17, //start half hour
				25, // end half hour
				true, // placed
				false // conflicted
				));
		SCHEDULE_ITEMS.add(new ScheduleItemGWT(
				1, //id 
				1, //course id 
				1,  // instructor id
				1,// location id
				1,  //section number
				createDaySet("Tu"), //days
				20, //start half hour
				24, // end half hour
				true, // placed
				false // conflicted
				));
		SCHEDULE_ITEMS.add(new ScheduleItemGWT(
				1, //id 
				1, //course id 
				1,  // instructor id
				1,// location id
				1,  //section number
				createDaySet("Tu"), //days
				18, //start half hour
				22, // end half hour
				true, // placed
				false // conflicted
				));
		SCHEDULE_ITEMS.add(new ScheduleItemGWT(
				1, //id 
				1, //course id 
				1,  // instructor id
				1,// location id
				1,  //section number
				createDaySet("Tu"), //days
				19, //start half hour
				21, // end half hour
				true, // placed
				false // conflicted
				));
		SCHEDULE_ITEMS.add(new ScheduleItemGWT(
				1, //id 
				1, //course id 
				1,  // instructor id
				1,// location id
				1,  //section number
				createDaySet("Tu"), //days
				17, //start half hour
				20, // end half hour
				true, // placed
				false // conflicted
				));
		SCHEDULE_ITEMS.add(new ScheduleItemGWT(
				1, //id 
				1, //course id 
				1,  // instructor id
				1,// location id
				1,  //section number
				createDaySet("Tu"), //days
				21, //start half hour
				24, // end half hour
				true, // placed
				false // conflicted
				));
		SCHEDULE_ITEMS.add(new ScheduleItemGWT(
				1, //id 
				1, //course id 
				1,  // instructor id
				1,// location id
				1,  //section number
				createDaySet("Tu"), //days
				22, //start half hour
				25, // end half hour
				true, // placed
				false // conflicted
				));
		SCHEDULE_ITEMS.add(new ScheduleItemGWT(
				1, //id 
				1, //course id 
				1,  // instructor id
				1,// location id
				1,  //section number
				createDaySet("Tu"), //days
				23, //start half hour
				33, // end half hour
				true, // placed
				false // conflicted
				));
		SCHEDULE_ITEMS.add(new ScheduleItemGWT(
				1, //id 
				1, //course id 
				1,  // instructor id
				1,// location id
				1,  //section number
				createDaySet("Tu"), //days
				24, //start half hour
				26, // end half hour
				true, // placed
				false // conflicted
				));
		SCHEDULE_ITEMS.add(new ScheduleItemGWT(
				1, //id 
				1, //course id 
				1,  // instructor id
				1,// location id
				1,  //section number
				createDaySet("Tu"), //days
				24, //start half hour
				27, // end half hour
				true, // placed
				false // conflicted
				));
		SCHEDULE_ITEMS.add(new ScheduleItemGWT(
				1, //id 
				1, //course id 
				1,  // instructor id
				1,// location id
				1,  //section number
				createDaySet("Tu"), //days
				24, //start half hour
				26, // end half hour
				true, // placed
				false // conflicted
				));
		SCHEDULE_ITEMS.add(new ScheduleItemGWT(
				1, //id 
				1, //course id 
				1,  // instructor id
				1,// location id
				1,  //section number
				createDaySet("Tu"), //days
				25, //start half hour
				25, // end half hour
				true, // placed
				false // conflicted
				));
		SCHEDULE_ITEMS.add(new ScheduleItemGWT(
				1, //id 
				1, //course id 
				1,  // instructor id
				1,// location id
				1,  //section number
				createDaySet("Tu"), //days
				20, //start half hour
				26, // end half hour
				true, // placed
				false // conflicted
				));
		SCHEDULE_ITEMS.add(new ScheduleItemGWT(
				1, //id 
				1, //course id 
				1,  // instructor id
				1,// location id
				1,  //section number
				createDaySet("Tu"), //days
				21, //start half hour
				23, // end half hour
				true, // placed
				false // conflicted
				));
		SCHEDULE_ITEMS.add(new ScheduleItemGWT(
				1, //id 
				1, //course id 
				1,  // instructor id
				1,// location id
				1,  //section number
				createDaySet("Tu"), //days
				21, //start half hour
				25, // end half hour
				true, // placed
				false // conflicted
				));
		SCHEDULE_ITEMS.add(new ScheduleItemGWT(
				1, //id 
				1, //course id 
				1,  // instructor id
				1,// location id
				1,  //section number
				createDaySet("Tu"), //days
				22, //start half hour
				22, // end half hour
				true, // placed
				false // conflicted
				));
		SCHEDULE_ITEMS.add(new ScheduleItemGWT(
				1, //id 
				1, //course id 
				1,  // instructor id
				1,// location id
				1,  //section number
				createDaySet("Tu"), //days
				23, //start half hour
				23, // end half hour
				true, // placed
				false // conflicted
				));
		SCHEDULE_ITEMS.add(new ScheduleItemGWT(
				1, //id 
				1, //course id 
				1,  // instructor id
				1,// location id
				1,  //section number
				createDaySet("Tu"), //days
				25, //start half hour
				27, // end half hour
				true, // placed
				false // conflicted
				));
		SCHEDULE_ITEMS.add(new ScheduleItemGWT(
				1, //id 
				1, //course id 
				1,  // instructor id
				1,// location id
				1,  //section number
				createDaySet("Tu"), //days
				25, //start half hour
				29, // end half hour
				true, // placed
				false // conflicted
				));
		SCHEDULE_ITEMS.add(new ScheduleItemGWT(
				1, //id 
				1, //course id 
				1,  // instructor id
				1,// location id
				1,  //section number
				createDaySet("Tu"), //days
				27, //start half hour
				28, // end half hour
				true, // placed
				false // conflicted
				));
		SCHEDULE_ITEMS.add(new ScheduleItemGWT(
				1, //id 
				1, //course id 
				1,  // instructor id
				1,// location id
				1,  //section number
				createDaySet("Tu"), //days
				27, //start half hour
				27, // end half hour
				true, // placed
				false // conflicted
				));
		SCHEDULE_ITEMS.add(new ScheduleItemGWT(
				1, //id 
				1, //course id 
				1,  // instructor id
				1,// location id
				1,  //section number
				createDaySet("Tu"), //days
				27, //start half hour
				31, // end half hour
				true, // placed
				false // conflicted
				));
		SCHEDULE_ITEMS.add(new ScheduleItemGWT(
				1, //id 
				1, //course id 
				1,  // instructor id
				1,// location id
				1,  //section number
				createDaySet("Tu"), //days
				27, //start half hour
				30, // end half hour
				true, // placed
				false // conflicted
				));
		SCHEDULE_ITEMS.add(new ScheduleItemGWT(
				1, //id 
				1, //course id 
				1,  // instructor id
				1,// location id
				1,  //section number
				createDaySet("Tu"), //days
				28, //start half hour
				28, // end half hour
				true, // placed
				false // conflicted
				));
		SCHEDULE_ITEMS.add(new ScheduleItemGWT(
				1, //id 
				1, //course id 
				1,  // instructor id
				1,// location id
				1,  //section number
				createDaySet("Tu"), //days
				29, //start half hour
				31, // end half hour
				true, // placed
				false // conflicted
				));
		SCHEDULE_ITEMS.add(new ScheduleItemGWT(
				1, //id 
				1, //course id 
				1,  // instructor id
				1,// location id
				1,  //section number
				createDaySet("Tu"), //days
				29, //start half hour
				33, // end half hour
				true, // placed
				false // conflicted
				));
		SCHEDULE_ITEMS.add(new ScheduleItemGWT(
				1, //id 
				1, //course id 
				1,  // instructor id
				1,// location id
				1,  //section number
				createDaySet("Tu"), //days
				30, //start half hour
				34, // end half hour
				true, // placed
				false // conflicted
				));
		SCHEDULE_ITEMS.add(new ScheduleItemGWT(
				1, //id 
				1, //course id 
				1,  // instructor id
				1,// location id
				1,  //section number
				createDaySet("Tu"), //days
				30, //start half hour
				30, // end half hour
				true, // placed
				false // conflicted
				));
		SCHEDULE_ITEMS.add(new ScheduleItemGWT(
				1, //id 
				1, //course id 
				1,  // instructor id
				1,// location id
				1,  //section number
				createDaySet("Tu"), //days
				31, //start half hour
				32, // end half hour
				true, // placed
				false // conflicted
				));
		SCHEDULE_ITEMS.add(new ScheduleItemGWT(
				1, //id 
				1, //course id 
				1,  // instructor id
				1,// location id
				1,  //section number
				createDaySet("Tu"), //days
				31, //start half hour
				33, // end half hour
				true, // placed
				false // conflicted
				));
		SCHEDULE_ITEMS.add(new ScheduleItemGWT(
				1, //id 
				1, //course id 
				1,  // instructor id
				1,// location id
				1,  //section number
				createDaySet("Tu"), //days
				32, //start half hour
				36, // end half hour
				true, // placed
				false // conflicted
				));
		SCHEDULE_ITEMS.add(new ScheduleItemGWT(
				1, //id 
				1, //course id 
				1,  // instructor id
				1,// location id
				1,  //section number
				createDaySet("Tu"), //days
				26, //start half hour
				26, // end half hour
				true, // placed
				false // conflicted
				));
		SCHEDULE_ITEMS.add(new ScheduleItemGWT(
				1, //id 
				1, //course id 
				1,  // instructor id
				1,// location id
				1,  //section number
				createDaySet("Tu"), //days
				26, //start half hour
				26, // end half hour
				true, // placed
				false // conflicted
				));
		SCHEDULE_ITEMS.add(new ScheduleItemGWT(
				1, //id 
				1, //course id 
				1,  // instructor id
				1,// location id
				1,  //section number
				createDaySet("Tu"), //days
				28, //start half hour
				28, // end half hour
				true, // placed
				false // conflicted
				));
	}
	
	/**
	 * Creates a set of DayGWTs containing the days specified in a String. It should be
	 * easy to figure out how to use it if you take a look at the implementation.
	 *  
	 * @param days The days the set will contain
	 * @return A set containing the specified days
	 */
	private static Set<DayGWT> createDaySet(String days) {
		Set<DayGWT> daySet = new HashSet<DayGWT>();
		
		if (days.contains("Su"))
			daySet.add(DayGWT.SUNDAY);
		if (days.contains("M"))
			daySet.add(DayGWT.MONDAY);
		if (days.contains("Tu"))
			daySet.add(DayGWT.TUESDAY);
		if (days.contains("W"))
			daySet.add(DayGWT.WEDNESDAY);
		if (days.contains("Th"))
			daySet.add(DayGWT.THURSDAY);
		if (days.contains("F"))
			daySet.add(DayGWT.FRIDAY);
		if (days.contains("Sa"))
			daySet.add(DayGWT.SATURDAY);
		
		return daySet;
		
	}
	
	@Override
	public void getCoursesForDocument(int documentID, AsyncCallback<List<CourseGWT>> callback) {
		callback.onSuccess(COURSES);
	}

	@Override
	public void removeCourse(Integer realCourseID, AsyncCallback<Void> callback) {
		callback.onFailure(new UnsupportedOperationException("Cannot remove course using this test double"));
	}

	@Override
	public void editCourse(CourseGWT realCourse, AsyncCallback<Void> callback) {
		callback.onFailure(new UnsupportedOperationException("Cannot edit course using this test double"));
	}

	@Override
	public void addCourseToDocument(int documentID, CourseGWT realCourse, AsyncCallback<CourseGWT> callback) {
		callback.onFailure(new UnsupportedOperationException("Cannot add course using this test double"));
	}

	@Override
	public void getInstructorsForDocument(int documentID, AsyncCallback<List<InstructorGWT>> callback) {
		callback.onSuccess(INSTRUCTORS);
	}

	@Override
	public void removeInstructor(Integer realInstructorID, AsyncCallback<Void> callback) {
		callback.onFailure(new UnsupportedOperationException("Cannot edit instrutor using this test double"));
	}

	@Override
	public void editInstructor(InstructorGWT realInstructor, AsyncCallback<Void> callback) {
		callback.onFailure(new UnsupportedOperationException("Cannot edit instrcutor using this test double"));
	}

	@Override
	public void addInstructorToDocument(int documentID, InstructorGWT realInstructor, AsyncCallback<InstructorGWT> callback) {
		callback.onFailure(new UnsupportedOperationException("Cannot add instructor using this test double"));
	}

	@Override
	public void addLocationToDocument(int documentID, LocationGWT location, AsyncCallback<LocationGWT> callback) {
		callback.onFailure(new UnsupportedOperationException("Cannot add location using this test double"));
	}

	@Override
	public void editLocation(LocationGWT source, AsyncCallback<Void> callback) {
		callback.onFailure(new UnsupportedOperationException("Cannot edit location using this test double"));
	}

	@Override
	public void getLocationsForDocument(int documentID, AsyncCallback<List<LocationGWT>> callback) {
		callback.onSuccess(LOCATIONS);
	}

	@Override
	public void removeLocation(Integer locationID, AsyncCallback<Void> callback) {
		callback.onFailure(new UnsupportedOperationException("Cannot remove location using this test double"));
	}

	@Override
	public void login(String username, AsyncCallback<Integer> callback) {
		callback.onFailure(new UnsupportedOperationException("Cannot login using this test double"));
	}

	@Override
	public void createOriginalDocument(String newDocName, AsyncCallback<DocumentGWT> callback) {
		callback.onFailure(new UnsupportedOperationException("Cannot create document using this test double"));
	}

	@Override
	public void saveWorkingCopyToOriginalDocument(Integer id, AsyncCallback<Void> callback) {
		callback.onFailure(new UnsupportedOperationException("Cannot save working copy using this test double"));
	}

	@Override
	public void createWorkingCopyForOriginalDocument(Integer originalDocumentID, AsyncCallback<DocumentGWT> callback) {
		callback.onFailure(new UnsupportedOperationException("Cannot create working copy using this test double"));
	}

	@Override
	public void deleteWorkingCopyDocument(Integer documentID, AsyncCallback<Void> callback) {
		callback.onFailure(new UnsupportedOperationException("Cannot delete working copy using this test double"));
	}

	@Override
	public void insertScheduleItem(int scheduleID, ScheduleItemGWT scheduleItem, AsyncCallback<Collection<ScheduleItemGWT>> callback) {
		callback.onFailure(new UnsupportedOperationException("Cannot insert item using this test double"));
	}

	@Override
	public void generateRestOfSchedule(int scheduleID, AsyncCallback<Collection<ScheduleItemGWT>> callback) {
		callback.onFailure(new UnsupportedOperationException("Cannot generate schedule using this test double"));
	}

	@Override
	public void updateScheduleItem(ScheduleItemGWT itemGWT, AsyncCallback<Collection<ScheduleItemGWT>> callback) {
		callback.onFailure(new UnsupportedOperationException("Cannot update item using this test double"));
	}

	@Override
	public void getScheduleItems(int scheduleID, AsyncCallback<Collection<ScheduleItemGWT>> callback) {
		callback.onSuccess(SCHEDULE_ITEMS);
	}

	@Override
	public void newRemoveScheduleItem(ScheduleItemGWT itemGWT, AsyncCallback<Collection<ScheduleItemGWT>> callback) {
		callback.onFailure(new UnsupportedOperationException("Cannot remove item using this test double"));
	}

	@Override
	public void updateDocument(DocumentGWT document, AsyncCallback<Void> callback) {
		callback.onFailure(new UnsupportedOperationException("Cannot update document using this test double"));
	}

	@Override
	public void associateWorkingCopyWithNewOriginalDocument(Integer workingCopyID, String scheduleName, boolean allowOverwrite, AsyncCallback<Void> callback) {
		callback.onFailure(new UnsupportedOperationException("Cannot associate working copy using this test double"));
	}

	@Override
	public void findDocumentByID(int automaticOpenDocumentID, AsyncCallback<DocumentGWT> callback) {
		callback.onFailure(new UnsupportedOperationException("Cannot find document using this test double"));
	}

	@Override
	public void getAllOriginalDocuments(AsyncCallback<Collection<DocumentGWT>> callback) {
		callback.onFailure(new UnsupportedOperationException("Cannot get documents using this test double"));
	}

	@Override
	public void removeOriginalDocument(Integer id, AsyncCallback<Void> callback) {
		callback.onFailure(new UnsupportedOperationException("Cannot remove document using this test double"));
	}

	@Override
	public void updateCourses(int documentID, List<CourseGWT> addedResources, Collection<CourseGWT> editedResources, List<Integer> deletedResourcesIDs, AsyncCallback<List<Integer>> callback) {
		callback.onFailure(new UnsupportedOperationException("Cannot update courses using this test double"));
	}
}
