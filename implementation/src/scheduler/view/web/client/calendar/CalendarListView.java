package scheduler.view.web.client.calendar;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

import scheduler.view.web.shared.DayGWT;
import scheduler.view.web.shared.ScheduleItemGWT;

public class CalendarListView extends SimplePanel {

	private List<ScheduleItemGWT> mScheduleItems;
	private List<ScheduleItemGWT> mFilteredScheduleItems;
	private final ScheduleEditWidget mScheduleController;
	private String mInnerHTML;
	private int mLeftOffset;
	private int mLastRowSelected = -1;
	private static final int KEYCODE_DELETE = 46;
	private static final int COLUMN_COUNT = 11;

	public CalendarListView(ScheduleEditWidget scheduleController) {
		mScheduleController = scheduleController;

		defineTableCallbacks();
	}

	public void setLeftOffset(int pixels) {
		mLeftOffset = pixels + 1;
		DOMUtility.setStyleAttribute("ListTableContainer", "left", (mLeftOffset+1)+"px");
	}

	private void setTopOffset(int pixels) {
		DOMUtility.setStyleAttribute("ListTableContainer", "top", pixels+"px");
	}

	/**
	 * Used to register callback methods for access via handwritten JavaScript
	 */
	private native void defineTableCallbacks() /*-{
		var scheduleTable = this;
		$wnd.calendarListDoubleClick = function(row, col) {
			return scheduleTable.@scheduler.view.web.client.calendar.CalendarListView::doubleClick(II)(row, col);
		}
		$wnd.calendarListMouseDown = function(row, col) {
			return scheduleTable.@scheduler.view.web.client.calendar.CalendarListView::mouseDown(II)(row, col);
		}
		$wnd.calendarListMouseUp = function(row, col) {
			return scheduleTable.@scheduler.view.web.client.calendar.CalendarListView::mouseUp(II)(row, col);
		}
		$wnd.calendarListMouseOver = function(row) {
			return scheduleTable.@scheduler.view.web.client.calendar.CalendarListView::mouseOver(I)(row);
		}
		$wnd.calendarListMouseOut = function(row) {
			return scheduleTable.@scheduler.view.web.client.calendar.CalendarListView::mouseOut(I)(row);
		}
		$wnd.calendarListKeyDown = function(row, keycode) {
			return scheduleTable.@scheduler.view.web.client.calendar.CalendarListView::keyDown(II)(row, keycode);
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
		builder.append("<div id=\"ListTableContainer\" onscroll=\"tableContainerScroll()\">");
		builder.append("<table id=\"ListTable\"><tr id=\"headerRow\">");

		// Add column header
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
		for (ScheduleItemGWT item : mFilteredScheduleItems) {
			int tableCol = 0;
			builder.append("<tr>");
			
			builder.append("<td " + "class=\"item\" id=\"x" + tableCol + "y"
					+ tableRow + "\" "
					+ "ondblclick=\"calendarListDoubleClick(" + tableRow + ","
					+ tableCol + ")\" "
					+ "onmousedown=\"calendarListMouseDown(" + tableRow + ","
					+ tableCol + ")\" " + "onmouseup=\"calendarListMouseUp("
					+ tableRow + "," + tableCol + ")\" " + "tabindex=\"0\" "
					+ "onkeydown=\"calendarListKeyDown(" + tableRow
					+ ",event.which)\" " + "onselectstart=\"return false\" "
					+ ">"
					+ mScheduleController.getCourseString(item.getCourseID())
					+ "</td>");
			tableCol++;
			builder.append("<td " + "class=\"item\" id=\"x" + tableCol + "y"
					+ tableRow + "\" "
					+ "ondblclick=\"calendarListDoubleClick(" + tableRow + ","
					+ tableCol + ")\" "
					+ "onmousedown=\"calendarListMouseDown(" + tableRow + ","
					+ tableCol + ")\" " + "onmouseup=\"calendarListMouseUp("
					+ tableRow + "," + tableCol + ")\" " + "tabindex=\"0\" "
					+ "onkeydown=\"calendarListKeyDown(" + tableRow
					+ ",event.which)\" " + "onselectstart=\"return false\" "
					+ ">" + item.getSection() + "</td>");
			tableCol++;

			builder.append("<td " + "class=\"item\" id=\"x" + tableCol + "y"
					+ tableRow + "\" "
					+ "ondblclick=\"calendarListDoubleClick(" + tableRow + ","
					+ tableCol + ")\" "
					+ "onmousedown=\"calendarListMouseDown(" + tableRow + ","
					+ tableCol + ")\" " + "onmouseup=\"calendarListMouseUp("
					+ tableRow + "," + tableCol + ")\" " + "tabindex=\"0\" "
					+ "onkeydown=\"calendarListKeyDown(" + tableRow
					+ ",event.which)\" " + "onselectstart=\"return false\" "
					+ ">" + mScheduleController.getCourse(item.getCourseID()).getType() + "</td>");
			tableCol++;

			builder.append("<td " + "class=\"item\" id=\"x" + tableCol + "y"
					+ tableRow + "\" "
					+ "ondblclick=\"calendarListDoubleClick(" + tableRow + ","
					+ tableCol + ")\" "
					+ "onmousedown=\"calendarListMouseDown(" + tableRow + ","
					+ tableCol + ")\" " + "onmouseup=\"calendarListMouseUp("
					+ tableRow + "," + tableCol + ")\" " + "tabindex=\"0\" "
					+ "onkeydown=\"calendarListKeyDown(" + tableRow
					+ ",event.which)\" " + "onselectstart=\"return false\" "
					+ ">" + mScheduleController.getCourse(item.getCourseID()).getScu() + "</td>");
			tableCol++;

			builder.append("<td " + "class=\"item\" id=\"x" + tableCol + "y"
					+ tableRow + "\" "
					+ "ondblclick=\"calendarListDoubleClick(" + tableRow + ","
					+ tableCol + ")\" "
					+ "onmousedown=\"calendarListMouseDown(" + tableRow + ","
					+ tableCol + ")\" " + "onmouseup=\"calendarListMouseUp("
					+ tableRow + "," + tableCol + ")\" " + "tabindex=\"0\" "
					+ "onkeydown=\"calendarListKeyDown(" + tableRow
					+ ",event.which)\" " + "onselectstart=\"return false\" "
					+ ">" + mScheduleController.getCourse(item.getCourseID()).getWtu() + "</td>");
			tableCol++;

			
			builder.append("<td " + "class=\"item\" id=\"x" + tableCol + "y"
					+ tableRow + "\" "
					+ "ondblclick=\"calendarListDoubleClick(" + tableRow + ","
					+ tableCol + ")\" "
					+ "onmousedown=\"calendarListMouseDown(" + tableRow + ","
					+ tableCol + ")\" " + "onmouseup=\"calendarListMouseUp("
					+ tableRow + "," + tableCol + ")\" " + "tabindex=\"0\" "
					+ "onkeydown=\"calendarListKeyDown(" + tableRow
					+ ",event.which)\" " + "onselectstart=\"return false\" "
					+ ">");
			
			if (mScheduleController.getInstructor(item.getInstructorID()) != null ) {
				builder.append(mScheduleController.getInstructor(item.getInstructorID()).getLastName());
			}			
			else {
				builder.append("TBA");
			}
			
			builder.append("</td>");
			tableCol++;

			builder.append("<td " + "class=\"item\" id=\"x" + tableCol + "y"
					+ tableRow + "\" "
					+ "ondblclick=\"calendarListDoubleClick(" + tableRow + ","
					+ tableCol + ")\" "
					+ "onmousedown=\"calendarListMouseDown(" + tableRow + ","
					+ tableCol + ")\" " + "onmouseup=\"calendarListMouseUp("
					+ tableRow + "," + tableCol + ")\" " + "tabindex=\"0\" "
					+ "onkeydown=\"calendarListKeyDown(" + tableRow
					+ ",event.which)\" " + "onselectstart=\"return false\" "
					+ ">");
			
			if (mScheduleController.getLocation(item.getLocationID()) != null) {
				builder.append(mScheduleController.getLocation(item.getLocationID()).getRoom()); 
			}
			else {
				builder.append("TBA");
			}
			
			builder.append("</td>");
			tableCol++;

			builder.append("<td " + "class=\"item\" id=\"x" + tableCol + "y"
					+ tableRow + "\" "
					+ "ondblclick=\"calendarListDoubleClick(" + tableRow + ","
					+ tableCol + ")\" "
					+ "onmousedown=\"calendarListMouseDown(" + tableRow + ","
					+ tableCol + ")\" " + "onmouseup=\"calendarListMouseUp("
					+ tableRow + "," + tableCol + ")\" " + "tabindex=\"0\" "
					+ "onkeydown=\"calendarListKeyDown(" + tableRow
					+ ",event.which)\" " + "onselectstart=\"return false\" "
					+ ">" + GetDaysString(item.getDays()) + "</td>");
			tableCol++;

			builder.append("<td " + "class=\"item\" id=\"x" + tableCol + "y"
					+ tableRow + "\" "
					+ "ondblclick=\"calendarListDoubleClick(" + tableRow + ","
					+ tableCol + ")\" "
					+ "onmousedown=\"calendarListMouseDown(" + tableRow + ","
					+ tableCol + ")\" " + "onmouseup=\"calendarListMouseUp("
					+ tableRow + "," + tableCol + ")\" " + "tabindex=\"0\" "
					+ "onkeydown=\"calendarListKeyDown(" + tableRow
					+ ",event.which)\" " + "onselectstart=\"return false\" "
					+ ">");
			
			if (CalendarTableView.getStartRow(item) >= 99) {
			   builder.append(ScheduleEditWidget.START_TIMES[CalendarTableView.getStartRow(item)]); 
			}
			
			builder.append("</td>");
			
			tableCol++;

			builder.append("<td " + "class=\"item\" id=\"x" + tableCol + "y"
					+ tableRow + "\" "
					+ "ondblclick=\"calendarListDoubleClick(" + tableRow + ","
					+ tableCol + ")\" "
					+ "onmousedown=\"calendarListMouseDown(" + tableRow + ","
					+ tableCol + ")\" " + "onmouseup=\"calendarListMouseUp("
					+ tableRow + "," + tableCol + ")\" " + "tabindex=\"0\" "
					+ "onkeydown=\"calendarListKeyDown(" + tableRow
					+ ",event.which)\" " + "onselectstart=\"return false\" "
					+ ">");
									
			if (CalendarTableView.getEndRow(item) + 1 >= 0) {
				builder.append(ScheduleEditWidget.END_TIMES[CalendarTableView.getEndRow(item) + 1]); 
			}
			
			builder.append("</td>");
			tableCol++;

			builder.append("<td " + "class=\"item\" id=\"x" + tableCol + "y"
					+ tableRow + "\" "
					+ "ondblclick=\"calendarListDoubleClick(" + tableRow + ","
					+ tableCol + ")\" "
					+ "onmousedown=\"calendarListMouseDown(" + tableRow + ","
					+ tableCol + ")\" " + "onmouseup=\"calendarListMouseUp("
					+ tableRow + "," + tableCol + ")\" " + "tabindex=\"0\" "
					+ "onkeydown=\"calendarListKeyDown(" + tableRow
					+ ",event.which)\" " + "onselectstart=\"return false\" "
					+ ">" + mScheduleController.getCourse(item.getCourseID()).getMaxEnroll() + "</td>");
			tableCol++;

			builder.append("</tr>");
			//builder.append("<tr height=0><td class=\"rowSpacer\" colspan=\"12\"></td></tr>");
			tableRow++;
		}

		builder.append("</table>");
		builder.append("</div>");

		mInnerHTML = builder.toString();
		setHTML(mInnerHTML);
		setLeftOffset(mLeftOffset);
		setTopOffset(mScheduleController.getWidget().getAbsoluteTop());
	}

	private String GetDaysString(Set<DayGWT> days) {
		String returnString = new String();
		Iterator<DayGWT> it = days.iterator();

		while (it.hasNext()) {
			String current = it.next().toString();
			if (current != null && current.compareTo("") != 0) {
				if (current.compareTo("THURSDAY") == 0) {
					returnString += "Tr";
				} else if (current.compareTo("SUNDAY") == 0) {
					returnString += "Su";
				} else {
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
		highlightRow(row);
		final ScheduleItemGWT item = mFilteredScheduleItems.get(row);

		mScheduleController.editItem(false, item, null, -1);
	}

	/**
	 * Called when the an item on the table gets a mouse down event
	 * 
	 * @return false to disable text selection on some browsers
	 */
	public Boolean mouseDown(int row, int col) {
		highlightRow(row);

		return false;
	}

	/**
	 * Called when a key is pressed down on an element
	 * 
	 */
	public void keyDown(int row, int keycode) {
		if (keycode == KEYCODE_DELETE) {
			mScheduleController.removeItem(mScheduleItems.get(row));
			mScheduleItems.remove(row);
			applyFilters();
			mLastRowSelected = -1;
//			this.drawList();
		}
	}

	private void highlightRow(int row) {
		if (row != mLastRowSelected) {
			for (int i = 0; i < COLUMN_COUNT; i++) {
				Element selectedCell = DOM.getElementById("x" + i + "y" + row);
				if (selectedCell != null) {
					selectedCell.addClassName("selectedItem");
				}

				// un-highlight old row
				Element oldCell = DOM.getElementById("x" + i + "y"
						+ mLastRowSelected);
				if (oldCell != null) {
					oldCell.removeClassName("selectedItem");
				}
			}

			mLastRowSelected = row;
		}		
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
