package edu.calpoly.csc.scheduler.view.web.client.views;

import java.util.Map;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HasAlignment;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.calpoly.csc.scheduler.view.web.client.GreetingServiceAsync;

public class SelectQuarterView extends VerticalPanel {
	Panel container;
	GreetingServiceAsync service;
	ListBox scheduleList;
	Map<Integer, String> scheduleIDsAndNames;
	
	public SelectQuarterView(Panel container, GreetingServiceAsync service, Map<Integer, String> scheduleIDsAndNames) {
		this.container = container;
		this.service = service;
		this.scheduleIDsAndNames = scheduleIDsAndNames;
	}
	
	@Override
	public void onLoad() {
		super.onLoad();
		
		addStyleName("selectQuarterView");

		VerticalPanel panel = new VerticalPanel();
		panel.setWidth("100%");
		add(panel);
		panel.setHorizontalAlignment(HasAlignment.ALIGN_CENTER);

		scheduleList = new ListBox();
		for (Integer scheduleID : scheduleIDsAndNames.keySet())
			scheduleList.addItem(scheduleIDsAndNames.get(scheduleID), scheduleID.toString());
		panel.add(scheduleList);
		
		panel.add(new Button("Select Schedule", new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				service.selectSchedule(0, new AsyncCallback<Void>() {
					@Override
					public void onFailure(Throwable caught) {
						// TODO Auto-generated method stub
						
					}
					
					public void onSuccess(Void result) {
						container.clear();
						container.add(new QuarterView(container, service));
					};
				});
			}
		}));
	}
}
