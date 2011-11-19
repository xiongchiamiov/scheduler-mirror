package edu.calpoly.csc.scheduler.view.web.shared;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;


public class InstructorGWT implements Serializable, Comparable<InstructorGWT>{
	private static final long serialVersionUID = -4982539363690274674L;
	
	private int id;
	private String userID;
	private String firstName, lastName, roomNumber, building;
	private boolean disabilities;
	private int maxwtu, curwtu, fairness, generosity;

	Vector<ScheduleItemGWT> itemsTaught;

	Map<DayGWT, Map<TimeGWT, TimePreferenceGWT>> tPrefs;

	Map<CourseGWT, Integer> coursePrefs;
	
	
	
	public InstructorGWT() {
		super();
	}

	public InstructorGWT(int id) {
		super();
		this.id = id;
		this.userID = "";
		this.firstName = "";
		this.lastName = "";
		this.roomNumber = "";
		this.building = "";
		this.disabilities = false;
		this.maxwtu = 4;
		this.curwtu = 0;
		this.fairness = 0;
		this.generosity = 0;
		this.itemsTaught = new Vector<ScheduleItemGWT>();
		this.tPrefs = new HashMap<DayGWT, Map<TimeGWT, TimePreferenceGWT>>();
		this.coursePrefs = new HashMap<CourseGWT, Integer>();
	}

	public InstructorGWT clone() {
		InstructorGWT instructor = new InstructorGWT();
		instructor.setUserID(userID);
		instructor.setFirstName(firstName);
		instructor.setLastName(lastName);
		instructor.setRoomNumber(roomNumber);
		instructor.setBuilding(building);
		instructor.setDisabilities(disabilities);
		instructor.setMaxWtu(maxwtu);
		instructor.setCurWtu(curwtu);
		instructor.setFairness(fairness);
		instructor.setGenerosity(generosity);
		instructor.setItemsTaught((Vector<ScheduleItemGWT>)itemsTaught.clone());
		
		Map<DayGWT, Map<TimeGWT, TimePreferenceGWT>> newTPrefs = new TreeMap<DayGWT, Map<TimeGWT,TimePreferenceGWT>>(); 
		for (DayGWT day : tPrefs.keySet()) {
			Map<TimeGWT, TimePreferenceGWT> dayPrefs = tPrefs.get(day);
			
			DayGWT newDay = day.clone();
			Map<TimeGWT, TimePreferenceGWT> newDayPrefs = new TreeMap<TimeGWT, TimePreferenceGWT>();
			
			for (TimeGWT time : dayPrefs.keySet()) {
				TimePreferenceGWT pref = dayPrefs.get(time);

				TimeGWT newTime = time.clone();
				TimePreferenceGWT newPref = pref.clone();
				newDayPrefs.put(newTime, newPref);
			}
			
			newTPrefs.put(newDay, newDayPrefs);
		}
		instructor.settPrefs(newTPrefs);
		
		Map<CourseGWT, Integer> newCoursePrefs = new LinkedHashMap<CourseGWT, Integer>();
		for (CourseGWT course : coursePrefs.keySet())
			newCoursePrefs.put(course.clone(), coursePrefs.get(course));
		instructor.setCoursePreferences(newCoursePrefs);
		
		instructor.verify();
		
		return instructor;
	}
	
//	private WeekAvailGWT availability; //will be objects

	public Map<DayGWT, Map<TimeGWT, TimePreferenceGWT>> gettPrefs() {
		return tPrefs;
	}

	public void settPrefs(
			Map<DayGWT, Map<TimeGWT, TimePreferenceGWT>> tPrefs) {
		this.tPrefs = tPrefs;
	}

	public void verify() {
		assert(userID != null);
		assert(firstName != null);
		assert(lastName != null);
		assert(roomNumber != null);
		assert(building != null);
		assert(coursePrefs != null);
		assert(itemsTaught != null);
	}

	public Vector<ScheduleItemGWT> getItemsTaugh(){
		return itemsTaught;
	}
	
	public void setItemsTaught(Vector<ScheduleItemGWT> items){
		this.itemsTaught = items;
	}
	
	public Map<CourseGWT, Integer> getCoursePreferences(){
		return coursePrefs;
	}
	
	public void setCoursePreferences(Map<CourseGWT, Integer> coursePrefs){
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
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	@Override
	public int compareTo(InstructorGWT that) {
		if(!this.userID.equals(that.userID)){ return this.userID.compareTo(that.userID);}
		if(!this.firstName.equals(that.firstName)){ return this.firstName.compareTo(that.firstName);}
		if(!this.lastName.equals(that.lastName)){ return this.lastName.compareTo(that.lastName);}
		if(!this.roomNumber.equals(that.roomNumber)){ return this.roomNumber.compareTo(that.roomNumber);}
		if(!this.building.equals(that.building)){ return this.building.compareTo(that.building);}
		if(this.disabilities != that.disabilities){ return (this.disabilities ? 1 : -1); }
		if(this.maxwtu != that.maxwtu){ return this.maxwtu - that.maxwtu; }
		if(this.curwtu != that.curwtu){ return this.curwtu - that.curwtu; }
		if(this.fairness != that.fairness){ return this.fairness - that.fairness; }
		if(this.generosity != that.generosity){ return this.generosity - that.generosity; }
		
		return 0;
	}
	
	@Override
	public boolean equals(Object obj) {
		return obj instanceof InstructorGWT && id == ((InstructorGWT)obj).id;
	}
	
	@Override
	public int hashCode() {
		return id;
	}
}
