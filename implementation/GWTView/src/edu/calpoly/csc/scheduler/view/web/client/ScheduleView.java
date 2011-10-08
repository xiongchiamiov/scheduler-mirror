package edu.calpoly.csc.scheduler.view.web.client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Vector;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;

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
 private GreetingServiceAsync greetingService;
 private ArrayList<gwtScheduleItem> scheduleItems = new ArrayList<gwtScheduleItem>();
 private ArrayList<String> professorList = new ArrayList<String>();
 private ListBox professorListBox = new ListBox(true);
 private HorizontalPanel mainPanel = new HorizontalPanel();
 private FlexTable scheduleGrid = new FlexTable();
 private VerticalPanel filtersPanel = new VerticalPanel();
 private ArrayList<ArrayList<Integer>> columnsOfDays;
 private static final int numberOfTimeSlots = 31;
 private static final int numberOfDays = 5;
 
 private void resetColumnsOfDays()
 {
  int timeRow, dayCol;
  ArrayList<Integer> cods;
  columnsOfDays  = new ArrayList<ArrayList<Integer>>();
  
  for(timeRow = 0; timeRow < numberOfTimeSlots; timeRow++)
  {
   cods = new ArrayList<Integer>();
   for(dayCol = 1; dayCol <= numberOfDays; dayCol++)
   {
    cods.add(new Integer(dayCol));
   }
   columnsOfDays.add(cods);
  }
 }
 
 private void resetRowSpans()
 {
  int row, col;
  FlexCellFormatter formatter = scheduleGrid.getFlexCellFormatter();
  for(row = 1; row <= numberOfTimeSlots; row++)
  {
   for(col = 1; col <= numberOfDays; col++)
   {
    System.out.println("working row " + row + " col " + col);
    formatter.setRowSpan(row, col, 1);
   }
  }
 }
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
  int i;
  
  for (i = 0; i < times.length; i++)
  {
   scheduleGrid.setText(i + 1, 0, times[i]);
  }
 }

 private void layoutDaysAndTimes()
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
  int rowRangeStart;
  int rowRangeEnd;
  int schdItemDays[];
  int i,j,k, day;
  ArrayList<Integer> cods;
  Integer dayColumn;

  rowRangeStart = getRowFromTime(placedSchdItem.getStartTime());
  rowRangeEnd = getRowFromTime(placedSchdItem.getEndTime());
  schdItemDays = placedSchdItem.getDayNums();

  for (i = 0; i < schdItemDays.length; i++)
  {
   day = columnsOfDays.get(rowRangeStart-1).get(schdItemDays[i]-1).intValue();
   scheduleGrid.setWidget(rowRangeStart, day, new HTML(placedSchdItem.toString()));
   scheduleGrid.getFlexCellFormatter().setRowSpan(rowRangeStart, day, 
    rowRangeEnd - rowRangeStart);
   for(j = rowRangeStart; j < rowRangeEnd-1 && j < columnsOfDays.size(); j++)
   {
    cods = columnsOfDays.get(j);
    for(k = schdItemDays[i]; k < numberOfDays; k++)
    {
     dayColumn = cods.get(k);
     dayColumn = new Integer(dayColumn.intValue()-1);
     cods.remove(k);
     cods.add(k, dayColumn);
    }
    columnsOfDays.remove(j);
    columnsOfDays.add(j, cods);
    scheduleGrid.removeCell(j+1, (scheduleGrid.getCellCount(j+1) - 1));
   }
  }
 }
 
 private void setProfessorList(ArrayList<gwtScheduleItem> schdItems)
 {
  for(gwtScheduleItem item : schdItems)
  {
   if(!professorList.contains(item.getProfessor()))
   {
    professorList.add(item.getProfessor());
   }
  }
 }
 
 private class professorSelected implements ChangeHandler
 {
  public void onChange(ChangeEvent event)
  {
   ArrayList<String> professorFilters = new ArrayList<String>();
   int i = 0;
   for(i = 0; i < professorListBox.getItemCount(); i++)
   {
    if(professorListBox.isItemSelected(i))
    {
     professorFilters.add(professorListBox.getItemText(i));
    }
   }
   filterScheduleItems(professorFilters);
  }
 }
 
 private void layoutProfList()
 {
  for(String prof : professorList)
  {
   professorListBox.addItem(prof);
  }
  professorListBox.setVisibleItemCount(5);
  professorListBox.addChangeHandler(new professorSelected());
  filtersPanel.add(new HTML("Select a professor to view their schedule.<br>" +
  		"Ctrl+Click to add or remove professors"));
  filtersPanel.add(professorListBox);
  mainPanel.add(filtersPanel);
 }
 
 private void layoutScheduleItems()
 {
  greetingService.getGWTScheduleItems(
   new AsyncCallback<ArrayList<gwtScheduleItem>>()
   {
    public void onFailure(Throwable caught)
    {
     Window.alert("Failed to get schedule: " + caught.toString());
    }

    public void onSuccess(ArrayList<gwtScheduleItem> result)
    {
     if (result != null)
     {
      Collections.sort(result);
      resetRowSpans();
      for(gwtScheduleItem item : result)
      {
       scheduleItems.add(item);
       placeScheduleItem(item);
      }
      mainPanel.add(scheduleGrid);
      setProfessorList(result);
      layoutProfList();
     }
    }
   });
 }
 
 private void filterScheduleItems(ArrayList<String> profNames)
 {
  scheduleGrid.clear();
  resetColumnsOfDays();
  resetRowSpans();
  for(gwtScheduleItem item : scheduleItems)
  {
   if(profNames.contains(item.getProfessor()) || profNames.size() == 0)
   {
    placeScheduleItem(item);
   }
  }
 }
 
 public void showScheduleView(GreetingServiceAsync service)
 {
  final ArrayList<gwtScheduleItem> scheduleItems;

  greetingService = service;
  resetColumnsOfDays();
  RootPanel.get().clear();
  layoutDaysAndTimes();
  
  layoutScheduleItems();
  
  RootPanel.get().add(mainPanel);
 }
}
