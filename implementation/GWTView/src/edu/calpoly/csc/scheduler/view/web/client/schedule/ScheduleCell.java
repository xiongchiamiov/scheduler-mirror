package edu.calpoly.csc.scheduler.view.web.client.schedule;

import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.SimplePanel;

import edu.calpoly.csc.scheduler.view.web.shared.ScheduleItemGWT;

public class ScheduleCell extends SimplePanel implements CloseHandler<PopupPanel>
{
 ScheduleItemGWT scheduleItem = null;
 int row = -1;
 int col = -1;
 ReschedulePopup rescheduler;
 ScheduleViewWidget schedule;
 
 public ScheduleCell(ScheduleViewWidget schedule)
 {
  this.schedule = schedule;
 }
 
 public void setScheduleItem(ScheduleItemGWT item)
 {
  scheduleItem = item;
 }

 public int getRow()
 {
  return row;
 }
 
 public int getCol()
 {
  return col;	 
 }
 
 public void setRow(int row)
 {
  this.row = row;
 }
 
 public void setCol(int col)
 {
  this.col = col;
 }

 public void onClose(CloseEvent<PopupPanel> event) 
 {
  System.out.println("Closed");
  schedule.moveItem(rescheduler.getItem(), rescheduler.getDays(), rescheduler.getRow());	
 }

 public void promptForDays(ScheduleItemGWT rescheduled, int row) 
 {
  rescheduler = new ReschedulePopup(rescheduled, row);
  rescheduler.addCloseHandler(this);
  rescheduler.center();
 }
}
