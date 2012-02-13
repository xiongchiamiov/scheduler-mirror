package edu.calpoly.csc.scheduler.view.web.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.calpoly.csc.scheduler.view.web.client.views.LoginView;
import edu.calpoly.csc.scheduler.view.web.client.views.LoginViewAutomatic;

public class GWTView implements EntryPoint {
	private static final GreetingServiceAsync service = GWT.create(GreetingService.class);

	MenuBar menuBar;
	
	public void onModuleLoad() {
		final VerticalPanel pagePanel = new VerticalPanel();
		pagePanel.addStyleName("mainWidget");
		
		FlowPanel topBar = new FlowPanel();
		topBar.addStyleName("topBar");
		
			VerticalPanel usernameAndLogout = new VerticalPanel();
				usernameAndLogout.addStyleName("usernameAndLogout");
				final SimplePanel usernameContainer = new SimplePanel();
				usernameAndLogout.add(usernameContainer);
				final SimplePanel logoutLinkContainer = new SimplePanel();
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
		
		menuBar = new MenuBar();
		pagePanel.add(menuBar);

//		   service.openExistingSchedule(scheduleid, new AsyncCallback<Void>() {
//	         @Override
//	         public void onFailure(Throwable caught) {
//	            System.out.println("selectSchedule onFailure");
//	            
//	            // This is a workaround, see http://code.google.com/p/google-web-toolkit/issues/detail?id=2858
//	            if (caught instanceof StatusCodeException && ((StatusCodeException)caught).getStatusCode() == 0) {
//	               // Do nothing
//	            }
//	            else {
//	               Window.alert("Failed to open schedule in: " + caught.getMessage());
//	            }
//	         }
//	         @Override
//	         public void onSuccess(Void v) {
//	            System.out.println("selectSchedule onSuccess");
//
////	            Integer permissionLevel = permissionAndInstructor.getLeft();
////	            InstructorGWT instructor = permissionAndInstructor.getRight();
////	            ViewFrame newViewFrame = null;
////	                  newViewFrame = new ViewFrame(new AdminScheduleNavView(service, menuBar, username, scheduleid, schedulename));
////	               pagePanel.add(newViewFrame);
////	               newViewFrame.afterPush();
//	            // TODO: use alternate constructors n stuff
//	            }
//		   });
//		}
//		else
//		{

		ViewFrame newViewFrame = null;
		if (Window.Location.getHref().contains("?userid=")) {
		   String query = Window.Location.getHref();
		   query = query.substring(query.lastIndexOf('?'));
		   String automaticLoginUsername = query.split("=")[1];
		   newViewFrame = new ViewFrame(new LoginViewAutomatic(service, usernameContainer, logoutLinkContainer, menuBar, automaticLoginUsername));
		}
		else if (Window.Location.getHref().contains("?scheduleid=")) {
		   String query = Window.Location.getHref();
		   query = query.substring(query.lastIndexOf('?'));
		   String[] params = query.split("&");
		   String automaticLoginUsername = params[2].split("=")[1];
		   int automaticOpenDocumentID = Integer.parseInt(params[0].split("=")[1]);
		   newViewFrame = new ViewFrame(new LoginViewAutomatic(service, usernameContainer, logoutLinkContainer, menuBar, automaticLoginUsername, automaticOpenDocumentID));
		}
		else {
		   newViewFrame = new ViewFrame(new LoginView(service, usernameContainer, logoutLinkContainer, menuBar));
		}

   		pagePanel.add(newViewFrame);
   		newViewFrame.afterPush();
		
		RootPanel.get().add(pagePanel);
	}
}
