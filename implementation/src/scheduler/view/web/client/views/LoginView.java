package scheduler.view.web.client.views;

import scheduler.view.web.client.CachedService;
import scheduler.view.web.client.GreetingServiceAsync;
import scheduler.view.web.client.LogoutHandler;
import scheduler.view.web.client.UpdateHeaderStrategy;
import scheduler.view.web.client.views.home.HomeView;
import scheduler.view.web.client.views.resources.instructors.InstructorsHomeView;
import scheduler.view.web.shared.LoginResponse;

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
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class LoginView extends SimplePanel {
	GreetingServiceAsync service;
	
	String username;
	final UpdateHeaderStrategy updateHeaderStrategy;
	TextBox textBox;
	
	VerticalPanel loginViewContents;

	public LoginView(GreetingServiceAsync service, UpdateHeaderStrategy updateHeaderStrategy) {
		this.updateHeaderStrategy = updateHeaderStrategy;
		
		this.service = service;
		
		loginViewContents = new VerticalPanel();

		loginViewContents.setWidth("100%");
		loginViewContents.setHorizontalAlignment(HasAlignment.ALIGN_CENTER);
		
		loginViewContents.add(new HTML("<h2>Login</h2>"));
		DOM.setElementAttribute(loginViewContents.getElement(), "id", "s_loginTag");

		textBox = new TextBox();
		DOM.setElementAttribute(textBox.getElement(), "id", "s_unameBox");
		
		textBox.addKeyPressHandler(new KeyPressHandler() {
			@Override
			public void onKeyPress(KeyPressEvent event) {
				if (event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ENTER)
					submitLogin(textBox.getText());
			}
		});
		loginViewContents.add(textBox);
		
		Button login = new Button("Login", new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				submitLogin(textBox.getText());
			}
		});
		
		DOM.setElementAttribute(login.getElement(), "id", "s_loginBtn");
		loginViewContents.add(login);
		
		this.setWidget(loginViewContents);
	}
	
	@Override
	protected void onLoad() {
		super.onLoad();
		
		textBox.setFocus(true);
	}
	
	protected void onLogout() {
		clear();
		setWidget(loginViewContents);
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
		
		final LoadingPopup loadingPopup = new LoadingPopup();
		loadingPopup.show();
		
		service.loginAndGetAllOriginalDocuments(username, new AsyncCallback<LoginResponse>() {
			@Override
			public void onFailure(Throwable caught) {
				loadingPopup.hide();
				
				Window.alert("Failed to get documents");
			}
			
			@Override
			public void onSuccess(LoginResponse response) {
				updateHeaderStrategy.onLogin(username, new LogoutHandler() {
					@Override
					public void handleLogout() {
						onLogout();
					}
				});

				loadingPopup.hide();
				
				final CachedService cachedService = new CachedService(true, service, response.sessionID, username, response.initialOriginalDocuments);
				
				if (response.isAdmin) {
					clear();
					setWidget(new HomeView(updateHeaderStrategy, cachedService));
				}
				else {
					clear();
					setWidget(new InstructorsHomeView(cachedService));
				}
			}
		});
	}
}
