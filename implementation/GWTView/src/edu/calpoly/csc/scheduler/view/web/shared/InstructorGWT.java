package edu.calpoly.csc.scheduler.view.web.shared;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Vector;

import com.google.gwt.user.client.Window;

import edu.calpoly.csc.scheduler.model.db.Time;
import edu.calpoly.csc.scheduler.model.db.idb.TimePreference;
import edu.calpoly.csc.scheduler.model.schedule.Day;
import edu.calpoly.csc.scheduler.model.schedule.WeekAvail;


public class InstructorGWT implements Serializable{
	private static final long serialVersionUID = -4982539363690274674L;
	
	private String userID;
	private String firstName, lastName, roomNumber, building;
	private boolean disabilities;
	private int maxwtu, curwtu, fairness, generosity;
	
//	private WeekAvailGWT availability; //will be objects

	Vector<ScheduleItemGWT> itemsTaught;

//	HashMap<Day, LinkedHashMap<Time, TimePreference>> tPrefs;

	HashMap<CourseGWT, Integer> coursePrefs;
	
	private int scheduleID;
	
	public void verify() {
		if (userID == null)
			Window.alert("zerp1");
		if (firstName == null)
			Window.alert("zerp2");
		if (lastName == null)
			Window.alert("zerp3");
		if (roomNumber == null)
			Window.alert("zerp4");
		if (building == null)
			Window.alert("zerp5");
		if (coursePrefs == null)
			Window.alert("zerp8");
		if (itemsTaught == null)
			Window.alert("zerp10");
	}

	public Vector<ScheduleItemGWT> getItemsTaugh(){
		return itemsTaught;
	}
	
	public void setItemsTaught(Vector<ScheduleItemGWT> items){
		this.itemsTaught = items;
	}
	
	public HashMap<CourseGWT, Integer> getCoursePreferences(){
		return coursePrefs;
	}
	
	public void setCoursePreferences(HashMap<CourseGWT, Integer> coursePrefs){
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
