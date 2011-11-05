package edu.calpoly.csc.scheduler.view.web.client.views;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.SimplePanel;

import edu.calpoly.csc.scheduler.view.web.client.GreetingServiceAsync;
import edu.calpoly.csc.scheduler.view.web.client.HTMLUtilities;

public class MainView extends DockLayoutPanel {
	GreetingServiceAsync service;
	Panel contents;
	String userName = "null" ;
	
	public MainView(GreetingServiceAsync service) {
		super(Unit.EM);
		
		this.service = service;
	}
	
	@Override
	protected void onLoad() {
		super.onLoad();
		
		addStyleName("mainWidget");
		
		SimplePanel topPanel = new SimplePanel();
		
		String HTMLtoAdd = "<div id='topBar'><img src='imgs/cp_logo.gif' alt='Cal Poly' title='Go to Cal Poly Home' width='166' height='60'>					<img src='imgs/TheSchedProjText.png' alt='TheSchedulerProject' height='60'>" ;
		//Figure out a way to add this code after the user has logged on
		//if(!userName.equals("null"))
		//{
		//  HTMLtoAdd.concat("<div id='loginStatus'><div id='loginName' style='height:20px; width:50px;margin-top:20px;margin-left:15px;margin-right:20px;color:white;padding-right:6px;'>"+userName+"</div><div id='logoutOption' style='height:50%; width:50px;margin-left:10px;margin-right:20px;text-align:center;'><a href='#' style='color:white;'>logout</a> </div>") ;
		//}
		HTMLtoAdd.concat("</div>") ;
		
		topPanel.add(new HTML(HTMLtoAdd));
		
	
		//topPanel.add(new HTML("The Scheduler Project"));
		addNorth(topPanel,6);
		
		add(contents = new SimplePanel());
		contents.add(new LoginView(this, contents, service));
	}
	
	public void onUserLoggedIn(String newUsername) {
		this.userName = newUsername ;
		//System.out.println("Hilton, put code here");
	}
}
