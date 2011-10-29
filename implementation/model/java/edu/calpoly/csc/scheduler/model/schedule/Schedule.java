package edu.calpoly.csc.scheduler.model.schedule;

import java.io.Serializable;
import java.util.*;

import edu.calpoly.csc.scheduler.model.db.*;
import edu.calpoly.csc.scheduler.model.db.cdb.*;
import edu.calpoly.csc.scheduler.model.db.idb.*;
import edu.calpoly.csc.scheduler.model.db.ldb.*;

/**
 * Represents a schedule. Supports methods for generating data from a list of
 * courses, instructors, and locations. Guarantees that a location/instructor
 * will never be double-booked.
 * 
 * @author Eric Liebowitz
 * @version Oct 25, 2011
 */
public class Schedule implements Serializable
{
   /**
    * Used for debugging. Toggle it to get debugging output
    */
   public static final boolean DEBUG = true;

   /**
    * Prints a message if DEBUG is true
    * 
    * @param s String to print
    */
   public static void debug (String s)
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
    * List of ScheduleItems which generation will create. This is the "schedule"
    * that the user will see.
    */
   private Vector<ScheduleItem> items = new Vector<ScheduleItem>();

   /**
    * This schedule's id
    */
   private Integer id;
   /**
    * The quarter id to link this schedule w/ a particular quarter
    */
   private String quarterId;
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
   }

   /**
    * Creates a schedule w/ the given lists of Instructors and Locations taken
    * into consideration when it generates.
    * 
    * @param i_list List of instructors to use
    * @param l_list List of locations to use
    */
   public Schedule (List<Instructor> i_list, List<Location> l_list)
   {
      this.iSourceList = new Vector<Instructor>(i_list);
      this.setlSourceList(l_list);
   }

   /**
    * Creates a ScheduleItem to teach a given course on a given set of days in a
    * given time range.
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
    */
   public ScheduleItem makeItem (Course c, Week days, Time s)
      throws CouldNotBeScheduledException
   {
      Vector<ScheduleItem> sis = new Vector<ScheduleItem>();

      TimeRange tr = new TimeRange(s, c.splitLengthOverDays(days.size()));

      ScheduleItem si = new ScheduleItem();
      si.setCourse(c);
      si.setDays(days);
      si.setTimeRange(tr);

      for (Instructor i : this.iSourceList)
      {
         if (i.isAvailable(days, tr))
         {
            ScheduleItem clone = si.clone();
            clone.setInstructor(i);
            List<ScheduleItem> list = Arrays.asList(new ScheduleItem[]
            {
               clone
            });

            /*
             * If there's a lab, fill our list with lectures all paired with
             * labs
             */
            // TODO: FIX
            // Lab lab = c.getLab();
            // if (lab != null)
            // {
            // list = addLab(lab, list);
            // }
            sis.addAll(list);
         }
      }

      /*
       * With our times, days, course, and instructor selected, we can just
       * throw locations at the problem. SiMap'll sort out everything and let
       * the best choice float to the top.
       */
      SiMap si_map = new SiMap(findLocations(sis));

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
    * Removes a given ScheduleItem from the schedule. Updates instructor and
    * location availability to show this new free time. The course's number of
    * sections taught are decremented. If this makes the course again eligible
    * to be taught, it will be added back to our cSourceList.
    * 
    * @param si ScheduleItem to remove
    * @return
    */
   public ScheduleItem remove (ScheduleItem si)
   {
      if (this.items.contains(si))
      {
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

         updateCourseSrcList(st);
      }
      return si;
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
      boolean r = false;
      /*
       * Verification checks the ScheduleItem and its lab component (if
       * applicable) all in one go.
       */
      if (verify(si))
      {
         book(si);
         if (si.hasLab())
         {
            book(si.getLabs());
         }
      }
      else
      {
         throw new CouldNotBeScheduledException();
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
    * @return true if the ScheduleItem was added. False otherwise.
    * 
    * @see #verify(ScheduleItem)
    */
   private void book (ScheduleItem si)
   {
      Instructor i = si.getInstructor();
      Location l = si.getLocation();
      Week days = si.getDays();
      TimeRange tr = si.getTimeRange();

      i.book(true, days, tr);
      l.book(true, days, tr);

      int wtu = i.getCurWtu();
      wtu += si.getWtuTotal();
      i.setCurWtu(wtu);

      this.items.add(si);

      SectionTracker st = getSectionTracker(si.getCourse());
      st.addSection();
      updateCourseSrcList(st);
   }

   /**
    * Books a list of ScheduleItems. You must have verified these yourself, as
    * this method won't do it.
    * 
    * @param sis List of ScheduleItems to book
    * 
    * @see #book(ScheduleItem)
    */
   private void book (List<ScheduleItem> sis)
   {
      for (ScheduleItem si : sis)
      {
         book(si);
      }
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
    * Removes the course associated w/ 'st' if no more sections of it can be
    * taught
    * 
    * @param st SectionTracker for the course we're updating
    * 
    * @see SectionTracker#canBookAnotherSection
    */
   private void updateCourseSrcList (SectionTracker st)
   {
      Course c = st.getCourse();
      if (st.canBookAnotherSection())
      {
         if (!this.cSourceList.contains(c))
         {
            this.cSourceList.add(c);
         }
      }
      else
      {
         this.cSourceList.remove(c);
      }
   }

   /**
    * Ensures that the given ScheduleItem can be scheduled. This means it
    * doesn't double book instructors/locations, and the instructor can teach
    * the lecture/lab w/o exceeding his max wtu limit. The instructor must also
    * be able to teach during the times specified.<br>
    * <br>
    * If the given ScheduleItem has a lab, it will also be verified. Should
    * either lab or lecture verification fail, you'll get false.<br>
    * <br>
    * Section counts are checked to ensure you don't overbook a course.<br>
    * <br>
    * If this method returns true, it is safe to call 'book(si)'.
    * 
    * @param si ScheduleItem to verify
    * 
    * @return true if 'si' (and lab, if applicable) can be taught by its
    *         instructor at its location, and the instructor can teach the
    *         course. I
    * 
    * @see Instructor#canTeach(Course)
    */
   private boolean verify (ScheduleItem si)
   {
      boolean r = true;

      Week days = si.getDays();
      Course c = si.getCourse();
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
      if (i.getAvgPrefForTimeRange(days, tr) == 0)
      {
         r = false;
      }
      if (!i.canTeach(si.getCourse()))
      {
         r = false;
      }
      SectionTracker st = this.sections.get(c);
      if (!st.canBookAnotherSection())
      {
         r = false;
      }

      if (si.hasLab())
      {
         r &= verify(si.getLabs());
      }

      return r;
   }

   /**
    * Just calls 'verify(ScheduleItem)' and returns whether we good all 'true'
    * values or not
    * 
    * @param sis List of ScheduleItems to verify
    * 
    * @return true if all ScheduleItems in 'sis' check out.
    * 
    * @see #verify(ScheduleItem)
    */
   private boolean verify (List<ScheduleItem> sis)
   {
      boolean r = true;
      for (ScheduleItem si : sis)
      {
         r &= verify(si);
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
   public Vector<ScheduleItem> generate (List<Course> c_list)
   {
      initGenData(c_list);

      debug("GENERATING");
      debug("COURSES: " + this.cSourceList);
      debug("INSTRUCTORS: " + this.iSourceList);
      debug("LOCATIONS: " + this.lSourceList);

      while (shouldKeepGenerating())
      {
         Vector<Instructor> toRemove = new Vector<Instructor>();
         addStaff();

         for (Instructor i : this.iSourceList)
         {
            debug("HAVE INSTRUCTOR " + i);
            try
            {
               ScheduleItem lec_si = new ScheduleItem();
               lec_si.setInstructor(i);
               lec_si = findCourse(lec_si);

               lec_si = genBestTime(lec_si, this.bounds);
               debug("LEC GOT " + lec_si);
               add(lec_si);
            }
            catch (InstructorCanTeachNothingException e)
            {
               System.err.println(e);
               toRemove.add(i);
            }
            catch (CouldNotBeScheduledException e)
            {
               System.out.println("GAH");
               System.out.println(e);
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

   public Vector<ScheduleItem> generate_beta (List<Course> c_list)
   {
      initGenData(c_list);

      debug("GENERATING");
      debug("COURSES: " + this.cSourceList);
      debug("INSTRUCTORS: " + this.iSourceList);
      debug("LOCATIONS: " + this.lSourceList);

      while (shouldKeepGenerating())
      {
         for (Course c: this.cSourceList)
         {
            ScheduleItem lec_si = genLectureItem(c);
            try { add(lec_si); }
            catch (CouldNotBeScheduledException e)
            {
               System.err.println ("GENERATION MADE A BAD LEC");
               System.err.println (lec_si);
            }
            
            Lab lab = c.getLab();
            if (lab != null)
            {
               int curEnrollment = 0;
               int goal = c.getEnrollment();
               while (curEnrollment < goal)
               {
               
                  ScheduleItem lab_si = genLabItem (lab, lec_si);
                  try 
                  { 
                     add(lab_si);
                     curEnrollment += lab.getEnrollment();
                  }
                  catch (CouldNotBeScheduledException e)
                  {
                     System.err.println ("GENERATION MADE A BAD LAB");
                     System.err.println (lab_si);
                  }
               }
            }
         }
      }

      return this.getItems();   
   }
   
   /**
    * Tells us whether we should go through another round of generation,
    * assigning one course to each instructor still able to teach.
    * 
    * @return this.cSourceList.size() > 0
    */
   private boolean shouldKeepGenerating ()
   {
      return this.cSourceList.size() > 0;
   }

   /**
    * Adds the special 'STAFF' instructor to our list of instructors if the list
    * is empty.
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
    * courses, instructors, and locations to the provides arguments.
    * 
    * @param c_list List of Courses that'll be put into the schedule
    * 
    * @see Tba
    */
   private void initGenData (List<Course> c_list)
   {
      cSourceList = new Vector<Course>(c_list);
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
      
      lec_si.setInstructor(findInstructor(lec));
      
      return genBestTime(lec_si, this.bounds);
   }

   /**
    * Creates a ScheduleItem for the given lab. The 'lec_si' is provided in
    * case the lab is tied to the lecture in any particular way. If it is, we 
    * can easily access its data at this point.<br>
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
      
      lab_si.setInstructor(getLabInstructor(lab, lec_si));
      
      TimeRange tr = this.bounds;
      if (lab.isTethered())
      {
         tr = new TimeRange(lec_si.getStart(), lab.getDayLength());
      }
      
      return genBestTime(lab_si, tr);
   }

   /**
    * Generates a ScheduleItem for a given instructor. Guarantees that no other
    * ScheduleItem could be created which this instructor would want <b>more</b>
    * than this one.
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
   private ScheduleItem genBestTime (ScheduleItem base, TimeRange tr)
   {
      Vector<ScheduleItem> si_list = new Vector<ScheduleItem>();

      si_list = findTimes(base, tr);
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
         debug("TRY COURSE " + temp);
         int pref = i.getPreference(temp);
         debug("PREF IS: " + pref);
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

      debug("BEST: " + bestC);
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
      debug("FINDING TIMES");
      Vector<ScheduleItem> sis = new Vector<ScheduleItem>();
      Course c = si.getCourse();
      Instructor i = si.getInstructor();

      TimeRange tr = new TimeRange(range.getS(), c.getDayLength());
      for (; !tr.getE().equals(range.getE()); tr.addHalf())
      {
         Week days = c.getDays();

         debug("CONSIDERING TR: " + tr);
         if (i.isAvailable(days, tr))
         {
            debug("AVAILABLE");
            if (i.getAvgPrefForTimeRange(days, tr) > 0)
            {
               debug("WANTS");
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
    * @param sis List of ScheduleItems with their instructor, course, days, and
    *        times fields already set.
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
    * @param lab Lab to be used in all lab ScheduleItems created
    * @param lec_si_list List of lecture ScheduleItems to pair with every
    *        possible lab ScheduleItem we can make for them
    * 
    * @return List of all pairs of lecture/lab ScheduleItems which could be
    *         scheduled for times the instructor is currently available.
    * 
    * @see #getLabInstructor(Lab, ScheduleItem)
    * @see #getLabTimeRange(Lab, ScheduleItem)
    * @see #pairLecWithlabs(ScheduleItem, Vector<lt>ScheduleItem<gt>)
    * 
    * @deprecated NOT READY. CAN GIVE ONE INSTRUCTOR LOTS O LABS
    */
   private Vector<ScheduleItem> addLab (Lab lab, List<ScheduleItem> lec_si_list)
   {
      ScheduleItem lab_base = new ScheduleItem();
      lab_base.setCourse(lab);

      SiMap si_map = new SiMap();
      Vector<ScheduleItem> si_list = new Vector<ScheduleItem>();

      /*
       * We try to pair labs with every lecture ScheduleItem.
       */
      for (ScheduleItem lec_si : lec_si_list)
      {
         debug("FINDING LAB FOR " + lec_si);

         Course lec = lec_si.getCourse();
         Vector<ScheduleItem> lab_si_list = new Vector<ScheduleItem>();

         lab_base.setInstructor(getLabInstructor(lab, lec_si));
         debug("GOT LAB INSTRUCTOR " + lab_base.getInstructor());

         TimeRange tr = getLabTimeRange(lab, lec_si);
         debug("GOT LAB TIMERANGE " + tr);

         lab_si_list = findTimes(lab_base, tr);
         lab_si_list = findLocations(lab_si_list);

         for (ScheduleItem si : pairLecWithLabs(lec_si, lab_si_list))
         {
            si_map.put(si);
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
    * Pairs a list of lab ScheduleItems with a single lecture ScheduleItem. The
    * returned list will contain ScheduleItems identical to the 'lec_si'
    * parameter, save for the fact that their lab field will be different.
    * Additionally, each lab is guaranteed to not overlap its lecture.
    * 
    * @param lec_si Lecture to pair labs with
    * @param lab_si_list List of labs to use for pairing
    * 
    * @return A list of ScheduleItems, each identical to 'lec_si' except for its
    *         lab component. Each ScheduleItem in this list will have a lab
    *         which does not conflict/overlap with its lecture
    */
   private List<ScheduleItem> pairLecWithLabs (ScheduleItem lec_si,
      List<ScheduleItem> lab_si_list)
   {
      Vector<ScheduleItem> si_list = new Vector<ScheduleItem>();

      /*
       * For every lab ScheduleItem created, pair it with the lecture
       * ScheduleItem.
       */
      for (ScheduleItem lab_si : lab_si_list)
      {
         debug("CONSDIDER LAB ADDITION " + lab_si);
         int enrollmentGoal = lec_si.getCourse().getEnrollment();
         int curEnrollment = 0;
         while (curEnrollment < enrollmentGoal)
         {
            /*
             * Only setup pair and add to possibilities if lab and lecture don't
             * overlap (which is a bad thing)
             */
            if (!lec_si.overlaps(lab_si))
            {
               ScheduleItem clone = lec_si.clone();
               lec_si.addLab(lab_si);

               si_list.add(clone);
               /*
                * TODO: Discuss w/ requirements and view guys
                */
               curEnrollment += lab_si.getCourse().getEnrollment();

               debug("SUCCESS! ENROLLMENT AT " + curEnrollment + "/"
                  + enrollmentGoal);
            }
         }
      }

      return si_list;
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
      Instructor r = null;
      int curMaxPref = 0;

      for (Instructor i : this.iSourceList)
      {
         if (i.canTeach(c))
         {
            int pref = i.getPreference(c);
            if (pref > curMaxPref)
            {
               r = i;
               curMaxPref = pref;
            }
         }
      }

      if (r == null)
      {
         r = Staff.getStaff();
      }
      return r;
   }

   /***********************
    * GETTERS & SETTERS *
    ***********************/

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
   public void setlSourceList (List<Location> lSourceList)
   {
      this.lSourceList = new Vector<Location>(lSourceList);
      this.lSourceList.add(Tba.TBA);
   }
}
