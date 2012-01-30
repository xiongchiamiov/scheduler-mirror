package edu.calpoly.csc.scheduler.view.web.client.schedule;

import com.allen_sauer.gwt.dnd.client.drop.DropController;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import edu.calpoly.csc.scheduler.view.web.shared.CourseGWT;

import java.util.ArrayList;

/**
 * A panel which holds the two course lists. Has buttons for moving courses 
 * between the two lists.
 * Uses modified code from Fred Sauer's example at
 * http://allen-sauer.com/com.allen_sauer.gwt.dnd.demo.DragDropDemo/DragDropDemo.html#DualListExample
 * 
 * @authors Fred Sauer, Mike McMahon
 */
public class DualListBox extends AbsolutePanel
{
 //Button for removing all courses from the "Courses to be scheduled" list.
 private Button allLeft;
 //Button for adding all courses to the "Courses to be scheduled" list.
 private Button allRight;
 //The drag controller for the two lists.
 private ListBoxDragController dragController;
 //The "Available Courses" list.
 private MouseListBox left;
 //Removes a selected course from the "Courses to be Scheduled" list.
 private Button oneLeft;
 //Adds a selected course in the "Available Courses" list to the "Courses to be Scheduled" list
 private Button oneRight;
 //The "Courses to be Scheduled" list.
 private MouseListBox right;
 //Controls drops into the "Available Courses" list.
 ListBoxDropController leftDropController;
 //Controls drops into the "Courses to be Scheduled" list.
 ListBoxDropController rightDropController;
 //The ScheduleViewWidget in which this panel resides.
 ScheduleViewWidget scheduleController;

 /**
  * Creates a panel with two list boxes and buttons.
  * 
  * @param visibleItems The number items viewable in the list boxes.
  * @param width The width of the list boxes.
  * @param totalItems The total number of items the list boxes will hold.
  * @param schedule The ScheduleViewWidget in which this panel resides.
  */
 public DualListBox(int visibleItems, String width, int totalItems,
   ScheduleViewWidget schedule)
 {
  scheduleController = schedule;
  //Create a panel which will hold both lists and buttons.
  HorizontalPanel horizontalPanel = new HorizontalPanel();
  add(horizontalPanel);
  horizontalPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);

  //Create a panel to hold the buttons.
  VerticalPanel verticalPanel = new VerticalPanel();
  //Create a panel to hold the "Available Courses" list.
  VerticalPanel availablePanel = new VerticalPanel();
  //Create a panel to hold the "Courses to Be Scheduled" list.
  VerticalPanel includedPanel = new VerticalPanel();

  //Create the drag controller and lists.
  dragController = new ListBoxDragController(this);
  left = new MouseListBox(dragController, totalItems, true);
  right = new MouseListBox(dragController, totalItems, false);

  left.setWidth(width);
  right.setWidth(width);

  //Added a title and lists to their respective panels.
  availablePanel.add(new HTML("<b>Available Courses</b>"));
  availablePanel.add(left);
  includedPanel.add(new HTML("<b>Courses to Schedule</b>"));
  includedPanel.add(right);
  horizontalPanel.add(availablePanel);
  horizontalPanel.add(verticalPanel);
  horizontalPanel.add(includedPanel);

  //Add buttons
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

  //Add click handlers to buttons.
  allRight.addClickHandler(new ClickHandler()
  {
   @Override
   public void onClick(ClickEvent event)
   {
    moveAllToIncluded();
   }
  });

  allLeft.addClickHandler(new ClickHandler()
  {
   @Override
   public void onClick(ClickEvent event)
   {
    removeAllFromIncluded();
   }
  });

  oneRight.addClickHandler(new ClickHandler()
  {
   @Override
   public void onClick(ClickEvent event)
   {
    moveSelectedToIncluded();
   }
  });

  oneLeft.addClickHandler(new ClickHandler()
  {
   @Override
   public void onClick(ClickEvent event)
   {
    removeSelectedFromIncluded();
   }
  });

  //Create drop controllers for the lists.
  leftDropController = new ListBoxDropController(left, schedule);
  rightDropController = new ListBoxDropController(right, schedule);
  //Register the drop controllers.
  dragController.registerDropController(leftDropController);
  dragController.registerDropController(rightDropController);
 }

 /**
  * Adds a widget to the left list box.
  * 
  * @param widget
  *         The widget which displays the course name.
  */
 public void addLeft(Widget widget)
 {
  left.add(widget);
 }

 /**
  * Adds an widget to the right list box.
  * 
  * @param widget
  *         The widget which displays the course name.
  */
 public void addRight(Widget widget)
 {
  right.add(widget);
 }

 /**
  * Get the drag controller for the widgets in the two list boxes.
  * 
  * @return The drag controller for the two list boxes.
  */
 public ListBoxDragController getDragController()
 {
  return dragController;
 }

 /**
  * Registers drop controllers located on a weekly schedule view's time slots.
  * 
  * @param dropController A timeslot's DropController
  */
 public void registerScheduleDrop(DropController dropController)
 {
  dragController.registerDropController(dropController);
 }

 /**
  * Registers the two lists' DropControllers with their DragController. Called
  * after the DragController has cleared its record of DropControllers.
  */
 public void reregisterBoxDrops()
 {
  dragController.registerDropController(leftDropController);
  dragController.registerDropController(rightDropController);
 }

 /**
  * Get the DropController for the "Available Courses" list.
  * 
  * @return The "Available Courses" list's DropController.
  */
 public ListBoxDropController getAvailableDropController()
 {
  return leftDropController;
 }
 
 /**
  * Get the DropController for the "Courses To Be Scheduled" list.
  * 
  * @return The "Courses To Be Scheduled" list's DropController. 
  */
 public ListBoxDropController getIncludedDropController()
 {
  return rightDropController;
 }
 
 /**
  * Get a list of all courses in the "Courses to be Scheduled" list.
  * 
  * @return A list of all courses in the get included courses list.
  */
 public ArrayList<CourseGWT> getIncludedCourses()
 {
  ArrayList<CourseGWT> courses = new ArrayList<CourseGWT>();

  for (Widget courseItem : right.widgetList())
  {
   if (courseItem instanceof CourseListItem)
   {
    courses.add(((CourseListItem) courseItem).getCourse());
   }
  }
  return courses;
 }

 /**
  * Move all courses in the "Available Courses" list to the "Courses to be Scheduled" list.
  */
 private void moveAllToIncluded()
 {
  for (Widget widget : left.widgetList())
  {
   //If the course is already on the schedule, or in the "Courses to be Scheduled" list then don't add it.
	  System.out.println(((CourseListItem) widget).getCourse().toString());
	  
			if (right.contains(((CourseListItem) widget)) < 0
					&& scheduleController.getSectionsOnSchedule(((CourseListItem) widget).getCourse()) <= 0)
   {
    right.add(new CourseListItem(((CourseListItem) widget).getCourse(), true));
   }
  }
 }

 /**
  * Removes all courses from the "Courses to be Scheduled" list.
  */
 void removeAllFromIncluded()
 {
  for (Widget widget : right.widgetList())
  {
   right.remove(widget);
  }
 }

 /**
  * Move a course that is selected in the "Available Courses" list to the 
  * "Courses to be Scheduled" list.
  */
 private void moveSelectedToIncluded()
 {
  ArrayList<Widget> selectedItems = dragController.getSelectedWidgets(left);
  
  for (Widget item : selectedItems)
  {
   //If the course is already on the schedule, or in the "Courses to be Scheduled" list, don't add it.
   if (right.contains(((CourseListItem) item)) < 0
     && scheduleController.getSectionsOnSchedule(((CourseListItem) item).getCourse()) <= 0)
   {
    right.add(new CourseListItem(((CourseListItem) item).getCourse(), true));
   }
  }
 }

 /**
  * Remove selected courses from the "Courses to be Scheduled" list.
  */
 private void removeSelectedFromIncluded()
 {
  ArrayList<Widget> selectedItems = dragController.getSelectedWidgets(right);
  for (Widget item : selectedItems)
  {
   right.remove(item);
  }
 }

 /**
  * Get the "Courses to be Scheduled" listbox.
  * 
  * @return The "Courses to be Scheduled" listbox.
  */
 public MouseListBox getIncludedListBox()
 {
  return right;
 }

 /**
  * Get the "Available Courses" listbox.
  * 
  * @return The "Available Courses" listbox.
  */
 public MouseListBox getAvailableListBox()
 {
  return left;
 }

 /**
  * Sets the maximum number of courses which each list can hold.
  * 
  * @param size The maximum list size.
  */
 public void setListLength(int size)
 {
  left.resetGrid(size);
  right.resetGrid(size);
 }
}
