package edu.calpoly.csc.scheduler.model.schedule;

import java.io.Serializable;
import java.util.*;

import edu.calpoly.csc.scheduler.model.db.*;
import edu.calpoly.csc.scheduler.model.db.cdb.*;
import edu.calpoly.csc.scheduler.model.db.idb.*;
import edu.calpoly.csc.scheduler.model.db.ldb.*;

import edu.calpoly.csc.scheduler.model.schedule.CouldNotBeScheduledException.*;

/**
 * <h1>Automated Scheduling Process</h1>
 * 
 * The scheduling algorithm follows a step-by-step process to schedule courses.
 * It is exhaustive and guarantees that all courses given to the algorithm will
 * be scheduled.
 * 
 * <h2>Choosing Course</h2>
 * 
 * Courses are not algorithmically "chosen". Instead, the algorithm simply loops
 * through every course "c" until all sections of "c" are scheduled. You'll want
 * to check out {@link SectionTracker}, 
 * {@link #initGenData(Collection<Course>)}, and 
 * {@link #getSectionTracker(Course)} to see how we keep track of course 
 * sections.
 * 
 * <h2>Choosing Instructor</h2>
 * 
 * Instructors are chosen based on who most desires to teach a given course. If
 * the one who most wants to teach it no longer can (i.e. his WTU's are maxed), 
 * he'll be removed from consideration and more instructors will be tested. Take
 * a look at {@link #findInstructor(Course, List<Instructor>)} to see how this 
 * is implemented.
 * 
 * If no Instructor can be found (no one is capable of teaching the Course, 
 * which can be b/c of preference or wtu reasons), the special {@link Staff} 
 * Instructor is used. This person always wants to teach a course and is always
 * available to teach at any time.
 * 
 * <h2>Choosing Times</h2>
 * 
 * Finding times depends on the type of course you're finding a time for. If 
 * you're scheduling a lecture course, it's easy. Lab courses, on the other 
 * hand, are a horse of a different color.<br>
 * <br>
 * Times per day will be figured by dividing the Course's length across the days
 * it's to be taught ({@link Course#getDayLength()}).<br> 
 * <br>
 * Times are selected by picking the time range across the Course's days 
 * ({@link Course#getDays()}) for which the chosen instructor want to teach
 * the most; the {@link TimeRange} for which he has the highest average 
 * preference ({@link Instructor#getAvgPrefForTimeRange(Week, TimeRange)}). If
 * an instructor has a pref of '0' for any time slot within the time 
 * {@link TimeRange}, the entire range will be disregarded.<br> 
 * <br>
 * 
 * <h3>Finding Lecture Times</h3>
 * 
 * Lecture times are found within the time bounds specified in the 
 * {@link #lec_bounds}. By default, this is 7a-10p. (My understanding of this is 
 * that this time is global throughout all Cal Poly).<br>
 * <br> 
 *
 * <h2>Choosing Location</h2>
 * 
 * Blah blah blah.
 * 
 * <h1>Selecting the Best Item</h1>
 * 
 * <h1>Further Design Suggestions/Options</h1>
 * 
 * Blah blah blah.
 * 
 * @author Eric Liebowitz
 * @version Nov 14, 2011
 */
public class Schedule extends DbData implements Serializable
{
   public static final long serialVersionUID = 1778968142419846280L;

	/**
    * Used for debugging. Toggle it to get debugging output
    */
   private static final boolean DEBUG = true;
   
   /**
    * Prints a message to STDERR if DEBUG is true
    * 
    * @param s String to print
    */
   private static void debug (String s)
   {
      if (DEBUG)
      {
         System.err.println(s);
      }
   }

   /**
    * List of courses that are to be added to the schedule. This list will be
    * shortened as courses are fully scheduled during generation.
    */
   private List<Course> cSourceList = new Vector<Course>();
   /**
    * List of instructors that are to be added to the schedule. This list will
    * be shortend as instructors become unavailable to teach anymore courses.
    */
   private List<Instructor> iSourceList = new Vector<Instructor>();
   /**
    * List of location that are to be made available for schedule generation.
    */
   private List<Location> lSourceList = new Vector<Location>();

   private HashMap<Course, SectionTracker> sections = new HashMap<Course, SectionTracker>();

   /**
    * The global start/end times for a given day on the schedule. Default bounds
    * are 7a-10p.
    */
   private TimeRange bounds = new TimeRange(new Time(7, 0), new Time(22, 0));

   /**
    * The TimeRange within which you want lectures to be taught
    */
   private TimeRange lec_bounds = new TimeRange(bounds);
   /**
    * The TimeRange within which you want labs to be taught
    */
   private TimeRange lab_bounds = new TimeRange(bounds);
   
   /**
    * List of ScheduleItems which generation will create. This is the "schedule"
    * that the user will see.
    */
   private Vector<ScheduleItem> items = new Vector<ScheduleItem>();

   /**
    * Set of currently conflicting schedule items. These are <b>not</b> 
    * considered during generation. They're stored here for the view's purposes
    * so it can accurately display/color conflicting things that need to be 
    * fixed.<br>
    * <br>
    * It's a HashSet so that you don't add duplicate conflicting items (I'm not
    * sure why you'd try to do that, but...) 
    */
   private HashSet<ScheduleItem> dirtyList = new HashSet<ScheduleItem>();
   
   /**
    * Human-readable string to identify this schedule
    */
   private String name;
   /**
    * The department this schedule is for
    */
   private String dept = "";

   /**
    * Default constructor. Does nothing but give you back a new object.
    */
   public Schedule ()
   {
	   this.setlSourceList(null);
   }

   /**
    * Creates a schedule w/ the given lists of Instructors and Locations taken
    * into consideration when it generates.
    * 
    * @param collection List of instructors to use
    * @param collection2 List of locations to use
    */
   public Schedule (Collection<Instructor> collection, Collection<Location> collection2)
   {
      this.iSourceList = new Vector<Instructor>(collection);
      this.setlSourceList(collection2);
   }
   
   /**
    * Generates and adds a ScheduleItem to teach a given course on a given set 
    * of days in a given time range. This method will also generate labs to 
    * go with the given Course, if it has any lab component
    * 
    * @param c Course to teach
    * @param days Days to teach
    * @param s Time of day the course should start
    * 
    * @see #lec_bounds
    * @see #lab_bounds
    * @see #generate(List\<Course\>)
    */
   public void genItem (Course c, Week days, Time s)
   {
      /*
       * We'll need a clone b/c we have to reach in and change some of its 
       * fields (such as 'days' and its lab(s)).
       */
      Course clone = new Course(c);
      
      /*
       * We'll limit the range the course can be taught so it ends up right 
       * where the user wants it
       */
      TimeRange oldBounds = 
         setLecBounds(new TimeRange(s, clone.splitLengthOverDays(days.size())));
      
      clone.setDays(days);
      generate(new Vector<Course>(Arrays.asList(new Course[]{clone})));
      
      /*
       * Reset lec_bounds back to whatever it was before we did this
       */
      setLecBounds(oldBounds);
   }
   
   /**
    * Moves an already-existing ScheduleItem from one place on the schedule 
    * to another. If this ScheduleItem has teathered lab ScheduleItems attached
    * to it, those labs will be moved as well. 
    * 
    * @param si ScheduleItem to move
    * @param days Days you want the ScheduleItem to be taught on
    * @param s The start time you want the ScheduleItem to be taught on
    * 
    * @return The new ScheduleItem, w/ its fields updated to where it was placed
    * 
    * @throws CouldNotBeScheduledException If you've moved the ScheduleItem to
    *         a time where the location is in use or the instructor is already
    *         teaching. 
    */
   public ScheduleItem move (ScheduleItem si, Week days, Time s) 
      throws CouldNotBeScheduledException
   {
      ScheduleItem fresh_si = new ScheduleItem(si);
      if (this.remove(si))
      {
         Course c = si.getCourse();
         
         TimeRange tr = new TimeRange(s, c.splitLengthOverDays(days.size()));
      
         fresh_si.setDays(days);
         fresh_si.setTimeRange(tr);
      
         this.add(fresh_si);
         
         /*
          * If the lab for the SI was teathered, we need to move it to just 
          * after the fresh_si
          */
         Lab lab = c.getLab();
         if (lab != null && lab.isTethered())
         {
            Time lab_s = tr.getE();
            for (ScheduleItem lab_si: si.getLabs())
            {
               move(lab_si, days, lab_s);
            }
         }
      }
      return fresh_si;
   }
   
   /**
    * Removes a given ScheduleItem from the schedule. Updates instructor and
    * location availability to show this new free time. The course's number of
    * sections taught are decremented. If this makes the course again eligible
    * to be taught, it will be added back to our cSourceList.
    * 
    * @param si ScheduleItem to remove
    * 
    * @return if the specified item was removed or not. It will not be removed
    *         if it does not exist in our list of items
    */
   public boolean remove (ScheduleItem si)
   {
      boolean r = false;
      if (this.items.contains(si))
      {
         r = true;
         
         Course c = si.getCourse();
         Instructor i = si.getInstructor();
         Location l = si.getLocation();
         Week days = si.getDays();
         TimeRange tr = si.getTimeRange();

         this.items.remove(si);
         i.book(false, days, tr);
         l.book(false, days, tr);

         int wtu = i.getCurWtu();
         wtu -= si.getWtuTotal();
         i.setCurWtu(wtu);

         SectionTracker st = getSectionTracker(c);
         st.removeSection(si.getSection());
         
         /*
          * Remove the labs only if they're teathered to the course
          */
         if (si.hasLabs())
         {
            if (c.getLab().isTethered())
            {
               remove(si.getLabs());
            }
         }
      }
      return r;
   }
   
   private void remove (List<ScheduleItem> toRemove)
   {
      for (ScheduleItem si: toRemove)
      {
         remove(si);
      }
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
   public boolean add (ScheduleItem si) throws CouldNotBeScheduledException
   {
      boolean r;
      /*
       * Verification checks the ScheduleItem and its lab component (if
       * applicable) all in one go.
       */
      if (r = verify(si))
      {
         book(si);
      }

      return r;
   }

   /**
    * Applies all the day/time/wtu commitments of a ScheduleItem to instructors
    * and locations to take up their availability. Instructor's WTU count is
    * updated. Course section count is also updated.<br>
    * <br>
    * <b>Note:</b> The ScheduleItem is not verified here! You must call verify
    * before booking to ensure it's safe to do so.
    * 
    * @param si The ScheduleItem w/ the days, times, etc. which'll be booked in
    *        the schedule
    * 
    * @see #verify(ScheduleItem)
    */
   private void book (ScheduleItem si)
   {
      Course c = si.getCourse();
      Instructor i = si.getInstructor();
      Location l = si.getLocation();
      Week days = si.getDays();
      TimeRange tr = si.getTimeRange();

      debug ("BOOKING");
      
      i.book(true, days, tr);
      l.book(true, days, tr);

      int wtu = i.getCurWtu();
      debug ("WTU BEFORE: " + wtu);
      wtu += c.getWtu();
      debug ("NOW AT " + wtu);
      i.setCurWtu(wtu);

      SectionTracker st = getSectionTracker(si.getCourse());
      if (st.addSection())
      {
         this.items.add(si);
         si.setSection(st.getCurSection());
         debug ("JUST ADDED A SECTION OF " + si.getCourse());
      }
      
      debug ("ITEM COUNT AT : " + this.items.size());
   }

   /**
    * Gets the SectionTracker associated with course 'c'. If no tracker yet
    * exists for the Course, one is created and added.
    * 
    * @param c Course to get the tracker for
    * 
    * @return this.sections.get(c);
    */
   private SectionTracker getSectionTracker (Course c)
   {
      if (!this.sections.containsKey(c))
      {
         this.sections.put(c, new SectionTracker(c));
      }
      return this.sections.get(c);
   }

   /**
    * Ensures that the given ScheduleItem can be scheduled. This means it
    * doesn't double book instructors/locations, and the instructor can teach
    * the lecture/lab w/o exceeding his max wtu limit. The instructor must also
    * be able to teach during the times specified.<br>
    * <br>
    * Section counts are checked to ensure you don't overbook a course.<br>
    * <br>
    * If this method returns true, it is safe to call 'book(si)'.
    * 
    * @param si ScheduleItem to verify
    * 
    * @return true if 'si' can be taught by its instructor, at its location, 
    *         and the instructor can teach the course.
    * 
    * @throws CouldNotBeScheduledException if a time/location conflict is 
    *         encountered, the instructor cannot teach during the given times, 
    *         the instructor cannot teach the course, or if no more sections of
    *         the given course can be taught 
    * 
    * @see Instructor#canTeach(Course)
    */
   private boolean verify (ScheduleItem si) throws CouldNotBeScheduledException
   {
      Week days = si.getDays();
      Course c = si.getCourse();
      TimeRange tr = si.getTimeRange();
      Instructor i = si.getInstructor();
      Location l = si.getLocation();

      if (!i.isAvailable(days, tr))
      {
         throw new CouldNotBeScheduledException(ConflictType.I_DBL_BK, si);
      }
      if (!l.isAvailable(days, tr))
      {
         throw new CouldNotBeScheduledException(ConflictType.L_DBL_BK, si);
      }
      if (i.getAvgPrefForTimeRange(days, tr) == 0)
      {
         throw new CouldNotBeScheduledException(ConflictType.NO_DESIRE, si);
      }
      if (!i.canTeach(c))
      {
         throw new CouldNotBeScheduledException(ConflictType.CANNOT_TEACH, si);
      }
      SectionTracker st = this.getSectionTracker(c);
      if (!st.canBookAnotherSection())
      {
         throw new CouldNotBeScheduledException(ConflictType.NO_SECTIONS_LEFT, 
            si);
      }

      return true;
   }

   /**
    * Does schedule generation
    * 
    * @param c_list List of courses you want scheduled
    */
   public Vector<ScheduleItem> generate (Collection<Course> c_list)
   {
      initGenData(c_list);

      debug("GENERATING");
      debug("COURSES: " + this.cSourceList);
      debug("INSTRUCTORS: " + this.iSourceList);
      debug("LOCATIONS: " + this.lSourceList);

      for (Course c : this.cSourceList)
      {
         debug ("MAKING SI's FOR COURSE " + c);
         SectionTracker st = this.sections.get(c);
         while (st.canBookAnotherSection())
         {
            debug ("SECTIONS SCHEDULED: " + st.getCurSection()
               + " / " + st.getMaxSections());
            
            ScheduleItem lec_si = genLectureItem(c);
            debug ("MADE LEC_SI\n" + lec_si);
            try
            {
               add(lec_si);
               debug ("ADDED IT");
            }
            catch (CouldNotBeScheduledException e)
            {
               System.err.println("GENERATION MADE A BAD LEC");
               System.err.println(lec_si);
            }
         
            Lab lab = c.getLab();
            if (lab != null)
            {
               debug ("HAVE LAB " + lab);
               /*
                * We need to schedule labs until we have enough enrollment to
                * supply the lecture
                */
               int curEnrollment = 0;
               int goal = c.getEnrollment();
               while (curEnrollment < goal)
               {

                  ScheduleItem lab_si = genLabItem(lab, lec_si);
                  try
                  {
                     /*
                      * If the add fails, we won't consider its enrollment, 
                      * which is good. So, don't screw w/ the order here
                      */
                     add(lab_si);
                     lec_si.addLab(lab_si);
                     curEnrollment += lab.getEnrollment();
                  }
                  catch (CouldNotBeScheduledException e)
                  {
                     System.err.println("GENERATION MADE A BAD LAB");
                     System.err.println(lab_si);
                  }
               }
            }
         }
      }

      debug ("GENERATION FINISHED W/: " + this.getItems().size());
      
      return this.getItems();
   }

   /**
    * Clears the record-keeping data associated w/ generation. Sets the list of
    * courses to the provided list. Resets all section tracker objects to use 
    * the section information contained in the courses provided in the given 
    * list. 
    * 
    * @param c_list List of Courses that'll be put into the schedule
    */
   private void initGenData (Collection<Course> c_list)
   {
      for (Course c: (cSourceList = new Vector<Course>(c_list)))
      {
         SectionTracker st = getSectionTracker(c);
         st.resetSectionCount(c.getNumOfSections());
      }
   }

   /**
    * Creates a lecture ScheduleItem for the given course. Finds an instructor
    * for the course, and subsequently finds times and locations which that
    * instructor wants to teach for.<br>
    * <br>
    * In the case that no instructor or location can be found, Staff and Tba
    * will be used to make sure generation can continue.
    * 
    * @param lec Course to schedule
    * 
    * @return A ScheduleItem which is safe to add to the schedule
    * 
    * @see Tba#getTba()
    * @see Staff#getStaff()
    */
   private ScheduleItem genLectureItem (Course lec)
   {
      Vector<ScheduleItem> si_list = new Vector<ScheduleItem>();
      ScheduleItem lec_si = new ScheduleItem();

      lec_si.setCourse(lec);
      lec_si.setInstructor(findInstructor(lec));

      return genBestTime(lec_si, this.lec_bounds);
   }

   /**
    * Creates a ScheduleItem for the given lab. The 'lec_si' is provided in case
    * the lab is tied to the lecture in any particular way. If it is, we can
    * easily access its data at this point.<br>
    * <br>
    * In the case that no instructor or location can be found, Staff and Tba
    * will be used to make sure generation can continue.
    * 
    * @param lab Lab to schedule
    * @param lec_si Lecture ScheduleItem which holds information which lab
    *        scheduling might need
    * 
    * @return A ScheduleItem for 'lab' which is safe to add to the schedule
    * 
    * @see Tba#getTba()
    * @see Staff#getStaff()
    */
   private ScheduleItem genLabItem (Lab lab, ScheduleItem lec_si)
   {
      ScheduleItem lab_si = new ScheduleItem();

      lab_si.setCourse(lab);
      lab_si.setInstructor(getLabInstructor(lab, lec_si));

      TimeRange tr = this.lab_bounds;
      if (lab.isTethered())
      {
         tr = new TimeRange(lec_si.getStart(), lab.getDayLength());
      }

      return genBestTime(lab_si, tr);
   }

   /**
    * Generates a ScheduleItem for a given instructor. Guarantees that no other
    * ScheduleItem could be created which this instructor would want <b>more</b>
    * than this one.<br>
    * <br> 
    * If there is no way that our 'base' ScheduleItem's instructor can teach
    * at any time in the supplied time range, this will change the considered
    * instructor to STAFF. Consequently, <i>this method guarantees that a valid
    * ScheduleItem will be generated</i>. It is <b>not</b> guaranteed that the
    * instructor will be the one in 'base', nor is it guaranteed the location 
    * will not be TBA.
    * 
    * @param base ScheduleItem containing basic info for generating. In 
    *        particular, the instructor and course must already be defined
    * @param tr TimeRange we'll look within when scheduling
    * 
    * @return A ScheduleItem which this instructor wants to teach at least as
    *         much as every other ScheduleItem that might be produced
    * 
    * @see SiMap#put(ScheduleItem)
    * @see ScheduleItem#getValue()
    * @see Staff#getStaff()
    * @see Tba#getTba()
    * @see #findTimes(Course, List\<Instructor\>)
    */
   private ScheduleItem genBestTime (ScheduleItem base, TimeRange tr)
   {
      Vector<ScheduleItem> si_list = new Vector<ScheduleItem>();

      /*
       * If we can't find times for the instructor in our base, we'll have to 
       * try other instructors until we find one w/ at least one time he
       * can teach
       */
      si_list = findTimes(base, tr);
      if (si_list.isEmpty())
      {
         ScheduleItem clone = base.clone();
         Course c = base.getCourse();
         
         /*
          * Keep track of instructors we've tried so we don't use them again. 
          * Eventually, if none are found, we'll end up using STAFF, which is
          * eager to please.
          */
         Vector<Instructor> haveTried = new Vector<Instructor>();
         haveTried.add(clone.getInstructor());
         do
         {
            Instructor i = findInstructor(c, haveTried);
            
            debug ("NO TIMES FOUND FOR " + base.getInstructor());
            debug ("TRYING " + i);
            
            clone.setInstructor(i);
            si_list = findTimes(clone, tr);
            haveTried.add(i);
            
         } while (si_list.isEmpty());
      }
      
      debug("GOT " + si_list.size() + " TIMES");
      
      si_list = findLocations(si_list);
      debug("GOT " + si_list.size() + " LOCATIONS");

      /*
       * The map will prune out items which are impossible. Note that there will
       * always be at least one location available: TBA
       */
      SiMap sortedItems = new SiMap(si_list);

      return new SiMap(si_list).getBest();
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
      debug("FINDING TIMES IN RANGE " + range);
      Vector<ScheduleItem> sis = new Vector<ScheduleItem>();
      Course c = si.getCourse();
      Instructor i = si.getInstructor();
      
      TimeRange tr = new TimeRange(range.getS(), c.getDayLength());
      for (; tr.getE().compareTo(range.getE()) < 1; tr.addHalf())
      {
         Week days = c.getDays();

         debug("CONSIDERING TR: " + tr);
         if (i.isAvailable(days, tr))
         {
            debug("AVAILABLE");
            double pref;
            if ((pref = i.getAvgPrefForTimeRange(days, tr)) > 0)
            {
               debug("WANTS: " + pref);
               ScheduleItem toAdd = si.clone();
               toAdd.setDays(days);
               toAdd.setTimeRange(new TimeRange(tr));

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
    * @param sis List of ScheduleItems with their instructor, course, days, and
    *        times fields already set.
    * 
    * @return A list of locaitons which can be taught on the days for course 'c'
    *         during at least the TimeRanges passed in.
    */
   private Vector<ScheduleItem> findLocations (Vector<ScheduleItem> sis)
   {
      Vector<ScheduleItem> si_list = new Vector<ScheduleItem>();

      debug ("HAVE " + sis.size() + " ITEMS FOR LOCATIONS TO TRY");
      for (ScheduleItem si : sis)
      {
         Week days = si.getDays();
         TimeRange tr = si.getTimeRange();
         for (Location l : this.lSourceList)
         {
            debug ("TRYING LOCATION " + l + " with time " + tr);
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
    * Returns an instructor to teach a given lab. If the lab is told to use the
    * same instructor as its lecture, that instructor is used. Otherwise, a new
    * instructor will be found who wants to teach the lab.
    * 
    * @param lab Lab we're finding an instructor for
    * @param lec_si Schedule information for the lecture component. Used to
    *        extract the instructor whose teaching the lecture in the case where
    *        the lab is teathered to the lecture.
    * 
    * @return An instructor to teach the lab. If the lab is teathered, this will
    *         be the same instructor returned by 'lec_si.getInstructor'. If no
    *         instructor is able to teach, STAFF is returned
    * 
    * @see Staff#getStaff()
    */
   private Instructor getLabInstructor (Lab lab, ScheduleItem lec_si)
   {
      Instructor r;

      if (!lab.shouldUseLectureInstructor())
      {
         r = findInstructor(lab);
      }
      else
      {
         Instructor i = lec_si.getInstructor();
         if (i.canTeach(lab))
         {
            r = i;
         }
         else
         {
            r = Staff.getStaff();
         }
      }
      return r;
   }

   /**
    * Determines the time range a lab can be taught for. In particular, if the
    * lab is teathered to its lecture, it must be taught directly after the
    * lecture. Otherwise, it can float around and be taught anywhere.
    * 
    * @param lab Lab to check for teathering
    * @param lec_si Schedule info for the lecture. Used for figuring out when a
    *        teathered lab to start
    * 
    * @return the TimeRange within which the given lab can be taught
    */
   private TimeRange getLabTimeRange (Lab lab, ScheduleItem lec_si)
   {
      TimeRange tr;
      if (lab.isTethered())
      {
         /*
          * Find times directly after the lecture only
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
      return tr;
   }

   /**
    * Finds an instructor for the given course, excluding any instructors 
    * in a given list
    * 
    * @param c Course to find an instructor for
    * @param doNotPick List of instructor to <b>not</b> choose
    * 
    * @return An intsructor to teach 'c'. This can be STAFF if no instructor
    *         if capable of teaching
    */
   private Instructor findInstructor (Course c, List<Instructor> doNotPick)
   {
      Instructor r = Staff.getStaff();
      int curMaxPref = 0;
      
      debug ("FINDING INSTRUCTOR FOR " + c);
      debug ("EXLUDING: " + doNotPick);
      for (Instructor i : this.iSourceList)
      {
         debug ("CONSIDERING " + i);
         if (doNotPick == null || !doNotPick.contains(i))
         {
            debug ("NOT EXCLUDED");
            if (i.canTeach(c))
            {
               debug ("CAN");
               int pref = i.getPreference(c);
               debug ("DESIRE: " + pref);
               if (pref > curMaxPref)
               {
                  debug ("WANTS MORE: " + pref + " > " + curMaxPref);
                  r = i;
                  curMaxPref = pref;
               }
            }
         }
      }
      
      if (r.equals(Staff.getStaff()))
      {
         debug ("NOBODY FOUND. GOING WITH STAFF");
      }
      
      return r;
   }
   
   /**
    * Finds an instructor who wants to teach a given course.
    * 
    * @param c Course to find an instructor for
    * 
    * @return An instructor who can and wants to teach the course. If no
    *         instructor can be found, Staff.getStaff is returned
    * 
    * @see Staff#getStaff()
    */
   private Instructor findInstructor (Course c)
   {
      return findInstructor(c, null);
   }

   /***********************
    * GETTERS & SETTERS *
    ***********************/

   /**
    * Adds a ScheduleItem to our list of bad/conflicting ScheduleItems. The item
    * will only be added if 1) It's actually a conflicting item and 2) It's not
    * already present in our list of conflicting items
    *  
    * @param si Conflicting Item to add
    * 
    * @return true if the item was added to our list. False otherwise. 
    */
   public boolean addConflictingItem (ScheduleItem si)
   {
      boolean isDirty = false;
      try
      {
         verify(si);
      }
      catch (CouldNotBeScheduledException e)
      {
         isDirty = true;
      }
      
      boolean r = false;
      if (isDirty)
      {
         r = this.dirtyList.add(si);
      }
      
      return r;
   }
   
   /**
    * Removes a ScheduleItem from our list of conflicting items. 
    * 
    * @param si ScheduleItem to remove from the list
    * 
    * @return true if the item actually existed in our list and was removed. 
    *         False otherwise.
    */
   public boolean removeConflictingItem (ScheduleItem si)
   {
      return this.dirtyList.remove(si);
   }
   
   /**
    * Returns the list of conflicting items
    * 
    * @return the list of conflicting items.
    */
   public HashSet<ScheduleItem> getDirtyList ()
   {
      return this.dirtyList;
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
      return getScheduleId();
   }

   /**
    * Sets the id to the given parameter.
    * 
    * @param id the id to set
    */
   public void setId (Integer id)
   {
      setScheduleId(id);
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

   /**
    * Returns the dept
    * 
    * @return the dept
    */
   public String getDept ()
   {
      return dept;
   }

   /**
    * Sets the dept to the given parameter.
    * 
    * @param dept the dept to set
    */
   public void setDept (String dept)
   {
      this.dept = dept;
   }

   /**
    * Returns the cSourceList
    * 
    * @return the cSourceList
    */
   public List<Course> getcSourceList ()
   {
      return cSourceList;
   }

   /**
    * Sets the cSourceList to the given parameter.
    * 
    * @param cSourceList the cSourceList to set
    */
   public void setcSourceList (List<Course> cSourceList)
   {
      this.cSourceList = cSourceList;
   }

   /**
    * Returns the iSourceList
    * 
    * @return the iSourceList
    */
   public List<Instructor> getiSourceList ()
   {
      return iSourceList;
   }

   /**
    * Sets the iSourceList to the given parameter.
    * 
    * @param iSourceList the iSourceList to set
    */
   public void setiSourceList (List<Instructor> iSourceList)
   {
      this.iSourceList = new Vector<Instructor>(iSourceList);
   }

   /**
    * Returns the lSourceList
    * 
    * @return the lSourceList
    */
   public List<Location> getlSourceList ()
   {
      return lSourceList;
   }

   /**
    * Sets the lSourceList to the given parameter. The special "TBA" location is
    * added to the end of the source list after setting.
    * 
    * @param lSourceList the lSourceList to set
    */
   public void setlSourceList (Collection<Location> lSourceList)
   {
      if (lSourceList == null)
      {
         lSourceList = new Vector<Location>();
      }
      this.lSourceList = new Vector<Location>(lSourceList);
      this.lSourceList.add(Tba.getTba());
   }

   /**
    * Sets the lecture time bounds
    * 
    * @param tr Bounds you want lectures to be taught within
    * 
    * @return The old lec_bounds value
    */
   private TimeRange setLecBounds (TimeRange tr)
   {
      TimeRange oldBounds = this.lec_bounds;
      this.lec_bounds = tr;
      return oldBounds;
   }

   public void verify ()
   {
      if (getScheduleId() ==  null)
      {
         throw new NullDataException();
      }
      if (name == null)
      {
         throw new NullDataException();
      }
      if (dept == null)
      {
         throw new NullDataException();
      }
   }
}