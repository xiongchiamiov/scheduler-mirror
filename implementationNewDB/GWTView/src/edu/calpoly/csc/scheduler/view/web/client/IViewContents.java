package edu.calpoly.csc.scheduler.view.web.client;

import com.google.gwt.user.client.ui.Widget;

public interface IViewContents {
	boolean canPop();
	void afterPush(ViewFrame frame);
	void beforePop();
	void beforeViewPushedAboveMe();
	void afterViewPoppedFromAboveMe();

	Widget getContents();
}
