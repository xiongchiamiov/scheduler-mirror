package scheduler.view.web.client.calendar;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import scheduler.view.web.shared.DayGWT;
import scheduler.view.web.shared.ScheduleItemGWT;
import scheduler.view.web.shared.WeekGWT;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * This class encapsulates both the view for the schedule in list form. 
 * 
 * @author Tyler Yero, Matt Schirle
 */
public class CalendarListView extends SimplePanel {
	
	private class scheduleItemComparator implements Comparator<ScheduleItemGWT> {

		@Override
		public int compare(ScheduleItemGWT arg0, ScheduleItemGWT arg1) {
			switch(mCurrentSort) {
				case COURSE_NAME_ASC:
					return safeCompareTo(mScheduleController.getCourseString(arg0.getCourseID()),
							mScheduleController.getCourseString(arg1.getCourseID()));

				case COURSE_NAME_DESC:
					return safeCompareTo(mScheduleController.getCourseString(arg1.getCourseID()),
							mScheduleController.getCourseString(arg0.getCourseID()));
					
				case SECTION_NUMBER_ASC:
					return arg1.getSection() - arg0.getSection();
					
				case SECTION_NUMBER_DESC:
					return arg0.getSection() - arg1.getSection();
					
				case INSTRUCTOR_ASC:
					return safeCompareTo(mScheduleController.getInstructor(arg0.getInstructorID()).getName(),
							mScheduleController.getInstructor(arg1.getInstructorID()).getName());

				case INSTRUCTOR_DESC:
					return safeCompareTo(mScheduleController.getInstructor(arg1.getInstructorID()).getName(),
							mScheduleController.getInstructor(arg0.getInstructorID()).getName());
					
				case DAYS_ASC:
					return safeCompareTo(getDaysString(arg0.getDays()), getDaysString(arg1.getDays()));
					
				case DAYS_DESC:
					return safeCompareTo(getDaysString(arg1.getDays()), getDaysString(arg0.getDays()));
					
				case START_TIME_ASC:
					return arg0.getStartHalfHour() - arg1.getStartHalfHour();
					
				case START_TIME_DESC:
					return arg1.getStartHalfHour() - arg0.getStartHalfHour();
					
				case END_TIME_ASC:
					return arg0.getEndHalfHour() - arg1.getEndHalfHour();
					
				case END_TIME_DESC:
					return arg1.getEndHalfHour() - arg0.getEndHalfHour();
			}
			return 0;
		}
	    
		/**
		 * Compares two Objects without throwing NullPointerExceptions
		 */
		private <T> int safeCompareTo(Comparable<T> left, T right) {
			if (left == null) {
				if (right == null)
					return 0;
				else
					return -1;
			}
			else if (right == null)
				return 1;
			
			return left.compareTo(right);
		}
	}

	private static enum columnIndices {
		COURSE_NAME,
		SECTION_NUMBER,
		INSTRUCTOR,
		DAYS,
		START_TIME,
		END_TIME
	}
	
	private static enum sortBy {
		NONE,
		COURSE_NAME_ASC,
		COURSE_NAME_DESC,
		SECTION_NUMBER_ASC,
		SECTION_NUMBER_DESC,
		INSTRUCTOR_ASC,
		INSTRUCTOR_DESC,
		DAYS_ASC,
		DAYS_DESC,
		START_TIME_ASC,
		START_TIME_DESC,
		END_TIME_ASC,
		END_TIME_DESC
	}
	
	private List<ScheduleItemGWT> mScheduleItems;
	private final ScheduleEditWidget mScheduleController;
	private String mInnerHTML;
	private int mLeftOffset;
	private int mLastRowSelected = -1;
	private sortBy mCurrentSort = sortBy.NONE;
	private static final int KEYCODE_DELETE = 46;
	private static final int COLUMN_COUNT = 11;

	public CalendarListView(ScheduleEditWidget scheduleController) {
		mScheduleController = scheduleController;

		defineTableCallbacks();
	}

	public void setLeftOffset(int pixels) {
		mLeftOffset = pixels;
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
		$wnd.calendarListSort = function(col) {
			return scheduleTable.@scheduler.view.web.client.calendar.CalendarListView::setSort(I)(col);
		}
	}-*/;

	/**
	 * Set the schedule items of the list to be displayed.
	 * 
	 * @param items List of schedule items to put into the list. This replaces the current items.
	 */
	public void setScheduleItems(List<ScheduleItemGWT> items) {
		mScheduleItems = items;
		sort();
	}
	
	/**
	 * Clears the current view and draws the list using the current set of schedule items.
	 * 
	 */
	public void drawList() {
		clear();
		
		final StringBuilder builder = new StringBuilder();
		builder.append("<div id=\"ListTableContainer\" onscroll=\"tableContainerScroll()\">");
		builder.append("<table id=\"ListTable\"><tr id=\"headerRow\">");

		// Add column header
		builder.append("<td class=\"columnHeader\" id='h'");
		builder.append("onclick=\"calendarListSort(").append(columnIndices.COURSE_NAME.ordinal()).append(")\"");
		builder.append(">Course");
		if (mCurrentSort == sortBy.COURSE_NAME_ASC)
			builder.append(" &#9660;");
		else if (mCurrentSort == sortBy.COURSE_NAME_DESC)
			builder.append(" &#9650;");
		builder.append("</td>");

		builder.append("<td class=\"columnHeader\" id='h'");
		builder.append("onclick=\"calendarListSort(").append(columnIndices.SECTION_NUMBER.ordinal()).append(")\"");
		builder.append(">Section Number");
		if (mCurrentSort == sortBy.SECTION_NUMBER_ASC)
			builder.append(" &#9660;");
		else if (mCurrentSort == sortBy.SECTION_NUMBER_DESC)
			builder.append(" &#9650;");
		builder.append("</td>");
		
		builder.append("<td class=\"columnHeader\" id='h'>Type</td>");

		builder.append("<td class=\"columnHeader\" id='h'>SCU</td>");
		
		builder.append("<td class=\"columnHeader\" id='h'>WTU</td>");

		builder.append("<td class=\"columnHeader\" id='h'");
		builder.append("onclick=\"calendarListSort(").append(columnIndices.INSTRUCTOR.ordinal()).append(")\"");
		builder.append(">Instructor");
		if (mCurrentSort == sortBy.INSTRUCTOR_ASC)
			builder.append(" &#9660;");
		else if (mCurrentSort == sortBy.INSTRUCTOR_DESC)
			builder.append(" &#9650;");
		builder.append("</td>");
		
		builder.append("<td class=\"columnHeader\" id='h'>Location</td>");

		builder.append("<td class=\"columnHeader\" id='h'");
		builder.append("onclick=\"calendarListSort(").append(columnIndices.DAYS.ordinal()).append(")\"");
		builder.append(">Days");
		if (mCurrentSort == sortBy.DAYS_ASC)
			builder.append(" &#9660;");
		else if (mCurrentSort == sortBy.DAYS_DESC)
			builder.append(" &#9650;");
		builder.append("</td>");

		builder.append("<td class=\"columnHeader\" id='h'");
		builder.append("onclick=\"calendarListSort(").append(columnIndices.START_TIME.ordinal()).append(")\"");
		builder.append(">Start Time");
		if (mCurrentSort == sortBy.START_TIME_ASC)
			builder.append(" &#9660;");
		else if (mCurrentSort == sortBy.START_TIME_DESC)
			builder.append(" &#9650;");
		builder.append("</td>");

		builder.append("<td class=\"columnHeader\" id='h'");
		builder.append("onclick=\"calendarListSort(").append(columnIndices.END_TIME.ordinal()).append(")\"");
		builder.append(">End Time");
		if (mCurrentSort == sortBy.END_TIME_ASC)
			builder.append(" &#9660;");
		else if (mCurrentSort == sortBy.END_TIME_DESC)
			builder.append(" &#9650;");
		builder.append("</td>");
		
		builder.append("<td class=\"columnHeader\" id='h'>Capacity</td>");

		builder.append("</tr>");

		int tableRow = 0;
		for (ScheduleItemGWT item : mScheduleItems) {
			int tableCol = 0;
			
			if (item.isConflicted()) {
				builder.append("<tr class=\"conflictedItem\">");
			}
			else {						
				builder.append("<tr>");
			}
			
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
				builder.append(", ");
				builder.append(mScheduleController.getInstructor(item.getInstructorID()).getFirstName());
			}			
			else {
				builder.append("STAFF");
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
					+ ">" + getDaysString(item.getDays()) + "</td>");
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
			
			int startRow = CalendarTableView.getStartRow(item);
			if (startRow >= 0 && startRow < ScheduleEditWidget.START_TIMES.length)
			   builder.append(ScheduleEditWidget.START_TIMES[startRow]);
			
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
			
			int endRow = CalendarTableView.getEndRow(item) + 1;
			if (endRow >= 0 && endRow < ScheduleEditWidget.END_TIMES.length)
				builder.append(ScheduleEditWidget.END_TIMES[endRow]); 
			
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

	private String getDaysString(WeekGWT days) {
		if (days == null)
			return "";
		
		String returnString = new String();
		Iterator<DayGWT> it = days.getDays().iterator();

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
	 * Called when the user double clicks an item in the list
	 */
	public void doubleClick(int row, int col) {
		highlightRow(row);
		final ScheduleItemGWT item = mScheduleItems.get(row);

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
	 * @param row The row in the list that was selected at the time of the key press.
	 * @param keycode The javascript keycode of which key was pressed.
	 */
	public void keyDown(int row, int keycode) {
		if (keycode == KEYCODE_DELETE) {
			mScheduleController.removeItem(mScheduleItems.get(row));
			mScheduleItems.remove(row);
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
	 * Called when the any cell on the list gets a mouse up event
	 * @param row
	 * @param col
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
	 * Called when the any cell on the list gets a mouse over event
	 * @param row
	 */
	public void mouseOver(int row) {
		// if (mDragController.isDragging()) {
		// DOMUtility.setStyleAttribute("y"+row, "backgroundColor", "#d1dfdf");
		// DOMUtility.setStyleAttribute("h"+row, "backgroundColor", "#d1dfdf");
		// }
	}

	/**
	 * Called when the any cell on the list gets a mouse out event
	 * @param row
	 */
	public void mouseOut(int row) {
		DOMUtility.setStyleAttribute("y" + row, "backgroundColor", "#FFFFFF");
		DOMUtility.setStyleAttribute("h" + row, "backgroundColor", "#edf2f2");
	}
	
	/**
	 * Uses column parameter to determine which column needs sorting. Then it sorts the list.
	 * @param column
	 */
	public void setSort(int column) {
		if (column == columnIndices.COURSE_NAME.ordinal()) {
			switch (mCurrentSort) {
				case COURSE_NAME_ASC:
					mCurrentSort = sortBy.COURSE_NAME_DESC;
					break;
				case COURSE_NAME_DESC:
					mCurrentSort = sortBy.NONE;
					break;
				default:
					mCurrentSort = sortBy.COURSE_NAME_ASC;
			}
		}
		else if (column == columnIndices.SECTION_NUMBER.ordinal()) {
			switch (mCurrentSort) {
				case SECTION_NUMBER_ASC:
					mCurrentSort = sortBy.SECTION_NUMBER_DESC;
					break;
				case SECTION_NUMBER_DESC:
					mCurrentSort = sortBy.NONE;
					break;
				default:
					mCurrentSort = sortBy.SECTION_NUMBER_ASC;
			}
		}
		else if (column == columnIndices.INSTRUCTOR.ordinal()) {
			switch (mCurrentSort) {
				case INSTRUCTOR_ASC:
					mCurrentSort = sortBy.INSTRUCTOR_DESC;
					break;
				case INSTRUCTOR_DESC:
					mCurrentSort = sortBy.NONE;
					break;
				default:
					mCurrentSort = sortBy.INSTRUCTOR_ASC;
			}
		}
		else if (column == columnIndices.DAYS.ordinal()) {
			switch (mCurrentSort) {
				case DAYS_ASC:
					mCurrentSort = sortBy.DAYS_DESC;
					break;
				case DAYS_DESC:
					mCurrentSort = sortBy.NONE;
					break;
				default:
					mCurrentSort = sortBy.DAYS_ASC;
			}
		}
		else if (column == columnIndices.START_TIME.ordinal()) {
			switch (mCurrentSort) {
				case START_TIME_ASC:
					mCurrentSort = sortBy.START_TIME_DESC;
					break;
				case START_TIME_DESC:
					mCurrentSort = sortBy.NONE;
					break;
				default:
					mCurrentSort = sortBy.START_TIME_ASC;
			}
		}
		else if (column == columnIndices.END_TIME.ordinal()) {
			switch (mCurrentSort) {
				case END_TIME_ASC:
					mCurrentSort = sortBy.END_TIME_DESC;
					break;
				case END_TIME_DESC:
					mCurrentSort = sortBy.NONE;
					break;
				default:
					mCurrentSort = sortBy.END_TIME_ASC;
			}
		}
		
		sort();
		drawList();
	}

	/**
	 * Clears the screen of visible widgets.
	 */
	@Override
	public void clear() {
		Iterator<Widget> it = iterator();
		while (it.hasNext()) {
			it.next();
			it.remove();
		}
	}
	
	private void sort() {
		if (mCurrentSort != null && mCurrentSort != sortBy.NONE)
			Collections.sort(mScheduleItems, new scheduleItemComparator());
	}
}
