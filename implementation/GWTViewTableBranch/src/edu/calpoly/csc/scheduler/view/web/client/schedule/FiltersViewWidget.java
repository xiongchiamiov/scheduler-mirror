package edu.calpoly.csc.scheduler.view.web.client.schedule;

import java.util.ArrayList;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import edu.calpoly.csc.scheduler.view.web.shared.ScheduleItemGWT;

public class FiltersViewWidget extends DialogBox
{
 private VerticalPanel mainPanel = new VerticalPanel();
 private HorizontalPanel filtersPanel = new HorizontalPanel();
 private VerticalPanel daysPanel = new VerticalPanel();
 private VerticalPanel instructorsPanel = new VerticalPanel();
 private VerticalPanel coursesPanel = new VerticalPanel();
 private VerticalPanel roomsPanel = new VerticalPanel();
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
  
 public FiltersViewWidget()
 {
  super(false);
  layoutDays();
  layoutInstructors();
  layoutCourses();
  layoutRooms();
  mainPanel.add(filtersPanel);
  mainPanel.add(new Button("Set Filters", 
   new ClickHandler()
   {
    public void onClick(ClickEvent event) 
    {
	 FiltersViewWidget.this.hide();  	
	} 
   }));
  this.setWidget(mainPanel);
 }
 
 private void layoutDays()
 {
  daysPanel.add(new HTML("Days"));
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
  instructorsPanel.add(new HTML("Instructors"));
  instructorsLB.setVisibleItemCount(5);
  instructorsPanel.add(instructorsLB);
  filtersPanel.add(instructorsPanel);
 }
 
 private void layoutCourses()
 {
  coursesPanel.add(new HTML("Courses"));
  coursesLB.setVisibleItemCount(5);
  coursesPanel.add(coursesLB);
  filtersPanel.add(coursesPanel);
 }
 
 private void layoutRooms()
 {
  roomsPanel.add(new HTML("Rooms"));
  roomsLB.setVisibleItemCount(5);
  roomsPanel.add(roomsLB);
  filtersPanel.add(roomsPanel);
 }
 
 public void addItems(ArrayList<ScheduleItemGWT> items)
 {
  String itemInstr;
  String itemCourse;
  String itemRoom;
  
  for(ScheduleItemGWT item : items)
  {
   itemInstr = item.getProfessor();
   itemCourse = item.getCourse();
   itemRoom = item.getRoom();
   
   if(!instructors.contains(itemInstr))
   {
	instructors.add(itemInstr);
	instructorsLB.addItem(itemInstr);
   }
   if(!courses.contains(itemCourse))
   {
	courses.add(itemCourse);
	coursesLB.addItem(itemCourse);
   }
   if(!rooms.contains(itemRoom))
   {
	rooms.add(itemRoom);
	roomsLB.addItem(itemRoom);
   }
  }
 }
 
 public ArrayList<String> getInstructors()
 {
  int i;
  ArrayList<String> ins = new ArrayList<String>();
  if(instructorsLB.getSelectedIndex() < 0)
  {
   return instructors;
  }
  for(i = 0; i < instructorsLB.getItemCount(); i++)
  {
   if(instructorsLB.isItemSelected(i))
   {
	ins.add(instructors.get(i));
   }
  }
  return ins;
 }
 
 public ArrayList<String> getCourses()
 {
  int i;
  ArrayList<String> crs = new ArrayList<String>();
  if(coursesLB.getSelectedIndex() < 0)
  {
   return courses;
  }
  for(i = 0; i < coursesLB.getItemCount(); i++)
  {
   if(coursesLB.isItemSelected(i))
   {
	crs.add(courses.get(i));
   }
  }
  return crs;
 }
 
 public ArrayList<String> getRooms()
 {
  int i;
  ArrayList<String> rms = new ArrayList<String>();
  if(roomsLB.getSelectedIndex() < 0)
  {
   return rooms;
  }
  for(i = 0; i < roomsLB.getItemCount(); i++)
  {
   if(roomsLB.isItemSelected(i))
   {
	rms.add(rooms.get(i));
   }
  }
  return rms;
 }
 
 public ArrayList<Integer> getDays()
 {
  ArrayList<Integer> days = new ArrayList<Integer>();
  if(!mondayCB.getValue())
  {
   days.add(1);
  }
  if(!tuesdayCB.getValue())
  {
   days.add(2);
  }
  if(!wednesdayCB.getValue())
  {
   days.add(3);
  }
  if(!thursdayCB.getValue())
  {
   days.add(4);
  }
  if(!fridayCB.getValue())
  {
   days.add(5);
  }
  return days;
 }
}
