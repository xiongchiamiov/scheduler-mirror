package edu.calpoly.csc.scheduler.view.web.client.views;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.calpoly.csc.scheduler.view.web.client.GreetingServiceAsync;

public class SelectQuarterView extends VerticalPanel {
	Panel container;
	GreetingServiceAsync service;
	ListBox scheduleList;
	
	public SelectQuarterView(Panel container, GreetingServiceAsync service) {
		this.container = container;
		this.service = service;
	}
	
	@Override
	public void onLoad() {
		super.onLoad();
		
		addStyleName("selectQuarterView");

		scheduleList = new ListBox();
		scheduleList.addItem("Fall Quarter 2011 Final Schedule", "1");
		scheduleList.addItem("Fall Quarter 2011 First Draft Schedule", "2");
		scheduleList.addItem("Summer Quarter 2011 Final Schedule", "3");
		scheduleList.addItem("Summer Quarter 2011 First Draft Schedule", "4");
		scheduleList.addItem("Spring Quarter 2011 Final Schedule", "5");
		add(scheduleList);
		
		add(new Button("Select Schedule", new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				container.clear();
				container.add(new QuarterView(container, service, scheduleList.getValue(scheduleList.getSelectedIndex())));
			}
		}));
	}
}
