package edu.calpoly.csc.scheduler.model.schedule;

import java.io.Serializable;
import java.util.*;

import edu.calpoly.csc.scheduler.model.db.*;
import edu.calpoly.csc.scheduler.model.db.cdb.*;
import edu.calpoly.csc.scheduler.model.db.idb.*;
import edu.calpoly.csc.scheduler.model.db.ldb.*;

/**
 * <h1>1: Scheduling Lectures</h1>
 * 
 * The scheduling algorithm follows a step-by-step process to schedule courses.
 * It is exhaustive and guarantees that all courses given to the algorithm will
 * be scheduled. The following sections walk you through the <b>exact</b> 
 * process the algorithm executes while scheduling. 
 * 
 * <h2>1.1: Choosing Course</h2>
 * 
 * Courses are not algorithmically "chosen". Instead, the algorithm simply loops
 * through every course "c" until all sections of "c" are scheduled. There is no
 * global "max" number of sections. Instead, the algorithm will schedule as many
 * sections of "c" as it has (see {@link Course#getNumOfSections()}).<br>
 * <br>
 * You'll want to check out {@link SectionTracker} to see how the algorithm 
 * records unique section numbers of a given course.<br>
 * <br>
 * 
 * <h2>1.2: Choosing Instructors</h2>
 * 
 * The method you'll want to see for this is {@link #findInstructor(Course)} and
 * {@link #findInstructor(Course, List)}.<br>
 * <br>
 * Instructors are chosen based on who most desires to teach a given course. If
 * the one who most wants to teach it no longer can (i.e. his WTU's are maxed), 
 * he'll be removed from consideration and more instructors will be tested.<br>
 * <br> 
 * If no Instructor can be found (no one is capable of teaching the Course, 
 * which can be b/c of preference or wtu reasons), the special {@link Staff} 
 * Instructor is used. This person always wants to teach a course and is always
 * available to teach at any time. This instructor is a last resort: it's only 
 * chosen if no one else is possible.
 * 
 * <h2>1.3: Choosing Times</h2>
 * 
 * Finding times depends on the type of course you're finding a time for. If 
 * you're scheduling a lecture course, it's easy. Lab courses, on the other 
 * hand, are a horse of a different color.<br>
 * <br>
 * Half-hours are the resolution used to schedule courses. So, if a given time
 * range doesn't work, the algorithm will shift the time range by a half hour
 * and try again. Course lengths must be split on half-hour boundaries: you 
 * can't have courses being taught for 1h45m or something.<br>
 * <br>
 * Times per day will be figured by dividing the Course's length across the days
 * it's to be taught (see {@link Course#getDayLength()}). Thus, a course taught
 * for 7 hours a week could be taught for: 7 hours, 1 day; 3.5 hours, 2 days. 
 * Other day combinations aren't possible, b/c they don't split 7 hours up into
 * equal numbers of half-hours per day.<br> 
 * <br>
 * Times are selected by picking the time range across the Course's days 
 * ({@link Course#getDays()}) for which the chosen instructor want to teach
 * the most; the {@link TimeRange} for which he has the highest average 
 * preference ({@link Instructor#getAvgPrefForTimeRange(Week, TimeRange)}). If
 * an instructor has a pref of '0' for any time slot within the TimeRange, 
 * the entire range will be disregarded.<br> 
 * <br>
 * 
 * <h3>1.3.1: Finding Lecture Times</h3>
 * 
 * The methods you'll want to see for this are 
 * {@link #genLecItem(Course)} and 
 * {@link #findTimes(ScheduleItem, TimeRange)}.<br>
 * <br>
 * Lecture times are found within the time bounds specified in the 
 * {@link #lec_bounds}. By default, this is 7a-10p. (My understanding of this is 
 * that this time is global throughout all Cal Poly). If you wish to change
 * this range, see {@link #setLecBounds(TimeRange)}, but be sure you put it
 * back to its old value when you're done.<br>
 * <br> 
 * 
 * <h2>1.4: Choosing Location</h2>
 * 
 * You'll want to see {@link #findLocations(Vector)} for details on
 * how this is implemented.<br>
 * <br>
 * Locations are chosen based on whether they're available for a given time and
 * whether they provide for the course to be taught. (Courses might need 
 * certain equipment only present in select rooms). That's it.<br>
 * <br>
 * Refer to the 'Future Design Suggestions/Options' section for how this 
 * implementation can/should change in the future.
 * 
 * <h2>1.5: Selecting the Best Item</h2>
 * 
 * Once a list of ScheduleItems have been created, each with unique times & 
 * locations, they are put into an {@link SiMap}. This map sorts the items 
 * according to their value defined in {@link ScheduleItem#getValue()} (this is
 * also how {@link ScheduleItem#compareTo(ScheduleItem)} compares things) and
 * {@link ScheduleItem#updateValue()}. Once all items have been added into the
 * sorted map, it's easy to pluck out the best one: the highest value will 
 * have floated to the top ({@link SiMap#getBest()}). 
 * 
 * <h1>2: Scheduling Labs</h1>
 * 
 * There's a difference in the type of labs which are scheduled. There are 
 * <b>teathered</b> and <b>unteathered</b> labs. <br>
 * <br>
 * Teathered labs cannot be dragged onto the schedule; teathered can be.<br>
 * <br>
 * If a lecture section is scheduled and that lecture has a lab, the lab will
 * be automatically scheduled right after the lecture. <b>This is a bug!</b> See
 * 
 * <h2>2.1: Teathered Labs</h2>
 * 
 * Teathered labs are always scheduled directly after the lecture they're 
 * paired with. For example, a 101 section taught MWF from 10a-11a would have 
 * its lab on MWF from 11a-12p. Basically, the end time of the lecture is the
 * start time of the lab.<br>
 * <br>
 * These labs should <b>not</b> be dragged and dropped onto the schedule. If you
 * do that, there's no lecture information, so the labs don't know where they're 
 * supposed to go. Thus, <b>the only way to schedule a teathered lab is to 
 * schedule its corresponding lecture component first</b>.
 * 
 * <h2>2.2: Unteathered Labs</h2>
 * 
 * As you might guess, unteathered labs don't have to come direclty after 
 * they're corresponding lectures. They can go on any day and be taught at any
 * time. Additionally, they can be dragged and dropped onto the schedule, b/c 
 * they don't need the info on their lecture components to place themselves on
 * the schedule.<br>
 * <br>
 * Be aware of an unusual quirk of scheduling these types of labs: if they are
 * dragged and dropped onto the schedule, the number of sections they contain
 * will be the number of sections scheduled. However, if you schedule their 
 * lecture component first, a different rule is used: lab sections will be 
 * scheduled until the amount of lab enrollment available meets or exceeds the
 * enrollment available from one section of a lecture. For example, a lecture 
 * section with 90 students would get 3 sections of lab with 30 students.<br>
 * <br>
 * A possible solution to this would be to not auto-schedule labs for lectures
 * by enrollment numbers unless they are teathered. Thus, a lecture containing
 * an unteathered lab would not start up lab scheduling...that lab would only 
 * be scheduling once it was encountered in the list of courses passed into 
 * {@link #generate(Collection)}. 
 * 
 * <h2>2.3: Tracking Lab Sections</h2> 
 * 
 * Regardless of the type of lab, the correct SectionTracker will be found, so
 * you'll always get good, unique section numbers for each lab.
 * 
 * <h2>2.4: Bug When Dragging Labs</h2>
 * 
 * Unteathered labs can be over-scheduled under the right conditions. 
 * 
 * <h3>Bug details</h3>
 * 
 * Imagine the following scenario:
 * 
 * <ul>
 *    <li>
 *       Lecture A (3 sections of 30 students) has Lab B (3 sections of 30 
 *       students). Note that the ratio of lectures to labs is 1:1
 *    </li>
 *    <li> 
 *       Lab B is unteathered.
 *    </li>
 *    <li>
 *       User drags Lab B onto the schedule; one section is scheduled.
 *    </li>
 *    <li>
 *       User does the above step twice more. At this point, there are 3 
 *       sections of B on the schedule. This is the amount that should be 
 *       scheduled.
 *    </li>
 *    <li>
 *       User drags A onto the schedule, or calls the scheduling algorithm with
 *       A in the list of courses to schedule
 *    </li>
 *    <li>
 *       When A is scheduled, the algorith will see that has lab B and schedule
 *       a section of it. At this point, there will be 4 sections of B when it
 *       was only supposed to have 3.  
 *    </li>
 * </ul>
 * 
 * The above situation schedules more sections of a lab than was necessary. This
 * is b/c enrollment info is not stored between instances of the scheduling 
 * algorithm running. 
 * 
 * <h3>Possible Sol'n</h3>
 * 
 * Put in a HashMap, keyed by Course objects, which yield total enrollment the
 * course provides. (So, 3 sections of a course with 35 students would have 
 * the value 105 stored under its enrollment info). This sol'n relies on the 
 * following, unwritten rule:<br>
 * <br>
 * 
 * <code>
 *  (# lecture sections * lecture enrollment) == 
 *  (# lab sections * lab enrollment)"
 * </code>
 * 
 * <br>
 * Basically, this sol'n requires a user to enter correct data. The
 * number of lab sections determined by how many sections of it are needed to 
 * meet the enrollment requirements imposed by the lecture component.<br>
 * <br>
 * To implement this fix, the View will have to notify the user when the number
 * of lab sections they <i>say</i> they want is inconsistent with the number
 * of sections that'll be computed from the above rule. If that rule is always
 * enforced and the algorithm can rely on this, I believe this bug can be fixed. 
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
   //private HashSet<ScheduleItem> dirtyList = new HashSet<ScheduleItem>();
   
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
   public Schedule (Collection<ScheduleItem> s_items, Collection<Instructor> collection, Collection<Location> collection2)
   {
      this.iSourceList = new Vector<Instructor>(collection);
      this.setlSourceList(collection2);
      items.addAll(s_items);
   }
   
   public void generateSchedule() {
	   items = Generate.generate(items, cSourceList, iSourceList, lSourceList);
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
      return getScheduleDBId();
   }

   /**
    * Sets the id to the given parameter.
    * 
    * @param id the id to set
    */
   public void setId (Integer id)
   {
      setScheduleDBId(id);
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
 
   public void verify ()
   {
      if (getScheduleDBId() ==  null)
      {
         throw new NullDataException();
      }
      if (name == null)
      {
         throw new NullDataException();
      }
   }
}