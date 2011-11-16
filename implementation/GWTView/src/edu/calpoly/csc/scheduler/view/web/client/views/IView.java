package edu.calpoly.csc.scheduler.view.web.client.views;

import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;

public interface IView<Container> {
	Widget getViewWidget();

	void willOpenView(Container container);
	
	// Returns true if we can close the view, returns false if we shouldn't close the view
	boolean canCloseView();
}
