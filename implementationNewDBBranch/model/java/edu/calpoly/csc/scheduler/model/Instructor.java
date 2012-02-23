package edu.calpoly.csc.scheduler.model;

import java.util.HashMap;
import java.util.Map.Entry;

import edu.calpoly.csc.scheduler.model.db.IDBCourse;
import edu.calpoly.csc.scheduler.model.db.IDBCoursePreference;
import edu.calpoly.csc.scheduler.model.db.IDBInstructor;
import edu.calpoly.csc.scheduler.model.db.IDBTime;
import edu.calpoly.csc.scheduler.model.db.IDBTimePreference;
import edu.calpoly.csc.scheduler.model.db.IDatabase;
import edu.calpoly.csc.scheduler.model.db.IDatabase.NotFoundException;

public class Instructor implements Identified {
	public static final int DEFAULT_PREF = 5;
	
	private final Model model;
	
	IDBInstructor underlyingInstructor;

	private boolean documentLoaded;
	private Document document;
	
	boolean timePreferencesLoaded;
	int[][] timePreferences;
	
	boolean coursePreferencesLoaded;
	HashMap<Integer, Integer> coursePreferences;
	
	Instructor(Model model, IDBInstructor underlyingInstructor) {
		this.model = model;
		this.underlyingInstructor = underlyingInstructor;
	}
	

	// PERSISTENCE FUNCTIONS

	public Instructor insert(Document containingDocument) {
		model.database.insertInstructor(containingDocument.underlyingDocument, underlyingInstructor);
		putTimePreferencesIntoDB();
		putCoursePreferencesIntoDB();
		return this;
	}

	public void update() {
		putTimePreferencesIntoDB();
		putCoursePreferencesIntoDB();
		model.database.updateInstructor(underlyingInstructor);
	}

	public void delete() {
		removeTimePreferencesFromDB();
		removeCoursePreferencesFromDB();
		model.database.deleteInstructor(underlyingInstructor);
	}
	
	
	// ENTITY ATTRIBUTES
	
	public int getID() { return underlyingInstructor.getID(); }

	public String getFirstName() { return underlyingInstructor.getFirstName(); }
	public void setFirstName(String string) { underlyingInstructor.setFirstName(string); }

	public String getLastName() { return underlyingInstructor.getLastName(); }
	public void setLastName(String lastName) { underlyingInstructor.setLastName(lastName); }
	
	public String getUsername() { return underlyingInstructor.getUsername(); }
	public void setUsername(String username) { underlyingInstructor.setUsername(username); }
	
	public String getMaxWTU() { return underlyingInstructor.getMaxWTU(); }
	public void setMaxWTU(String maxWTU) { underlyingInstructor.setMaxWTU(maxWTU); }

	public void setIsSchedulable(boolean schedulable) { underlyingInstructor.setIsSchedulable(schedulable); }
	public boolean isSchedulable() { return underlyingInstructor.isSchedulable(); }

	public int getMaxWTUInt() { return Integer.parseInt(getMaxWTU()); }

	
	
	
	// ENTITY RELATIONS
	
	
	// Time Preferences

	public int[][] getTimePreferences() {
		if (!timePreferencesLoaded) {
			timePreferences = new int[Day.values().length][48];
			
			for (Day day : Day.values())
				for (int halfHour = 0; halfHour < 48; halfHour++)
					timePreferences[day.ordinal()][halfHour] = Instructor.DEFAULT_PREF;
			
			for (Entry<IDBTime, IDBTimePreference> entry : model.database.findTimePreferencesByTimeForInstructor(underlyingInstructor).entrySet()) {
				IDBTime time = entry.getKey();
				IDBTimePreference pref = entry.getValue();
				Day day = Day.values()[time.getDay()];
				int halfHour = time.getHalfHour();
				
				timePreferences[day.ordinal()][halfHour] = pref.getPreference();
			}
			
			timePreferencesLoaded = true;
		}
		
		return timePreferences;
	}
	public Instructor setTimePreferences(int[][] timePreferences) {
		timePreferencesLoaded = true;
		this.timePreferences = timePreferences;
		return this;
	}

	private void removeTimePreferencesFromDB() {
		for (IDBTimePreference timePref : model.database.findTimePreferencesByTimeForInstructor(underlyingInstructor).values())
			model.database.deleteTimePreference(timePref);
	}

	private void putTimePreferencesIntoDB() {
		if (!timePreferencesLoaded)
			return;
		
		for (Day day : Day.values()) {
			for (int halfHour = 0; halfHour < 48; halfHour++) {
				IDBTime time = model.database.findTimeByDayAndHalfHour(day.ordinal(), halfHour); 
				
				int preference = timePreferences[day.ordinal()][halfHour];
				model.database.insertTimePreference(underlyingInstructor, time, model.database.assembleTimePreference(preference));
			}
		}
	}
	
	public int getTimePreferences(Day day, int halfHour) {
		return getTimePreferences()[day.ordinal()][halfHour];
	}
	public void setTimePreferences(Day day, int halfHour, int preference) {
		this.getTimePreferences()[day.ordinal()][halfHour] = preference;
	}
	
	
	// Course Preferences
	
	public HashMap<Integer, Integer> getCoursePreferences() {
		if (!coursePreferencesLoaded) {
			coursePreferences = new HashMap<Integer, Integer>();
			for (Entry<IDBCourse, IDBCoursePreference> entry : model.database.findCoursePreferencesByCourseForInstructor(underlyingInstructor).entrySet())
				coursePreferences.put(entry.getKey().getID(), entry.getValue().getPreference());
			coursePreferencesLoaded = true;
		}
		
		return coursePreferences;
	}
	public Instructor setCoursePreferences(HashMap<Integer, Integer> coursePreferences) {
		this.coursePreferences = coursePreferences;
		coursePreferencesLoaded = true;
		return this;
	}


	private void removeCoursePreferencesFromDB() {
		for (IDBCoursePreference coursePref : model.database.findCoursePreferencesByCourseForInstructor(underlyingInstructor).values())
			model.database.deleteCoursePreference(coursePref);
	}
	
	private void putCoursePreferencesIntoDB() {
		if (!coursePreferencesLoaded)
			return;
		
		for (Entry<Integer, Integer> coursePreference : coursePreferences.entrySet()) {
			int courseID = coursePreference.getKey();
			int preference = coursePreference.getValue();
			
			try {
				model.database.insertCoursePreference(underlyingInstructor, model.database.findCourseByID(courseID), model.database.assembleCoursePreference(preference));
			} catch (NotFoundException e) {
				throw new AssertionError(e);
			}
		}
	}

	
	// Document
	
	public Document getDocument() throws NotFoundException {
		if (!documentLoaded) {
			assert(document == null);
			document = model.findDocumentByID(model.database.findDocumentForInstructor(underlyingInstructor).getID());
			documentLoaded = true;
		}
		return document;
	}

	public void setDocument(Document newDocument) {
		document = newDocument;
		documentLoaded = true;
	}
	
	
	
	// Utilities
	
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


}
