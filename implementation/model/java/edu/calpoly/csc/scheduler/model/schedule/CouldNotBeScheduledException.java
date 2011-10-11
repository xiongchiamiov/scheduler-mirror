package edu.calpoly.csc.scheduler.model.schedule;

import edu.calpoly.csc.scheduler.model.db.cdb.Course;
import edu.calpoly.csc.scheduler.model.db.cdb.*;

/**
 * Thrown when the Scheduler is unable to find a single day-time combination 
 * (DaysAndTime object) for a given course. 
 *
 * @author Eric Liebowitz
 * @version 26jun10
 */
public class CouldNotBeScheduledException extends Exception
{
   protected Course c;
   protected ScheduleItem si;

   public CouldNotBeScheduledException ()
   {
      super ();
   }

   public CouldNotBeScheduledException (ScheduleItem si)
   {
      super();
      this.si = si;
   }

   public CouldNotBeScheduledException (Course c)
   {
      super ();
      this.c = c;
   }
}
