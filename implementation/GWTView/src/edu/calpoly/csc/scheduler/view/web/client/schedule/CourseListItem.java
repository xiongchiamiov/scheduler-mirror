package edu.calpoly.csc.scheduler.view.web.client.schedule;

import com.google.gwt.user.client.ui.Label;

import edu.calpoly.csc.scheduler.view.web.shared.CourseGWT;

public class CourseListItem extends Label
{
 private CourseGWT course;
 
 public CourseListItem(CourseGWT course)
 {
  super(course.getDept() + " " + course.getCatalogNum());
  this.course = course;
 }
 
 CourseGWT getCourse()
 {
  return course;
 }
}
