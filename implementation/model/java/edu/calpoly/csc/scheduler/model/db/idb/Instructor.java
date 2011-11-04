package edu.calpoly.csc.scheduler.model.db.idb;

import edu.calpoly.csc.scheduler.model.db.*;
import edu.calpoly.csc.scheduler.model.db.ldb.*;
import edu.calpoly.csc.scheduler.model.db.cdb.*;
import edu.calpoly.csc.scheduler.model.schedule.*;

import java.util.*;
import java.io.Serializable;

/**
 * This class holds the information necessary to represent an instructor.
 * 
 * @author Eric Liebowitz
 * @version October 22, 2011
 */

public class Instructor extends DbData 
                        implements Comparable<Instructor>, Serializable
{
   public static final int serialVersionUID = 42;

   public static final int DEFAULT_PREF = 5;

   /**
    * Quarter this Instructor is a part of
    */
   private String quarterId;
   /**
    * Schedule this Instructor is a part of
    */
   private Integer scheduleId;
   
   /**
    * First name of instructor.
    */
   private String firstName;

   /**
    * Last name of instructor.
    */
   private String lastName;

   /**
    * User ID of instructor.
    */
   private String userID;

   /**
    * Work-Time units.
    */
   private Integer maxWtu;

   /**
    * Current amount of wtus
    */
   private Integer curWtu;

   /**
    * Building and office number of instructor.
    */
   private Location office;

   /**
    * The fairness with which the instructor has been treated
    */
   private Integer fairness;

   /**
    * Whether or not the instructor has a disability.
    * 
    **/
   private Boolean disability;

   /**
    * The generosity of the instructors scheduler.
    **/
   private int generosity;

   /**
    * Records this instructor's current availability.
    */
   private WeekAvail availability;

   /**
    * Keeps track of an instructor course preferences.
    */
   private HashMap<Course, Integer> coursePreferences = 
      new HashMap<Course, Integer>();

   /**
    * List of time preferences an instructor has for a course.
    */
   private HashMap<Day, LinkedHashMap<Time, TimePreference>> tPrefs;

   private Vector<ScheduleItem> itemsTaught = new Vector<ScheduleItem>();

   public Instructor () { }

   /**
    * Constructs the instructor with the given first and last names, and user
    * id.
    * 
    * @param first the first name of the instructor
    * @param last the last name of the instructor
    * @param id the user id of the instructor
    * @param wtu the work-time units of the instructor
    * @param office the office building and room numbers of the instructor
    * @param disabilities whether or not the professor has any disabilities
    */
   public Instructor (String first, String last, String id, int wtu,
      Location office, boolean disabilities)
   {
      this.firstName = first;
      this.lastName = last;
      this.userID = id;
      this.maxWtu = wtu;
      this.office = office;
      this.disability = disabilities;
      this.availability = new WeekAvail();
      initTPrefs();
   }
   
   /**
    * Returns a new, fresh copy of a given instructor. 
    * 
    * @param i The instructor to copy
    */
   public Instructor (Instructor i)
   {
      this.firstName = i.firstName;
      this.lastName = i.lastName;
      this.userID = i.userID;
      this.maxWtu = i.maxWtu;
      this.office = i.office;
      // Preserve preferences!
      this.coursePreferences = new HashMap<Course, Integer>(
         i.getCoursePreferences());
      this.tPrefs = i.getTimePreferences();
   }

   public Instructor (String f, String l, String id, int wtu, Location office)
   {
      this (f, l, id, wtu, office, false);
   }

   public Instructor (String first, String last, String id, int wtu,
      String building, String room, boolean disabilities)
   {
      
      this (first, last, id, wtu, new Location(building, room), disabilities);
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
    * @param d The day to fill with default TPrefs
    * 
    * @throws NotADayException if "d" is not one of the days defined in
    *         generate.Week.java
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
    * @param d The day to apply the preference to
    * @param tp The time preference
    * 
    * @throws NotADayException if "d" is not one of the days defined in
    *         generate.Week.java
    */
   public void addTimePreference (Day d, TimePreference tp)
   {
      assert (tp != null);

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
   public void addCoursePreference (CoursePreference cp)
   {
      assert (cp != null);

      coursePreferences.put(cp.getCourse(), cp.getDesire());
   }

   /**
    * This method removes a course preference to the instructor.
    * 
    * @param preference
    */
   public void removeCoursePreference (CoursePreference preference)
   {
      assert (preference != null);

      coursePreferences.remove(preference);
   }

   /**
    * Added so I can make an ordered list of Instructors
    * 
    * @param o Thing to compare
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
    * @param other the object to compare with
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

      if (!((Instructor) other).getId().equals(this.userID))
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
   public HashMap<Course, Integer> getCoursePreferences ()
   {
      return this.coursePreferences;
   }

   /**
    * This method sets the course preferences for the instructor.
    * 
    * @param the list of course preferences.
    */
   public void setCoursePreferences (HashMap<Course, Integer> cps)
   {
      this.coursePreferences = cps;
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

   public String getBuilding ()
   {
      return office.getBuilding();
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

   public String getRoomNumber ()
   {
      return this.office.getRoom();
   }

   /**
    * @param firstName the firstName to set
    */
   public void setFirstName(String firstName)
   {
      this.firstName = firstName;
   }

   /**
    * @param lastName the lastName to set
    */
   public void setLastName(String lastName)
   {
      this.lastName = lastName;
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
    * @return the userID
    */
   public String getUserID()
   {
      return userID;
   }

   /**
    * @return the maxWtu
    */
   public Integer getMaxWtu()
   {
      return maxWtu;
   }

   /**
    * @return the tPrefs
    */
   public HashMap<Day, LinkedHashMap<Time, TimePreference>> gettPrefs()
   {
      return tPrefs;
   }

   /**
    * @return the itemsTaught
    */
   public Vector<ScheduleItem> getItemsTaught()
   {
      return itemsTaught;
   }

   /**
    * @param userID the userID to set
    */
   public void setUserID(String userID)
   {
      this.userID = userID;
   }

   /**
    * @param maxWtu the maxWtu to set
    */
   public void setMaxWtu(Integer maxWtu)
   {
      this.maxWtu = maxWtu;
   }

   /**
    * @param curWtu the curWtu to set
    */
   public void setCurWtu(Integer curWtu)
   {
      this.curWtu = curWtu;
   }

   /**
    * @param office the office to set
    */
   public void setOffice(Location office)
   {
      this.office = office;
   }

   /**
    * @param fairness the fairness to set
    */
   public void setFairness(Integer fairness)
   {
      this.fairness = fairness;
   }

   /**
    * @param disability the disability to set
    */
   public void setDisability(Boolean disability)
   {
      this.disability = disability;
   }

   /**
    * @param generosity the generosity to set
    */
   public void setGenerosity(int generosity)
   {
      this.generosity = generosity;
   }

   /**
    * @param tPrefs the tPrefs to set
    */
   public void settPrefs(HashMap<Day, LinkedHashMap<Time, TimePreference>> tPrefs)
   {
      this.tPrefs = tPrefs;
   }

   /**
    * @param itemsTaught the itemsTaught to set
    */
   public void setItemsTaught(Vector<ScheduleItem> itemsTaught)
   {
      this.itemsTaught = itemsTaught;
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
   public int getAvailableWTU ()
   {
      return this.getCurWtu();
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
    * Returns the scheduleId
    * 
    * @return the scheduleId
    */
   public Integer getScheduleId ()
   {
      return scheduleId;
   }

   /**
    * Sets the scheduleId to the given parameter.
    *
    * @param scheduleId the scheduleId to set
    */
   public void setScheduleId (Integer scheduleId)
   {
      this.scheduleId = scheduleId;
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
    * @param course the course for which to get the preference.
    * @return the course preference for the given course.
    */
   public int getPreference (Course course)
   {
      int desire = DEFAULT_PREF;

      Integer temp = coursePreferences.get(course);
      if (temp != null)
      {
         desire = temp;
      }

      return desire;
   }

   /**
    * This method will return the time preference from the list of preferences
    * for a given day and time. If the day in question does not yet have any
    * TPrefs, it will be populated with default values.
    * 
    * @param d the day the preference is from
    * @param t the time of the preference to get.
    * 
    * @return the preference (int) related to the given time on the given day
    * 
    * @throws NotADayException if "d" is not one of the days defined in
    *         generate.Week.java
    * 
    *         Written by: Eric Liebowitz
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
    * Returns the average desire an Instructor has for teaching a give length of
    * time on given days. If there is a preference of 0 found in this length of
    * Time for any day in the Week, 0 is returned immediately.
    * 
    * @param w The day(s) to check
    * @param tr Time range to check
    * 
    * @return the average desire for the given time span
    * 
    * @see #getAvgPrefForTimeRange (Week,Time,Time)
    */
   public double getAvgPrefForTimeRange (Week w, TimeRange tr)
   {
      return this.getAvgPrefForTimeRange(w, tr.getS(), tr.getE());
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
    * Sets the instructor as busy for a given set of days over a given
    * TimeRange.
    * 
    * @param days Days to book for
    * @param tr TimeRange to book across the days
    * 
    * @return True if the instructor was booked. False if he was already booked
    *         for any part of the time specified.
    */
   public boolean book (boolean b, Week days, TimeRange tr)
   {
      return this.availability.book(b, days, tr);
   }

   /**
    * This method will take in a day, start time, and end time and set that time
    * interval as busy for this instructor.
    * 
    * @param dayOfWeek the day of the week.
    * @param starttime the start time to set busy.
    * @param endtime the end time of the busy interval.
    */
   public boolean book (boolean b, Day dayOfWeek, Time starttime, Time endtime)
   {
      return this.availability.book(b, starttime, endtime, dayOfWeek);
   }

   /**
    * @return the availability
    */
   public WeekAvail getAvailability ()
   {
      return availability;
   }

   /**
    * @param availability the availability to set
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
    * Checks to see if an instructor can teach the given course. Checks
    * available WTUs and if their teaching preference is not 0.
    * 
    * @param course Course instructor might teach
    * 
    * @return A list of time ranges that instructor can and wants to teach this
    *         course
    */
   public boolean canTeach (Course course)
   {
      // Check if instructor has enough WTUs
      if ((this.curWtu + course.getWtu()) <= this.maxWtu)
      {
         /*
          * TODO: rewrite this when CoursePreference is changed to a hash. Note
          * that you'll not need to change this...just change the method
          * "getPreference"
          */
         if (getPreference(course) > 0)
         {
            return true;
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
    * 
    * @deprecated Shouldn't be needed
    */
   public Vector<TimeRange> getTeachingTimes (Course c)
   {
      Vector<TimeRange> r = new Vector<TimeRange>();
      int length = c.getLength();

      for (Time t : Time.ALL_TIMES_IN_DAY)
      {
         TimeRange tr = new TimeRange(t, length);
         double pref = this.getAvgPrefForTimeRange(c.getDays(), tr.getS(),
            tr.getE());
         if (pref > 0 && this.isAvailable(c.getDays(), tr))
         {
            r.add(tr);
         }
      }

      return r;
   }

   /**
    * Adds the given ScheduleItem as an item to be taught by this instructor. 
    * This item is not verified.
    * 
    * @param si ScheduleItem to add to this instructor's list o' stuff
    * 
    * @return True if this item currently isn't held within this instructor. 
    *         False otherwise
    */
   public boolean addItem (ScheduleItem si)
   {
      boolean r = false;

      if (!this.itemsTaught.contains(si))
      {
         Course c = si.getCourse();
         if (canTeach(c))
         {
            this.curWtu += c.getWtu();
            r = true;
         }
      }

      return r;
   }

   /**
    * Verifies that the vital fields of this Object  (i.e. those essential 
    * for generation of identification in a DB) are not null. "Vital" fields
    * are as follows:
    * 
    * <ul>
    *    <li>coursePreferences</li>
    *    <li>curWtu</li>
    *    <li>maxWtu</li>
    *    <li>office</li>
    *    <li>quarterId</li>
    *    <li>scheduleId</li>
    *    <li>tPrefs</li>
    *    <li>userID</li>
    * </ul>
    * 
    * @throws NullDataException if any field vital to generation or storage is
    *         null
    *
    * @see edu.calpoly.csc.scheduler.model.db.DbData#verify()
    */
   public void verify () throws NullDataException 
   {
      if (coursePreferences   == null)
    	  throw new NullDataException();
      if (curWtu              == null)
    	  throw new NullDataException();
	  if (maxWtu              == null)
		  throw new NullDataException();
	  if (office              == null)
		  throw new NullDataException();
	  if (quarterId           == null)
		  throw new NullDataException();
      if (tPrefs              == null)
    	  throw new NullDataException();
      if (userID              == null)
    	  throw new NullDataException();
   }
   
   public Instructor getCannedData()
   {
      Instructor i = new Instructor();
      i.setFirstName("Lim");
      i.setLastName("Yo-Hwan");
      i.setUserID("SlayerS_BoxeR");
      i.setMaxWtu(60);
      i.setCurWtu(0);
      i.setOffice(new Location());
      i.setFairness(1);
      i.setDisability(false);
      i.setGenerosity(1);
      i.setAvailability(new WeekAvail());
      i.setCoursePreferences(coursePreferences);
      i.settPrefs(new HashMap<Day, LinkedHashMap<Time, TimePreference>>());
      i.setItemsTaught(itemsTaught);
      i.setQuarterId("w2011");
      i.setScheduleId(1);
      return i;
   }
}

