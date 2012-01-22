package edu.calpoly.csc.scheduler.view.web.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.calpoly.csc.scheduler.view.web.client.views.LoginView;

public class GWTView implements EntryPoint {
	private static final GreetingServiceAsync service = GWT.create(GreetingService.class);

	public void onModuleLoad() {
		VerticalPanel pagePanel = new VerticalPanel();
		pagePanel.addStyleName("mainWidget");
		
		FlowPanel topBar = new FlowPanel();
		topBar.addStyleName("topBar");
		
			VerticalPanel usernameAndLogout = new VerticalPanel();
				usernameAndLogout.addStyleName("usernameAndLogout");
				SimplePanel usernameContainer = new SimplePanel();
				usernameAndLogout.add(usernameContainer);
				SimplePanel logoutLinkContainer = new SimplePanel();
				usernameAndLogout.add(logoutLinkContainer);
			topBar.add(usernameAndLogout);
			
			Image logo = new Image("imgs/cp_logo.gif");
				logo.setAltText("Cal Poly Scheduler Project");
				// needs to be width 166 height 60
			topBar.add(logo);
			
			Image titleImage = new Image("imgs/TheSchedProjText.png");
				titleImage.setAltText("TheSchedulerProject");
				// needs to be height 60
			topBar.add(titleImage);
		
		pagePanel.add(topBar);
		
		MenuBar menuBar = new MenuBar();
		pagePanel.add(menuBar);

		ViewFrame newViewFrame = new ViewFrame(new LoginView(service, usernameContainer, logoutLinkContainer, menuBar));
		pagePanel.add(newViewFrame);
		newViewFrame.afterPush();
		
		RootPanel.get().add(pagePanel);
	}
}
