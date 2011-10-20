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
 private int startTimeMin;
 private int endTimeMin;
 private boolean placed = false;
 private int rowPlaced = -1;
 private int colPlaced = -1;
 private int overlapCount = -1;
 private String room;
 public gwtScheduleItem()
 {
  
 }
 
 public gwtScheduleItem(String prof, String dept, int cNum, int sec, String ds,
		                 int dNums[], int sth, int stm, int eth, int etm, 
		                  String rm)
 {
  professor = prof;
  courseDept = dept;
  courseNum = cNum;
  section = sec;
  days = ds;
  dayNums = dNums;
  startTimeHour = sth;
  startTimeMin = stm;
  endTimeHour = eth;
  endTimeMin = etm;
  room = rm;
 }
 
 public String toString()
 {
  String startHour, endHour, startMin, endMin;
  String startAmPm, endAmPm;
  
   startAmPm = startTimeHour >= 12 ? "pm" : "am";
   endAmPm = endTimeHour >= 12 ? "pm" : "am";
  
  if(startTimeHour > 12)
  {
   startHour = Integer.toString(startTimeHour-12);
  }
  else
  {
   startHour = Integer.toString(startTimeHour);
  }
  if(endTimeHour > 12)
  {
   endHour = Integer.toString(endTimeHour-12);
  }
  else
  {
   endHour = Integer.toString(endTimeHour);
  }
  if(startTimeMin < 10)
  {
   startMin = "0"+Integer.toString(startTimeMin);
  }
  else
  {
   startMin = Integer.toString(startTimeMin);
  }
  if(endTimeMin < 10)
  {
   endMin = "0"+Integer.toString(endTimeMin);
  }
  else
  {
   endMin = Integer.toString(endTimeMin);
  }

  return courseDept + courseNum + "-" + section + "<br>" + professor + "<br>" +
   startHour + ":" + startMin + startAmPm + " - " + endHour + ":" + endMin + 
   endAmPm + "<br>" + days; 
 }
 
 public int getStartTimeHour()
 {
  return startTimeHour;
 }
 
 public int getEndTimeHour()
 {
  return endTimeHour;
 }
 
 public int getStartTimeMin()
 {
  return startTimeMin;
 }

 public int getEndTimeMin()
 {
  return endTimeMin;
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
  return courseDept + " " + courseNum;
 }
 
 public String getRoom()
 {
  return room;
 }
 public int compareTo(Object compared)
 {
  if(this.startTimeHour > ((gwtScheduleItem)compared).getStartTimeHour())
  {
   return 1;
  }
  else if(this.startTimeHour < ((gwtScheduleItem)compared).getStartTimeHour())
  {
   return -1;
  }
  else if(this.startTimeMin > ((gwtScheduleItem)compared).getStartTimeMin())
  {
   return 1;
  }
  else if(this.startTimeMin < ((gwtScheduleItem)compared).getStartTimeMin())
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
 
 public boolean startsAfterHalf()
 {
  return startTimeMin >= 30;
 }
 
 public boolean endsAfterHalf()
 {
  return endTimeMin >=30;
 }
 
 public void setRow(int row)
 {
  rowPlaced = row;
 }
 
 public int getRow()
 {
  return rowPlaced;
 }
 
 public void setCol(int col)
 {
  colPlaced = col;
 }
 
 public int getCol()
 {
  return colPlaced;
 }
 
 public void setOverlapCount(int overlaps)
 {
  overlapCount = overlaps;
 }
 
 public int getOverlapCount()
 {
  return overlapCount;
 }
}
