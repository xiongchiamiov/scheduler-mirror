package scheduler.model.algorithm;

import java.util.Collection;
import java.util.Vector;

import scheduler.model.Location;
import scheduler.model.Course;
import scheduler.model.Instructor;
import scheduler.model.Model;
import scheduler.model.Schedule;
import scheduler.model.ScheduleItem;
import scheduler.model.db.DatabaseException;

public class GenerateEntryPoint {
	
	private static Vector<InstructorDecorator> insD;
	private static Vector<LocationDecorator> locD;
	
	public static Vector<ScheduleItem> generate(Model model, Schedule schedule, Collection<ScheduleItem> s_items, 
			Collection<Course> c_list, Collection<Instructor> i_coll,
			Collection<Location> l_coll) throws DatabaseException {
		
		if(model == null || schedule == null) 
			throw new NullPointerException();
		
		insD = new Vector<InstructorDecorator>();
		locD = new Vector<LocationDecorator>();
		
	    for(Instructor i : i_coll) {
			try {
				checkValid(i);
				insD.add(new InstructorDecorator(i));
			} catch (BadInstructorDataException e) {
				System.out.println("caught bad instructor exception");
				e.printStackTrace();
			}
	    }
	   
	    for(Location l : l_coll) {    	
			try {
				checkValid(l);
				locD.add(new LocationDecorator(l));
			} catch (BadLocationDataException e) {
				System.out.println("caught bad instructor exception");
				e.printStackTrace();
			}
	    }
		
		return Generate.generate(model, schedule, s_items, c_list, insD, locD);
	}
	
	private static void checkValid(Instructor ins) throws 
	BadInstructorDataException, DatabaseException{
		//validity checks
		if(ins==null)
			throw new BadInstructorDataException(BadInstructorDataException.ConflictType.IS_NULL,
					ins, "null", "instructor object");
		
		if(!ins.isSchedulable())
			throw new BadInstructorDataException(BadInstructorDataException.ConflictType.IS_NULL,
					ins, "null", "instructor object");
		checkInsTimePrefs(ins);
		checkInsCoursePrefs(ins);
	}
	
	private static void checkInsTimePrefs(Instructor ins) throws BadInstructorDataException, DatabaseException{
		if(ins.getCoursePreferences() == null)
			throw new BadInstructorDataException(BadInstructorDataException.ConflictType.NULL_C_PREFS,
					ins, "null", "instructor course prefs");
		if(ins.getTimePreferences() == null)
			throw new BadInstructorDataException(BadInstructorDataException.ConflictType.NULL_C_PREFS,
					ins, "null", "instructor course prefs");
		//int[][] prefs = ins.getTimePreferences();
	}
	
	private static void checkInsCoursePrefs(Instructor ins) {

	}
	
	private static void checkValid(Location loc) throws BadLocationDataException{
		//validity checks
		if(loc == null)
			throw new BadLocationDataException(BadLocationDataException.ConflictType.IS_NULL, loc, "null", "loc"); 

	}
	
}
