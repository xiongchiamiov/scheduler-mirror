package edu.calpoly.csc.scheduler.model;

import java.util.HashMap;

import edu.calpoly.csc.scheduler.model.db.IDBInstructor;

public class Instructor implements Identified {
	public static final int DEFAULT_PREF = 5;
	
	public static int[][] createUniformTimePreferences(int value) {
		int[][] result = new int[Day.values().length][48];
		for (Day day : Day.values())
			for (int halfHour = 0; halfHour < 48; halfHour++)
				result[day.ordinal()][halfHour] = value;
		return result;
	}
	
	public static int[][] createDefaultTimePreferences() {
		return createUniformTimePreferences(DEFAULT_PREF);
	}
	
	IDBInstructor underlyingInstructor;
	int[][] timePreferences;
	HashMap<Integer, Integer> coursePreferences;
	
	Instructor(IDBInstructor underlyingInstructor,
			int[][] timePreferences,
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

	public int[][] getTimePreferences() { return timePreferences; }
	public int getTimePreferences(Day day, int halfHour) {
		return timePreferences[day.ordinal()][halfHour];
	}
	public void setTimePreferences(Day day, int halfHour, int preference) {
		this.timePreferences[day.ordinal()][halfHour] = preference;
	}
	public void setTimePreferences(int[][] timePreferences) {
		this.timePreferences = timePreferences;
	}
	
	public HashMap<Integer, Integer> getCoursePreferences() { return coursePreferences; }
	public void setCoursePreferences(HashMap<Integer, Integer> coursePreferences) { this.coursePreferences = coursePreferences; }

	public void setIsSchedulable(boolean schedulable) { underlyingInstructor.setIsSchedulable(schedulable); }
	public boolean isSchedulable() { return underlyingInstructor.isSchedulable(); }
}
