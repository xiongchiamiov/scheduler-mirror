package edu.calpoly.csc.scheduler.view.web.client.schedule;

import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.SimplePanel;

import edu.calpoly.csc.scheduler.view.web.shared.ScheduleItemGWT;

/**
 * This SimplePanel lies in each cell of the schedule table.
 * 
 * @author Mike McMahon
 */
public class ScheduleCell extends SimplePanel implements
  CloseHandler<PopupPanel>
{

 private class HoverTextPopup extends PopupPanel
 {
  public HoverTextPopup(String hoverText)
  {
   super(true);
   setWidget(new HTML(hoverText));
   setStyleName("scheduleItemHoverText");
  }
 }

 ScheduleItemGWT scheduleItem = null;
 int row = -1;
 int col = -1;
 ReschedulePopup rescheduler;
 ScheduleViewWidget schedule;
 boolean rescheduling;
 boolean fromIncluded;
 HoverTextPopup hoverPopup;

 public ScheduleCell(ScheduleViewWidget schedule)
 {
  this.schedule = schedule;
 }

 public void setScheduleItem(ScheduleItemHTML item, int height)
 {
  scheduleItem = item.getScheduleItem();
  setWidget(item);

  //Color the schedule item
  if (item.getScheduleItem().isConflicted())
  {
   addStyleName("scheduleItemConflicted");
  } else
  {
   addStyleName("scheduleItemNoConflict");
  }

  setSize("100%", "100%");

  item.setSize("100%", "100%");
  hoverPopup = new HoverTextPopup(scheduleItem.getHoverText());
  sinkEvents(Event.ONMOUSEOVER);
  sinkEvents(Event.ONMOUSEOUT);
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

 /**
  * Called when the ReschedulerPopup is closed. Calls a method to move an 
  * existing schedule item, or place a new course as a schedule item.
  */
 public void onClose(CloseEvent<PopupPanel> event)
 {
  schedule.moveItem(rescheduler.getItem(), rescheduler.getDays(),
    rescheduler.getRow(), rescheduling, fromIncluded);
 }

 /**
  * Called when a schedule item is moved, or a course is dropped onto the 
  * schedule.
  * @param rescheduled The moved schedule item, or a schedule item containing the dropped course.
  * @param row The row on which the drop happened.
  * @param inScheduled True if an existing schedule item was dragged, false if a course was dragged from a list.
  * @param fromIncluded True if course was dragged from the To Be Scheduled list.
  */
 public void promptForDays(ScheduleItemGWT rescheduled, int row,
   boolean inScheduled, boolean fromIncluded)
 {
  rescheduling = inScheduled;
  this.fromIncluded = fromIncluded;
  rescheduler = new ReschedulePopup(rescheduled, row);
  rescheduler.addCloseHandler(this);
  rescheduler.center();
 }

 public void highlightRow()
 {
  schedule.highlightRow(row);
 }

 public void unhighlightRow()
 {
  schedule.unhighlightRow(row);
 }

 public void onBrowserEvent(Event event)
 {
  switch (DOM.eventGetType(event))
  {
  case Event.ONMOUSEOVER:
   if (hoverPopup != null && !hoverPopup.isShowing())
   {
    hoverPopup.setPopupPosition(getAbsoluteLeft() + getOffsetWidth(),
      getAbsoluteTop());
    hoverPopup.show();
   }
   break;
  case Event.ONMOUSEOUT:
   if (hoverPopup != null)
   {
    hoverPopup.hide();
   }
   break;
  }
 }
}
