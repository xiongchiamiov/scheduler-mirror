package scheduler.view.web.client.views;

import scheduler.view.web.client.GreetingServiceAsync;
import scheduler.view.web.client.Login;
import scheduler.view.web.client.TabOpener;
import scheduler.view.web.client.UpdateHeaderStrategy;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasAlignment;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class LoginView extends VerticalPanel {
	GreetingServiceAsync service;
	
	String username;
	final UpdateHeaderStrategy updateHeaderStrategy;
	TextBox textBox;

	public LoginView(GreetingServiceAsync service, UpdateHeaderStrategy updateHeaderStrategy) {
		this.updateHeaderStrategy = updateHeaderStrategy;
		
		this.service = service;

		this.setWidth("100%");
		this.setHorizontalAlignment(HasAlignment.ALIGN_CENTER);
		
		this.add(new HTML("<h2>Login</h2>"));
		DOM.setElementAttribute(this.getElement(), "id", "s_loginTag");

		textBox = new TextBox();
		DOM.setElementAttribute(textBox.getElement(), "id", "s_unameBox");
		
		textBox.addKeyPressHandler(new KeyPressHandler() {
			@Override
			public void onKeyPress(KeyPressEvent event) {
				if (event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ENTER)
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
		
	    DOM.setElementAttribute(login.getElement(), "id", "s_loginBtn");
		this.add(login);
	}
	
	@Override
	protected void onLoad() {
		super.onLoad();
		
		textBox.setFocus(true);
	}

   protected void submitLogin(final String username) {
		if ("".equals(username)) {
			Window.alert("Please enter a username.");
			return;
		}
		
		if (!username.matches("^[A-Za-z0-9_]+$")) {
			Window.alert("A username must contain only letters and numbers.");
			return;
		}
		
		Login.login(service, username, new AsyncCallback<Void>() {
			public void onSuccess(Void result) {
				LoginView.this.username = username;
				pushSelectScheduleView(username);
			}
			public void onFailure(Throwable caught) {
				Window.alert("Failed to log in: " + caught.getMessage());
			}
		});
	}
	
	protected void pushSelectScheduleView(String username) {
		TabOpener.openHomeInThisTab(username);
	}
}
