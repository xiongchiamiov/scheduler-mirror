package edu.calpoly.csc.scheduler.view.web.shared;

import java.io.Serializable;


public class UserDataGWT implements Serializable, Comparable<UserDataGWT> {
	private static final long serialVersionUID = -3337091550673507081L;
	
	private int id;
	private String userName;
	private int permissionLevel;
	private int scheduleID;
	   
	public Integer getScheduleID() {
		return scheduleID;
	}

	public void setScheduleID(int scheduleID) {
		this.scheduleID = scheduleID;
	}

	public UserDataGWT clone() {
		UserDataGWT newCourse = new UserDataGWT();
		newCourse.userName = userName;
		newCourse.permissionLevel = permissionLevel;
		newCourse.id = id;
		return newCourse;
	}
	
	public UserDataGWT() { }

	public UserDataGWT(int id, String user, int level) {
		super();
		this.id = id;
		userName = user;
		permissionLevel = level;
	}
	
	public void verify() {
		assert(userName != null);
	}

	@Override
	public int compareTo(UserDataGWT o) {
		if (permissionLevel != o.permissionLevel)
			return permissionLevel - o.permissionLevel;
		return userName.compareTo(o.userName);
	}
	public String toString() {
		return this.userName + " " + Integer.toString(this.permissionLevel);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof UserDataGWT))
			return false;
		UserDataGWT that = (UserDataGWT)obj;
		return permissionLevel == that.permissionLevel; 
	}
	
	@Override
	public int hashCode() {
		return userName.hashCode() + permissionLevel * 1337;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public Integer getPermissionLevel() {
		return permissionLevel;
	}

	public void setPermissionLevel(int permissionLevel) {
		this.permissionLevel = permissionLevel;
	}

}
