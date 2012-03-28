package scheduler.view.web.client.schedule;

import com.google.gwt.user.client.ui.Label;

import scheduler.view.web.shared.CourseGWT;

/**
 * Extension of the Label class for displaying a course in the listboxes which
 * appear on the schedule view. Holds a CourseGWT object to be accessed during
 * drag and drop operations.
 * 
 * @author Mike McMahon
 */
public class CourseListItem extends Label
{
 private CourseGWT course;

 /**
  * Creates the label with the provided course. The label will show the number
  * of course sections if it's in the "Courses to Schedule" list.
  *  
  * @param course The course this will label hold.
  * @param showSections True if the label should show a section count, false otherwise.
  */
 public CourseListItem(CourseGWT course, boolean showSections)
 {
  //Sets the label's text to appear like this: CHEM 101(LEC)
  String itemText = course.getDept() + " " + course.getCatalogNum() + "(" 
   + course.getType() + ")"; 
  
  //If label should show sections, adds the number of sections like this: CHEM 101(LEC)(3)
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

 /**
  * Compares another CourseListItem with this one to see if they hold the same
  * course.
  * @param item The CourseListItem to compare this CourseListItem with.
  * @return True if the objects hold the same course, false otherwise.
  */
 public boolean sameCourse(CourseListItem item)
 {
  //Compares course's departments and catalog numbers.
  if (course.getDept().equals(item.getCourse().getDept())
    && course.getCatalogNum() == item.getCourse().getCatalogNum())
  {
   return true;
  }
  return false;
 }
}
