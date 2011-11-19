package edu.calpoly.csc.scheduler.view.web.shared;

import java.io.Serializable;


public class UserDataGWT implements Serializable, Comparable<UserDataGWT> {
	private static final long serialVersionUID = -3337091550673507081L;
	
	private int id;
	private String scheduleName;
	private String userName;
	private int permissionLevel;
	   
	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public int getPermissionLevel() {
		return permissionLevel;
	}

	public void setPermissionLevel(int permissionLevel) {
		this.permissionLevel = permissionLevel;
	}

	public UserDataGWT clone() {
		UserDataGWT newCourse = new UserDataGWT();
		newCourse.userName = userName;
		newCourse.permissionLevel = permissionLevel;
		newCourse.scheduleName = scheduleName;
		newCourse.id = id;
		return newCourse;
	}
	
	public UserDataGWT() {
		super();
		userName = "";
		permissionLevel = 0;
		scheduleName = "";
		id = 0;
	}

	public UserDataGWT(int id, String user, int level, String scheduleName) {
		super();
		this.id = id;
		userName = user;
		permissionLevel = level;
		this.scheduleName = scheduleName;
	}
	
	public void verify() {
		assert(userName != null);
	}

	public String getScheduleName() {
		return scheduleName;
	}

	public void setScheduleName(String scheduleName) {
		this.scheduleName = scheduleName;
	}

	public String getCourseName() {
		return userName;
	}

	public void setCourseName(String courseName) {
		this.userName = courseName;
	}

	public int getCatalogNum() {
		return permissionLevel;
	}

	public void setCatalogNum(int catalogNum) {
		this.permissionLevel = catalogNum;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
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
}
