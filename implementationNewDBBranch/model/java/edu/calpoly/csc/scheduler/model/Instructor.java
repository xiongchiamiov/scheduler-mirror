package edu.calpoly.csc.scheduler.model;

import java.util.HashMap;

import edu.calpoly.csc.scheduler.model.db.IDBInstructor;

public class Instructor {
	IDBInstructor underlyingInstructor;
	HashMap<Integer, HashMap<Integer, Integer>> timePreferences;
	HashMap<Integer, Integer> coursePreferences;
	
	Instructor(IDBInstructor underlyingInstructor,
			HashMap<Integer, HashMap<Integer, Integer>> timePreferences,
			HashMap<Integer, Integer> coursePreferences) {
		this.underlyingInstructor = underlyingInstructor;
		this.timePreferences = timePreferences;
		this.coursePreferences = coursePreferences;
	}
	
	public int getID() { return underlyingInstructor.getID(); }

	public String getFirstName() { return underlyingInstructor.getFirstName(); }
	public void setFirstName(String string) { underlyingInstructor.setFirstName(string); }

	public String getLastName() { return underlyingInstructor.getLastName(); }
	public void setLastName(String lastName) { underlyingInstructor.setLastName(lastName); }
	
	public String getUsername() { return underlyingInstructor.getUsername(); }
	public void setUsername(String username) { underlyingInstructor.setUsername(username); }
	
	public String getMaxWTU() { return underlyingInstructor.getMaxWTU(); }
	public void setMaxWTU(String maxWTU) { underlyingInstructor.setMaxWTU(maxWTU); }

	public HashMap<Integer, HashMap<Integer, Integer>> getTimePreferences() { return timePreferences; }
	public void setTimePreferences(HashMap<Integer, HashMap<Integer, Integer>> timePreferences) { this.timePreferences = timePreferences; }
	public HashMap<Integer, Integer> getCoursePreferences() { return coursePreferences; }
	public void setCoursePreferences(HashMap<Integer, Integer> coursePreferences) { this.coursePreferences = coursePreferences; }

	public void setIsSchedulable(boolean schedulable) { underlyingInstructor.setIsSchedulable(schedulable); }
	public boolean isSchedulable() { return underlyingInstructor.isSchedulable(); }
}
