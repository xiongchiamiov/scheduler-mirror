package edu.calpoly.csc.scheduler.view.web.client.table;

import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
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
	private int desiredWidthPixels;
	final ResizeCallback callback;

	public ResizeableWidget(Widget draggableArea, Widget contents, final ResizeCallback callback) {
		this.callback = callback;
		
		add(contents);
		
		addStyleName("resizeable");
		
		SimplePanel resizer = new SimplePanel();
		resizer.addStyleName("resizer");
		add(resizer);
		
		resizer.addDomHandler(new MouseDownHandler() {
			public void onMouseDown(MouseDownEvent event) {
				dragging = true;
				dragAnchorX = event.getClientX();
				desiredWidthPixels = callback.getWidth();
				event.preventDefault();
			}
		}, MouseDownEvent.getType());
		
		draggableArea.addDomHandler(new MouseMoveHandler() {
			public void onMouseMove(MouseMoveEvent event) {
				if (dragging) {
					int dragX = event.getClientX();
					desiredWidthPixels += dragX - dragAnchorX;
					dragAnchorX = dragX;
					callback.setWidth(desiredWidthPixels);
					System.out.println("Setting width to " + desiredWidthPixels);
					refreshWidth();
					event.preventDefault();
				}
			}
		}, MouseMoveEvent.getType());
		
		draggableArea.addDomHandler(new MouseUpHandler() {
			public void onMouseUp(MouseUpEvent event) { dragging = false; }
		}, MouseUpEvent.getType());
	}

	void refreshWidth() {
		System.out.println("refreshing width to " + callback.getWidth() + "px");
		setWidth(callback.getWidth() + "px");
	}
}
