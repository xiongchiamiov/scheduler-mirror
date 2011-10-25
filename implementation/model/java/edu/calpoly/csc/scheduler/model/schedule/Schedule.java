package edu.calpoly.csc.scheduler.model.schedule;

import java.io.Serializable;
import java.util.*;

import edu.calpoly.csc.scheduler.model.db.*;
import edu.calpoly.csc.scheduler.model.db.cdb.*;
import edu.calpoly.csc.scheduler.model.db.idb.*;
import edu.calpoly.csc.scheduler.model.db.ldb.*;

/**
 * 
 * @author Eric Liebowitz
 * @version Oct 10, 2011
 */
public class Schedule extends Observable implements Serializable
{
   /**
    * Used for debugging. Toggle it to get debugging output
    */
   public static final boolean DEBUG = !true;
   /**
    * Prints a message if DEBUG is true
    * 
    * @param s String to print
    */
   public static void debug (String s)
   {
      if (DEBUG)
      {
         System.err.println (s);
      }
   }
   
   /**
    * List of courses that are to be added to the schedule. This list will be
    * shortened as courses are fully scheduled during generation.
    */
   private List<Course> cSourceList       = new Vector<Course>();
   /**
    * List of instructors that are to be added to the schedule. This list will 
    * be shortend as instructors become unavailable to teach anymore courses.
    */
   private List<Instructor> iSourceList   = new Vector<Instructor>();
   /**
    * List of location that are to be made available for schedule generation.
    */
   private List<Location> lSourceList     = new Vector<Location>();

   /**
    * Keeps track of how many sections of a given course have been scheduled. 
    */
   private HashMap<Course, Integer> courseCount = 
      new HashMap<Course, Integer>();
   
   /**
    * The global start/end times for a given day on the schedule. Default 
    * bounds are 7a-10p.
    */
   private TimeRange bounds = new TimeRange(new Time(7, 0), new Time(22, 0));

   /**
    * List of ScheduleItems which generation will create. This is the "schedule"
    * that the user will see.
    */
   private Vector<ScheduleItem> items = new Vector<ScheduleItem>();

   /**
    * This schedule's id
    */
   private Integer id = -1;
   /**
    * The quarter id to link this schedule w/ a particular quarter
    */
   private String quarterId = "";
   /**
    * Human-readable string to identify this schedule
    */
   private String name = "";
   
   /**
    * Default constructor. Does nothing but give you back a new object. 
    */
   public Schedule () { }
   
   /**
    * Creates a ScheduleItem to teach a given course on a given set of days 
    * in a given time range. 
    * 
    * @param c Course to teach
    * @param days Days to teach
    * @param s Time of day the course should start
    * 
    * @return A ScheduleItem w/ an instructor and location aptly suited for 
    *         teaching with the given parameters. 
    *         
    * @throws CouldNotBeScheduledException If no instructor or location can be
    *         found which is compatible w/ the given parameters.
    *
    * @.todo Incorporate labs into this
    */
   public ScheduleItem makeItem (Course c, Week days, Time s)
      throws CouldNotBeScheduledException
   {
      TimeRange tr = new TimeRange(s, c.splitLengthOverDays(days.size()));
      
      /*
       * SiMap'll sort out everything and let the best choice float to the top
       */
      SiMap si_map = new SiMap();
      
      ScheduleItem si = new ScheduleItem();
      si.setCourse(c);
      si.setDays(days);
      si.setTimeRange(tr);
      
      for (Instructor i: this.iSourceList)
      {
         /*
          * The map won't add if the instructor has a 0 pref for anything in
          * the ScheduleItem.  
          */
         ScheduleItem clone = si.clone();
         clone.setInstructor(i);
         List<ScheduleItem> list = Arrays.asList(new ScheduleItem[]{ clone });

         /*
          * If there's a lab, fill our list with lectures all paired with labs
          */
         Lab lab = c.getLab();
         if (lab != null)
         {
            list = addLab(lab, list);
         }
         si_map.putAll(list);
      }
      
      /*
       * If not even one not-impossible ScheduleItem was created, we'll have to
       * balk
       */
      if (si_map.size() == 0)
      {
         throw new CouldNotBeScheduledException();
      }
      
      return si_map.getBest();
   }
   
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
      return book(si);
   }
   
   /**
    * Applies all the day/time/wtu commitments of a ScheduleItem to instructors
    * and locations to take up their availability. Instructor's WTU count is 
    * updated. Course section count is also updated.<br>
    * <br>
    * The ScheduleItem is also verified before it is booked. If verification 
    * fails, the ScheduleItem is not added.  
    *  
    * @param si The ScheduleItem w/ the days, times, etc. which'll be booked
    *           in the schedule
    *
    * @return true if the ScheduleItem was added. False otherwise. 
    *
    * @see #verify(ScheduleItem)
    */
   private boolean book (ScheduleItem si)
   {
      boolean r = false;
      
      if (verify(si))
      {
         Instructor i   = si.getInstructor();
         Location l     = si.getLocation();
         Week days      = si.getDays();
         TimeRange tr   = si.getTimeRange();
         
         i.setBusy(days, tr);
         l.setBusy(days, tr);
         
         int wtu = i.getCurWtu();
         wtu += si.getWtuTotal();
         i.setCurWtu(wtu);
         
         bookSection(si.getCourse());
         
         this.items.add(si);
         
         r = true;
      }
      
      return r;
   }
   
   /**
    * Books another section of the given course. 
    * @param c
    */
   private void bookSection (Course c)
   {
      debug ("BOOKING SECTION FOR " + c);
      if (!this.courseCount.containsKey(c))
      {
         this.courseCount.put(c, 0);
      }
      
      int i = this.courseCount.get(c);
      i ++;
      this.courseCount.put(c, i);
      
      if (i == c.getNumOfSections())
      {
         debug ("REMOVING IT");
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
      
      if (!i.isAvailable(days, tr))
      {
         r = false;
      }
      if (!l.isAvailable(days, tr))
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
   public Vector<ScheduleItem> generate (List<Course> c_list, 
                                         List<Instructor> i_list, 
                                         List<Location> l_list)
   {
      initGenData(c_list, i_list, l_list);

      debug ("GENERATING");
      debug ("COURSES: " + this.cSourceList);
      debug ("INSTRUCTORS: " + this.iSourceList);
      debug ("LOCATIONS: " + this.lSourceList);
      
      while (shouldKeepGenerating())
      {
         Vector<Instructor> toRemove = new Vector<Instructor>();         
         
         addStaff();
         
         for (Instructor i : this.iSourceList)
         {
            debug ("HAVE INSTRUCTOR " + i);
            try
            {
               ScheduleItem si = genSIForInstructor(i);
               debug ("GOT " + si);
               add(si);
            }
            catch (InstructorCanTeachNothingException e)
            {
               System.err.println (e);
               toRemove.add(i);
            }
            catch (CouldNotBeScheduledException e)
            {
               System.out.println ("GAH");
               System.out.println (e);
            }
         }
         /*
          * Now that we're not using the list of instructors, we can remove 
          * those which were deemed unable to teach anymore
          */
         this.iSourceList.removeAll(toRemove);
      }
      
      return this.getItems();
   }
   
   private boolean shouldKeepGenerating ()
   {
      return this.cSourceList.size() > 0;
   }
   
   /**
    * Adds the special 'STAFF' instructor to our list of instructors if the
    * list is empty.
    */
   private void addStaff ()
   {
      if (this.iSourceList.isEmpty())
      {
         this.iSourceList.add(Staff.getStaff());
      }
   }
   
   /**
    * Clears the record-keeping data associated w/ generation. Sets the list of
    * courses, instructors, and locations to the provides arguments.<br>
    * <br>
    * The special location 'Tba' is added to the end of the location list
    * 
    * @param c_list List of Courses that'll be put into the schedule
    * @param i_list List of Instructors that'll be put into the schedule
    * @param l_list List of Locations that'll be put into the schedule
    * 
    * @see Tba
    */
   private void initGenData (List<Course> c_list, List<Instructor> i_list, 
      List<Location> l_list)
   {
      cSourceList = new Vector<Course>(c_list);
      iSourceList = new Vector<Instructor>(i_list);
      lSourceList = new Vector<Location>(l_list);
      
      lSourceList.add(Tba.getTba());
   }
   
   /**
    * Generates a ScheduleItem for a given instructor. Guarantees that no other
    * ScheduleItem could be created which this instructor would want 
    * <b>more</b> than this one.
    *  
    * @param i Instructor build ScheduleItems for
    * 
    * @return A ScheduleItem which this instructor wants to teach at least as 
    *         much as every other ScheduleItem that might be produced
    *         
    * @throws InstructorCanTeachNothingException if no course exists which the 
    *         Instructor wants to teach and/or no course exists which does not
    *         exceed the Instructor's WTU limit. 
    *         
    * @throws CouldNotBeScheduledException if no time/location could be found
    *         for the Instructor to teach the course (and possible its lab)
    *
    * @see SiMap#put(ScheduleItem)
    * @see ScheduleItem#getValue()
    */
   private ScheduleItem genSIForInstructor (Instructor i)
      throws InstructorCanTeachNothingException,
             CouldNotBeScheduledException
   {
      Vector<ScheduleItem> sis = new Vector<ScheduleItem>();
      Vector<ScheduleItem> si_list = new Vector<ScheduleItem>();

      /*
       *  Get ScheduleItems for the lecture
       */
      ScheduleItem lec_base = new ScheduleItem();
      
      lec_base.setInstructor(i);
      
      lec_base    = findCourse(lec_base);
      debug ("FOUND COURSE");
      si_list = findTimes(lec_base, this.bounds);
      debug ("GOT " + si_list.size() + " TIMES");
      si_list = findLocations(si_list);
      debug ("GOT " + si_list.size() + " LOCATIONS");
      
      /*
       * Handle lab component, if necessary
       * 
       * TODO: Add support for using an instructor other than the lectures
       */
      Lab lab = lec_base.getCourse().getLab();
      if (lab != null)
      {
         debug ("HAS LAB " + lab);
         
         si_list = addLab(lab, si_list);
         debug ("MADE " + si_list.size() + " POSSIBLE LAB PAIRINGS");
      }
      
      /*
       * The map will prune out items which are impossible
       */
      SiMap sortedItems = new SiMap(si_list);
      if (sortedItems.size() == 0)
      {
         throw new CouldNotBeScheduledException();
      }
      return new SiMap(si_list).getBest();
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
         debug ("TRY COURSE " + temp);
         int pref = i.getPreference(temp);
         debug ("PREF IS: " + pref);
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

      debug ("BEST: " + bestC);
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
   private Vector<ScheduleItem> findTimes (ScheduleItem si, TimeRange range)
   {
      debug ("FINDING TIMES");
      Vector<ScheduleItem> sis = new Vector<ScheduleItem>();
      Course c = si.getCourse();
      Instructor i = si.getInstructor();

      TimeRange tr = new TimeRange(range.getS(), c.getDayLength());
      for (; !tr.getE().equals(range.getE()); tr.addHalf())
      {
         Week days = c.getDays();

         debug ("CONSIDERING TR: " + tr);
         if (i.isAvailable(days, tr))
         {
            debug ("AVAILABLE");
            if (i.getAvgPrefForTimeRange(days, tr) > 0)
            {
               debug ("WANTS");
               ScheduleItem toAdd = si.clone();
               toAdd.setDays(days);
               toAdd.setTimeRange(tr);
               
               sis.add(toAdd);
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
         TimeRange tr = si.getTimeRange();
         for (Location l : this.lSourceList)
         {
            if (l.isAvailable(days, tr))
            {
               if (l.providesFor(si.getCourse()))
               {
                  /*
                   * I clone so we don't keep changing the same object...that'd
                   * be pretty bad.
                   */
                  ScheduleItem base = si.clone();
                  base.setLocation(l);
                  
                  si_list.add(base);
               }
            }
         }
      }
      
      return si_list;
   }
   
   /**
    * Adds a given ScheduleItem lab to every lecture ScheduleItem in a list. 
    * Each new lab ScheduleItem for the same lecture will have potentially 
    * different times and/or locations
    * 
    * @param lab_base Contains base information to be used in all lad 
    *        ScheduleItems created
    * @param lec_si_list List of lecture ScheduleItems to pair with every 
    *        possible lab ScheduleItem we can make for them
    *        
    * @return List of all pairs of lecture/lab ScheduleItems which could be
    *         scheduled for times the instructor is currently available. 
    */
   private Vector<ScheduleItem> addLab (Lab lab,
      List<ScheduleItem> lec_si_list)
   {
      ScheduleItem lab_base = new ScheduleItem();
      lab_base.setCourse(lab);
      
      Vector<ScheduleItem> si_list = new Vector<ScheduleItem>();
      
      /*
       * We try to pair labs with every lecture ScheduleItem.
       */
      for (ScheduleItem lec_si: lec_si_list)
      {
         Vector<ScheduleItem> lab_si_list = new Vector<ScheduleItem>();
         //TODO: Support unteathered instructor choice
         lab_base.setInstructor(lec_si.getInstructor());
         TimeRange tr;
         if (lab.isTethered())
         {
            /*
             *  Find times directly after the lecture only
             */
            tr = new TimeRange(lec_si.getEnd(), lab.getLength());
         }
         /*
          * Otherwise, lab can go anywhere within schedule bounds
          */
         else
         {
            tr = this.bounds;
         }
         lab_si_list = findTimes(lab_base, tr);
         lab_si_list = findLocations (lab_si_list);
         
         /*
          * For every lab ScheduleItem created, pair it with the lecture 
          * ScheduleItem. 
          * 
          * We are not concerned with overlap problems here. The SiMap, paired 
          * with the 'getValue' method of a ScheduleItem will help us with that
          * later
          */
         for (ScheduleItem lab_si: lab_si_list)
         {
            /*
             * TODO: Add support to consider lab enrollment vs. lecture 
             *       enrollment
             */
            ScheduleItem clone = lec_si.clone();
            lec_si.setLab(lab_si);
            
            si_list.add(clone);
         }
      }
      
      return si_list;
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
