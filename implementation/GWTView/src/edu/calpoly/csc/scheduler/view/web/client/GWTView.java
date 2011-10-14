package edu.calpoly.csc.scheduler.view.web.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.StackPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.calpoly.csc.scheduler.view.web.client.views.CoursesView;
import edu.calpoly.csc.scheduler.view.web.client.views.InstructorsView;
import edu.calpoly.csc.scheduler.view.web.client.views.RoomsView;
import edu.calpoly.csc.scheduler.view.web.client.views.ScheduleView;
import edu.calpoly.csc.scheduler.view.web.client.views.View;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class GWTView implements EntryPoint {
	/**
	 * Create a remote service proxy to talk to the server-side Greeting service.
	 */
	private static final GreetingServiceAsync greetingService = GWT
			.create(GreetingService.class);

	VerticalPanel bodyPanel;
	
	Button instructorsViewButton, roomsViewButton, coursesViewButton, scheduleViewButton;

	InstructorsView instructorsView;
	RoomsView roomsView;
	CoursesView coursesView;
	ScheduleView scheduleView;
	
	View currentView;
	
	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {

		////////////Title Bar Menu
		HorizontalPanel titlePanel = new HorizontalPanel();
		
		titlePanel.addStyleName("titlePanelStyle") ;
		Image logoPlaceHolder = new Image();
	    // Point the image at a real URL.
		logoPlaceHolder.setUrl("imgs/LogoPlaceHolder.png");
		logoPlaceHolder.setStyleName("logoPlaceHolderStyle") ;

		HTML northPanel = new HTML() ;
	 	Image titleImg = new Image() ;
	 	titleImg.setUrl("imgs/titleImg.png") ;
	 	titleImg.setStyleName("titleImgStyle") ;
		
		northPanel.getElement().setId("northPanelID") ;
	 	northPanel.setHTML("<div id='titleText'><img src='imgs/titleImg.png'></div>") ;
	    //p.addNorth(new HTML("<div id='northDiv' style='background-color:#004000; width:100%; height:100%';>north</div>"), 2);
	 	
	 	
		titlePanel.add(logoPlaceHolder) ;
		titlePanel.add(northPanel) ;
		
		titlePanel.setCellWidth(titlePanel.getWidget(0), "100px");
		
		titlePanel.getWidget(0).setStyleName("logoContainerDiv") ;
		
		////////////
		//LEFT MENU LAYOUT
		StackPanel leftMenuSP = new StackPanel();
		leftMenuSP.add(new HTML("Start<BR>Generate<br>Review"),"Generate Schedule",true);
		leftMenuSP.add(new HTML("Export Schedule"), "Export Schedule",true);
		leftMenuSP.add(createLeftPanel(), "Configuration",true);
		leftMenuSP.setStyleName("myStackPanel") ;

		 DockLayoutPanel p = new DockLayoutPanel(Unit.EM);
		 	
		 	p.addNorth(titlePanel, 7);
			  
		 	p.addSouth(new HTML("south"), 2);
		    //p.addEast(new HTML("east"), 2);
		    p.addWest(leftMenuSP, 15);
		    p.getWidgetContainerElement(leftMenuSP).setId("leftPanelID") ;

			bodyPanel = new VerticalPanel();
		    p.add(bodyPanel) ;
		   
		    // Attach the LayoutPanel to the RootLayoutPanel. The latter will listen for
		    // resize events on the window to ensure that its children are informed of
		    // possible size changes.
		    RootLayoutPanel rp = RootLayoutPanel.get();
		    rp.add(p);
		
		instructorsViewButton.click();
	}
	
	private Panel createLeftPanel() {
		instructorsViewButton = new Button("Instructors", new ClickHandler() {
			public void onClick(ClickEvent events) {
				if (instructorsView == null)
					instructorsView = new InstructorsView(greetingService);
				showView(instructorsView);
			}
		});

		roomsViewButton = new Button("Rooms", new ClickHandler() {
			public void onClick(ClickEvent events) {
				if (roomsView == null)
					roomsView = new RoomsView(greetingService);
				showView(roomsView);
			}
		});

		coursesViewButton = new Button("Courses", new ClickHandler() {
			public void onClick(ClickEvent events) {
				if (coursesView == null)
					coursesView = new CoursesView(greetingService);
				showView(coursesView);
			}
		});

		scheduleViewButton = new Button("Schedule", new ClickHandler() {
			public void onClick(ClickEvent events) {
				if (scheduleView == null)
					scheduleView = new ScheduleView(greetingService);
				showView(scheduleView);
			}
		});

		Panel leftPanel = new VerticalPanel();
		leftPanel.add(instructorsViewButton);
		leftPanel.add(roomsViewButton);
		leftPanel.add(coursesViewButton);
		leftPanel.add(scheduleViewButton);
		return leftPanel;
	}
	
	public void showView(View newPage) {
		if (currentView != null) {
			bodyPanel.clear();
			currentView.beforeHide();
			currentView = null;
		}
		
		assert(newPage != null);
		
		currentView = newPage;
		bodyPanel.add(currentView);
		currentView.afterShow();
	}
}
