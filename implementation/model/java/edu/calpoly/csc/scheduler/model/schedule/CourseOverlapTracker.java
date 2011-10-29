package edu.calpoly.csc.scheduler.model.schedule;

import edu.calpoly.csc.scheduler.model.db.cdb.*;

import java.io.Serializable;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Vector;

import edu.calpoly.csc.scheduler.model.db.Time;
import edu.calpoly.csc.scheduler.model.db.cdb.Course;
import edu.calpoly.csc.scheduler.model.db.pdb.NoClassOverlap;
import edu.calpoly.csc.scheduler.model.db.pdb.SchedulePreference;

/**
 * Keeps track of what courses are booked for what days/times and, given a set
 * of NoClassOverlap preferences, will ensure that class do not overlap. 
 *
 * Written almost entirely as a convenience for me, this class's big feature
 * is remembering when/how to remember a coure's paired lab, so as to be sure 
 * neither the lecture nor the lab for a particular course step on the toes of
 * any other courses they are not allowed to overlap with. 
 *
 * @author Eric Liebowitz
 * @version 01sep10
 */
public class CourseOverlapTracker implements Serializable
{
   public static final int serialVersionUID = 42;
   private HashMap<Course, CourseAvailList> cBookings;

   /**
    * Dummy constructor: simply initializes empty data.
    */
   public CourseOverlapTracker ()
   {
      this.cBookings = new HashMap<Course, CourseAvailList>();
   }

   /**
    * Builds the tracer based on a collection of SchedulePreferences. (In 
    * particular, NoClassOverlap preferences are the only one that'll be used).
    *
    * @param prefs List of SchedulePreferences (only NoClassOverlap will be 
    *        considered) which will be used to setup the CourseOverlapTracker.
    */
   public CourseOverlapTracker (Vector<SchedulePreference> prefs)
   {
      this.cBookings = new HashMap<Course, CourseAvailList>();
      for (SchedulePreference sp: prefs)
      {
         /*
          * I hate using "instanceof", but as the generate algorithm has to 
          * deal with all SchedulePreferences, this seems like the easiest way
          * way to weed out the ones I don't want.
          */
         if (sp instanceof NoClassOverlap)
         {
            tieCoursesTogether((NoClassOverlap)sp);
         }
      }
   }

   /**
    * Creates keys in the private hashMap used to keep track of what courses are
    * booked for what times. For a given NCO's list of courses, each course will
    * be given the same NcoWeekAvail object, so that one's time occupation will 
    * effect the others' time availability.
    *
    * @param nco The NCO containing the list of courses which are not allowed
    *        to overlap.
    */
   private void tieCoursesTogether (NoClassOverlap nco)
   {
      CourseWeekAvail cwa = new CourseWeekAvail(nco);
      for (Course c: nco.getCourseList())
      {
         if (!this.cBookings.containsKey(c))
         {
            this.cBookings.put(c, new CourseAvailList());
         }
         this.cBookings.get(c).addWeekAvail(cwa);
         /*
          * If it has a lab, incorporate that as well
          */
         //TODO: FIX
//         if (c.hasLab())
//         {
//            Course lab = c.getLabPairing();
//            if (!this.cBookings.containsKey(lab))
//            {
//               this.cBookings.put(lab, new CourseAvailList());
//            }
//            this.cBookings.get(lab).addWeekAvail(cwa);
//         }
      }
   }

   /**
    * Books a given time range on a given list of days for all WeekAvail objects 
    * associated w/ a given course
    *
    * @param c The course to have time booked for
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
   public boolean book (boolean b, Course c, Time s, Time e, Week days)
   {
      if (this.cBookings.containsKey(c))
      {
         return this.cBookings.get(c).book(b, c, s, e, days);
      }
      return true;
   }

   /**
    * Checks whether given time range on a given list of days for all WeekAvail
    * objects for this course is available. 
    *
    * @param c The course to check
    * @param s The start time
    * @param e The end time
    * @param days The days to book on
    *
    * @return true if the booking could be made. False otherwise.
    *
    * @throws NotADayException if "days" are not valid days as defined in
    *         generate.Week.java
    */
   public boolean isFree (Course c, Time s, Time e, Week days)
   {
      if (this.cBookings.containsKey(c))
      {
         return this.cBookings.get(c).isFree(c, s, e, days);
      }
      return true;
   }

   /**
    * Clears all internal data in the CourseOverlapTracker
    */
   public void clear ()
   {
      if (this.cBookings != null)
      {
         this.cBookings.clear();
      }
   }
}
