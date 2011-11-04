package edu.calpoly.csc.scheduler.view.web.client.schedule;

import java.util.ArrayList;

import com.allen_sauer.gwt.dnd.client.DragContext;
import com.allen_sauer.gwt.dnd.client.drop.SimpleDropController;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Label;

import edu.calpoly.csc.scheduler.view.web.shared.ScheduleItemGWT;

public class ScheduleCellDropController extends SimpleDropController 
{
 ScheduleCell targetCell;
 ScheduleViewWidget schedule;
 MouseListBox includedListBox;
 
 ScheduleCellDropController(ScheduleCell target, ScheduleViewWidget schedule, MouseListBox includedListBox)
 {
  super(target);  
  targetCell = target;
  this.schedule = schedule;
  this.includedListBox = includedListBox;
 }
 
 public void onDrop(DragContext context)
 {
  ScheduleItemHTML droppedItem;
  ScheduleItemGWT courseHolder;
  String[] deptAndCatalogNum;
  int widgetIndex;
  
  super.onDrop(context);
  
  if(context.draggable.getClass() == ScheduleItemHTML.class)
  {
   droppedItem = (ScheduleItemHTML)context.draggable;
   targetCell.promptForDays(droppedItem.getScheduleItem(), targetCell.getRow(), 
		   true);
  }
  else
  {
   deptAndCatalogNum = ((CourseListItem)context.draggable).getText().split(" ");
   courseHolder = new ScheduleItemGWT("", deptAndCatalogNum[0], 
		   Integer.valueOf(deptAndCatalogNum[1]), 1, 
           new ArrayList<Integer>(), 0, 0, 0, 
           0, "");
   targetCell.promptForDays(courseHolder, targetCell.getRow(), false);
   if(((MouseListBox)context.draggable.getParent().getParent()).isAvailableBox())
   {
    ((CourseListItem)context.draggable).setIsScheduled();
    widgetIndex = includedListBox.contains((CourseListItem)context.draggable);
    if(widgetIndex >= 0)
    {
     includedListBox.setWidget(widgetIndex, null);
    }
   }
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
