package edu.calpoly.csc.scheduler.view.web.client.schedule;

import com.google.gwt.user.client.ui.Label;

import edu.calpoly.csc.scheduler.view.web.shared.CourseGWT;

public class CourseListItem extends Label
{
 private CourseGWT course;
 private boolean isScheduled = false;
 public int sectionsScheduled = 0;

 public CourseListItem(CourseGWT course, boolean showSections)
 {
  String itemText = course.getDept() + " " + course.getCatalogNum() + "(" 
   + course.getType() + ")"; 
  
  if(showSections)
  {
   itemText = itemText.concat(" (" + course.getNumSections() + ")");
  }
  setText(itemText);
  this.course = course;
 }

 CourseGWT getCourse()
 {
  return course;
 }

 public boolean sameCourse(CourseListItem item)
 {
  if (course.getDept().equals(item.getCourse().getDept())
    && course.getCatalogNum() == item.getCourse().getCatalogNum())
  {
   return true;
  }
  return false;
 }
}
