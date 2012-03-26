package edu.calpoly.csc.scheduler.view.web.client.schedule;

import com.google.gwt.user.client.ui.HTML;

import edu.calpoly.csc.scheduler.view.web.shared.OldScheduleItemGWT;

/**
 * The HTML which displays a schedule item.
 * @author Mike McMahon
 */
public class ScheduleItemHTML extends HTML
{

 OldScheduleItemGWT scheduleItem;

 ScheduleItemHTML(OldScheduleItemGWT schdItem)
 {
  super();
  scheduleItem = schdItem;
  setHTML(scheduleItem.getSchdItemText());
  setStyleName("scheduleItemHTML");
 }

 public OldScheduleItemGWT getScheduleItem()
 {
  return scheduleItem;
 }
}
