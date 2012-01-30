package edu.calpoly.csc.scheduler.view.web.client.views;

import java.util.HashMap;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import edu.calpoly.csc.scheduler.model.schedule.ScheduleItem;
import edu.calpoly.csc.scheduler.view.web.client.GreetingServiceAsync;
import edu.calpoly.csc.scheduler.view.web.client.IViewContents;
import edu.calpoly.csc.scheduler.view.web.client.ViewFrame;
import edu.calpoly.csc.scheduler.view.web.client.schedule.ScheduleViewWidget;

public class ScheduleView extends VerticalPanel implements IViewContents {
	private GreetingServiceAsync service;
	private String scheduleName;
	HashMap<String, ScheduleItem> scheduleItems;
	
	public ScheduleView(GreetingServiceAsync greetingService,
			String scheduleName) {
		this.service = greetingService;
		this.scheduleName = scheduleName;
	}

	@Override
	public void afterPush(ViewFrame frame) {
		this.setWidth("100%");
		this.setHeight("100%");

		this.add(new HTML("<h2>" + scheduleName + "</h2>"));

		this.add(new Button("Save", new ClickHandler() {
			public void onClick(ClickEvent event) {
				final LoadingPopup popup = new LoadingPopup();
				popup.show();
				service.saveSchedule(new AsyncCallback<Void>() {
					@Override
					public void onFailure(Throwable caught) {
						popup.hide();
						Window.alert("There was an error saving the schedule: "
								+ caught.getMessage());
					}

					@Override
					public void onSuccess(Void derp) {
						popup.hide();
						Window.alert("Schedule has been saved successfully.");
					}
				});
			}
		}));

		ScheduleViewWidget schdView = new ScheduleViewWidget();

		this.add(schdView.getWidget(service));
	}

	@Override
	public boolean canPop() {
		return true;
	}

	@Override
	public void beforePop() {
	}

	@Override
	public void beforeViewPushedAboveMe() {
	}

	@Override
	public void afterViewPoppedFromAboveMe() {
	}

	@Override
	public Widget getContents() {
		return this;
	}
}
