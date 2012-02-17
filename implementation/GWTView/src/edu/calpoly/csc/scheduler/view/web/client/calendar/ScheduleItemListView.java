package edu.calpoly.csc.scheduler.view.web.client.calendar;

import java.util.List;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.SimplePanel;

import edu.calpoly.csc.scheduler.view.web.shared.ScheduleItemGWT;

public class ScheduleItemListView extends SimplePanel {

	private final ScheduleEditWidget mScheduleController;
	private final DragAndDropController mDragController;
	
	private List<ScheduleItemGWT> mModel;
	
	public ScheduleItemListView(List<ScheduleItemGWT> items, ScheduleEditWidget scheduleController, DragAndDropController dragController) {
		//mModel = new DummySchedule();
		mModel = items;
		mScheduleController = scheduleController;
		mDragController = dragController;

		defineTableCallbacks();
	}
	
	public List<ScheduleItemGWT> getItems() { return mModel; }
	
	public void setItems(List<ScheduleItemGWT> items) { mModel = items; }
	
	public void toggle(boolean hidden) {
		Element container = DOM.getElementById("ScheduleListContainer");
		if (hidden)
			DOM.setStyleAttribute(container, "display", "none");
		else
			DOM.setStyleAttribute(container, "display", "block");
	}
	
	public void drawList() {
		StringBuilder builder = new StringBuilder();
		builder.append("<style type=\"text/css\">"+
				"#ScheduleListContainer {position:absolute;top:116px;bottom:33px;left:0px;width:200px;border-right:1px solid #000000;}"+
				"#ScheduleListTableContainer {position:absolute;top:28px;left:0px;right:0px;bottom:0px;width:100%;border:none;margin:0px;padding:0px;overflow-y:auto;overflow-x:hidden;}"+
				"#ScheduleList {margin:0px;background-color:#000000;border:none;cellspacing:0px;border-spacing:0px;width:100%;}"+
				"#ScheduleList tr {height:20px;}"+
				"#ScheduleList td {background-color:#FFFFFF;border-bottom:1px solid #d1dfdf;text-align:center;padding:0px;margin:0px;}"+
				"#ScheduleListHeader {width:100%;height:19px;background-color:#edf2f2;font-weight:bold;border-bottom:1px solid #000000;padding:0px;margin:0px;padding-top:4px;padding-bottom:4px;}"+
				"#ScheduleList td .ScheduleListItem {background-color:#DFF0CF;margin:1px;text-align:center;padding-top:4px;padding-bottom:4px;height:100%;width:100%;border:none;cursor:move;}"+
				"</style>");
		builder.append("<div id=\"ScheduleListContainer\">" +
				"<div id=\"ScheduleListHeader\">AvailableCourses</div>" +
				"<div id=\"ScheduleListTableContainer\">" +
				"<table id=\"ScheduleList\">");
		
		int rowNum = 0;
		for (ScheduleItemGWT item : mModel) {
			builder.append("<tr " +
					"onmouseover=\"listMouseOver("+rowNum+")\" " +
					"onmouseout=\"listMouseOut("+rowNum+")\" " +
					">");
			builder.append("<td><div class=\"ScheduleListItem\" id=\"list"+rowNum+"\"" +
					"ondblclick=\"listDoubleClick("+rowNum+")\" " +
					"onmousedown=\"listMouseDown("+rowNum+")\" " +
					"onmouseup=\"listMouseUp("+rowNum+")\" " +
					"onselectstart=\"return false\" "+
					">"+item.getCourseString()+"</div></td>");
			builder.append("</tr>");
			rowNum++;
		}
		
		builder.append("</div></table></div>");
		
		setHTML(builder.toString());
	}
	
	private void setHTML(String html) {
		clear();
		add(new HTML(html));
	}

	/**
	 * Called when the user double clicks an item in the table
	 */
	public void doubleClick(int row) {
		ScheduleItemGWT item = mModel.get(row);
		
		mScheduleController.editItem(item);
	}
	
	/**
	 * Called when the an item on the table gets a mouse down event
	 * 
	 * @return false to disable text selection on some browsers
	 */
	public Boolean mouseDown(int row) {
		final ScheduleItemGWT item = mModel.get(row);
		
		// Set the text of the div that moves with the cursor
		Element dragDiv = DOM.getElementById(DragAndDropController.DRAGGED_ID);
		DOM.setInnerText(dragDiv, item.getCourseString());
		
		mDragController.onMouseDown(item, row, -1);
		return false;
	}

	/**
	 * Called when the any cell on the table gets a mouse up event
	 */
	public void mouseUp(int row) {
		mDragController.onDrop(row, -1);
	}
	
	/**
	 * Called when the any cell on the table gets a mouse up event
	 */
	public void mouseOver(int row) {
		if (mDragController.isDragging()) {
			Element tr = DOM.getElementById("list"+row);
			DOM.setStyleAttribute(tr, "backgroundColor", "#d1dfdf");
		}
	}
	
	/**
	 * Called when the any cell on the table gets a mouse up event
	 */
	public void mouseOut(int row) {
		Element cell = DOM.getElementById("list"+row);
		DOM.setStyleAttribute(cell, "backgroundColor", "#DFF0CF");
	}
	
	/**
	 * Used to register callback methods for access via handwritten javascript
	 */
	private native void defineTableCallbacks() /*-{
		var availableCourses = this;
		$wnd.listDoubleClick = function(row) {
			return availableCourses.@edu.calpoly.csc.scheduler.view.web.client.calendar.ScheduleItemListView::doubleClick(I)(row);
		}
		$wnd.listMouseDown = function(row) {
			return availableCourses.@edu.calpoly.csc.scheduler.view.web.client.calendar.ScheduleItemListView::mouseDown(I)(row);
		}
		$wnd.listMouseUp = function(row) {
			return availableCourses.@edu.calpoly.csc.scheduler.view.web.client.calendar.ScheduleItemListView::mouseUp(I)(row);
		}
		$wnd.listMouseOver = function(row) {
			return availableCourses.@edu.calpoly.csc.scheduler.view.web.client.calendar.ScheduleItemListView::mouseOver(I)(row);
		}
		$wnd.listMouseOut = function(row) {
			return availableCourses.@edu.calpoly.csc.scheduler.view.web.client.calendar.ScheduleItemListView::mouseOut(I)(row);
		}
    }-*/;
}
