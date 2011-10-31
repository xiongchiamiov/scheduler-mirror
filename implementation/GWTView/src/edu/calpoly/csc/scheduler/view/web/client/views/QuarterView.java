package edu.calpoly.csc.scheduler.view.web.client.views;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

import edu.calpoly.csc.scheduler.view.web.client.GreetingServiceAsync;

public class QuarterView extends DockLayoutPanel {
	Panel container;
	GreetingServiceAsync service;
	Panel contentPanel;
	
	QuarterView(Panel container, GreetingServiceAsync service) {
		super(Unit.EM);
		
		this.container = container;
		this.service = service;
	}
	
	@Override
	public void onLoad() {
		super.onLoad();

		setWidth("100%");
		setHeight("100%");
		
		Widget leftPanel = createLeftPanel();
		addWest(leftPanel, 15);
		getWidgetContainerElement(leftPanel).addClassName("quarterViewMenu");
		
		add(contentPanel = new SimplePanel());
		contentPanel.add(new ScheduleView(contentPanel, service));
	}
	
	static HTML createLink(String label, String styleName, ClickHandler handler) {
		HTML result = createLabel(label, styleName);
		result.addClickHandler(handler);
		return result;
	}
	
	static HTML createLabel(String label, String styleName) {
		HTML result = new HTML(label);
		result.addStyleName(styleName);
		return result;
	}
	
	protected Widget createLeftPanel() {
//		StackPanel leftMenuSP = new StackPanel();
//		leftMenuSP.setStyleName("myStackPanel") ;
//		leftMenuSP.add(new HTML(""),"Home",true);
		
		FlowPanel leftMenuVP = new FlowPanel();
		
		leftMenuVP.add(createLink("<b>Back to Select Quarter</b>", "inAppLink", new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				container.clear();
				container.add(new HomeView(container, service, "gfisher"));
			}
		}));
		
		leftMenuVP.add(new HTML("<b>Schedule</b>"));
		/*leftMenuVP.add(createLink("Build", "inAppLink indented", new ClickHandler() {
			public void onClick(ClickEvent events) {
				contentPanel.clear();
				contentPanel.add(new ScheduleBuildView(contentPanel, service));
			}
		}));*/
		leftMenuVP.add(createLink("Build / View", "inAppLink indented", new ClickHandler() {
			public void onClick(ClickEvent events) {
				contentPanel.clear();
				contentPanel.add(new ScheduleView(contentPanel, service));
			}
		}));
		//leftMenuVP.add(createLabel("Edit", "indented"));
		
		leftMenuVP.add(new HTML("<b>Manage</b>"));
		leftMenuVP.add(createLink("Instructors", "inAppLink indented", new ClickHandler() {
			public void onClick(ClickEvent events) {
				contentPanel.clear();
				contentPanel.add(new InstructorsView(contentPanel, service));
			}
		}));
		leftMenuVP.add(createLink("Locations", "inAppLink indented", new ClickHandler() {
			public void onClick(ClickEvent events) {
				contentPanel.clear();
				contentPanel.add(new RoomsView(contentPanel, service));
			}
		}));
		leftMenuVP.add(createLink("Courses", "inAppLink indented", new ClickHandler() {
			public void onClick(ClickEvent events) {
				contentPanel.clear();
				contentPanel.add(new CoursesView(contentPanel, service));
			}
		}));
//		leftMenuSP.add(ManagePanel, "Manage",true);
				
		return leftMenuVP;
	}
}
