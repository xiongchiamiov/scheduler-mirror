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
		
		insD = new Vector<InstructorDecorator>();
		locD = new Vector<LocationDecorator>();
		
	    for(Instructor i : i_coll) {
	    	try {
				insD.add(checkValid(i));
			} catch (BadInstructorDataException e) {
				e.printStackTrace();
			}
	    }
	   
	    for(Location l : l_coll) {
	    	try {
				locD.add(checkValid(l));
			} catch (BadLocationDataException e) {
				e.printStackTrace();
			}
	    }
		
		
		return Generate.generate(model, schedule, s_items, c_list, insD, locD);
	}
	
	private static InstructorDecorator checkValid(Instructor ins) throws BadInstructorDataException{
		//validity checks
		//if(ins!=null)
			return new InstructorDecorator(ins);
	}
	
	private static LocationDecorator checkValid(Location loc) throws BadLocationDataException{
		//validity checks
		return new LocationDecorator(loc);
	}
	
}
