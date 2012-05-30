package scheduler.model.algorithm;

import java.util.Collection;
import java.util.Vector;

import com.sun.xml.internal.bind.v2.model.core.ID;
import scheduler.model.Day;
import scheduler.model.Course;
import scheduler.model.Document;
import scheduler.model.Instructor;
import scheduler.model.Location;
import scheduler.model.Model;
import scheduler.model.ScheduleItem;
import scheduler.model.db.DatabaseException;

public class GenerateEntryPoint {
	
	private static Vector<InstructorDecorator> insD;
	private static Vector<LocationDecorator> locD;
	
	public static Vector<ScheduleItem> generate(Model model, Document document, Collection<ScheduleItem> s_items, 
			Collection<Course> c_list, Collection<Instructor> i_coll,
			Collection<Location> l_coll) throws DatabaseException, BadInstructorDataException {
		
		if(model == null || document == null) 
			throw new NullPointerException();
		
		insD = new Vector<InstructorDecorator>();
		locD = new Vector<LocationDecorator>();
		
		
		
	    for(Instructor i : i_coll) {
			try {
				checkValid(i);
				insD.add(new InstructorDecorator(i));
			} catch (BadInstructorDataException e) {
				e.printStackTrace();
			}
	    }
	   
	    for(Location l : l_coll) {    	
			try {
				checkValid(l);
				locD.add(new LocationDecorator(l));
			} catch (BadLocationDataException e) {
				e.printStackTrace();
			}
	    }
	    
	    for(Course c : c_list) {
	    	try {
	    		checkValid(c);
	    	} catch (BadCourseDataException e) {
	    		e.printStackTrace();
	    	}
	    }
	        
	    //check schedule items - note, this should never get the staff, choose for me or tba items since they'll never be 
	    //in the given lists
	    for(InstructorDecorator id : insD)
		    for(ScheduleItem s: s_items) 
		    	if(id.equals(s.getInstructor())) {
		    		//System.out.println("+++found the instructor+++ " + s.getInstructor().getFirstName());
		    		//update the instructor decorator
		    		id.addWTU(new Integer(s.getCourse().getWTUInt()));
		    		//System.out.println("WTUs for id: " + id.getCurWTU());
		    		TimeRange tmp = new TimeRange(s.getStartHalfHour(), s.getEndHalfHour());
		    		id.getAvailability().book(new Week(s.getDays()), tmp);
		    		//remove the course from the course collection, it's been scheduled
		    		c_list.remove(s.getCourse());
		    	}
	    //update the location but don't remove it
	    for(LocationDecorator ld : locD) 
	    	for(ScheduleItem s : s_items) 
	    		if(ld.equals(s.getLocation())) {
	    			TimeRange tmp = new TimeRange(s.getStartHalfHour(), s.getEndHalfHour());
	    			ld.getAvailability().book(new Week(s.getDays()), tmp);
	    		}
	    
		return Generate.generate(model, document, s_items, c_list, insD, locD);
	}

	public static Collection<ScheduleItem> checkForConflicts(Collection<ScheduleItem> s_items, Collection<Instructor> instructors, Collection<Location> locations)
			throws DatabaseException {
		
		insD = new Vector<InstructorDecorator>();
		locD = new Vector<LocationDecorator>();
		
		for(Instructor i : instructors)
			if(!(i.getDocument().getStaffInstructor().getID().equals(i.getID())) && 
			  (!(i.getDocument().getChooseForMeInstructor().getID().equals(i.getID())))) {
				insD.add(new InstructorDecorator(i));
			}
		for(Location l : locations)
			if(!(l.getDocument().getTBALocation().getID().equals(l.getID())) && 
			  (!(l.getDocument().getChooseForMeLocation().getID().equals(l.getID())))) {
				locD.add(new LocationDecorator(l));
			}

		//if there is a time or wtu conflict then mark it conflicted	
		for(InstructorDecorator id : insD) {
			for(ScheduleItem s : s_items) {
				if(id.equals(s.getInstructor())) 
					if(!(id.hasEnoughWTUToTeach(s.getCourse())))
						s.setIsConflicted(true);
					else id.addWTU(s.getCourse().getWTUInt());
			
				TimeRange tmp = new TimeRange(s.getStartHalfHour(), s.getEndHalfHour());
				//System.out.println(s.getDays())
				if(!(id.getAvailability().isFree(new Week(s.getDays()), tmp)))
					s.setIsConflicted(true);
				else id.getAvailability().book(new Week(s.getDays()), tmp);
			}
		}
		
		//if there is a time conflict mark it conflicted
		for(LocationDecorator ld : locD) {
			for(ScheduleItem s : s_items) {
				if(ld.equals(s.getLocation())) {
	    			TimeRange tmp = new TimeRange(s.getStartHalfHour(), s.getEndHalfHour());
	    			if(!(ld.getAvailability().isFree(new Week(s.getDays()), tmp)))
	    				s.setIsConflicted(true);
	    			else ld.getAvailability().book(new Week(s.getDays()), tmp);
	    		}	
			}
		}
	
//		for(ScheduleItem s : s_items)
//			if(s.isConflicted())
//				System.out.println(s);
	    return s_items;
	}
	
	private static void checkValid(Instructor ins) throws BadInstructorDataException, DatabaseException{
		if(ins==null)
			throw new BadInstructorDataException(BadInstructorDataException.ConflictType.IS_NULL,
					ins, "null", "instructor object");
		if(ins.getDocument() == null)
			throw new BadInstructorDataException(BadInstructorDataException.ConflictType.NULL_DOC, ins, "null", 
			"instructor document");
		
		if(ins.getDocument().getStaffInstructor().getID().equals(ins.getID()))
			throw new BadInstructorDataException(BadInstructorDataException.ConflictType.IS_STAFF,
					ins, "Staff", " not staff instructor");
		
		if(!ins.isSchedulable())
			throw new BadInstructorDataException(BadInstructorDataException.ConflictType.NOT_SCHEDULABLE,
					ins, "Bad", "schedulable");
		if(ins.getFirstName() == null || ins.getFirstName() == "")
			throw new BadInstructorDataException(BadInstructorDataException.ConflictType.BAD_FNAME,
					ins, "Null or empty first name", "first name");
		if(ins.getLastName() == null || ins.getLastName() == "")
			throw new BadInstructorDataException(BadInstructorDataException.ConflictType.BAD_LNAME,
					ins, "Null or empty last name", "last name");
		if(ins.getUsername() == null || ins.getUsername() == "")
			throw new BadInstructorDataException(BadInstructorDataException.ConflictType.BAD_UNAME,
					ins, "Null or empty username", "username");
		if(ins.getMaxWTU() == null || ins.getMaxWTUInt() < 0)
			throw new BadInstructorDataException(BadInstructorDataException.ConflictType.BAD_MAXWTU, ins,
					"null or < 0 ", ">0");
		checkInsTimePrefs(ins);
		checkInsCoursePrefs(ins);
	}
	
	private static void checkInsTimePrefs(Instructor ins) throws BadInstructorDataException, DatabaseException{
		if(ins.getTimePreferences() == null)
			throw new BadInstructorDataException(BadInstructorDataException.ConflictType.NULL_I_PREFS,
					ins, "null", "instructor time prefs");
		//ins.g
	}
	
	private static void checkInsCoursePrefs(Instructor ins) throws DatabaseException, BadInstructorDataException {
		if(ins.getCoursePreferences() == null)
			throw new BadInstructorDataException(BadInstructorDataException.ConflictType.NULL_C_PREFS, 
					ins, "null", "instructor course preferences");		
	}
	
	private static void checkValid(Location loc) throws BadLocationDataException, DatabaseException{
		//validity checks
		if(loc == null)
			throw new BadLocationDataException(BadLocationDataException.ConflictType.IS_NULL, loc, "null", "Location"); 
		if(loc.getDocument()==null)
			throw new BadLocationDataException(BadLocationDataException.ConflictType.IS_NULL, loc, "null", "Associated Document for " +
					"location " + loc.getRoom()); 
		if(loc.getDocument().getTBALocation().getID().equals(loc.getID()))
			throw new BadLocationDataException(BadLocationDataException.ConflictType.IS_TBA, loc, "tba", "loc"); 
		//dont care case if(loc.getDocument().g)
		if(!loc.isSchedulable())
			throw new BadLocationDataException(BadLocationDataException.ConflictType.NOT_SCHEDULABLE, loc, "Location " + loc.getRoom() 
					 , " schedulable not set"); 
		if(loc.getRoom() == null || loc.getRoom() == "")
			throw new BadLocationDataException(BadLocationDataException.ConflictType.BAD_ROOM, loc, "null or empty room", "valid room");
		if(loc.getType() == null || loc.getType() == "")
			throw new BadLocationDataException(BadLocationDataException.ConflictType.BAD_LTYPE, loc, "null or empty type", "valid type");
		if(loc.getMaxOccupancy() == null || loc.getMaxOccupancyInt() < 0)
			throw new BadLocationDataException(BadLocationDataException.ConflictType.BAD_MAXOCC, loc, "null or < 0", "> 0");
		//if(l) all equipment type values are valid. i think null shouldnt be. have a none option.
	}
	
	private static void checkValid(Course c) throws BadCourseDataException, DatabaseException {
		if(c == null)
			throw new BadCourseDataException(BadCourseDataException.ConflictType.IS_NULL, c, "Course entered", "null");
		if(c.getName() == null)
			throw new BadCourseDataException(BadCourseDataException.ConflictType.NULL_CNAME, c, "Course name as non-null string", "null string");
		if(!c.isSchedulable())
			throw new BadCourseDataException(BadCourseDataException.ConflictType.NOT_SCHEDULABLE, c, "Course " + c.getName() + " not schedulable",
					"schedulable is true");
		if(c.getDepartment() == null)
			throw new BadCourseDataException(BadCourseDataException.ConflictType.NULL_DPTMT, c, "Course " + c.getName() + " department", "null/no department");
	    if(c.getCatalogNumber() == null || c.getCatalogNumber() == "")
	    	throw new BadCourseDataException(BadCourseDataException.ConflictType.BAD_CAT_NUM, c, "Course " + c.getName() + " catalog number ", "null or empty string ");
		if(c.getDocument() == null)
			throw new BadCourseDataException(BadCourseDataException.ConflictType.IS_NULL, c, "Document for course " + c.getName()
					, "null");
		if(c.getNumSections() == null || c.getNumSectionsInt() <= 0)
	    	throw new BadCourseDataException(BadCourseDataException.ConflictType.BAD_NUMSECTS, c, "Course " + c.getName() + " number sections > 0", "null or <= 0");
	    if(c.getWTU() == null || c.getWTUInt() <= 0)
			throw new BadCourseDataException(BadCourseDataException.ConflictType.BAD_NUMWTU, c, "Course WTU for course " + c.getName(),
					"null or <= 0");
	    if(c.getSCU() == null || c.getSCUInt() <= 0)
	    	throw new BadCourseDataException(BadCourseDataException.ConflictType.BAD_NUMSCU, c, "SCU for course " + c.getName() + " >0 ", "null or <= 0)");
	    
	    //check requirements for day combos still?
	    if(c.isTetheredToLecture())
	    	if(c.getLecture() == null || c.getLecture().getID() == null)
	    		throw new BadCourseDataException(BadCourseDataException.ConflictType.TETHERED_NULL_LECT, c, " Course " + c.getName() + "is tethered with lecture ", 
	    				" tethered lecture or lecture ID is null");
	    
	    //there should be an extra reqt..it can't be higher than some value
	    if(c.getNumHalfHoursPerWeek() == null || c.getNumHalfHoursPerWeekInt() < 0)
	    	throw new BadCourseDataException(BadCourseDataException.ConflictType.BAD_HHR_WEEK, c, "Course " + c.getName() + " hours per week > 0", "Number of hours per week <= 0 or null");
	    if(c.getMaxEnrollment() == null || c.getMaxEnrollmentInt() < 0)
	    	throw new BadCourseDataException(BadCourseDataException.ConflictType.BAD_MAXENR, c,  "Course " + c.getName() + "max enrollment >= 0", " enrollment null or < 0");
	    if(c.getType() == null || c.getType() == "")
	    	throw new BadCourseDataException(BadCourseDataException.ConflictType.BAD_CTYPE, c,  "Course " + c.getName() + " type should be LEC/LAB/ACT/SEM/DIS/IND", "null or empty string");
	    if(c.getTypeEnum() == null)
	    	throw new BadCourseDataException(BadCourseDataException.ConflictType.BAD_CTYPE, c, "Course " + c.getName() + " type should be LEC/LAB/ACT/SEM/DIS/IND", "null enum type");
	    //all values are valid if(c.getUsedEquipment())
	    //if(c.getID()) all values valid according to kaylene 
	    //if(c.isTetheredToLecture()) not going to be null or empty string since its a boolean
	}
}
