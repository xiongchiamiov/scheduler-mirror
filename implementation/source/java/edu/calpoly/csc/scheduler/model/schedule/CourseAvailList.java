package edu.calpoly.csc.scheduler.model.schedule;

import scheduler.db.coursedb.*;

import java.util.Vector;
import java.io.Serializable;

import edu.calpoly.csc.scheduler.model.db.Time;
import edu.calpoly.csc.scheduler.model.db.cdb.Course;

/**
 * Used to keep track of multiple "WeekAvail" objects. Provides methods to check
 * availability across multiple WeekAvails, book times, and add more WeekAvails.
 * Written primarily to make it easy to obey NoClassOverlap preferences, as a 
 * course listed in multiple NCO's would need multiple WeekAvail objects.
 *
 * @author Eric Liebowitz
 * @version 31aug10
 */
public class CourseAvailList extends Vector<CourseWeekAvail> 
                             implements Serializable
{
   public static final int serialVersionUID = 42;

   public CourseAvailList ()
   {
      super ();
   }

   /**
    * Adds a NcoWeekAvail object to this object's list of WeekAvail's
    *
    * @param wa The NcoWeekAvail to add
    */
   public void addWeekAvail (CourseWeekAvail cwa)
   {
      this.add(cwa);
   }

   /**
    * Books a given time range on a given day for all NcoWeekAvail objects in this
    * object.
    *
    * @param c The course to be booked
    * @param s The start time
    * @param e The end time
    * @param d The day to book on
    *
    * @return true if the booking was made. False it is wasn't (likely b/c the 
    *         entire time span wasn't free).
    *
    * @throws NotADayException if "d" is not a valid day as defined in 
    *         generate.Week.java
    */
   public boolean book (Course c, Time s, Time e, int d)
   {
      boolean r;

      if (r = isFree(c, s, e, d))
      {
         for (CourseWeekAvail cwa: this)
         {
            r &= cwa.book(c, s, e, d);
   
         }
      }
      return r;
   }

   /**
    * Books a given time range on a given list of days for all NcoWeekAvail 
    * objects in this object.
    *
    * @param c The course to be booked
    * @param s The start time
    * @param e The end time
    * @param days The days to book on
    *
    * @return true if the booking was made. False it is wasn't (likely b/c the 
    *         entire time span wasn't free).
    *
    * @throws NotADayException if any of the "days" are not a valid day as 
    *         defined in generate.Week.java
    */
   public boolean book (Course c, Time s, Time e, Vector<Integer> days)
   {
      boolean r;

      if (r = isFree (c, s, e, days))
      {
         for (CourseWeekAvail cwa: this)
         {
            r &= cwa.book(c, s, e, days);
         }
      }
      return r;
   }

   /**
    * Checks whether a given time range on a given list of days for all 
    * NcoWeekAvail objects in this object are free for booking
    *
    * @param c The course to check for validity
    * @param s The start time
    * @param e The end time
    * @param d The day to book
    *
    * @return true if the booking could be made. False otherwise.
    *
    * @throws NotADayException if "d" is not a valid day as defined in 
    *         generate.Week.java
    */
   public boolean isFree (Course c, Time s, Time e, int d)
   {
      boolean r = true;
      for (CourseWeekAvail cwa: this)
      {
         r &= cwa.isFree(c, s, e, d);
      }
      return r;
   }

   /**
    * Checks whether given time range on a given list of days for all 
    * NcoWeekAvail objects in this object are free for booking
    *
    * @param c The course to check for validity
    * @param s The start time
    * @param e The end time
    * @param days The days to book on
    *
    * @return true if the booking could be made. False otherwise.
    *
    * @throws NotADayException if "days" are not valid days as defined in
    *         generate.Week.java
    */
   public boolean isFree (Course c, Time s, Time e, Vector<Integer> days)
   {
      boolean r = true;
      for (CourseWeekAvail cwa: this)
      {
         r &= cwa.isFree(c, s, e, days);
      }
      return r;
   }
}
