package edu.calpoly.csc.scheduler.view.web.client.views;

import java.util.Map;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasAlignment;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.calpoly.csc.scheduler.view.web.client.GreetingServiceAsync;

public class LoginView extends ScrollPanel {
	Panel container;
	GreetingServiceAsync service;
	
	LoginView(Panel container, GreetingServiceAsync service) {
		this.container = container;
		this.service = service;
	}
	
	@Override
	public void onLoad() {
		super.onLoad();
		
		VerticalPanel panel = new VerticalPanel();
		panel.setWidth("100%");
		add(panel);
		panel.setHorizontalAlignment(HasAlignment.ALIGN_CENTER);
		
		panel.add(new HTML("<h2>Login</h2>"));

		panel.add(new Button("Fake CAS Login", new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {

				container.clear();
				container.add(new HomeView(container, service, "gfisher"));
			}
		}));
	}
}
