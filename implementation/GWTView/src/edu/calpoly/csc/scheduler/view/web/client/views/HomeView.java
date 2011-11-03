package edu.calpoly.csc.scheduler.view.web.client.views;

import java.util.Map;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
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
		
		final HomeView self = this;

		vp.add(new Button("Open", new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				final LoadingPopup popup = new LoadingPopup();
				popup.show();
				
				String existingScheduleStr = listBox.getValue(listBox.getSelectedIndex());
				service.openExistingSchedule(existingScheduleStr, new AsyncCallback<Void>() {
					@Override
					public void onFailure(Throwable caught) {
						popup.hide();
						Window.alert("Failed to open schedule in: " + caught.getMessage());
					}
					@Override
					public void onSuccess(Void derp) {
						popup.hide();
						container.clear();
						container.add(new ScheduleNavView(self, container, service));
					}
				});
			}
		}));
		
		vp.add(new Button("New Schedule", new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				displayNewSchedPopup();
			}
		}));
	}
	
	private void populateSchedules() {
		
		service.getScheduleNames(new AsyncCallback<Map<String,Integer>>() {
			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub
				
			}
			@Override
			public void onSuccess(Map<String, Integer> schedulesIDsAndNames) {
				for (String scheduleID : schedulesIDsAndNames.keySet())
					listBox.addItem(scheduleID);
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
	
	public void displayNewSchedPopup() {
		final HomeView self = this;
		
		final TextBox tb = new TextBox();
		final DialogBox db = new DialogBox(false);
		VerticalPanel vp = new VerticalPanel();
		
		db.setText("Name Schedule");
		vp.add(new HTML("<center>Specify a new schedule name.</center>"));
		vp.add(tb);
		vp.add(new Button("Create Schedule", new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {		
				db.hide();
				
			    final LoadingPopup popup = new LoadingPopup();
			    popup.show();
				
				service.openNewSchedule(tb.getText(), new AsyncCallback<Void>() {
					@Override
					public void onFailure(Throwable caught) {
						popup.hide();
						Window.alert("Failed to open new schedule in: " + caught.getMessage());
					}
					
					@Override
					public void onSuccess(Void result) {
						popup.hide();
						container.clear();
						container.add(new ScheduleNavView(self, container, service));
					}
				});
			}
		}));
		
		db.setWidget(vp);
		db.center();
	}
}
