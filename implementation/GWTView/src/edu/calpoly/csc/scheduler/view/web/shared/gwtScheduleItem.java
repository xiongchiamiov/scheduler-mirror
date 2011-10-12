package edu.calpoly.csc.scheduler.view.web.shared;

import com.google.gwt.user.client.rpc.IsSerializable;

public class gwtScheduleItem implements IsSerializable, Comparable
{
 private String professor;
 private String courseDept;
 private int courseNum;
 private int section;
 private String days;
 private int dayNums[];
 private int startTimeHour;
 private int endTimeHour;
 //private int startTimeMin;
 //private int endTimeMin;
 private boolean placed = false;
 
 public gwtScheduleItem()
 {
  
 }
 
 public gwtScheduleItem(String prof, String dept, int cNum, int sec, String ds, int dNums[], int sth, int eth)
 {
  professor = prof;
  courseDept = dept;
  courseNum = cNum;
  section = sec;
  days = ds;
  dayNums = dNums;
  startTimeHour = sth;
  //startTimeMin = stm;
  endTimeHour = eth;
  //endTimeMin = etm;
 }
 
 public String toString()
 {
  return courseDept + courseNum + "-" + section + "<br>" + professor + "<br>" +
   startTimeHour + " - " + endTimeHour + "<br>" + days; 
 }
 
 public int getStartTime()
 {
  return startTimeHour;
 }
 
 public int getEndTime()
 {
  return endTimeHour;
 }
 
 public int[] getDayNums()
 {
  return dayNums;
 }
 
 public String getProfessor()
 {
  return professor;
 }

 public String getCourse()
 {
  return courseDept + courseNum;
 }
 public int compareTo(Object compared)
 {
  if(this.startTimeHour > ((gwtScheduleItem)compared).getStartTime())
  {
   return 1;
  }
  else if(this.startTimeHour < ((gwtScheduleItem)compared).getStartTime())
  {
   return -1;
  }
  return 0;
 }
 public boolean isPlaced()
 {
  return placed;
 }
 public void setPlaced(boolean p)
 {
  placed = p;
 }
}
