package scheduler.model;

import java.util.HashMap;
import java.util.Map.Entry;

import scheduler.model.db.DatabaseException;
import scheduler.model.db.IDBCourse;
import scheduler.model.db.IDBCoursePreference;
import scheduler.model.db.IDBInstructor;
import scheduler.model.db.IDBTime;
import scheduler.model.db.IDBTimePreference;
import scheduler.model.db.IDatabase.NotFoundException;
import scheduler.model.Course;

public class Instructor extends ModelObject {
	public static final int DEFAULT_PREF = 0;
	
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

		if (!underlyingInstructor.isTransient())
			assert(!model.instructorCache.inCache(underlyingInstructor)); // make sure its not in the cache yet (how could it be, we're not even done with the constructor)
	}
	

	// PERSISTENCE FUNCTIONS

	public Instructor insert() throws DatabaseException {
		assert(document != null);
		preInsertOrUpdateSanityCheck();
		model.instructorCache.insert(this);
		putTimePreferencesIntoDB();
		putCoursePreferencesIntoDB();
		return this;
	}

	public void update() throws DatabaseException {
		removeTimePreferencesFromDB();
		removeCoursePreferencesFromDB();
		preInsertOrUpdateSanityCheck();
		model.instructorCache.update(this);
		putTimePreferencesIntoDB();
		putCoursePreferencesIntoDB();
	}

	public void delete() throws DatabaseException {
		removeTimePreferencesFromDB();
		removeCoursePreferencesFromDB();
		model.instructorCache.delete(this);
	}
	
	
	// ENTITY ATTRIBUTES
	
	public Integer getID() { return underlyingInstructor.getID(); }

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

	public int[][] getTimePreferences() throws DatabaseException {
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

	private void removeTimePreferencesFromDB() throws DatabaseException {
		for (IDBTimePreference timePref : model.database.findTimePreferencesByTimeForInstructor(underlyingInstructor).values())
			model.database.deleteTimePreference(timePref);
	}

	private void putTimePreferencesIntoDB() throws DatabaseException {
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
	
	public int getTimePreferences(Day day, int halfHour) throws DatabaseException {
		return getTimePreferences()[day.ordinal()][halfHour];
	}
	public void setTimePreferences(Day day, int halfHour, int preference) throws DatabaseException {
		this.getTimePreferences()[day.ordinal()][halfHour] = preference;
	}
	
	
	// Course Preferences
	
	public HashMap<Integer, Integer> getCoursePreferences() throws DatabaseException {
		if (!coursePreferencesLoaded) {
			coursePreferences = new HashMap<Integer, Integer>();
			for (Entry<IDBCourse, IDBCoursePreference> entry : model.database.findCoursePreferencesByCourseForInstructor(underlyingInstructor).entrySet())
				coursePreferences.put(entry.getKey().getID(), entry.getValue().getPreference());
			coursePreferencesLoaded = true;
		}
		
		for (Course c : this.getDocument().getCourses())
		{
			if (coursePreferences.get(c.getID()) == null)
			{
				coursePreferences.put(c.getID(), new Integer(0));
			}
		}
		
		/*for (Integer key : coursePreferences.keySet())
		{
			if (coursePreferences.get(key) == null)
			{
				coursePreferences.put(key, new Integer(0));
			}
		}*/
		return coursePreferences;
	}
	public Instructor setCoursePreferences(HashMap<Integer, Integer> coursePreferences) {
		this.coursePreferences = coursePreferences;
		coursePreferencesLoaded = true;
		return this;
	}


	private void removeCoursePreferencesFromDB() throws DatabaseException {
		for (IDBCoursePreference coursePref : model.database.findCoursePreferencesByCourseForInstructor(underlyingInstructor).values())
			model.database.deleteCoursePreference(coursePref);
	}
	
	private void putCoursePreferencesIntoDB() throws DatabaseException {
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
	
	public Document getDocument() throws DatabaseException {
		if (!documentLoaded) {
			assert(document == null);
			document = model.findDocumentByID(model.database.findDocumentForInstructor(underlyingInstructor).getID());
			documentLoaded = true;
		}
		return document;
	}

	public Instructor setDocument(Document newDocument) {
		assert(!newDocument.isTransient()); // You need to insert something before you can reference it
		document = newDocument;
		documentLoaded = true;
		return this;
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
	
	public String toString() {
		return this.getFirstName() + " " + this.getLastName();
	}
	
	public boolean equals(Object other) {
		if(this == other)
			return true;
		if((other == null) || (this.getClass() != other.getClass()))
			return false;
		Instructor instructor = (Instructor)other;
		return this.underlyingInstructor.equals(instructor.underlyingInstructor);
	}


	@Override
	public void preInsertOrUpdateSanityCheck() {
		assert getFirstName() != null : "firstname null";
		
		assert getLastName() != null : "lastname null";
		
		assert getUsername() != null : "username null";
		
		assert getMaxWTU() != null : "maxwtu null";
		
		if (documentLoaded)
			assert document != null : "doc null";
		
		if (timePreferencesLoaded)
			assert timePreferences != null : "timeprefs null";
		
		if (coursePreferencesLoaded)
			assert coursePreferences != null : "courseprefs null";
	}
}
