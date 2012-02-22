//package edu.calpoly.csc.scheduler.model.algorithm;
//
//import java.sql.Time;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Set;
//import java.util.Vector;
//
//import edu.calpoly.csc.scheduler.model.Course;
//import edu.calpoly.csc.scheduler.model.Instructor;
//import edu.calpoly.csc.scheduler.model.Location;
//import edu.calpoly.csc.scheduler.model.Model;
//import edu.calpoly.csc.scheduler.model.Schedule;
//import edu.calpoly.csc.scheduler.model.ScheduleItem;
//import edu.calpoly.csc.scheduler.model.algorithm.CouldNotBeScheduledException.ConflictType;
//
//public class Generate {
//	
//	/**
//	 * Used for debugging. Toggle it to get debugging output
//	 */
//	 private static final boolean DEBUG = !true; // !true == false ; )
//	   
//	/**
//	 * Prints a message to STDERR if DEBUG is true
//	 * 
//	 * @param s String to print
//	 */
//	private static void debug (String s)
//	{
//	   if (DEBUG)
//	   {
//	      System.err.println(s);
//	   }
//	}
//
//	public static Vector<ScheduleItem> generate(Model model, Schedule schedule, Vector<ScheduleItem> s_items, List<Course> c_list, List<Instructor> i_list, List<Location> l_list) {
//		Vector<ScheduleItem> items = s_items;
//		HashMap<Integer, SectionTracker> sections = new HashMap<Integer, SectionTracker>();
//		TimeRange bounds = new TimeRange(14, 44);
//		TimeRange lec_bounds = new TimeRange(bounds);
//		TimeRange lab_bounds = new TimeRange(bounds);
//	      
//	    ScheduleDecorator sd = new ScheduleDecorator();
//	    sd.constructMaps(i_list, l_list);
//
//	    debug("GENERATING");
//	    debug("COURSES: " + c_list);
//	    debug("INSTRUCTORS: " + i_list);
//	    debug("LOCATIONS: " + l_list);
//	      
//	    //Generate labs from the course list
//	      HashMap<Integer, Course> labList = new HashMap<Integer, Course>();
//	      for (Course c : c_list) {
//	    	//Is a lab (or until a case found otherwise, an ACT or DIS) associated with a lecture
//	    	  if(c.getTypeEnum() == Course.CourseType.LAB || c.getTypeEnum() == Course.CourseType.ACT ||
//	    			  c.getTypeEnum() == Course.CourseType.DIS) { 
//	    		  
//	    		  debug ("Found act/dis/lab: " + c.getTypeEnum() + " " + c.getCatalogNumber() + " " + c.getName());
//	    		  labList.put(c.getLectureID(), c);
//	    	  }
//	    	  // IND/SEM treated as lectures until found otherwise     	  
//	      }
//
//	      for (Course c : c_list)
//	      {
//	           if(c.getTypeEnum() == Course.CourseType.LEC ||c.getTypeEnum() == Course.CourseType.IND ||
//	        		   c.getTypeEnum() == Course.CourseType.SEM) {
//	                debug ("MAKING SI's FOR COURSE " + c);
//
//	                ScheduleItem lec_si = null;
//	                
//	                SectionTracker st = getSectionTracker(c, sections);
//	                for (int i = 0; i < c.getNumSectionsInt(); i ++)
//	                {
//	                     debug ("SECTIONS SCHEDULED: " + st.getCurSection()
//	                        + " / " + c.getNumSections());
//	            
//	                     lec_si = genLecItem(model, schedule, c, sd, lec_bounds, i_list, l_list);
//	                     debug ("MADE LEC_SI\n" + lec_si);
//	                     try
//	                     {
//	                          add(lec_si, sd, items, sections);
//	                          debug ("ADDED IT");
//	                     }
//	                     catch (CouldNotBeScheduledException e)
//	                     {
//	                          System.err.println("GENERATION MADE A BAD LEC");
//	                          System.err.println(lec_si);
//	                     }
//	                //}
//	                
//	                debug ("Done with scheduling LECTURE");
//	                
//	                if(labList.containsKey(c.getID())) { //Have a lab or labs that we need to schedule
//	                	debug ("Found lab/act/dis for " + c.toString());
//	                	
//	                	Course lab = labList.get(c.getID());
//	                	
//	                	debug ("Now scheduling labs/act/dis for " + c.toString());
//	                	
//	                	/*st = getSectionTracker(lab);
//	                    for (int i = 0; i < c.getNumOfSections(); i ++)
//	                    {*/
//	                	    ScheduleItem lab_si = genLabItem(model, schedule, lab, lec_si, sd, lab_bounds, i_list, l_list);
//	                        try
//	                        {
//	                           add(lab_si, sd, items, sections);
//	                           lec_si.getLabIDs().add(lab_si.getCourseID());
//	                        }
//	                        catch (CouldNotBeScheduledException e)
//	                        {
//	                           System.err.println("GENERATION MADE A BAD LAB");
//	                           System.err.println(lab_si);
//	                        }
////	                        debug ("The lab enrollment is: " + Integer.toString(lab_si.getCourse().getEnrollment()));
//	                    //}
//	                }
//	                }
//	           }
//	      }
//
//	      debug ("GENERATION FINISHED W/: " + items.size());
//	      
//	      return items;
//	}
//	
//	//public static add
//	
//	/**
//	    * Gets the SectionTracker associated with course 'c'. If no tracker yet
//	    * exists for the Course, one is created and added.
//	    * 
//	    * @param c Course to get the tracker for
//	    * 
//	    * @return this.sections.get(c);
//	    */
//	   private static SectionTracker getSectionTracker (Course c, HashMap<Integer, SectionTracker> sections)
//	   {
//	      if (!sections.containsKey(c.getID()))
//	      {
//	         sections.put(c.getID(), new SectionTracker(c));
//	      }
//	      return sections.get(c.getID());
//	   }
//	   
//	   /**
//	    * Creates a lecture ScheduleItem for the given course. Finds an instructor
//	    * for the course, and subsequently finds times and locations which that
//	    * instructor wants to teach for.<br>
//	    * <br>
//	    * In the case that no instructor or location can be found, Staff and Tba
//	    * will be used to make sure generation can continue.
//	    * 
//	    * @param lec Course to schedule
//	    * 
//	    * @return A ScheduleItem which is safe to add to the schedule
//	    * 
//	    * @see Tba#getTba()
//	    * @see Staff#getStaff()
//	    */
//	   private static ScheduleItem genLecItem (Model model, Schedule schedule, Course lec, ScheduleDecorator sd, TimeRange lec_bounds, List<Instructor> i_list, List<Location> l_list)
//	   {
//	      ScheduleItem lec_si = model.assembleScheduleItem(schedule, lec, findInstructor(lec, sd, i_list), null, 0, null, 0, 0, false, false);
//
//	      return genBestTime(model, schedule, lec_si, lec_bounds, sd, i_list, l_list);
//	   }
//	   
//	   /**
//	    * Adds the given ScheduleItem to this schedule. Before an add is done, the
//	    * ScheduleItem is verified to make sure it doesn't double-book an
//	    * instructor/location and that the instructor can actually teach the course.
//	    * 
//	    * @param si ScheduleItem to add
//	    * 
//	    * @return true if the item was added. False otherwise.
//	    */
//	   private static boolean add (ScheduleItem si, ScheduleDecorator sd, Vector<ScheduleItem> items, HashMap<Integer, SectionTracker> sections) throws CouldNotBeScheduledException
//	   {
//	      boolean r;
//	      /*
//	       * Verification checks the ScheduleItem and its lab component (if
//	       * applicable) all in one go.
//	       */
//	      if (r = verify(si, sd))
//	      {
//	         book(si, sd, items, sections);
//	      }
//
//	      return r;
//	   }
//	   
//	   /**
//	    * Creates a ScheduleItem for the given lab. The 'lec_si' is provided in case
//	    * the lab is tied to the lecture in any particular way. If it is, we can
//	    * easily access its data at this point.<br>
//	    * <br>
//	    * In the case that no instructor or location can be found, Staff and Tba
//	    * will be used to make sure generation can continue.
//	    * 
//	    * @param lab Lab to schedule
//	    * @param lec_si Lecture ScheduleItem which holds information which lab
//	    *        scheduling might need
//	    * 
//	    * @return A ScheduleItem for 'lab' which is safe to add to the schedule
//	    * 
//	    * @see Tba#getTba()
//	    * @see Staff#getStaff()
//	    */
//	   private static ScheduleItem genLabItem (Model model, Schedule schedule, Course lab, ScheduleItem lec_si, ScheduleDecorator sd, TimeRange lab_bounds, List<Instructor> i_list, List<Location> l_list)
//	   {
//	      ScheduleItem lab_si = model.assembleScheduleItem(schedule, lab, getLabInstructor(lab, lec_si), null, 0, null, 0, 0, false, false);
//	      
//	      TimeRange tr = lab_bounds;
//	      
//	      if (lab.isTetheredToLecture())
//	      {
////	    	 debug ("Found tethered lab for " + lab.getDept() + " " + lab.getCatalogNumber());
//	         tr = new TimeRange(lec_si.getEndHalfHour(), lab.getNumHalfHoursPerWeekInt() / lab_si.getDays().size());
//	      }
//
//	      return genBestTime(model, schedule, lab_si, tr, sd, i_list, l_list);
//	   }
//	   
//	   /**
//	    * Finds an instructor who wants to teach a given course.
//	    * 
//	    * @param c Course to find an instructor for
//	    * 
//	    * @return An instructor who can and wants to teach the course. If no
//	    *         instructor can be found, Staff.getStaff is returned
//	    * 
//	    * @see Staff#getStaff()
//	    */
//	   private static Instructor findInstructor (Course c, ScheduleDecorator sd, List<Instructor> i_list)
//	   {
//	      return findInstructor(c, null, sd, i_list);
//	   }
//	   
//	   static Instructor getStaff() { return null; }
//	   
//	   /**
//	    * Finds an instructor for the given course, excluding any instructors 
//	    * in a given list
//	    * 
//	    * @param c Course to find an instructor for
//	    * @param doNotPick List of instructor to <b>not</b> choose
//	    * 
//	    * @return An intsructor to teach 'c'. This can be STAFF if no instructor
//	    *         if capable of teaching
//	    */
//	   private static Instructor findInstructor (Course c, List<Instructor> doNotPick, ScheduleDecorator sd, List<Instructor> i_list)
//	   {
//	      Instructor r = getStaff();
//	      int curMaxPref = 0;
//	      
//	      debug ("FINDING INSTRUCTOR FOR " + c);
//	      debug ("EXLUDING: " + doNotPick);
//	      for (Instructor i : i_list)
//	      {
//	         debug ("CONSIDERING " + i);
//	         if (doNotPick == null || !doNotPick.contains(i))
//	         {
//	            debug ("NOT EXCLUDED");
//	            if (canTeach(i, c, sd.getCurWTU(i)))
//	            {
//	               debug ("CAN");
//	               int pref = getPreference(i, c);
//	               debug ("DESIRE: " + pref);
//	               if (pref > curMaxPref)
//	               {
//	                  debug ("WANTS MORE: " + pref + " > " + curMaxPref);
//	                  r = i;
//	                  curMaxPref = pref;
//	               }
//	            }
//	         }
//	      }
//	      
//	      if (r.equals(getStaff()))
//	      {
//	         debug ("NOBODY FOUND. GOING WITH STAFF");
//	      }
//	      
//	      return r;
//	   }
//	   
//	   /**
//	    * Generates a ScheduleItem for a given instructor. Guarantees that no other
//	    * ScheduleItem could be created which this instructor would want <b>more</b>
//	    * than this one.<br>
//	    * <br> 
//	    * If there is no way that our 'base' ScheduleItem's instructor can teach
//	    * at any time in the supplied time range, this will change the considered
//	    * instructor to STAFF. Consequently, <i>this method guarantees that a valid
//	    * ScheduleItem will be generated</i>. It is <b>not</b> guaranteed that the
//	    * instructor will be the one in 'base', nor is it guaranteed the location 
//	    * will not be TBA.
//	    * 
//	    * @param base ScheduleItem containing basic info for generating. In 
//	    *        particular, the instructor and course must already be defined
//	    * @param tr TimeRange we'll look within when scheduling
//	    * 
//	    * @return A ScheduleItem which this instructor wants to teach at least as
//	    *         much as every other ScheduleItem that might be produced
//	    * 
//	    * @see SiMap#put(ScheduleItem)
//	    * @see ScheduleItem#getValue()
//	    * @see Staff#getStaff()
//	    * @see Tba#getTba()
//	    * @see #findTimes(ScheduleItem, TimeRange)
//	    */
//	   private static ScheduleItem genBestTime (Model model, Schedule schedule, ScheduleItem base, TimeRange tr, ScheduleDecorator sd, List<Instructor> i_list, List<Location> l_list)
//	   {
//	      Vector<ScheduleItemDecorator> si_list = new Vector<ScheduleItemDecorator>();
//
//	      /*
//	       * If we can't find times for the instructor in our base, we'll have to 
//	       * try other instructors until we find one w/ at least one time he
//	       * can teach
//	       */
//	      si_list = findTimes(base, tr, sd);
//	      if (si_list.isEmpty())
//	      {
//	         ScheduleItem clone = model.assembleScheduleItemCopy(base);
//	         Course c = model.findCourseByID(base.getCourseID());
//	         
//	         /*
//	          * Keep track of instructors we've tried so we don't use them again. 
//	          * Eventually, if none are found, we'll end up using STAFF, which is
//	          * eager to please.
//	          */
//	         Vector<Integer> haveTriedInstructorIDs = new Vector<Integer>();
//	         haveTriedInstructorIDs.add(clone.getInstructorID());
//	         do
//	         {
//	            Instructor i = findInstructor(c, haveTriedInstructorIDs, sd, i_list);
//	            
////	            debug ("NO TIMES FOUND FOR " + base.getInstructor());
////	            debug ("TRYING " + i);
//	            
//	            clone.setInstructorID(i.getID());
//	            si_list = findTimes(clone, tr, sd);
//	            haveTriedInstructorIDs.add(i.getID());
//	            
//	         } while (si_list.isEmpty());
//	      }
//	      
//	      debug("GOT " + si_list.size() + " TIMES");
//	      
//	      si_list = findLocations(si_list, sd, l_list);
//	      debug("GOT " + si_list.size() + " LOCATIONS");
//
//	      /*
//	       * The map will prune out items which are impossible. Note that there will
//	       * always be at least one location available: TBA
//	       */
//	      //SiMap sortedItems = new SiMap(si_list);
//
//	      return new SiMap(si_list).getBest().item;
//	   }
//	   
//	   /**
//	    * Ensures that the given ScheduleItem can be scheduled. This means it
//	    * doesn't double book instructors/locations, and the instructor can teach
//	    * the lecture/lab w/o exceeding his max wtu limit. The instructor must also
//	    * be able to teach during the times specified.<br>
//	    * <br>
//	    * If this method returns true, it is safe to call 'book(si)'. (In fact, 
//	    * it'll always return true unless an exception gets thrown, in which case
//	    * it'll never get a chance to return at all).
//	    * 
//	    * @param si ScheduleItem to verify
//	    * 
//	    * @return true if 'si' can be taught by its instructor, at its location, 
//	    *         and the instructor can teach the course.
//	    * 
//	    * @throws CouldNotBeScheduledException if a time/location conflict is 
//	    *         encountered, the instructor cannot teach during the given times, 
//	    *         the instructor cannot teach the course, or if no more sections of
//	    *         the given course can be taught 
//	    * 
//	    * @see Instructor#canTeach(Course)
//	    */
//	   private static boolean verify (ScheduleItem si, ScheduleDecorator sd) throws CouldNotBeScheduledException
//	   {
//	      Week days = si.getDays();
//	      Course c = si.getCourse();
//	      TimeRange tr = si.getTimeRange();
//	      Instructor i = si.getInstructor();
//	      Location l = si.getLocation();
//
//	      if (!isAvailable(i, days, tr, sd))
//	      {
//	         throw new CouldNotBeScheduledException(ConflictType.I_DBL_BK, si);
//	      }
//	      if (!isAvailable(i, days, tr, sd))
//	      {
//	         throw new CouldNotBeScheduledException(ConflictType.L_DBL_BK, si);
//	      }
//	      if (getAvgPrefForTimeRange(i, days, tr) == 0)
//	      {
//	         throw new CouldNotBeScheduledException(ConflictType.NO_DESIRE, si);
//	      }
//	      if (!canTeach(i, c, sd.getCurWTU(i)))
//	      {
//	         throw new CouldNotBeScheduledException(ConflictType.CANNOT_TEACH, si);
//	      }
//
//	      return true;
//	   }
//	   
//	   /**
//	    * Applies all the day/time/wtu commitments of a ScheduleItem to instructors
//	    * and locations to take up their availability. Instructor's WTU count is
//	    * updated. Course section count is also updated.<br>
//	    * <br>
//	    * <b>Note:</b> The ScheduleItem is not verified here! You must call verify
//	    * before booking to ensure it's safe to do so.
//	    * 
//	    * @param si The ScheduleItem w/ the days, times, etc. which'll be booked in
//	    *        the schedule
//	    * 
//	    * @see #verify(ScheduleItem)
//	    */
//	   private static void book (ScheduleItem si, ScheduleDecorator sd, Vector<ScheduleItem> items, HashMap<Integer, SectionTracker> sections)
//	   {
//	      Instructor i = si.getInstructor();
//	      Location l = si.getLocation();
//	      Week days = si.getDays();
//	      TimeRange tr = si.getTimeRange();
//
//	      debug ("BOOKING");
//	      
//	      i.book(true, days, tr, sd);
//	      l.book(true, days, tr, sd);
//
//	      sd.addWTU(i, si.getWtuTotal());
//
//	      SectionTracker st = getSectionTracker(si.getCourse(), sections);
//	      st.addSection();
//	      si.setSection(st.getCurSection());
//	      
//	      items.add(si);
//	      
////	      debug ("JUST ADDED SECTION " + st.getCurSection() + " OF " + 
//	         si.getCourse());
//	      debug ("ITEM COUNT AT : " + items.size());
//	   }
//	   
//	   /**
//	    * Returns an instructor to teach a given lab. If the lab is told to use the
//	    * same instructor as its lecture, that instructor is used. Otherwise, a new
//	    * instructor will be found who wants to teach the lab.
//	    * 
//	    * @param lab Lab we're finding an instructor for
//	    * @param lec_si Schedule information for the lecture component. Used to
//	    *        extract the instructor whose teaching the lecture in the case where
//	    *        the lab is teathered to the lecture.
//	    * 
//	    * @return An instructor to teach the lab. If the lab is teathered, this will
//	    *         be the same instructor returned by 'lec_si.getInstructor'. If no
//	    *         instructor is able to teach, STAFF is returned
//	    * 
//	    * @see Staff#getStaff()
//	    */
//	   private static Instructor getLabInstructor (Course lab, ScheduleItem lec_si)
//	   {
//	      /*Instructor r;
//
//	      if (!lab.shouldUseLectureInstructor())
//	      {
//	         r = findInstructor(lab);
//	      }
//	      else
//	      {
//	         Instructor i = lec_si.getInstructor();
//	         if (i.canTeach(lab))
//	         {
//	            r = i;
//	         }
//	         else
//	         {
//	            r = Staff.getStaff();
//	         }
//	      }
//	      return r;*/
//		   
//		   return lec_si.getInstructor();
//	   }
//	   
//	   /**
//	    * Gets all the possible time ranges that an instructor can teach a given
//	    * course. The days that are considered for teaching are the days defined by
//	    * the course to be taught. Each ScheduleItem returned is a clone of the
//	    * single ScheduleItem passed in, so that all the fields in the returned list
//	    * will be the same <b>except</b> for their times.
//	    * 
//	    * @param si ScheduleItem w/ the course and instructor we're to use in
//	    *        computing times to consider. This ScheduleItem is cloned for every
//	    *        time range returned.
//	    * 
//	    * @return A list of ScheduleItems w/ their days and time ranges set to times
//	    *         which the instructor wants to/can teach and on the days the Course
//	    *         is to be taught on.
//	    */
//	   private static Vector<ScheduleItemDecorator> findTimes (Model model, Schedule schedule, ScheduleItem si, TimeRange range, ScheduleDecorator sd)
//	   {
//	      debug("FINDING TIMES IN RANGE " + range);
//	      Vector<ScheduleItem> sis = new Vector<ScheduleItem>();
//	      Course c = model.findCourseByID(si.getCourseID());
//	      Instructor i = model.findInstructorByID(si.getInstructorID());
//	      
//	      TimeRange tr = new TimeRange(range.getS(), getDayLength(c));
//	      for (; tr.getE() < range.getE(); tr.addHalf())
//	      {
//	         Set<Week> days = null;// c.getDays();
//	         assert(false); // there are many combinations, what do we do here?
//	         
//
//	         debug("CONSIDERING TR: " + tr);
//	         if (isAvailable(i, days.iterator().next(), tr, sd))
//	         {
//	            debug("AVAILABLE");
//	            double pref;
//	            if ((pref = getAvgPrefForTimeRange(i, days.iterator().next(), tr)) > 0)
//	            {
//	               debug("WANTS: " + pref);
//	               ScheduleItem toAdd = model.assembleScheduleItemCopy(si);
//	               toAdd.setDays(days.iterator().next().getDays());
//	               toAdd.setStartHalfHour(tr.getS());
//	               toAdd.setEndHalfHour(tr.getE());
//
//	               sis.add(toAdd);
//	            }
//	         }
//	      }
//
//	      return sis;
//	   }
//	   
//	   private static int getDayLength(Course c) {
//		   assert(false); // totally just made this up. we dont have the concept of day length anymore...
//		return c.getNumHalfHoursPerWeekInt() / c.getDayPatterns().iterator().next().size();
//	}
//
//	/**
//	    * Finds all locations which are compatible with the given Course and are
//	    * available for any of the given list of TimeRanges.
//	    * 
//	    * @param sis List of ScheduleItems with their instructor, course, days, and
//	    *        times fields already set.
//	    * 
//	    * @return A list of locations which can be taught on the days for course 'c'
//	    *         during at least the TimeRanges passed in.
//	    */
//	   private static Vector<ScheduleItem> findLocations (Model model, Schedule schedule, Vector<ScheduleItem> sis, ScheduleDecorator sd, List<Location> l_list)
//	   {
//		  //might have to look into TBA location for IND type courses
//	      Vector<ScheduleItem> si_list = new Vector<ScheduleItem>();
//
//	      debug ("HAVE " + sis.size() + " ITEMS FOR LOCATIONS TO TRY");
//	      for (ScheduleItem si : sis)
//	      {
//	         Week days = new Week(si.getDays());
//	         TimeRange tr = getTimeRange(si);
//	         for (Location l : l_list)
//	         {
//	            debug ("TRYING LOCATION " + l + " with time " + tr);
//	            if (isAvailable(i, days, tr, sd))
//	            {
//	               if (l.providesFor(model.findCourseByID(si.getCourseID())))
//	               {
//	                  /*
//	                   * I clone so we don't keep changing the same object...that'd
//	                   * be pretty bad.
//	                   */
//	                  ScheduleItem base = model.assembleScheduleItemCopy(si);
//	                  base.setLocationID(l.getID());
//
//	                  si_list.add(base);
//	               }
//	            }
//	         }
//	      }
//
//	      return si_list;
//	   }
//	   
//	   /**
//	    * Moves an already-existing ScheduleItem from one place on the schedule 
//	    * to another. If this ScheduleItem has teathered lab ScheduleItems attached
//	    * to it, those labs will be moved as well. 
//	    * 
//	    * @param si ScheduleItem to move
//	    * @param days Days you want the ScheduleItem to be taught on
//	    * @param s The start time you want the ScheduleItem to be taught on
//	    * 
//	    * @return The new ScheduleItem, w/ its fields updated to where it was placed
//	    * 
//	    * @throws CouldNotBeScheduledException If you've moved the ScheduleItem to
//	    *         a time where the location is in use or the instructor is already
//	    *         teaching. 
//	    */
//	   public static ScheduleItem move (Model model, Vector<ScheduleItem> s_items, ScheduleItem si, Week days, Time s, ScheduleDecorator sd) 
//	      throws CouldNotBeScheduledException
//	   {
//	      ScheduleItem fresh_si = model.assembleScheduleItemCopy(si);
//	      if (remove(s_items, si, sd))
//	      {
//	         Course c = model.findCourseByID(si.getCourseID());
//	         
//	         TimeRange tr = new TimeRange(s, splitLengthOverDays(c, days.size()));
//	      
//	         fresh_si.setDays(days.getDays());
//	         fresh_si.setTimeRange(tr);
//	      
//	         add(fresh_si, sd, s_items, null);
//	         
//	         /*
//	          * If the lab for the SI was teathered, we need to move it to just 
//	          * after the fresh_si
//	          */
//	         //assert(false);
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
//	   
//	   /**
//	    * Removes a given ScheduleItem from the schedule. Updates instructor and
//	    * location availability to show this new free time. The course's number of
//	    * sections taught are decremented. If this makes the course again eligible
//	    * to be taught, it will be added back to our cSourceList.
//	    * 
//	    * @param si ScheduleItem to remove
//	    * 
//	    * @return if the specified item was removed or not. It will not be removed
//	    *         if it does not exist in our list of items
//	    */
//	   public static boolean remove (Vector<ScheduleItem> s_items, ScheduleItem si, ScheduleDecorator sd)
//	   {
//		   
//		   Vector<ScheduleItem> items = s_items;
//		   HashMap<Integer, SectionTracker> sections = new HashMap<Integer, SectionTracker>();
//	      
//		   boolean r = false;
//	      if (items.contains(si))
//	      {
//	         r = true;
//	         
//	         Course c = si.getCourse();
//	         Instructor i = si.getInstructor();
//	         Location l = si.getLocation();
//	         Week days = si.getDays();
//	         TimeRange tr = si.getTimeRange();
//
//	         s_items.remove(si);
//	         i.book(false, days, tr, sd);
//	         l.book(false, days, tr, sd);
//
//	         sd.subtractWTU(i, si.getWtuTotal());
//
//	         SectionTracker st = getSectionTracker(c, sections);
//	         st.removeSection(si.getSection());
//	         
//	         /*
//	          * Remove the labs only if they're teathered to the course
//	          */
//	         if (si.hasLabs())
//	         {
//	            if (c.getTetheredToLecture())
//	            {
//	               removeItem(items, si.getLabs(), sd);
//	            }
//	         }
//	      }
//	      return r;
//	   }
//	   
//	   private static void removeItem (Vector<ScheduleItem> s_items,
//			   List<ScheduleItem> toRemove, ScheduleDecorator sd)
//	   {
//	      for (ScheduleItem si: toRemove)
//	      {
//	         remove(s_items, si, sd);
//	      }
//	   }
//	   
//	   /**
//	    * Determines the time range a lab can be taught for. In particular, if the
//	    * lab is teathered to its lecture, it must be taught directly after the
//	    * lecture. Otherwise, it can float around and be taught anywhere.
//	    * 
//	    * @param lab Lab to check for teathering
//	    * @param lec_si Schedule info for the lecture. Used for figuring out when a
//	    *        teathered lab to start
//	    * 
//	    * @return the TimeRange within which the given lab can be taught
//	    */
////	   private TimeRange getLabTimeRange (Lab lab, ScheduleItem lec_si)
////	   {
////	      TimeRange tr;
////	      if (lab.isTethered())
////	      {
////	         /*
////	          * Find times directly after the lecture only
////	          */
////	         tr = new TimeRange(lec_si.getEnd(), lab.getLength());
////	      }
////	      /*
////	       * Otherwise, lab can go anywhere within schedule bounds
////	       */
////	      else
////	      {
////	         tr = this.bounds;
////	      }
////	      return tr;
////	   }
//	   
//	   /**
//	    * Adds a ScheduleItem to our list of bad/conflicting ScheduleItems. The item
//	    * will only be added if 1) It's actually a conflicting item and 2) It's not
//	    * already present in our list of conflicting items
//	    *  
//	    * @param si Conflicting Item to add
//	    * 
//	    * @return true if the item was added to our list. False otherwise. 
//	    */
////	   public boolean addConflictingItem (ScheduleItem si, ScheduleDecorator sd)
////	   {
////	      boolean isDirty = false;
////	      try
////	      {
////	         verify(si, sd);
////	      }
////	      catch (CouldNotBeScheduledException e)
////	      {
////	         isDirty = true;
////	      }
////	      
////	      boolean r = false;
////	      if (isDirty)
////	      {
////	         r = this.dirtyList.add(si);
////	      }
////	      
////	      return r;
////	   }
//	   
//	   /**
//	    * Removes a ScheduleItem from our list of conflicting items. 
//	    * 
//	    * @param si ScheduleItem to remove from the list
//	    * 
//	    * @return true if the item actually existed in our list and was removed. 
//	    *         False otherwise.
//	    */
////	   public boolean removeConflictingItem (ScheduleItem si)
////	   {
////	      return this.dirtyList.remove(si);
////	   }
//	   
//	   /**
//	    * Returns the list of conflicting items
//	    * 
//	    * @return the list of conflicting items.
//	    */
////	   public HashSet<ScheduleItem> getDirtyList ()
////	   {
////	      return this.dirtyList;
////	   }
//	   
//	   /**
//	    * Sets the lecture time bounds
//	    * 
//	    * @param tr Bounds you want lectures to be taught within
//	    * 
//	    * @return The old lec_bounds value
//	    */
////	   private TimeRange setLecBounds (TimeRange tr)
////	   {
////	      TimeRange oldBounds = this.lec_bounds;
////	      this.lec_bounds = tr;
////	      return oldBounds;
////	   }
//}
