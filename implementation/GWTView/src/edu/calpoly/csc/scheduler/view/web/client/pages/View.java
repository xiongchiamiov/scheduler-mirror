package edu.calpoly.csc.scheduler.view.web.client.pages;

import com.google.gwt.user.client.ui.FlowPanel;

public abstract class View extends FlowPanel {
	public abstract void beforeHide();
	public abstract void afterShow();
}
