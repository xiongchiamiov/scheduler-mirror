package scheduler.view.web.client.views.resources.instructors;

import java.util.ArrayList;
import java.util.List;

import scheduler.view.web.shared.CourseGWT;

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
				Window win = new Window();
				win.setTitle("Preferences");
				win.setAutoCenter(true);
				win.setSize("500px", "400px");
				win.addItem(new InstructorsWizardCoursesView());
				win.show();
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
