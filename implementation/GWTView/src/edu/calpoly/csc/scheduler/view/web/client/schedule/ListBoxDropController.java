package edu.calpoly.csc.scheduler.view.web.client.schedule;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Widget;

import com.allen_sauer.gwt.dnd.client.DragContext;
import com.allen_sauer.gwt.dnd.client.VetoDragException;
import com.allen_sauer.gwt.dnd.client.drop.AbstractDropController;

import edu.calpoly.csc.scheduler.view.web.shared.CourseGWT;

/**
 * A drop controller for handling drops onto the course lists.
 * Based on example from Fred Sauer at 
 * DropController for {@link DualListExample}.
 */
class ListBoxDropController extends AbstractDropController
{
 //The drop target. 
 private MouseListBox mouseListBox;
 //The ScheduleViewWidget in which draggable objects reside
 private ScheduleViewWidget schedule;

 /**
  * Create a DropController for the provided list box which resides on the 
  * provided ScheduleViewWidget.
  * 
  * @param mouseListBox The listbox which has widgets dropped on it.
  * @param schedule The ScheduleViewWidget in which this listbox resides.
  */
 ListBoxDropController(MouseListBox mouseListBox, ScheduleViewWidget schedule)
 {
  super(mouseListBox);
  this.mouseListBox = mouseListBox;
  this.schedule = schedule;
 }

 /**
  * Called when a widget is dropped on to this list box.
  */
 @Override
 public void onDrop(DragContext context)
 {
  MouseListBox from;
  CourseGWT course;
  int itemIndex, sectionsIncluded;

  //This case handles a schedule item dragged from the schedule onto a course list.
  if (context.draggable instanceof ScheduleItemHTML)
  {
   //Remove the ScheduleItem from the schedule.
   schedule.removeItem(((ScheduleItemHTML)context.draggable).getScheduleItem());
   
   //This case handles when a schedule item is dragged onto the "Course to be Schedule" list.
   if(!mouseListBox.isAvailableBox())
   {
	//Get the ScheduleItem's course.
    course = new CourseGWT(((ScheduleItemHTML)context.draggable).getScheduleItem().getCourse());
    //Get the index of the course in the "Courses to be Scheduled" list.
    itemIndex = mouseListBox.contains(new CourseListItem(course, true));
    //Get the number of sections of the course in the "Courses to be Scheduled" list.
    sectionsIncluded = mouseListBox.getSectionsInBox(course);
    
    //If the list contains the course, increment the number of sections by one.
    if (itemIndex >= 0)
    {
     course.setNumSections(sectionsIncluded + 1);
     mouseListBox.setWidget(itemIndex, new CourseListItem(course, true));
    }
    //If the list does not contain the course, add one section to the list.
    else
    {
     course.setNumSections(1);
     mouseListBox.add(new CourseListItem(course, true));
    }
   }
  }
  //This case handles when a course is dragged from one list to another.
  else
  {
   from = (MouseListBox) context.draggable.getParent().getParent();
   for (Widget widget : context.selectedWidgets)
   {
    if (widget.getParent().getParent() == from)
    {
     //Only remove courses 
     if (mouseListBox.isAvailableBox())
     {
      from.remove(widget);
     }
     else
     {
      itemIndex = mouseListBox.contains((CourseListItem) widget);
      course = new CourseGWT(((CourseListItem) widget).getCourse());
      sectionsIncluded = mouseListBox.getSectionsInBox(course);

      if (course.getNumSections() > sectionsIncluded
        + schedule.getSectionsOnSchedule(course))
      {
       if (itemIndex >= 0)
       {
        course.setNumSections(sectionsIncluded + 1);
        mouseListBox.setWidget(itemIndex, new CourseListItem(course, true));
       } else
       {
        course.setNumSections(1);
        mouseListBox.add(new CourseListItem(course, true));
       }
      } else
      {
       Window.alert("No more sections to schedule");
      }
     }
    }
   }
  }
  super.onDrop(context);
 }

 @Override
 public void onPreviewDrop(DragContext context) throws VetoDragException
 {
  if(context.draggable instanceof CourseListItem)
  {
   MouseListBox from = (MouseListBox) context.draggable.getParent().getParent();
   if (from == mouseListBox)
   {
    throw new VetoDragException();
   }
  }
  super.onPreviewDrop(context);
 }
}
