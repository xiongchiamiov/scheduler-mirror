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
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import edu.calpoly.csc.scheduler.view.web.client.GreetingServiceAsync;
import edu.calpoly.csc.scheduler.view.web.client.HTMLUtilities;
import edu.calpoly.csc.scheduler.view.web.client.IViewContents;
import edu.calpoly.csc.scheduler.view.web.client.ViewFrame;

public class LoginView extends VerticalPanel implements IViewContents {
	GreetingServiceAsync service;
	final MenuBar menuBar;
	
	String username;
	ViewFrame myFrame;
	final SimplePanel usernameContainer, logoutLinkContainer;

	public LoginView(GreetingServiceAsync service, SimplePanel usernameContainer, SimplePanel logoutLinkContainer, MenuBar menuBar) {
		this.usernameContainer = usernameContainer;
		this.logoutLinkContainer = logoutLinkContainer;
		
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
				loggedIn(username);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				popup.hide();
				Window.alert("Failed to log in: " + caught.getMessage());
			}
		});
	}
	
	private void loggedIn(String username) {
		this.username = username;
		
		assert(myFrame.canPopViewsAboveMe());
		
		myFrame.popFramesAboveMe();
		myFrame.frameViewAndPushAboveMe(new SelectScheduleView(service, menuBar, username));
	}
	
	@Override
	public void beforeViewPushedAboveMe() {
		this.usernameContainer.add(new HTML(username));
		
		this.logoutLinkContainer.add(HTMLUtilities.createLink("logout", "inAppLink", new ClickHandler() {
			public void onClick(ClickEvent event) {
				if (myFrame.canPopViewsAboveMe()) {
					myFrame.popFramesAboveMe();
					logout();
				}
			}
		}));
	}
	
	@Override
	public void afterViewPoppedFromAboveMe() {
		this.usernameContainer.clear();
		this.logoutLinkContainer.clear();
	}
	
	private void logout() {
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
