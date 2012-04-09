package scheduler.view.web.client.calendar;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.RootPanel;

import scheduler.view.web.shared.DayGWT;
import scheduler.view.web.shared.ScheduleItemGWT;

/**
 * Takes in various mouse events to determine when items are dragged/dropped. 
 * Notifies the model of drops.
 * It also contains the logic for displaying the item being dragged next to the cursor.
 * 
 * @author Matt Schirle
 */
public class DragAndDropController implements MouseMoveHandler, MouseOutHandler, MouseUpHandler {

	public static final String DRAGGED_ID = "dragItem";
	
	private final ScheduleEditWidget mWidget;
	
	private Element mItemView;
	private ScheduleItemGWT mDraggingItem;
	private ScheduleItemGWT mDroppedItem;
	private boolean isMoving = false;
	private boolean fromCalendar = false;
	
	public DragAndDropController(ScheduleEditWidget widget) {
		mWidget = widget;
		
		addMouseHandlers();
	}
	
	public boolean isHolding() {
		return mDraggingItem != null && mItemView != null;
	}
	
	public boolean isDragging() {
		return isMoving && isHolding();
	}
	
	@Override
	public void onMouseMove(MouseMoveEvent event) {
		if (isHolding()) {
			isMoving = true;
			
			// Make the DRAGGED_ID div follow the cursor
			DOMUtility.setStyleAttribute(DRAGGED_ID, "display", "block");
			DOMUtility.setStyleAttribute(DRAGGED_ID, "left", event.getClientX()+"px");
			DOMUtility.setStyleAttribute(DRAGGED_ID, "top", event.getClientY()+"px");
			
			// Hide the contents of the table cell the user dragged
			DOMUtility.setStyleAttribute(mItemView.getId(), "color", "#FFFFFF");
			DOMUtility.setStyleAttribute(mItemView.getId(), "backgroundColor", "#FFFFFF");
			
			// Disable text selection
			setTextSelection(false);
			
			// TODO Hide contents of every occurrence of the dragged item on the table (if it appears on multiple days)
			// TODO if this is a list item with multiple sections remaining, just decrement section count and don't hide
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
	 * @param row The row number of this item in the table or in the list
	 * @param tableCol The column number of this item in the table, 
	 * 	or -1 if available courses list
	 */
	public void onMouseDown(ScheduleItemGWT item, int row, int tableCol) {
		mDraggingItem = item;
		isMoving = false;
		
		if (tableCol < 0) {
			mItemView = DOM.getElementById("list"+row);
			fromCalendar = false;
		}
		else {
			mItemView = DOM.getElementById("x"+tableCol+"y"+row);
			fromCalendar = true;
		}
	}
	
	/**
	 * Triggered when an item is dropped (as opposed to every mouse up event)
	 *  
	 * @param row The table row the item was dropped on, or -1 if off table
	 * @param col The Day the item was dropped on, or null if off table
	 */
	public void onDrop(int row, DayGWT day) {
		// Check if moving to prevent clicks from registering as drops
		if (isMoving)
			dropItem(true);
		else
			dropItem(false);
			
		if (mDroppedItem != null) {
			final String courseString = mWidget.getCourseString(mDroppedItem.getCourseID());
			
			if (row < 0) { // dropped on list
				System.out.println(courseString+" dropped on list");
				
				if (fromCalendar) 
					mWidget.removeItem(mDroppedItem);
				else
					cancelDrop();
			}
			else {
				List<Integer> days = new ArrayList<Integer>();
				
				if (day == null) {// dropped on time column
					System.out.println(courseString+" dropped on time column at "+row);
				}
				else { // dropped on a day column
					System.out.println(courseString+" dropped on "+day.ordinal()+" at "+row);
					days.add(day.ordinal());
				}
				
				mWidget.editItem(!fromCalendar, mDroppedItem, days, row);
			}
			
			mDroppedItem = null;
		}
	}
	
	public void cancelDrop() {
		dropItem(false);
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
		else if (mItemView != null) {
			// Show contents of hidden table cell
			DOM.setStyleAttribute(mItemView, "color", "#000000");
			DOM.setStyleAttribute(mItemView, "backgroundColor", "#DFF0CF");
			mItemView = null;
		}
		
		// Hide the div that follows the cursor while dragging
		DOMUtility.setStyleAttribute(DRAGGED_ID, "display", "none");
		
		// Allow text selection
		setTextSelection(true);
	}
	
	private void addMouseHandlers() {
		// TODO unregister these? or use this class as a singleton?
		// When a user leaves the calendar view and comes back to it a new DragAndDropController is constructed
		
		RootPanel.get().addDomHandler(this, MouseMoveEvent.getType());
		RootPanel.get().addDomHandler(this, MouseOutEvent.getType());
		RootPanel.get().addDomHandler(this, MouseUpEvent.getType());
	}
	
	/**
	 * Allow or disallow text selection.
	 * 
	 * @param selectionAllowed true if text selection should be allowed, false if it should be disabled
	 */
	private void setTextSelection(boolean selectionAllowed) {
		String attrVal = "text";
		if (!selectionAllowed)
			attrVal = "none";
		
		// Set the user select attribute for all browsers
		DOMUtility.setStyleAttribute(RootPanel.getBodyElement(), "webkitTouchCallout", attrVal);
		DOMUtility.setStyleAttribute(RootPanel.getBodyElement(), "webkitUserSelect", attrVal);
		DOMUtility.setStyleAttribute(RootPanel.getBodyElement(), "khtmlUserSelect", attrVal);
		DOMUtility.setStyleAttribute(RootPanel.getBodyElement(), "mozUserSelect", attrVal);
		DOMUtility.setStyleAttribute(RootPanel.getBodyElement(), "msUserSelect", attrVal);
		DOMUtility.setStyleAttribute(RootPanel.getBodyElement(), "userSelect", attrVal);
	}
}
