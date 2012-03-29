package scheduler.view.web.client.views.resources.courses;

import scheduler.view.web.client.table.IStaticValidator.InputValid;
import scheduler.view.web.client.table.IStaticValidator.ValidateResult;
import scheduler.view.web.client.table.OsmTable;
import scheduler.view.web.client.table.OsmTable.Cell;
import scheduler.view.web.client.table.OsmTable.EditingCell;
import scheduler.view.web.client.table.OsmTable.IRowForColumn;
import scheduler.view.web.client.table.OsmTable.ReadingCell;
import scheduler.view.web.client.views.resources.courses.AssociationsCell.GetCoursesCallback;
import scheduler.view.web.shared.CourseGWT;

public class AssociationsColumn implements OsmTable.IEditingColumn<CourseGWT> {
	final GetCoursesCallback getCourses;
	
	public AssociationsColumn(GetCoursesCallback getCourses) {
		this.getCourses = getCourses;
	}
	
	@Override
	public Cell createCell(IRowForColumn<CourseGWT> row) {
		return new AssociationsCell(getCourses);
	}

	@Override
	public void updateFromObject(IRowForColumn<CourseGWT> row, ReadingCell rawCell) {
		AssociationsCell cell = (AssociationsCell)rawCell;
		int lectureID = row.getObject().getLectureID();
		
		setCellSelectedCourse(cell, lectureID);
		
		System.out.println(row.getObject().getType() + " so " + row.getObject().getType().equals("LEC"));
		cell.setCourseIsLecture(row.getObject().getType().equals("LEC"));
	}
	
	private void setCellSelectedCourse(AssociationsCell cell, int courseID) {
		if (courseID < 0) {
			cell.setSelectedCourse(null);
			return;
		}
		
		for (CourseGWT course : getCourses.getCourses()) {
			if (course.getID() == courseID) {
				cell.setSelectedCourse(course);
				return;
			}
		}

		System.out.println("ERROR Trying to set selected course id " + courseID);
		assert(false);
	}

	@Override
	public void commitToObject(IRowForColumn<CourseGWT> row, EditingCell rawCell) {
		AssociationsCell cell = (AssociationsCell)rawCell;
		row.getObject().setLectureID(cell.getSelectedCourseID());
		System.out.println("Setting lectureID to " + cell.getSelectedCourseID());
	}

	@Override
	public ValidateResult validate(IRowForColumn<CourseGWT> row, EditingCell cell) {
		return new InputValid();
	}
}
