package edu.calpoly.csc.scheduler.view.web.client.views;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.SimplePanel;

import edu.calpoly.csc.scheduler.view.web.client.GreetingServiceAsync;

public class MainView extends DockLayoutPanel {
	GreetingServiceAsync service;
	
	Panel contents;
	
	public MainView(GreetingServiceAsync service) {
		super(Unit.EM);
		
		this.service = service;
	}
	
	@Override
	protected void onLoad() {
		super.onLoad();
		
		addStyleName("mainWidget");
		
		SimplePanel topPanel = new SimplePanel();
		topPanel.addStyleName("topPanel");
		addNorth(topPanel, 5);
		
		add(contents = new SimplePanel());
		contents.add(new LoginView(contents, service));
	}
}
