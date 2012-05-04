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
			Collection<ScheduleItem> s_items, Collection<Course> c_list, Vector<InstructorDecorator> i_vec,
			Vector<LocationDecorator> l_vec) throws DatabaseException, BadInstructorDataException {
		
		Vector<ScheduleItemDecorator> items = new Vector<ScheduleItemDecorator>();
		for (ScheduleItem si : s_items)
			items.add(new ScheduleItemDecorator(si));
		
		HashMap<Integer, SectionTracker> sections = new HashMap<Integer, SectionTracker>();
		TimeRange bounds = new TimeRange(14, 44);
		TimeRange lec_bounds = new TimeRange(bounds);
		TimeRange lab_bounds = new TimeRange(bounds);
	    
	    //Set staff preferences for this schedule generation.
	    InstructorDecorator staff = new InstructorDecorator(schedule.getDocument(), c_list);

	    debug("GENERATING");
	    debug("COURSES: " + c_list);
	    debug("INSTRUCTORS: " + i_vec);
	    debug("LOCATIONS : " + l_vec);
	      
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
	            
	                     lec_si = genLecItem(model, schedule, c, lec_bounds, i_vec, l_vec);
	                     lec_si.getItem().setSection(i + 1);
	                     debug ("MADE LEC_SI\n" + lec_si);
	                     try
	                     {
	                          add(model, schedule, lec_si, items, sections, i_vec, l_vec);
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
	                    			 lab_bounds, i_vec, l_vec);
	                    	 
	                    	 try
		                     {
		                         add(model, schedule, lab_si, items, sections, i_vec, l_vec);
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
               	    		lab_bounds, i_vec, l_vec);
	                	
                    try
                    {
                        add(model, schedule, lab_si, items, sections, i_vec, l_vec);
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
	      for (ScheduleItemDecorator si_dec : items)
	    	  result.add(si_dec.getItem());
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
	 * @throws BadInstructorDataException 
	    * @see Tba#getTba()
	    * @see Staff#getStaff()
	    */
	   private static ScheduleItemDecorator genLecItem (Model model, Schedule schedule, Course lec, 
			   TimeRange lec_bounds, Vector<InstructorDecorator> id_vec, Vector<LocationDecorator> ld_vec)
					   throws DatabaseException, BadInstructorDataException
	   {
	      ScheduleItem lec_si = model.createTransientScheduleItem(0, lec.getDayPatterns().iterator().next(),
	    		  lec_bounds.getS(), lec_bounds.getE(), false, false);
	      lec_si.setCourse(lec);
	      
	      lec_si.setInstructor((findInstructor(lec, id_vec)).getInstructor());

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
	 * @throws BadInstructorDataException 
	 * @throws NotFoundException 
	    */
	   private static boolean add (Model model, Schedule schedule, ScheduleItemDecorator si, 
			   Vector<ScheduleItemDecorator> items, HashMap<Integer, SectionTracker> sections, 
			   Vector<InstructorDecorator> id_vec, Vector<LocationDecorator> ld_vec) 
					   throws CouldNotBeScheduledException, DatabaseException, BadInstructorDataException
	   {
	      boolean r;

	      assert(si.getItem().getInstructor() != null);
	      //this should be  ok, as staff prefs should be set by now, BUT ensure this is the case. potential bugsrc
          InstructorDecorator id = getStaff(schedule);
          for(InstructorDecorator dec : id_vec) {
        	  if(dec.equals(si.getItem().getInstructor()))
        		  id = dec;
          }
          
          assert(id != null);
	      assert(si.getItem().getLocation() != null);
	      
	      LocationDecorator ld = new LocationDecorator(schedule.getDocument().getTBALocation());
	      for(LocationDecorator dec : ld_vec) {
	    	  if(dec.getLocation().equals(si.getItem().getLocation()))
	    		  ld = dec;
	      }
	      assert(ld != null);

	      if (r = verify(model, si, id, ld))
	      {
	         book(model, si, items, sections, id, ld);
	         //Persist the availability to the id_vec list
             
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
	 * @throws BadInstructorDataException 
	 * @see Tba#getTba()
	    * @see Staff#getStaff()
	    */
	   private static ScheduleItemDecorator genLabItem (Model model, Schedule schedule, Course lab, 
			   ScheduleItemDecorator lec_si, TimeRange lab_bounds, Vector<InstructorDecorator> id_vec, Vector<LocationDecorator> ld_vec) 
					   throws DatabaseException, BadInstructorDataException
	   {
		  ScheduleItem lab_si = model.createTransientScheduleItem(0, lab.getDayPatterns().iterator().next(), lab_bounds.getS(),
				  lab_bounds.getE(), false, false);
		  lab_si.setCourse(lab);
		  lab_si.setInstructor((findInstructor(lab, id_vec)).getInstructor());
	      
	      TimeRange tr = lab_bounds;
	      
	      if (lab.isTetheredToLecture())
	      {
	    	 debug ("Found tethered lab");
	         tr = new TimeRange(lec_si.getItem().getEndHalfHour(), lec_si.getItem().getEndHalfHour() + 
	        		 (lab.getNumHalfHoursPerWeekInt() / lab_si.getDays().size()));
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
	   private static InstructorDecorator getStaff(Schedule s) throws DatabaseException {
		   assert(s != null);
		   assert(s.getDocument().getStaffInstructor() != null);
		   return new InstructorDecorator(s.getDocument().getStaffInstructor()); 
	   }
	   
	   private static InstructorDecorator getStaff(Document doc) throws DatabaseException {
		   assert (doc != null); 
		   return new InstructorDecorator(doc.getStaffInstructor());
	   }
	   
 	   private static InstructorDecorator findInstructor (Course c, Vector<InstructorDecorator> id_vec) throws DatabaseException, BadInstructorDataException
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
	    * @return An instructor to teach 'c'. This can be STAFF if no instructor
	    *         if capable of teaching
	 * @throws BadInstructorDataException 
	    */
	   private static InstructorDecorator findInstructor (Course c, List<Integer> doNotPickInstructorIDs, 
			   Vector<InstructorDecorator> id_vec) throws DatabaseException, BadInstructorDataException
	   {
	      InstructorDecorator r = getStaff(c.getDocument());
	      if(doNotPickInstructorIDs != null) {
	    	  Integer toRemove = r.getInstructorID();
	    	  doNotPickInstructorIDs.remove(toRemove);
	      }
	      int curMaxPref = 0;
	      
	      debug ("FINDING INSTRUCTOR FOR " + c);
	      debug ("EXCLUDING: " + doNotPickInstructorIDs);
	      for (InstructorDecorator id : id_vec)
	      {
	    	  //Instructor i = id.getInstructor(); using instructor id's seems more stable
	    	  if(!(id.getInstructorID().equals(r.getInstructorID()))) {
	    		  debug ("CONSIDERING " + id.getInstructor());
	    		  if (doNotPickInstructorIDs == null || !doNotPickInstructorIDs.contains(id.getInstructorID()))
	    		  {
	    			  debug ("NOT EXCLUDED");
	    			  if (canTeach(id, c))
	    			  {
	    				  debug ("CAN");
	    				  int pref = getPreference(id, c);
	    				  debug ("DESIRE: " + pref);
	    				  if (pref > curMaxPref)
	    				  {
	    					  debug ("WANTS MORE: " + pref + " > " + curMaxPref);
	    					  r = id;
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
	 * @throws BadInstructorDataException 
	    */
	   public static int getPreference (InstructorDecorator instructor, Course course) throws DatabaseException, BadInstructorDataException
	   {
	      int desire = InstructorDecorator.getDefaultPref();

	      Integer temp = instructor.actualCoursePreferenceAsInt(course);
	      if (temp != null)
	         desire = temp;

	      return desire;
	   }

	   /**
	    * Checks to see if an instructor can teach the given course. Checks
	    * available WTUs and if their teaching preference is not 0.
	 * @throws BadInstructorDataException 
	    */
		public static boolean canTeach (InstructorDecorator instructor, Course course) throws DatabaseException, BadInstructorDataException
		{
		  if(instructor.isStaffInstructor())
			  return true;
		  if (instructor.checkWTUs(course) && instructor.preferenceForCourse(course))
		      return true;
		  
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
	   public static int getPreference (InstructorDecorator i, Day d, int time) throws DatabaseException
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
		      
	      return i.getTimePreferenceFor(d, time);
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
	 * @throws BadInstructorDataException 
	 * @throws NotFoundException 
	    * 
	    * @see SiMap#put(ScheduleItem)
	    * @see ScheduleItem#getValue()
	    * @see Staff#getStaff()
	    * @see Tba#getTba()
	    * @see #findTimes(ScheduleItem, TimeRange)
	    */
	   private static ScheduleItemDecorator genBestTime (Model model, Schedule schedule, ScheduleItem base, TimeRange tr, 
			   Vector<InstructorDecorator> id_vec, Vector<LocationDecorator> ld_vec) throws DatabaseException, BadInstructorDataException
	   {
	      Vector<ScheduleItemDecorator> si_list = new Vector<ScheduleItemDecorator>();

	      /*
	       * If we can't find times for the instructor in our base, we'll have to 
	       * try other instructors until we find one w/ at least one time he
	       * can teach
	       */
	      InstructorDecorator id = new InstructorDecorator(schedule.getDocument().getStaffInstructor());
	      for(InstructorDecorator dec: id_vec) {
	    	  //get the actual instructor we care about
	    	  if(dec.getInstructorID() == model.findInstructorByID(base.getInstructor().getID()).getID()) {
	    	     id = dec;
	    	  }
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
	            InstructorDecorator i = findInstructor(c, haveTriedInstructorIDs, id_vec);
	            debug ("FindInstructor returned: " + i.getInstructor());

	            //id = new InstructorDecorator(i);
	            
	            debug ("NO TIMES FOUND FOR " + base.getInstructor());
	            debug ("TRYING " + i.getInstructor());
	            
	            clone.setInstructor(i.getInstructor());
	            si_list = findTimes(model, schedule, clone, tr, id);
	            haveTriedInstructorIDs.add(i.getInstructorID());
	            
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
	 * @throws BadInstructorDataException 
	 * @throws NotFoundException 
	    * 
	    * @see Instructor#canTeach(Course)
	    */   
	   private static boolean verify (Model model, ScheduleItemDecorator si,
			   InstructorDecorator ins, LocationDecorator loc) throws CouldNotBeScheduledException, DatabaseException, BadInstructorDataException
	   {
	      Week days = new Week(si.getItem().getDays());
	      Course c = model.findCourseByID(si.getItem().getCourse().getID());
	      TimeRange tr = new TimeRange(si.getItem().getStartHalfHour(), si.getItem().getEndHalfHour());
	      //Instructor i = model.findInstructorByID(si.getItem().getInstructor().getID());     

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
	      if (getAvgPrefForTimeRange(ins, days, tr.getS(), tr.getE()) == 0)
	      {
	    	 debug("No desire to teach course.  Verification FAIL.");
	         throw new CouldNotBeScheduledException(ConflictType.NO_DESIRE, si);
	      }
	      if (!canTeach(ins, c))
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
	   public static double getAvgPrefForTimeRange (InstructorDecorator ins, Week w, int s, int e) 
			   throws DatabaseException
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
	            debug("PREF FOR " + d.toString() + " AT "+ tempS + ": " + desire);
	            /*
	             * If a zero is encountered, immediately break out and return 0 to
	             * let the caller know that this Time range (or some part of it)
	             * cannot be applied to this Instructor
	             */
	            if (desire < 1)
	               return 0;
	            else
	               dayTotal += desire;
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
	   public static boolean isAvailable (Week days, TimeRange tr, InstructorDecorator id) 
			   throws EndBeforeStartException, DatabaseException
	   {
		   if(id.isStaffInstructor())//.getInstructor().getDocument().getStaffInstructor() == id.getInstructor())
			   return true;
		   
	       return id.getAvailability().isFree(days, tr);
	   }

	   /**
	    * Determines whether a location is available during the given span of time,
	    * over the given week of days.
	    * 
	    * @param week The week of days that must be free
	    * @param tr TimeRange to check
	    * 
	    * @return True if the TimeRange is free on all days of "week"
	    * @throws DatabaseException 
	    */	   
	   public static boolean isAvailable(Week days, TimeRange tr, LocationDecorator ld) 
			   throws EndBeforeStartException, DatabaseException
	   {
		   if(ld.isTBALocation())//(ld.getLocation().getDocument().getTBALocation() == ld.getLocation())
			   return true;
		   
		   return ld.getAvailability().isFree(days, tr);
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
			   HashMap<Integer, SectionTracker> sections, InstructorDecorator id, LocationDecorator ld) 
					   throws DatabaseException
	   {
	      Week days = new Week(si.getItem().getDays());
	      TimeRange tr = new TimeRange(si.getItem().getStartHalfHour(), si.getItem().getEndHalfHour());

	      debug ("BOOKING");
	      
	      book(days, tr, id);
	      book(days, tr, ld);

	      id.addWTU(si.getItem().getCourse().getWTUInt());

	      SectionTracker st = getSectionTracker(model.findCourseByID(si.getItem().getCourse().getID()), sections);
	      st.addSection();
	      si.getItem().setSection(st.getCurSection());
	      
	      items.add(si);

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
	   private static InstructorDecorator getLabInstructor (Vector<InstructorDecorator> id_vec, Model model, 
			   Course lab, ScheduleItemDecorator lec_si) throws DatabaseException
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
		   
		   return getInstructorDecorator(id_vec, model, lec_si.getItem());// model.findInstructorByID(lec_si.getItem().getInstructor().getID());
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
	      
	      for(Set<Day> days : c.getDayPatterns()) {
	      
	          TimeRange tr = new TimeRange(range.getS(), range.getS() + getDayLength(c, days));
	          for (; tr.getE() < range.getE(); tr.addHalf())
	          {      	
	                debug("CONSIDERING Time Range: " + tr);
	                if (isAvailable(new Week(days), tr, id))
	                {
	                   debug("AVAILABLE");
	                   double pref;
	                   if ((pref = getAvgPrefForTimeRange(id, new Week(days), tr.getS(), tr.getE())) > 0)
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
	          if(sis.isEmpty()) { //Didn't find any times.  Probably a tethered lab.
	    	      debug("Found no matching times.  Tethered lab?");
	    	      if(si.getCourse().isTetheredToLecture()) {
	    		      debug("Found tethered lab.");	
	    		      debug("Calling isAvailable with: " + id.getInstructor().toString());
	    		      if(isAvailable(new Week(days), tr, id)) {
	    			      debug("AVAILABLE - Tethered");
	    			      double pref;
	  	                  if ((pref = getAvgPrefForTimeRange(id, new Week(days), tr.getS(), tr.getE())) > 0)
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
	      }

	      return sis;
	   }
	   
	   private static int getDayLength(Course c, Set<Day> days) throws DatabaseException {
		  return c.getNumHalfHoursPerWeekInt() / days.size();
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
		  LocationDecorator tbaLocation = getTBA(schedule);
		  
	      Vector<ScheduleItemDecorator> si_list = new Vector<ScheduleItemDecorator>();

	      debug ("HAVE " + sis.size() + " ITEMS FOR LOCATIONS TO TRY");
	      for (ScheduleItemDecorator si : sis)
	      {
	         Week days = new Week(si.getItem().getDays());
	         TimeRange tr = new TimeRange(si.getItem().getStartHalfHour(), si.getItem().getEndHalfHour());
	         for (LocationDecorator ld : ld_vec)
	         {
	        	//Location l = ld.getLocation();
	            debug ("TRYING LOCATION " + ld.getLocation() + " with time " + tr);
	            if (isAvailable(days, tr, ld))
	            {
	               if (providesFor(ld, model.findCourseByID(si.getItem().getCourse().getID())))
	               {
	                  /*
	                   * I clone so we don't keep changing the same object...that'd
	                   * be pretty bad.
	                   */
	                  ScheduleItem base = si.getItem().createTransientCopy();
	                  base.setLocation(ld.getLocation());

	                  si_list.add(new ScheduleItemDecorator(base));
	               }
	            }
	         }
	         //If no valid location is found, use TBA
	         if(si_list.isEmpty()) 
	         {
	        	 ScheduleItem base = si.getItem().createTransientCopy();
	        	 base.setLocation(tbaLocation.getLocation());
	        	 
	        	 si_list.add(new ScheduleItemDecorator(base));
	         }
	      }

	      return si_list;
	   }
	   
	   /*
	    * If there's no location available, use TBA 
	    */
	   private static LocationDecorator getTBA(Schedule s) throws DatabaseException {
		   assert(s!=null);
		   assert(s.getDocument().getTBALocation() != null);
		   return new LocationDecorator(s.getDocument().getTBALocation()); 
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
	   public static boolean providesFor(LocationDecorator loc, Course c)
	   {
	      boolean r = false;
	      if (loc.providesFor(c));
	         r = true;
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
	   
	   public static boolean book (Week days, TimeRange tr, InstructorDecorator id)
	   {
	       return id.getAvailability().book(days, tr);
	   }

	   /**
	    * Books the specified Location so no other classes are scheduled in the Location for the
	    * specified TimeRange
        *
	    * @return True if Location is successfully booked, otherwise false.
	    */
	   public static boolean book(Week days, TimeRange tr, LocationDecorator ld)
	   {
	       return ld.getAvailability().book(days, tr);
	   }
	   
	   /**
	    * Unbooks the specified Instructor from the specified TimeRange
	    */
	   public static boolean unbook(Week days, TimeRange tr, InstructorDecorator id)
	   {
		   return id.getAvailability().unbook(days, tr);
	   }
	   
	   /**
	    * Unbooks the specified Location from the specified TimeRange
	    */
	   public static boolean unbook(Week days, TimeRange tr, LocationDecorator ld)
	   {
		   return ld.getAvailability().unbook(days, tr);
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
}
