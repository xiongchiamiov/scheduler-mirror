package edu.calpoly.csc.scheduler.view.web.shared;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;


public class InstructorGWT implements Serializable, Identified {
	private static final long serialVersionUID = -4982539363690274674L;
	
	private int id;
	private String userID;
	private String firstName, lastName, roomNumber, building;
	private boolean disabilities;
	private int maxwtu, curwtu, fairness, generosity;

	Map<Integer, Map<Integer, TimePreferenceGWT>> tPrefs;

	Map<Integer, Integer> coursePrefs;

	public InstructorGWT(int id, String userID, String firstName,
			String lastName, String roomNumber, String building,
			boolean disabilities, int maxwtu, int curwtu, int fairness,
			int generosity,
			Map<Integer, Map<Integer, TimePreferenceGWT>> tPrefs,
			HashMap<Integer, Integer> hashMap) {
		super();
		this.id = id;
		this.userID = userID;
		this.firstName = firstName;
		this.lastName = lastName;
		this.roomNumber = roomNumber;
		this.building = building;
		this.disabilities = disabilities;
		this.maxwtu = maxwtu;
		this.curwtu = curwtu;
		this.fairness = fairness;
		this.generosity = generosity;
		this.tPrefs = tPrefs;
		this.coursePrefs = hashMap;
	}
	
	public InstructorGWT() {
		super();
	}

	public InstructorGWT(InstructorGWT that) {
		this(that.id, that.userID, that.firstName, that.lastName,
				that.roomNumber, that.building, that.disabilities,
				that.maxwtu, that.curwtu, that.fairness, that.generosity, null, null);
		
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
			
		Map<Integer, Integer> newCoursePrefs = new LinkedHashMap<Integer, Integer>();
		for (Integer course : that.coursePrefs.keySet())
			newCoursePrefs.put(course, that.coursePrefs.get(course));
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
		assert(userID != null);
		assert(firstName != null);
		assert(lastName != null);
		assert(roomNumber != null);
		assert(building != null);
		assert(coursePrefs != null);
	}

	public Map<Integer, Integer> getCoursePreferences(){
		return coursePrefs;
	}
	
	public void setCoursePreferences(Map<Integer, Integer> coursePrefs){
		this.coursePrefs = coursePrefs;
	}
	
	public int getCurWtu(){
		return curwtu;
	}
	
	public void setCurWtu(int curWtu){
		this.curwtu = curWtu;
	}
	
	public int getFairness(){
		return fairness;
	}
	
	public void setFairness(int fair){
		this.fairness = fair;
	}
	
	public int getGenerosity(){
		return generosity;
	}
	
	public void setGenerosity(int gen){
		this.generosity = gen;
	}
	
	public String getFirstName(){
		return firstName;
	}
	
	public String getLastName(){
		return lastName;
	}
	
	public String getBuilding(){
		return building;
	}
	
	public String getRoomNumber(){
		return roomNumber;
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

	public String getUserID() {
		return userID;
	}

	public void setUserID(String userID) {
		this.userID = userID;
	}

	public int getMaxWtu() {
		return maxwtu;
	}

	public void setMaxWtu(int wtu) {
		this.maxwtu = wtu;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public void setRoomNumber(String roomNumber) {
		this.roomNumber = roomNumber;
	}

	public void setBuilding(String building) {
		this.building = building;
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
		if (!(this.id == that.id &&
				this.userID.equals(that.userID) &&
				this.firstName.equals(that.firstName) &&
				this.lastName.equals(that.lastName) &&
				this.roomNumber.equals(that.roomNumber) &&
				this.building.equals(that.building) &&
				this.disabilities == that.disabilities &&
				this.maxwtu == that.maxwtu &&
				this.curwtu == that.curwtu &&
				this.fairness == that.fairness &&
				this.generosity == that.generosity))
			return false;
		// TODO: do we need to compare prefs and items taught?
		return true;
	}
}
