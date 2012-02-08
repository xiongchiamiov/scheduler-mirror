package edu.calpoly.csc.scheduler.view.web.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.StatusCodeException;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.calpoly.csc.scheduler.view.web.client.views.AdminScheduleNavView;
import edu.calpoly.csc.scheduler.view.web.client.views.GuestScheduleNavView;
import edu.calpoly.csc.scheduler.view.web.client.views.InstructorScheduleNavView;
import edu.calpoly.csc.scheduler.view.web.client.views.LoginView;
import edu.calpoly.csc.scheduler.view.web.shared.InstructorGWT;
import edu.calpoly.csc.scheduler.view.web.shared.Pair;

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

		//See if we need to open straight to a schedule
		if(Window.Location.getHref().contains("?scheduleid="))
		{
		   String query = Window.Location.getHref();
		   query = query.substring(query.lastIndexOf('?'));
		   String[] params = query.split("&");
		   final Integer scheduleid = Integer.parseInt(params[0].split("=")[1]);
		   final String schedulename = params[1].split("=")[1];
		   final String username = params[2].split("=")[1];
		   System.out.println("id: " + scheduleid + " sname: " + schedulename + " username: " + username);
		   
		   service.openExistingSchedule(scheduleid, new AsyncCallback<Pair<Integer, InstructorGWT>>() {
	         @Override
	         public void onFailure(Throwable caught) {
	            System.out.println("selectSchedule onFailure");
	            
	            // This is a workaround, see http://code.google.com/p/google-web-toolkit/issues/detail?id=2858
	            if (caught instanceof StatusCodeException && ((StatusCodeException)caught).getStatusCode() == 0) {
	               // Do nothing
	            }
	            else {
	               Window.alert("Failed to open schedule in: " + caught.getMessage());
	            }
	         }
	         @Override
	         public void onSuccess(Pair<Integer, InstructorGWT> permissionAndInstructor) {
	            System.out.println("selectSchedule onSuccess");

	            Integer permissionLevel = permissionAndInstructor.getLeft();
	            InstructorGWT instructor = permissionAndInstructor.getRight();
	            ViewFrame newViewFrame = null;
               switch (permissionLevel) {
	               case 0: // todo: enumify
	                  newViewFrame = new ViewFrame(new GuestScheduleNavView(service, menuBar, schedulename));
	                  break;
	               case 1: // todo: enumify
	                  newViewFrame = new ViewFrame(new InstructorScheduleNavView(service, menuBar, schedulename, instructor));
	                  break;
	               case 2: // todo: enumify
	                  newViewFrame = new ViewFrame(new AdminScheduleNavView(service, menuBar, username, scheduleid, schedulename));
	                  break;
	               default:
	                  assert(false);
	               }
	               pagePanel.add(newViewFrame);
	               newViewFrame.afterPush();
	            }
		   });
		}
		else
		{
   		ViewFrame newViewFrame = new ViewFrame(new LoginView(service, usernameContainer, logoutLinkContainer, menuBar));
   		pagePanel.add(newViewFrame);
   		newViewFrame.afterPush();
		}
		
		RootPanel.get().add(pagePanel);
	}
}
