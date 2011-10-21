package edu.calpoly.csc.scheduler.view.web.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;

import edu.calpoly.csc.scheduler.view.web.shared.gwtScheduleItem;

public class ScheduleViewWidget implements CloseHandler<PopupPanel>
{
 private GreetingServiceAsync greetingService;
 private ArrayList<gwtScheduleItem> scheduleItems = new ArrayList<gwtScheduleItem>();
 private ArrayList<String> professorList = new ArrayList<String>();
 private ListBox professorListBox = new ListBox(true);
 private VerticalPanel mainPanel = new VerticalPanel();
 private FlexTable scheduleGrid = new FlexTable();
 private HorizontalPanel interfacePanel = new HorizontalPanel();
 private ArrayList<ArrayList<Integer>> columnsOfDays;
 private static final int numberOfTimeSlots = 30;
 private static final int numberOfDays = 5;
 private ArrayList<Integer> dayColumnSpans;
 private FiltersViewWidget filtersDialog = new FiltersViewWidget();
 
 private void resetColumnsOfDays()
 {
  int timeRow, dayCol;
  ArrayList<Integer> cods;
  columnsOfDays  = new ArrayList<ArrayList<Integer>>();
  
  for(timeRow = 0; timeRow < numberOfTimeSlots; timeRow++)
  {
   cods = new ArrayList<Integer>();
   for(dayCol = 1; dayCol <= numberOfDays+1; dayCol++)
   {
    cods.add(new Integer(dayCol));
   }
   columnsOfDays.add(cods);
  }
 }
 
 private void resetDayColumnSpans()
 {
  int day;
  FlexCellFormatter formatter = scheduleGrid.getFlexCellFormatter();
  
  dayColumnSpans = new ArrayList<Integer>();
  for(day = 1; day <= 5; day++)
  {
   formatter.setColSpan(0, day, 1);
   dayColumnSpans.add(new Integer(1));
  }
  dayColumnSpans.add(new Integer(1));
 }
 
 private void resetRowSpans()
 {
  int row, col;
  FlexCellFormatter formatter = scheduleGrid.getFlexCellFormatter();
  for(row = 1; row <= numberOfTimeSlots; row++)
  {
   for(col = 1; col <= numberOfDays; col++)
   {
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
   scheduleGrid.setWidget(0, i + 1, new HTML("<b>"+days[i]+"</b>"));
  }
 }

 /*
  * Places the times 7am-10pm at half hour increments as the rows in the first
  * column in the schedule.
  */
 private void setTimes()
 {
  String times[] = { "7:00am", "7:30am", "8:00am", "8:30am", "9:00am",
    "9:30am", "10:00am", "10:30am", "11:00am", "11:30am", "12:00pm", "12:30pm",
    "1:00pm", "1:30pm", "2:00pm", "2:30pm", "3:00pm", "3:30pm", "4:00pm",
    "4:30pm", "5:00pm", "5:30pm", "6:00pm", "6:30pm", "7:00pm", "7:30pm",
    "8:00pm", "8:30pm", "9:00pm", "9:30pm"};
  int i;
  
  for (i = 0; i < times.length; i++)
  {
   scheduleGrid.setWidget(i + 1, 0, new HTML("<b>"+times[i]+"</b>"));
  }
 }

 private void layoutDaysAndTimes()
 {
  scheduleGrid.setBorderWidth(1);
  setDaysOfWeek();
  setTimes();
  resetRowSpans();
  mainPanel.add(scheduleGrid);
 }

 private int getRowFromTime(int hour, boolean overHalfHour)
 {
  int halfHourRow = 0;
  if(overHalfHour)
  {
   halfHourRow = 1;
  }
  return (2*hour)-13+halfHourRow;
 }
 
 private int overlaps(gwtScheduleItem toBePlaced, int day)
 {
  ArrayList<Integer> occupiedDays;
  int h, i;  
  int overlapCount = 0;
  int maxOverlap = 0;
  int startRow = 
   getRowFromTime(toBePlaced.getStartTimeHour(), 
    toBePlaced.startsAfterHalf());
  int endRow =
   getRowFromTime(toBePlaced.getEndTimeHour(), 
    toBePlaced.endsAfterHalf()) - 1;
  int occupiedEndRow, occupiedStartRow;
  
  for(h = startRow; h <= endRow; h++)
  {
   for(gwtScheduleItem item : scheduleItems)
   {
	if(item.isPlaced())
	{
     occupiedDays = item.getDayNums();
     occupiedStartRow = getRowFromTime(item.getStartTimeHour(), 
      item.startsAfterHalf());
     occupiedEndRow = getRowFromTime(item.getEndTimeHour(),
      item.endsAfterHalf()) - 1;
     for(int occDay : occupiedDays)
     {
      if(occDay == day && h >= occupiedStartRow 
          && h <= occupiedEndRow)
      {
       overlapCount++;
      }
     }
    }
   }
   maxOverlap = Math.max(maxOverlap, overlapCount);
   overlapCount = 0;
  }
  return maxOverlap;
 }  
 
 private void expandDay(int day)
 {
  int i, j;
  Integer dayColumn;
  Integer dayColSpan;
  FlexCellFormatter formatter = scheduleGrid.getFlexCellFormatter();
  HTML dayText;
  
  dayColSpan = dayColumnSpans.get(day-1).intValue()+1;
  dayColumnSpans.set(day-1, dayColSpan);
  dayText = (HTML)scheduleGrid.getWidget(0, day);
  formatter.setColSpan(0, day, dayColSpan);
  scheduleGrid.setWidget(0, day, dayText);

  for(i = 1; i <= numberOfTimeSlots; i++)
  {
   scheduleGrid.insertCell(i, columnsOfDays.get(i-1).get(day-1).intValue());
   for(j = day; j <= numberOfDays; j++)
   {
    dayColumn = columnsOfDays.get(i-1).get(j).intValue() + 1;
    columnsOfDays.get(i-1).set(j, dayColumn);
   }
  }
 }
 
 private void placeScheduleItem(gwtScheduleItem placedSchdItem,
                                 ArrayList<Integer> filteredDays)
 {
  ArrayList<Integer> schdItemDays;
  int j, k, dayCol, rowRangeStart, rowRangeEnd, overlapCount;
  ArrayList<Integer> cods;
  rowRangeStart = getRowFromTime(placedSchdItem.getStartTimeHour(),
		                          placedSchdItem.startsAfterHalf());
  rowRangeEnd = getRowFromTime(placedSchdItem.getEndTimeHour(),
		                        placedSchdItem.endsAfterHalf());
  schdItemDays = placedSchdItem.getDayNums();
  
  for (int dayNum : schdItemDays)
  {
   if(!filteredDays.contains(dayNum))
   {   
    overlapCount = overlaps(placedSchdItem, dayNum);
    placedSchdItem.setOverlapCount(overlapCount);
    if(overlapCount >= dayColumnSpans.get(dayNum-1))
    {
 	 expandDay(dayNum);
    }
    dayCol = columnsOfDays.get(rowRangeStart-1).get(dayNum-1).intValue();
    scheduleGrid.setWidget(rowRangeStart, dayCol, new HTML(placedSchdItem.toString()));
    scheduleGrid.getFlexCellFormatter().setRowSpan(rowRangeStart, dayCol, 
     rowRangeEnd - rowRangeStart);
   
    cods = columnsOfDays.get(rowRangeStart-1);
    cods.set(dayNum-1, cods.get(dayNum-1) + 1);
    columnsOfDays.set(rowRangeStart-1, cods);
    for(j = rowRangeStart; j < rowRangeEnd-1; j++)
    {
     cods = columnsOfDays.get(j);
     for(k = dayNum; k <= numberOfDays; k++)
     {
      cods.set(k, cods.get(k)-1);
     }
     columnsOfDays.set(j, cods);
     scheduleGrid.removeCell(j+1, (scheduleGrid.getCellCount(j+1) - 1));
    }
   }
  }
  placedSchdItem.setPlaced(true);
 }
 
 private void getScheduleItems()
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
      resetSchedule();
      scheduleItems = new ArrayList<gwtScheduleItem>();
      for(gwtScheduleItem item : result)
      {
       scheduleItems.add(item);
      }
      filtersDialog.addItems(scheduleItems);
      filterScheduleItems();
     }
    }
   });
 }
 
 private void resetIsPlaced()
 {
  for(gwtScheduleItem item : scheduleItems)
  {
   item.setPlaced(false);
  }
 }
 
 private void trimExtraCells()
 {
  int i, cellsInRow;
  for(i = 1; i <= numberOfTimeSlots; i++)
  {
   for(cellsInRow = scheduleGrid.getCellCount(i); cellsInRow > 6; cellsInRow--)
   {
    scheduleGrid.removeCell(i, cellsInRow-1);
   }
  }
 }
 
 private void resetSchedule()
 {
  scheduleGrid.clear();
  resetColumnsOfDays();
  resetRowSpans();
  resetDayColumnSpans();
  resetIsPlaced();
  trimExtraCells();
  setTimes();
  setDaysOfWeek();
 }
 
 private void filterScheduleItems()
 {
  resetSchedule();
  ArrayList<String> filtInstructors = filtersDialog.getInstructors();
  ArrayList<String> filtCourses = filtersDialog.getCourses();
  ArrayList<String> filtRooms = filtersDialog.getRooms();
  ArrayList<Integer> filtDays = filtersDialog.getDays();
  for(gwtScheduleItem item : scheduleItems)
  {
   if(filtInstructors.contains(item.getProfessor()) && 
	   filtCourses.contains(item.getCourse()) && 
	    filtRooms.contains(item.getRoom()))
   {
    placeScheduleItem(item, filtDays);
   }
  }
 }
 
 private class GenerateScheduleClickHandler implements ClickHandler
 {
  public void onClick(ClickEvent event) 
  {
   getScheduleItems();	
  }
 }
 
 private void layoutInterface()
 {
  interfacePanel.add(new Button("Filters", 
   new ClickHandler()
   {
	public void onClick(ClickEvent event) 
	{
	 filtersDialog.center();	
	}  
   }));
  filtersDialog.addCloseHandler(this);
  interfacePanel.add(new Button("Generate Schedule", 
   new GenerateScheduleClickHandler()));
  mainPanel.add(interfacePanel);
 }
 
 public Widget getWidget(GreetingServiceAsync service)
 {
  greetingService = service;
  layoutInterface();
  layoutDaysAndTimes();
  
  return mainPanel;
 }

 public void onClose(CloseEvent<PopupPanel> event) 
 {
  filterScheduleItems();
 }
}
