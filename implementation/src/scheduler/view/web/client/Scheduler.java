package scheduler.view.web.client;

import java.util.Map;
import java.util.TreeMap;

import scheduler.view.web.client.views.LoginView;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
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
			appNameContainer.setWidget(lbl);
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
		
		viewContainer.clear();
		
		Map<String, String> urlArguments = new TreeMap<String, String>();
		
		if (parseURLArguments)
			urlArguments = URLUtilities.parseURLArguments(Window.Location.getHref());
		
		final String username = urlArguments.get("userid");
		final String documentIDStr = urlArguments.get("originaldocumentid");
		
//		if (username == null) {
//			assert(documentIDStr == null);
			viewContainer.setWidget(new LoginView(service, this));
//			loadingPopup.hide();
//		}
//		else {
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
		scheduleNameContainer.setWidget(new Label(documentName));
		
		appNameContainer.clear();
		appNameContainer.setWidget(new Label(" - The Scheduler Project"));

		refreshWindowTitle();
	}

	public void onLogin(String username, final LogoutHandler logoutHandler) {
		Label uname = new Label(username);
		DOM.setElementAttribute(uname.getElement(), "id", "s_unameLbl");
		usernameContainer.setWidget(uname);
		
		logoutLinkContainer.setWidget(HTMLUtilities.createLink("Log Out", "inAppLink", new ClickHandler() {
			public void onClick(ClickEvent event) {
				logoutHandler.handleLogout();
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
		scheduleNameContainer.setWidget(new Label(newDocumentName));

		refreshWindowTitle();
	}

	@Override
	public void setDocumentChanged(boolean documentChanged) {
		documentChangedIndicatorContainer.clear();
		
		if (documentChanged) {
			Label label = new Label("*");
			label.setTitle("This document has been changed, and hasn't been saved yet.");
			documentChangedIndicatorContainer.setWidget(label);
		}
		
		refreshWindowTitle();
	}
	
	private void refreshWindowTitle() {
		Window.setTitle(scheduleNameContainer.getElement().getInnerText() + documentChangedIndicatorContainer.getElement().getInnerText() + this.appNameContainer.getElement().getInnerText());
	}
}
