package scheduler.model.algorithm;

import java.util.Collection;
import java.util.HashMap;

import scheduler.model.Course;
import scheduler.model.Day;
import scheduler.model.Document;
import scheduler.model.Instructor;
import scheduler.model.db.DatabaseException;

public class InstructorDecorator {

	private Instructor instructor;
	private Integer WTU;
	private WeekAvail availability;
	
	public InstructorDecorator(Instructor ins) {
		this.instructor =  ins;
		this.WTU = Integer.valueOf(0);
		this.availability = new WeekAvail();
	}
	
	/*
	 * Only call this for staff
	 */
	public InstructorDecorator(Document doc, Collection<Course> c_list) throws DatabaseException {
		 assert(doc.getStaffInstructor() != null);
		 this.instructor = doc.getStaffInstructor(); 
		 
		 //set staff preferences
		 HashMap<Integer, Integer> coursePrefs = new HashMap<Integer, Integer>();
		 for (Course course : c_list) {
		    	coursePrefs.put(course.getID(), 10);
		 }
		 this.instructor.setCoursePreferences(coursePrefs);
		    
		 int[][] timePrefs = Instructor.createUniformTimePreferences(10);
		 this.instructor.setTimePreferences(timePrefs);
		    
		 this.instructor.update();
	}
	
	public void subtractWTU(Integer wtu) {
		this.WTU -= wtu;
	}
	
	public void addWTU(Integer wtu) {
		this.WTU += wtu;
	}
	
	public int getCurWTU() {
		return this.WTU.intValue();
	}
	
	public WeekAvail getAvailability() {
		return this.availability;
	}
	
	public int getMaxWTUInt() {
		return this.instructor.getMaxWTUInt();
	}
	
	public boolean isStaffInstructor() throws DatabaseException {
		return ((this.instructor.getDocument().getStaffInstructor()).getID().equals(this.instructor.getID()));
	}
	
	public boolean checkWTUs(Course course) {
		return ((this.WTU.intValue() + course.getWTUInt()) <= this.instructor.getMaxWTUInt());
	}
	
	public boolean preferenceForCourse(Course course) throws DatabaseException {
		return (this.instructor.getCoursePreferences().get(course.getID()) > 0);
	}
	
	public int actualCoursePreferenceAsInt(Course course) throws DatabaseException {
		return new Integer((this.instructor.getCoursePreferences().get(course.getID())));
	}
	
	public int getTimePreferenceFor(Day d, int time) throws DatabaseException {
		return (this.instructor.getTimePreferences(d, time));
	}
	
	public Integer getInstructorID() {
		return (this.instructor.getID());
	}
	
	public static int getDefaultPref() {
		return (Instructor.DEFAULT_PREF);
	}
	
	public boolean equals(Instructor ins) {
		return (this.instructor.equals(ins));
	}
	
	/*
	 * Only for very particular instances should this be used. And only
	 * for input into another function as required. Usage of this in other cases is grounds for 
	 * looks of disapproval.
	 */
	
	public Instructor getInstructor() {
		return this.instructor;
	}
	
	public static Instructor getStaff(Document doc) throws DatabaseException {
		assert doc!=null;
		return doc.getStaffInstructor();
	}
}
