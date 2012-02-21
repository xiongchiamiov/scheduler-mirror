package edu.calpoly.csc.scheduler.model.schedule;

import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import edu.calpoly.csc.scheduler.model.db.Time;
import edu.calpoly.csc.scheduler.model.db.TimeRange;
import edu.calpoly.csc.scheduler.model.db.cdb.Course;
import edu.calpoly.csc.scheduler.model.db.idb.Instructor;
import edu.calpoly.csc.scheduler.model.db.idb.Staff;
import edu.calpoly.csc.scheduler.model.db.ldb.Location;
import edu.calpoly.csc.scheduler.model.db.ldb.Tba;
import edu.calpoly.csc.scheduler.model.schedule.CouldNotBeScheduledException.ConflictType;

public class Generate {
	
	/**
	 * Used for debugging. Toggle it to get debugging output
	 */
	 private static final boolean DEBUG = !true; // !true == false ; )
	   
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

	public static Vector<ScheduleItem> generate(List<Course> c_list, List<Instructor> i_list, List<Location> l_list) {
		Vector<ScheduleItem> items = new Vector<ScheduleItem>();
		HashMap<Integer, SectionTracker> sections = new HashMap<Integer, SectionTracker>();
		TimeRange bounds = new TimeRange(new Time(7, 0), new Time(22, 0));
		TimeRange lec_bounds = new TimeRange(bounds);
		TimeRange lab_bounds = new TimeRange(bounds);
	      
	    ScheduleDecorator sd = new ScheduleDecorator();
	    sd.constructMaps(i_list, l_list);

	    debug("GENERATING");
	    debug("COURSES: " + c_list);
	    debug("INSTRUCTORS: " + i_list);
	    debug("LOCATIONS: " + l_list);
	      
	    //Generate labs from the course list
	      HashMap<Integer, Course> labList = new HashMap<Integer, Course>();
	      for (Course c : c_list) {
	    	//Is a lab (or until a case found otherwise, an ACT or DIS) associated with a lecture
	    	  if(c.getType() == Course.CourseType.LAB || c.getType() == Course.CourseType.ACT ||
	    			  c.getType() == Course.CourseType.DIS) { 
	    		  
	    		  debug ("Found act/dis/lab: " + c.getType() + " " + c.getCatalogNum() + " " + c.getName());
	    		  labList.put(c.getLectureID(), c);
	    	  }
	    	  // IND/SEM treated as lectures until found otherwise     	  
	      }

	      for (Course c : c_list)
	      {
	           if(c.getType() == Course.CourseType.LEC ||c.getType() == Course.CourseType.IND ||
	        		   c.getType() == Course.CourseType.SEM) {
	                debug ("MAKING SI's FOR COURSE " + c);

	                ScheduleItem lec_si = null;
	                
	                SectionTracker st = getSectionTracker(c, sections);
	                for (int i = 0; i < c.getNumOfSections(); i ++)
	                {
	                     debug ("SECTIONS SCHEDULED: " + st.getCurSection()
	                        + " / " + c.getNumOfSections());
	            
	                     lec_si = genLecItem(c, sd, lec_bounds, i_list, l_list);
	                     debug ("MADE LEC_SI\n" + lec_si);
	                     try
	                     {
	                          add(lec_si, sd, items, sections);
	                          debug ("ADDED IT");
	                     }
	                     catch (CouldNotBeScheduledException e)
	                     {
	                          System.err.println("GENERATION MADE A BAD LEC");
	                          System.err.println(lec_si);
	                     }
	                //}
	                
	                debug ("Done with scheduling LECTURE");
	                
	                if(labList.containsKey(c.getDbid())) { //Have a lab or labs that we need to schedule
	                	debug ("Found lab/act/dis for " + c.toString());
	                	
	                	Course lab = labList.get(c.getDbid());
	                	
	                	debug ("Now scheduling labs/act/dis for " + c.toString());
	                	
	                	/*st = getSectionTracker(lab);
	                    for (int i = 0; i < c.getNumOfSections(); i ++)
	                    {*/
	                	    ScheduleItem lab_si = genLabItem(lab, lec_si, sd, lab_bounds, i_list, l_list);
	                        try
	                        {                           
	                           add(lab_si, sd, items, sections);
	                           lec_si.addLab(lab_si);
	                        }
	                        catch (CouldNotBeScheduledException e)
	                        {
	                           System.err.println("GENERATION MADE A BAD LAB");
	                           System.err.println(lab_si);
	                        }
	                        debug ("The lab enrollment is: " + Integer.toString(lab_si.getCourse().getEnrollment()));
	                    //}
	                }
	                }
	           }
	      }

	      debug ("GENERATION FINISHED W/: " + items.size());
	      
	      return items;
	}
	
	/**
	    * Gets the SectionTracker associated with course 'c'. If no tracker yet
	    * exists for the Course, one is created and added.
	    * 
	    * @param c Course to get the tracker for
	    * 
	    * @return this.sections.get(c);
	    */
	   private static SectionTracker getSectionTracker (Course c, HashMap<Integer, SectionTracker> sections)
	   {
	      if (!sections.containsKey(c.getDbid()))
	      {
	         sections.put(c.getDbid(), new SectionTracker(c));
	      }
	      return sections.get(c.getDbid());
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
	   private static ScheduleItem genLecItem (Course lec, ScheduleDecorator sd, TimeRange lec_bounds, List<Instructor> i_list, List<Location> l_list)
	   {
	      ScheduleItem lec_si = new ScheduleItem();

	      lec_si.setCourse(lec);
	      lec_si.setInstructor(findInstructor(lec, sd, i_list));

	      return genBestTime(lec_si, lec_bounds, sd, i_list, l_list);
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
	   private static boolean add (ScheduleItem si, ScheduleDecorator sd, Vector<ScheduleItem> items, HashMap<Integer, SectionTracker> sections) throws CouldNotBeScheduledException
	   {
	      boolean r;
	      /*
	       * Verification checks the ScheduleItem and its lab component (if
	       * applicable) all in one go.
	       */
	      if (r = verify(si, sd))
	      {
	         book(si, sd, items, sections);
	      }

	      return r;
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
	   private static ScheduleItem genLabItem (Course lab, ScheduleItem lec_si, ScheduleDecorator sd, TimeRange lab_bounds, List<Instructor> i_list, List<Location> l_list)
	   {
	      ScheduleItem lab_si = new ScheduleItem();

	      lab_si.setCourse(lab);
	      lab_si.setInstructor(getLabInstructor(lab, lec_si));

	      TimeRange tr = lab_bounds;
	      
	      if (lab.getTetheredToLecture())
	      {
	    	 debug ("Found tethered lab for " + lab.getDept() + " " + lab.getCatalogNum());
	         tr = new TimeRange(lec_si.getEnd(), lab.getDayLength());
	      }

	      return genBestTime(lab_si, tr, sd, i_list, l_list);
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
	   private static Instructor findInstructor (Course c, ScheduleDecorator sd, List<Instructor> i_list)
	   {
	      return findInstructor(c, null, sd, i_list);
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
	   private static Instructor findInstructor (Course c, List<Instructor> doNotPick, ScheduleDecorator sd, List<Instructor> i_list)
	   {
	      Instructor r = Staff.getStaff();
	      int curMaxPref = 0;
	      
	      debug ("FINDING INSTRUCTOR FOR " + c);
	      debug ("EXLUDING: " + doNotPick);
	      for (Instructor i : i_list)
	      {
	         debug ("CONSIDERING " + i);
	         if (doNotPick == null || !doNotPick.contains(i))
	         {
	            debug ("NOT EXCLUDED");
	            if (i.canTeach(c, sd.getCurWTU(i)))
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
	    * @see #findTimes(ScheduleItem, TimeRange)
	    */
	   private static ScheduleItem genBestTime (ScheduleItem base, TimeRange tr, ScheduleDecorator sd, List<Instructor> i_list, List<Location> l_list)
	   {
	      Vector<ScheduleItem> si_list = new Vector<ScheduleItem>();

	      /*
	       * If we can't find times for the instructor in our base, we'll have to 
	       * try other instructors until we find one w/ at least one time he
	       * can teach
	       */
	      si_list = findTimes(base, tr, sd);
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
	            Instructor i = findInstructor(c, haveTried, sd, i_list);
	            
	            debug ("NO TIMES FOUND FOR " + base.getInstructor());
	            debug ("TRYING " + i);
	            
	            clone.setInstructor(i);
	            si_list = findTimes(clone, tr, sd);
	            haveTried.add(i);
	            
	         } while (si_list.isEmpty());
	      }
	      
	      debug("GOT " + si_list.size() + " TIMES");
	      
	      si_list = findLocations(si_list, sd, l_list);
	      debug("GOT " + si_list.size() + " LOCATIONS");

	      /*
	       * The map will prune out items which are impossible. Note that there will
	       * always be at least one location available: TBA
	       */
	      //SiMap sortedItems = new SiMap(si_list);

	      return new SiMap(si_list).getBest();
	   }
	   
	   /**
	    * Ensures that the given ScheduleItem can be scheduled. This means it
	    * doesn't double book instructors/locations, and the instructor can teach
	    * the lecture/lab w/o exceeding his max wtu limit. The instructor must also
	    * be able to teach during the times specified.<br>
	    * <br>
	    * If this method returns true, it is safe to call 'book(si)'. (In fact, 
	    * it'll always return true unless an exception gets thrown, in which case
	    * it'll never get a chance to return at all).
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
	   private static boolean verify (ScheduleItem si, ScheduleDecorator sd) throws CouldNotBeScheduledException
	   {
	      Week days = si.getDays();
	      Course c = si.getCourse();
	      TimeRange tr = si.getTimeRange();
	      Instructor i = si.getInstructor();
	      Location l = si.getLocation();

	      if (!i.isAvailable(days, tr, sd))
	      {
	         throw new CouldNotBeScheduledException(ConflictType.I_DBL_BK, si);
	      }
	      if (!l.isAvailable(days, tr, sd))
	      {
	         throw new CouldNotBeScheduledException(ConflictType.L_DBL_BK, si);
	      }
	      if (i.getAvgPrefForTimeRange(days, tr) == 0)
	      {
	         throw new CouldNotBeScheduledException(ConflictType.NO_DESIRE, si);
	      }
	      if (!i.canTeach(c, sd.getCurWTU(i)))
	      {
	         throw new CouldNotBeScheduledException(ConflictType.CANNOT_TEACH, si);
	      }

	      return true;
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
	   private static void book (ScheduleItem si, ScheduleDecorator sd, Vector<ScheduleItem> items, HashMap<Integer, SectionTracker> sections)
	   {
	      Instructor i = si.getInstructor();
	      Location l = si.getLocation();
	      Week days = si.getDays();
	      TimeRange tr = si.getTimeRange();

	      debug ("BOOKING");
	      
	      i.book(true, days, tr, sd);
	      l.book(true, days, tr, sd);

	      sd.addWTU(i, si.getWtuTotal());

	      SectionTracker st = getSectionTracker(si.getCourse(), sections);
	      st.addSection();
	      si.setSection(st.getCurSection());
	      
	      items.add(si);
	      
	      debug ("JUST ADDED SECTION " + st.getCurSection() + " OF " + 
	         si.getCourse());
	      debug ("ITEM COUNT AT : " + items.size());
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
	   private static Instructor getLabInstructor (Course lab, ScheduleItem lec_si)
	   {
	      /*Instructor r;

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
	      return r;*/
		   
		   return lec_si.getInstructor();
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
	   private static Vector<ScheduleItem> findTimes (ScheduleItem si, TimeRange range, ScheduleDecorator sd)
	   {
	      debug("FINDING TIMES IN RANGE " + range);
	      Vector<ScheduleItem> sis = new Vector<ScheduleItem>();
	      Course c = si.getCourse();
	      Instructor i = si.getInstructor();
	      
	      TimeRange tr = new TimeRange(range.getS(), c.getDayLength());
	      for (; tr.getE().compareTo(range.getE()) < 1; tr.addHalf())
	      {
	         Set<Week> days = c.getDays();

	         debug("CONSIDERING TR: " + tr);
	         if (i.isAvailable(days.iterator().next(), tr, sd))
	         {
	            debug("AVAILABLE");
	            double pref;
	            if ((pref = i.getAvgPrefForTimeRange(days.iterator().next(), tr)) > 0)
	            {
	               debug("WANTS: " + pref);
	               ScheduleItem toAdd = si.clone();
	               toAdd.setDays(days.iterator().next());
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
	    * @return A list of locations which can be taught on the days for course 'c'
	    *         during at least the TimeRanges passed in.
	    */
	   private static Vector<ScheduleItem> findLocations (Vector<ScheduleItem> sis, ScheduleDecorator sd, List<Location> l_list)
	   {
		  //might have to look into TBA location for IND type courses
	      Vector<ScheduleItem> si_list = new Vector<ScheduleItem>();

	      debug ("HAVE " + sis.size() + " ITEMS FOR LOCATIONS TO TRY");
	      for (ScheduleItem si : sis)
	      {
	         Week days = si.getDays();
	         TimeRange tr = si.getTimeRange();
	         for (Location l : l_list)
	         {
	            debug ("TRYING LOCATION " + l + " with time " + tr);
	            if (l.isAvailable(days, tr, sd))
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
//	   public static ScheduleItem move (ScheduleItem si, Week days, Time s, ScheduleDecorator sd) 
//	      throws CouldNotBeScheduledException
//	   {
//	      ScheduleItem fresh_si = new ScheduleItem(si);
//	      if (this.remove(si, sd))
//	      {
//	         Course c = si.getCourse();
//	         
//	         TimeRange tr = new TimeRange(s, c.splitLengthOverDays(days.size()));
//	      
//	         fresh_si.setDays(days);
//	         fresh_si.setTimeRange(tr);
//	      
//	         add(fresh_si, sd);
//	         
//	         /*
//	          * If the lab for the SI was teathered, we need to move it to just 
//	          * after the fresh_si
//	          */
//	         assert(false);
//	         /*
//	          *  
//	         Lab lab = c.getLab();
//	         if (lab != null && lab.isTethered())
//	         {
//	            Time lab_s = tr.getE();
//	            for (ScheduleItem lab_si: si.getLabs())
//	            {
//	               move(lab_si, days, lab_s);
//	            }
//	         }*/
//	      }
//	      return fresh_si;
//	   }
	   
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
//	   public boolean remove (ScheduleItem si, ScheduleDecorator sd)
//	   {
//	      boolean r = false;
//	      if (this.items.contains(si))
//	      {
//	         r = true;
//	         
//	         Course c = si.getCourse();
//	         Instructor i = si.getInstructor();
//	         Location l = si.getLocation();
//	         Week days = si.getDays();
//	         TimeRange tr = si.getTimeRange();
//
//	         this.items.remove(si);
//	         i.book(false, days, tr, sd);
//	         l.book(false, days, tr, sd);
//
//	         sd.subtractWTU(i, si.getWtuTotal());
//
//	         SectionTracker st = getSectionTracker(c);
//	         st.removeSection(si.getSection());
//	         
//	         /*
//	          * Remove the labs only if they're teathered to the course
//	          */
//	         if (si.hasLabs())
//	         {
//	            if (c.getTetheredToLecture())
//	            {
//	               remove(si.getLabs(), sd);
//	            }
//	         }
//	      }
//	      return r;
//	   }
//	   
//	   private void remove (List<ScheduleItem> toRemove, ScheduleDecorator sd)
//	   {
//	      for (ScheduleItem si: toRemove)
//	      {
//	         remove(si, sd);
//	      }
//	   }
	   
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
//	   private TimeRange getLabTimeRange (Lab lab, ScheduleItem lec_si)
//	   {
//	      TimeRange tr;
//	      if (lab.isTethered())
//	      {
//	         /*
//	          * Find times directly after the lecture only
//	          */
//	         tr = new TimeRange(lec_si.getEnd(), lab.getLength());
//	      }
//	      /*
//	       * Otherwise, lab can go anywhere within schedule bounds
//	       */
//	      else
//	      {
//	         tr = this.bounds;
//	      }
//	      return tr;
//	   }
	   
	   /**
	    * Adds a ScheduleItem to our list of bad/conflicting ScheduleItems. The item
	    * will only be added if 1) It's actually a conflicting item and 2) It's not
	    * already present in our list of conflicting items
	    *  
	    * @param si Conflicting Item to add
	    * 
	    * @return true if the item was added to our list. False otherwise. 
	    */
//	   public boolean addConflictingItem (ScheduleItem si, ScheduleDecorator sd)
//	   {
//	      boolean isDirty = false;
//	      try
//	      {
//	         verify(si, sd);
//	      }
//	      catch (CouldNotBeScheduledException e)
//	      {
//	         isDirty = true;
//	      }
//	      
//	      boolean r = false;
//	      if (isDirty)
//	      {
//	         r = this.dirtyList.add(si);
//	      }
//	      
//	      return r;
//	   }
	   
	   /**
	    * Removes a ScheduleItem from our list of conflicting items. 
	    * 
	    * @param si ScheduleItem to remove from the list
	    * 
	    * @return true if the item actually existed in our list and was removed. 
	    *         False otherwise.
	    */
//	   public boolean removeConflictingItem (ScheduleItem si)
//	   {
//	      return this.dirtyList.remove(si);
//	   }
	   
	   /**
	    * Returns the list of conflicting items
	    * 
	    * @return the list of conflicting items.
	    */
//	   public HashSet<ScheduleItem> getDirtyList ()
//	   {
//	      return this.dirtyList;
//	   }
	   
	   /**
	    * Sets the lecture time bounds
	    * 
	    * @param tr Bounds you want lectures to be taught within
	    * 
	    * @return The old lec_bounds value
	    */
//	   private TimeRange setLecBounds (TimeRange tr)
//	   {
//	      TimeRange oldBounds = this.lec_bounds;
//	      this.lec_bounds = tr;
//	      return oldBounds;
//	   }
}
