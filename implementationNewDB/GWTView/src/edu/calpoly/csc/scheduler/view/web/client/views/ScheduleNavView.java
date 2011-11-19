package edu.calpoly.csc.scheduler.view.web.client.views;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import edu.calpoly.csc.scheduler.view.web.client.GreetingServiceAsync;
import edu.calpoly.csc.scheduler.view.web.shared.InstructorGWT;

public abstract class ScheduleNavView extends VerticalPanel implements IView<MainView> {
	public Widget getViewWidget() { return this; }
	
	public boolean canCloseView() { return currentView.canCloseView(); }
	public void willOpenView(MainView container) { }
	
	SelectScheduleView selectScheduleView;
	
	FlowPanel topPanel;
	Panel container;
	GreetingServiceAsync service;
	Panel contentPanel;
	int selectedScheduleID;
	final String scheduleName;
	InstructorGWT currentInstructor;
	
	FocusPanel currentNavButton;
	IView<ScheduleNavView> currentView;
	
	ScheduleNavView(SelectScheduleView homeView, Panel container, GreetingServiceAsync service, int selectedScheduleID, String scheduleName) {
		this.selectScheduleView = homeView;
		
		this.container = container;
		this.service = service;
		
		this.selectedScheduleID = selectedScheduleID;
		this.scheduleName = scheduleName;

		setWidth("100%");

		topPanel = new FlowPanel();
		topPanel.setWidth("100%");
		topPanel.addStyleName("topBarMenu");
		
		Widget scheduleSelectWidget = selectScheduleView.createMiniSelectWidget(selectedScheduleID);
		scheduleSelectWidget.addStyleName("topBarScheduleSelect");
		topPanel.add(scheduleSelectWidget);
		
		add(topPanel);
		
		add(contentPanel = new SimplePanel());
	}
	
	boolean closeCurrentView() {
		return currentView == null || currentView.canCloseView();
	}
	
	// Give null as navButton to keep the current button highlit
	public void switchToView(FocusPanel navButton, IView<ScheduleNavView> newView) {
		if (navButton == null)
			navButton = currentNavButton;
		
		if (currentView != null) {
			currentView = null;
			contentPanel.clear();
		}
		
		if (currentNavButton != null) {
			currentNavButton.removeStyleName("currentView");
			currentNavButton = null;
		}
		
		assert(currentView == null);
		assert(currentNavButton == null);

		currentNavButton = navButton;
		currentNavButton.addStyleName("currentView");

		currentView = newView;
		currentView.willOpenView(this);
		contentPanel.add(currentView.getViewWidget());
	}
	
	void addButton(String label, String styleNames, boolean defaultView, final CreateViewCallback creator) {
		final FocusPanel newPanel = new FocusPanel();
		newPanel.add(new HTML(label));
		newPanel.addStyleName(styleNames);
		newPanel.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				newPanel.setFocus(false);
				if (closeCurrentView())
					switchToView(newPanel, creator.createView());
			}
		});
		topPanel.add(newPanel);
		
		if (defaultView) {
			currentNavButton = newPanel;
			currentView = creator.createView();
		}
	}
	
	protected interface CreateViewCallback {
		IView<ScheduleNavView> createView();
	}
	
	@Override
	protected void onLoad() {
		super.onLoad();
		
		assert((currentNavButton == null) == (currentView == null));
		
		if (currentView != null) {
			IView<ScheduleNavView> defaultView = currentView;
			FocusPanel defaultButton = currentNavButton;
			currentView = null;
			currentNavButton = null;

			defaultButton.setFocus(false);
			if (closeCurrentView())
				switchToView(defaultButton, defaultView);
		}
		
	}
}
