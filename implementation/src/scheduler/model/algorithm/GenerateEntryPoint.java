package scheduler.model.algorithm;

import java.util.Collection;
import java.util.Vector;

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

		return Generate.generate(model, document, s_items, c_list, insD, locD);
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
			throw new BadLocationDataException(BadLocationDataException.ConflictType.IS_NULL, loc, "null", "loc"); 
		if(loc.getDocument()==null)
			throw new BadLocationDataException(BadLocationDataException.ConflictType.IS_NULL, loc, "null", "document"); 
		if(loc.getDocument().getTBALocation().getID().equals(loc.getID()))
			throw new BadLocationDataException(BadLocationDataException.ConflictType.IS_TBA, loc, "tba", "loc"); 
		//dont care case if(loc.getDocument().g)
		if(!loc.isSchedulable())
			throw new BadLocationDataException(BadLocationDataException.ConflictType.NOT_SCHEDULABLE, loc, "tba", "loc"); 
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
			throw new BadCourseDataException(BadCourseDataException.ConflictType.IS_NULL, c, "null", "course");
		if(c.getDocument() == null)
			throw new BadCourseDataException(BadCourseDataException.ConflictType.IS_NULL, c, "null", "document");
		if(!c.isSchedulable())
			throw new BadCourseDataException(BadCourseDataException.ConflictType.NOT_SCHEDULABLE, c, "not schedulable", "true");
		if(c.getDepartment() == null)
			throw new BadCourseDataException(BadCourseDataException.ConflictType.NULL_DPTMT, c, "null", "department");
	    if(c.getCatalogNumber() == null || c.getCatalogNumber() == "")
	    	throw new BadCourseDataException(BadCourseDataException.ConflictType.BAD_CAT_NUM, c, "null or empty", "valid string");
		if(c.getName() == null)
			throw new BadCourseDataException(BadCourseDataException.ConflictType.NULL_CNAME, c, "null", "non-null string");
	    if(c.getNumSections() == null || c.getNumSectionsInt() <= 0)
	    	throw new BadCourseDataException(BadCourseDataException.ConflictType.BAD_NUMSECTS, c, "null or <= 0", "integer > 0");
	    if(c.getWTU() == null || c.getWTUInt() <= 0)
			throw new BadCourseDataException(BadCourseDataException.ConflictType.BAD_NUMWTU, c, "null or <= 0", "integer > 0");
	    if(c.getSCU() == null || c.getSCUInt() <= 0)
	    	throw new BadCourseDataException(BadCourseDataException.ConflictType.BAD_NUMSCU, c, "null or <= 0", "integer > 0");
	    
	    //check requirements for day combos still
	    //c.getLecture().getID(), c
	    if(c.isTetheredToLecture())
	    	if(c.getLecture() == null || c.getLecture().getID() == null)
	    		throw new BadCourseDataException(BadCourseDataException.ConflictType.TETHERED_NULL_LECT, c, "null", "course");
//	    	throw new ()
//	    if(c.getLecture().getID())
	    
	    //there should be an extra reqt..it can't be higher than soemthing (check)
	    
	    if(c.getNumHalfHoursPerWeek() == null || c.getNumHalfHoursPerWeekInt() <= 0)
	    	throw new BadCourseDataException(BadCourseDataException.ConflictType.BAD_HHR_WEEK, c, "null or <= 0", "integer > 0");
	    //adding the reqt of 0 being valid. a course with 0 as max enrollment makes no sense to me
	    if(c.getMaxEnrollment() == null || c.getMaxEnrollmentInt() <= 0)
	    	throw new BadCourseDataException(BadCourseDataException.ConflictType.BAD_MAXENR, c, "null or <= 0", "integer > 0");
	    if(c.getType() == null || c.getType() == "")
	    	throw new BadCourseDataException(BadCourseDataException.ConflictType.BAD_CTYPE, c, "null or empty", "LEC/LAB/ACT/SEM/DIS/IND");
	    if(c.getTypeEnum() == null)
	    	throw new BadCourseDataException(BadCourseDataException.ConflictType.BAD_CTYPE, c, "null or empty", "LEC/LAB/ACT/SEM/DIS/IND");
	    //all values are valid if(c.getUsedEquipment())
	    //if(c.getID()) all values valid according to kaylene 
	    //if(c.isTetheredToLecture()) not going to be null or empty string since its a boolean
	}
}
