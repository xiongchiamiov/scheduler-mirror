package edu.calpoly.csc.scheduler.model.db.ldb;

import edu.calpoly.csc.scheduler.model.db.Time;
import edu.calpoly.csc.scheduler.model.db.TimeRange;
import edu.calpoly.csc.scheduler.model.db.cdb.Course;
import edu.calpoly.csc.scheduler.model.schedule.Day;
import edu.calpoly.csc.scheduler.model.schedule.Week;

/**
 * A made-up location that is always available and always provides for a course
 * or instructor's needs.
 * 
 * @author Eric Liebowitz
 * @version Oct 24, 2011
 */
public class Tba extends Location
{
   private static final Tba me = new Tba();
   
   private Tba ()
   {
      super (-1, -1);
   }
   
   public static Tba getTba ()
   {
      return me;
   }
   
   @Override
   public boolean providesFor (Course c)
   {
      return true;
   }
   
   @Override
   public boolean isAvailable(Day dayOfWeek, Time s, Time e)
   {
      return true;
   }
   
   @Override
   public boolean isAvailable(Week week, Time s, Time e)
   {
      return true;
   }
   
   @Override
   public boolean book (boolean b, Day dayOfWeek, Time s, Time e)
   {
      return true;
   }

   @Override
   public boolean book (boolean b, Week week, Time s, Time e)
   {
      return true;
   }

   @Override
   public boolean book (boolean b, Week week, TimeRange tr)
   {
      return true;
   }
}
