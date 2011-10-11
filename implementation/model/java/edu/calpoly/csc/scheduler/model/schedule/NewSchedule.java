package edu.calpoly.csc.scheduler.model.schedule;

import java.io.Serializable;
import java.util.Collection;
import java.util.Observable;
import java.util.Vector;

import edu.calpoly.csc.scheduler.model.db.Time;
import edu.calpoly.csc.scheduler.model.db.cdb.*;
import edu.calpoly.csc.scheduler.model.db.idb.*;
import edu.calpoly.csc.scheduler.model.db.ldb.*;

/**
 *
 * @author Eric Liebowitz
 * @version Oct 10, 2011
 */
public class NewSchedule extends Observable implements Serializable
{
   private NewCourseDB cdb;
   private NewInstructorDB idb;
   private LocationDB ldb;
   
   Vector<ScheduleItem> siList = new Vector<ScheduleItem>();
   
   public NewSchedule (NewCourseDB cdb, NewInstructorDB idb, LocationDB ldb)
   {
      this.cdb = cdb;
      this.idb = idb;
      this.ldb = ldb;
   }
   
   /**
    * Creates a ScheduleItem for the given course. Will find an appropriate 
    * instructor and location for the course.
    * 
    * @.todo write this 
    * @.todo Consider passing a list of days
    * @param c
    * @param t
    * @return
    */
   public ScheduleItem makeItem (Course c, Time t)
   {
      return null;
   }
   
   /**
    * Does schedule generation.
    * 
    * @.todo Fix how many times we loop for courses
    */
   public void generate ()
   {
      Collection<Course> courses = this.cdb.getData();
      
      for (Course c: courses)
      {
         for (int s = 0; s < c.getNumOfSections(); s ++)
         {
            Instructor i = findInstructor(c);
            Vector<Time> times = findTimes(i);
            Vector<Location> locations = findLocations(times);
         }
      }
   }
   
   private Instructor findInstructor (Course c)
   {
      return this.idb.getInstructor(c);
   }
   
   private Vector<Time> findTimes (Instructor i)
   {
      return null;
   }
   
   private Vector<Location> findLocations (Vector<Time> times)
   {
      return null;
   }
}
