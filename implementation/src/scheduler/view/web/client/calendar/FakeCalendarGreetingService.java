package scheduler.view.web.client.calendar;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import scheduler.view.web.client.GreetingServiceAsync;
import scheduler.view.web.shared.ClientChangesResponse;
import scheduler.view.web.shared.CompleteWorkingCopyDocumentGWT;
import scheduler.view.web.shared.CourseGWT;
import scheduler.view.web.shared.DayGWT;
import scheduler.view.web.shared.InstructorGWT;
import scheduler.view.web.shared.LocationGWT;
import scheduler.view.web.shared.LoginResponse;
import scheduler.view.web.shared.OriginalDocumentGWT;
import scheduler.view.web.shared.ScheduleItemGWT;
import scheduler.view.web.shared.ServerResourcesResponse;
import scheduler.view.web.shared.SynchronizeRequest;
import scheduler.view.web.shared.SynchronizeResponse;

import com.google.gwt.user.client.rpc.AsyncCallback;

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
	public void saveWorkingCopyToOriginalDocument(int sessionID, int workingCopyDocumentID, AsyncCallback<Void> callback) {
		callback.onFailure(new UnsupportedOperationException("Method not implemented in this test double."));
	}

	@Override
	public void createAndOpenWorkingCopyForOriginalDocument(
			int sessionID,
			int originalDocumentID,
			AsyncCallback<CompleteWorkingCopyDocumentGWT> callback) {
		callback.onFailure(new UnsupportedOperationException("Method not implemented in this test double."));
	}

	@Override
	public void deleteWorkingCopyDocument(int sessionID, int documentID, AsyncCallback<Void> callback) {
		callback.onFailure(new UnsupportedOperationException("Method not implemented in this test double."));
	}

	@Override
	public void associateWorkingCopyWithNewOriginalDocument(
			int sessionID,
			int workingCopyID,
			String scheduleName,
			boolean allowOverwrite,
			AsyncCallback<Void> callback) {
		callback.onFailure(new UnsupportedOperationException("Method not implemented in this test double."));
	}

	@Override
	public void synchronizeDocumentCourses(
			int sessionID,
			int documentID,
			SynchronizeRequest<CourseGWT> request,
			AsyncCallback<SynchronizeResponse<CourseGWT>> callback) {
		callback.onSuccess(new SynchronizeResponse<CourseGWT>(new ClientChangesResponse(new LinkedList<Integer>()), new ServerResourcesResponse<CourseGWT>(COURSES)));
	}

	@Override
	public void synchronizeDocumentInstructors(
			int sessionID,
			int documentID,
			SynchronizeRequest<InstructorGWT> request,
			AsyncCallback<SynchronizeResponse<InstructorGWT>> callback) {
		callback.onSuccess(new SynchronizeResponse<InstructorGWT>(new ClientChangesResponse(new LinkedList<Integer>()), new ServerResourcesResponse<InstructorGWT>(INSTRUCTORS)));
	}

	@Override
	public void synchronizeDocumentLocations(
			int sessionID,
			int documentID,
			SynchronizeRequest<LocationGWT> request,
			AsyncCallback<SynchronizeResponse<LocationGWT>> callback) {
		callback.onSuccess(new SynchronizeResponse<LocationGWT>(new ClientChangesResponse(new LinkedList<Integer>()), new ServerResourcesResponse<LocationGWT>(LOCATIONS)));
	}

	@Override
	public void synchronizeOriginalDocuments(
			int sessionID,
			SynchronizeRequest<OriginalDocumentGWT> request,
			AsyncCallback<SynchronizeResponse<OriginalDocumentGWT>> callback) {
		callback.onFailure(new UnsupportedOperationException("Method not implemented in this test double."));
	}

	@Override
	public void generateRestOfSchedule(
			int sessionID,
			int scheduleID,
			AsyncCallback<ServerResourcesResponse<ScheduleItemGWT>> callback) {
		callback.onFailure(new UnsupportedOperationException("Method not implemented in this test double."));
	}

	@Override
	public void getAllOriginalDocuments(
			int sessionID,
			AsyncCallback<ServerResourcesResponse<OriginalDocumentGWT>> callback) {
		callback.onFailure(new UnsupportedOperationException("Method not implemented in this test double."));
	}

	@Override
	public void synchronizeDocumentScheduleItems(
			int sessionID,
			int documentID,
			SynchronizeRequest<ScheduleItemGWT> request,
			AsyncCallback<SynchronizeResponse<ScheduleItemGWT>> callback) {
		callback.onFailure(new UnsupportedOperationException("Method not implemented in this test double."));
	}

	@Override
	public void loginAndGetAllOriginalDocuments(String username, AsyncCallback<LoginResponse> callback) {
		callback.onFailure(new UnsupportedOperationException("Method not implemented in this test double."));
	}

}
