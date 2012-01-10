package edu.calpoly.csc.scheduler.view.web.client.table;

import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

class ResizeableWidget extends FlowPanel {
	public interface ResizeCallback {
		int getWidth();
		void setWidth(int newWidthPixels);
	}
	
	private boolean dragging = false;
	private int dragAnchorX;
	final ResizeCallback callback;

	public ResizeableWidget(Widget draggableArea, boolean resizable, Widget contents, final ResizeCallback callback) {
		this.callback = callback;
		
		add(contents);
		
		addStyleName("resizeable");
		
		if (resizable) {
			SimplePanel resizer = new SimplePanel();
			resizer.addStyleName("resizer");
			add(resizer);
			
			resizer.addDomHandler(new MouseDownHandler() {
				public void onMouseDown(MouseDownEvent event) {
					dragging = true;
					dragAnchorX = event.getClientX();
					event.preventDefault();
				}
			}, MouseDownEvent.getType());
		
			draggableArea.addDomHandler(new MouseMoveHandler() {
				public void onMouseMove(MouseMoveEvent event) {
					if (dragging) {
						event.preventDefault();
						
						int dragX = event.getClientX();
						int difference = dragX - dragAnchorX;
						dragAnchorX = dragX;
						setBothWidths(getOffsetWidth() + difference);
					}
				}
			}, MouseMoveEvent.getType());
			
			draggableArea.addDomHandler(new MouseUpHandler() {
				public void onMouseUp(MouseUpEvent event) { dragging = false; }
			}, MouseUpEvent.getType());
		}
	}

	void synchronizeToMaximumOfBoth() {
		setBothWidths(Math.max(callback.getWidth(), getOffsetWidth()));
	}
//	
//	void synchronizeWidths(boolean stretchToAccommodateCallbackWidth) {
//		int neededWidth = getOffsetWidth();
//		if (stretchToAccommodateCallbackWidth)
//			neededWidth = Math.max(callback.getWidth(), neededWidth);
//		System.out.println("refreshing width to " + neededWidth + "px");
//		
//		callback.setWidth(neededWidth);
//		if (callback.getWidth() != neededWidth) {
//			System.out.println("Callback is not honoring its setwidth! we called setWidth with " + neededWidth + " but after that it returned " + callback.getWidth());
//		}
//		
//		if (stretchToAccommodateCallbackWidth) {
//			setWidth(neededWidth + "px");
//			if (getOffsetWidth() != neededWidth) {
//				System.out.println("i set my width but now im " + getOffsetWidth() + " and callback is returning " + callback.getWidth());
//			}
//		}
//	}
//	
	void setBothWidths(int newWidth) {
		if (newWidth < 0)
			newWidth = 0;
		
		setWidth(newWidth + "px");
		if (getOffsetWidth() != newWidth) {
//			System.out.println("Contents not honoring setwidth!");
			// Will have to be synchronized
		}
		
		callback.setWidth(newWidth);
		if (callback.getWidth() != newWidth) {
//			System.out.println("Callback not honoring setwidth!");
			// Will have to be synchronized
		}
		
		synchronize();
	}
	
	// Tries to conform to callback's first
	void synchronize() {
		if (callback.getWidth() != getOffsetWidth()) {
			setWidth(callback.getWidth() + "px");
		}

		if (callback.getWidth() != getOffsetWidth()) {
			callback.setWidth(getOffsetWidth());
		}

		if (callback.getWidth() != getOffsetWidth()) {
//			System.out.println("Resizeable and callback can't synchronize!");
		}
	}
}
