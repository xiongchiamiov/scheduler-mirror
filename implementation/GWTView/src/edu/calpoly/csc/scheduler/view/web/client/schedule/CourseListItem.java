package edu.calpoly.csc.scheduler.view.web.client.schedule;

import com.google.gwt.user.client.ui.Label;

import edu.calpoly.csc.scheduler.view.web.shared.CourseGWT;

public class CourseListItem extends Label
{
 private CourseGWT course;
 private boolean isScheduled = false;
 public int sectionsScheduled = 0;

 public CourseListItem(CourseGWT course)
 {
  super(course.getDept() + " " + course.getCatalogNum() + " ("
    + course.getNumSections() + ")");
  this.course = course;
 }

 CourseGWT getCourse()
 {
  return course;
 }

 public void setIsScheduled()
 {
  isScheduled = true;
 }

 public boolean isScheduled()
 {
  return isScheduled;
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
