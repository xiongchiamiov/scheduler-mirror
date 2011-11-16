package edu.calpoly.csc.scheduler.view.web.client.views;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.calpoly.csc.scheduler.view.web.client.GreetingServiceAsync;

public class MainView extends VerticalPanel {
	GreetingServiceAsync service;
	Panel contents;
	String userName = "null";
	IView<MainView> currentView;
	
	public MainView(GreetingServiceAsync service) {
		this.service = service;
	}
	
	@Override
	protected void onLoad() {
		super.onLoad();
		
		addStyleName("mainWidget");
		
		SimplePanel topPanel = new SimplePanel();
		
		String HTMLtoAdd = "<div id='topBar'><img src='imgs/cp_logo.gif' alt='Cal Poly' title='Go to Cal Poly Home' width='166' height='60'><img src='imgs/TheSchedProjText.png' alt='TheSchedulerProject' height='60'>" ;
		//Figure out a way to add this code after the user has logged on
		//if(!userName.equals("null"))
		//{
		//  HTMLtoAdd.concat("<div id='loginStatus'><div id='loginName' style='height:20px; width:50px;margin-top:20px;margin-left:15px;margin-right:20px;color:white;padding-right:6px;'>"+userName+"</div><div id='logoutOption' style='height:50%; width:50px;margin-left:10px;margin-right:20px;text-align:center;'><a href='#' style='color:white;'>logout</a> </div>") ;
		//}
		HTMLtoAdd.concat("</div>") ;
		
		topPanel.add(new HTML(HTMLtoAdd));
	
		add(topPanel);
		
		add(contents = new SimplePanel());
		contents.add(new LoginView(this, service));
		
	}
	
	public void onUserLoggedIn(String newUsername) {
		this.userName = newUsername;
	}

	public boolean canCloseCurrentView() {
		return currentView == null || currentView.canCloseView();
	}

	public void switchToView(IView<MainView> newView) {
		assert(canCloseCurrentView());
		currentView = newView;
		currentView.willOpenView(this);
		contents.clear();
		contents.add(newView.getViewWidget());
	}
}
