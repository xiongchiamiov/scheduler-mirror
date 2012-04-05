package scheduler.view.web.client.views.resources.instructors;

import scheduler.view.web.client.GreetingServiceAsync;
import scheduler.view.web.shared.InstructorGWT;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.smartgwt.client.widgets.Window;

public class InstructorsHomeView extends VerticalPanel{
	//private List<CourseGWT> courseList = new ArrayList<CourseGWT>();
	private FlexTable schedList = new FlexTable();
	
	public InstructorsHomeView()
	{
		//Hard coding temporary data for the schedule list
		
		schedList.setWidget(0, 0, new HTML("Winter 2012 &nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp"));
		Button prefs = new Button("Preferences");
		prefs.addClickHandler(new ClickHandler(){

			@Override
			public void onClick(ClickEvent event) {
				final Window win1 = new Window();
				win1.setTitle("Course Preferences");
				win1.setAutoCenter(true);
				win1.setSize("500px", "400px");
				
				final Window win2 = new Window();
				win2.setTitle("Time Preferences");
				win2.setAutoCenter(true);
				win2.setSize("500px", "400px");

//================================================================================
//				the following code is important, but can not be executed yet,
//				because there is some data missing (service, documentID and instructor):
//================================================================================
//				
//				// has to be fetched...
//				GreetingServiceAsync service = null;
//				int documentID = 0;
//				InstructorGWT instructor = null;
//				
//				final InstructorPrefsWizardCourseView courses =
//						new InstructorPrefsWizardCourseView(service, documentID, instructor);
//				final InstructorPrefsWizardTimeView times =
//						new InstructorPrefsWizardTimeView(service, documentID, instructor);
//				courses.addCloseClickHandler(new ClickHandler(){
//					@Override
//					public void onClick(ClickEvent event) {
//						win1.hide();
//					}
//				});
//				courses.addNextClickHandler(new ClickHandler(){
//					@Override
//					public void onClick(ClickEvent event) {
//						win1.hide();
//						win2.show();
//					}
//				});
//				times.addFinishClickHandler(new ClickHandler(){
//					@Override
//					public void onClick(ClickEvent event) {
//						win2.hide();
//					}
//				});
//				times.addBackClickHandler(new ClickHandler(){
//					@Override
//					public void onClick(ClickEvent event) {
//						win2.hide();
//						win1.show();
//					}
//				});
//				courses.setParent(win1);
//				courses.afterPush();
//				
//				win1.addItem(courses);
//				win2.addItem(times);
//================================================================================
				win1.show();
			}
			
		});
		schedList.setWidget(0, 1, prefs);
		
		schedList.setStyleName("otherCenterness");
		HTML schedule = new HTML("Scheduler\n\n\n");
		
		HTML mydocs = new HTML("My Scheduling Documents:");
		mydocs.setStyleName("centerness");
		
		schedule.addStyleName("editTableHeading");
		
		this.setStyleName("centerness");
		add(schedule);
		add(mydocs);
		add(schedList);
	}
	
	
}
