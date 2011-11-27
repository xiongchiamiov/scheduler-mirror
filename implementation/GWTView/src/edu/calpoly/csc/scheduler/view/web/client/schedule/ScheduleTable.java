package edu.calpoly.csc.scheduler.view.web.client.schedule;

import java.util.ArrayList;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;

import edu.calpoly.csc.scheduler.view.web.shared.ScheduleItemGWT;

public class ScheduleTable extends FlexTable
{
 // The scheduleViewWidget that this schedule resides in
 private ScheduleViewWidget scheduleController;
 // For each row, this variable holds the column which aligns to each day
 private ArrayList<ArrayList<Integer>> columnsOfDays;
 private static final int numberOfTimeSlots = 30;
 private static final int numberOfDays = 5;
 // For each day, this variable holds the number of columns spanned by that
 // day
 private ArrayList<Integer> dayColumnSpans;
 public static String times[] = { "7:00am", "7:30am", "8:00am", "8:30am", "9:00am",
   "9:30am", "10:00am", "10:30am", "11:00am", "11:30am", "12:00pm", "12:30pm",
   "1:00pm", "1:30pm", "2:00pm", "2:30pm", "3:00pm", "3:30pm", "4:00pm",
   "4:30pm", "5:00pm", "5:30pm", "6:00pm", "6:30pm", "7:00pm", "7:30pm",
   "8:00pm", "8:30pm", "9:00pm", "9:30pm" };

 public ScheduleTable(ScheduleViewWidget scheduleController)
 {
  this.scheduleController = scheduleController;
 }

 /**
  * Places the days Mon-Fri as the first row in the schedule
  */
 void setDaysOfWeek()
 {
  String days[] = { "Monday", "Tuesday", "Wednesday", "Thursday", "Friday" };
  int i = 0;

  for (i = 0; i < days.length; i++)
  {
   setWidget(0, i + 1, new HTML("<b>" + days[i] + "</b>"));
  }
 }

 /**
  * Places the times 7am-10pm at half hour increments as the rows in the first
  * column in the schedule.
  */
 void setTimes()
 {
  int i;

  for (i = 0; i < times.length; i++)
  {
   setWidget(i + 1, 0, new HTML("<b>" + times[i] + "</b>"));
  }
 }

 /**
  * Places a ScheduleCell in each cell for dragging and dropping schedule items.
  */
 void placePanels()
 {
  int row, col;
  ScheduleCell schdCell;

  for (row = 1; row <= numberOfTimeSlots; row++)
  {
   for (col = 1; col <= numberOfDays; col++)
   {
    schdCell = new ScheduleCell(scheduleController);
    schdCell.setWidget(new HTML("&nbsp"));
    setWidget(row, col, schdCell);
    schdCell.setRow(row);
    // schdCell.setCol(col);
    getFlexCellFormatter().removeStyleName(row, col, "scheduleItemNoConflict");
    getFlexCellFormatter().removeStyleName(row, col, "scheduleItemConflicted");
   }
  }
 }

 /**
  * Lays out an initial empty schedule with just days columns and time rows.
  */
 void layoutDaysAndTimes()
 {
  addStyleName("scheduleTable");
  setDaysOfWeek();
  setTimes();
 }

 /**
  * Resets days Mon, Tue, Wed, Thu, Fri to align with columns 1, 2, 3, 4, and 5
  */
 void resetColumnsOfDays()
 {
  int timeRow, dayCol;
  ArrayList<Integer> cods;
  columnsOfDays = new ArrayList<ArrayList<Integer>>();

  for (timeRow = 0; timeRow < numberOfTimeSlots; timeRow++)
  {
   cods = new ArrayList<Integer>();
   for (dayCol = 1; dayCol <= numberOfDays + 1; dayCol++)
   {
    cods.add(new Integer(dayCol));
   }
   columnsOfDays.add(cods);
  }
 }

 /**
  * Resets each day column to span one column.
  */
 void resetDayColumnSpans()
 {
  int day;
  FlexCellFormatter formatter = getFlexCellFormatter();

  dayColumnSpans = new ArrayList<Integer>();
  for (day = 1; day <= 5; day++)
  {
   formatter.setColSpan(0, day, 1);
   dayColumnSpans.add(new Integer(1));
  }
  dayColumnSpans.add(new Integer(1));
 }

 /**
  * Resets each row to span one row.
  */
 void resetRowSpans()
 {
  int row, col;
  FlexCellFormatter formatter = getFlexCellFormatter();
  for (row = 1; row <= numberOfTimeSlots; row++)
  {
   for (col = 1; col <= numberOfDays; col++)
   {
    formatter.setRowSpan(row, col, 1);
   }
  }
 }

 /**
  * Converts a time to a row in the table.
  * 
  * @param hour
  *         The hour of the time being converted
  * @param overHalfHour
  *         Whether the time goes over a half hour
  * @return The row corresponding to the provided time
  */
 public static int getRowFromTime(int hour, boolean overHalfHour)
 {
  int halfHourRow = 0;
  if (overHalfHour)
  {
   halfHourRow = 1;
  }
  return (2 * hour) - 13 + halfHourRow;
 }

 /**
  * For a given schedule item at a given day, counts the maximum number of
  * overlaps at all times that this schedule item will occupy. Looks for
  * overlaps with schedule items already placed.
  * 
  * @param toBePlaced
  *         The item for which overlaps will be counted.
  * @param day
  *         The day to search on.
  * @return The most times this item overlaps with other items.
  */
 private int overlaps(ScheduleItemGWT toBePlaced, int day)
 {
  ArrayList<Integer> occupiedDays;
  int h;
  int overlapCount = 0;
  int maxOverlap = 0;
  int startRow = getRowFromTime(toBePlaced.getStartTimeHour(),
    toBePlaced.startsAfterHalf());
  int endRow = getRowFromTime(toBePlaced.getEndTimeHour(),
    toBePlaced.endsAfterHalf()) - 1;
  int occupiedEndRow, occupiedStartRow;

  // Examine each time this item will occupy
  for (h = startRow; h <= endRow; h++)
  {
   // Examine each item already placed on schedule
   for (ScheduleItemGWT item : scheduleController.getItemsInSchedule())
   {
    if (item.isPlaced())
    {
     occupiedDays = item.getDayNums();
     occupiedStartRow = getRowFromTime(item.getStartTimeHour(),
       item.startsAfterHalf());
     occupiedEndRow = getRowFromTime(item.getEndTimeHour(),
       item.endsAfterHalf()) - 1;
     // Examine each day the placed item occupies
     for (int occDay : occupiedDays)
     {
      /*
       * If the item placed item occupies times at the same time and same day
       * then increment the number of overlaps at the examined time.
       */
      if (occDay == day && h >= occupiedStartRow && h <= occupiedEndRow)
      {
       overlapCount++;
      }
     }
    }
   }
   // Retain a count of the most overlaps at each time.
   maxOverlap = Math.max(maxOverlap, overlapCount);
   overlapCount = 0;
  }
  return maxOverlap;
 }

 /**
  * Expands a day column to a span that is one bigger than its previous span.
  * 
  * @param day
  *         The day to expand.
  */
 private void expandDay(int day)
 {
  int i, j, col;
  FlexCellFormatter formatter = getFlexCellFormatter();
  HTML dayText;
  ScheduleCell cellPanel;

  // Increase the column span count for this day
  dayColumnSpans.set(day - 1, dayColumnSpans.get(day - 1) + 1);
  // Retain the day text that was in this column
  dayText = (HTML) getWidget(0, day);
  // Increment the column span
  formatter.setColSpan(0, day, dayColumnSpans.get(day - 1));
  setWidget(0, day, dayText);

  // Add a new column of cells underneath this day heading
  for (i = 1; i <= numberOfTimeSlots; i++)
  {
   col = columnsOfDays.get(i - 1).get(day - 1);
   insertCell(i, col);
   cellPanel = new ScheduleCell(scheduleController);
   cellPanel.setRow(i);
   // cellPanel.setCol(col);
   cellPanel.setWidget(new HTML("&nbsp"));
   setWidget(i, col, cellPanel);

   // The new column will bump columns of following days forward by one
   for (j = day; j <= numberOfDays; j++)
   {
    columnsOfDays.get(i - 1).set(j, columnsOfDays.get(i - 1).get(j) + 1);
   }
  }
 }

 /**
  * Places a schedule item on schedule.
  * 
  * @param placedSchdItem
  *         The item to be placed
  * @param filteredDays
  *         The days on which the item should not show up
  */
 void placeScheduleItem(ScheduleItemGWT placedSchdItem,
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

  // For each day on which the item is scheduled
  for (int dayNum : schdItemDays)
  {
   // Don't place the item on a filtered day
   if (!filteredDays.contains(dayNum))
   {
    /*
     * Get the maximum number of times this item will overlap with already
     * placed items
     */
    overlapCount = overlaps(placedSchdItem, dayNum);
    /*
     * If the overlap count is larger than the span of the day column, expand
     * the column
     */
    if (overlapCount >= dayColumnSpans.get(dayNum - 1))
    {
     expandDay(dayNum);
    }
    // Get the column which this day aligns with
    dayCol = columnsOfDays.get(rowRangeStart - 1).get(dayNum - 1).intValue();
    schdItem = new ScheduleItemHTML(placedSchdItem);
    scheduleController.getItemDragController().makeDraggable(schdItem);
    // Set the schedule item to span rows which its time occupies
    getFlexCellFormatter().setRowSpan(rowRangeStart, dayCol,
      rowRangeEnd - rowRangeStart);
    // Place the schedule item at it's start time
    ((ScheduleCell) getWidget(rowRangeStart, dayCol)).setScheduleItem(schdItem,
      getFlexCellFormatter().getElement(rowRangeStart, dayCol).getOffsetHeight());
    
    if (placedSchdItem.isConflicted())
    {
     getFlexCellFormatter().addStyleName(rowRangeStart, dayCol,
       "scheduleItemConflicted");
    } else
    {
     getFlexCellFormatter().addStyleName(rowRangeStart, dayCol,
       "scheduleItemNoConflict");
    }
    // The next schedule item placed at this time will go 1 column
    // to the right
    cods = columnsOfDays.get(rowRangeStart - 1);
    cods.set(dayNum - 1, cods.get(dayNum - 1) + 1);
    columnsOfDays.set(rowRangeStart - 1, cods);
    /*
     * Every row which was spanned upon will bump one column forward, like this:
     * |0,0|0,1|0,2|0,3| Cell 0,0 spans 3 rows |0,0|0,1|0,2|0,3|
     * |1,0|1,1|1,2|1,3| --------------------> |0,0|1,0|1,1|1,2|1,3|
     * |2,0|2,1|2,2|2,3| |0,0|2,0|2,1|2,2|2,3| |3,0|3,1|3,2|3,3|
     * |3,0|3,1|3,2|3,3|
     */
    for (j = rowRangeStart; j < rowRangeEnd - 1; j++)
    {
     cods = columnsOfDays.get(j);
     // Offset the column which a day aligns with by -1 for each
     // following day
     for (k = dayNum; k <= numberOfDays; k++)
     {
      cods.set(k, cods.get(k) - 1);
     }
     columnsOfDays.set(j, cods);
     // Remove excess cell created at the end of this row
     removeCell(j + 1, (getCellCount(j + 1) - 1));
    }
   }
  }
  placedSchdItem.setPlaced(true);
 }

 /**
  * Remove cells which are not under any day column.
  */
 void trimExtraCells()
 {
  int i, cellsInRow;
  for (i = 1; i <= numberOfTimeSlots; i++)
  {
   for (cellsInRow = getCellCount(i); cellsInRow > 6; cellsInRow--)
   {
    removeCell(i, cellsInRow - 1);
   }
  }
 }

}
