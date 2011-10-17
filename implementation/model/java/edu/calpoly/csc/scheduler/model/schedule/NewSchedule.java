package edu.calpoly.csc.scheduler.model.schedule;

import java.io.Serializable;
import java.util.Collection;
import java.util.Observable;
import java.util.Vector;

import edu.calpoly.csc.scheduler.model.db.*;
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

   private TimeRange bounds = new TimeRange(new Time(7, 0), new Time(22, 0));

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
    * 
    * @param c Course to add to the schedule
    * @param t Start time the course is to be taught on
    * @param days Days of the week to schedule the course
    * 
    * @return A schedule item for this course to be taught starting on time 't'
    *         on days 'days', w/ an automatically-picked instructor teaching it
    *         in a auto-picked location.
    */
   public ScheduleItem makeItem (Course c, Time t, Week days)
   {
      return null;
   }

   /**
    * Does schedule generation.
    */
   public void generate ()
   {
      Collection<Course> courses = this.cdb.getData();

      for (Course lec : courses)
      {
         Course lab = lec.getLab();
         for (int section = 0; section < lec.getNumOfSections(); section++)
         {
            Vector<ScheduleItem> items = new Vector<ScheduleItem>();
            
            for (Instructor i: findInstructors(lec))
            {
               Vector<TimeRange> lec_times = findTimes(i, lec);
               Vector<Location> lec_places = findLocations(lec, lec_times);

               Vector<ScheduleItem> lec_items = new Vector<ScheduleItem>();
            }
         }
      }
   }

   /**
    * Takes a list of lectures and a potentially-not-zero list of labs and 
    * makes new items with each lecture paired with each lab, ensuring that 
    * the lectures/labs are compatible (i.e. they don't overlap). Those which 
    * do not pair well together will not be added to the list of returned items.
    * 
    * @.todo Check the lab pad
    *  
    * @param lecs List of lecture ScheduleItems 
    * @param labs List of lab ScheduleItems
    * 
    * @return List of All the lectures paired w/ all their labs. Each pair is 
    *         guaranteed to not overlap or conflict in any other way. 
    */
   private Vector<ScheduleItem> joinLecsWithLabs (Vector<ScheduleItem> lecs,
      Vector<ScheduleItem> labs)
   {
      Vector<ScheduleItem> items = new Vector<ScheduleItem>();
      for (ScheduleItem lec_si : lecs)
      {
         /**
          * We need to clone the lecture's b/c we're possibly setting their lab
          * in each iteration. 
          */
         ScheduleItem lec_clone = lec_si.clone();
         
         for (ScheduleItem lab_si : labs)
         {
            /*
             * Only pair lec and lab si's if they don't overlap
             */
            if (!lec_si.getTimeRange().overlaps(lab_si.getTimeRange()))
            {
               lec_clone.setLab(lab_si);
               /* TODO: Check lab pad. Currently impossible */
            }
         }
         items.add(lec_clone);
      }
      return items;
   }

   /**
    * Gets a list of instructors who want to teach a given Course. The list is
    * sorted in descending order of desire to teach this course. 
    * 
    * @param c Course to find an instructor for
    * 
    * @return a list of instructors who want to teach 'c', sorted in descending
    *         order of desire to teach 'c'
    * 
    * @see NewInstructorDB#getInstructors(Course)
    */
   private Vector<Instructor> findInstructors (Course c)
   {
      return this.idb.getInstructors(c);
   }

   /**
    * Gets all the possible time ranges that an instructor can teach a given
    * course. The days that are considered for teaching are the days defined
    * by the course to be taught .
    * 
    * @param i Instructor who is to teach
    * @param c Course that is to be taught
    * 
    * @return A list of time ranges that the instructor is available to teach
    *         the given course. 
    * 
    * @see Instructor#getTeachingTimes(Course)
    * @see Course#getDays()
    */
   private Vector<TimeRange> findTimes (Instructor i, Course c)
   {
      return i.getTeachingTimes(c);
   }

   /**
    * Finds all locations which are compatible with the given Course and are 
    * available for any of the given list of TimeRanges.
    * 
    * @param c Course to find a location for
    * @param times List of TimeRanges which this course can be taught for. 
    * 
    * @return A list of locaitons which can be taught on the days for course
    *         'c' during at least one of the TimeRanges passed in
    */
   private Vector<Location> findLocations (Course c, Vector<TimeRange> times)
   {
      return null;
   }
}
