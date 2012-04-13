package scheduler.model.algorithm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import scheduler.model.Course;
import scheduler.model.Day;
import scheduler.model.Document;
import scheduler.model.Instructor;
import scheduler.model.Location;
import scheduler.model.Model;
import scheduler.model.Schedule;
import scheduler.model.ScheduleItem;
import scheduler.model.algorithm.CouldNotBeScheduledException.ConflictType;
import scheduler.model.db.DatabaseException;
import scheduler.model.db.IDatabase.NotFoundException;

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

	public static Vector<ScheduleItem> generate(Model model, Schedule schedule, 
			Collection<ScheduleItem> s_items, Collection<Course> c_list, Collection<Instructor> i_coll,
			Collection<Location> l_coll) throws DatabaseException {
		
		Vector<ScheduleItemDecorator> items = new Vector<ScheduleItemDecorator>();
		for (ScheduleItem si : s_items)
			items.add(new ScheduleItemDecorator(si));
		
		HashMap<Integer, SectionTracker> sections = new HashMap<Integer, SectionTracker>();
		TimeRange bounds = new TimeRange(14, 44);
		TimeRange lec_bounds = new TimeRange(bounds);
		TimeRange lab_bounds = new TimeRange(bounds);
	    
	    Vector<InstructorDecorator> id_vec = new Vector<InstructorDecorator>();
	    Vector<LocationDecorator> ld_vec = new Vector<LocationDecorator>();
	    
	    for(Instructor i : i_coll) {
	    	id_vec.add(new InstructorDecorator(i));
	    }
	   
	    for(Location l : l_coll) {
	    	ld_vec.add(new LocationDecorator(l));
	    }
	    
	    //Set staff preferences for this schedule generation.
	    Instructor staff = schedule.getDocument().getStaffInstructor();
	    HashMap<Integer, Integer> coursePrefs = new HashMap<Integer, Integer>();
	    for (Course course : c_list) {
	    	coursePrefs.put(course.getID(), 10);
	    }
	    staff.setCoursePreferences(coursePrefs);
	    
	    int[][] timePrefs = Instructor.createUniformTimePreferences(10);
	    staff.setTimePreferences(timePrefs);
	    
	    staff.update();

	    debug("GENERATING");
	    debug("COURSES: " + c_list);
	    debug("INSTRUCTORS: " + i_coll);
	    debug("LOCATIONS : " + l_coll);
	      
	    //Generate labs from the course list
	    HashMap<Integer, Course> tetheredLabs = new HashMap<Integer, Course>();
	    ArrayList<Course> untetheredLabs = new ArrayList<Course>();
	    for (Course c : c_list) {
	        //Is a lab (or until a case found otherwise, an ACT or DIS) associated with a lecture
	    	if(c.getTypeEnum() == Course.CourseType.LAB || c.getTypeEnum() == Course.CourseType.ACT || 
	    			c.getTypeEnum() == Course.CourseType.DIS) { 
	    		  
	            debug ("Found act/dis/lab: " + c.getTypeEnum() + " " + c.getCatalogNumber() + " " + c.getName());
	    	    if(c.isTetheredToLecture())
	    		    tetheredLabs.put(c.getLecture().getID(), c);
	    	    else
	    		    untetheredLabs.add(c);
	        }    	  
	    }

	      for (Course c : c_list)
	      {
	           if(c.getTypeEnum() == Course.CourseType.LEC ||c.getTypeEnum() == Course.CourseType.IND ||
	        		   c.getTypeEnum() == Course.CourseType.SEM) {
	                debug ("MAKING SI's FOR COURSE " + c);

	                ScheduleItemDecorator lec_si = null;
	                
	                SectionTracker st = getSectionTracker(c, sections);
	                for (int i = 0; i < c.getNumSectionsInt(); i ++)
	                {
	                     debug ("SECTIONS SCHEDULED: " + st.getCurSection()
	                        + " / " + c.getNumSections());
	            
	                     lec_si = genLecItem(model, schedule, c, lec_bounds, id_vec, ld_vec);
	                     debug ("MADE LEC_SI\n" + lec_si);
	                     try
	                     {
	                          add(model, lec_si, items, sections, id_vec, ld_vec);
	                          debug ("ADDED IT");
	                     }
	                     catch (CouldNotBeScheduledException e)
	                     {
	                          System.err.println("GENERATION MADE A BAD LEC");
	                          System.err.println(lec_si);
	                     }
	                     
	                     //Have a tethered lab.  Since tethered lab sections should always match
	                     //the course sections, we need to schedule a lab section here.
	                     if(tetheredLabs.containsKey(c.getID())) {
	                    	 debug ("Found tethered LAB/ACT/DIS for " + c.toString());
	                    	 
	                    	 Course lab = tetheredLabs.get(c.getID());
	                    	 
	                    	 ScheduleItemDecorator lab_si = genLabItem(model, schedule, lab, lec_si, 
	                    			 lab_bounds, id_vec, ld_vec);
	                    	 
	                    	 try
		                     {
		                         add(model, lab_si, items, sections, id_vec, ld_vec);
		                         lec_si.getItem().getLabs().add(lab_si.getItem());
		                     }
		                     catch (CouldNotBeScheduledException e)
		                     {
		                         System.err.println("GENERATION MADE A BAD LAB");
		                         System.err.println(lab_si);
		                     }
	                     }
	                     //Update the section number we're on in the SectionTracker
	                     st.setCurSection(st.getCurSection() + 1);
	                }
	                
	                debug ("Done with scheduling LEC");
	           }
	                
	           //Now schedule any untethered labs left over
	           for (Course lab : untetheredLabs) {
	            	debug ("Now scheduling untethered LAB/ACT/DIS");
	                	
	              	ScheduleItemDecorator lab_si = genLabItem(model, schedule, lab, null,
               	    		lab_bounds, id_vec, ld_vec);
	                	
                    try
                    {
                        add(model, lab_si, items, sections, id_vec, ld_vec);
                    }
                    catch (CouldNotBeScheduledException e)
                    {
                        System.err.println("Generation made a bad Untethered LAB");
                        System.err.println(lab_si);
                    }
	            }
	      }

	      debug ("GENERATION FINISHED W/: " + items.size());
	      
	      Vector<ScheduleItem> result = new Vector<ScheduleItem>();
	      for (ScheduleItemDecorator derp : items)
	    	  result.add(derp.getItem());
	      return result;
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
	      if (!sections.containsKey(c.getID()))
	      {
	         sections.put(c.getID(), new SectionTracker(c));
	      }
	      return sections.get(c.getID());
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
	 * @throws DatabaseException 
	 * @see Tba#getTba()
	    * @see Staff#getStaff()
	    */
	   private static ScheduleItemDecorator genLecItem (Model model, Schedule schedule, Course lec, 
			   TimeRange lec_bounds, Vector<InstructorDecorator> id_vec, Vector<LocationDecorator> ld_vec)
					   throws DatabaseException
	   {
	      ScheduleItem lec_si = model.createTransientScheduleItem(0, lec.getDayPatterns().iterator().next(),
	    		  lec_bounds.getS(), lec_bounds.getE(), false, false);
	      lec_si.setCourse(lec);
	      
	      lec_si.setInstructor(findInstructor(lec, id_vec));

	      return genBestTime(model, schedule, lec_si, lec_bounds, id_vec, ld_vec);
	   }
	   
	   /**
	    * Adds the given ScheduleItem to this schedule. Before an add is done, the
	    * ScheduleItem is verified to make sure it doesn't double-book an
	    * instructor/location and that the instructor can actually teach the course.
	    * 
	    * @param si ScheduleItem to add
	    * 
	    * @return true if the item was added. False otherwise.
	 * @throws NotFoundException 
	    */
	   private static boolean add (Model model, ScheduleItemDecorator si, 
			   Vector<ScheduleItemDecorator> items, HashMap<Integer, SectionTracker> sections, Vector<InstructorDecorator> id_vec,
			   Vector<LocationDecorator> ld_vec) 
					   throws CouldNotBeScheduledException, DatabaseException
	   {
	      boolean r;

	      assert(si.getItem().getInstructor() != null);
	      InstructorDecorator id = new InstructorDecorator(si.getItem().getInstructor());
	      
	      assert(si.getItem().getLocation() != null);
	      LocationDecorator ld = new LocationDecorator(si.getItem().getLocation());

	      if (r = verify(model, si, id, ld))
	      {
	         book(model, si, items, sections, id, ld);
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
	 * @throws DatabaseException 
	 * @see Tba#getTba()
	    * @see Staff#getStaff()
	    */
	   private static ScheduleItemDecorator genLabItem (Model model, Schedule schedule, Course lab, 
			   ScheduleItemDecorator lec_si, TimeRange lab_bounds, Vector<InstructorDecorator> id_vec, Vector<LocationDecorator> ld_vec) 
					   throws DatabaseException
	   {
		  ScheduleItem lab_si = model.createTransientScheduleItem(0, lab.getDayPatterns().iterator().next(), lab_bounds.getS(),
				  lab_bounds.getE(), false, false);
		  lab_si.setCourse(lab);
		  lab_si.setInstructor(findInstructor(lab, id_vec));
	      
	      TimeRange tr = lab_bounds;
	      
	      if (lab.isTetheredToLecture())
	      {
	    	 debug ("Found tethered lab");
	         tr = new TimeRange(lec_si.getItem().getEndHalfHour(), lec_si.getItem().getEndHalfHour() + (lab.getNumHalfHoursPerWeekInt() / lab_si.getDays().size()));
	      }

	      return genBestTime(model, schedule, lab_si, tr, id_vec, ld_vec);
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
	   private static Instructor getStaff(Document doc) throws DatabaseException {
		   assert(doc.getStaffInstructor() != null);
		   return doc.getStaffInstructor(); 
	   }
	   
	   private static Instructor findInstructor (Course c, Vector<InstructorDecorator> id_vec) throws DatabaseException
	   {
	      return findInstructor(c, null, id_vec);
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
	   private static Instructor findInstructor (Course c, List<Integer> doNotPickInstructorIDs, Vector<InstructorDecorator> id_vec) throws DatabaseException
	   {
	      Instructor r = getStaff(c.getDocument());
	      if(doNotPickInstructorIDs != null) {
	    	  Integer toRemove = r.getID();
	    	  doNotPickInstructorIDs.remove(toRemove);
	      }
	      int curMaxPref = 0;
	      
	      debug ("FINDING INSTRUCTOR FOR " + c);
	      debug ("EXCLUDING: " + doNotPickInstructorIDs);
	      for (InstructorDecorator id : id_vec)
	      {
	    	  Instructor i = id.getInstructor();
	    	  if(i != r) {
	    		  debug ("CONSIDERING " + i);
	    		  if (doNotPickInstructorIDs == null || !doNotPickInstructorIDs.contains(i.getID()))
	    		  {
	    			  debug ("NOT EXCLUDED");
	    			  if (canTeach(c, id))
	    			  {
	    				  debug ("CAN");
	    				  int pref = getPreference(i, c);
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
	      }
	      
	      return r;
	   }
	   
	   /**
	    * This method will get a course preference from the list of preferences.
	    * 
	    * @param course the course for which to get the preference.
	    * @return the course preference for the given course.
	    */
	   public static int getPreference (Instructor instructor, Course course) throws DatabaseException
	   {
	      int desire = Instructor.DEFAULT_PREF;

	      Integer temp = instructor.getCoursePreferences().get(course.getID());
	      if (temp != null)
	      {
	         desire = temp;
	      }

	      return desire;
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
	   public static boolean canTeach (Instructor instructor, Course course, int curWtu) throws DatabaseException
	   {
		  if(instructor.getDocument().getStaffInstructor() == instructor)
			  return true;
		   
	      // Check if instructor has enough WTUs
	      if ((curWtu + course.getWTUInt()) <= instructor.getMaxWTUInt())
	      {
	         /*
	          * TODO: rewrite this when CoursePreference is changed to a hash. Note
	          * that you'll not need to change this...just change the method
	          * "getPreference"
	          */
	    	  if (instructor.getCoursePreferences().get(course.getID()) > 0)
	         {
	            return true;
	         }
	      }
	      return false;
	   }

	   public static boolean canTeach (Course course, InstructorDecorator id) throws DatabaseException
	   {
		  //TODO - Need to implement an equals method for Instructor.  Can't do == to check for STAFF
		  //because we clone instructors and it will return false.
		  if(id.getInstructor().getDocument().getStaffInstructor() == id.getInstructor())
			  return true;
		   
	      // Check if instructor has enough WTUs
	      if ((id.getCurWTU() + course.getWTUInt()) <= id.getInstructor().getMaxWTUInt())
	      {
	         /*
	          * TODO: rewrite this when CoursePreference is changed to a hash. Note
	          * that you'll not need to change this...just change the method
	          * "getPreference"
	          */
	    	  if (id.getInstructor().getCoursePreferences().get(course.getID()) > 0)
	         {
	            return true;
	         }
	      }
	      return false;
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
	   public static int getPreference (Instructor i, Day d, int time) throws DatabaseException
	   {
		   /*if (DEBUG) {
			   System.out.println("Instructor prefs for " + getLastName());

		      for (Day day: this.tPrefs.keySet())
		      {
		         System.out.println ("COMPARING " + day + " with " + d);
		         if (d.hashCode() == day.hashCode() && d.equals(day))
		         {
		            System.out.println (d + " IS ALREADY HERE");
		         }
		      }
		   }*/
		      
	      return i.getTimePreferences(d, time);
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
	 * @throws NotFoundException 
	    * 
	    * @see SiMap#put(ScheduleItem)
	    * @see ScheduleItem#getValue()
	    * @see Staff#getStaff()
	    * @see Tba#getTba()
	    * @see #findTimes(ScheduleItem, TimeRange)
	    */
	   private static ScheduleItemDecorator genBestTime (Model model, Schedule schedule, ScheduleItem base, TimeRange tr, 
			   Vector<InstructorDecorator> id_vec, Vector<LocationDecorator> ld_vec) throws DatabaseException
	   {
	      Vector<ScheduleItemDecorator> si_list = new Vector<ScheduleItemDecorator>();

	      /*
	       * If we can't find times for the instructor in our base, we'll have to 
	       * try other instructors until we find one w/ at least one time he
	       * can teach
	       */
	      InstructorDecorator id = id_vec.get(0);
	      for(InstructorDecorator dec: id_vec) {
	    	  //get the actual instructor we care about
	    	  if(dec.getInstructor() == model.findInstructorByID(base.getInstructor().getID()))
	    		  id = dec;
	      }
	      
	      debug("Model instructor chosen: " + model.findInstructorByID(base.getInstructor().getID()));
	      debug("Instructor from vector chosen: " + id.getInstructor());
	      
	      si_list = findTimes(model, schedule, base, tr, id);
	      if (si_list.isEmpty())
	      {
	         ScheduleItem clone = base.createTransientCopy();
	         Course c = model.findCourseByID(base.getCourse().getID());
	         
	         /*
	          * Keep track of instructors we've tried so we don't use them again. 
	          * Eventually, if none are found, we'll end up using STAFF, which is
	          * eager to please.
	          */
	         Vector<Integer> haveTriedInstructorIDs = new Vector<Integer>();
	         haveTriedInstructorIDs.add(clone.getInstructor().getID());
	         do
	         {
	            Instructor i = findInstructor(c, haveTriedInstructorIDs, id_vec);
	            debug ("FindInsructor returned: " + i.toString());
	            
	            id = new InstructorDecorator(i);
	            
	            debug ("NO TIMES FOUND FOR " + base.getInstructor());
	            debug ("TRYING " + i);
	            
	            clone.setInstructor(i);
	            si_list = findTimes(model, schedule, clone, tr, id);
	            haveTriedInstructorIDs.add(i.getID());
	            
	         } while (si_list.isEmpty());
	      }
	      
	      debug("GOT " + si_list.size() + " TIMES");
	      
	      si_list = findLocations(model, schedule, si_list, ld_vec);
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
	 * @throws NotFoundException 
	    * 
	    * @see Instructor#canTeach(Course)
	    */   
	   private static boolean verify (Model model, ScheduleItemDecorator si,
			   InstructorDecorator ins, LocationDecorator loc) throws CouldNotBeScheduledException, DatabaseException
	   {
	      Week days = new Week(si.getItem().getDays());
	      Course c = model.findCourseByID(si.getItem().getCourse().getID());
	      TimeRange tr = new TimeRange(si.getItem().getStartHalfHour(), si.getItem().getEndHalfHour());
	      Instructor i = model.findInstructorByID(si.getItem().getInstructor().getID());     

	      if (!isAvailable(days, tr, ins))
	      {
	    	 debug("Instructor is not available.  Verification FAIL.");
	         throw new CouldNotBeScheduledException(ConflictType.I_DBL_BK, si);
	      }
	      //possible bug this was 'i' and switched to 'l' since that's the type of booking error we're looking for
	      if (!isAvailable(days, tr, loc))
	      {
	    	 debug("Location is not available. Verification FAIL.");
	         throw new CouldNotBeScheduledException(ConflictType.L_DBL_BK, si);
	      }
	      if (getAvgPrefForTimeRange(i, days, tr.getS(), tr.getE()) == 0)
	      {
	    	  debug("No desire to teach course.  Verification FAIL.");
	         throw new CouldNotBeScheduledException(ConflictType.NO_DESIRE, si);
	      }
	      if (!canTeach(c, ins))
	      {
	    	  debug("Instructor not able to teach time.  Verification FAIL.");
	         throw new CouldNotBeScheduledException(ConflictType.CANNOT_TEACH, si);
	      }

	      return true;
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
	   public static double getAvgPrefForTimeRange (Instructor ins, Week w, int s, int e) throws DatabaseException
	   {
	      double total = 0;
	      int length = e - s;

	      /*
	       * Go over every day in the week
	       */
	      for (Day d : w.getDays())
	      {
	         int tempS = s;
	         double dayTotal = 0;
	         /*
	          * Get the desire from each half hour slot in the Time range
	          */
	         for (int i = 0; i < length; i++, tempS++)
	         {
	            int desire = getPreference(ins, d, tempS);
	     	   if (DEBUG)
	            System.err.println ("PREF FOR " + tempS + ": " + desire);
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
	    * Returns true if this instructor is available for the given Week of days
	    * and the given time range
	    * 
	    * @param w Week containing the days to check
	    * @param tr Time range to check
	    * 
	    * @return True if the instructor is available for the given time range
	    *         across the given Week
	    * @throws DatabaseException 
	    * @throws EndBeforeStartException 
	    */	   
	   public static boolean isAvailable (Week days, TimeRange tr, InstructorDecorator id) throws EndBeforeStartException, DatabaseException
	   {
		   if(id.getInstructor().getDocument().getStaffInstructor() == id.getInstructor())
			   return true;
		   
	       return id.getAvailability().isFree(tr.getS(), tr.getE(), days);
	   }

	   /**
	    * This method will tell whether this location is availble during the given
	    * time slot.
	    * 
	    * @param dayOfWeek
	    *           The day (0 = Sun; 6 = Sat)
	    * @param s
	    *           The start time
	    * @param e
	    *           The end time
	    * 
	    * @return True if the given span of time is available. False otherwise.
	    * 
	    *         Written by: Eric Liebowitz
	    */
	   public static boolean isAvailable(Day dayOfWeek, int s, int e, LocationDecorator ld)
	   {		   
		   return ld.getAvailability().isFree(s, e, dayOfWeek);
	   }

	   /**
	    * Determines whether a location is available during the given span of time,
	    * over the given week of days.
	    * 
	    * @param week
	    *           The week of days that must be free
	    * @param s
	    *           The start time
	    * @param e
	    *           The end time
	    * 
	    * @return True if the time between "s" and "e" is free on all days of "week"
	    * 
	    *         Written by: Eric Liebowitz
	    */
	   public static boolean isAvailable(Week week, int s, int e, LocationDecorator ld)
	   {
		   return ld.getAvailability().isFree(s, e, week);
	   }

	   /**
	    * Determines whether a location is available during the given span of time,
	    * over the given week of days.
	    * 
	    * @param week
	    *           The week of days that must be free
	    * @param tr
	    *           TimeRange to check
	    * 
	    * @return True if the TimeRange is free on all days of "week"
	    * @throws DatabaseException 
	    */	   
	   public static boolean isAvailable(Week week, TimeRange tr, LocationDecorator ld) throws DatabaseException
	   {
		   if(ld.getLocation().getDocument().getTBALocation() == ld.getLocation())
			   return true;
		   
		   return ld.getAvailability().isFree(tr, week);
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
	 * @throws NotFoundException 
	    * 
	    * @see #verify(ScheduleItem)
	    */
	   private static void book (Model model, ScheduleItemDecorator si, Vector<ScheduleItemDecorator> items, 
			   HashMap<Integer, SectionTracker> sections, InstructorDecorator id, LocationDecorator ld) throws DatabaseException
	   {
	      Week days = new Week(si.getItem().getDays());
	      TimeRange tr = new TimeRange(si.getItem().getStartHalfHour(), si.getItem().getEndHalfHour());

	      debug ("BOOKING");
	      
	      book(true, days, tr, id);
	      //TODO: I think there's a bug here...doesn't make sense to book i twice with same fields and l is unused
	      //so switched i to l in second book call.
	      book(true, days, tr, ld);

	      id.addWTU(si.getItem().getCourse().getWTUInt());

	      SectionTracker st = getSectionTracker(model.findCourseByID(si.getItem().getCourse().getID()), sections);
	      st.addSection();
	      si.getItem().setSection(st.getCurSection());
	      
	      items.add(si);
	      
//	      debug ("JUST ADDED SECTION " + st.getCurSection() + " OF " + 
//	         si.getCourse());
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
	 * @throws NotFoundException 
	    * 
	    * @see Staff#getStaff()
	    */
	   private static Instructor getLabInstructor (Model model, Course lab, ScheduleItemDecorator lec_si) throws DatabaseException
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
		   
		   return model.findInstructorByID(lec_si.getItem().getInstructor().getID());
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
	    * findTimes
	    * @return A list of ScheduleItems w/ their days and time ranges set to times
	    *         which the instructor wants to/can teach and on the days the Course
	    *         is to be taught on.
	 * @throws NotFoundException 
	    */
	   private static Vector<ScheduleItemDecorator> findTimes (Model model, Schedule schedule, ScheduleItem si, TimeRange range, 
			   InstructorDecorator id) throws DatabaseException
	   {
	      debug("FINDING TIMES IN RANGE " + range);
	      Vector<ScheduleItemDecorator> sis = new Vector<ScheduleItemDecorator>();
	      Course c = model.findCourseByID(si.getCourse().getID());
	      assert(si.getInstructor() != null);
	      Instructor i = model.findInstructorByID(si.getInstructor().getID());
	      
	      TimeRange tr = new TimeRange(range.getS(), range.getS() + getDayLength(c));
	      debug("Daylength: " + getDayLength(c));
	      debug("Time range end is: " + tr.getE());
	      debug("End of day range is: " + range.getE());
	      for (; tr.getE() < range.getE(); tr.addHalf())
	      {
	         Set<Day> days = c.getDayPatterns().iterator().next();	         

	         debug("CONSIDERING Time Range: " + tr);
	         if (isAvailable(new Week(days), tr, id))
	         {
	            debug("AVAILABLE");
	            double pref;
	            if ((pref = getAvgPrefForTimeRange(i, new Week(days), tr.getS(), tr.getE())) > 0)
	            {
	               debug("WANTS: " + pref);
	               ScheduleItem toAdd = si.createTransientCopy();
	               toAdd.setDays(days);
	               toAdd.setStartHalfHour(tr.getS());
	               toAdd.setEndHalfHour(tr.getE());

	               sis.add(new ScheduleItemDecorator(toAdd));
	            }
	         }
	      }
	      //TODO - Quick fix.  Does not address the fact that STAFF probably won't teach the lab.
	      if(sis.isEmpty()) { //Didn't find any times.  Probably a tethered lab.
	    	  debug("Found no matching times.  Tethered lab?");
	    	  if(si.getCourse().isTetheredToLecture()) {
	    		  debug("Found tethered lab.");
	    		  Set<Day> days = c.getDayPatterns().iterator().next();	
	    		  debug("Calling isAvailable with: " + id.getInstructor().toString());
	    		  if(isAvailable(new Week(days), tr, id)) {
	    			  debug("AVAILABLE - Tethered");
	    			  double pref;
	  	              if ((pref = getAvgPrefForTimeRange(i, new Week(days), tr.getS(), tr.getE())) > 0)
	  	              {
	  	                 debug("WANTS: " + pref);
	  	                 ScheduleItem toAdd = si.createTransientCopy();
	  	                 toAdd.setDays(days);
	  	                 toAdd.setStartHalfHour(tr.getS());
	  	                 toAdd.setEndHalfHour(tr.getE());

	  	                 sis.add(new ScheduleItemDecorator(toAdd));
	  	              }
	    		  }
	    	  }
	      }

	      return sis;
	   }
	   
	   private static int getDayLength(Course c) throws DatabaseException {
		  return c.getNumHalfHoursPerWeekInt() / c.getDayPatterns().iterator().next().size();
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
	 * @throws NotFoundException 
	    */  
	   private static Vector<ScheduleItemDecorator> findLocations (Model model, Schedule schedule, Vector<ScheduleItemDecorator> sis,
			   Vector<LocationDecorator> ld_vec) throws DatabaseException
	   {
		  //might have to look into TBA location for IND type courses
		  assert(schedule.getDocument().getTBALocation() != null);
		  Location tbaLocation = schedule.getDocument().getTBALocation();
	      Vector<ScheduleItemDecorator> si_list = new Vector<ScheduleItemDecorator>();

	      debug ("HAVE " + sis.size() + " ITEMS FOR LOCATIONS TO TRY");
	      for (ScheduleItemDecorator si : sis)
	      {
	         Week days = new Week(si.getItem().getDays());
	         TimeRange tr = new TimeRange(si.getItem().getStartHalfHour(), si.getItem().getEndHalfHour());
	         for (LocationDecorator ld : ld_vec)
	         {
	        	Location l = ld.getLocation();
	            debug ("TRYING LOCATION " + l + " with time " + tr);
	            if (isAvailable(days, tr, ld))
	            {
	               if (providesFor(l, model.findCourseByID(si.getItem().getCourse().getID())))
	               {
	                  /*
	                   * I clone so we don't keep changing the same object...that'd
	                   * be pretty bad.
	                   */
	                  ScheduleItem base = si.getItem().createTransientCopy();
	                  base.setLocation(l);

	                  si_list.add(new ScheduleItemDecorator(base));
	               }
	            }
	         }
	         //If no valid location is found, use TBA
	         if(si_list.isEmpty()) 
	         {
	        	 ScheduleItem base = si.getItem().createTransientCopy();
	        	 base.setLocation(tbaLocation);
	        	 
	        	 si_list.add(new ScheduleItemDecorator(base));
	         }
	      }

	      return si_list;
	   }

	   /**
	    * Determines whether this location provides the required equipment for a
	    * given course, and is of a compatible type.
	    * 
	    * @param c
	    *           The course to provide for
	    * 
	    * @return true if this location has enough seats to support the given
	    *         course.
	    */
	 //TODO: Support for required equipment and course types
	   public static boolean providesFor(Location loc, Course c)
	   {
	      boolean r = false;
	      if (c.getMaxEnrollmentInt() <= loc.getMaxOccupancyInt())
	      {
	         r = true;
	      }
	      return r;
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
	 * @throws NotFoundException 
	    */
	   private static int splitLengthOverDays(Course c, int size) {
		   assert(false);
		// TODO Auto-generated method stub
		return 0;
	   }
	   
	   public static ScheduleItemDecorator move (Model model, Vector<ScheduleItemDecorator> s_items,
			   ScheduleItemDecorator si, Week days, int s, Vector<InstructorDecorator> id_vec, Vector<LocationDecorator> ld_vec) 
			      throws CouldNotBeScheduledException, DatabaseException
			   {
			      ScheduleItemDecorator fresh_si = new ScheduleItemDecorator(si.getItem().createTransientCopy());
			      if (remove(model, id_vec, ld_vec, s_items, si))
			      {
			         Course c = model.findCourseByID(si.getItem().getCourse().getID());
			         
			         TimeRange tr = new TimeRange(s, splitLengthOverDays(c, days.size()));
			      
			         fresh_si.setDays(days.getDays());
			         fresh_si.setStartHalfHour(tr.getS());
			         fresh_si.setEndHalfHour(tr.getE());
			      
			         add(model, fresh_si, s_items, null, id_vec, ld_vec);
			         
			         /*
			          * If the lab for the SI was teathered, we need to move it to just 
			          * after the fresh_si
			          */
			         //assert(false);
			         /*
			          *  
			         Lab lab = c.getLab();
			         if (lab != null && lab.isTethered())
			         {
			            Time lab_s = tr.getE();
			            for (ScheduleItem lab_si: si.getLabs())
			            {
			               move(lab_si, days, lab_s);
			            }
			         }*/
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
	 * @throws NotFoundException 
	    */
	   public static boolean remove (Model model, Vector<InstructorDecorator> id_vec, Vector<LocationDecorator> ld_vec, 
			   Vector<ScheduleItemDecorator> s_items, ScheduleItemDecorator si) 
					   throws DatabaseException
	   {
		   
		   Vector<ScheduleItemDecorator> items = s_items;
		    
		   HashMap<Integer, SectionTracker> sections = new HashMap<Integer, SectionTracker>();
	      
		   boolean r = false;
	      if (items.contains(si))
	      {
	         r = true;
	         
	         Course c = model.findCourseByID(si.getItem().getCourse().getID());
	         Week days = new Week(si.getItem().getDays());
	         TimeRange tr = new TimeRange(si.getItem().getStartHalfHour(), si.getItem().getEndHalfHour());

	         InstructorDecorator id = getInstructorDecorator(id_vec, model, si.getItem());
	         LocationDecorator ld = getLocationDecorator(ld_vec, model, si.getItem());
	         
	         s_items.remove(si);
	         book(false, days, tr, id);
	         book(false, days, tr, ld);

	         id.subtractWTU(si.getItem().getCourse().getWTUInt());

	         SectionTracker st = getSectionTracker(c, sections);
	         st.removeSection(si.getItem().getSection());
	         
	         /*
	          * Remove the labs only if they're teathered to the course
	          */
	         if (!si.getItem().getLabs().isEmpty())
	         {
	            if (c.isTetheredToLecture())
	            {
	            	assert(false); // not sure what to give in for the 4th arg
	               removeItem(model, id, ld, items, null, id_vec, ld_vec);
	            }
	         }
	      }
	      return r;
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
	   
	   public static boolean book (boolean b, Week days, TimeRange tr, InstructorDecorator id)
	   {
	       return id.getAvailability().book(b, days, tr);
	   }

	   /**
	    * Books this location for a given time over a given span of days (Week).
	    * 
	    * @param week
	    *           The span of days to book
	    * @param s
	    *           The start time
	    * @param e
	    *           The end time
	    * 
	    * @return if the time was booked, and thus free beforehand.
	    * 
	    *         Written by: Eric Liebowitz
	    */
	   public boolean book(boolean b, Week week, int s, int e, LocationDecorator ld)
	   {
		   return ld.getAvailability().book(b, s, e, week);
	   }

	   public static boolean book(boolean b, Week week, TimeRange tr, LocationDecorator ld)
	   {
	       return ld.getAvailability().book(b, week, tr);
	   }

	   /**
	    * This method will take in a day, start time, and end time and set that time
	    * interval as busy for this location.
	    * 
	    * @param dayOfWeek
	    *           The day (0 = Sun; 6 = Sat)
	    * @param s
	    *           The start time
	    * @param e
	    *           The end time
	    * 
	    *           Written by: Eric Liebowitz
	    */
	   
	   //TODO: can probably get rid of this eventually and just call ld.getAvailability() in the caller
	   public static boolean book(boolean b, Day dayOfWeek, int s, int e, LocationDecorator ld)
	   {
		   return ld.getAvailability().book(b, s, e, dayOfWeek);
	   }
	   
	   
	   private static void removeItem (Model model, InstructorDecorator ins, LocationDecorator loc, 
			   Vector<ScheduleItemDecorator> s_items, List<ScheduleItemDecorator> toRemove, Vector<InstructorDecorator> id_vec,
			   Vector<LocationDecorator> ld_vec) throws DatabaseException
	   {
	      for (ScheduleItemDecorator si: toRemove)
	      {
	         remove(model, id_vec, ld_vec, s_items, si);
	      }
	   }
	   
	   private static InstructorDecorator getInstructorDecorator(Vector<InstructorDecorator> id_vec, Model model, ScheduleItem base)
			   throws DatabaseException {
		      InstructorDecorator id = id_vec.get(0);
		      
		      for(InstructorDecorator dec: id_vec) {
		    	  //get the actual instructor we care about
		    	  if(dec.getInstructor() == model.findInstructorByID(base.getInstructor().getID()))
		    		  id = dec;
		      }
		      return id;
	   }
	   
	   private static LocationDecorator getLocationDecorator(Vector<LocationDecorator> ld_vec, Model model, ScheduleItem base)
			   throws DatabaseException {
		      LocationDecorator ld = ld_vec.get(0);
		      
		      for(LocationDecorator dec: ld_vec) {
		    	  //get the actual instructor we care about
		    	  if(dec.getLocation() == model.findLocationByID(base.getLocation().getID()))
		    		  ld = dec;
		      }
		      return ld;
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
