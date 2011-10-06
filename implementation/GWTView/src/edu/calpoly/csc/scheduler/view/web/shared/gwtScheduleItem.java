package edu.calpoly.csc.scheduler.view.web.shared;

import java.io.Serializable;

import com.google.gwt.user.client.rpc.IsSerializable;

public class gwtScheduleItem implements IsSerializable
{
 private String courseDept;
 private int courseNum;
 private int section;
 private String days;
 private int dayNums[];
 private int startTime;
 private int endTime;
 
 public gwtScheduleItem()
 {
  
 }
 public gwtScheduleItem(String dept, int cNum, int sec, String ds, int dNums[], int st, int et)
 {
  courseDept = dept;
  courseNum = cNum;
  section = sec;
  days = ds;
  dayNums = dNums;
  startTime = st;
  endTime = et;
 }
 
 public String toString()
 {
  return courseDept + courseNum + "-" + section + "<br>" + startTime + " - " + 
   endTime + "<br>" + days; 
 }
 
 public int getStartTime()
 {
  return startTime;
 }
 
 public int getEndTime()
 {
  return endTime;
 }
 
 public int[] getDayNums()
 {
  return dayNums;
 }
}
