package scheduler.view.web.client;

import java.util.Map;
import java.util.TreeMap;

import scheduler.view.web.client.views.AdminScheduleNavView;
import scheduler.view.web.client.views.LoadingPopup;
import scheduler.view.web.client.views.LoginView;
import scheduler.view.web.client.views.SelectScheduleView;
import scheduler.view.web.shared.DocumentGWT;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class Scheduler implements EntryPoint, UpdateHeaderStrategy
{
	private static final GreetingServiceAsync service = GWT.create(GreetingService.class);
	
	SimplePanel scheduleNameContainer;
	SimplePanel appNameContainer;
	SimplePanel usernameContainer;
	SimplePanel logoutLinkContainer;
	SimplePanel viewContainer;
	
	public void onModuleLoad()
	{
		VerticalPanel pagePanel = new VerticalPanel();
		pagePanel.addStyleName("mainWidget");
		
			HorizontalPanel topBar = new HorizontalPanel();
			topBar.addStyleName("topBar");
			
			FlowPanel topBarLeftSide = new FlowPanel();
			topBarLeftSide.addStyleName("topBarLeftSide");
			topBar.add(topBarLeftSide);
			
			scheduleNameContainer = new SimplePanel();
			scheduleNameContainer.setStyleName("scheduleName");
			topBarLeftSide.add(scheduleNameContainer);
			
			appNameContainer = new SimplePanel();
			appNameContainer.setStyleName("appName");
			appNameContainer.add(new Label("Schedulizerifier"));
			topBarLeftSide.add(appNameContainer);
			
			VerticalPanel usernameAndLogout = new VerticalPanel();
			usernameAndLogout.addStyleName("usernameAndLogout");
			
			usernameContainer = new SimplePanel();
			usernameAndLogout.add(usernameContainer);
			
			logoutLinkContainer = new SimplePanel();
			usernameAndLogout.add(logoutLinkContainer);
			topBar.add(usernameAndLogout);
		
		pagePanel.add(topBar);
		
		viewContainer = new SimplePanel();
		pagePanel.add(viewContainer);

		RootPanel.get().add(pagePanel);
		
		openInitialView(true, viewContainer);
	}
	
	private void openInitialView(
			boolean parseURLArguments,
			final Panel viewContainer) {
		
		final LoadingPopup loadingPopup = new LoadingPopup();
		loadingPopup.show();
		
		viewContainer.clear();
		
		Map<String, String> urlArguments = new TreeMap<String, String>();
		
		if (parseURLArguments)
			urlArguments = URLUtilities.parseURLArguments(Window.Location.getHref());
		
		final String username = urlArguments.get("userid");
		String documentIDStr = urlArguments.get("originaldocumentid");
		
		if (username == null) {
			assert(documentIDStr == null);
			ViewFrame newViewFrame = new ViewFrame(new LoginView(service, this));
			viewContainer.add(newViewFrame);
			newViewFrame.afterPush();
			loadingPopup.hide();
		}
		else {
			onLogin(username);
			
			if (documentIDStr == null) {
				ViewFrame newViewFrame = new ViewFrame(new SelectScheduleView(service, username));
				viewContainer.add(newViewFrame);
				newViewFrame.afterPush();
				loadingPopup.hide();
			}
			else {
				final int originalDocumentID = Integer.parseInt(documentIDStr);
				
				service.createWorkingCopyForOriginalDocument(originalDocumentID, new AsyncCallback<DocumentGWT>() {
					public void onSuccess(DocumentGWT workingCopyDocument) {
						onOpenedDocument(workingCopyDocument.getName());
						
						ViewFrame newViewFrame = new ViewFrame(new AdminScheduleNavView(service, username, workingCopyDocument));
						viewContainer.add(newViewFrame);
						newViewFrame.afterPush();
						loadingPopup.hide();
					}
					public void onFailure(Throwable caught) {
						Window.alert("Failed to get document!");
						loadingPopup.hide();
					}
				});
			}
		}
	}

//	static String parseURLArgument(String url, String parameter) {
//		RegExp regExp = RegExp.compile("[\\?|&].*" + parameter + "=(\\w+)");
//		if (!regExp.test(url))
//			return null;
//		
//		MatchResult matcher = regExp.exec(url);
//		assert (matcher.getGroupCount() == 2);
//		
//		return matcher.getGroup(1);
//	}

	public void onOpenedDocument(String documentName) {
		scheduleNameContainer.add(new Label(documentName));
		
		appNameContainer.clear();
		appNameContainer.add(new Label("- Schedulizerifier"));
	}
	
	public void onLogin(String username) {
		usernameContainer.add(new Label(username));
		logoutLinkContainer.add(HTMLUtilities.createLink("Log Out", "inAppLink", new ClickHandler() {
			public void onClick(ClickEvent event) {
				viewContainer.clear();
				openInitialView(false, viewContainer);
			}
		}));
	}

	@Override
	public void clearHeader() {
		scheduleNameContainer.clear();
		usernameContainer.clear();
		logoutLinkContainer.clear();
	}
}
