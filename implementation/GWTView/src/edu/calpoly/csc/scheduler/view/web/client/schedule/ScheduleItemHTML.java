package edu.calpoly.csc.scheduler.view.web.client.schedule;

import com.google.gwt.user.client.ui.HTML;

import edu.calpoly.csc.scheduler.view.web.shared.ScheduleItemGWT;

/**
 * The HTML which displays a schedule item.
 * @author Mike McMahon
 */
public class ScheduleItemHTML extends HTML
{

 ScheduleItemGWT scheduleItem;

 ScheduleItemHTML(ScheduleItemGWT schdItem)
 {
  super();
  scheduleItem = schdItem;
  setHTML(scheduleItem.getSchdItemText());
  setStyleName("scheduleItemHTML");
 }

 public ScheduleItemGWT getScheduleItem()
 {
  return scheduleItem;
 }
}
