package edu.calpoly.csc.scheduler.view.web.client.schedule;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

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
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import edu.calpoly.csc.scheduler.view.web.client.GreetingServiceAsync;
import edu.calpoly.csc.scheduler.view.web.client.views.LoadingPopup;
import edu.calpoly.csc.scheduler.view.web.shared.CourseGWT;
import edu.calpoly.csc.scheduler.view.web.shared.ScheduleItemGWT;
import edu.calpoly.csc.scheduler.view.web.shared.ScheduleItemList;

/**
 * This class generates a widget that displays a schedule in calendar form. It
 * also has a listbox of available classes and another listbox of classes to be
 * included in the schedule. The user may drag items between these lists and the
 * calendar.
 * 
 * @author Mike McMahon, Tyler Yero
 */
public class ScheduleViewWidget implements CloseHandler<PopupPanel>
{
 private GreetingServiceAsync greetingService;
 private ArrayList<ScheduleItemGWT> scheduleItems = new ArrayList<ScheduleItemGWT>();
 private VerticalPanel mainPanel = new VerticalPanel();
 private ScheduleTable scheduleGrid = new ScheduleTable(this);
 private HorizontalPanel interfacePanel = new HorizontalPanel();

 private FiltersViewWidget filtersDialog = new FiltersViewWidget();
 private PickupDragController dragController = new PickupDragController(
   RootPanel.get(), false);
 private TextBox searchBox;
 private DualListBox dualListBoxCourses;
 private ListBoxDragController listBoxDragController;
 private MouseListBox includedListBox;
 private MouseListBox availableListBox;
 private HorizontalPanel boxesAndSchedulePanel;

 /**
  * Registers all cells in schedule table as drop targets.
  */
 private void registerDrops()
 {
  Iterator<Widget> allCells = scheduleGrid.iterator();
  Widget cell;
  DropController dropController;

  while (allCells.hasNext())
  {
   cell = allCells.next();
   if (cell.getClass().equals(ScheduleCell.class))
   {
    dropController = new ScheduleCellDropController((ScheduleCell) cell, this,
      includedListBox, availableListBox);
    dragController.registerDropController(dropController);
    dualListBoxCourses.registerScheduleDrop(dropController);
    dualListBoxCourses.reregisterBoxDrops();
   }
  }
  dragController.registerDropController(dualListBoxCourses.getAvailableDropController());
  dragController.registerDropController(dualListBoxCourses.getIncludedDropController());  
 }

 /**
  * Retrieves a schedule items from a generated schedule from the server.
  */
 private void getScheduleItemsFromServer()
 {

  if (dualListBoxCourses.getIncludedCourses().size() == 0)
  {
   Window.alert("No courses to schedule");
   return;
  }

  greetingService.getGWTScheduleItems(dualListBoxCourses.getIncludedCourses(),
    new AsyncCallback<List<ScheduleItemGWT>>()
    {
     public void onFailure(Throwable caught)
     {
      Window.alert("Failed to get schedule: " + caught.toString());
     }

     public void onSuccess(List<ScheduleItemGWT> result)
     {
      if (result != null)
      {
       // Sort result by start times in ascending order
       Collections.sort(result);

       // Reset column and row spans, remove any items
       // already placed
       resetSchedule();
       scheduleItems = new ArrayList<ScheduleItemGWT>();
       for (ScheduleItemGWT item : result)
       {
        scheduleItems.add(item);
       }

       // Add the attributes of the retrieved items to the
       // filters list
       filtersDialog.addItems(scheduleItems);

       // Place schedule items with any previously set
       // filters
       filterScheduleItems(searchBox.getText());
       
       dualListBoxCourses.removeAllFromIncluded();
      }
     }
    });
 }

 /**
  * Sets all schedule items retrieved as not placed on the schedule.
  */
 private void resetIsPlaced()
 {
  for (ScheduleItemGWT item : scheduleItems)
  {
   item.setPlaced(false);
  }
 }

 /**
  * Remove all placed schedule items, return schedule to blank schedule.
  */
 private void resetSchedule()
 {
  scheduleGrid.clear();
  scheduleGrid.resetColumnsOfDays();
  scheduleGrid.resetRowSpans();
  scheduleGrid.resetDayColumnSpans();
  resetIsPlaced();
  scheduleGrid.trimExtraCells();
  scheduleGrid.setTimes();
  scheduleGrid.setDaysOfWeek();
  scheduleGrid.placePanels();
  dragController.unregisterDropControllers();
  dualListBoxCourses.getDragController().unregisterDropControllers();
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
  for (ScheduleItemGWT item : scheduleItems)
  {
   if (filtInstructors.contains(item.getProfessor())
     && filtCourses.contains(item.getCourseString())
     && filtRooms.contains(item.getRoom())
     && item.getSchdItemText().contains(search))
   {
    scheduleGrid.placeScheduleItem(item, filtDays);
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
   getScheduleItemsFromServer();
  }
 }

 /**
  * Lays out the buttons which will appear on this widget
  */
 private void layoutInterface()
 {
  searchBox = new TextBox();
  searchBox.addKeyPressHandler(new KeyPressHandler()
  {
   public void onKeyPress(KeyPressEvent event)
   {
    if (event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ENTER)
    {
     search();
    }
   }
  });
  interfacePanel.add(searchBox);
  interfacePanel.add(new Button("Filters", new ClickHandler()
  {
   public void onClick(ClickEvent event)
   {
    // Causes the filters dialog to appear in the center of this
    // widget
    filtersDialog.center();
   }
  }));

  /*
   * Causes this class' onClose method to be called when the filters dialog is
   * closed
   */
  filtersDialog.addCloseHandler(this);
  interfacePanel.add(new Button("Generate Schedule",
    new GenerateScheduleClickHandler()));
  
  interfacePanel.add(new Button("Save Schedule", new ClickHandler()
  {
   public void onClick(ClickEvent event)
   {
    // Causes the filters dialog to appear in the center of this
    // widget
    saveSchedule();
   }
  }));
  
  interfacePanel.add(new CSVButton(greetingService).getButton());
  
  
  mainPanel.add(interfacePanel);
 }

 /* Displays only schedule items which contain text in the search box */
 private void search()
 {
  filterScheduleItems(searchBox.getText());
 }

 /* Retrieves the course list and adds it to the available courses box */
 private void addCoursesToBoxes()
 {
  greetingService.getCourses(new AsyncCallback<List<CourseGWT>>()
  {
   @Override
   public void onFailure(Throwable caught)
   {
    Window.alert("Failed to retrieve courses");
    registerDrops();
   }

   @Override
   public void onSuccess(List<CourseGWT> result)
   {
    if (result != null)
    {
     if (result.size() > 10)
     {
      dualListBoxCourses.setListLength(result.size());
     }
     for (CourseGWT course : result)
     {
      dualListBoxCourses.addLeft(new CourseListItem(course, false));
     }
     registerDrops();
    }
   }
  });
 }

 /* Laysout the available and included courses boxes and the schedule */
 private void layoutBoxesAndSchedule()
 {
  boxesAndSchedulePanel = new HorizontalPanel();
  boxesAndSchedulePanel.setSpacing(2);
  boxesAndSchedulePanel
    .setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
  dualListBoxCourses = new DualListBox(10, "10em", 10, this);
  includedListBox = dualListBoxCourses.getIncludedListBox();
  availableListBox = dualListBoxCourses.getAvailableListBox();
  listBoxDragController = new ListBoxDragController(dualListBoxCourses);
  boxesAndSchedulePanel.add(dualListBoxCourses);
  scheduleGrid.layoutDaysAndTimes();
  scheduleGrid.placePanels();
  boxesAndSchedulePanel.add(scheduleGrid);
  // add some items to the list
  addCoursesToBoxes();
  mainPanel.add(boxesAndSchedulePanel);
 }

 /**
  * Returns this widget in its entirety.
  * 
  * @param service
  *         The server-side service which this widget will contact
  * @return This widget
  */
 public Widget getWidget(GreetingServiceAsync service)
 {
  greetingService = service;
  layoutInterface();
  layoutBoxesAndSchedule();
  final LoadingPopup loading = new LoadingPopup();
  
  loading.show();
  greetingService.getSchedule(
    new AsyncCallback<List<ScheduleItemGWT>>()
    {
     @Override
     public void onFailure(Throwable caught)
     {
      Window.alert("Failed to retrieve schedule.");
      loading.hide();
     }

     @Override
     public void onSuccess(List<ScheduleItemGWT> result)
     {
      scheduleItems = new ArrayList<ScheduleItemGWT>();
      for (ScheduleItemGWT item : result)
      {
       scheduleItems.add(item);
      }

      Collections.sort(scheduleItems);
      // Add the attributes of the retrieved items to the
      // filters list
      filtersDialog.addItems(scheduleItems);

      // Place schedule items with any previously set
      // filters
      filterScheduleItems(searchBox.getText());
      loading.hide();
     }
     
    });
  return mainPanel;
 }

 /**
  * Called when the filters dialog closes
  */
 public void onClose(CloseEvent<PopupPanel> event)
 {
  filterScheduleItems(searchBox.getText());
 }

 /* Converts a row to an hour */
 private int getHourFromRow(int row)
 {
  return (row / 2) + 6 + (row % 2);
 }

 /*
  * Returns true if a row is at a half hour (i.e. 7:30), false if a row is on
  * the hour (i.e. 7:00)
  */
 private boolean rowIsAtHalfHour(int row)
 {
  return row % 2 == 0;
 }

 /*
  * Called when a schedule item is dragged to a new position, or when a course
  * from one of the lists is dropped onto the schedule
  */
 public void moveItem(final ScheduleItemGWT scheduleItem,
   ArrayList<Integer> days, int row, final boolean inSchedule,
   final boolean fromIncluded)
 {
  final int startHour = getHourFromRow(row);
  final boolean atHalfHour = rowIsAtHalfHour(row);
  CourseGWT course = new CourseGWT();
  course.setDept(scheduleItem.getDept());
  course.setCatalogNum(scheduleItem.getCatalogNum());

  greetingService.rescheduleCourse(scheduleItem, days, startHour, atHalfHour,
    inSchedule, new AsyncCallback<ScheduleItemList>()
    {
     @Override
     public void onFailure(Throwable caught)
     {
      Window.alert("Failed to retrieve rescheduled item");
     }

     @Override
     public void onSuccess(ScheduleItemList rescheduled)
     {
      CourseGWT courseHolder;
      int sectionsIncluded, itemIndex;

      // If this course was dragged from a course list
      if (!inSchedule)
      {
       courseHolder = new CourseGWT();
       courseHolder.setDept(scheduleItem.getDept());
       courseHolder.setCatalogNum(scheduleItem.getCatalogNum());
       sectionsIncluded = includedListBox.getSectionsInBox(courseHolder);
       itemIndex = includedListBox.contains(new CourseListItem(courseHolder, true));
       // If the included list box contains sections of this course
       if (itemIndex >= 0)
       {
        // Decrement the section count in the included list box if this course
        // was dragged from the included list box or the number of combined
        // sections on the schedule and in the included list box is equal to the
        // total number of sections
        if (fromIncluded
          || sectionsIncluded + getSectionsOnSchedule(courseHolder) == availableListBox
            .getSectionsInBox(courseHolder))
        {
         // Decrement by one if there is more than one section
         if (sectionsIncluded > 1)
         {
          courseHolder = ((CourseListItem) includedListBox.getWidget(itemIndex))
            .getCourse();
          courseHolder.setNumSections(courseHolder.getNumSections() - 1);
          includedListBox
            .setWidget(itemIndex, new CourseListItem(courseHolder, true));
         }
         // Remove the course if there is only one section
         else
         {
          includedListBox.remove(includedListBox.getWidget(itemIndex));
         }
        }
       }
      }

      scheduleItems = new ArrayList<ScheduleItemGWT>();
      for (ScheduleItemGWT schdItem : rescheduled)
      {
       scheduleItems.add(schdItem);
      }
      Collections.sort(scheduleItems);

      filtersDialog.addItems(scheduleItems);
      filterScheduleItems(searchBox.getText());

      if (rescheduled.conflict.length() > 0)
      {
       Window.alert(rescheduled.conflict);
      }
     }
    });
 }

 public int getSectionsOnSchedule(CourseGWT course)
 {
  String dept = course.getDept();
  int catalogNum = course.getCatalogNum();
  int count = 0;

  for (ScheduleItemGWT item : scheduleItems)
  {
   if (item.getDept() == dept && item.getCatalogNum() == catalogNum)
   {
    count++;
   }
  }

  return count;
 }

 /* Highlights the border of a row */
 public void highlightRow(int row)
 {
  scheduleGrid.getRowFormatter().addStyleName(row, "highlightedBorder");
 }

 /* Unhighlights the border of a row */
 public void unhighlightRow(int row)
 {
  scheduleGrid.getRowFormatter().removeStyleName(row, "highlightedBorder");
  scheduleGrid.getRowFormatter().removeStyleName(row - 1, "highlightedBorder");
 }

 /* Returns all schedule items retrieved from the model's schedule object */
 public ArrayList<ScheduleItemGWT> getItemsInSchedule()
 {
  return scheduleItems;
 }

 /* Returns the drag controller for items on the schedule */
 public PickupDragController getItemDragController()
 {
  return dragController;
 }

 public void removeItem(ScheduleItemGWT removed)
 {
  greetingService.removeScheduleItem(removed, 
    new AsyncCallback<List<ScheduleItemGWT>>()
    {
     @Override
     public void onFailure(Throwable caught)
     {
      Window.alert("Failed to remove item.");
     }

     @Override
     public void onSuccess(List<ScheduleItemGWT> result)
     {
      scheduleItems = new ArrayList<ScheduleItemGWT>();
      for (ScheduleItemGWT schdItem : result)
      {
       scheduleItems.add(schdItem);
      }
      Collections.sort(scheduleItems);

      filtersDialog.addItems(scheduleItems);
      filterScheduleItems(searchBox.getText());
     }   
    });
 }
 
 public void saveSchedule()
 {
  Window.alert("This feature is not yet functional, so this schedule won't be saved if the page is refreshed.");
  /*This throws exceptions!
  final LoadingPopup loading = new LoadingPopup();
  loading.show();
  greetingService.saveSchedule(
    new AsyncCallback<Void>()
    {

     @Override
     public void onFailure(Throwable caught)
     {
      loading.hide();
      Window.alert("Failed to save schedule.");
     }

     @Override
     public void onSuccess(Void result)
     {
      loading.hide();
      Window.alert("Schedule successfully saved.");
     }
    });*/
 }
}
