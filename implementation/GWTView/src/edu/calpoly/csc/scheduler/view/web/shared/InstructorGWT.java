package edu.calpoly.csc.scheduler.view.web.shared;

import java.io.Serializable;


public class InstructorGWT implements Serializable{
	private static final long serialVersionUID = -539236134373434229L;
	
	private String name, userID, office;
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
}
