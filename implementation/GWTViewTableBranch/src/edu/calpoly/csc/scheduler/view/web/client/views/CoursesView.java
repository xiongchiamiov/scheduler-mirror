package edu.calpoly.csc.scheduler.view.web.client.views;

import java.util.Collection;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.calpoly.csc.scheduler.view.web.client.GreetingServiceAsync;
import edu.calpoly.csc.scheduler.view.web.client.table.Table;
import edu.calpoly.csc.scheduler.view.web.client.table.TableFactory;
import edu.calpoly.csc.scheduler.view.web.shared.CourseGWT;

public class CoursesView extends ScrollPanel {
	private GreetingServiceAsync service;
	private Table<CourseGWT> cTable;

	public CoursesView(GreetingServiceAsync greetingService, String scheduleName) {
		this.service = greetingService;

		setWidth("100%");
		setHeight("100%");
		
		VerticalPanel vp = new VerticalPanel();
		this.add(vp);

		vp.add(new HTML("<h2>" + scheduleName + " - Courses</h2>"));
		
		cTable = TableFactory.course(service);
		vp.add(cTable.getWidget());
	}
	
	@Override
	protected void onLoad() {
		super.onLoad();

		final LoadingPopup popup = new LoadingPopup();
		popup.show();

		cTable.clear();
		
		service.getCourses(new AsyncCallback<Collection<CourseGWT>>() {
			public void onFailure(Throwable caught) {
				popup.hide();
				
				Window.alert("Failed to get courses: " + caught.toString());
			}
			
			public void onSuccess(Collection<CourseGWT> result){
				popup.hide();
				
				if (result != null) {
					cTable.set(result);
				}
			}
		});
	}
}
