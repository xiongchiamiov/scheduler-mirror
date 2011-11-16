package edu.calpoly.csc.scheduler.view.web.client.views;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasAlignment;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.calpoly.csc.scheduler.view.web.client.GreetingServiceAsync;

public class LoginView extends VerticalPanel {
	MainView mainView; // Kept around so we can tell it to change the username
	
	Panel container;
	GreetingServiceAsync service;
	
	LoginView(MainView mainView, GreetingServiceAsync service) {
		this.mainView = mainView;
		this.service = service;
	}
	
	@Override
	public void onLoad() {
		super.onLoad();

		SimplePanel fakeTopPanel = new SimplePanel();
		fakeTopPanel.setWidth("100%");
		fakeTopPanel.addStyleName("topBarMenu");
		add(fakeTopPanel);
		
		setWidth("100%");
		setHorizontalAlignment(HasAlignment.ALIGN_CENTER);
		
		add(new HTML("<h2>Login</h2>"));

		final TextBox textBox = new TextBox();
		textBox.addKeyPressHandler(new KeyPressHandler() {
			@Override
			public void onKeyPress(KeyPressEvent event) {
				if (event.getCharCode() == KeyCodes.KEY_ENTER)
					login(textBox.getText());
			}
		});
		add(textBox);
		
		add(new Button("Login", new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				login(textBox.getText());
			}
		}));
	}
	
	private void login(final String username) {
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
				mainView.onUserLoggedIn(username);
				
				popup.hide();
				if (mainView.canCloseCurrentView())
					mainView.switchToView(new SelectScheduleView(mainView, service));
			}
			
			@Override
			public void onFailure(Throwable caught) {
				popup.hide();
				Window.alert("Failed to log in: " + caught.getMessage());
			}
		});
	}
}
