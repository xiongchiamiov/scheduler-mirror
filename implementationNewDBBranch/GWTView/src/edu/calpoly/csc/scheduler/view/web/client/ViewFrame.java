package edu.calpoly.csc.scheduler.view.web.client;

import com.google.gwt.user.client.ui.SimplePanel;

public final class ViewFrame extends SimplePanel {
	ViewFrame viewAboveMe;
	IViewContents contents;
	
	public ViewFrame(IViewContents contents) {
		this.contents = contents;
		add(contents.getContents());
	}
	
	protected boolean canPop() {
		return contents.canPop();
	}
	protected void afterPush() {
		contents.afterPush(this);
	}
	protected void beforePop() {
		contents.beforePop();
	}
	protected void beforeViewPushedAboveMe() {
		contents.beforeViewPushedAboveMe();
	}
	protected void afterViewPoppedFromAboveMe() {
		contents.afterViewPoppedFromAboveMe();
	}
	
	public final boolean canPopViewsAboveMe() {
		if (viewAboveMe != null) {
			if (!viewAboveMe.canPop())
				return false;
			if (!viewAboveMe.canPopViewsAboveMe())
				return false;
		}
		
		return true;
	}
	
	public final void popFramesAboveMe() {
		assert (canPopViewsAboveMe());
		if (viewAboveMe != null) {
			viewAboveMe.popFramesAboveMe();
			viewAboveMe.beforePop();
			clear();
			add(contents.getContents());
			viewAboveMe = null;
			afterViewPoppedFromAboveMe();
		}
	}
	
	public final void frameViewAndPushAboveMe(IViewContents newViewContents) {
		ViewFrame newView = new ViewFrame(newViewContents);
		
		assert (viewAboveMe == null);
		
		beforeViewPushedAboveMe();
		clear();
		
		viewAboveMe = newView;
		
		add(viewAboveMe);
		
		viewAboveMe.afterPush();
	}
}
