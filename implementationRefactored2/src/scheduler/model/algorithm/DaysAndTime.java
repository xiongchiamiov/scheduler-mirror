package scheduler.model.algorithm;

import scheduler.model.Day;

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
      Week w = new Week(new Day[] { Day.SUNDAY });
      DaysAndTime dat = new DaysAndTime (w, 0, 1);
      
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
   protected int s;
   /**
    * The end time
    */
   protected int e;

   /**
    * @param days Week of days to represent
    * @param s The start time
    * @param e The end time
    */
   public DaysAndTime (Week days, int s, int e)
   {
      this.week = days;
      init (s, e);
   }

   private void init (int s, int e)
   {
      this.tr = new TimeRange (s, e);
      this.s = s;
      this.e = e;
   }

   public int getS () { return tr.getS(); }
   public int getE () { return tr.getE(); }
   public Week getWeek () { return this.week; }

   /**
    * Prints the object in the form "week start end"
    */
   public String toString ()
   {
      return this.week + " " + this.s + " " + this.e;
   }
}
