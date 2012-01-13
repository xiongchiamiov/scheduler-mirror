package edu.calpoly.csc.scheduler.view.web.client.schedule;

import java.util.ArrayList;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.calpoly.csc.scheduler.view.web.shared.ScheduleItemGWT;

/**
 * A DialogBox which displays filters for viewing the schedule. Contains
 * methods which allow another widget to see which filters are set.
 * 
 * @author Mike McMahon
 */
public class FiltersViewWidget extends DialogBox
{
 private VerticalPanel mainPanel = new VerticalPanel();
 private HorizontalPanel filtersPanel = new HorizontalPanel();
 private VerticalPanel daysPanel = new VerticalPanel();
 private VerticalPanel instructorsPanel = new VerticalPanel();
 private VerticalPanel coursesPanel = new VerticalPanel();
 private VerticalPanel roomsPanel = new VerticalPanel();
 private VerticalPanel timesPanel = new VerticalPanel();
 private CheckBox mondayCB = new CheckBox("Monday");
 private CheckBox tuesdayCB = new CheckBox("Tuesday");
 private CheckBox wednesdayCB = new CheckBox("Wednesday");
 private CheckBox thursdayCB = new CheckBox("Thursday");
 private CheckBox fridayCB = new CheckBox("Friday");
 private ListBox instructorsLB = new ListBox(true);
 private ArrayList<String> instructors = new ArrayList<String>();
 private ListBox coursesLB = new ListBox(true);
 private ArrayList<String> courses = new ArrayList<String>();
 private ListBox roomsLB = new ListBox(true);
 private ArrayList<String> rooms = new ArrayList<String>();
 private ListBox timesLB = new ListBox(true); 

 public FiltersViewWidget()
 {
  super(false);
  layoutDays();
  layoutInstructors();
  layoutCourses();
  layoutRooms();
  layoutTimes();
  mainPanel.add(filtersPanel);
  mainPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
  mainPanel.add(new Button("Set Filters", new ClickHandler()
  {
   public void onClick(ClickEvent event)
   {
    FiltersViewWidget.this.hide();
   }
  }));
  this.setWidget(mainPanel);
 }

 private void layoutTimes() 
 {
  int i;
  
  timesPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
  timesPanel.add(new HTML("<b>Times</b>"));
  timesLB.setVisibleItemCount(5);
  timesPanel.add(timesLB);
  filtersPanel.add(timesPanel);
  
  for(i = 0; i < ScheduleTable.times.length; i++)
  {
   timesLB.addItem(ScheduleTable.times[i]);
  }
 }

 private void layoutDays()
 {
  HTML daysLabel = new HTML("<b>Days</b>");
  
  daysLabel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
  daysPanel.setWidth("100px");
  daysPanel.add(daysLabel);
  mondayCB.setValue(true);
  daysPanel.add(mondayCB);
  tuesdayCB.setValue(true);
  daysPanel.add(tuesdayCB);
  wednesdayCB.setValue(true);
  daysPanel.add(wednesdayCB);
  thursdayCB.setValue(true);
  daysPanel.add(thursdayCB);
  fridayCB.setValue(true);
  daysPanel.add(fridayCB);
  filtersPanel.add(daysPanel);
 }

 private void layoutInstructors()
 {
  instructorsPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
  instructorsPanel.add(new HTML("<b>Instructors</b>"));
  instructorsLB.setVisibleItemCount(5);
  instructorsLB.setWidth("130px");
  instructorsPanel.add(instructorsLB);
  filtersPanel.add(instructorsPanel);
 }

 private void layoutCourses()
 {
  coursesPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
  coursesPanel.add(new HTML("<b>Courses</b>"));
  coursesLB.setVisibleItemCount(5);
  coursesLB.setWidth("130px");
  coursesPanel.add(coursesLB);
  filtersPanel.add(coursesPanel);
 }

 private void layoutRooms()
 {
  roomsPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
  roomsPanel.add(new HTML("<b>Rooms</b>"));
  roomsLB.setVisibleItemCount(5);
  roomsLB.setWidth("130px");
  roomsPanel.add(roomsLB);
  filtersPanel.add(roomsPanel);
 }

 public void addItems(ArrayList<ScheduleItemGWT> items)
 {
  String itemInstr;
  String itemCourse;
  String itemRoom;

  instructors = new ArrayList<String>();
  instructorsLB.clear();
  courses = new ArrayList<String>();
  coursesLB.clear();
  rooms = new ArrayList<String>();
  roomsLB.clear();
  
  for (ScheduleItemGWT item : items)
  {
   itemInstr = item.getProfessor();
   itemCourse = item.getCourseString();
   itemRoom = item.getRoom();

   instructors.add(itemInstr);
   instructorsLB.addItem(itemInstr);
   
   courses.add(itemCourse);
   coursesLB.addItem(itemCourse);
   
   rooms.add(itemRoom);
   roomsLB.addItem(itemRoom);
  }
  
  filtersPanel.setWidth("100%");
 }

 public ArrayList<String> getInstructors()
 {
  int i, instructorCount;
  ArrayList<String> ins = new ArrayList<String>();
  
  if (instructorsLB.getSelectedIndex() < 0)
  {
   return instructors;
  }
  
  instructorCount = instructorsLB.getItemCount();
  for (i = 0; i < instructorCount; i++)
  {
   if (instructorsLB.isItemSelected(i))
   {
    ins.add(instructors.get(i));
   }
  }
  return ins;
 }

 public ArrayList<String> getCourses()
 {
  int i, courseCount;
  ArrayList<String> crs = new ArrayList<String>();
  
  if (coursesLB.getSelectedIndex() < 0)
  {
   return courses;
  }
  
  courseCount = coursesLB.getItemCount();
  for (i = 0; i < courseCount; i++)
  {
   if (coursesLB.isItemSelected(i))
   {
    crs.add(courses.get(i));
   }
  }
  return crs;
 }

 public ArrayList<String> getRooms()
 {
  int i, roomCount;
  ArrayList<String> rms = new ArrayList<String>();
  
  if (roomsLB.getSelectedIndex() < 0)
  {
   return rooms;
  }
  
  roomCount = roomsLB.getItemCount();
  for (i = 0; i < roomCount; i++)
  {
   if (roomsLB.isItemSelected(i))
   {
    rms.add(rooms.get(i));
   }
  }
  return rms;
 }

 public ArrayList<Integer> getDays()
 {
  ArrayList<Integer> days = new ArrayList<Integer>();
  if (!mondayCB.getValue())
  {
   days.add(1);
  }
  if (!tuesdayCB.getValue())
  {
   days.add(2);
  }
  if (!wednesdayCB.getValue())
  {
   days.add(3);
  }
  if (!thursdayCB.getValue())
  {
   days.add(4);
  }
  if (!fridayCB.getValue())
  {
   days.add(5);
  }
  return days;
 }
 
 public ArrayList<Integer> getTimes()
 {
  int i, timesCount;
  ArrayList<Integer> selectedTimeRows = new ArrayList<Integer>();
  
  timesCount = timesLB.getItemCount();
  for(i = 0; i < timesCount; i++)
  {
   if(timesLB.isItemSelected(i))
   {
    selectedTimeRows.add(i + 1);
   }
  }
  
  return selectedTimeRows;
 }
}
