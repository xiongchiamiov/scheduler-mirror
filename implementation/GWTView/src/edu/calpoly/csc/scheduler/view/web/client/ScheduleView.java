package edu.calpoly.csc.scheduler.view.web.client;

import java.util.ArrayList;
import java.util.Vector;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;

import edu.calpoly.csc.scheduler.model.schedule.Schedule;
import edu.calpoly.csc.scheduler.model.schedule.ScheduleItem;
import edu.calpoly.csc.scheduler.view.web.shared.gwtScheduleItem;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class ScheduleView
{
 /**
  * The message displayed to the user when the server cannot be reached or
  * returns an error.
  */
 private static final String SERVER_ERROR = "An error occurred while "
   + "attempting to contact the server. Please check your network "
   + "connection and try again.";

 private HorizontalPanel mainPanel = new HorizontalPanel();
 private FlexTable scheduleGrid = new FlexTable();

 // Places the days Mon-Fri as the first row in the schedule
 private void setDaysOfWeek()
 {
  String days[] = { "Monday", "Tuesday", "Wednesday", "Thursday", "Friday" };
  int i = 0;

  for (i = 0; i < days.length; i++)
  {
   scheduleGrid.setText(0, i + 1, days[i]);
  }
 }

 /*
  * Places the times 7am-10pm at half hour increments as the rows in thefirst
  * column in the schedule.
  */
 private void setTimes()
 {
  String times[] = { "7:00am", "7:30am", "8:00am", "8:30am", "9:00am",
    "9:30am", "10:00am", "10:30am", "11:00am", "11:30am", "12:00pm", "12:30pm",
    "1:00pm", "1:30pm", "2:00pm", "2:30pm", "3:00pm", "3:30pm", "4:00pm",
    "4:30pm", "5:00pm", "5:30pm", "6:00pm", "6:30pm", "7:00pm", "7:30pm",
    "8:00pm", "8:30pm", "9:00pm", "9:30pm", "10:00pm" };
  int i = 0;
  for (i = 0; i < times.length; i++)
  {
   scheduleGrid.setText(i + 1, 0, times[i]);
  }
 }

 private void layOutDaysAndTimes()
 {
  scheduleGrid.setBorderWidth(1);
  setDaysOfWeek();
  setTimes();
 }

 private int getRowFromTime(int time)
 {
  return (2*time)-13;
 }
 
 private void placeScheduleItem(gwtScheduleItem placedSchdItem)
 {
  int rowRange[] = new int[2];
  int schdItemDays[];
  int i = 0;

  rowRange[0] = getRowFromTime(placedSchdItem.getStartTime());
  rowRange[1] = getRowFromTime(placedSchdItem.getEndTime());
  schdItemDays = placedSchdItem.getDayNums();

  for (i = 0; i < schdItemDays.length; i++)
  {
   scheduleGrid.setWidget(rowRange[0], schdItemDays[i], new HTML(placedSchdItem.toString()));
   scheduleGrid.getFlexCellFormatter().setRowSpan(rowRange[0], schdItemDays[i],
    rowRange[1] - rowRange[0]);
  }
 }

 public void showScheduleView(GreetingServiceAsync greetingService)
 {
  final ArrayList<gwtScheduleItem> scheduleItems = new ArrayList<gwtScheduleItem>();

  RootPanel.get().clear();
  layOutDaysAndTimes();

  greetingService
    .getGWTScheduleItems(new AsyncCallback<ArrayList<gwtScheduleItem>>()
    {
     public void onFailure(Throwable caught)
     {
      Window.alert("Failed to get schedule: " + caught.toString());
     }

     public void onSuccess(ArrayList<gwtScheduleItem> result)
     {
      if (result != null)
      {
       Window.alert("Retrieved " + result.size() + " items");
       for(gwtScheduleItem schdItem : result)
       {
        placeScheduleItem(schdItem);
       }
      }
     }
    });

  mainPanel.add(scheduleGrid);
  RootPanel.get().add(mainPanel);
 }
}
