package scheduler.view.web.shared;

import java.io.Serializable;

public class CompleteWorkingCopyDocumentGWT implements Serializable {
	private static final long serialVersionUID = 1L;

	public WorkingDocumentGWT realWorkingDocument;
	public ServerResourcesResponse<CourseGWT> courses;
	public ServerResourcesResponse<InstructorGWT> instructors;
	public ServerResourcesResponse<LocationGWT> locations;
	public ServerResourcesResponse<ScheduleItemGWT> scheduleItems;
	
	public CompleteWorkingCopyDocumentGWT() { }
	
	public CompleteWorkingCopyDocumentGWT(
			WorkingDocumentGWT realWorkingDocument,
			ServerResourcesResponse<CourseGWT> courses,
			ServerResourcesResponse<InstructorGWT> instructors,
			ServerResourcesResponse<LocationGWT> locations,
			ServerResourcesResponse<ScheduleItemGWT> scheduleItems) {
		this.realWorkingDocument = realWorkingDocument;
		this.courses = courses;
		this.instructors = instructors;
		this.locations = locations;
		this.scheduleItems = scheduleItems;
	}
}
