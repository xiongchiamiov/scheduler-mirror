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
   private InstructorDB idb;
   private LocationDB ldb;
   
   Vector<ScheduleItem> siList = new Vector<ScheduleItem>();
   
   public NewSchedule (NewCourseDB cdb, InstructorDB idb, LocationDB ldb)
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
            Instructor i = chooseInstructor(c);
            Location l = chooseLocation(i);
         }
      }
   }
   
   /**
    * Chooses an 
    * @param c
    * @return
    */
   private Instructor chooseInstructor (Course c)
   {
      return null;
   }
   
   private Location chooseLocation (Instructor i)
   {
      return null;
   }
}
