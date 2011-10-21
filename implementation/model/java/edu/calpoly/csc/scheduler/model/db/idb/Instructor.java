/*
 * NOTE TO AUTHOR (LORENZ): In the "getCoursePreferences()" method, you returned
 * a new ArrayList of CoursePreferences if the instructor's were null: I changed
 * that to return null. The method was frustrating b/c, if an instructor's
 * CPrefs were null, I couldn't know, since the "getCoursePreferences" method
 * said they weren't. But, when I would call the "getPreference(Course)" method,
 * it will give me a null-pointer-exception, since the CPrefs -were- null. To
 * avoid confusion, the method now returns null if the prefs are null. As of now
 * (8-18-10), doing so hasn't hindered the scheduler at all. Let me know if
 * these changes have impacted something I overlooked. Contact me at
 * eliebowi@calpoly.edu or call/text at 818.530.3799 - Eric
 */
package edu.calpoly.csc.scheduler.model.db.idb;

import edu.calpoly.csc.scheduler.model.db.*;
import edu.calpoly.csc.scheduler.model.db.ldb.*;
import edu.calpoly.csc.scheduler.model.db.cdb.*;
import edu.calpoly.csc.scheduler.model.schedule.*;

import java.util.*;
import java.io.Serializable;

/**
 * This class holds the information necessary to represent an instructor.
 * Through this class the name, office number, work-time units, and other
 * necessary information may be accessed and stored.
 * 
 * @author Cedric Wienold Modified by Jan Lorenz Soliman (March 2010 to June
 *         2010)
 */

public class Instructor implements Comparable<Instructor>, Serializable
{
   public static final int serialVersionUID = 42;

   /**
    * This exception class is thrown when the day of week is not in the Sunday
    * to Saturday format.
    * 
    * @author Cedric Wienold
    * 
    */
   public class InvalidDayOfWeekException extends RuntimeException
   {
      /**
       * Calls the exception constructor.
       */
      public InvalidDayOfWeekException ()
      {
         super();
      }
   }

   /**
    * This exception is raised when invalid time inputs are entered.
    * 
    * @author Cedric Wienold
    * 
    */
   public class InvalidTimeInputException extends RuntimeException
   {
      public InvalidTimeInputException ()
      {
         super();
      }
   }

   /**
    * Exception class for null preferences.
    * 
    * @author Cedric Wienold
    * 
    */
   public static class NullPreferenceException extends RuntimeException
   {
      /**
       * This constructor is used for throwing this exception.
       */
      public NullPreferenceException ()
      {
         super();
      }
   }

   /**
    * Exception class for null user ids.
    * 
    * @author Cedric Wienold
    * 
    */
   public static class NullUserIDException extends RuntimeException
   {
      /**
       * This constructor is used for throwing this exception.
       */
      public NullUserIDException ()
      {
         super();
      }
   }

   /**
    * Exception class for generally bad instructors.
    * 
    * @author Cedric Wienold
    * 
    */
   public static class InvalidInstructorException extends RuntimeException
   {
      /**
       * This constructor throws this exception.
       */
      public InvalidInstructorException ()
      {
         super();
      }
   }

   /**
    * First name of instructor.
    */
   private String firstName;

   /**
    * Last name of instructor.
    */
   private String lastName;
   
   private String roomNumber;

   /**
    * User ID of instructor.
    */
   private String userID;

   /**
    * Work-Time units.
    */
   private int maxWtu;
   /**
    * Current amount of wtus
    */
   private int curWtu;
   
   /**
    * Building and office number of instructor.
    */
   private Location office;

   /**
    * The fairness with which the instructor has been treated
    */
   private int fairness;

   /**
    * Whether or not the instructor has a disability.
    * 
    **/
   private boolean disability;

   /**
    * The generosity of the instructors scheduler.
    **/
   private int generosity;

   /**
    * Records this instructor's current availability.
    */
   private WeekAvail availability;
   
   private String building;

   /**
    * List of preferences each instructor has for a course.
    */
   private ArrayList<CoursePreference> coursePreferences;

   /**
    * List of time preferences an instructor has for a course.
    */
   private HashMap<Day, LinkedHashMap<Time, TimePreference>> tPrefs;

   public Instructor ()
   {
   }

   /**
    * TODO: REWRITE
    * 
    * @return
    */
   protected ArrayList<CoursePreference> initialCoursePreferences ()
   {
      ArrayList<CoursePreference> returnVal = new ArrayList<CoursePreference>();
      // if (Scheduler.getLocalCDB() != null)
      // {
      // System.err.println ("Instructor says local CDB not null");
      // Vector<Course> cList = new Vector<Course>(Scheduler.getLocalCDB());
      // for (Course c: cList)
      // {
      // returnVal.add(new CoursePreference(c, 5));
      // }
      // }
      // else
      // {
      // System.err.println("My method sucks!");
      // return null;
      // }
      return returnVal;
   }

   /**
    * Represents the STAFF instructor. Placed here to provide a stable place to
    * find this instructor. Hopefully, all other classes will eventually use
    * this whenever they need STAFF.
    * 
    * By: Eric Liebowitz
    */
   public static final Instructor STAFF = new Instructor("Staff", "ffatS",
      "STAFF", -1, null);

   /**
    * Returns a new, fresh copy of a given instructor. Written by: Eric
    * Liebowitz
    * 
    * @param i
    *           The instructor to copy
    */
   public Instructor (Instructor i)
   {
      this.firstName = i.firstName;
      this.lastName = i.lastName;
      this.userID = i.userID;
      this.maxWtu = i.maxWtu;
      this.office = i.office;
      // Preserve preferences!
      this.coursePreferences = i.coursePreferences;
      this.tPrefs = i.getTimePreferences();
   }

   /**
    * Constructs the instructor with the given first and last names, and user
    * id. This will set "disabilities" to false
    * 
    * @param first
    *           the first name of the instructor
    * @param last
    *           the last name of the instructor
    * @param id
    *           the user id of the instructor
    * @param wtu
    *           the work-time units of the instructor
    * @param office
    *           the office building and room numbers of the instructor
    */
   public Instructor (String first, String last, String id, int wtu,
      Location office)
   {
      init(first, last, id, wtu, office, false);
   }

   /**
    * Constructs the instructor with the given first and last names, and user
    * id.
    * 
    * @param first
    *           the first name of the instructor
    * @param last
    *           the last name of the instructor
    * @param id
    *           the user id of the instructor
    * @param wtu
    *           the work-time units of the instructor
    * @param office
    *           the office building and room numbers of the instructor
    * @param disabilities
    *           whether or not the professor has any disabilities
    */
   public Instructor (String first, String last, String id, int wtu,
		   Location office, boolean disabilities)
		   {
		      init(first, last, id, wtu, office, disabilities);
		   }
   
   public Instructor (String first, String last, String id, int wtu,
		   String building, String room, boolean disabilities)
		   {
		      init(first, last, id, wtu, building, room, disabilities);
		   }

   /**
    * Does the initialization all Instructor constructors need. Since they all
    * do the same thing (with minor tweaks), unifying the actions into one
    * method seemed wise.
    * 
    * @param first
    *           the first name of the instructor
    * @param last
    *           the last name of the instructor
    * @param id
    *           the user id of the instructor
    * @param wtu
    *           the work-time units of the instructor
    * @param office
    *           the office building and room numbers of the instructor
    * @param disabilities
    *           whether or not the professor has any disabilities
    */
   private void init (String first, String last, String id, int wtu,
		      Location office, boolean disabilities)
		   {
		      this.firstName = first;
		      this.lastName = last;
		      this.userID = id;
		      this.maxWtu = wtu;
		      this.office = office;
		      this.disability = disabilities;
		      this.availability = new WeekAvail();
		      this.coursePreferences = initialCoursePreferences();
		      initTPrefs();
		   }
   
   private void init (String first, String last, String id, int wtu,
		      String building, String room, boolean disabilities)
		   {
		      this.firstName = first;
		      this.lastName = last;
		      this.userID = id;
		      this.maxWtu = wtu;
		      this.building = building;
		      this.roomNumber = room;
		      this.disability = disabilities;
		      this.availability = new WeekAvail();
		      this.coursePreferences = initialCoursePreferences();
		      initTPrefs();
		   }

   /**
    * Gives instructor's prefs of 5 for all times between 7a.m. and 10 p.m..
    * Everything else gets a 0.
    * 
    * TODO: Make this some built-in preference the user can change themselves.
    * Hard-coding it here just isn't a good plan.
    * 
    * Written by: Eric Liebowitz
    */
   private void initTPrefs ()
   {
      this.tPrefs = new HashMap<Day, LinkedHashMap<Time, TimePreference>>();
      for (Day d : Week.fiveDayWeek.getDays())
      {
         fillDayWithTPrefs(d);
      }
   }

   /**
    * Initiliazes a given days' LinkedHashMap of TimePreferences. Times from
    * 7a-10p get a desire of 5; all others get 0.
    * 
    * @param d
    *           The day to fill with default TPrefs
    * 
    * @throws NotADayException
    *            if "d" is not one of the days defined in generate.Week.java
    */
   private void fillDayWithTPrefs (Day d)
   {
      /*
       * Make a list of the preferences for a given day
       */
      LinkedHashMap<Time, TimePreference> tPrefList = new LinkedHashMap<Time, TimePreference>();

      for (int hour = 0; hour < 24; hour++)
      {
         int desire = (hour < 7 || hour > 20) ? 0 : 5;
         /*
          * Yes, there's ducpliate data between the time keys and the TPref
          * values. This is intentional, as the generate algorithm will need it.
          */
         Time t1 = new Time(hour, 0);
         Time t2 = new Time(hour, 30);
         TimePreference tp1 = new TimePreference(t1, desire);
         TimePreference tp2 = new TimePreference(t2, desire);

         tPrefList.put(t1, tp1);
         tPrefList.put(t2, tp2);
      }
      /*
       * Once created, add the list to the day it applies to in the instructor
       * tPrefs
       */
      this.tPrefs.put(d, tPrefList);
   }

   /**
    * This method will add a TimePreference to a given day.
    * 
    * @param d
    *           The day to apply the preference to
    * @param tp
    *           The time preference
    * 
    * @throws NotADayException
    *            if "d" is not one of the days defined in generate.Week.java
    */
   public void addTimePreference (Day d, TimePreference tp)
      throws NullPreferenceException
   {
      if (tp == null)
      {
         throw new NullPreferenceException();
      }

      if (!this.tPrefs.containsKey(d))
      {
         fillDayWithTPrefs(d);
      }
      this.tPrefs.get(d).put(tp.getTime(), tp);
   }

   /**
    * This method adds a course preference to the instructor.
    * 
    * @param preference
    */
   public void addCoursePreference (CoursePreference preference)
      throws NullPreferenceException
   {
      if (preference == null)
      {
         throw new NullPreferenceException();
      }
      coursePreferences.add(preference);
   }

   /**
    * This method removes a course preference to the instructor.
    * 
    * @param preference
    */
   public void removeCoursePreference (CoursePreference preference)
      throws NullPreferenceException
   {
      if (preference == null)
      {
         throw new NullPreferenceException();
      }
      Iterator iterator = coursePreferences.iterator();

      int index = 0;
      int remove = -1;
      while (iterator.hasNext())
      {
         CoursePreference check = (CoursePreference) iterator.next();
         if (coursePreferences.contains(preference))
         {
            remove = index;
            break;
         }
         index++;
      }

      if (remove >= 0)
      {
         coursePreferences.remove(remove);
      }
   }

   /**
    * Added so I can make an ordered list of Instructors
    * 
    * @param o
    *           Thing to compare
    * @return 1 if o was greater. 0 if equal. -1 if lesser.
    * 
    *         Written by: Eric Liebowitz
    */
   public int compareTo (Instructor o)
   {
      if (this.generosity < o.getGenerosity())
      {
         return -1;
      }
      else if (this.generosity == o.getGenerosity())
      {
         return 0;
      }
      else
      {
         return 1;
      }
   }

   /**
    * This method will return true if the argument is not null, is an Instructor
    * class and has the same userID as this object.
    * 
    * @param other
    *           the object to compare with
    * @return whether the two objects are equal
    */
   public boolean equals (Object other)
   {
      if (other == null)
         return false;

      if (!(other instanceof Instructor))
      {
         return false;
      }

      try
      {
         if (!((Instructor) other).getId().equals(this.userID))
         {
            return false;
         }
      }
      catch (NullUserIDException e)
      {
         return false;
      }

      return true;
   }

   /**
    * This method returns the list of course preferences.
    * 
    * @return the list of course preferences.
    */
   public ArrayList<CoursePreference> getCoursePreferences ()
   {
      if (coursePreferences == null)
      {
         /*
          * NOTE: This used to returns a new, all-5's ArrayList of
          * CoursePreference objects whenever the instructor CPrefs were null.
          * This wasn't good, as I didn't know when the CPrefs weren't around,
          * b/c this method gave me good prefs regardless of whether they were
          * actually null or not. - Eric
          */
         System.err.println("CPrefs are null!");
         return null;
      }
      else
      {
         return new ArrayList<CoursePreference>(coursePreferences);
      }
   }

   /**
    * This method sets the course preferences for the instructor.
    * 
    * @param the
    *           list of course preferences.
    */
   public void setCoursePreferences (ArrayList<CoursePreference> cPreferences)
   {
      this.coursePreferences = cPreferences;
      // return new ArrayList<CoursePreference>(coursePreferences);
   }

   /**
    * Returns a boolean representing whether an instructor has a disability.
    * 
    * @return a boolean representing whether an instructor has a disability
    */
   public boolean getDisability ()
   {
      return disability;
   }

   public String getBuilding(){
	   return building;
   }
   
   /**
    * Returns the fairness value of this instructor.
    * 
    * @return the fairness value of this instructor.
    */
   public int getFairness ()
   {
      return fairness;
   }

   /**
    * Returns a string representing this instructor's first name.
    * 
    * @return the instructor's first name.
    * 
    */
   public String getFirstName ()
   {
      return new String(this.firstName);
   }

   /**
    * Returns the generosity of the instructors.
    **/
   public int getGenerosity ()
   {
      return generosity;
   }

   /**
    * Returns the id of the instructors.
    **/
   public String getId () 
   {
      return userID;
   }

   /**
    * Returns a string representing this instructor's last name.
    * 
    * @return the instructor's last name.
    * 
    */
   public String getLastName ()
   {
      return new String(this.lastName);
   }

   /**
    * Returns the instructor's work-time units.
    * 
    * @return this instructor's work-time units
    */
   public int getMaxWTU ()
   {
      return this.maxWtu;
   }

   public String getRoomNumber(){
	   return roomNumber;
   }
   
   /**
    * Sets the maxWtu to the given parameter.
    *
    * @param maxWtu the maxWtu to set
    */
   public void setMaxWtu (int maxWtu)
   {
      this.maxWtu = maxWtu;
   }

   /**
    * Returns this instructor's current wtu count
    * 
    * @return this instructor's current wtu count
    */
   public int getCurWtu ()
   {
      return this.curWtu;
   }
   
   /**
    * Sets the curWtu to the given parameter.
    *
    * @param curWtu the curWtu to set
    */
   public void setCurWtu (int curWtu)
   {
      this.curWtu = curWtu;
   }

   /**
    * Returns this instructor's current wtu count
    * 
    * @return this instructor's current wtu count
    */
   public int getAvailableWTU()
   {
      return this.getCurWtu();
   }
   
   /**
    * Returns a string representing the instructor's name in the form first name
    * - space - last name.
    * 
    * @return the first - space - last name representation of this instructor's
    *         name.
    */
   public String getName ()
   {
      return new String(this.firstName + " " + this.lastName);
   }

   /**
    * Returns the instructor's office.
    * 
    * @return the instructor's office.
    */
   public Location getOffice ()
   {
      return this.office;
   }

   /**
    * This method will get a course preference from the list of preferences.
    * 
    * @param course
    *           the course for which to get the preference.
    * @return the course preference for the given course.
    */
   public int getPreference (Course course)
   {
      int desire = 5;
      for (CoursePreference c : coursePreferences)
      {
         if (c.getCourse().equals(course))
         {
            desire = c.getDesire();
         }
      }
      return desire;
   }

   /**
    * This method will return the time preference from the list of preferences
    * for a given day and time. If the day in question does not yet have any
    * TPrefs, it will be populated with default values.
    * 
    * @param d
    *           the day the preference is from
    * @param t
    *           the time of the preference to get.
    * 
    * @return the preference (int) related to the given time on the given day
    * 
    * @throws NotADayException
    *            if "d" is not one of the days defined in generate.Week.java
    * 
    *            Written by: Eric Liebowitz
    */
   public int getPreference (Day d, Time time)
   {
      if (!this.tPrefs.containsKey(d))
      {
         fillDayWithTPrefs(d);
      }
      if (this.tPrefs.get(d).get(time) == null)
      {
         return 0;
      }
      return this.tPrefs.get(d).get(time).getDesire();
   }

   /**
    * Looks up a professor's TimePreferences for a given day.
    * 
    * @param d
    *           Day which all TimePreferences should be for.
    * 
    * @return List of TimePreferences, sorted in descending order of
    *         desirability
    * 
    * @throws NotADayException
    *            if "d" is not a valid day. See "Week" under the "generate"
    *            package, and look for the "isValidDay" method docs for more
    *            information.
    * 
    *            Written by: Eric Liebowitz
    */
   public Vector<TimePreference> getTPrefsByDay (int d)
   {
      Vector<TimePreference> r = new Vector<TimePreference>(this.tPrefs.get(d)
         .values());
      Collections.sort(r, new TimePreferenceComparator());
      return r;
   }

   /**
    * Returns the average desire an Instructor has for teaching a give length of
    * time on given days. If there is a preference of 0 found in this length of
    * Time for any day in the Week, 0 is returned immediately.
    * 
    * @param dat
    *           Object containing the week, start, and end time to lookup the
    *           average preference for.
    * 
    * @return the average desire for the given time range. If there was a 0
    *         found anywhere in this time range, 0 is returned.
    */
   public double getAvgPrefForTimeRange (DaysAndTime dat)
   {
      return getAvgPrefForTimeRange(dat.getWeek(), dat.getS(), dat.getE());
   }

   /**
    * Returns the average desire an Instructor has for teaching a give length of
    * time on given days. If there is a preference of 0 found in this length of
    * Time for any day in the Week, 0 is returned immediately.
    * 
    * @param w The day(s) to check
    * @param s The start time
    * @param e The end time
    * 
    * @return the average desire for the given time span
    */
   public double getAvgPrefForTimeRange (Week w, Time s, Time e)
   {
      double total = 0;
      int length = s.getHalfHoursBetween(e);

      /*
       * Go over every day in the week
       */
      for (Day d : w.getDays())
      {
         Time tempS = new Time(s);
         double dayTotal = 0;
         /*
          * Get the desire from each half hour slot in the Time range
          */
         for (int i = 0; i < length; i++, tempS.addHalf())
         {
            int desire = getPreference(d, tempS);
            /*
             * If a zero is encountered, immediately break out and return 0 to
             * let the caller know that this Time range (or some part of it)
             * cannot be applied to this Instructor
             */
            if (desire < 1)
            {
               return 0;
            }
            else
            {
               dayTotal += desire;
            }
         }
         total += (dayTotal / length);
      }

      return total;
   }

   /**
    * This method returns the list of time preferences.
    * 
    * @return the list of time preferences.
    */
   public HashMap<Day, LinkedHashMap<Time, TimePreference>> getTimePreferences ()
   {
      return tPrefs;
   }

   /**
    * Update the preferences to get rid of courses that may not be there
    * anymore.
    * 
    * TODO: REWRITE
    * 
    */
   public void updateLocalPreferences ()
   {
      Iterator it = this.coursePreferences.iterator();
      Vector<Integer> removeIndexes = new Vector<Integer>();
      int i = 0;
      while (it.hasNext())
      {
         CoursePreference cp = (CoursePreference) it.next();
         // if (!Scheduler.cdb.getLocalData().contains(cp.getCourse())) {
         // //System.out.println("Removing " + cp);
         // removeIndexes.add(i);
         // }
         i++;
      }

      // it = Scheduler.cdb.getLocalData().iterator();
      //
      // boolean found = false;
      // while (it.hasNext()) {
      // Course co = (Course) it.next();
      // found = false;
      // for (CoursePreference c : coursePreferences) {
      // if (c.getCourse().equals(co)) {
      // found = true;
      // break;
      // }
      // }
      // if (!found) {
      // coursePreferences.add(new CoursePreference(co, 5));
      // }
      // }

      it = removeIndexes.iterator();
      while (it.hasNext())
      {
         int removeIndex = (Integer) it.next();
         // System.out.println("Removing Index " + removeIndex);
         // System.out.println("Size is " + coursePreferences.size());
         coursePreferences.remove(removeIndex);
      }

   }

   /**
    * This method sets the list of time preferences.
    * 
    * TODO: The LinkedHashMap is a terrible way to do this.
    * 
    * @param tps a list of time preferences.
    * @return the list of time preferences.
    */
   public void setTimePreferences (
      HashMap<Day, LinkedHashMap<Time, TimePreference>> tps)
   {
      this.tPrefs = tps;
   }

   /**
    * Overridden to allow for hash table implementation
    * 
    * @author Eric Liebowitz
    */
   public int hashCode ()
   {
      return userID.hashCode();
   }
   
   /**
    * Returns true if this instructor is available for the given Week of days
    * and the given time range
    * 
    * @param w Week containing the days to check
    * @param tr Time range to check 
    * 
    * @return True if the instructor is available for the given time range 
    *         across the given Week
    */
   public boolean isAvailable (Week days, TimeRange tr)
   {
      return this.availability.isFree(tr.getS(), tr.getE(), days);
   }

   /**
    * This method will take in a day, start time, and end time and set that time
    * interval as busy for this instructor.
    * 
    * @param dayOfWeek the day of the week.
    * @param starttime the start time to set busy.
    * @param endtime the end time of the busy interval.
    */
   public void setBusy (Day dayOfWeek, Time starttime, Time endtime)
   {
      this.availability.book(starttime, endtime, dayOfWeek);
   }

   /**
    * @return the availability
    */
   public WeekAvail getAvailability ()
   {
      return availability;
   }

   /**
    * @param availability
    *           the availability to set
    */
   public void setAvailability (WeekAvail availability)
   {
      this.availability = availability;
   }

   /**
    * Returns a lastname-comma-firstname representation of this instructor's
    * name.
    * 
    * @return the full string representation of this instructor's name
    */
   public String toString ()
   {
      return new String(this.getLastName() + ", " + this.getFirstName());
   }
   
      /**
    * Checks to see if an instructor can teach the given course.
    * Checks available WTUs and if their teaching preference is not 0.
    * 
    * @param course Course instructor might teach
    * 
    * @return A list of time ranges that instructor can and wants to teach this
    *         course
    */
   public boolean canTeach(Course course)
   {
      //Check if instructor has enough WTUs
      if((this.curWtu + course.getWtu()) <= this.maxWtu)
      {
         //Check if preferences allow it
         //TODO: rewrite this when CoursePreference is changed to a hash
         for(CoursePreference coursePref : this.coursePreferences)
         {
            if(coursePref.getCourse().equals(course))
            {
               if(coursePref.getDesire() > 0)
               {
                  return true;
               }
            }
         }
      }
      return false;
   }
   
   /**
    * Gets the list of time ranges that this instructor is available and wants
    * to teach this course. The Course's 'getDays' method is used to determine
    * what days to use
    * 
    * @param c Course instructor is to teach
    * 
    * @return A list of time ranges that instructor can and wants to teach this
    *         course
    * 
    * @see Course#getDays()
    */
   public Vector<TimeRange> getTeachingTimes (Course c)
   {
      Vector<TimeRange> r = new Vector<TimeRange>();
      int length = c.getLength();
      
      for (Time t: Time.ALL_TIMES_IN_DAY)
      {
         TimeRange tr = new TimeRange(t, length);
         double pref = 
            this.getAvgPrefForTimeRange(c.getDays(), tr.getS(), tr.getE());
         if (pref > 0 && this.isAvailable(c.getDays(), tr))
         {
            r.add(tr);
         }
      }
      
      return r;
   }
}
