package edu.calpoly.csc.scheduler.view.web.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.calpoly.csc.scheduler.view.web.client.views.LoginView;

public class GWTView implements EntryPoint {
	private static final GreetingServiceAsync service = GWT.create(GreetingService.class);

	public void onModuleLoad() {
		VerticalPanel pagePanel = new VerticalPanel();
		pagePanel.addStyleName("mainWidget");
		
		pagePanel.add(new HTML("<div id='topBar'><img src='imgs/cp_logo.gif' alt='Cal Poly' title='Go to Cal Poly Home' width='166' height='60'><img src='imgs/TheSchedProjText.png' alt='TheSchedulerProject' height='60'></div>"));
		
		MenuBar menuBar = new MenuBar();
		pagePanel.add(menuBar);

		ViewFrame newViewFrame = new ViewFrame(new LoginView(service, menuBar));
		pagePanel.add(newViewFrame);
		newViewFrame.afterPush();
		
		RootPanel.get().add(pagePanel);
	}
}
