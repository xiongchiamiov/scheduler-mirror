package edu.calpoly.csc.scheduler.view.web.shared;

import java.io.Serializable;


public class InstructorGWT implements Serializable{
	private static final long serialVersionUID = -4982539363690274674L;
	
	private String userID;
	private String firstName, lastName, roomNumber, building;
	private boolean disabilities;
	private int maxwtu, curwtu, fairness, generosity;
	
	private String office, availability, coursePrefs, tPrefs, itemsTaught; //will be objects
	
	private String quarterID;
	
	private int scheduleID;

	public InstructorGWT(){
		userID = "";
		office = "";
		firstName = "";
		lastName = "";
		roomNumber = "";
		building = "";
		disabilities = false;
		maxwtu = 0;
	}
	
	public InstructorGWT(String name, String userID,
			int wtu, String office) {
		super();
		this.firstName = name;
		this.userID = userID;
		this.maxwtu = wtu;
		this.office = office;
	}
	
	public InstructorGWT(String firstName, String lastName, String userID,
			int wtu, String building, String roomNumber, boolean disabilities) {
		super();
		this.firstName = firstName;
		this.lastName = lastName;
		this.userID = userID;
		this.maxwtu = wtu;
		this.building = building;
		this.roomNumber = roomNumber;
		this.disabilities = disabilities;
	}
	
	public String getItemsTaugh(){
		return itemsTaught;
	}
	
	public void setItemsTaught(String items){
		this.itemsTaught = items;
	}
	
	public String getAvailability(){
		return availability;
	}
	
	public void setAvailability(String avail){
		this.availability = avail;
	}
	
	public String getCoursePreferences(){
		return coursePrefs;
	}
	
	public void setCoursePreferences(String coursePrefs){
		this.coursePrefs = coursePrefs;
	}
	
	public String getTPreferences(){
		return tPrefs;
	}
	
	public void setTPreferences(String tprefs){
		this.tPrefs = tprefs;
	}
	
	public int getCurWtu(){
		return curwtu;
	}
	
	public void setCurWtu(int curWtu){
		this.curwtu = curWtu;
	}
	
	public String getQuarterID(){
		return quarterID;
	}
	
	public void setQuarterID(String quarterID){
		this.quarterID = quarterID;
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

	public String getOffice() {
		return office;
	}

	public void setOffice(String office) {
		this.office = office;
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
