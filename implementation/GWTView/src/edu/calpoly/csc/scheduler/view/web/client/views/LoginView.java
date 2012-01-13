package edu.calpoly.csc.scheduler.view.web.client.views;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasAlignment;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import edu.calpoly.csc.scheduler.view.web.client.GreetingServiceAsync;
import edu.calpoly.csc.scheduler.view.web.client.IViewContents;
import edu.calpoly.csc.scheduler.view.web.client.ViewFrame;

public class LoginView extends VerticalPanel implements IViewContents {
	GreetingServiceAsync service;
	final MenuBar menuBar;
	
	Integer userID;
	String username;
	ViewFrame myFrame;
	MenuItem logoutMenuItem;

	public LoginView(GreetingServiceAsync service, MenuBar menuBar) {
		this.service = service;
		this.menuBar = menuBar;
		
		DOM.setElementAttribute(menuBar.getElement(), "id", "menu");

		this.setWidth("100%");
		this.setHorizontalAlignment(HasAlignment.ALIGN_CENTER);
		
		this.add(new HTML("<h2>Login</h2>"));

		final TextBox textBox = new TextBox();
		DOM.setElementAttribute(textBox.getElement(), "id", "uname");
		
		textBox.addKeyPressHandler(new KeyPressHandler() {
			@Override
			public void onKeyPress(KeyPressEvent event) {
				if (event.getCharCode() == KeyCodes.KEY_ENTER)
					submitLogin(textBox.getText());
			}
		});
		this.add(textBox);
		
		Button login = new Button("Login", new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				submitLogin(textBox.getText());
			}
		});
		
	    DOM.setElementAttribute(login.getElement(), "id", "login");
		this.add(login);	
	}

	private void submitLogin(final String username) {
		if ("".equals(username)) {
			Window.alert("Please enter a username.");
			return;
		}
		
		if (!username.matches("^[A-Za-z0-9_]+$")) {
			Window.alert("A username must contain only letters and numbers.");
			return;
		}

		final LoadingPopup popup = new LoadingPopup();
		popup.show();
		
		service.login(username, new AsyncCallback<Void>() {
			@Override
			public void onSuccess(Void derp) {
				popup.hide();
				loggedIn(-3, username);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				popup.hide();
				Window.alert("Failed to log in: " + caught.getMessage());
			}
		});
	}
	
	private void loggedIn(int userID, String username) {
		this.userID = userID;
		this.username = username;
		
		assert(myFrame.canPopViewsAboveMe());
		
		myFrame.popFramesAboveMe();
		myFrame.frameViewAndPushAboveMe(new SelectScheduleView(service, menuBar, userID, username));
	}
	
	@Override
	public void beforeViewPushedAboveMe() {
		assert(logoutMenuItem == null);
//		logoutNavButton = new FocusPanel();
//		logoutNavButton.addStyleName("topBarLink");
//		logoutNavButton.add(new HTML("Log Out (" + username + ")"));
		
		logoutMenuItem = new MenuItem("Log Out (" + username + ")", true, new Command() {
			public void execute() {
				if (myFrame.canPopViewsAboveMe()) {
					myFrame.popFramesAboveMe();
					logout();
				}
			}
		});
		
		DOM.setElementAttribute(logoutMenuItem.getElement(), "id", "logout");
		menuBar.addItem(logoutMenuItem);
	}
	
	@Override
	public void afterViewPoppedFromAboveMe() {
		assert(logoutMenuItem != null);
		
		menuBar.removeItem(logoutMenuItem);
		
		logoutMenuItem = null;
	}
	
	private void logout() {
		userID = 0;
		username = null;
	}

	@Override
	public boolean canPop() { return true; }
	@Override
	public void afterPush(ViewFrame frame) { myFrame = frame; }
	@Override
	public void beforePop() { }
	@Override
	public Widget getContents() { return this; }
}
