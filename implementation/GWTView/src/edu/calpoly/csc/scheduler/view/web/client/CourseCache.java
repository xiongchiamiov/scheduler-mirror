package edu.calpoly.csc.scheduler.view.web.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

import edu.calpoly.csc.scheduler.view.web.client.views.AssociationsCell.GetCoursesCallback;
import edu.calpoly.csc.scheduler.view.web.shared.CourseGWT;

public class CourseCache {
	final GreetingServiceAsync service;
	final HashMap<Integer, CourseGWT> existingCourses = new HashMap<Integer, CourseGWT>();
	
	public CourseCache(GreetingServiceAsync service) {
		this.service = service;
	}
	
	public ArrayList<CourseGWT> getCourses() {
		return new ArrayList<CourseGWT>(existingCourses.values());
	}

	public void getCourses(final AsyncCallback<List<CourseGWT>> asyncCallback) {
		service.getCourses(new AsyncCallback<List<CourseGWT>>() {
			public void onSuccess(List<CourseGWT> result) {
				existingCourses.clear();
				for (CourseGWT course : result)
					existingCourses.put(course.getID(), course);
				asyncCallback.onSuccess(result);
			}
			
			public void onFailure(Throwable caught) { asyncCallback.onFailure(caught); }
		});
	}

	public void addCourse(CourseGWT toAdd, final AsyncCallback<CourseGWT> callback) {
		service.addCourse(toAdd, new AsyncCallback<CourseGWT>() {
			public void onSuccess(CourseGWT result) {
				existingCourses.put(result.getID(), result);
				callback.onSuccess(result);
			}
			
			public void onFailure(Throwable caught) { callback.onFailure(caught); }
		});
	}

	public void editCourse(final CourseGWT toEdit, final AsyncCallback<Void> callback) {
		service.editCourse(toEdit, new AsyncCallback<Void>() {
			public void onSuccess(Void v) {
				existingCourses.put(toEdit.getID(), toEdit);
				callback.onSuccess(null);
			}
			
			public void onFailure(Throwable caught) { callback.onFailure(caught); }
		});
	}

	public void removeCourse(final CourseGWT toRemove, final AsyncCallback<Void> callback) {
		service.removeCourse(toRemove, new AsyncCallback<Void>() {
			public void onSuccess(Void v) {
				existingCourses.remove(toRemove.getID());
				callback.onSuccess(null);
			}
			
			public void onFailure(Throwable caught) { callback.onFailure(caught); }
		});
	}
}
