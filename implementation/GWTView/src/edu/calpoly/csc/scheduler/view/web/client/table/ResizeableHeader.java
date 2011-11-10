package edu.calpoly.csc.scheduler.view.web.client.table;

import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class ResizeableHeader extends FocusPanel {
	private boolean dragging = false;
	private int dragAnchorX;
	private int widthPixels;
	
	public ResizeableHeader(FocusPanel draggableArea, final Widget contents) {
		FlowPanel panel = new FlowPanel();
		add(panel);
		
		panel.addStyleName("resizeableHeader");
		
		panel.add(contents);
		
		SimplePanel resizer = new SimplePanel();
		resizer.addStyleName("resizer");
		panel.add(resizer);
		
		this.addMouseDownHandler(new MouseDownHandler() {
			public void onMouseDown(MouseDownEvent event) {
				dragging = true;
				dragAnchorX = event.getClientX();
				widthPixels = getOffsetWidth();
				event.preventDefault();
			}
		});
		
		draggableArea.addMouseMoveHandler(new MouseMoveHandler() {
			public void onMouseMove(MouseMoveEvent event) {
				if (dragging) {
					int dragX = event.getClientX();
					widthPixels += dragX - dragAnchorX;
					dragAnchorX = dragX;
					contents.setWidth(widthPixels + "px");
					event.preventDefault();
				}
			}
		});
		
		draggableArea.addMouseUpHandler(new MouseUpHandler() {
			public void onMouseUp(MouseUpEvent event) { dragging = false; }
		});
	}
}
