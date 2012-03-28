package scheduler.view.web.shared;

import java.io.Serializable;


public class UserDataGWT implements Serializable, Identified {
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

	public UserDataGWT(int id, String user, int level, int scheduleID) {
		super();
		this.id = id;
		userName = user;
		permissionLevel = level;
		this.scheduleID = scheduleID;
	}
	
	public UserDataGWT(UserDataGWT that) {
		this(that.id, that.userName, that.permissionLevel, that.scheduleID);
	}

	public void verify() {
		assert(userName != null);
	}

	public String toString() {
		return this.userName + " " + Integer.toString(this.permissionLevel);
	}

	public void setID(int id) {
		this.id = id;
	}
	
	public Integer getID() { return id; }

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
