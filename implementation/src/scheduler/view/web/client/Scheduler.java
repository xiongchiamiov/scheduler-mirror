package scheduler.view.web.client;

import scheduler.view.web.client.views.AdminScheduleNavView;
import scheduler.view.web.client.views.LoadingPopup;
import scheduler.view.web.client.views.LoginView;
import scheduler.view.web.shared.DocumentGWT;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class Scheduler implements EntryPoint
{
	private static final GreetingServiceAsync service = GWT.create(GreetingService.class);
	
	public void onModuleLoad()
	{
		VerticalPanel pagePanel = new VerticalPanel();
		pagePanel.addStyleName("mainWidget");
		
		HorizontalPanel topBar = new HorizontalPanel();
		topBar.addStyleName("topBar");
		
		SimplePanel scheduleNameContainer = new SimplePanel();
		scheduleNameContainer.setStyleName("scheduleName");
		topBar.add(scheduleNameContainer);
		
		VerticalPanel usernameAndLogout = new VerticalPanel();
		usernameAndLogout.addStyleName("usernameAndLogout");
		
		SimplePanel usernameContainer = new SimplePanel();
		usernameAndLogout.add(usernameContainer);
		
		SimplePanel logoutLinkContainer = new SimplePanel();
		usernameAndLogout.add(logoutLinkContainer);
		topBar.add(usernameAndLogout);
		
		pagePanel.add(topBar);
		
		MenuBar menuBar = new MenuBar();
		pagePanel.add(menuBar);
		
		SimplePanel viewContainer = new SimplePanel();
		pagePanel.add(viewContainer);
		
		RootPanel.get().add(pagePanel);
		
		openInitialView(true, scheduleNameContainer, viewContainer, usernameContainer, logoutLinkContainer, menuBar);
	}
	
	private void openInitialView(
			boolean parseURLArguments,
			final SimplePanel scheduleNameContainer,
			final SimplePanel viewContainer,
			final SimplePanel usernameContainer,
			final SimplePanel logoutLinkContainer,
			final MenuBar menuBar) {
		
		final LoadingPopup loadingPopup = new LoadingPopup();
		loadingPopup.show();
		
		scheduleNameContainer.clear();
		viewContainer.clear();
		usernameContainer.clear();
		logoutLinkContainer.clear();
		menuBar.clearItems();
		
		final String username = parseURLArguments ? parseURLArgument(Window.Location.getHref(), "userid") : null;
		String documentIDStr = parseURLArguments ? parseURLArgument(Window.Location.getHref(), "originaldocumentid") : null;
		assert ((username == null) == (documentIDStr == null));
		
		if (username == null || documentIDStr == null) {
			ViewFrame newViewFrame = new ViewFrame(new LoginView(service, usernameContainer, logoutLinkContainer,
					scheduleNameContainer, menuBar));
			viewContainer.add(newViewFrame);
			newViewFrame.afterPush();
			loadingPopup.hide();
			return;
		}
		
		final int originalDocumentID = Integer.parseInt(documentIDStr);
		
		Login.login(service, username, new AsyncCallback<Void>() {
			public void onSuccess(Void result) {
				
				usernameContainer.add(new Label(username));
				logoutLinkContainer.add(HTMLUtilities.createLink("Log Out", "inAppLink", new ClickHandler() {
					public void onClick(ClickEvent event) {
						viewContainer.clear();
						openInitialView(false, scheduleNameContainer, viewContainer, usernameContainer, logoutLinkContainer, menuBar);
					}
				}));
				
				service.createWorkingCopyForOriginalDocument(originalDocumentID, new AsyncCallback<DocumentGWT>() {
					public void onSuccess(DocumentGWT workingCopyDocument) {
						HTML documentNameHTML = new HTML("<h2>" + workingCopyDocument.getName() + "<h2>");
						scheduleNameContainer.add(documentNameHTML);
						
						ViewFrame newViewFrame = new ViewFrame(new AdminScheduleNavView(service, menuBar, username,
								workingCopyDocument));
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
			
			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Failed to log in as username " + username);
				loadingPopup.hide();
			}
		});
	}

	static String parseURLArgument(String url, String parameter) {
		RegExp regExp = RegExp.compile("[\\?|&].*" + parameter + "=(\\w+)");
		if (!regExp.test(url))
			return null;
		
		MatchResult matcher = regExp.exec(url);
		assert (matcher.getGroupCount() == 2);
		
		return matcher.getGroup(1);
	}
}
