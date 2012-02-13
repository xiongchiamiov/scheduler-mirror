package edu.calpoly.csc.scheduler.view.web.client;

import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public abstract class View extends SimplePanel {
	View viewAboveMe;
	
	protected abstract Widget getViewWidget();
	
	protected boolean canPop() { return true; }
	protected void afterPush() { }
	protected void beforePop() { }
	protected void beforeViewPushedAboveMe() { }
	protected void afterViewPoppedFromAboveMe() { }
	
	protected final boolean canPopViewsAboveMe() {
		if (viewAboveMe != null) {
			if (!viewAboveMe.canPop())
				return false;
			if (!viewAboveMe.canPopViewsAboveMe())
				return false;
		}
		
		return true;
	}
	
	protected final void popViewsAboveMe() {
		assert(canPopViewsAboveMe());
		if (viewAboveMe != null) {
			viewAboveMe.popViewsAboveMe();
			viewAboveMe.beforePop();
			clear();
			viewAboveMe = null;
			afterViewPoppedFromAboveMe();
		}
	}
	
	protected final void pushViewAboveMe(View newView) {
		assert(viewAboveMe == null);
		
		beforeViewPushedAboveMe();
		
		viewAboveMe = newView;
		
		Widget viewWidget = newView.getViewWidget();
		assert(viewWidget != null && viewWidget != this);
		add(viewWidget);
		
		viewAboveMe.afterPush();
	}
}
