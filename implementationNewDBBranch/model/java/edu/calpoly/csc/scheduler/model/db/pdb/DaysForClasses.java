package edu.calpoly.csc.scheduler.model.db.pdb;

import java.util.Vector;
import java.io.Serializable;

import edu.calpoly.csc.scheduler.model.schedule.*;


/**
 * Represents the schedule preference which specifies which days of the week
 * courses should be scheduled (MWF, TR, etc.).
 *
 * Day abbreviations are as follows:
 *
 *    SUN - Su
 *    MON - M
 *    TUE - T
 *    WED - W
 *    THU - R
 *    FRI - F
 *    SAT - Sa
 *
 * @author Eric Liebowitz
 * @version 18aug10
 */
public class DaysForClasses extends SchedulePreference 
                            implements Comparable<DaysForClasses>, Serializable
{
   /** 
    * For serializing
    */
   public static final long serialVersionUID = 42;

   /**
    * Conveniently-defined preference for teaching a class any/all five days 
    * of the work week. Probably most useful as a default DaysForClasses 
    * preference. 
    */
   public static final DaysForClasses MTWRF = 
      new DaysForClasses ("MTWRF", 5, new Day[] {Day.MON,
                                                 Day.TUE, 
                                                 Day.WED, 
                                                 Day.THU, 
                                                 Day.FRI});

   public Week days;

   /**
    * Builds a preference for which days to offer courses.
    *
    * @param name The name this preference will be referred to as
    * @param weight The priority (0 - 10...10 is non-violatable)
    * @param days The days this preference represents. Note that this can be 
    *             appended to.
    */
   public DaysForClasses (String name, int weight, Week days)
   {
      super (name, weight);
      this.days   = days;
   }

   /**
    * Builds a preference for which days to offer courses.
    *
    * @param name The name this preference will be referred to as
    * @param weight The priority (0 - 10...10 is non-violatable)
    * @param days An array of integers representing days of the week. (Sun = 0, 
    *             6 = Sat). This array will be used to create the "Week" object.
    */
   public DaysForClasses (String name, int weight, Day[] days)
   {
      super (name, weight);
      this.days = new Week(days);
   }

   /**
    * Adds a day to the list of days courses are to be taught on.
    *
    * @param day The day to add
    *
    * @return true if the list was altered. False otherwise. 
    */
   public boolean addDay (Day day)
   {
      return this.days.add(day);
   }

   /**
    * Remoes a day from the list of days courses are to be taught on.
    *
    * @param day The day to remove
    * 
    * @return true if the list was altered. False otherwise. 
    */
   public boolean delDay (Day day)
   {
      /*
       * Have to make new Int to get the right method out of Week
       */
      return this.days.del(day);
   }

   /**
    * Oveerrides the compareTo method
    * @param dfc The DaysForClasses to compare.
    * @return 1 if its greater than, 0 if its equal, -1 if its less than
    *
    **/
   public int compareTo(DaysForClasses dfc) 
   {
      if (dfc.weight > this.weight) 
      {
         return -1;
      }
      else if (dfc.weight < this.weight) 
      {
         return 1;
      }
      else 
      {
         return 0;
      }
   }
}
