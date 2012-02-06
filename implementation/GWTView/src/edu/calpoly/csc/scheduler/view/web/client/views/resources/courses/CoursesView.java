package edu.calpoly.csc.scheduler.view.web.client.views.resources.courses;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import edu.calpoly.csc.scheduler.view.web.client.GreetingServiceAsync;
import edu.calpoly.csc.scheduler.view.web.client.IViewContents;
import edu.calpoly.csc.scheduler.view.web.client.ViewFrame;
import edu.calpoly.csc.scheduler.view.web.client.views.LoadingPopup;
import edu.calpoly.csc.scheduler.view.web.shared.CourseGWT;
import edu.calpoly.csc.scheduler.view.web.shared.DayCombinationGWT;

public class CoursesView extends VerticalPanel implements IViewContents, CoursesTable.Strategy {
	private GreetingServiceAsync service;
	private String scheduleName;
	int nextTableCourseID = -2;
	int transactionsPending = 0;
	Map<Integer, Integer> realIDsByTableID = new HashMap<Integer, Integer>();
	
	final ArrayList<Integer> deletedTableCourseIDs = new ArrayList<Integer>();
	final ArrayList<CourseGWT> editedTableCourses = new ArrayList<CourseGWT>();
	final ArrayList<CourseGWT> addedTableCourses = new ArrayList<CourseGWT>();
	
	private int generateTableCourseID() {
		return nextTableCourseID--;
	}
	
	public CoursesView(GreetingServiceAsync service, String scheduleName) {
		this.service = service;
		this.scheduleName = scheduleName;
		this.addStyleName("iViewPadding");
	}

	@Override
	public boolean canPop() {
		return true;
//		assert(table != null);
//		if (table.isSaved())
//			return true;
//		return Window.confirm("You have unsaved data which will be lost. Are you sure you want to navigate away?");
	}
	
	@Override
	public void afterPush(ViewFrame frame) {		
		this.setWidth("100%");
		this.setHeight("100%");

		this.add(new HTML("<h2>" + scheduleName + " - Courses</h2>"));

		add(new CoursesTable(this));
	}

	@Override
	public void getAllCourses(final AsyncCallback<List<CourseGWT>> callback) {
		final LoadingPopup popup = new LoadingPopup();
		popup.show();

		service.getCourses(new AsyncCallback<List<CourseGWT>>() {
			public void onFailure(Throwable caught) {
				popup.hide();
				callback.onFailure(caught);
			}
			
			public void onSuccess(List<CourseGWT> courses){
				assert(courses != null);
				popup.hide();
				
				callback.onSuccess(courses);
			}
		});
	}

	@Override
	public CourseGWT createCourse() {
		CourseGWT course = new CourseGWT("", "", "", 0, 0, 0, "LEC", 0, -1, 6, new HashSet<DayCombinationGWT>(), 0, generateTableCourseID(), false);
		
		addedTableCourses.add(course);
		
		assert(!editedTableCourses.contains(course));
		
		assert(!deletedTableCourseIDs.contains(course));

		sendUpdates();
		
		return course;
	}
	
	@Override
	public void onCourseEdited(CourseGWT course) {
		assert(!deletedTableCourseIDs.contains(course.getID()));
		
		if (realIDsByTableID.containsKey(course.getID())) {
			// exists on remote side
			if (!editedTableCourses.contains(course))
				editedTableCourses.add(course);
		}
		else {
			// doesnt exist on remote side
			// do nothing, its already on the add list.
			assert(addedTableCourses.contains(course));
		}
		
		sendUpdates();
	}
	
	@Override
	public void onCourseDeleted(CourseGWT course) {
		editedTableCourses.remove(course);
		
		if (addedTableCourses.contains(course)) {
			addedTableCourses.remove(course);
			return;
		}
		
		assert(!deletedTableCourseIDs.contains(course.getID()));
		deletedTableCourseIDs.add(course.getID());
		
		sendUpdates();
	}

	private void sendUpdates() {
		assert(transactionsPending == 0);
		transactionsPending = deletedTableCourseIDs.size() + editedTableCourses.size() + addedTableCourses.size();
		if (transactionsPending == 0)
			return;
		
		for (Integer deletedTableCourseID : deletedTableCourseIDs) {
			Integer realCourseID = realIDsByTableID.get(deletedTableCourseID);
			service.removeCourse(realCourseID, new AsyncCallback<Void>() {
				public void onSuccess(Void result) { updateFinished(); }
				public void onFailure(Throwable caught) { Window.alert("Update failed: " + caught.getMessage()); }
			});
		}
		
		for (CourseGWT editedTableCourse : editedTableCourses) {
			Integer realCourseID = realIDsByTableID.get(editedTableCourse.getID());
			CourseGWT realCourse = new CourseGWT(editedTableCourse);
			realCourse.setID(realCourseID);
			service.editCourse(realCourse, new AsyncCallback<Void>() {
				public void onSuccess(Void result) { updateFinished(); }
				public void onFailure(Throwable caught) { Window.alert("Update failed: " + caught.getMessage()); }
			});
		}
		
		for (CourseGWT addedTableCourse : addedTableCourses) {
			final int tableCourseID = addedTableCourse.getID();
			CourseGWT realCourse = new CourseGWT(addedTableCourse);
			realCourse.setID(-1);
			service.addCourse(realCourse, new AsyncCallback<CourseGWT>() {
				public void onSuccess(CourseGWT result) {
					realIDsByTableID.put(tableCourseID, result.getID());
					updateFinished();
				}
				public void onFailure(Throwable caught) { Window.alert("Update failed: " + caught.getMessage()); }
			});
		}
		
		deletedTableCourseIDs.clear();
		editedTableCourses.clear();
		addedTableCourses.clear();
	}
	
	private void updateFinished() {
		assert(transactionsPending > 0);
		transactionsPending--;
		if (transactionsPending == 0)
			sendUpdates();
	}

	@Override
	public void beforePop() { }
	@Override
	public void beforeViewPushedAboveMe() { }
	@Override
	public void afterViewPoppedFromAboveMe() { }
	@Override
	public Widget getContents() { return this; }
}
