package edu.calpoly.csc.scheduler.view.web.client.schedule;

import java.util.ArrayList;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.calpoly.csc.scheduler.view.web.shared.OldScheduleItemGWT;

/**
 * A DialogBox is displayed when a course is dragged onto the schedule or when
 * a schedule item is dragged to a new time. 
 * 
 * @author Mike McMahon
 */
public class ReschedulePopup extends DialogBox
{
 private VerticalPanel mainPanel;
 private VerticalPanel daysPanel;
 private CheckBox mondayCB = new CheckBox("Monday");
 private CheckBox tuesdayCB = new CheckBox("Tuesday");
 private CheckBox wednesdayCB = new CheckBox("Wednesday");
 private CheckBox thursdayCB = new CheckBox("Thursday");
 private CheckBox fridayCB = new CheckBox("Friday");
 private OldScheduleItemGWT rescheduled;
 private int row;

 ReschedulePopup(OldScheduleItemGWT rescheduled, int row)
 {
  super(false);
  this.rescheduled = rescheduled;
  this.row = row;
  layoutDaysPanel();
  mainPanel = new VerticalPanel();
  mainPanel.add(daysPanel);
  mainPanel.add(new Button("Schedule Course", new ClickHandler()
  {
   @Override
   public void onClick(ClickEvent event)
   {
    ReschedulePopup.this.hide();
   }
  }));
  this.setWidget(mainPanel);
 }

 private void layoutDaysPanel()
 {
  daysPanel = new VerticalPanel();
  // Set initial values on checkboxes to match days already scheduled
  for (Integer dayScheduled : rescheduled.getDayNums())
  {
   switch (dayScheduled)
   {
   case 1:
    mondayCB.setValue(true);
    break;
   case 2:
    tuesdayCB.setValue(true);
    break;
   case 3:
    wednesdayCB.setValue(true);
    break;
   case 4:
    thursdayCB.setValue(true);
    break;
   case 5:
    fridayCB.setValue(true);
    break;
   }
  }
  daysPanel.add(new HTML("Select the days on which to schedule this course:"));
  daysPanel.add(mondayCB);
  daysPanel.add(tuesdayCB);
  daysPanel.add(wednesdayCB);
  daysPanel.add(thursdayCB);
  daysPanel.add(fridayCB);
 }

 public ArrayList<Integer> getDays()
 {
  ArrayList<Integer> days = new ArrayList<Integer>();
  if (mondayCB.getValue())
  {
   days.add(1);
  }
  if (tuesdayCB.getValue())
  {
   days.add(2);
  }
  if (wednesdayCB.getValue())
  {
   days.add(3);
  }
  if (thursdayCB.getValue())
  {
   days.add(4);
  }
  if (fridayCB.getValue())
  {
   days.add(5);
  }
  return days;
 }

 public OldScheduleItemGWT getItem()
 {
  return rescheduled;
 }

 public int getRow()
 {
  return row;
 }
}
