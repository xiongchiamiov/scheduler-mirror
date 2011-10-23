package edu.calpoly.csc.scheduler.view.web.shared;

import java.io.Serializable;


public class InstructorGWT implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4982539363690274674L;
	private String name, userID, office;
	private String firstName, lastName, roomNumber, building;
	private boolean disabilities;
	private int wtu;

	public InstructorGWT(){}
	
	public InstructorGWT(String name, String userID,
			int wtu, String office) {
		super();
		this.name = name;
		this.userID = userID;
		this.wtu = wtu;
		this.office = office;
	}
	
	public InstructorGWT(String firstName, String lastName, String userID,
			int wtu, String building, String roomNumber, boolean disabilities) {
		super();
		this.firstName = firstName;
		this.lastName = lastName;
		this.userID = userID;
		this.wtu = wtu;
		this.building = building;
		this.roomNumber = roomNumber;
		this.disabilities = disabilities;
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
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUserID() {
		return userID;
	}

	public void setUserID(String userID) {
		this.userID = userID;
	}

	public int getWtu() {
		return wtu;
	}

	public void setWtu(int wtu) {
		this.wtu = wtu;
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
