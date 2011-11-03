package edu.calpoly.csc.scheduler.view.web.client.views;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasAlignment;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.calpoly.csc.scheduler.view.web.client.GreetingServiceAsync;

public class LoginView extends ScrollPanel {
	MainView mainView; // Kept around so we can tell it to change the username
	
	Panel container;
	GreetingServiceAsync service;
	
	LoginView(MainView mainView, Panel container, GreetingServiceAsync service) {
		this.mainView = mainView;
		this.container = container;
		this.service = service;
	}
	
	@Override
	public void onLoad() {
		super.onLoad();
		
		final VerticalPanel panel = new VerticalPanel();
		panel.setWidth("100%");
		add(panel);
		panel.setHorizontalAlignment(HasAlignment.ALIGN_CENTER);
		
		panel.add(new HTML("<h2>Login</h2>"));

		final TextBox textBox = new TextBox();
		panel.add(textBox);
		
		panel.add(new Button("Login", new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				final String username = textBox.getText();
				
				if ("".equals(username)) {
					Window.alert("Please enter a username.");
					return;
				}

				final LoadingPopup popup = new LoadingPopup();
				popup.show();
				
				service.login(username, new AsyncCallback<Void>() {
					@Override
					public void onSuccess(Void derp) {
						mainView.onUserLoggedIn(username);
						
						popup.hide();
						container.clear();
						container.add(new HomeView(container, service));
					}
					
					@Override
					public void onFailure(Throwable caught) {
						popup.hide();
						Window.alert("Failed to log in: " + caught.getMessage());
					}
				});
			}
		}));
	}
}
