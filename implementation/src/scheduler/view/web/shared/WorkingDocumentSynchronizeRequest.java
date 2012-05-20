package scheduler.view.web.shared;

import java.io.Serializable;

public class WorkingDocumentSynchronizeRequest implements Serializable {
	private static final long serialVersionUID = 1L;

	public SynchronizeRequest<CourseGWT> courses = new SynchronizeRequest<CourseGWT>();
	public SynchronizeRequest<InstructorGWT> instructors = new SynchronizeRequest<InstructorGWT>();
	public SynchronizeRequest<LocationGWT> locations = new SynchronizeRequest<LocationGWT>();
	public SynchronizeRequest<ScheduleItemGWT> scheduleItems = new SynchronizeRequest<ScheduleItemGWT>();
	
	public WorkingDocumentSynchronizeRequest() { }

	public WorkingDocumentSynchronizeRequest(SynchronizeRequest<CourseGWT> courses,
			SynchronizeRequest<InstructorGWT> instructors, SynchronizeRequest<LocationGWT> locations,
			SynchronizeRequest<ScheduleItemGWT> scheduleItems) {
		super();
		this.courses = courses;
		this.instructors = instructors;
		this.locations = locations;
		this.scheduleItems = scheduleItems;
	}
}
