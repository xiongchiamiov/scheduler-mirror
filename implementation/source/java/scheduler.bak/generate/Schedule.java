package scheduler.generate;

import scheduler.db.*;
import scheduler.db.Time.InvalidInputException;
import scheduler.db.instructordb.*;
import scheduler.db.coursedb.*;
import scheduler.db.locationdb.*;
import scheduler.db.preferencesdb.*;
import scheduler.menu.schedule.allInOne.Progress;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.LinkedHashMap;
import java.util.Observable;
import java.util.TreeMap;
import java.util.Vector;

import java.io.PrintStream;
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
    * Holds a list of ScheduleItems which were "locked" and must be 
    * incoporated, as-is, into the Schedule. 
    */
   private HashMap<Course, ScheduleItem> lockedItems = 
      new HashMap<Course, ScheduleItem>();

   /**
    * Determines when the day begins for scheduling. Default is 7a.
    */
   private Time dayStart = new Time (7, 0);

   /**
    * Determines when the day ends for scheduling. Default is 10p.
    */
   private Time dayEnd = new Time (22, 0);

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
    * @param pdb The list of schedule preferences to be incorporated in the 
    *            schedule
    * @param p SwingWorker used to display scheduling progress to the user. If
    *          null, no progress bar will be created
    */
   /* generate ==>*/
   public void generate (Vector<Course> cdb,
                         Vector<Instructor> idb, 
                         Vector<Location> ldb,
                         Vector<SchedulePreference> pdb,
                         Progress p)
   {
      int count = 0;
      int total = 0;
      for (Course c: cdb)
      {
         total += c.getSection();
      }

      /*
       * Get all the locked ScheduleItems and reset all records (including the 
       * schedule).
       */
      compileLockedItems();
      reset();
      initBookings(idb, ldb, pdb);
      /*
       * With the new Schedule and all its data empty, pre-book all the locked
       * ScheduleItems
       */
      initSchedule();
      System.err.println ("HERE");
      Course c = null;
      while (!cdb.isEmpty())
      {
         System.err.println ("CDB not empty");
         Vector<Instructor> toRemove = new Vector<Instructor>();
         /*
          * If there are no more instructors, add the almighty "STAFF" to teach
          * the rest of the courses. It is here that an "iBookings" entry will
          * be created for STAFF.
          */
         if (idb.isEmpty())
         {
            idb.add(Instructor.STAFF);
         }
         for (Instructor i: idb)
         {
            try 
            {
               /*
                * Gen and update progress
                */
               System.err.println (i);
               c = gen (i, cdb, idb, ldb, pdb);


            }
            /* Fatal */
            catch (EmptyCourseDatabaseException e) 
            {
               System.err.println ("Empty CDB"); 
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
               System.err.println ("Have no more WTU's");
               toRemove.add(i);
            }
            catch (NullInstructorException e) 
            {
               e.printStackTrace();
            }
            catch (InstructorNotInDatabaseException e)
            {
               e.printStackTrace();
            }
            catch (CouldNotBeScheduledException e)
            {
               System.err.println ("TBA up top for " + e.c + ". Dec'ing sec. count");
               e.printStackTrace();
               this.TBAs.add(new TBA(e.c, i));
               decrementSectionCount(e.c, cdb);
            }
            catch (Exception e)
            {
               e.printStackTrace();
            }
            finally
            {
               if (p != null)
               {
                  p.info.setText("Course: " + c.toString() + " | Section: " + 
                     c.getSection());

                  p.completeOneTask();
               }
            }
         }
         //Remove instructors who should no longer be considered
         for (Instructor i: toRemove)
         {
            idb.remove(i);
         }
         System.err.println ("Instructors left: " + idb);

      }
      System.err.println ("NOTIFYING");
      this.setChanged();
      System.err.println ("SET");
      this.notifyObservers();
      System.err.println ("Looks like we're done");
   }/*<==*/
   
   /**
    * Goes through the current schedule and puts all the "locked" ScheduleItems
    * into "lockedItems", keyed by the courses they contain. The previous list 
    * of lockedItems is reset here.
    */
   private void compileLockedItems ()/*==>*/
   {
      this.lockedItems.clear();
      for (ScheduleItem si: this.s)
      {
         if (si.locked)
         {
            System.err.println ("Key: " + si.c + " section " + si.c.getSection());
            System.err.println ("Putting: " + si);
            this.lockedItems.put(si.c, si);
         }
      }
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
    * @param pdb List of SchedulePreferences to apply to the schedule 
    *            (currently, should only be NCO's)
    */
   /* initBookings ==>*/
   private void initBookings (Vector<Instructor> idb,
                              Vector<Location> ldb,
                              Vector<SchedulePreference> pdb)
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
      this.iBookings.put(Instructor.STAFF, new WeekAvail());
      this.treatment.put(Instructor.STAFF, new Treatment());

      this.cot = new CourseOverlapTracker (pdb);
   }/*<==*/

   /**
    * Fills the schedule with whatever ScheduleItems are currently values in the
    * "lockedItems" LinkedHashMap
    */
   private void initSchedule ()/*==>*/
   {
      for (ScheduleItem si: this.lockedItems.values())
      {
         try
         {
            add(si);
         }
         catch (Exception e)
         {
            e.printStackTrace();
         }
      }
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
                       Vector<Location> ldb,
                       Vector<SchedulePreference> pdb)
      throws EmptyCourseDatabaseException,
             InstructorCanTeachNothingException,
             InstructorWTUMaxedException,
             InstructorNotInDatabaseException,
             NullInstructorException,
             CouldNotBeScheduledException
   {
      /*
       * Get the pieces of a ScheduleItem: a course, all possible day-time
       * combintations, and a location which works for a particular day-time
       * combination
       */
      Course c = findCourse (cdb, idb, i);
      Course lab = c.getLabPairing();     // Could be null
      /*
       * If this course is already booked due to a locked ScheduleItem, return
       * immediately. The Scheduler will keep on cranking, thinking the course
       * to was scheduled. Technically, it has been, but was done so
       * in the previous generation for the old schedule.
       */
      if (!alreadyLocked(c))
      {
         System.err.println ("Not locked!");
         System.err.println ("Got course: " + c + " w/ section: " + c.getSection());
         Vector<Vector<DaysAndTime>> dats = lec_findTime(i, c); 
         System.err.println ("Got some dats");
         Vector<ScheduledLocation> l = findLocation(ldb, dats, c);
         System.err.println ("Got some locations");

         /*
          * Add it
          */
         try
         {
            this.add(createSI(i, c, l.firstElement()));
            if (lab != null)
            {
               /*
                * By convention, the lab's location is the second ("last") element in
                * the "l" vector
                */
               this.add(createSI (i, lab, l.lastElement()));
            }
         }
         catch (Exception e)
         {
            e.printStackTrace();
         }
   
         /* Debugging */
         System.err.println ("Done adding " + c);
         System.err.println ("HAS:");
         for (Course temp: this.treatment.get(i).courses)
         {
            System.err.println (temp + " " + temp.getSection() + " " + 
                  temp.getCourseType());
         }
         System.err.println ("");
         System.err.println("\n======================================\n");
      }

      /*
       * Decrement course section counts, regardless of whether it was in a 
       * locked SI or not. See the documentation for this method...it's helpful
       */
      decrementSectionCount(c, cdb);

      return c;
   }/*<==*/

   /**
    * Used to find out whether a locked ScheduleItem contains a given course. 
    *
    * @param c Course whose "locked-ness" is to be checked for
    *
    * @param true if one of the locked ScheduleItems contains "c"
    */
   private boolean alreadyLocked (Course c)/*==>*/
   {
      boolean r = false;
      /*
       * HACK (sort of): Course's section numbers are not considered for
       * equality, but they are necessary for deciding whether a course has
       * already been scheduled b/c it was locked. So, a simple call to 
       * "lockedItems.containsKey" won't work, b/c it will return true 
       * irrespective of a course's section count. Thus, sadly, I've to 
       * iterate through all the lockedItems keys and look for a match.
       */
      for (Course locked: this.lockedItems.keySet())
      {
         if (c.equals(locked) && c.getSection() == locked.getSection())
         {
            r = true;
         }
      }
      return r;
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
      Course realLab = realCourse.getLabPairing();    //Can be null

      realCourse.setSection(realCourse.getSection() - 1);
      if (realLab != null)
      {
         realLab.setSection(realLab.getSection() - 1);
      }
      if (realCourse.getSection() < 1)
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
    * @param idb Database of instructors
    * @param i The instructor to select for
    *
    * @return The most ideal course to give this instructor. If this instructor
    *         cannot be assigned any more classes (i.e. maxed wtu's; no classes)
    *         available), null.
    *
    * @throws EmptyCourseDatabaseException if "cdb" is empty
    * @throws NullInstructorException if "i" is null
    * @throws InstructorNotInDatabaseException if, for some reason, "i" isn't in
    *         the "idb"
    * @throws InstructorWTUMaxedException if no course exists which will not
    *         push an Instructor's WTU's past his/her limit
    * @throws InstructorCanTeachNothingException if no course exists for which 
    *         the Instructor's preference is not a 0
    */
   /* findCourse ==>*/
   private Course findCourse (Vector<Course> cdb,
                              Vector<Instructor> idb, 
                              Instructor i) 
      throws
         EmptyCourseDatabaseException,
         NullInstructorException,
         InstructorNotInDatabaseException,
         InstructorWTUMaxedException,
         InstructorCanTeachNothingException//
   {
      findCourseCheckPre (cdb, idb, i);

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
      //System.err.println ("HERE, w/ cdb = " + cdb);
      for (Course temp: cdb)
      {
         int pref = i.getPreference(temp);
         System.err.println ("Has pref " + pref + " for " + temp);
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
            if ((i.getMaxWTU() < 0) || (temp.getWTUs() + iWTU <= i.getMaxWTU()))
            {
               canWTU = true;
               bestC = temp;
               bestPref = pref;
            }
         }
         /*
          * Can't get any better than this.
          */
         if (bestPref == 10) { break; }
         System.err.println ("CanPref: " + canPref);
         System.err.println ("CanWTU: "  + canWTU);
      }
      System.err.println ("Looped through all courses");
      /*
       * If no course existed which professor was able to teach.
       */
      if (!canPref) { throw new InstructorCanTeachNothingException (); }
      /*
       * If instructor had no WTU's to spare for any course he could still 
       * teach.
       */
      if (!canWTU)  { throw new InstructorWTUMaxedException (); }
      
      System.err.println ("Finished");
      System.err.println (); /* For aesthetics */
      return bestC;
   }/*<==*/

   /**
    * Checks the preconditions for "findIdealCourse(...)" and throws
    * appropriate exceptions. 
    *
    * @param cdb The course database
    * @param idb The instructor database
    * @param i The instructor
    *
    * @throws EmptyCourseDatabaseException if "cdb" is empty
    * @throws NullInstructorException if "i" is null
    * @throws InstructorNotInDatabaseException if, for some reason, "i" isn't in
    *         the "idb"
    * @throws InstructorWTUMaxedException if no course exists which will not
    *         push an Instructor's WTU's past his/her limit
    * @throws InstructorCanTeachNothingException if no course exists for which 
    *         the Instructor's preference is not a 0
    */
   /* findCourseCheckPre ==>*/
   private void findCourseCheckPre (Vector cdb,
                                    Vector idb,
                                    Instructor i)
      throws 
         EmptyCourseDatabaseException,
         NullInstructorException,
         InstructorNotInDatabaseException,
         InstructorWTUMaxedException,
         InstructorCanTeachNothingException//
   {
      if (cdb.size() < 1)
      {
         throw new EmptyCourseDatabaseException ();
      }
      if (i == null)
      {
         throw new NullInstructorException ();
      }
      if (!idb.contains(i))
      {
         throw new InstructorNotInDatabaseException ();
      }
   }/*<==*/
   /*<==*/

   /* For finding times ==>*/
   /**
    * Compiles a list of all day/time combinations a given instructor can teach
    * a given course. Takes into consideration both DaysForClasses preferences
    * and NoClassOverlap preferences. 
    *
    * @param i The Instructor
    * @param c The Course
    *
    * @return A list of lists. Each entry in the first list represents a single
    *         day/time combination which worked. The first entry in the second
    *         list is for the lecture, while the second entry is for a possible
    *         lab.
    *
    * @throws CouldNotBeScheduledException when no DaysAndTime object can be
    *         created for a given course, meaning it is to be TBA
    */
   /* findTime ==>*/
   private Vector<Vector<DaysAndTime>> lec_findTime (Instructor i,
                                                 Course c)
      throws CouldNotBeScheduledException
   {
      Vector<Vector<DaysAndTime>> dats = new Vector<Vector<DaysAndTime>>();
      
      /*
       * Get a list of the following tuples:
       *
       * - The number of days to teach a course
       * - The number of half-hours it will be taught each day
       */
      Vector<Vector<Integer>> halfHourSlots = computeHalfHourLength (c);
      for (Vector<Integer> aDayCombo: halfHourSlots)
      {
         dats.addAll(createDATSForGivenTime (i,
                                             c,
                                             aDayCombo));
      }

      /*
       * If not even one DAT could be made for this course, it's a TBA
       */
      if (dats.size() == 0)
      {
         throw new CouldNotBeScheduledException (c);
      }
      System.err.println ("Had at least one DAT");

      return dats;
   }/*<==*/
   
   /**
    * Computes the number of half-hours slots a course can be taught over the
    * school week. In particular, courses with labs are to be taught for their
    * SCU - 1 hours. Courses w/o them are taught for their SCUs' worth of hours.
    *
    * @param c The course to be taught over the week
    *
    * @return A Vector of Vectors of Integers. Each vector 
    *         represents a given day-to=half-hour distribution. The first 
    *         element of this vector if the number of days; the second, how 
    *         many half-hours will fill each day.
    */
   /* computeHalfHourLength ==>*/
   private Vector<Vector<Integer>> computeHalfHourLength (Course c)
   {
      Vector<Vector<Integer>> listOfDaysTimes = new Vector<Vector<Integer>>();
      int totalHalfHoursToTeach;

      /*
       * All lectures are taught their SCUs'-worth of hours/week
       */
      totalHalfHoursToTeach = c.getSCUs();

      /*
       * Converts hours to their intended unit: half hours. This conversion
       * allows me to do arithmetic with integers, instead of nasty decimals.
       */
      totalHalfHoursToTeach *= 2;

      /*
       * For all days of the week (5, 4, 3, 2, & 1), see which split of the 
       * hour length among said days is acceptable. "Acceptable" is where 
       * a course's teaching length is equal across all days taught, and it's
       * taught for at least an hour everyday. 
       */
      for (int days = 5; days > 0; days --)
      {
         if (hoursDivideIntoDaysWell(totalHalfHoursToTeach, days))
         {
            Vector<Integer> toAdd = new Vector<Integer>();
            /*
             * Vector's elements are, in order:
             *  
             *  - Number of days to teach
             *  - Number of half-hours to teach every day
             */
            toAdd.add(days);
            toAdd.add(totalHalfHoursToTeach / days);
            listOfDaysTimes.add(toAdd);
         }
      }

      return listOfDaysTimes;
   }/*<==*/

   /**
    * Returns whether a number of half-hours can be evenly distributed over
    * a given number of days in such a way that the course is taught for
    * at least an hour every day. 
    *
    * @param halfHours Number of half hours to distribute
    * @param days Number of days to distribute the halfHours over
    *
    * @return (((halfHours % days) == 0) && ((halfHours / days) > 1))
    */
   /* hoursDivideIntoDaysWell ==>*/
   private boolean hoursDivideIntoDaysWell (int halfHours, int days)
   {
      return (((halfHours % days) == 0) && ((halfHours / days) > 1));
   }/*<==*/

   /**
    * Creates a list of lists of possible DaysAndTime objects. Each first list
    * representing DATs for a given number of days of the week. With each of 
    * these lists, the first DAT is for the lecture, and the second is for the 
    * lab, if one exists.
    *
    * A course's "DaysForClasses" preferences are taken into consideration here.
    *
    * @param i The instructor who to be scheduled for
    * @param c The course to schedule
    * @param aDayCombo Two-element vector. First element is how many days, 
    *        second is number of half hours to be taught on each day
    * 
    * @return A vector of vectors, each of which represents DATS which could
    *         be created for each DaysForClasses preference.
    */
   /* createDATSForGivenTime ==>*/
   private Vector<Vector<DaysAndTime>> createDATSForGivenTime 
      /* Line too long to fit all the params */
      (
         Instructor i,
         Course c,
         Vector<Integer> aDayCombo
      )
   {
      int nDays = aDayCombo.get(0);
      int nHalfHours = aDayCombo.get(1);

      Vector<Vector<DaysAndTime>> dats = new Vector<Vector<DaysAndTime>>();
      System.err.println ("Num of days: " + nDays);
      /*
       * Create DaysAndTime objects for when this course could be taught across
       * the Weeks computed above. 
       *
       * Each list given can have up to 2 elements: for the lecture and for 
       * its corresponding lab, if it has one.
       */
      Vector<Week> allWeeks = getAllDayCombos(c, nDays);
      System.err.println ("Start week");
      for (Week w: allWeeks)
      {
         System.err.println ("Have week: " + w);
         /*
          * Go and get all the times for which the Instructor has expressed a
          * desire to teach (those that aren't 0). Times will be returned in
          * descending order of desirability. 
          *
          * Note that multiplication by "multiple", which will double the length
          * of the time range if the course has a lab. This is to ensure that
          * labs are considered when looking for good times, as lab-time will
          * effectively double the time taught.
          *
          */
         int multiple = (c.hasLab()) ? 2 : 1;
         /*
          * This is a very important method call. Read its documentation (both 
          * javadoc and in-line).
          */
         for (Time t: getSortedTimesForWeek(i, w, nHalfHours * multiple))
         {
            Vector<DaysAndTime> toAdd = makeDATList(c, t, nHalfHours, w, i);
            if (toAdd != null)
            {
               System.err.println (t + " was good");
               dats.add(toAdd);
            }
         }
      }
      System.err.println ("End week loop");
      return dats;
   }/*<==*/

   /**
    * Given a number of days, returns a list of all weeks containing as many
    * permutations of that numbers of days as are possible. Uses the course's 
    * DFC(s) to define the form of the week, though not necessarily the number
    * of days.
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
      Vector<DaysForClasses> dPrefs = c.getDFC();
      /*
       * Go through all desired day combinations specified by Schedule
       * Preferences.
       */
      if (dPrefs == null || dPrefs.isEmpty())
      {
         System.err.println ("Course " + c + " has no DFC");
         c.addToFrontDFC(DaysForClasses.MTWRF);
      }
      for (DaysForClasses dfc: dPrefs)
      {
         System.err.println ("Have Course DFC: " + dfc);
         try
         {
            /*
             * Choose a number of days from the given Week (if able). 
             */
            allWeeks.add(chooseWhichDays (numOfDays, dfc.days, new Week()));
         }
         catch (NotEnoughDaysException e)
         {
            System.err.println ("Need " + numOfDays + " days; " + 
               "given insufficient '" + dfc.days + "'");
         }
      }
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
      if (depth > availableDays.size())
      {
         throw new NotEnoughDaysException();
      }
      if (depth == 0)
      {
         return alreadyAssigned;
      }
      else
      {
         for (Integer d: availableDays.getWeek())
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
    * Sorts an Instructors TimePreferences based on the average of his/her 
    * desire to teach during given times for every day in a given Week. Desires
    * are averaged and sorted in descending order. Any Time which has a desire 
    * 0 at any of the Week will disqualify the Time from further consideration.
    *
    * @param i The instructor whose TimePreferences are to be considered
    * @param w The Week which the Instructor's TimePreferences will be for (the
    *          days he/she will be teaching)
    * @param halfHours Number of halfHours to span a length of time to check
    *        preferences for
    * 
    * @return A sorted list of Times the Instructor wants to teach, in 
    *         descending order of desirability
    */
   private Vector<Time> getSortedTimesForWeek (Instructor i, /*==>*/
                                               Week w, 
                                               int halfHours)
   {
      /*
       * A TreeMap will make it easy to sort the Times based on their average
       * desirability across the given Week. The keys used will actually be 
       * Strings (to make desires unique, as there'll probably be a lot of the
       * same ones), and the Comparator I provide will sort the Strings in 
       * descending order according to desirability. In the case of ties, 
       * earlier times will be favored.
       *
       * See the documentation for the TimeSortByAvgDesireComparator class for
       * details on how the strings are sorted.
       */
      TreeMap avgTimeDesire = new TreeMap(new TimeSortByAvgDesireComparator());

      /*
       * Going past 23:30 means going to 0:00 the next day, which is never 
       * allowed (you'll even get an exception when you do)
       */
      Time t = new Time (0, 0);
      try
      {
         for (t = new Time(0, 0); /* Exception = break */; t.addHalf())
         {
            double total = 0;
            boolean alright = true;
            /*
             * We'll average the desires across the Week for a given time
             */
            for (int d: w.getWeek())
            {
               /*
                * Get the average desire over the span of time needed to teach. 
                * This will be done for all days in the Week and if, for any of 
                * them, a preference of 0 is found, the time range will no 
                * longer be considered.
                *
                * If this course has a lab, the time range will have been 
                * doubled, as labs are taught for equal amounts of time as 
                * their lecture and are always directly after the lecture
                */
               double desire = i.getAvgPrefForTimeRange(d, t, halfHours);
               /*
                * Remove the entire time span from consideration
                */
               System.err.println ("From " + t + " w/ length " + halfHours + ": " + desire);
               if (desire < 1)
               {
                  System.err.println ("DESIRE IS 0");
                  alright = false;
               }
               else
               {
                  total += desire;
               }
            }
            /*
             * Adding keys of the form "[desire]_[Hour]:[Min] will ensure unique
             * keys for all desires and all times. The comparator will do the 
             * [nasty] work of parsing these Strings to sort them properly.
             */
            if (alright)
            {
               avgTimeDesire.put(((total / w.size()) + "_" + t), new Time(t));
            }
         }
      }
      catch (InvalidInputException e)
      {
         /*
          * Means we reached the end of the day and can go no further
          */
         System.err.println ("Time: " + t + " w/ length " + halfHours + 
            " killed it");
      }

      /*
       * Since the values (Times) are sorted based on their average desirability
       * across the Week (Integers), we can just call "values" and get the 
       * sorted Times we want
       */
      System.err.println (avgTimeDesire.values());
      return new Vector<Time>(avgTimeDesire.values());
   }/*<==*/

   /**
    * Creates a list of acceptable DaysAndTime objects for a given Instructor's
    * starting time preference. At most, this will return two DAT objects: the 
    * first for the lecture, and the second for the lab. 
    *
    * @param c The course to make the DAT for. Used for checking for lab-ness
    * @param s The starting time
    * @param halfHours The number of half-hours after "s" the time should end
    * @param w The Week of days to schedule over
    * @param i The instructor, who must be available for all times assigned
    *
    * @return A list of DAT objects. The first will be for the course's lecture.
    *         If it has a lab, the second element in the list will be for said
    *         lab.
    */
   /* makeDATList ==>*/
   private Vector<DaysAndTime> makeDATList (Course c, 
                                            Time s, 
                                            int halfHours,
                                            Week w, 
                                            Instructor i)
   {
      Vector<DaysAndTime> list = new Vector<DaysAndTime>();
      
      /*
       * If there was a suitable time for the lecture
       */
      DaysAndTime lecDAT = createDAT (c, s, halfHours, w, i);
      if (lecDAT != null)
      {
         /*
          * If there is a lab, we must make sure another valid DAT can
          * be found for the instructor directly after the lecture. 
          */
         if (c.hasLab())
         {
            DaysAndTime labDAT = createDAT (c.getLabPairing(), lecDAT.e, halfHours, w, i);
            /*
             * If we've found suitable times for both lecture and lab, we can
             * accept them. There's no point teaching a lecture/lab if the 
             * counterpart can't also be taught
             */
            if (labDAT != null)
            {
               list.add(lecDAT);
               list.add(labDAT);
            }
            /*
             * Otherwise, we'll return null
             */
            else
            {
               list = null;
            }
         }
         else
         {
            list.add(lecDAT);
         }
      }
      /*
       * Return null if no acceptable time for the lecture could be found
       */
      else
      {
         list = null;
      }

      return list;
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
    *         available for this time frame, null is returned. 
    */
   /* createDAT ==>*/
   private DaysAndTime createDAT (Course c, Time s, int halfHours, Week days, 
                                  Instructor i)
   {
      /*
       * Since we know how long the course will go, computing the end-time 
       * relative to the start time is easy...just add up all the half-hours.
       */
      Time e = new Time (s.getHour(), s.getMinute());
      System.err.println ("Adding " + halfHours + " to " + s);
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
            /*
             * Ensure this course (whether lab or lecture) does not overlap 
             * with other courses it's not supposed to
             */
            if (doesNotOverlap(c, s, e, days))
            {
               return new DaysAndTime (days, s, e);
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
      return null;
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
    * Finds the location for a course and, if it has one, its labs.
    *
    * @param ldb List of locations to try
    * @param times List of day/time combinations to try
    * @param c The course to find a location for (contains its lab, if any).
    * 
    * @return Location for a course and, if it has one, its lab. Lecture data
    *         is at index 0; lab data is at index 1
    * 
    * @throws CouldNotBeScheduledException if TBA is given as the lecture's
    *         location.
    */
   /* findLocation ==>*/
   private Vector<ScheduledLocation> findLocation (Vector<Location> ldb,
                                                   Vector<Vector<DaysAndTime>> times,
                                                   Course c)
      throws CouldNotBeScheduledException
   {
      Vector<ScheduledLocation> locs = new Vector<ScheduledLocation>();
      ScheduledLocation lec = ScheduledLocation.TBA;
      ScheduledLocation lab = ScheduledLocation.TBA;

      /*
       * Try every day/time combo for the lecture (and use the second day/time
       * pair for the lecture's lab, if it has one).
       * 
       * Note that, should there be no case where non-TBA locations can be found
       * for lectures (and labs), TBA's will be used as a last resort.
       */
      for (Vector<DaysAndTime> dats: times)
      {
         lec = findLoc(ldb, dats.firstElement(), c);
         /*
          * If a lab is present, then an extra check must be made to ensure that
          * a lecture and lab are scheduled back-to-back on the same days
          */
         if (c.hasLab())
         {
            lab = findLoc(ldb, dats.lastElement(), c.getLabPairing());
            /*
             * Only if both the lecture AND lab could be scheduled next to one
             * another can we accept ScheduledLocation's
             */
            if (lec != ScheduledLocation.TBA && lab != ScheduledLocation.TBA)
            {
               break;
            }
            /*
             * If one (or both) weren't good, reset both and try again
             */
            else
            {
               lec = ScheduledLocation.TBA;
               lab = ScheduledLocation.TBA;
            }
         }
         else
         {
            /*
             * Only accept if the location is a legitamate one
             */
            if (lec != ScheduledLocation.TBA)
            {
               break;
            }
         }
      }

      /*
       * If the lecture could not be scheduled, raise a red flag and let the
       * caller deal with it
       */
      if (lec == ScheduledLocation.TBA)
      {
         throw new CouldNotBeScheduledException(c);
      }
      /*
       * Otherwise, add the found locations to the list and return them
       */
      locs.add(lec);
      if (c.hasLab())
      {
         locs.add(lab);
      }

      return locs;
   }/*<==*/

   /**
    * Finds a location for a given course, given a day/time to book.
    *
    * @param ldb List of locations to try
    * @param dat Day/time combination to try
    * @param c The course to put in the returned location.
    *
    * @return A ScheduledLocation object "c" can be taught in, or "TBA" if none 
    *         was found.
    */
   /* findLoc ==>*/
   private ScheduledLocation findLoc (Vector<Location> ldb, 
                                      DaysAndTime dat,
                                      Course c)
   {
      ScheduledLocation sl = ScheduledLocation.TBA;
      for (Location l: ldb)
      {
         /*
          * If a course has a lab, the lab's day/time combination is the 
          * second (last) element in the vector
          */
         if (l.providesFor(c))
         {
            try
            {
               if (lBookings.get(l).isFree(dat.s, dat.e, dat.week))
               {
                  sl = new ScheduledLocation(l, dat);
                  break;
               }
            }
            catch (NotADayException e)
            {
               e.printStackTrace();
            }
            catch (EndBeforeStartException e)
            {
               e.printStackTrace();
            }
         }
      }
      return sl;
   }/*<==*/
  /*<==*/ 

   /* For adding to the schedule ==>*/
   /**
    * Creates a ScheduleItem. Note that the Course supplied to this method will
    * be copied into the ScheduleItem (they'll be two completely separate 
    * objects).
    * 
    * @param i The instructor
    * @param c The course
    * @param l The location and day/time pair
    *
    * @return A ScheduleItem containing the following data:
    * <pre>
    *       i                 - Instructor
    *       c                 - Course
    *       l.l               - Location
    *       c.getSection()    - Course section
    *       l.dat.week        - Days to schedule it
    *       l.dat.s           - Start time
    *       l.dat.e           - End time
    * </pre>
    */
   /* createSI ==>*/
   private ScheduleItem createSI (Instructor i, Course c, ScheduledLocation l)
   {
      return new ScheduleItem (i, 
                               new Course(c),
                               l.l, 
                               c.getSection(), 
                               l.dat.week, 
                               l.dat.s, 
                               l.dat.e);
   }/*<==*/

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
      System.err.println ("In add");
      /*
       * If Location was undetermined, don't add it to the schedule...add it to
       * a special list
       */
      if (si.l == Location.TBA)
      {
         System.err.println ("Shouldn't have a TBA when adding!");
         this.TBAs.add(new TBA(si.c, si.i));
         return false;
      }
      else
      {
         System.err.println ("Not a TBA");
         if (!verifyNoBadBookings (si))
         {
            return false;
         }
         System.err.println ("Booked well");
         /*
          * Update book-keeping records (i, c, and l lists, along with 
          * Instructor treatment)
          */
         updateRecords (si);
   
         /*
          * Book the course, instructor, location for this time
          */
         this.cot.book(si.c, si.start, si.end, si.days);
         book(this.lBookings.get(si.l), si.days, si.start, si.end);
         book(this.iBookings.get(si.i), si.days, si.start, si.end);

         System.err.println (si);

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
      if (!check(this.lBookings.get(si.l), si.days, si.start, si.end))
      {
         System.err.println ("Double booked location " + si.l + " from " + 
               si.start + " to " + si.end + " on " + si.days +
               "with instructor " + si.i + " and course " + si.c);
         passed = false;
      }
      if (!check(this.iBookings.get(si.i), si.days, si.start, si.end))
      {
         System.err.println ("Double booked instructor " + si.i + " from " + 
               si.start + " to " + si.end + " on " + si.days + " with course " +
               si.c);
         passed = false;
      }
      /*
       * Ensure that no-overlap classes don't overlap
       *
       * TODO: Make better/right
       */
      //if (!check(this.cBookings.get(si.c), si.days, si.start, si.end))
      //{
         //System.err.println ("Class " + si.c + " overlapping something it " +
               //" shouldn't be on " + si.days + " from " + si.start + " to " + 
               //si.end);
         //passed = false;
      //}
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
         System.err.println ("Error adding on " + days + " from " + start + 
            " to " + end);
      }
      catch (EndBeforeStartException e)
      {
         System.err.println ("Error adding on " + days + " from " + start + 
            " to " + end);
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
      if (!this.lList.contains(si.l))
      {
         this.lList.add(si.l);
      }
      
      /*
       * Update "treatment" according to what's in the ScheduleItem
       */
      Treatment t = this.treatment.get(si.i);
      t.courses.add(si.c);
      t.wtu += si.c.getWTUs();
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
      return new LinkedList(this.cList);
   }

   /**
    * Returns the list of instructors.
    *
    * @return the list of instructors
    */
   public LinkedList<Instructor> getInstructorList ()
   {
      return new LinkedList(this.iList);
   }

   /**
    * Returns the list of locations.
    *
    * @return the list of locations
    */
   public LinkedList<Location> getLocationList ()
   {
      return new LinkedList(this.lList);
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
      this.initBookings(s.iList, s.lList, new Vector<SchedulePreference>());
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
      System.err.println ("Done recreating Schedule");
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
