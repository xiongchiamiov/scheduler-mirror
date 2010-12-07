package scheduler.generate;

import scheduler.db.instructordb.Instructor;
import scheduler.db.*;

/**
 * Represents a collections of days and a start/end time for which an 
 * instructor will be scheduled to teach a course. 
 *
 * @author Eric Liebowitz
 * @version 13apr10
 */
public class DaysAndTime
{
   /**
    * Represents an unscheduled ("To be announced") time
    */
   public static DaysAndTime TBA ()
   {
      DaysAndTime dat = new DaysAndTime (new int[]{Week.SUN}, 
                                         new Time (0, 0), 
                                         new Time (1, 0));
      return dat;
   }

   /** 
    * Represents the days 
    */
   protected Week week;
   /**
    * The time range over which this object applies
    */
   TimeRange tr;
   /**
    * The start time
    */
   protected Time s;
   /**
    * The end time
    */
   protected Time e;


   /**
    * @param days Array of ints which the week will be made up of
    *             (0 = sun, 6 = sat)
    * @param s The start time
    * @param e The end time
    */
   public DaysAndTime (int[] days, Time s, Time e)
   {
      this.week = new Week(days);
      init (s, e);
   }

   /**
    * @param days Week of days to represent
    * @param s The start time
    * @param e The end time
    */
   public DaysAndTime (Week days, Time s, Time e)
   {
      this.week = days;
      init (s, e);
   }

   private void init (Time s, Time e)
   {
      this.tr = new TimeRange (s, e);
      this.s = s;
      this.e = e;
   }

   public Time getS () { return tr.getS(); }
   public Time getE () { return tr.getE(); }
   public Week getWeek () { return this.week; }

   /**
    * Prints the object in the form "week start end"
    */
   public String toString ()
   {
      return this.week + " " + this.s + " " + this.e;
   }
}
