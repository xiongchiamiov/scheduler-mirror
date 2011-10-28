package edu.calpoly.csc.scheduler.view.web.client.schedule;

import com.allen_sauer.gwt.dnd.client.DragContext;
import com.allen_sauer.gwt.dnd.client.drop.SimpleDropController;

public class ScheduleCellDropController extends SimpleDropController 
{
 ScheduleCell targetCell;
 ScheduleViewWidget schedule;
 
 ScheduleCellDropController(ScheduleCell target, ScheduleViewWidget schedule)
 {
  super(target);  
  targetCell = target;
  this.schedule = schedule;
 }
 
 public void onDrop(DragContext context)
 {
  ScheduleItemHTML droppedItem;
  super.onDrop(context);
  droppedItem = (ScheduleItemHTML)context.draggable;
  targetCell.promptForDays(droppedItem.getScheduleItem(), targetCell.getRow());
 }
 
 public void onEnter(DragContext context)
 {
  super.onEnter(context);
 }
}
