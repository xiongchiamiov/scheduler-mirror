package edu.calpoly.csc.scheduler.view.web.shared;

import java.io.Serializable;

public class DocumentGWT implements Serializable {
	private static final long serialVersionUID = 1L;
	
	int id;
	String name;
	int scheduleID;
	int staffInstructorID;
	int tbaLocationID;
	boolean isTrashed;
	int startHalfHour;
	int endHalfHour;
	
	public DocumentGWT() { }
	
	public DocumentGWT(int id, String name, int scheduleID, int staffInstructorID, int tbaLocationID, boolean isTrashed) {
		this.id = id;
		this.name = name;
		this.scheduleID = scheduleID;
		this.staffInstructorID = staffInstructorID;
		this.tbaLocationID = tbaLocationID;
		this.isTrashed = isTrashed;
	}
	
	public DocumentGWT(DocumentGWT that) {
		this(that.id, that.name, that.scheduleID, that.staffInstructorID, that.tbaLocationID, that.isTrashed);
	}

	public Integer getID() { return id; }
	public void setID(int id) { this.id = id; }
	public String getName() { return name; }
	public void setName(String name) { this.name = name; }
	public int getScheduleID() { return scheduleID; }
	public void setScheduleID(int scheduleID) { this.scheduleID = scheduleID; }
	public int getStaffInstructorID() { return staffInstructorID; }
	public void setStaffInstructorID(int staffInstructorID) { this.staffInstructorID = staffInstructorID; }
	public int getTBALocationID() { return tbaLocationID; }
	public void setTBALocationID(int tbaLocationID) { this.tbaLocationID = tbaLocationID; }
	public boolean isTrashed() { return isTrashed; }
	public void setTrashed(boolean isTrashed) { this.isTrashed = isTrashed; }
	public int getStartHalfHour() { return startHalfHour; }
	public void setStartHalfHour(int startHalfHour) { this.startHalfHour = startHalfHour; }
	public int getEndHalfHour() { return endHalfHour; }
	public void setEndHalfHour(int endHalfHour) { this.endHalfHour = endHalfHour; }
}
