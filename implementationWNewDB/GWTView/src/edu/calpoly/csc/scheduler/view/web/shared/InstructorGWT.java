package edu.calpoly.csc.scheduler.view.web.shared;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;


public class InstructorGWT implements Serializable, Identified {
	private static final long serialVersionUID = -4982539363690274674L;
	
	private int id;
	private String username;
	private String firstName, lastName;
	private boolean disabilities;
	private String maxwtu;

	Map<Integer, Map<Integer, TimePreferenceGWT>> tPrefs;

	Map<Integer, Integer> coursePrefs;

	public InstructorGWT(int id, String username, String firstName,
			String lastName,
			boolean disabilities, String maxwtu,
			Map<Integer, Map<Integer, TimePreferenceGWT>> tPrefs,
			HashMap<Integer, Integer> hashMap) {
		super();
		this.id = id;
		this.username = username;
		this.firstName = firstName;
		this.lastName = lastName;
		this.disabilities = disabilities;
		this.maxwtu = maxwtu;
		this.tPrefs = tPrefs;
		this.coursePrefs = hashMap;
	}
	
	public InstructorGWT() {
		super();
	}

	public InstructorGWT(InstructorGWT that) {
		this(that.id, that.username, that.firstName, that.lastName,
				that.disabilities,
				that.maxwtu, null, null);
		
		Map<Integer, Map<Integer, TimePreferenceGWT>> newTPrefs = new TreeMap<Integer, Map<Integer,TimePreferenceGWT>>(); 
		for (Integer day : that.tPrefs.keySet()) {
			Map<Integer, TimePreferenceGWT> thatDayPrefs = that.tPrefs.get(day);
			Map<Integer, TimePreferenceGWT> newDayPrefs = new TreeMap<Integer, TimePreferenceGWT>();
			
			for (Integer time : thatDayPrefs.keySet()) {
				TimePreferenceGWT sourcePref = thatDayPrefs.get(time);
				newDayPrefs.put(time, new TimePreferenceGWT(sourcePref));
			}
			
			newTPrefs.put(day, newDayPrefs);
		}
		tPrefs = newTPrefs;
			
		Map<Integer, Integer> newCoursePrefs = new LinkedHashMap<Integer, Integer>(that.coursePrefs);
		//for (Integer course : that.coursePrefs.keySet())
			//newCoursePrefs.put(course, that.coursePrefs.get(course));
		coursePrefs = newCoursePrefs;
		
		verify();
	}

	public Map<Integer, Map<Integer, TimePreferenceGWT>> gettPrefs() {
		return tPrefs;
	}

	public void settPrefs(Map<Integer, Map<Integer, TimePreferenceGWT>> tPrefs) {
		this.tPrefs = tPrefs;
	}

	public void verify() {
		assert(username != null);
		assert(firstName != null);
		assert(lastName != null);
		assert(coursePrefs != null);
	}

	public Map<Integer, Integer> getCoursePreferences(){
		return coursePrefs;
	}
	
	public void setCoursePreferences(Map<Integer, Integer> coursePrefs){
		this.coursePrefs = coursePrefs;
	}
	
	public String getFirstName(){
		return firstName;
	}
	
	public String getLastName(){
		return lastName;
	}
	
	public boolean getDisabilities(){
		return disabilities;
	}
	
	public String getName() {
		return firstName;
	}

	public void setName(String name) {
		this.firstName = name;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public int getMaxWtu() {
		try { return Integer.parseInt(maxwtu); }
		catch (NumberFormatException e) { return 0; }
	}
	
	public String getRawMaxWtu() {
		return maxwtu;
	}

	public void setMaxWtu(String wtu) {
		this.maxwtu = wtu;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public void setDisabilities(boolean disabilities) {
		this.disabilities = disabilities;
	}
	
	public Integer getID() {
		return id;
	}

	public void setID(int id) {
		this.id = id;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof InstructorGWT))
			return false;
		InstructorGWT that = (InstructorGWT)obj;
		return this.id == that.id;
//		if (!(this.id == that.id &&
//				this.userID.equals(that.userID) &&
//				this.firstName.equals(that.firstName) &&
//				this.lastName.equals(that.lastName) &&
//				this.roomNumber.equals(that.roomNumber) &&
//				this.building.equals(that.building) &&
//				this.disabilities == that.disabilities &&
//				this.maxwtu == that.maxwtu &&
//				this.curwtu == that.curwtu &&
//				this.fairness == that.fairness &&
//				this.generosity == that.generosity))
//			return false;
//		// TODO: do we need to compare prefs and items taught?
//		return true;
	}
}
