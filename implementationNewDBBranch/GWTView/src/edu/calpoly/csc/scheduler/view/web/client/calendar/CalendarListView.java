package edu.calpoly.csc.scheduler.view.web.client.calendar;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

import edu.calpoly.csc.scheduler.view.web.shared.DayGWT;
import edu.calpoly.csc.scheduler.view.web.shared.ScheduleItemGWT;

public class CalendarListView extends SimplePanel {

	private List<ScheduleItemGWT> mScheduleItems;
	private List<ScheduleItemGWT> mFilteredScheduleItems;
	private final ScheduleEditWidget mScheduleController;
	private String mInnerHTML;
	private int mLeftOffset;

	public CalendarListView(ScheduleEditWidget scheduleController) {
		mScheduleController = scheduleController;

		defineTableCallbacks();
	}

	public void setLeftOffset(int pixels) {
		mLeftOffset = pixels + 1;
		DOMUtility.setStyleAttribute("ListTableContainer", "left", mLeftOffset + "px");
	}

	/**
	 * Used to register callback methods for access via handwritten javascript
	 */
	private native void defineTableCallbacks() /*-{
		var scheduleTable = this;
		$wnd.calendarListDoubleClick = function(row, col) {
			return scheduleTable.@edu.calpoly.csc.scheduler.view.web.client.calendar.CalendarListView::doubleClick(II)(row, col);
		}
		$wnd.calendarListMouseDown = function(row, col) {
			return scheduleTable.@edu.calpoly.csc.scheduler.view.web.client.calendar.CalendarListView::mouseDown(II)(row, col);
		}
		$wnd.calendarListMouseUp = function(row, col) {
			return scheduleTable.@edu.calpoly.csc.scheduler.view.web.client.calendar.CalendarListView::mouseUp(II)(row, col);
		}
		$wnd.calendarListMouseOver = function(row) {
			return scheduleTable.@edu.calpoly.csc.scheduler.view.web.client.calendar.CalendarListView::mouseOver(I)(row);
		}
		$wnd.calendarListMouseOut = function(row) {
			return scheduleTable.@edu.calpoly.csc.scheduler.view.web.client.calendar.CalendarListView::mouseOut(I)(row);
		}

	}-*/;

	public void setScheduleItems(List<ScheduleItemGWT> items) {
		mScheduleItems = items;
		applyFilters();
	}

	private void applyFilters() {
		// TODO implement filtering
		mFilteredScheduleItems = mScheduleItems;
	}

	public void drawList() {
		clear();

		final StringBuilder builder = new StringBuilder();		
		builder.append("<style type=\"text/css\">"
				+ "* {-webkit-user-select:none;-moz-user-select:none;}"
				+ "#ListTableContainer {position:absolute;top:116px;left:"
				+ mLeftOffset
				+ "px;right:0px;bottom:33px;overflow:auto;background-color:#FFFFFF;}"
				+ "#ListTable {border-spacing:0px;cellspacing:0px;border:none;}"
				+ "#ListTable tr {height:20px;}"
				+ "#ListTable td {overflow:hidden;padding:4px;border-top:1px solid #d1dfdf;}"
				+ "#ListTable td.item {text-align:center;border:1px solid black;}"
				+ "#ListTable td.columnHeader {position:relative;background-color:#edf2f2;border-right:1px solid #000000;border-bottom:1px solid #000000;font-weight:bold;text-align:center;z-index:2;}"
				+ "#ListTable td.timeHeader {position:relative;background-color:#edf2f2;border-right:1px solid #000000;white-space:nowrap;text-align:right;}"
				+ "#ListTable td#topCorner {border-bottom:1px solid #000000;background-color:#edf2f2;}"
				+ "#ListTable td.daySpacer {border-right:1px solid #000000;padding:0px;margin:0px;width:0px;}"
				+ "</style>");
		
		builder.append("<div id=\"ListTableContainer\" onscroll=\"tableContainerScroll()\">");
		builder.append("<table id=\"ListTable\"><tr id=\"headerRow\">");

		// Add column headers
		// builder.append("<td class=\"columnHeader\" id='h'>Department</td>");
		builder.append("<td class=\"columnHeader\" id='h'>Course Number</td>");
		builder.append("<td class=\"columnHeader\" id='h'>Section Number</td>");
		builder.append("<td class=\"columnHeader\" id='h'>Type</td>");

		builder.append("<td class=\"columnHeader\" id='h'>SCU</td>");
		builder.append("<td class=\"columnHeader\" id='h'>WTU</td>");
		builder.append("<td class=\"columnHeader\" id='h'>Instructor</td>");
		builder.append("<td class=\"columnHeader\" id='h'>Building</td>");

		builder.append("<td class=\"columnHeader\" id='h'>Days</td>");
		builder.append("<td class=\"columnHeader\" id='h'>Start Time</td>");
		builder.append("<td class=\"columnHeader\" id='h'>End Time</td>");
		builder.append("<td class=\"columnHeader\" id='h'>Capacity</td>");

		builder.append("</tr>");

		int tableRow = 0;
		for (ScheduleItemGWT item : mScheduleItems) {
			int tableCol = 0;
			builder.append("<tr>");

			builder.append("<td " +
								"class=\"item\" id=\"x"+tableCol+"y"+tableRow+"\" " +
								"ondblclick=\"calendarListDoubleClick("+tableRow+","+tableCol+")\" " +
								"onmousedown=\"calendarListMouseDown("+tableRow+","+tableCol+")\" " +
								"onmouseup=\"calendarListMouseUp("+tableRow+","+tableCol+")\" " +
								"onselectstart=\"return false\" " +
								">" + mScheduleController.getCourseString(item.getCourseID())	+ "</td>");
			tableCol++;
			
			builder.append("<td " +
					"class=\"item\" id=\"x"+tableCol+"y"+tableRow+"\" " +
					"ondblclick=\"calendarListDoubleClick("+tableRow+","+tableCol+")\" " +
					"onmousedown=\"calendarListMouseDown("+tableRow+","+tableCol+")\" " +
					"onmouseup=\"calendarListMouseUp("+tableRow+","+tableCol+")\" " +
					"onselectstart=\"return false\" " +
					">" + item.getSection() + "</td>");
			tableCol++;
			
			builder.append("<td " +
					"class=\"item\" id=\"x"+tableCol+"y"+tableRow+"\" " +
					"ondblclick=\"calendarListDoubleClick("+tableRow+","+tableCol+")\" " +
					"onmousedown=\"calendarListMouseDown("+tableRow+","+tableCol+")\" " +
					"onmouseup=\"calendarListMouseUp("+tableRow+","+tableCol+")\" " +
					"onselectstart=\"return false\" " +
					">" + "Course Type Here" + "</td>");
			tableCol++;
			
			builder.append("<td " +
					"class=\"item\" id=\"x"+tableCol+"y"+tableRow+"\" " +
					"ondblclick=\"calendarListDoubleClick("+tableRow+","+tableCol+")\" " +
					"onmousedown=\"calendarListMouseDown("+tableRow+","+tableCol+")\" " +
					"onmouseup=\"calendarListMouseUp("+tableRow+","+tableCol+")\" " +
					"onselectstart=\"return false\" " +
					">" + "Course SCU" + "</td>");
			tableCol++;
			
			builder.append("<td " +
					"class=\"item\" id=\"x"+tableCol+"y"+tableRow+"\" " +
					"ondblclick=\"calendarListDoubleClick("+tableRow+","+tableCol+")\" " +
					"onmousedown=\"calendarListMouseDown("+tableRow+","+tableCol+")\" " +
					"onmouseup=\"calendarListMouseUp("+tableRow+","+tableCol+")\" " +
					"onselectstart=\"return false\" " +
					">" + "Course WTU" + "</td>");
			tableCol++;
			
			builder.append("<td " +
					"class=\"item\" id=\"x"+tableCol+"y"+tableRow+"\" " +
					"ondblclick=\"calendarListDoubleClick("+tableRow+","+tableCol+")\" " +
					"onmousedown=\"calendarListMouseDown("+tableRow+","+tableCol+")\" " +
					"onmouseup=\"calendarListMouseUp("+tableRow+","+tableCol+")\" " +
					"onselectstart=\"return false\" " +
					">" + "Course Instructor" + "</td>");
			tableCol++;
			
			builder.append("<td " +
					"class=\"item\" id=\"x"+tableCol+"y"+tableRow+"\" " +
					"ondblclick=\"calendarListDoubleClick("+tableRow+","+tableCol+")\" " +
					"onmousedown=\"calendarListMouseDown("+tableRow+","+tableCol+")\" " +
					"onmouseup=\"calendarListMouseUp("+tableRow+","+tableCol+")\" " +
					"onselectstart=\"return false\" " +
					">" + item.getLocationID() + "</td>");
			tableCol++;
			
			builder.append("<td " +
					"class=\"item\" id=\"x"+tableCol+"y"+tableRow+"\" " +
					"ondblclick=\"calendarListDoubleClick("+tableRow+","+tableCol+")\" " +
					"onmousedown=\"calendarListMouseDown("+tableRow+","+tableCol+")\" " +
					"onmouseup=\"calendarListMouseUp("+tableRow+","+tableCol+")\" " +
					"onselectstart=\"return false\" " +
					">" + GetDaysString(item.getDays()) + "</td>");
			tableCol++;
			
			builder.append("<td " +
					"class=\"item\" id=\"x"+tableCol+"y"+tableRow+"\" " +
					"ondblclick=\"calendarListDoubleClick("+tableRow+","+tableCol+")\" " +
					"onmousedown=\"calendarListMouseDown("+tableRow+","+tableCol+")\" " +
					"onmouseup=\"calendarListMouseUp("+tableRow+","+tableCol+")\" " +
					"onselectstart=\"return false\" " +
					">" + "Start Time" + "</td>");
			tableCol++;
			
			builder.append("<td " +
					"class=\"item\" id=\"x"+tableCol+"y"+tableRow+"\" " +
					"ondblclick=\"calendarListDoubleClick("+tableRow+","+tableCol+")\" " +
					"onmousedown=\"calendarListMouseDown("+tableRow+","+tableCol+")\" " +
					"onmouseup=\"calendarListMouseUp("+tableRow+","+tableCol+")\" " +
					"onselectstart=\"return false\" " +
					">" + "End Time" + "</td>");
			tableCol++;
			
			builder.append("<td " +
					"class=\"item\" id=\"x"+tableCol+"y"+tableRow+"\" " +
					"ondblclick=\"calendarListDoubleClick("+tableRow+","+tableCol+")\" " +
					"onmousedown=\"calendarListMouseDown("+tableRow+","+tableCol+")\" " +
					"onmouseup=\"calendarListMouseUp("+tableRow+","+tableCol+")\" " +
					"onselectstart=\"return false\" " +
					">" + "Capacity" + "</td>");
			tableCol++;			

			builder.append("</tr>");
			tableRow++;
		}

		builder.append("</table>");
		builder.append("</div>");

		mInnerHTML = builder.toString();
		setHTML(mInnerHTML);
	}

	private String GetDaysString(Set<DayGWT> days) {
		String returnString = new String();
		Iterator<DayGWT> it = days.iterator();
		
		while (it.hasNext()) {
			String current = it.next().toString();
			if (current != null && current.compareTo("") != 0) {
				if (current.compareTo("THURSDAY") == 0) {
					returnString += "TH";
				}
				else if (current.compareTo("SUNDAY") == 0) {
					returnString += "SU";
				}
				else {
					returnString += current.charAt(0) + "";
				}
			}
			 
		}
		
		return returnString;
	}

	private void setHTML(String html) {
		clear();
		add(new HTML(html));
	}

	/**
	 * Called when the user double clicks an item in the table
	 */
	public void doubleClick(int row, int col) {
		// final CalendarDayModel day = mModel.get(col);
		// final ScheduleItemGWT item = day.get(row).get(col - day.getOffset());
		final ScheduleItemGWT item = mScheduleItems.get(row);
		
		System.out.println("Row " + row + " double clicked on the list.");
		mScheduleController.editItem(false, item, null, -1);
	}

	/**
	 * Called when the an item on the table gets a mouse down event
	 * 
	 * @return false to disable text selection on some browsers
	 */
	public Boolean mouseDown(int row, int col) {
		// final CalendarDayModel day = mModel.get(col);
		// final ScheduleItemGWT item = day.get(row).get(col - day.getOffset());
		//
		// // Set the text of the div that moves with the cursor
		// Element dragDiv =
		// DOM.getElementById(DragAndDropController.DRAGGED_ID);
		// DOM.setInnerText(dragDiv,
		// mScheduleController.getCourseString(item.getCourseID()));
		//
		// mDragController.onMouseDown(item, row, col);
		Element selectedCell = DOM.getElementById("x"+col+"y"+row);
		
		return false;
	}

	/**
	 * Called when the any cell on the table gets a mouse up event
	 */
	public void mouseUp(int row, int col) {
		// if (mDragController.isDragging()) {
		// DOMUtility.setStyleAttribute("y"+row, "backgroundColor", "#FFFFFF");
		// DOMUtility.setStyleAttribute("h"+row, "backgroundColor", "#edf2f2");
		// }
		//
		// mDragController.onDrop(row, mModel.getDay(col));
	}

	/**
	 * Called when the any cell on the table gets a mouse up event
	 */
	public void mouseOver(int row) {
		// if (mDragController.isDragging()) {
		// DOMUtility.setStyleAttribute("y"+row, "backgroundColor", "#d1dfdf");
		// DOMUtility.setStyleAttribute("h"+row, "backgroundColor", "#d1dfdf");
		// }
	}

	/**
	 * Called when the any cell on the table gets a mouse up event
	 */
	public void mouseOut(int row) {
		DOMUtility.setStyleAttribute("y" + row, "backgroundColor", "#FFFFFF");
		DOMUtility.setStyleAttribute("h" + row, "backgroundColor", "#edf2f2");
	}

	public void clear() {
		Iterator<Widget> it = iterator();
		while (it.hasNext()) {
			it.next();
			it.remove();
		}
	}
}
