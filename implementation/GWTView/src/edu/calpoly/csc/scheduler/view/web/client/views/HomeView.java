package edu.calpoly.csc.scheduler.view.web.client.views;

import java.util.ArrayList;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import edu.calpoly.csc.scheduler.view.web.client.GreetingServiceAsync;
import edu.calpoly.csc.scheduler.view.web.client.HTMLUtilities;
import edu.calpoly.csc.scheduler.view.web.shared.gwtScheduleItem;

public class HomeView extends SimplePanel {
	private GreetingServiceAsync service;
	private Panel container;
	private ListBox listBox;
	private VerticalPanel vp;
	
	public HomeView(Panel container, GreetingServiceAsync service) {
		this.container = container;
		this.service = service;

        vp = new VerticalPanel();		
		listBox = new ListBox();
	}
	
	@Override
	public void onLoad() {
		super.onLoad();
		
		addStyleName("homeView");		
		
		vp.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		
		this.add(vp);
		
		vp.add(createTitleBar());
		
		populateSchedules();
		
		vp.add(listBox);

		vp.add(new Button("Open", new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				container.clear();
				container.add(new QuarterView(container, service, listBox.getValue(listBox.getSelectedIndex())));
			}
		}));
		
		vp.add(new Button("New Schedule", new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				container.clear();
				container.add(new QuarterView(container, service, ""));
			}
		}));
		
		vp.add(createDBInfoPanel());
		

	}
	
	private void populateSchedules() {
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
		listBox.addItem("Fall Quarter 2011 Final Schedule");
		listBox.addItem("Fall Quarter 2011 First Draft Schedule");
		listBox.addItem("Summer Quarter 2011 Final Schedule");
		listBox.addItem("Summer Quarter 2011 First Draft Schedule");
		listBox.addItem("Spring Quarter 2011 Final Schedule");
	}
	
	private Widget createTitleBar() {
		return new HTMLPanel("<h2>Home</h2><h4>Previous Schedules</h4>");
	}
	
	private Widget createDBInfoPanel() {
		VerticalPanel vp = new VerticalPanel();
		
		vp.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		
		vp.add(new HTML("<br /><b>13</b> Instructors"));
		
		vp.add(HTMLUtilities.createLink("Modify Instructors", "inAppLink", new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				container.clear();
				container.add(new InstructorsView(container, service, "0"));
			}
		}));
		
		vp.add(new HTML("<b>32</b> Courses"));
		
		vp.add(HTMLUtilities.createLink("Modify Courses", "inAppLink", new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				container.clear();
				container.add(new CoursesView(container, service, "0"));
			}
		}));
		
		vp.add(new HTML("<b>7</b> Locations"));
		
		vp.add(HTMLUtilities.createLink("Modify Locations", "inAppLink", new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				container.clear();
				container.add(new RoomsView(container, service, "0"));
			}
		}));
		
		return vp;
	}
}
