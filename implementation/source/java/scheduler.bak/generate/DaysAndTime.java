package scheduler.generate;

import scheduler.db.Time;

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
   public static DaysAndTime TBA = 
      new DaysAndTime (new int[]{Week.SUN}, new Time (0, 0), new Time (1, 0));

   /** 
    * Represents the days 
    */
   protected Week week;
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
      week = new Week(days);
      this.s = s;
      this.e = e;
   }

   /**
    * @param days Week of days to represent
    * @param s The start time
    * @param e The end time
    */
   public DaysAndTime (Week days, Time s, Time e)
   {
      this.week = days;
      this.s = s;
      this.e = e;
   }

   /**
    * Prints the object in the form "week start end"
    */
   public String toString ()
   {
      return this.week + " " + this.s + " " + this.e;
   }
}
