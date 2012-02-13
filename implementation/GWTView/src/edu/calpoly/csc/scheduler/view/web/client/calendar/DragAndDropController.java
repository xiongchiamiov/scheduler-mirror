package edu.calpoly.csc.scheduler.view.web.client.calendar;

import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.RootPanel;

import edu.calpoly.csc.scheduler.view.web.shared.ScheduleItemGWT;

/**
 * Takes in various mouse events to determine when items are dragged/dropped. 
 * Notifies the model of drops.
 * It also contains the logic for displaying the item being dragged next to the cursor.
 * 
 * @author Matt Schirle
 */
public class DragAndDropController implements MouseMoveHandler, MouseOutHandler, MouseUpHandler {

	public static final String DRAGGED_ID = "dragItem";
	
	private Element mTableCell;
	private ScheduleItemGWT mDraggingItem;
	private ScheduleItemGWT mDroppedItem;
	private boolean isMoving = false;
	
	public DragAndDropController() {
		System.out.println("Mediator ctor");
		addMouseHandlers();
	}
	
	public boolean isHolding() {
		return mDraggingItem != null && mTableCell != null;
	}
	
	public boolean isDragging() {
		return isMoving && isHolding();
	}
	
	@Override
	public void onMouseMove(MouseMoveEvent event) {
		if (isHolding()) {
			isMoving = true;
			
			// Make the DRAGGED_ID div follow the cursor
			Element dragDiv = DOM.getElementById(DRAGGED_ID);
			DOM.setStyleAttribute(dragDiv, "display", "block");
			DOM.setStyleAttribute(dragDiv, "left", event.getClientX()+"px");
			DOM.setStyleAttribute(dragDiv, "top", event.getClientY()+"px");
			
			// Hide the contents of the table cell the user dragged
			DOM.setStyleAttribute(mTableCell, "color", "#FFFFFF");
			DOM.setStyleAttribute(mTableCell, "backgroundColor", "#FFFFFF");
		}
	}
	
	@Override
	public void onMouseOut(MouseOutEvent event) {
		if (isDragging())
			dropItem(false);
	}
	
	@Override
	public void onMouseUp(MouseUpEvent event) {
		if (isDragging())
			dropItem(false);
	}
	
	/**
	 * Triggered when the user starts dragging an item
	 * 
	 * @param item The item
	 * @param tableRow The row number of this item in the table, 
	 * 	or -1 if available courses list
	 * @param tableCol The column number of this item in the table, 
	 * 	or -1 if available courses list
	 */
	public void onMouseDown(ScheduleItemGWT item, int tableRow, int tableCol) {
		mDraggingItem = item;
		mTableCell = DOM.getElementById("x"+tableCol+"y"+tableRow);
		isMoving = false;
	}
	
	/**
	 * Triggered when an item is dropped
	 *  
	 * @param row The table row the item was dropped on, or -1 if off table
	 * @param col The table column the item was dropped on, or -1 if off table
	 */
	public void onDrop(int row, int col) {
		// Check if moving to prevent clicks from registering as drops
		if (isMoving)
			dropItem(true);
		else
			dropItem(false);
		
		if (mDroppedItem != null) {
			if (row < 0 || col < 0)
				System.out.println(mDroppedItem.getCourseString() + " dropped on list");
			else
				System.out.println(mDroppedItem.getCourseString() + " dropped at x"+col+"y"+row);
			mDroppedItem = null;
		}
	}
	
	/**
	 * Helper method for updating the UI to reflect this drop
	 * 
	 * @param validDrop True iff the user dropped an item on the calendar or available courses list
	 */
	private void dropItem(boolean validDrop) {
		if (validDrop) {
			mDroppedItem = mDraggingItem;
			mDraggingItem = null;
		}
		else if (mTableCell != null) {
			// Show contents of hidden table cell
			DOM.setStyleAttribute(mTableCell, "color", "#000000");
			DOM.setStyleAttribute(mTableCell, "backgroundColor", "#DFF0CF");
			mTableCell = null;
		}
		
		// Hide the div that follows the cursor while dragging
		Element dragDiv = DOM.getElementById(DRAGGED_ID);
		DOM.setStyleAttribute(dragDiv, "display", "none");
	}
	
	private void addMouseHandlers() {
		// TODO unregister these? or use this class as a singleton?
		// When a user leaves the calendar view and comes back to it a new DragAndDropController is constructed
		
		RootPanel.get().addDomHandler(this, MouseMoveEvent.getType());
		RootPanel.get().addDomHandler(this, MouseOutEvent.getType());
		RootPanel.get().addDomHandler(this, MouseUpEvent.getType());
	}
}
