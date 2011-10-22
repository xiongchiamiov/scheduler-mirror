package edu.calpoly.csc.scheduler.model.schedule;

import edu.calpoly.csc.scheduler.model.db.*;
import edu.calpoly.csc.scheduler.model.db.idb.*;
import edu.calpoly.csc.scheduler.model.db.cdb.*;
import edu.calpoly.csc.scheduler.model.db.ldb.*;
import edu.calpoly.csc.scheduler.model.db.pdb.*;
import java.util.*;

import java.io.*;
import java.io.Serializable;

import javax.swing.JFrame;
import javax.swing.JProgressBar;

/**
 * Contains the methods for generating a schedule. See method documentation for
 * the details behind this process. 
 *
 * @author Eric Liebowitz
 */
public class Schedule extends Observable implements Serializable
{
   public static final int serialVersionUID = 42;

   /* Instance Variables ==>*/
   /**
    * What courses, times, and WTU's an instructor had been assigned
    */
   public Hashtable<Instructor, Treatment> treatment = 
      new Hashtable<Instructor, Treatment>();
   /** 
    * The generated schedule 
    */
   public Vector<ScheduleItem> s;
   /**
    * List of courses that had TBA locations.
    */
   public Vector<TBA> TBAs = new Vector<TBA>();
   /**
    * Used to keep track of when rooms are booked
    */
   private Hashtable<Location, WeekAvail> lBookings = 
      new Hashtable<Location, WeekAvail>();
   /**
    * Used to keep track of when instructors are booked
    */
   private Hashtable<Instructor, WeekAvail> iBookings = 
      new Hashtable<Instructor, WeekAvail>();
   /**
    * Used to keep track of when courses are booked
    */
   protected CourseOverlapTracker cot = new CourseOverlapTracker();
   
   /** 
    * Records how many courses are in the schedule (not number of sections)
    */
   protected Vector<Course> cList = new Vector<Course> ();
   /**
    * Records how many instructors are in the schedule
    */
   protected Vector<Instructor> iList = new Vector<Instructor>();
   /**
    * Records how many locations are in the schedule
    */
   protected Vector<Location> lList = new Vector<Location>();


   /**
    * Determines when the day begins for scheduling. Default is 7a.
    */
   private Time dayStart = new Time (7, 0);

   /**
    * Determines when the day ends for scheduling. Default is 10p.
    */
   private Time dayEnd = new Time (22, 0);
   
   /**
    * ID for the schedule in the database
    */
   private int id = -1;
   
   /**
    * Name for the schedule
    */
   private String name = "";

   /*<==*/

   /* Schedule construtors ==>*/
   /** 
    * Creates an empty schedule. 
    */
   public Schedule ()
   {
      super ();
      s = new Vector<ScheduleItem>();
   }

   /**
    * Creates a schedule from a given vector of ScheduleItem's.
    *
    * @param s Vector of ScheduleItem's to create a schedule from.
    */
   public Schedule (Collection<ScheduleItem> s)
   {
      super ();
      /*
       * I don't use super(<Collection>) b/c I need to do specific checks
       * before the add can occur. It made the most sense to put these
       * within the add method.
       */
      for (ScheduleItem si: s)
      {
         this.add(si);
      }
   }/*<==*/

   /* Main "generate" methods ==>*/
   /**
    * <pre>
    * Generates a schedule. In particular, the process adheres to the process 
    * outlined in the pseudo code below:
    *
    *    while (there are courses)
    *       get Insructor "i"
    *       choose Course "c" which "i" most wants to teach
    *       choose a time "t" when "i" most wants to teach
    *       choose a location "l" which can accomodate "c" and "t"
    *
    * This method calls the "gen" method to do most the heavy lifting for
    * generation. However, most, if not all of the exception handling is done
    * here.
    *
    * However, before generation begins, any/all "locked" ScheduleItems from the
    * previous schedule are immediately added to the new Schedule. 
    * </pre>
    *
    * @param cdb The list of courses to schedule
    * @param idb The list of instructors with which to teach the courses
    * @param ldb The list of location in which to teach the courses
    */
   /* generate ==>*/
   public void generate (Vector<Course> cdb,
                         Vector<Instructor> idb, 
                         Vector<Location> ldb)
   {
      /*
       * Reset all records (including the schedule).
       */
      reset();
      initBookings(idb, ldb);

      Course c = null;
      while (!cdb.isEmpty())
      {
         Vector<Instructor> toRemove = new Vector<Instructor>();
         /*
          * If there are no more instructors, add the almighty "STAFF" to teach
          * the rest of the courses. It is here that an "iBookings" entry will
          * be created for STAFF.
          */
         if (idb.isEmpty())
         {
//            idb.add(Instructor.STAFF);
         }
         for (Instructor i: idb)
         {
            try 
            {
               c = gen (i, cdb, idb, ldb);
            }
            /* Fatal */
            catch (EmptyCourseDatabaseException e) 
            {
               break;
            }
            /* 
             * These two exceptions represent when an Instructor can't teach 
             * anything else and should be removed. But, that can't be done
             * right in the middle of a "foreach". So, the removals are appended
             * to a queue. After the loop, the instructors are actually removed.
             */
            catch (InstructorCanTeachNothingException e) 
            {
               toRemove.add(i);
            }
            catch (InstructorWTUMaxedException e) 
            {
               toRemove.add(i);
            }
            catch (CouldNotBeScheduledException e)
            {
               e.printStackTrace();

               c = e.si.getCourse();
               this.TBAs.add(new TBA(c, i));
               decrementSectionCount(c, cdb);
            }
            catch (Exception e)
            {
               e.printStackTrace();
            }
         }
         for (Instructor i: toRemove)
         {
            idb.remove(i);
         }

      }
      this.setChanged();
      this.notifyObservers();
   }/*<==*/

   /**
    * Resets (clears) all data particular to the schedule. Note that 
    * "lockedItems" is not reset here. Since this piece of data needs the old
    * Schedule's locked items prior to generation, it will have been created
    * before this method is called (as the old schedule is wiped here). So, I
    * shouldn't go wiping it out just after I made it. 
    */
   /* reset ==>*/
   private void reset ()
   {
      this.s.clear();
      this.TBAs.clear();
      this.treatment.clear();
      this.cot.clear();
      this.iBookings.clear();
      this.lBookings.clear();
      this.cList.clear();
      this.iList.clear();
      this.lList.clear();
      
   }/*<==*/

   /**
    * Initializes the l and i bookings HashMaps to contain fresh WeekAvail
    * objects for each item in the cdb, ldb, and idb (respectively). 
    * 
    * It'll also create Treatment objects for each  Instructor while it's at 
    * it.
    * 
    * Also inits the "cOverlaps" variable, which keeps track of what classes 
    * can/can't overlap one another.
    *
    * @param idb List of instructors for generating
    * @param ldb List of locations for generating
    */
   /* initBookings ==>*/
   private void initBookings (Vector<Instructor> idb,
                              Vector<Location> ldb)
   {
      for (Location l: ldb)
      {
         this.lBookings.put(l, new WeekAvail());
      }
      for (Instructor i: idb)
      {
         this.iBookings.put(i, new WeekAvail());
         this.treatment.put(i, new Treatment());
      }
      /*
       * Adding info for STAFF here so it's available at all times, even if 
       * the STAFF isn't needed yet (when opening a schedule from a file that
       * has a  STAFF in it, for instance)
       */
//      this.iBookings.put(Instructor.STAFF, new WeekAvail());
//      this.treatment.put(Instructor.STAFF, new Treatment());
   }/*<==*/

   /**
    * <pre>
    * Does the heavy lifting for schedule generation. 
    * 
    * Given an instructor, it selects a course for him/her to teach. 
    * 
    * It subsequently gathers a list of all possible days/time combinations, 
    * in descending order of desirability. 
    * 
    * With this information, it will compile a list of all locations which can
    * be taught on any of those day/time combinations, in descending order of
    * desirability.
    * 
    * All this information will culminate in the course being scheduled. The 
    * course's section count will be decremented by one and, if it has a lab
    * which was also scheduled, the lab's will be decremented as well.
    *
    * </pre>
    *
    * @param i The given Instructor
    * @param cdb List of courses still available to teach
    * @param idb List of all Instructor still able to teach
    * @param ldb List of all Locations
    * @param pdb List of all SchedulePreferences to apply to this schedule
    *
    * @return The Course scheduled
    *
    * @throws EmptyCourseDatabaseException if the "cdb" is empty
    * @throws InstructorCanTeachNothingException if no course exists for which 
    *         the Instructor's preference is not a 0
    * @throws InstructorWTUMaxedException if no course exists which will not
    *         push an Instructor's WTU's past his/her limit
    * @throws InstructorNotInDatabaseException if, for some reason, the 
    *         Instructor given isn't in the idb
    * @throws NullInstructorException if, for some reason, the given Instructor
    *         is null
    * @throws CouldNotBeScheduledException when no DaysAndTime object can be 
    *         created for a given course, meaning it is to be TBA
    */
   /* gen ==>*/
   private Course gen (Instructor i,
                       Vector<Course> cdb, 
                       Vector<Instructor> idb, 
                       Vector<Location> ldb)
      throws EmptyCourseDatabaseException,
             InstructorCanTeachNothingException,
             InstructorWTUMaxedException,
             InstructorNotInDatabaseException,
             NullInstructorException,
             CouldNotBeScheduledException
   {
      ScheduleItem base = new ScheduleItem();
      base.setInstructor(i);

      /*
       * Build a piece of a ScheduleItem: its course (and possible lab-SI 
       * component)
       */
      base = buildSI_course (cdb, base);

      /*
       * This list will contain SI's with the same course, but with different
       * start times, end times, and days-to-be-taught. 
       */
      Vector<ScheduleItem> sis = findTime(base, base.getCourse());
      /*
       * This'll return a list similar to the one above, but with more
       * SI's. In particular, each SI will have the same course, times, and
       * days-to-be-taught (along w/ any corresponding lab). However, the
       * location of each SI (and any lab) will be different.
       * 
       * I reassign to "sis" b/c there's no need to keep the old one lyin' 
       * around.
       */
      sis = findLocations (ldb, sis);
      
      /*
       * If not even one SI could be created, the course could not be 
       * scheduled, so we should throw an exception saying so. 
       */
      if (sis.size() == 0)
      {
         throw new CouldNotBeScheduledException(base);
      }

      /*
       * Add it
       */
      try
      {
         addBest(sis);
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }

      /*
       * Decrement course section counts, regardless of whether it was in a 
       * locked SI or not. See the documentation for this method...it's helpful
       */
      decrementSectionCount(base.getCourse(), cdb);

      return base.getCourse();
   }/*<==*/

   /**
    * Decrements a given Course's section count along w/ its lab's section
    * count, if it has one. If the lecture's section count falls to 0, it is 
    * removed from the "cdb". 
    *
    * Note the the provided Course "c" will not have its section count
    * decremented directly. Rather, "c" is used to look itself up in "cdb", and
    * the resulting object is decrememnted. This is done b/c, due to the 
    * possibility of a locked Course, the provided Course might already be part
    * of a locked SI. Since we don't want to alter the SI in any way, we've got
    * to be sure we only alter the section count of the Course in the CDB, which
    * does not yet have a home. 
    *
    * @param c Course to decrement section count
    * @param cdb List of courses for generation
    */
   /* decrementSectionCount ==>*/
   private void decrementSectionCount (Course c, Vector<Course> cdb)
   {
      Course realCourse = cdb.get(cdb.indexOf(c));
      ////System.err.println ("INDEX: " + cdb.indexOf(c));
      Course realLab = realCourse.getLab();    //Can be null

      realCourse.setNumOfSections(realCourse.getNumOfSections() - 1);
      if (realLab != null)
      {
         realLab.setNumOfSections(realLab.getNumOfSections() - 1);
      }
      if (realCourse.getNumOfSections() < 1)
      {
         cdb.remove(realCourse);
      }
   }/*<==*/
   /*<==*/ 

   /* For selecting a course ==>*/
   /**
    * Finds the most suitable course for a given instructor to teach. In
    * particular, the selected course will be whatever course for which the
    * given instructor had the highest preference to teach. If he/she has the
    * same highest preference for two courses, the first course encountered
    * will be the one selected. 
    *
    * @param cdb Database of courses
    * @param si ScheduleItem containing the instructor to give the course to
    *
    * @return A ScheduleItem with its course field set to the most desirable 
    *         course available to give this Instructor. If the course has a lab,
    *         the returned SI will have its "lab" field filled appropriately.
    *
    * @throws InstructorWTUMaxedException if no course exists which will not
    *         push an Instructor's WTU's past his/her limit
    * @throws InstructorCanTeachNothingException if no course exists for which 
    *         the Instructor's preference is not a 0
    */
   /* buildSI_course ==>*/
   private ScheduleItem buildSI_course (Vector<Course> cdb,
                                        ScheduleItem si)
      throws
         EmptyCourseDatabaseException,
         NullInstructorException,
         InstructorNotInDatabaseException,
         InstructorWTUMaxedException,
         InstructorCanTeachNothingException//
   {
      Instructor i = si.getInstructor();

      /* The best course found */
      Course bestC = null;

      /* Instructor's current WTU count */
      int iWTU = this.treatment.get(i).wtu, bestPref = 0;

      /* 
       * Whether an instructor has the WTU's or preference for at least one 
       * course.
       */
      boolean canWTU = false, canPref = false;

      /*
       * Exhaustively find the best course.
       */
      for (Course temp: cdb)
      {
         int pref = i.getPreference(temp);

         /*
          * Don't consider courses w/ a "0" preference
          */
         if (pref == 0) 
         {
            continue;
         }
         /*
          * If prof wants this course more than previous "best".
          */
         if (pref > bestPref)
         {
            canPref = true;
            /*
             * If STAFF, or he/she has the WTU's to teach it.
             */
            if ((i.getMaxWTU() < 0) || (temp.getWtu() + iWTU <= i.getMaxWTU()))
            {
               canWTU = true;
               bestC = temp;
               bestPref = pref;
            }
         }
      }
      /*
       * If no course existed which professor was able to teach.
       */
      if (!canPref) { throw new InstructorCanTeachNothingException (); }
      /*
       * If instructor had no WTU's to spare for any course he could still 
       * teach.
       */
      if (!canWTU)  { throw new InstructorWTUMaxedException (); }

      si.setCourse(new Course(bestC));
      /*
       * Make the lab SI if needed 
       */
      if (bestC.hasLab())
      {
         ScheduleItem lab = new ScheduleItem();
         lab.setCourse(new Course(bestC.getLab()));
         lab.setInstructor(i);
         si.setLab(lab);
         
      }

      return si;
   }/*<==*/

   /*<==*/

   /* For finding times ==>*/
   /**
    * Compiles a list of all day/time combinations a given instructor can teach
    * a given course. These combinations are stored in individual ScheduleItem
    * objects. Each object returned will contain valid start time, end time, 
    * and days-to-be-taught values. (Also, if they've a lab component, their
    * SI's lab field will contain a similarly structured SI object).
    *
    * Takes into consideration both DaysForClasses preferences and 
    * NoClassOverlap preferences. 
    *
    * @param i The Instructor
    * @param lec The lec
    * @param lab The lec's lab (can be null if there isn't one)
    *
    * @return A list of ScheduleItems, each with a different start time, end
    *         time, or days-to-be-taught (or a combination of all three), but 
    *         with the same course "lec". If "lec" had a lab ("lab"), each 
    *         returned SI will have its "lab" field filled with an SI with its
    *         own start time, end time, and days-to-be-taught.
    */
   /* findTime ==>*/
   /* TODO: Adjust to take a single SI, with courses already set */
   private Vector<ScheduleItem> findTime (ScheduleItem base,
                                          Course lec)
      throws CouldNotBeScheduledException
   {
      Vector<ScheduleItem> sis = new Vector<ScheduleItem>();

      /*
       * Until we've made at least one SI for the lecture, we must consider 
       * this generation "not alright". If it stays that way, we'll throw a
       * CouldNotBeScheduledException to notify the caller
       */
      for (ScheduleItem lec_si: buildSI_setDats(base, lec))
      {
         /*
          * We'll have to stop and consider the lab as well, if there is one
          */
         if (base.hasLab())
         {
            ScheduleItem base_lab = base.getLab();
            Course lab = base_lab.getCourse();
            /*
             * Though finding a lecture made generation "alright", we're now 
             * comitted to finding a time for its lab as well. If we can't
             * find at least one DAT for the lab, we'll have to throw a 
             * CouldNotBeScheduledException for the lab portion, as a lecture
             * can't be taught w/o its lab
             */
            for (ScheduleItem lab_si: buildSI_setDats(base_lab, lab))
            {
               /*
                * The lecture and lab cannot overlap each other
                */
               if (!lec_si.overlaps(lab_si))
               {
                  /*
                   * More cloning! The "lec_si" is the base upon which this lab
                   * will be added, but the reference to said base must be 
                   * different, else we won't get unique lec/lab combinations
                   */
                  ScheduleItem toAdd = lec_si.clone();
                  toAdd.setLab(lab_si);

                  /*
                   * I'm aware I call "sis.add" in two different contexts. 
                   * I must call it -here- b/c I now know that both the lecture
                   * and lab have good times. I call it in the "else" block
                   * below b/c if there's no lab, we can just add the lecture
                   * right away.
                   */
                  sis.add(toAdd);
               }
            }
         }
         /*
          * Otherwise, if there was no lab, we can add the lecture to our
          * return right away
          */
         else
         {
            sis.add(lec_si);
         }
      }

      return sis;
   }/*<==*/
   
   /**
    * Builds the days and time portion of a ScheduleItem, using a provided base
    * to supply each created object w/ any other fields which need not be 
    * considered, but which should be copied over. Thus, each ScheduleItem 
    * returned from this method will have the same course "c" applied to it, but
    * different DATs.
    *
    * @param base The "base". This base will be used to create a list of clones,
    *             each of which will be given a different DAT.
    * @param c The course to create DATS for
    *
    * @return A list of ScheduleItems, each of which has same fields as that
    *         of the "base", except for "days", "start", and "end", which will
    *         have been set according to the unique DATs generated in this 
    *         method.
    */
   /* buildSI_setDats ==>*/
   public Vector<ScheduleItem> buildSI_setDats (ScheduleItem base, Course c)
   {
      Vector<ScheduleItem> sis = new Vector<ScheduleItem>();

      /*
       * A return < 1 means that time didn't divide well into the number of
       * days provided.
       */
      int halfHours = c.getDayLength();
      if (halfHours > 0)
      {
         for (DaysAndTime dat: createDatsForDays(base.i, 
                                                 c,
                                                 c.getDays().size(), 
                                                 halfHours))
         {
            /*
             * Clone our "base" SI and set its days and time. Note that
             * "null" DAT's should not be considered.
             */
            if (dat != null)
            {
               ScheduleItem si = base.clone();
               si.setDaysAndTime(dat);
               sis.add(si);
            }
         }
      }


      return sis;
   }/*<==*/

   /**
    * Creates a list of lists of possible DaysAndTime objects. w
    *
    * A course's "DaysForClasses" preferences are taken into consideration here.
    *
    * @param i The instructor who to be scheduled for
    * @param c The course to schedule
    * @param nDays How many days to schedule for
    * @param nHalfHours the number of half hours to be taught on each day
    * 
    * @return A vector of DATs, each of which is a viable DAT for the given 
    *         Course/Instructor. It is guaranteed that none of these DAT's will
    *         represent a time which the instructor is incapable of teaching.
    */
   /* createDatsForDays ==>*/
   private Vector<DaysAndTime> createDatsForDays 
      (
         Instructor i,
         Course c,
         int nDays,
         int nHalfHours
      )
   {
      Vector<DaysAndTime> dats = new Vector<DaysAndTime>();

      /*
       * Create DaysAndTime objects for when this course could be taught across
       * the Weeks computed above. 
       */
      Vector<Week> allWeeks = getAllDayCombos(c, nDays);
      for (Week w: allWeeks)
      {
         try
         {
            for (Time t = new Time(0, 0); t.addHalf();)
            {
               /*
                * Need to make a new Time w/ "t's" fields, so that we don't 
                * suffer from the subtle nuances of passing the same object
                * reference around
                */
               DaysAndTime toAdd = makeDAT (c, new Time(t), nHalfHours, w, i);
               /*
                * Only pass "toAdd" on if it's valid and if it 
                * represents a Time the Instructor is remotely willing to teach
                * (doesn't have 0 specified for the days/time range). There's 
                * no points in carrying this information and spawning multitudes
                * of other SI's if there times are just going to be rejected
                * later.
                */
               if (toAdd != null)
               {
                  dats.add(toAdd);
               }
            }
         }
         catch (Exception e) { e.printStackTrace(); }
      }
      return dats;
   }/*<==*/

   /**
    * Given a number of days, returns a list of all weeks containing as many
    * permutations of that numbers of days as are possible. Uses the course's 
    * DFC(s) to define the form of the week, though not necessarily the number
    * of days.
    *
    * @.todo This does nothing right now
    *
    * @param c Course with the course preferences to use
    * @param numOfDays Number of days each Week should have
    * 
    * @return a Vector of Week objects, each with "numOfDays" days. 
    */
   /* getAllDayCombos ==>*/
   private Vector<Week> getAllDayCombos (Course c, int numOfDays)
   {
      Vector<Week> allWeeks = new Vector<Week>();
      allWeeks.add(c.getDays());

      return allWeeks;
   }/*<==*/

   /**
    * Chooses a number of days from a given Week of available days.
    * 
    * @param depth Number of days to choose
    * @param availableDays Days available to choose
    * @param alreadyAssigned Days already in the Week to return
    *
    * @return A Week consisting of the days chosen from "availableDays". There 
    *         will be exactly "depth" days in this Week.
    *
    * @throws NotEnoughDaysException if/when depth > availableDays.size()
    */
   /* chooseWhichDays ==>*/
   private Week chooseWhichDays (int depth,
                                 Week availableDays,
                                 Week alreadyAssigned)
      throws NotEnoughDaysException
   {
      if (depth > availableDays.getDays().size())
      {
         throw new NotEnoughDaysException();
      }
      if (depth == 0)
      {
         return alreadyAssigned;
      }
      else
      {
         for (Day d: availableDays.getDays())
         {
            if (!alreadyAssigned.contains(d))
            {
               alreadyAssigned.add(d);
               break;
            }
         }
      }
      return chooseWhichDays (depth - 1, availableDays, alreadyAssigned);
   }/*<==*/

   /**
    * Creates a DaysAndTime object for a given time range across a given Week
    * for a given instructor. If the instructor is not available for this
    * time, null is returned. 
    *
    * @param c The course to be scheduling
    * @param s The start time
    * @param halfHours The number of half hours to go from "s"
    * @param days The Week these times will be applied to
    * @param i The instructor who needs to be available for this time span
    *
    * @return A DaysAndTime representing a time starting at "s", ending
    *         (halfHours * 30) minutes afterwards across "days". If "i" is not
    *         available for this time frame or cannot teach for the given time
    *         frame, null is returned. 
    */
   /* makeDAT ==>*/
   private DaysAndTime makeDAT (Course c, Time s, int halfHours, Week days, 
                                  Instructor i)
   {
      DaysAndTime r = null;
      /*
       * Since we know how long the course will go, computing the end-time 
       * relative to the start time is easy...just add up all the half-hours.
       */
      Time e = new Time (s);
      e.addHalves(halfHours);
      /*
       * If the professor is free for the specified time and across the 
       * specified days, it's a valid DAT. Otherwise, return null and deal
       * w/ it from the caller's persepctive.
       */
      try
      {
         if (this.iBookings.get(i).isFree(s, e, days))
         {
            if (((int)i.getAvgPrefForTimeRange(days, s, e)) > 0)
            {
               /*
                * TODO: ENABLE ONCE PREFERENCES ARE WORKING AGAIN
                */
//               if (doesNotOverlap(c, s, e, days))
//               {
                  r = new DaysAndTime (days, s, e);
//               }
            }
         }
      }
      /*
       * If there's a bad time/day, return null anyhow 
       */
      catch (NotADayException exc)
      {
         exc.printStackTrace();
      }
      catch (EndBeforeStartException exc)
      {
         exc.printStackTrace();
      }

      return r;
   }/*<==*/
   
   /**
    * Determines whether a given course's teaching time is to overlap that
    * of another, already-scheduled course with which it is forbidden to 
    * overlap. If a given course isn't forbidden to overlap anything else, 
    * true is returned w/o question. 
    *
    * @param c The course in question
    * @param s The time the course is to start
    * @param e The time the course is to end
    * @param days The days over which the course is to be scheduled
    *
    * @return true if the Course doesn't overlap anything it should. False 
    *         otherwise. 
    */
   private boolean doesNotOverlap (Course c, Time s, Time e, Week days)/*==>*/
   {
      return this.cot.isFree(c, s, e, days);
   }/*<==*/
   /*<==*/

   /* For finding locations ==>*/
   /**
    * Finds every possible, compatible location for every ScheduleItem in "sis".
    * For every SI in the list, it'll clone it as many times as it finds 
    * different locations which provide for its needs and are available for its
    * days/times. 
    *
    * This method was written to find locations for SI's which shared all the
    * same fields. While you could pass in <i>any</i> list of SI's, I use it
    * by passing in a list of SI's which all have the same instructor and 
    * course, but different days and times. The list which comes out of
    * this method will have even <b>more</b> SI's. Those with the same 
    * instructor, days, and times will have different locations.
    *
    * @param ldb List of locations to try
    * @param sis List of all the ScheduleItems to find locations for. 
    * 
    * @return A list of ScheduleItems, each with a start, end, days
    *         taught, and <b>location</b>. While many will have identical fields
    *         for the first 3, their location will be what distinguishes them.
    */
   /* findLocation ==>*/
   private Vector<ScheduleItem> findLocations (Vector<Location> ldb,
                                               Vector<ScheduleItem> sis)
      throws CouldNotBeScheduledException
   {
      Vector<ScheduleItem> r = new Vector<ScheduleItem>();

      //for (ScheduleItem si: sis)
      //{
         //System.err.println ("BEFORE RUN: " + si);
      //}

      /*
       * For every SI (each of which is for the same instructor, course, time,
       * days, etc.), create more SI's, each of which has a different location
       * to be taught in
       */
      for (ScheduleItem si: sis)
      {
         //System.err.println ("BEFORE BUILD: " + si);
         ////System.err.println ("Finding locations for " + si.getCourse());
         /*
          * Build a list of ScheduleItems for the lecture
          */
         for (ScheduleItem lec_si: buildSIList_locs(si, ldb))
         {
            //System.err.println ("BUILT LEC: " + lec_si);
            ////System.err.println ("SI w/ location: " + lec_si.getLocation());
            /*
             * If there's a lab, go make new SI's for it as well, and clone 
             * the lec_si for each new lab SI built
             */
            if (lec_si.hasLab())
            {
               ScheduleItem base_lab = lec_si.getLab();
               /*
                * The lec_si can't be cleared for adding until we know at least
                * one location can be found for its corresponding lab
                */
               //System.err.println ("BASE LAB: " + base_lab);
               for (ScheduleItem lab_si: buildSIList_locs(base_lab, ldb))
               {
                  /*
                   * More cloning! The "lec_si" is the base upon which all found
                   * labs must be attached, but the reference to said base must
                   * be different, else we won't get unique lec/lab combinations.
                   */
                  ScheduleItem toAdd = lec_si.clone();
                  toAdd.setLab(lab_si);

                  r.add(toAdd);
               }
            }
            /*
             * If there was no lab, we can add the lec_si right away
             */
            else
            {
               r.add(lec_si);
            }
         }
      }

      return r;
   }/*<==*/

   /**
    *
    * Finds all possible locations which are compatible with a given, 
    * partially-built ScheduleItem. Partially built = the SI has at least its 
    * "start", "end", and "days" fields initialized to something valid. 
    * Compatible = the location is free for the SI's days/times, and it is 
    * compatible with the SI's equipment needs (which are really its course's
    * equipment needs).
    *
    * @param base The ScheduleItem to use to get information about what time the
    *             selected locations should be available. Thus, when this method
    *             is called, this object must have at least its "start", "end", 
    *             and "days" fields initialized. 
    * @param ldb List of locations to try
    *
    * @return A list of ScheduleItems, each with a different location to be 
    *         taught in. NOTE: Currently, this list will only contain a single
    *         worthy location. There is no current need to gather all available
    *         locations, and doing so slows down generation exponentially as 
    *         more locations are incorporated. So once if find a good spot, 
    *         it'll break out and return. 
    */
   /* buildSIList_locs ==>*/
   private Vector<ScheduleItem> buildSIList_locs (ScheduleItem base,
                                                  Vector<Location> ldb)
   {
      Vector<ScheduleItem> r = new Vector<ScheduleItem>();

      /*
       * Consider every location for "base"
       */
      for (Location l: ldb)
      {
         /*
          * If it's good, we can add it the list of SI's w/ good locations that 
          * we return
          */
         if (haveGoodLocForSI(l, base))
         {
            ScheduleItem si = base.clone();
            si.location = l;
            
            r.add(si);
            break;
         }
      }
      return r;
   }/*<==*/

   private boolean haveGoodLocForSI (Location l, ScheduleItem si)
   {
      return (l.providesFor(si.c) && lBookings.get(l).isFree(si.start,
                                                             si.end,
                                                             si.days));
   }
  /*<==*/ 

   /* For adding to the schedule ==>*/
   /**
    * Adds the "best" ScheduleItem of a given list to the Schedule, along with
    * its corresponding lab when applicable. "Best" is determined by the "value"
    * field associated w/ SI's. The one with the highest value will be chosen.
    *
    * @param sis The list of SI's to choose from
    *
    * @see edu.calpoly.csc.scheduler.model.schedule.Schedule#chooseBest
    */
   private void addBest (Vector<ScheduleItem> sis)
   {
      ScheduleItem toAdd = chooseBest(sis);
      this.add(toAdd);
      if (toAdd.hasLab())
      {
         this.add(toAdd.getLab());
      }
   }
   
   /** 
    * Sorts a list of ScheduleItems according to their "value" and returns the 
    * highest value. 
    * 
    * @param sis The list of SI's to choose from
    * 
    * @return The SI w/ the highest value
    *
    * @see edu.calpoly.csc.scheduler.model.schedule.SiMap
    */
   private ScheduleItem chooseBest (Vector<ScheduleItem> sis)
   {
      return new SiMap(sis).lastKey();
   }

   /**
    * Extends "add", so as to update schedule-related information as items are
    * scheduled. Also enforced "no double booking" rules.
    *
    * @param si ScheduleItem to add
    *
    * @return true if the ScheduleItem was successfully added. False if it 
    *         violated any rules (double booking, etc.)
    */
   /* add ==>*/
   public boolean add (ScheduleItem si)
   {
      /*
       * If Location was undetermined, don't add it to the schedule...add it to
       * a special list
       */
      if (si.location == Location.TBA)
      {
         this.TBAs.add(new TBA(si.c, si.i));
         return false;
      }
      else
      {
         if (!verifyNoBadBookings (si))
         {
            return false;
         }
         /*
          * Update book-keeping records (i, c, and l lists, along with 
          * Instructor treatment)
          */
         updateRecords (si);
   
         /*
          * Book the course, instructor, location for this time
          */
//         this.cot.book(si.c, si.start, si.end, si.days);
         book(this.lBookings.get(si.location), si.days, si.start, si.end);
         book(this.iBookings.get(si.i), si.days, si.start, si.end);

         return this.s.add(si);
      }
   }/*<==*/
   
   /**
    * Checks to ensure that no instructors, locations, or courses were booked
    * innappropriately (double-booked, overlapped, etc.).
    *
    * @param si The ScheduleItem to verify
    * 
    * @return true if no rules are violated. False otherwise. 
    */
   /* verifyNoBadBookings ==>*/
   private boolean verifyNoBadBookings (ScheduleItem si)
   {
      boolean passed = true;

      /*
       * Check for double-booked location and instructor.
       */
      if (!check(this.lBookings.get(si.location), si.days, si.start, si.end))
      {
         ////System.err.println ("Double booked location " + si.l + " from " + 
               //si.start + " to " + si.end + " on " + si.days +
               //"with instructor " + si.i + " and course " + si.c);
         passed = false;
      }
      if (!check(this.iBookings.get(si.i), si.days, si.start, si.end))
      {
         ////System.err.println ("Double booked instructor " + si.i + " from " + 
               //si.start + " to " + si.end + " on " + si.days + " with course " +
               //si.c);
         passed = false;
      }

      return passed;
   }/*<==*/

   /**
    * Checks whether a given hash of availability has a given span of free time.
    *
    * @param a Hash of availability
    * @param days Days to consider for span of availability
    * @param start Beginning of the given span of time
    * @param end End of the given span of time
    * @return true if the time span from "start" to "end" is free for all "days"
    */
   /* check ==>*/
   private static boolean check (WeekAvail a,
                                 Week days,
                                 Time start,
                                 Time end)
   {
      boolean passed = true;
      try
      {
         passed = a.isFree(start, end, days);
      }
      catch (NotADayException e)
      {
         passed = false;
      }
      catch (EndBeforeStartException e) 
      { 
         passed = false; 
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
      return passed;
   }/*<==*/

   /**
    * Books a span of time over a set of days in a hash (indexed by numerical
    * days) of availabilities.
    *
    * @param a Hash of availabilities
    * @param days Set of days
    * @param start Beginning of span of time
    * @param end End of span of time
    */
   /* book ==>*/
   private static void book (WeekAvail a, //
                             Week days,
                             Time start,
                             Time end)
   {
      try
      {
         a.book(start, end, days);
      }
      catch (NotADayException e)
      {
         ////System.err.println ("Error adding on " + days + " from " + start + 
            //" to " + end);
      }
      catch (EndBeforeStartException e)
      {
         ////System.err.println ("Error adding on " + days + " from " + start + 
            //" to " + end);
      }
   }/*<==*/
   
   /**
    * Updates the c, i, and l lists for a given schedule item, along with the
    * instructor's "treatment".
    *
    * @param si The ScheduleItem containing all the information needed
    */
   /* updateRecords ==>*/
   private void updateRecords (ScheduleItem si)
   {
      /*
       * Add course to the course list, if it isn't already there
       */
      if (!this.cList.contains(si.c))
      {
         this.cList.add(si.c);
      }
      /*
       * Add instructor the instructor list, if he/she isn't already there
       */
      if (!this.iList.contains(si.i))
      {
         this.iList.add(si.i);
      }
      /*
       * Add location to the location list, if it isn't already there
       */
      if (!this.lList.contains(si.location))
      {
         this.lList.add(si.location);
      }
      
      /*
       * Update "treatment" according to what's in the ScheduleItem
       */
      Treatment t = this.treatment.get(si.i);
      t.courses.add(si.c);
      t.wtu += si.c.getWtu();
   }/*<==*/
   /*<==*/ 

   /* toString ==>*/
   /**
    * Returns a String of the schedule. In reality, this just invokes the 
    * "toString" for every ScheduleItem contained in the schedule
    */
   public String toString ()
   {
      String r = "";
      for (ScheduleItem si: this.s)
      {
         r += si;
      }
      return r;
   }/*<==*/

   /* Simple Getters & Setters ==>*/
   /**
    * Returns the list of courses.
    *
    * @return the list of courses
    */
   public LinkedList<Course> getCourseList ()
   {
      return new LinkedList<Course>(this.cList);
   }

   /**
    * Returns the list of instructors.
    *
    * @return the list of instructors
    */
   public LinkedList<Instructor> getInstructorList ()
   {
      return new LinkedList<Instructor>(this.iList);
   }

   /**
    * Returns the list of locations.
    *
    * @return the list of locations
    */
   public LinkedList<Location> getLocationList ()
   {
      return new LinkedList<Location>(this.lList);
   }

   public Vector<TBA> getTBAs ()
   {
      return this.TBAs;
   }

   public Vector<ScheduleItem> getScheduleItems ()
   {
      return this.s;
   }

   /**
    * Returns when the day starts for scheduling
    *
    * @return when the day starts for scheduling
    */
   public Time getDayStart ()
   {
      return this.dayStart;
   }

   /**
    * Sets when the day starts for scheduling
    *
    * @param s When the day will start for scheduling
    */
   public void setDayStart (Time s)
   {
      this.dayStart = s;
   }

   /**
    * Returns when the day ends for scheduling
    *
    * @return when the day ends for scheduling
    */
   public Time getDayEnd ()
   {
      return this.dayEnd;
   }

   /**
    * Sets when the day ends for scheduling
    *
    * @param e when the day ends for scheduling
    */
   public void setDayEnd (Time e)
   {
      this.dayEnd = e;
   }
   /*<==*/

   public int getId() {
	return id;
}

public void setId(int id) {
	this.id = id;
}

public String getName() {
	return name;
}

public void setName(String name) {
	this.name = name;
}

/**
    * Replaces this schedule with a given one, while preserving this one's 
    * reference. All data (lists, TBAs, booking information, treatment, and the
    * actual schedule) is passed over via shallow copies. 
    *
    * @param s The schedule to replace this one with
    */
   /* replaceWithThisFromFile ==>*/
   public void replaceWithThisFromFile (Schedule s)
   {
      this.reset();
      this.initBookings(s.iList, s.lList);
      this.cot = s.cot;
      this.TBAs = s.TBAs;
      this.treatment = s.treatment;
      /*
       * The "add" method takes care of booking info, list info, and 
       * reconstructs the schedule
       */
      for (ScheduleItem si: s.s)
      {
         this.add(si);
      }
      ////System.err.println ("Done recreating Schedule");
      this.setChanged();
      this.notifyObservers();
   }/*<==*/

   public void dumpAsPerlText (PrintStream ps)
   {
      ps.println("--SCHEDULE BEGIN--");
      
      for (ScheduleItem si: this.s)
      {
         si.dumpAsPerlText(ps);
         ps.println("===");
      }
      ps.println("--SCHEDULE END--");
   }
}

/* Keep this here
         
         -Eric */
/* For Jason */
