package scheduler.view.web.client;

import java.util.Map;
import java.util.TreeMap;

import scheduler.view.web.client.views.AdminScheduleNavView;
import scheduler.view.web.client.views.LoadingPopup;
import scheduler.view.web.client.views.LoginView;
import scheduler.view.web.client.views.home.HomeView;
import scheduler.view.web.client.views.resources.instructors.InstructorsHomeView;
import scheduler.view.web.shared.DocumentGWT;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class Scheduler implements EntryPoint, UpdateHeaderStrategy
{
	private static final GreetingServiceAsync service = GWT.create(GreetingService.class);
	
	SimplePanel scheduleNameContainer;
	SimplePanel documentChangedIndicatorContainer;
	SimplePanel appNameContainer;
	SimplePanel usernameContainer;
	SimplePanel logoutLinkContainer;
	SimplePanel viewContainer;
	
	public void onModuleLoad() {
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
			
			documentChangedIndicatorContainer = new SimplePanel();
			documentChangedIndicatorContainer.setStyleName("documentChangedIndicator");
			topBarLeftSide.add(documentChangedIndicatorContainer);
			
			//need for selenium
			Label lbl = new Label("The Scheduler Project");
			appNameContainer = new SimplePanel();
			appNameContainer.setStyleName("appName");
			appNameContainer.add(lbl);
			topBarLeftSide.add(appNameContainer);
			DOM.setElementAttribute(lbl.getElement(), "id", "appNameTtl");
			
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

		refreshWindowTitle();
		
		openInitialView(true, viewContainer);
	}
	
	private void openInitialView(
			boolean parseURLArguments,
			final SimplePanel viewContainer) {
		
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
			viewContainer.add(new LoginView(service, this));
			loadingPopup.hide();
		}
		else {
			onLogin(username);
			
			if(username.equals("jjuszak"))
			{
				viewContainer.add(new InstructorsHomeView(service, username));
				loadingPopup.hide();
				return;
			}
			
			if (documentIDStr == null) {
				viewContainer.add(new HomeView(service, viewContainer, username));
				loadingPopup.hide();
			}
			else {
				final int originalDocumentID = Integer.parseInt(documentIDStr);
				
				service.createWorkingCopyForOriginalDocument(originalDocumentID, new AsyncCallback<DocumentGWT>() {
					public void onSuccess(DocumentGWT workingCopyDocument) {
						onOpenedDocument(workingCopyDocument.getName());
						
						viewContainer.add(new AdminScheduleNavView(service, Scheduler.this, username, workingCopyDocument));
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
		appNameContainer.add(new Label(" - The Scheduler Project"));

		refreshWindowTitle();
	}
	
	public void onLogin(String username) {
		Label uname = new Label(username);
		DOM.setElementAttribute(uname.getElement(), "id", "s_unameLbl");
		usernameContainer.add(uname);
		
		logoutLinkContainer.add(HTMLUtilities.createLink("Log Out", "inAppLink", new ClickHandler() {
			public void onClick(ClickEvent event) {
				TabOpener.openLoginInThisTab();
			}
		}));

		refreshWindowTitle();
	    DOM.setElementAttribute(logoutLinkContainer.getWidget().getElement(), "id", "s_logoutLnk");
	}

	@Override
	public void clearHeader() {
		scheduleNameContainer.clear();
		usernameContainer.clear();
		logoutLinkContainer.clear();
		documentChangedIndicatorContainer.clear();
		appNameContainer.clear();

		refreshWindowTitle();
	}

	@Override
	public void onDocumentNameChanged(String newDocumentName) {
		scheduleNameContainer.clear();
		scheduleNameContainer.add(new Label(newDocumentName));

		refreshWindowTitle();
	}

	@Override
	public void setDocumentChanged(boolean documentChanged) {
		documentChangedIndicatorContainer.clear();
		
		if (documentChanged) {
			Label label = new Label("*");
			label.setTitle("This document has been changed, and hasn't been saved yet.");
			documentChangedIndicatorContainer.add(label);
		}
		
		refreshWindowTitle();
	}
	
	private void refreshWindowTitle() {
		Window.setTitle(scheduleNameContainer.getElement().getInnerText() + documentChangedIndicatorContainer.getElement().getInnerText() + this.appNameContainer.getElement().getInnerText());
	}
}
