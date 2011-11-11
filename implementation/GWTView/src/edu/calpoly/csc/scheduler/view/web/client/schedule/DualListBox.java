package edu.calpoly.csc.scheduler.view.web.client.schedule;

import com.allen_sauer.gwt.dnd.client.drop.DropController;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import edu.calpoly.csc.scheduler.view.web.shared.CourseGWT;

import java.util.ArrayList;

/**
 * Example of two lists side by side for {@link DualListExample}.
 */
public class DualListBox extends AbsolutePanel {

	private static final String CSS_DEMO_DUAL_LIST_EXAMPLE_CENTER = "demo-DualListExample-center";

	private Button allLeft;

	private Button allRight;

	private ListBoxDragController dragController;

	private MouseListBox left;

	private Button oneLeft;

	private Button oneRight;

	private MouseListBox right;
	ListBoxDropController leftDropController;
	ListBoxDropController rightDropController;

	public DualListBox(int visibleItems, String width, int totalItems,
			ScheduleViewWidget schedule) {
		HorizontalPanel horizontalPanel = new HorizontalPanel();
		add(horizontalPanel);
		// horizontalPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);

		VerticalPanel verticalPanel = new VerticalPanel();
		// verticalPanel.addStyleName(CSS_DEMO_DUAL_LIST_EXAMPLE_CENTER);
		verticalPanel
				.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		VerticalPanel availablePanel = new VerticalPanel();
		VerticalPanel includedPanel = new VerticalPanel();

		dragController = new ListBoxDragController(this);
		left = new MouseListBox(dragController, totalItems, true);
		right = new MouseListBox(dragController, totalItems, false);

		left.setWidth(width);
		right.setWidth(width);

		availablePanel.add(new HTML("<b>Available Courses</b>"));
		availablePanel.add(left);
		includedPanel.add(new HTML("<b>Courses to Schedule</b>"));
		includedPanel.add(right);
		horizontalPanel.add(availablePanel);
		horizontalPanel.add(verticalPanel);
		horizontalPanel.add(includedPanel);

		oneRight = new Button("&gt;");
		oneLeft = new Button("&lt;");
		allRight = new Button("&gt;&gt;");
		allLeft = new Button("&lt;&lt;");
		verticalPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		verticalPanel.add(oneRight);
		verticalPanel.add(oneLeft);
		verticalPanel.add(new HTML("&nbsp;"));
		verticalPanel.add(allRight);
		verticalPanel.add(allLeft);

		allRight.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				moveAllToIncluded();
				// moveItems(left, right, false);
			}
		});

		allLeft.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				removeAllFromIncluded();
				// moveItems(right, left, false);
			}
		});

		oneRight.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				moveSelectedToIncluded();
			}
		});

		oneLeft.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				removeSelectedFromIncluded();
			}
		});

		leftDropController = new ListBoxDropController(left, schedule);
		rightDropController = new ListBoxDropController(right, schedule);
		dragController.registerDropController(leftDropController);
		dragController.registerDropController(rightDropController);
	}

	public void addLeft(String string) {
		left.add(string);
	}

	/**
	 * Adds an widget to the left list box.
	 * 
	 * @param widget
	 *            the text of the item to be added
	 */
	public void addLeft(Widget widget) {
		left.add(widget);
	}

	/**
	 * Adds an widget to the right list box.
	 * 
	 * @param widget
	 *            the text of the item to be added
	 */
	public void addRight(Widget widget) {
		right.add(widget);
	}

	public void addRight(String string) {
		right.add(string);
	}

	public ListBoxDragController getDragController() {
		return dragController;
	}

	public void registerScheduleDrop(DropController dropController) {
		dragController.registerDropController(dropController);
	}

	public void reregisterBoxDrops() {
		dragController.registerDropController(leftDropController);
		dragController.registerDropController(rightDropController);
	}

	public ArrayList<CourseGWT> getIncludedCourses() {
		ArrayList<CourseGWT> courses = new ArrayList<CourseGWT>();

		for (Widget courseItem : right.widgetList()) {
			if (courseItem instanceof CourseListItem) {
				courses.add(((CourseListItem) courseItem).getCourse());
			}
		}
		return courses;
	}

	private void moveAllToIncluded() {
		for (Widget widget : left.widgetList()) {
			if (right.contains(((CourseListItem) widget)) < 0
					&& !((CourseListItem) widget).isScheduled()) {
				right.add(new CourseListItem(((CourseListItem) widget)
						.getCourse()));
			}
		}
	}

	private void removeAllFromIncluded() {
		for (Widget widget : right.widgetList()) {
			right.remove(widget);
		}
	}

	private void moveSelectedToIncluded() {
		ArrayList<Widget> selectedItems = dragController
				.getSelectedWidgets(left);
		for (Widget item : selectedItems) {
			if (right.contains(((CourseListItem) item)) < 0
					&& !((CourseListItem) item).isScheduled()) {
				right.add(new CourseListItem(((CourseListItem) item)
						.getCourse()));
			}
		}
	}

	private void removeSelectedFromIncluded() {
		ArrayList<Widget> selectedItems = dragController
				.getSelectedWidgets(right);
		for (Widget item : selectedItems) {
			right.remove(item);
		}
	}

	public MouseListBox getIncludedListBox() {
		return right;
	}
	
	public MouseListBox getAvailableListBox() {
		return left;
	}

	public void setListLength(int size) 
	{
	 left.resetGrid(size);
	 right.resetGrid(size);
	}
}
