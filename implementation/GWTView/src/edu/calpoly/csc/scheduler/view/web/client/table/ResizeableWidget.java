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
		void trySettingWidth(int newWidthPixels);
	}
	
	private boolean dragging = false;
	private int dragAnchorX;
	final ResizeCallback callback;

	public ResizeableWidget(Widget draggableArea, String initialWidth, boolean resizable, Widget contents, final ResizeCallback callback) {
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
						trySettingWidth(getOffsetWidth() + difference);
					}
				}
			}, MouseMoveEvent.getType());
			
			draggableArea.addDomHandler(new MouseUpHandler() {
				public void onMouseUp(MouseUpEvent event) { dragging = false; }
			}, MouseUpEvent.getType());
		}
		
		if (initialWidth != null) {
			setWidth(initialWidth);
			int initialWidthPixels = getOffsetWidth();
			trySettingWidth(initialWidthPixels);
		}
	}
	
	void trySettingWidth(int newWidth) {
		if (newWidth < 0)
			newWidth = 0;
		
		callback.trySettingWidth(newWidth);
		
		setWidth(callback.getWidth() + "px");
		
		assert(getOffsetWidth() == callback.getWidth());
		
	}

	public void updateWidth() {
		setWidth(callback.getWidth() + "px");
		
		assert(getOffsetWidth() == callback.getWidth());
	}
}
