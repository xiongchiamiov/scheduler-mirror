package edu.calpoly.csc.scheduler.view.web.client.schedule;

import com.google.gwt.user.client.ui.SimplePanel;

import edu.calpoly.csc.scheduler.view.web.shared.ScheduleItemGWT;

public class ScheduleCell extends SimplePanel 
{
 ScheduleItemGWT scheduleItem = null;
 int row = -1;
 int col = -1;
 
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
}
