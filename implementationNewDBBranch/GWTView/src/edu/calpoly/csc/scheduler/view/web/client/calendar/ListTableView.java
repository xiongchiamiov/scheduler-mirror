package edu.calpoly.csc.scheduler.view.web.client.calendar;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

import edu.calpoly.csc.scheduler.view.web.client.calendar.CalendarModel;
import edu.calpoly.csc.scheduler.view.web.shared.DayGWT;
import edu.calpoly.csc.scheduler.view.web.shared.ScheduleItemGWT;

public class ListTableView extends SimplePanel {
	
	private List<ScheduleItemGWT> mScheduleItems;
	private List<ScheduleItemGWT> mFilteredScheduleItems;
	private final ScheduleEditWidget mScheduleController;
	private String mInnerHTML;
	private CalendarModel mModel;
	private int mLeftOffset;
	
	public ListTableView(ScheduleEditWidget scheduleController) {
		mModel = new CalendarModel();
		mScheduleController = scheduleController;
		
		defineTableCallbacks();
	}
	
	public void setLeftOffset(int pixels) {
		mLeftOffset = pixels + 1;
		DOMUtility.setStyleAttribute("ListTableContainer", "left", mLeftOffset+"px");
	}
	
	/**
	 * Used to register callback methods for access via handwritten javascript
	 */
	private native void defineTableCallbacks() /*-{
		var scheduleTable = this;
		$wnd.tableDoubleClick = function(row, col) {
			return scheduleTable.@edu.calpoly.csc.scheduler.view.web.client.calendar.ListTableView::doubleClick(II)(row, col);
		}
		$wnd.tableMouseDown = function(row, col) {
			return scheduleTable.@edu.calpoly.csc.scheduler.view.web.client.calendar.ListTableView::mouseDown(II)(row, col);
		}
		$wnd.tableMouseUp = function(row, col) {
			return scheduleTable.@edu.calpoly.csc.scheduler.view.web.client.calendar.ListTableView::mouseUp(II)(row, col);
		}
		$wnd.tableMouseOver = function(row) {
			return scheduleTable.@edu.calpoly.csc.scheduler.view.web.client.calendar.ListTableView::mouseOver(I)(row);
		}
		$wnd.tableMouseOut = function(row) {
			return scheduleTable.@edu.calpoly.csc.scheduler.view.web.client.calendar.ListTableView::mouseOut(I)(row);
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
		mModel = buildTableModel();
		
		final StringBuilder builder = new StringBuilder();
		builder.append("<style type=\"text/css\">"+
			"* {-webkit-user-select:none;-moz-user-select:none;}" +
			"#ListTableContainer {position:absolute;top:116px;left:"+mLeftOffset+"px;right:0px;bottom:33px;overflow:auto;background-color:#FFFFFF;}"+
			"#ListTable {border-spacing:0px;cellspacing:0px;border:none;}"+
			"#ListTable tr {height:20px;}"+
			"#ListTable td {overflow:hidden;padding:4px;border-top:1px solid #d1dfdf;}"+
			"#ListTable td.item {background-color:#DFF0CF;text-align:center;border:1px solid #FFFFFF;cursor:move;}"+
			"#ListTable td.dayHeader {position:relative;background-color:#edf2f2;border-right:1px solid #000000;border-bottom:1px solid #000000;font-weight:bold;text-align:center;z-index:2;}"+
			"#ListTable td.timeHeader {position:relative;background-color:#edf2f2;border-right:1px solid #000000;white-space:nowrap;text-align:right;}" +
			"#ListTable td#topCorner {border-bottom:1px solid #000000;background-color:#edf2f2;}"+
			"#ListTable td.daySpacer {border-right:1px solid #000000;padding:0px;margin:0px;width:0px;}"+			
			"</style>");
		builder.append("<div id=\"ListTableContainer\" onscroll=\"tableContainerScroll()\">");
		builder.append("<table id=\"ListTable\"><tr id=\"headerRow\">");
		
		// Add column headers
		//builder.append("<td class=\"dayHeader\" id='h'>Department</td>");
		builder.append("<td class=\"dayHeader\" id='h'>Course Number</td>");
		builder.append("<td class=\"dayHeader\" id='h'>Section Number</td>");
		builder.append("<td class=\"dayHeader\" id='h'>Type</td>");
		
		builder.append("<td class=\"dayHeader\" id='h'>SCU</td>");
		builder.append("<td class=\"dayHeader\" id='h'>WTU</td>");
		builder.append("<td class=\"dayHeader\" id='h'>Instructor</td>");
		builder.append("<td class=\"dayHeader\" id='h'>Building</td>");
		
		builder.append("<td class=\"dayHeader\" id='h'>Days</td>");
		builder.append("<td class=\"dayHeader\" id='h'>Start Time</td>");
		builder.append("<td class=\"dayHeader\" id='h'>End Time</td>");
		builder.append("<td class=\"dayHeader\" id='h'>Capacity</td>");
		
		builder.append("</tr>");
		
		for (ScheduleItemGWT item : mScheduleItems) {
			builder.append("<tr>");
			
			builder.append("<td>" + mScheduleController.getCourseString(item.getCourseID()) + "/<td>");
			builder.append("<td>" + item.getSection() + "</td>");
			builder.append("<td>" + "Course Type Here" + "</td>");
			builder.append("<td>" + "Course SCU" + "</td>");
			builder.append("<td>" + "Course WTU" + "</td>");
			builder.append("<td>" + "Course Instructor" + "</td>");
			builder.append("<td>" + item.getLocationID() + "</td>");
			
			builder.append("<td>" + item.getDays().toString() + "</td>");
			builder.append("<td>" + "Start Time" + "</td>");
			builder.append("<td>" + "End Time" + "</td>");
			builder.append("<td>" + "Capacity" + "</td>");
			
			builder.append("</tr>");
		}
//		// Add day headers
//		for (DayGWT day : DayGWT.values()) {
//			int colspan = mModel.get(day).getWidth() + 1; // +1 for day spacer cells (see loop below)
//			
//			builder.append("<td colspan="+colspan+" class=\"dayHeader\" id='h"+day.name+"'>"+day.name+"</td>");
//		}
//		
//		
//		// Fill in table
//		for (int rowNum = 0; rowNum < CalendarModel.END_TIMES.length; rowNum++) {
//			builder.append("<tr id=\"y"+rowNum+"\" " +
//				"onmouseover=\"tableMouseOver("+rowNum+")\" " +
//				"onmouseout=\"tableMouseOut("+rowNum+")\" " +
//				"><td class=\"timeHeader\" id=\"h"+rowNum+"\">"+ CalendarModel.END_TIMES[rowNum]+"</td>");
//			
//			for (DayGWT day : DayGWT.values()) {
//				final CalendarDayModel dayModel = mModel.get(day);
//				
//				for (int colNum = 0; colNum < dayModel.getWidth(); colNum++) {
//					final ScheduleItemGWT item = dayModel.get(rowNum).get(colNum);
//					final int tableRow = rowNum;
//					final int tableCol = colNum + dayModel.getOffset();
//					
//					if (item == null) {
//						builder.append("<td id=\"x"+tableCol+"y"+tableRow+"\"" +
//								"onmouseup=\"tableMouseUp("+tableRow+","+tableCol+")\" " +
//								"></td>");
//					}
//					else if (rowNum == getStartRow(item)) {
//						final int rowspan = getEndRow(item) - getStartRow(item) + 1;
//						
//						builder.append("<td " +
//								"rowspan="+rowspan+" " +
//								"class=\"item\" id=\"x"+tableCol+"y"+tableRow+"\" " +
//								"ondblclick=\"tableDoubleClick("+tableRow+","+tableCol+")\" " +
//								"onmousedown=\"tableMouseDown("+tableRow+","+tableCol+")\" " +
//								"onmouseup=\"tableMouseUp("+tableRow+","+tableCol+")\" " +
//								"onselectstart=\"return false\" " +
//								">"+mScheduleController.getCourseString(item.getCourseID())+"</td>");
//					}
//				}
//				
//				builder.append("<td class=\"daySpacer\"></td>");
//			}
//			
//			builder.append("</tr>");
//		}
		builder.append("</table>");
		builder.append("</div>");
		
		mInnerHTML = builder.toString();
		setHTML(mInnerHTML);
	}
	
	private void setHTML(String html) {
		clear();
		add(new HTML(html));
	}
	
	/**
	 * Lays out a table model from the list of filtered scheduled items
	 */
	private CalendarModel buildTableModel() {
		// TODO link schedule item between its' days in model so you can 
		// highlight them item on all of it's days when it's selected on the UI
		
		CalendarModel model = new CalendarModel();
		
		for (ScheduleItemGWT item : mFilteredScheduleItems) {
			for (DayGWT day : item.getDays()) {
				CalendarDayModel dayModel = model.get(day);
				
				// Find the leftmost column that is open on all rows that this item needs to occupy
//				int colNdx = 0;
//				for (; colNdx < dayModel.getWidth(); colNdx++) {
//					boolean occupied = false;
//					
//					for (int rowNdx = getStartRow(item); rowNdx <= getEndRow(item); rowNdx++) {
//						CalendarRowModel row = dayModel.get(rowNdx);
//						
//						if (row.get(colNdx) != null) {
//							occupied = true;
//							break;
//						}
//					}
//					
//					if (!occupied)
//						break;
//				}
//				
//				// Check it we need to increase the width of this day
//				if (colNdx >= dayModel.getWidth()) {
//					dayModel.setWidth(colNdx + 1);
//				}
//				
//				int startRow = getStartRow(item);
//				int endRow = getEndRow(item);
//				if (startRow > endRow)
//					throw new RuntimeException("lol ur dum");
//				
//				// Add the item to the column we picked on all of the item's rows
//				for (int rowNdx = startRow; rowNdx <= endRow; rowNdx++) {
//					CalendarRowModel row = dayModel.get(rowNdx);
//					
//					if (colNdx >= row.size()) {
//						row.setSize(colNdx + 1);
//					}
//					row.set(colNdx, item);
//				}
			}
		}
		
		// Calculate all of the day offsets (the column each day starts on)
		for (DayGWT day : Arrays.asList(DayGWT.values()).subList(1, DayGWT.values().length - 1)) {
			DayGWT prevDay = DayGWT.values()[day.ordinal() - 1];
			int prevOffset = model.get(prevDay).getOffset();
			int prevWidth = model.get(prevDay).getWidth();
			model.get(day).setOffset(prevOffset + prevWidth);
		}
		
		return model;
	}
	
	/**
	 * Called when the user double clicks an item in the table
	 */
	public void doubleClick(int row, int col) {
		final CalendarDayModel day = mModel.get(col);
		final ScheduleItemGWT item = day.get(row).get(col - day.getOffset());

		mScheduleController.editItem(false, item, null, -1);
	}
	
	/**
	 * Called when the an item on the table gets a mouse down event
	 * 
	 * @return false to disable text selection on some browsers
	 */
	public Boolean mouseDown(int row, int col) {
//		final CalendarDayModel day = mModel.get(col);
//		final ScheduleItemGWT item = day.get(row).get(col - day.getOffset());
//		
//		// Set the text of the div that moves with the cursor
//		Element dragDiv = DOM.getElementById(DragAndDropController.DRAGGED_ID);
//		DOM.setInnerText(dragDiv, mScheduleController.getCourseString(item.getCourseID()));
//		
//		mDragController.onMouseDown(item, row, col);
		return false;
	}

	/**
	 * Called when the any cell on the table gets a mouse up event
	 */
	public void mouseUp(int row, int col) {
//		if (mDragController.isDragging()) {
//			DOMUtility.setStyleAttribute("y"+row, "backgroundColor", "#FFFFFF");
//			DOMUtility.setStyleAttribute("h"+row, "backgroundColor", "#edf2f2");
//		}
//		
//		mDragController.onDrop(row, mModel.getDay(col));
	}
	
	/**
	 * Called when the any cell on the table gets a mouse up event
	 */
	public void mouseOver(int row) {
//		if (mDragController.isDragging()) {
//			DOMUtility.setStyleAttribute("y"+row, "backgroundColor", "#d1dfdf");
//			DOMUtility.setStyleAttribute("h"+row, "backgroundColor", "#d1dfdf");
//		}
	}
	
	/**
	 * Called when the any cell on the table gets a mouse up event
	 */
	public void mouseOut(int row) {
		DOMUtility.setStyleAttribute("y"+row, "backgroundColor", "#FFFFFF");
		DOMUtility.setStyleAttribute("h"+row, "backgroundColor", "#edf2f2");
	}

	public void clear() {
	    Iterator<Widget> it = iterator();
	    while (it.hasNext()) {
	      it.next();
	      it.remove();
	    }
	  }
}
