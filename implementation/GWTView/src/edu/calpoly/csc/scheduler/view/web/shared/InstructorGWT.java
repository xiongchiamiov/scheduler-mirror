package edu.calpoly.csc.scheduler.view.web.shared;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;


public class InstructorGWT implements Serializable{
	private static final long serialVersionUID = -4982539363690274674L;
	
	private String userID;
	private String firstName, lastName, roomNumber, building;
	private boolean disabilities;
	private int maxwtu, curwtu, fairness, generosity;

	Vector<ScheduleItemGWT> itemsTaught;

	Map<DayGWT, Map<TimeGWT, TimePreferenceGWT>> tPrefs;

	Map<CourseGWT, Integer> coursePrefs;
	
	private int scheduleID;
	
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
	
	public int getScheduleID(){
		return scheduleID;
	}
	
	public void setScheduleID(int sched){
		this.scheduleID = sched;
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
}
