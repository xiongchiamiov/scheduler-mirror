package edu.calpoly.csc.scheduler.view.web.client.views;

import java.util.Map;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import edu.calpoly.csc.scheduler.view.web.client.GreetingServiceAsync;
import edu.calpoly.csc.scheduler.view.web.client.HTMLUtilities;

public class HomeView extends ScrollPanel {
	private GreetingServiceAsync service;
	private Panel container;
	private ListBox listBox;
	private VerticalPanel vp;
	String userid;
	
	public HomeView(Panel container, GreetingServiceAsync service, String userid) {
		this.container = container;
		this.service = service;

        vp = new VerticalPanel();		
		listBox = new ListBox();
		this.userid = userid;
	}
	
	@Override
	public void onLoad() {
		super.onLoad();
		
		service.getScheduleNames(userid, new AsyncCallback<Map<Integer,String>>() {
			
			@Override
			public void onSuccess(Map<Integer, String> result) {
				// TODO Auto-generated method stub

				addStyleName("homeView");		
				
				vp.setWidth("100%");
				vp.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
				
				add(vp);
				
				vp.add(createTitleBar());
				
				vp.add(listBox);
				

				
				populateSchedules(result);

				vp.add(new Button("Open", new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
						service.selectSchedule(Integer.parseInt(listBox.getValue(listBox.getSelectedIndex())), new AsyncCallback<Void>() {
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
						service.newSchedule(new AsyncCallback<Void>() {
							@Override
							public void onFailure(Throwable caught) {
								// TODO Auto-generated method stub
								
							}
							
							@Override
							public void onSuccess(Void result) {
								// TODO Auto-generated method stub
								container.clear();
								container.add(new QuarterView(container, service));
							}
						});
					}
				}));
				
				vp.add(createDBInfoPanel());
				
			}
			
			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub
				
			}
		});
	}
	
	private void populateSchedules(Map<Integer, String> schedulesIDsAndNames) {
		/*greetingService.getSchedules(new AsyncCallback<ArrayList<ScheduleGWT>>() {
			public void onFailure(Throwable caught) {
				Window.alert("Failed to get schedules: " + caught.toString());
			}
			
			public void onSuccess(ArrayList<ScheduleGWT> result){
				if (result != null) {
					for (ScheduleGWT sched : result) {
                       listBox.addItem(sched.toString());
					}
				}
			}
		});*/
		for (Integer scheduleID : schedulesIDsAndNames.keySet())
			listBox.addItem(schedulesIDsAndNames.get(scheduleID), scheduleID.toString());
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
}
