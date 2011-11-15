package edu.calpoly.csc.scheduler.view.web.client.schedule;

import java.util.ArrayList;

import com.allen_sauer.gwt.dnd.client.DragContext;
import com.allen_sauer.gwt.dnd.client.drop.SimpleDropController;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Label;

import edu.calpoly.csc.scheduler.view.web.shared.CourseGWT;
import edu.calpoly.csc.scheduler.view.web.shared.ScheduleItemGWT;

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
  String dept;
  int catalogNum, widgetIndex, sectionsIncluded;
  CourseGWT courseDropped;
  boolean fromIncluded;

  super.onDrop(context);

  if (context.draggable.getClass() == ScheduleItemHTML.class)
  {
   droppedItem = (ScheduleItemHTML) context.draggable;
   targetCell.promptForDays(droppedItem.getScheduleItem(), targetCell.getRow(),
     true, false);
  } else
  {
   courseDropped = ((CourseListItem) context.draggable).getCourse();

   if (availableListBox.getSectionsInBox(courseDropped) <= schedule
     .getSectionsOnSchedule(courseDropped))
   {
    Window.alert("No more sections to schedule");
    return;
   }

   dept = courseDropped.getDept();
   catalogNum = courseDropped.getCatalogNum();
   courseHolder = new ScheduleItemGWT(null, "", "", dept, catalogNum, 1,
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
