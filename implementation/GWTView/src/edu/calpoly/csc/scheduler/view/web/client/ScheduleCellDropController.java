package edu.calpoly.csc.scheduler.view.web.client;

import com.allen_sauer.gwt.dnd.client.DragContext;
import com.allen_sauer.gwt.dnd.client.drop.SimpleDropController;

public class ScheduleCellDropController extends SimpleDropController 
{
 ScheduleCell targetCell;
 
 ScheduleCellDropController(ScheduleCell target)
 {
  super(target);  
  targetCell = target;
 }
 
 public void onDrop(DragContext context)
 {
  targetCell.setWidget(context.draggable);
  super.onDrop(context);
 }
 
 public void onEnter(DragContext context)
 {
  super.onEnter(context);
 }
}
