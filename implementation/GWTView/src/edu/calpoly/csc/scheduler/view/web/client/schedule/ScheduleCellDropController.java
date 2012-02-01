package edu.calpoly.csc.scheduler.view.web.client.schedule;

import java.util.ArrayList;

import com.allen_sauer.gwt.dnd.client.DragContext;
import com.allen_sauer.gwt.dnd.client.drop.SimpleDropController;
import com.google.gwt.user.client.Window;

import edu.calpoly.csc.scheduler.view.web.shared.CourseGWT;
import edu.calpoly.csc.scheduler.view.web.shared.ScheduleItemGWT;

/**
 * Handles drops onto the schedule table.
 * 
 * @author Mike McMahon
 */
public class ScheduleCellDropController extends SimpleDropController
{
 ScheduleCell targetCell;
 ScheduleViewWidget schedule;
 MouseListBox includedListBox;
 MouseListBox availableListBox;

 ScheduleCellDropController(ScheduleCell target, ScheduleViewWidget schedule,
   MouseListBox includedListBox, MouseListBox availableListBox)
 {
  super(target);
  targetCell = target;
  this.schedule = schedule;
  this.includedListBox = includedListBox;
  this.availableListBox = availableListBox;
 }

 public void onDrop(DragContext context)
 {
  ScheduleItemHTML droppedItem;
  ScheduleItemGWT courseHolder;
  CourseGWT courseDropped, oneSectionCourse;
  boolean fromIncluded;

  super.onDrop(context);

  //Handles the case of an existing schedule item being moved
  if (context.draggable.getClass() == ScheduleItemHTML.class)
  {
   droppedItem = (ScheduleItemHTML) context.draggable;
   targetCell.promptForDays(droppedItem.getScheduleItem(), targetCell.getRow(),
     true, false);
  } 
  //Handles the case of a course dragged onto the schedule.
  else
  {
   courseDropped = ((CourseListItem) context.draggable).getCourse();

   if (availableListBox.getSectionsInBox(courseDropped) <= schedule
     .getSectionsOnSchedule(courseDropped))
   {
    Window.alert("No more sections to schedule");
    return;
   }

   //Make a clone of the course, with one section because we only schedule one section at a time.
   oneSectionCourse = new CourseGWT(courseDropped);
   oneSectionCourse.setNumSections(1);
   //The course is held in a schedule item because of my "make it up as you go" design... sorry.
   courseHolder = new ScheduleItemGWT(oneSectionCourse, "", "", "", "", 1,
     new ArrayList<Integer>(), 0, 0, 0, 0, "", false);
   fromIncluded = !((MouseListBox) context.draggable.getParent().getParent())
     .isAvailableBox();
   targetCell.promptForDays(courseHolder, targetCell.getRow(), false,
     fromIncluded);
  }
 }

 public void onEnter(DragContext context)
 {
  super.onEnter(context);
  targetCell.highlightRow();
 }

 public void onLeave(DragContext context)
 {
  super.onLeave(context);
  targetCell.unhighlightRow();
 }
}
