package edu.calpoly.csc.scheduler.view.web.shared;

import java.io.Serializable;


public class UserGWT implements Serializable, Comparable<UserGWT> {
	private static final long serialVersionUID = -3337091550673507081L;
	
	private String userName;
	private int permissionLevel;
	
	public UserGWT clone() {
		UserGWT newCourse = new UserGWT();
		newCourse.userName = userName;
		newCourse.permissionLevel = permissionLevel;
		
		return newCourse;
	}
	
	public UserGWT() {
		super();
		userName = "";
		permissionLevel = 0;
	}

	public UserGWT(String user, int level) {
		super();
		userName = user;
		permissionLevel = level;
	}
	
	public void verify() {
		assert(userName != null);
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

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	@Override
	public int compareTo(UserGWT o) {
		if (permissionLevel != o.permissionLevel)
			return permissionLevel - o.permissionLevel;
		return userName.compareTo(o.userName);
	}
	public String toString() {
		return this.userName + " " + Integer.toString(this.permissionLevel);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof UserGWT))
			return false;
		UserGWT that = (UserGWT)obj;
		return permissionLevel == that.permissionLevel; 
	}
	
	@Override
	public int hashCode() {
		return userName.hashCode() + permissionLevel * 1337;
	}
}
