package edu.calpoly.csc.scheduler.view.web.shared;

import java.io.Serializable;


public class InstructorGWT implements Serializable{
	private static final long serialVersionUID = -4158472135773670339L;
	
	private String firstName, lastName, userID, officeBldg, officeRoom;

	public InstructorGWT(){}
	
	public InstructorGWT(String firstName, String lastName, String userID,
			String officeBldg, String officeRoom) {
		super();
		this.firstName = firstName;
		this.lastName = lastName;
		this.userID = userID;
		this.officeBldg = officeBldg;
		this.officeRoom = officeRoom;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getUserID() {
		return userID;
	}

	public void setUserID(String userID) {
		this.userID = userID;
	}

	public String getOfficeBldg() {
		return officeBldg;
	}

	public void setOfficeBldg(String officeBldg) {
		this.officeBldg = officeBldg;
	}

	public String getOfficeRoom() {
		return officeRoom;
	}

	public void setOfficeRoom(String officeRoom) {
		this.officeRoom = officeRoom;
	}
}
