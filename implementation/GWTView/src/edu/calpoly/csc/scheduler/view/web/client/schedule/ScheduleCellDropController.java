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
 
 ScheduleCellDropController(ScheduleCell target, ScheduleViewWidget schedule)
 {
  super(target);  
  targetCell = target;
  this.schedule = schedule;
 }
 
 public void onDrop(DragContext context)
 {
  ScheduleItemHTML droppedItem;
  ScheduleItemGWT courseHolder;
  String[] deptAndCatalogNum;
  super.onDrop(context);
  
  Window.alert("Dropped");
  if(context.draggable.getClass() == ScheduleItemHTML.class)
  {
   droppedItem = (ScheduleItemHTML)context.draggable;
   targetCell.promptForDays(droppedItem.getScheduleItem(), targetCell.getRow(), 
		   true);
  }
  else
  {
   deptAndCatalogNum = ((Label)context.draggable).getText().split(" ");
   courseHolder = new ScheduleItemGWT("", deptAndCatalogNum[0], 
		   Integer.valueOf(deptAndCatalogNum[1]), 1, 
           new ArrayList<Integer>(), 0, 0, 0, 
           0, "");
   targetCell.promptForDays(courseHolder, targetCell.getRow(), false);
  }
 }
 
 public void onEnter(DragContext context)
 {
  super.onEnter(context);
 }
}
