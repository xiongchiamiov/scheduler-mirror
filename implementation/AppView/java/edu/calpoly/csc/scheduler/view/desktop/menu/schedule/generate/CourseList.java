package edu.calpoly.csc.scheduler.view.desktop.menu.schedule.generate;

import edu.calpoly.csc.scheduler.model.db.cdb.*;

import java.util.Vector;
import java.awt.*;
import javax.swing.*;
import javax.swing.table.*;

import edu.calpoly.csc.scheduler.model.db.cdb.Course;
import edu.calpoly.csc.scheduler.model.db.cdb.CourseDB;

/**
 * Represents a checkbox list of the courses in a course database. Note that the
 * data displayed represents the contents of the -local- database, not the 
 * remotely-global one. 
 *
 * @author Eric Liebowitz
 * @version 23jun10
 */
public class CourseList extends GenList<Course>
{
   /**
    * Course database from whence this list will grab its data to display.
    */
   private CourseDB cdb;

   /**
    * Creates a list of courses. Column headers are "Course Name" and "#".
    *
    * @param axis Direction the contents of the box are to go (hor. or vert.)
    * @param cdb CourseDB you want backing this list
    */
   public CourseList (int axis, CourseDB cdb)
   {
      super (axis, new Vector<String>(), "Courses: ");
      this.cdb = cdb;

      Vector<String> colData = new Vector<String>();
      colData.add(" ");
      colData.add("Course Name");
      colData.add("#");
      this.table.getModel().setColumnIdentifiers(colData);
      
      refresh();
   }

   /**
    * Populates the list of courses. For each course, its name and section count
    * are displayed. 
    */
   protected void populate ()
   {
      for (Course c: cdb.getLocalData())
      {
         if (c.getCourseType().equals(Course.LEC))
         {
            Vector<Object> row = new Vector<Object>();

            row.add(this.selections.contains(c));
            row.add(c);
            row.add(c.getNumberOfSections());

            this.table.getModel().addRow(row);
         }
      }
   }

   /**
    * What should go into the "selections" list: a deep copy of the Course.
    *
    * @param c The Course to copy
    *
    * @return A deep copy of "c"
    */
   protected Course copy (Course c) { return new Course (c); }
  
}
