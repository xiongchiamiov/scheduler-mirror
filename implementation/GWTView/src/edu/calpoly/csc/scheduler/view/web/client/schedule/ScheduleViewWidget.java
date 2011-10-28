package edu.calpoly.csc.scheduler.view.web.client.schedule;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.allen_sauer.gwt.dnd.client.drop.DropController;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.KeyboardListener;
import com.google.gwt.user.client.ui.KeyboardListenerAdapter;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import edu.calpoly.csc.scheduler.view.web.client.GreetingServiceAsync;
import edu.calpoly.csc.scheduler.view.web.shared.ScheduleItemGWT;

/**
 * This class generates a widget that displays a schedule.
 * 
 * @author Mike McMahon
 */
public class ScheduleViewWidget implements CloseHandler<PopupPanel>
{
 private GreetingServiceAsync greetingService;
 private ArrayList<ScheduleItemGWT> scheduleItems = new ArrayList<ScheduleItemGWT>();
 private VerticalPanel mainPanel = new VerticalPanel();
 private FlexTable scheduleGrid = new FlexTable();
 private HorizontalPanel interfacePanel = new HorizontalPanel();
 //For each row, this variable holds the column which aligns to each day
 private ArrayList<ArrayList<Integer>> columnsOfDays;
 private static final int numberOfTimeSlots = 30;
 private static final int numberOfDays = 5;
 //For each day, this variable holds the number of columns spanned by that day
 private ArrayList<Integer> dayColumnSpans;
 private FiltersViewWidget filtersDialog = new FiltersViewWidget();
 PickupDragController dragController = 
  new PickupDragController(RootPanel.get(), false);
 TextBox searchBox;
 private ListBox listBoxAvailable;
 private ListBox listBoxIncluded;
 private HorizontalPanel boxesAndSchedulePanel;
 
 /**
  * Places a ScheduleCell in each cell for dragging and dropping schedule items.
  */
 private void placePanels()
 {
  int row, col;
  ScheduleCell schdCell;
  
  for(row = 1; row <= numberOfTimeSlots; row++)
  {
   for(col = 1; col <= numberOfDays; col++)
   {
	schdCell = new ScheduleCell(this);
	schdCell.setWidget(new HTML("&nbsp"));
	scheduleGrid.setWidget(row, col, schdCell);
	schdCell.setRow(row);
	//schdCell.setCol(col);
   }
  }
 }
 
 /**
  * Registers all cells in schedule table as drop targets.
  */
 private void registerDrops()
 {
  Iterator<Widget> allCells = scheduleGrid.iterator();
  Widget cell;
  DropController dropController;
  
  while(allCells.hasNext())
  {
   cell = allCells.next();
   if(cell.getClass().equals(ScheduleCell.class))
   {
    dropController = new ScheduleCellDropController((ScheduleCell)cell, this);
    dragController.registerDropController(dropController);
   }
  }
 }
 
 /**
  * Resets days Mon, Tue, Wed, Thu, Fri to align with columns 1, 2, 3, 4, and 5
  */
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
 
 /**
  * Resets each day column to span one column.
  */
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
 
 /**
  * Resets each row to span one row. 
  */
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
 
 /**
  * Places the days Mon-Fri as the first row in the schedule
  */
 private void setDaysOfWeek()
 {
  String days[] = { "Monday", "Tuesday", "Wednesday", "Thursday", "Friday" };
  int i = 0;

  for (i = 0; i < days.length; i++)
  {
   scheduleGrid.setWidget(0, i + 1, new HTML("<b>"+days[i]+"</b>"));
  }
 }

 /**
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

 /**
  * Lays out an initial empty schedule with just days columns and time rows.
  */
 private void layoutDaysAndTimes()
 {
  scheduleGrid.setBorderWidth(1);
  setDaysOfWeek();
  setTimes();
  resetRowSpans();
  placePanels();
  boxesAndSchedulePanel.add(scheduleGrid);
 }

 /**
  * Converts a time to a row in the table.
  * @param hour The hour of the time being converted
  * @param overHalfHour Whether the time goes over a half hour
  * @return The row corresponding to the provided time
  */
 private int getRowFromTime(int hour, boolean overHalfHour)
 {
  int halfHourRow = 0;
  if(overHalfHour)
  {
   halfHourRow = 1;
  }
  return (2*hour)-13+halfHourRow;
 }
 
 /**
  * For a given schedule item at a given day, counts the maximum number of
  * overlaps at all times that this schedule item will occupy.  Looks for
  * overlaps with schedule items already placed. 
  * @param toBePlaced The item for which overlaps will be counted.
  * @param day The day to search on.
  * @return The most times this item overlaps with other items.
  */
 private int overlaps(ScheduleItemGWT toBePlaced, int day)
 {
  ArrayList<Integer> occupiedDays;
  int h;
  int overlapCount = 0;
  int maxOverlap = 0;
  int startRow = 
   getRowFromTime(toBePlaced.getStartTimeHour(), 
    toBePlaced.startsAfterHalf());
  int endRow =
   getRowFromTime(toBePlaced.getEndTimeHour(), 
    toBePlaced.endsAfterHalf()) - 1;
  int occupiedEndRow, occupiedStartRow;
  
  //Examine each time this item will occupy
  for(h = startRow; h <= endRow; h++)
  {
   //Examine each item already placed on schedule
   for(ScheduleItemGWT item : scheduleItems)
   {
	if(item.isPlaced())
	{
     occupiedDays = item.getDayNums();
     occupiedStartRow = getRowFromTime(item.getStartTimeHour(), 
      item.startsAfterHalf());
     occupiedEndRow = getRowFromTime(item.getEndTimeHour(),
      item.endsAfterHalf()) - 1;
     //Examine each day the placed item occupies
     for(int occDay : occupiedDays)
     {
      /*If the item placed item occupies times at the same time and same day 
       * then increment the number of overlaps at the examined time.*/
      if(occDay == day && h >= occupiedStartRow 
          && h <= occupiedEndRow)
      {
       overlapCount++;
      }
     }
    }
   }
   //Retain a count of the most overlaps at each time.
   maxOverlap = Math.max(maxOverlap, overlapCount);
   overlapCount = 0;
  }
  return maxOverlap;
 }  
 
 /**
  * Expands a day column to a span that is one bigger than its previous span.
  * @param The day to expand.
  */
 private void expandDay(int day)
 {
  int i, j, col;
  FlexCellFormatter formatter = scheduleGrid.getFlexCellFormatter();
  HTML dayText;
  ScheduleCell cellPanel;
  
  //Increase the column span count for this day
  dayColumnSpans.set(day-1, dayColumnSpans.get(day-1) + 1);
  //Retain the day text that was in this column
  dayText = (HTML)scheduleGrid.getWidget(0, day);
  //Increment the column span
  formatter.setColSpan(0, day, dayColumnSpans.get(day - 1));
  scheduleGrid.setWidget(0, day, dayText);

  //Add a new column of cells underneath this day heading
  for(i = 1; i <= numberOfTimeSlots; i++)
  {
   col = columnsOfDays.get(i-1).get(day-1);
   scheduleGrid.insertCell(i, col);
   cellPanel = new ScheduleCell(this);
   cellPanel.setRow(i);
   //cellPanel.setCol(col);
   cellPanel.setWidget(new HTML("&nbsp"));
   scheduleGrid.setWidget(i, col, cellPanel);
   
   //The new column will bump columns of following days forward by one
   for(j = day; j <= numberOfDays; j++)
   {
    columnsOfDays.get(i-1).set(j, columnsOfDays.get(i-1).get(j) + 1);
   }
  }
 }
 
 /**
  * Places a schedule item on schedule.
  * @param placedSchdItem The item to be placed
  * @param filteredDays The days on which the item should not show up
  */
 private void placeScheduleItem(ScheduleItemGWT placedSchdItem,
                                 ArrayList<Integer> filteredDays)
 {
  ArrayList<Integer> schdItemDays;
  int j, k, dayCol, rowRangeStart, rowRangeEnd, overlapCount;
  ArrayList<Integer> cods;
  ScheduleItemHTML schdItem;
  
  rowRangeStart = getRowFromTime(placedSchdItem.getStartTimeHour(),
		                          placedSchdItem.startsAfterHalf());
  rowRangeEnd = getRowFromTime(placedSchdItem.getEndTimeHour(),
		                        placedSchdItem.endsAfterHalf());
  schdItemDays = placedSchdItem.getDayNums();
  
  //For each day on which the item is scheduled
  for (int dayNum : schdItemDays)
  {
   //Don't place the item on a filtered day
   if(!filteredDays.contains(dayNum))
   {   
	/*Get the maximum number of times this item will overlap with already 
	   placed items*/
    overlapCount = overlaps(placedSchdItem, dayNum);
    /*If the overlap count is larger than the span of the day column, 
     expand the column*/
    if(overlapCount >= dayColumnSpans.get(dayNum-1))
    {
 	 expandDay(dayNum);
    }
    //Get the column which this day aligns with
    dayCol = columnsOfDays.get(rowRangeStart-1).get(dayNum-1).intValue();
    schdItem = new ScheduleItemHTML(placedSchdItem);
    dragController.makeDraggable(schdItem);
    //Place the schedule item at it's start time
    ((SimplePanel)scheduleGrid.getWidget(rowRangeStart, dayCol)).setWidget(schdItem);
    //Set the schedule item to span rows which its time occupies
    scheduleGrid.getFlexCellFormatter().setRowSpan(rowRangeStart, dayCol, 
     rowRangeEnd - rowRangeStart);
   
    //The next schedule item placed at this time will go 1 column to the right
    cods = columnsOfDays.get(rowRangeStart-1);
    cods.set(dayNum-1, cods.get(dayNum-1) + 1);
    columnsOfDays.set(rowRangeStart-1, cods);
    /*Every row which was spanned upon will bump one column forward, like this:
     * |0,0|0,1|0,2|0,3| Cell 0,0 spans 3 rows |0,0|0,1|0,2|0,3|
     * |1,0|1,1|1,2|1,3| --------------------> |0,0|1,0|1,1|1,2|1,3|
     * |2,0|2,1|2,2|2,3|                       |0,0|2,0|2,1|2,2|2,3|
     * |3,0|3,1|3,2|3,3|                       |3,0|3,1|3,2|3,3|
     */
    for(j = rowRangeStart; j < rowRangeEnd-1; j++)
    {
     cods = columnsOfDays.get(j);
     //Offset the column which a day aligns with by -1 for each following day
     for(k = dayNum; k <= numberOfDays; k++)
     {
      cods.set(k, cods.get(k)-1);
     }
     columnsOfDays.set(j, cods);
     //Remove excess cell created at the end of this row
     scheduleGrid.removeCell(j+1, (scheduleGrid.getCellCount(j+1) - 1));
    }
   }
  }
  placedSchdItem.setPlaced(true);
 }
 
 /**
  * Retrieves a schedule items from a generated schedule from the server.
  */
 private void getScheduleItems()
 {
  greetingService.getGWTScheduleItems(
   new AsyncCallback<ArrayList<ScheduleItemGWT>>()
   {
    public void onFailure(Throwable caught)
    {
     Window.alert("Failed to get schedule: " + caught.toString());
    }

    public void onSuccess(ArrayList<ScheduleItemGWT> result)
    {
     if (result != null)
     {
      //Sort result by start times in ascending order
      Collections.sort(result);
      //Reset column and row spans, remove any items already placed
      resetSchedule();
      scheduleItems = new ArrayList<ScheduleItemGWT>();
      for(ScheduleItemGWT item : result)
      {
       scheduleItems.add(item);
      }
      //Add the attributes of the retrieved items to the filters list
      filtersDialog.addItems(scheduleItems);
      //Place schedule items with any previously set filters
      filterScheduleItems(searchBox.getText());
     }
    }
   });
 }
 
 /**
  * Sets all schedule items retrieved as not placed on the schedule.
  */
 private void resetIsPlaced()
 {
  for(ScheduleItemGWT item : scheduleItems)
  {
   item.setPlaced(false);
  }
 }
 
 /**
  * Remove cells which are not under any day column.
  */
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
 
 /**
  * Remove all placed schedule items, return schedule to blank schedule.
  */
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
  placePanels();
  dragController.unregisterDropControllers();
 }
 
 /**
  * Place schedule items which are not filtered.
  */
 private void filterScheduleItems(String search)
 {
  resetSchedule();
  ArrayList<String> filtInstructors = filtersDialog.getInstructors();
  ArrayList<String> filtCourses = filtersDialog.getCourses();
  ArrayList<String> filtRooms = filtersDialog.getRooms();
  ArrayList<Integer> filtDays = filtersDialog.getDays();
  for(ScheduleItemGWT item : scheduleItems)
  {
   if(filtInstructors.contains(item.getProfessor()) && 
	   filtCourses.contains(item.getCourse()) && 
	   filtRooms.contains(item.getRoom()) && 
	   item.getSchdItemText().contains(search))
   {
    placeScheduleItem(item, filtDays);
   }
  }
  registerDrops();
 }
 
 /**
  * Contains the method called when the "Generate Schedule" button is clicked.
  */
 private class GenerateScheduleClickHandler implements ClickHandler
 {
  public void onClick(ClickEvent event) 
  {
   getScheduleItems();	
  }
 }
 
 /** Lays out the buttons which will appear on this widget
  */
 private void layoutInterface()
 {
  searchBox = new TextBox();
  searchBox.addKeyPressHandler(
   new KeyPressHandler()
   {
	public void onKeyPress(KeyPressEvent event) 
	{
	 if(event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ENTER)
	 {
	  search();
	 }
	}
   });
  interfacePanel.add(searchBox);
  interfacePanel.add(new Button("Filters", 
   new ClickHandler()
   {
	public void onClick(ClickEvent event) 
	{
	 //Causes the filters dialog to appear in the center of this widget
	 filtersDialog.center();	
	}  
   }));
  /*Causes this class' onClose method to be called when the filters dialog is
   closed*/
  filtersDialog.addCloseHandler(this);
  interfacePanel.add(new Button("Generate Schedule", 
   new GenerateScheduleClickHandler()));
  mainPanel.add(interfacePanel);
 }
 
 private void search() 
 {
  filterScheduleItems(searchBox.getText());
 }

 private void addCoursesToBoxes()
 {
  listBoxAvailable.addItem("CPE 101");
  listBoxAvailable.addItem("CPE 402");
  listBoxAvailable.addItem("CPE 102");
  listBoxAvailable.addItem("CPE 103");
  listBoxAvailable.addItem("CPE 365");
  listBoxAvailable.addItem("CPE 491");
  listBoxAvailable.addItem("CPE 300");

  listBoxIncluded.addItem("CPE 508");
  listBoxIncluded.addItem("CPE 509");
  listBoxIncluded.addItem("CPE 570");
 }
 
 private void layoutBoxesAndSchedule()
 {
  boxesAndSchedulePanel = new HorizontalPanel();
  listBoxAvailable = new ListBox(true);
  listBoxIncluded = new ListBox(true);
  
  listBoxAvailable.setVisibleItemCount(10);
  listBoxIncluded.setVisibleItemCount(10);
  addCoursesToBoxes();
  boxesAndSchedulePanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
  boxesAndSchedulePanel.add(listBoxAvailable);
  boxesAndSchedulePanel.add(listBoxIncluded);
  layoutDaysAndTimes(); 
  mainPanel.add(boxesAndSchedulePanel);
 }
 
/**
  * Returns this widget in its entirety.
  * @param service The server-side service which this widget will contact
  * @return This widget
  */
 public Widget getWidget(GreetingServiceAsync service)
 {
  greetingService = service;
  layoutInterface();
  layoutBoxesAndSchedule();
  
  return mainPanel;
 }

 /**
  * Called when the filters dialog closes
  */
 public void onClose(CloseEvent<PopupPanel> event) 
 {
   filterScheduleItems(searchBox.getText());
 }

 private int getHourFromRow(int row)
 {
  return (row/2)+6+(row%2);
 }
 
 private boolean rowIsAtHalfHour(int row)
 {
  return row%2 == 0;
 }
 
 public void moveItem(final ScheduleItemGWT scheduleItem, 
  ArrayList<Integer> days, int row) 
 {
  final int startHour = getHourFromRow(row);
  final boolean atHalfHour = rowIsAtHalfHour(row);
  greetingService.rescheduleCourse(scheduleItem, days, startHour, 
   atHalfHour, 
   new AsyncCallback<ScheduleItemGWT>()
   {
    @Override
	public void onFailure(Throwable caught) 
    {
	 Window.alert("Failed to retrieve rescheduled item");			
    }
	@Override
	public void onSuccess(ScheduleItemGWT rescheduled) 
	{
	 if(rescheduled == null)
	 {
	  Window.alert("Course could not be rescheduled at time " + startHour + (atHalfHour? ":30" : ":00"));
	 }
	 else
	 {
	  scheduleItems.remove(scheduleItem);
	  scheduleItems.add(rescheduled);
	  Collections.sort(scheduleItems);
	 }
     filtersDialog.addItems(scheduleItems);
	 filterScheduleItems(searchBox.getText());			
	}
   });
 }
}
