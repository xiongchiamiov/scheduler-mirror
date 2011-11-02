package edu.calpoly.csc.scheduler.view.web.client.views;

import java.util.Map;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Random;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import edu.calpoly.csc.scheduler.view.web.client.GreetingServiceAsync;
import edu.calpoly.csc.scheduler.view.web.client.HTMLUtilities;

public class HomeView extends ScrollPanel {
	private GreetingServiceAsync service;
	private Panel container;
	private ListBox listBox;
	private VerticalPanel vp;
	
	public HomeView(Panel container, GreetingServiceAsync service) {
		this.container = container;
		this.service = service;
	}
	
	@Override
	public void onLoad() {
		super.onLoad();

		addStyleName("homeView");
		
        vp = new VerticalPanel();
		add(vp);
		vp.setWidth("100%");
		vp.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		
		vp.add(createTitleBar());
		
		listBox = new ListBox();
		vp.add(listBox);
		
		populateSchedules();

		vp.add(new Button("Open", new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				int existingScheduleID = Integer.parseInt(listBox.getValue(listBox.getSelectedIndex()));
				service.openExistingSchedule(existingScheduleID, new AsyncCallback<Void>() {
					@Override
					public void onFailure(Throwable caught) {
						// TODO Auto-generated method stub
						
					}
					@Override
					public void onSuccess(Void result) {
						container.clear();
						container.add(new QuarterView(container, service));
					}
				});
			}
		}));
		
		vp.add(new Button("New Schedule", new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				String schedName = Window.prompt("What would you like to name your new schedule?", "");
				
				service.openNewSchedule(schedName, new AsyncCallback<Void>() {
					@Override
					public void onFailure(Throwable caught) {
						Window.alert("Failed to create new schedule: " + caught.getMessage());
					}
					
					@Override
					public void onSuccess(Void result) {
						container.clear();
						container.add(new QuarterView(container, service));
					}
				});
			}
		}));
	}
	
	private void populateSchedules() {
		service.getScheduleNames(new AsyncCallback<Map<Integer,String>>() {
			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Error getting schedule names: " + caught.getMessage());
			}
			
			@Override
			public void onSuccess(Map<Integer, String> schedulesIDsAndNames) {
				for (Integer scheduleID : schedulesIDsAndNames.keySet())
					listBox.addItem(schedulesIDsAndNames.get(scheduleID), scheduleID.toString());
			}
		});
		
	}
	
	private Widget createTitleBar() {
		return new HTMLPanel("<h2>Select a Schedule</h2><h4>Previous Schedules</h4>");
	}
	
	private Widget createDBInfoPanel() {
		VerticalPanel vp = new VerticalPanel();
		
		vp.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		
		vp.add(new HTML("<br /><b>13</b> Instructors"));
		
		vp.add(HTMLUtilities.createLink("Modify Instructors", "inAppLink", new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				container.clear();
				container.add(new InstructorsView(container, service));
			}
		}));
		
		vp.add(new HTML("<b>32</b> Courses"));
		
		vp.add(HTMLUtilities.createLink("Modify Courses", "inAppLink", new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				container.clear();
				container.add(new CoursesView(container, service));
			}
		}));
		
		vp.add(new HTML("<b>7</b> Locations"));
		
		vp.add(HTMLUtilities.createLink("Modify Locations", "inAppLink", new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				container.clear();
				container.add(new RoomsView(container, service));
			}
		}));
		
		return vp;
	}
	
	public String displayNewSchedPopup() {
		PopupPanel popup = new PopupPanel();
		
		popup.add(new TextBox());
		
		return new String();
	}
}
