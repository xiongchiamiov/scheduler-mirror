package scheduler.view.web.shared;

import java.io.Serializable;

public class WorkingDocumentSynchronizeResponse implements Serializable {
	private static final long serialVersionUID = 1L;

	public SynchronizeResponse<CourseGWT> courses = new SynchronizeResponse<CourseGWT>();
	public SynchronizeResponse<InstructorGWT> instructors = new SynchronizeResponse<InstructorGWT>();
	public SynchronizeResponse<LocationGWT> locations = new SynchronizeResponse<LocationGWT>();
	public SynchronizeResponse<ScheduleItemGWT> scheduleItems = new SynchronizeResponse<ScheduleItemGWT>();
	
	public WorkingDocumentSynchronizeResponse() { }

	public WorkingDocumentSynchronizeResponse(SynchronizeResponse<CourseGWT> courses,
			SynchronizeResponse<InstructorGWT> instructors, SynchronizeResponse<LocationGWT> locations,
			SynchronizeResponse<ScheduleItemGWT> scheduleItems) {
		super();
		this.courses = courses;
		this.instructors = instructors;
		this.locations = locations;
		this.scheduleItems = scheduleItems;
	}
}
