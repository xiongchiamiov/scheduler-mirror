package scheduler.view.web.client.calendar;

import java.util.ArrayList;
import java.util.List;

import scheduler.view.web.shared.CourseGWT;
import scheduler.view.web.shared.ScheduleItemGWT;
import scheduler.view.web.shared.WeekGWT;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.SimplePanel;

public class CourseListView extends SimplePanel {

	private final ScheduleEditWidget mScheduleController;
	private final DragAndDropController mDragController;
	
	private List<CourseGWT> mModel;
	
	public CourseListView(ScheduleEditWidget scheduleController, DragAndDropController dragController) {
		mScheduleController = scheduleController;
		mDragController = dragController;

		mModel = new ArrayList<CourseGWT>();
		
		defineTableCallbacks();
	}
	
	public List<CourseGWT> getItems() { return mModel; }
	
	public void setItems(List<CourseGWT> items) { mModel = items; }
	
	public void toggle(boolean hidden) {
		if (hidden)
			DOMUtility.setStyleAttribute("ScheduleListContainer", "display", "none");
		else
			DOMUtility.setStyleAttribute("ScheduleListContainer", "display", "block");
	}
	
	public void drawList() {
		StringBuilder builder = new StringBuilder();
		builder.append("<div id=\"ScheduleListContainer\"" +
				"onmouseup=\"listMouseUp("+-1+")\" " +
				">");
		builder.append("<div id=\"ScheduleListHeader\">AvailableCourses</div>" +
				"<div id=\"ScheduleListTableContainer\">" +
				"<table id=\"ScheduleList\">");
		
		int rowNum = 0;
		for (CourseGWT course : mModel) {
			builder.append("<tr " +
					"onmouseover=\"listMouseOver("+rowNum+")\" " +
					"onmouseout=\"listMouseOut("+rowNum+")\" " +
					">");
			builder.append("<td><div class=\"ScheduleListItem\" id=\"list"+rowNum+"\"" +
					"ondblclick=\"listDoubleClick("+rowNum+")\" " +
					"onmousedown=\"listMouseDown("+rowNum+")\" " +
					"onmouseup=\"listMouseUp("+rowNum+")\" " +
					"onselectstart=\"return false\" "+
					">"+mScheduleController.getCourseString(course.getID())+" ("+course.getNumSections()+")</div></td>");
			builder.append("</tr>");
			rowNum++;
		}
		
		builder.append("</div></table></div>");
		
		setHTML(builder.toString());
		setTopOffset(mScheduleController.getWidget().getAbsoluteTop());
	}

	private void setTopOffset(int pixels) {
		DOMUtility.setStyleAttribute("ScheduleListContainer", "top", pixels+"px");
	}

	private void setHTML(String html) {
		clear();
		add(new HTML(html));
	}

	/**
	 * Called when the user double clicks an item in the table
	 */
	public void doubleClick(int row) {
		final CourseGWT course = mModel.get(row);
		mScheduleController.editItem(course);
	}
	
	/**
	 * Called when the an item on the table gets a mouse down event
	 * 
	 * @return false to disable text selection on some browsers
	 */
	public Boolean mouseDown(int row) {
		final CourseGWT course = mModel.get(row);
		
		// Set the text of the div that moves with the cursor
		Element dragDiv = DOM.getElementById(DragAndDropController.DRAGGED_ID);
		DOM.setInnerText(dragDiv, mScheduleController.getCourseString(course.getID()));
		
		mDragController.onMouseDown(createItem(course), row, -1);
		return false;
	}
	
	private ScheduleItemGWT createItem(CourseGWT course) {
		return new ScheduleItemGWT(-1, course.getID(), -1, -1, 
				-1, new WeekGWT(), 0, 0, false, false);
	}

	/**
	 * Called when the any cell on the table gets a mouse up event
	 */
	public void mouseUp(int row) {
		mDragController.onDrop(-1, null);//row = -1 indicates dropped on list
	}
	
	/**
	 * Called when the any cell on the table gets a mouse up event
	 */
	public void mouseOver(int row) {
		if (mDragController.isDragging())
			DOMUtility.setStyleAttribute("list"+row, "backgroundColor", "#d1dfdf");
	}
	
	/**
	 * Called when the any cell on the table gets a mouse up event
	 */
	public void mouseOut(int row) {
		DOMUtility.setStyleAttribute("list"+row, "backgroundColor", "#DFF0CF");
	}
	
	/**
	 * Used to register callback methods for access via handwritten javascript
	 */
	private native void defineTableCallbacks() /*-{
		var availableCourses = this;
		$wnd.listDoubleClick = function(row) {
			return availableCourses.@scheduler.view.web.client.calendar.CourseListView::doubleClick(I)(row);
		}
		$wnd.listMouseDown = function(row) {
			return availableCourses.@scheduler.view.web.client.calendar.CourseListView::mouseDown(I)(row);
		}
		$wnd.listMouseUp = function(row) {
			return availableCourses.@scheduler.view.web.client.calendar.CourseListView::mouseUp(I)(row);
		}
		$wnd.listMouseOver = function(row) {
			return availableCourses.@scheduler.view.web.client.calendar.CourseListView::mouseOver(I)(row);
		}
		$wnd.listMouseOut = function(row) {
			return availableCourses.@scheduler.view.web.client.calendar.CourseListView::mouseOut(I)(row);
		}
    }-*/;
}
