package edu.calpoly.csc.scheduler.view.web.client.views;

import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.SimplePanel;

import edu.calpoly.csc.scheduler.view.web.client.GreetingServiceAsync;
import edu.calpoly.csc.scheduler.view.web.client.ViewFrame;

public class LoginViewAutomatic extends LoginView {
	final String automaticLoginUsername;
	final int automaticOpenOriginalDocumentID;
	final SimplePanel scheduleNameContainer;
	
	public LoginViewAutomatic(GreetingServiceAsync service, SimplePanel usernameContainer,
			SimplePanel logoutLinkContainer, SimplePanel scheduleNameContainer, MenuBar menuBar, String automaticLoginUsername, int automaticOpenOriginalDocumentID) {
		super(service, usernameContainer, logoutLinkContainer, scheduleNameContainer, menuBar);
		this.automaticLoginUsername = automaticLoginUsername;
		this.automaticOpenOriginalDocumentID = automaticOpenOriginalDocumentID;
		this.scheduleNameContainer = scheduleNameContainer;
	}
	
	public LoginViewAutomatic(GreetingServiceAsync service, SimplePanel usernameContainer,
         SimplePanel logoutLinkContainer, SimplePanel scheduleNameContainer, MenuBar menuBar,
         String automaticLoginUsername)
   {
	   super(service, usernameContainer, logoutLinkContainer, scheduleNameContainer, menuBar);
	   this.automaticLoginUsername = automaticLoginUsername;
	   this.automaticOpenOriginalDocumentID = Integer.MIN_VALUE;
	   this.scheduleNameContainer = scheduleNameContainer;
   }

   @Override
	public void afterPush(ViewFrame frame) {
		super.afterPush(frame);
		
		submitLogin(automaticLoginUsername);
	}
	
	@Override
	protected void pushSelectScheduleView(String username) {
		assert(username.equals(automaticLoginUsername));
		
		assert(myFrame.canPopViewsAboveMe());
		myFrame.popFramesAboveMe();
		if(automaticOpenOriginalDocumentID == Integer.MIN_VALUE)
		{
		   myFrame.frameViewAndPushAboveMe(new SelectScheduleView(service, scheduleNameContainer, menuBar, username));
		}
		else
		{
		   myFrame.frameViewAndPushAboveMe(new SelectScheduleViewAutomatic(service, scheduleNameContainer, menuBar, username, automaticOpenOriginalDocumentID));
		}
	}
	
}
