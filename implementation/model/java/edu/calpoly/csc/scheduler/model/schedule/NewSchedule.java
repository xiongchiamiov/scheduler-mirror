package edu.calpoly.csc.scheduler.model.schedule;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Observable;
import java.util.List;
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
   private List<Course> cSourceList       = new Vector<Course>();
   private List<Instructor> iSourceList   = new Vector<Instructor>();
   private List<Location> lSourceList     = new Vector<Location>();

   private List<Course> genCourseList           = new Vector<Course>();
   private List<Instructor> genInstructorList   = new Vector<Instructor>();
   private List<Location> genLocationList       = new Vector<Location>();

   private HashMap<Instructor, WeekAvail> iBookings = 
      new HashMap<Instructor, WeekAvail>();
   
   private HashMap<Location, WeekAvail> lBookings = 
      new HashMap<Location, WeekAvail>();

   private HashMap<Course, Integer> courseCount = 
      new HashMap<Course, Integer>();
   
   private TimeRange bounds = new TimeRange(new Time(7, 0), new Time(22, 0));

   Vector<ScheduleItem> items = new Vector<ScheduleItem>();

   private Integer id = -1;
   private String quarterId = "";
   private String name = "";
   
   public NewSchedule () { }
   
   /**
    * Adds the given ScheduleItem to this schedule. Before an add is done, the
    * ScheduleItem is verified to make sure it doesn't double-book an 
    * instructor/location and that the instructor can actually teach the course.
    * 
    * @param si ScheduleItem to add
    * 
    * @return true if the item was added. False otherwise.
    */
   public boolean add (ScheduleItem si)
   {
      boolean r = false;
      
      Course c       = si.getCourse();
      Instructor i   = si.getInstructor();
      Location l     = si.getLocation();
      
      if (!verify(si))
      {
         
      }
      else
      {
         if (!genInstructorList.contains(i))
         {
            genInstructorList.add(i);
         }
         if (!genLocationList.contains(l))
         {
            genLocationList.contains(l);
         }
      
         book (si);
         r = true;
      }
      return r;
   }
   
   /**
    * Applies all the day/time/wtu commitments of a ScheduleItem to the internal
    * structures the Schedule uses to keep track of things. iBookings, 
    * lBookings, and the instructor's WTU count are updated. It is assumed 
    * you've already verified the ScheduleItem.
    *  
    * @param si The ScheduleItem w/ the days, times, etc. which'll be booked
    *           in the schedule
    *
    * @see #verify(ScheduleItem)
    */
   private void book (ScheduleItem si)
   {
      Instructor i   = si.getInstructor();
      Location l     = si.getLocation();
      Week days      = si.getDays();
      TimeRange tr   = si.getTimeRange();
      
      this.iBookings.get(i).book(days, tr);
      this.lBookings.get(l).book(days, tr);
      
      int wtu = i.getCurWtu();
      wtu += si.getWtuTotal();
      i.setCurWtu(wtu);
      
      bookSection(si.getCourse());
      
      this.items.add(si);
   }
   
   /**
    * Books another section of the given course. 
    * @param c
    */
   private void bookSection (Course c)
   {
      if (!this.courseCount.containsKey(c))
      {
         this.courseCount.put(c, 0);
      }
      
      int i = this.courseCount.get(c);
      i ++;
      this.courseCount.put(c, i);
      
      if (i == c.getNumOfSections())
      {
         cSourceList.remove(c);
      }
   }
   
   /**
    * Ensures that the given ScheduleItem can be scheduled. This means it 
    * doesn't double book instructors/locations, and the instructor can teach
    * the lecture/lab w/o exceeding his max wtu limit. 
    * 
    * @param si ScheduleItem to verify
    * 
    * @return true if 'si' can be taught by its instructor at its location, and
    *         the instructor can teach the course. 
    *         
    * @see Instructor#canTeach(Course)
    */
   private boolean verify (ScheduleItem si)
   {
      boolean r = true;
      
      Week days = si.getDays();
      TimeRange tr = si.getTimeRange();
      Instructor i = si.getInstructor();
      Location l = si.getLocation();
      
      if (i.isAvailable(days, tr))
      {
         r = false;
      }
      if (l.isAvailable(days, tr))
      {
         r = false;
      }
      if (!i.canTeach(si.getCourse()))
      {
         r = false;
      }
      
      return r;
   }
   
   /**
    * Does schedule generation
    * 
    * @param c_list List of courses you want scheduled
    * @param i_list List of instructors you want to teach stuff
    * @param l_list List of locations you want stuff to be taught in
    */
   public void generate (List<Course> c_list, 
                         List<Instructor> i_list, 
                         List<Location> l_list)
   {
      initGenData(c_list, i_list, l_list);

      while (shouldKeepGenerating())
      {
         Vector<Instructor> toRemove = new Vector<Instructor>();
         for (Instructor i : this.iSourceList)
         {
            try
            {
               Vector<ScheduleItem> sis = genListForInstructor(i);
               add(sis.get(0));
               //TODO: Write pruning method
            }
            catch (InstructorCanTeachNothingException e)
            {
               toRemove.add(i);
            }
            //TODO: Sort ScheduleItems
         }
      }
   }
   
   private boolean shouldKeepGenerating ()
   {
      return this.cSourceList.size() > 0;
   }
   
   /**
    * Clears the record-keeping data associated w/ generation. 
    * 
    * @param c_list List of Courses that'll be put into the schedule
    * @param i_list List of Instructors that'll be put into the schedule
    * @param l_list List of Locations that'll be put into the schedule
    */
   private void initGenData (List<Course> c_list, List<Instructor> i_list, 
      List<Location> l_list)
   {
      cSourceList = new Vector<Course>(c_list);
      iSourceList = new Vector<Instructor>(i_list);
      lSourceList = new Vector<Location>(l_list);
   }
   
   /**
    * Generates a list of ScheduleItems for a given instructor. All items 
    * returned will contain the same course (one which the instructor wishes
    * to/can teach), contain times for which the instructor wants to/is able to
    * teach, and be taught in a location available for the time range specified.
    *  
    * @param i Instructor build ScheduleItems for
    * 
    * @return A list of ScheduleItems for what this instructor was scheduled to
    *         teach.
    *         
    * @throws InstructorCanTeachNothingException if no course exists which the 
    *         Instructor wants to teach and/or no course exists which does not
    *         exceed the Instructor's WTU limit. 
    */
   private Vector<ScheduleItem> genListForInstructor (Instructor i)
      throws InstructorCanTeachNothingException
   {
      Vector<ScheduleItem> sis = new Vector<ScheduleItem>();
      Vector<ScheduleItem> lec_si_list = new Vector<ScheduleItem>();
      Vector<ScheduleItem> lab_si_list = new Vector<ScheduleItem>();

      /*
       *  Get ScheduleItems for the lecture
       */
      ScheduleItem lec_base = new ScheduleItem();
      
      lec_base.setInstructor(i);
      
      lec_base    = findCourse(lec_base);
      lec_si_list = findTimes(lec_base);
      lec_si_list = findLocations(lec_si_list);
      
      //TODO: Do labs
      
      return lec_si_list;
   }
   
   /**
    * Finds a course which a given instructor wants to teach and can teach. This
    * means that the course returned will not exceed his WTU limit. Furthermore,
    * the instructor's preference for the class is at least as high as his
    * preference for every other class.
    * 
    * @param si ScheduleItem with the instructor we're to find a course for
    * 
    * @return A ScheduleItem w/ its course value set
    * 
    * @throws InstructorCanTeachNothingException If no course exists which this
    *         instructor can teach (has a preference > 0)
    * @throws InstructorWTUMaxedException if no course exists which will not
    *         push this instructor's WTUs over his limit
    * 
    * @see genCourseList
    */
   private ScheduleItem findCourse (ScheduleItem si)
      throws InstructorCanTeachNothingException
   {
      Instructor i = si.getInstructor();
      Course bestC = null;
      int bestPref = 0;
      boolean canWTU = false;
      boolean canPref = false;

      /*
       * Exhaustively find the best course.
       */
      for (Course temp : this.cSourceList)
      {
         int pref = i.getPreference(temp);

         /*
          * If prof wants this course more than previous "best".
          */
         if (pref > bestPref)
         {
            canPref = true;
             if (i.canTeach(temp))
            {
               canWTU = true;
               bestC = temp;
               bestPref = pref;
            }
         }
      }

      if (!canPref || !canPref)
      {
         throw new InstructorCanTeachNothingException();
      }

      si.setCourse(bestC);
      return si;
   }

   /**
    * Gets all the possible time ranges that an instructor can teach a given
    * course. The days that are considered for teaching are the days defined by
    * the course to be taught. Each ScheduleItem returned is a clone of the
    * single ScheduleItem passed in, so that all the fields in the returned list
    * will be the same <b>except</b> for their times.
    * 
    * @param si ScheduleItem w/ the course and instructor we're to use in
    *        computing times to consider. This ScheduleItem is cloned for every
    *        time range returned.
    * 
    * @return A list of ScheduleItems w/ their days and time ranges set to times
    *         which the instructor wants to/can teach and on the days the Course
    *         is to be taught on.
    */
   private Vector<ScheduleItem> findTimes (ScheduleItem si)
   {
      Vector<ScheduleItem> sis = new Vector<ScheduleItem>();
      Course c = si.getCourse();
      Instructor i = si.getInstructor();

      TimeRange tr = new TimeRange(this.bounds.getS(), c.getDayLength());
      for (; tr.getE().equals(this.bounds.getE()); tr.addHalf())
      {
         Week days = c.getDays();

         if (this.iBookings.get(i).isFree(tr, days))
         {
            if (i.getAvgPrefForTimeRange(days, tr.getS(), tr.getE()) > 0)
            {
               ScheduleItem toAdd = si.clone();
               si.setDays(days);
               si.setTimeRange(tr);
            }
         }
      }

      return sis;
   }

   /**
    * Finds all locations which are compatible with the given Course and are
    * available for any of the given list of TimeRanges.
    * 
    * @param c Course to find a location for
    * @param times List of TimeRanges which this course can be taught for.
    * 
    * @return A list of locaitons which can be taught on the days for course 'c'
    *         during at least the TimeRanges passed in.
    */
   private Vector<ScheduleItem> findLocations (Vector<ScheduleItem> sis)
   {
      Vector<ScheduleItem> si_list = new Vector<ScheduleItem>();

      for (ScheduleItem si : sis)
      {
         Week days = si.getDays();

         for (Location l : this.lSourceList)
         {
            if (this.lBookings.get(l).isFree(si.getTimeRange(), days))
            {
               ScheduleItem base = si.clone();
               base.setLocation(l);
               si_list.add(base);
            }
         }
      }
      
      return si_list;
   }

   /**
    * Takes a list of lectures and a potentially-not-zero list of labs and makes
    * new items with each lecture paired with each lab, ensuring that the
    * lectures/labs are compatible (i.e. they don't overlap). Those which do not
    * pair well together will not be added to the list of returned items.
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
            }
         }
         items.add(lec_clone);
      }
      return items;
   }

   /**
    * Returns the items
    * 
    * @return the items
    */
   public Vector<ScheduleItem> getItems ()
   {
      return items;
   }

   /**
    * Returns the id
    * 
    * @return the id
    */
   public Integer getId ()
   {
      return id;
   }

   /**
    * Sets the id to the given parameter.
    *
    * @param id the id to set
    */
   public void setId (Integer id)
   {
      this.id = id;
   }

   /**
    * Returns the quarterId
    * 
    * @return the quarterId
    */
   public String getQuarterId ()
   {
      return quarterId;
   }

   /**
    * Sets the quarterId to the given parameter.
    *
    * @param quarterId the quarterId to set
    */
   public void setQuarterId (String quarterId)
   {
      this.quarterId = quarterId;
   }

   /**
    * Returns the name
    * 
    * @return the name
    */
   public String getName ()
   {
      return name;
   }

   /**
    * Sets the name to the given parameter.
    *
    * @param name the name to set
    */
   public void setName (String name)
   {
      this.name = name;
   }
}
