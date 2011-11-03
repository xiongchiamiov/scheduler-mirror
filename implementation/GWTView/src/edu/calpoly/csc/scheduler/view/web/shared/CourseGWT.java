package edu.calpoly.csc.scheduler.view.web.shared;

import java.io.Serializable;

import com.google.gwt.user.client.Window;

import edu.calpoly.csc.scheduler.model.db.cdb.Lab;


public class CourseGWT implements Serializable{
	private static final long serialVersionUID = -3337091550673507081L;
	
	private String courseName;
	private int catalogNum;
	private String dept;
	private int wtu, scu, numSections;
	private String type; //will be an object
	private int maxEnroll;
	private int labId;
	private int labPad;
	private int length;
	private WeekGWT days;
	private String quarterID;
	private int scheduleID;
	
	public void verify() {
		if (courseName == null)
			Window.alert("flerp1");
		if (dept == null)
			Window.alert("flerp2");
		if (type == null)
			Window.alert("flerp3");
		if (days == null)
			Window.alert("flerp5");
		if (quarterID == null)
			Window.alert("flerp6");
	}

	public void setQuarterID(String quarter)
	{
		this.quarterID = quarter;
	}
	
	public String getQuarterID()
	{
		return quarterID;
	}
	
	public void setScheduleID(int schedule)
	{
		this.scheduleID = schedule;
	}
	
	public int getScheduleID()
	{
		return scheduleID;
	}
	
	public void setLabPad(int labpad)
	{
		this.labPad = labpad;
	}
	
	public int getLabPad()
	{
		return labPad;
	}
	
	public void setDays(WeekGWT days)
	{
		this.days = days;
	}
	
	public WeekGWT getDays()
	{
		return days;
	}

	public int getLabID()
	{
	   return labId;
	}
   

	public int getLength()
	{
	   return length;
	}

	public String getCourseName() {
		return courseName;
	}

	public void setCourseName(String courseName) {
		this.courseName = courseName;
	}

	public int getCatalogNum() {
		return catalogNum;
	}

	public void setCatalogNum(int catalogNum) {
		this.catalogNum = catalogNum;
	}

	public String getDept() {
		return dept;
	}

	public void setDept(String dept) {
		this.dept = dept;
	}

	public int getWtu() {
		return wtu;
	}

	public void setWtu(int wtu) {
		this.wtu = wtu;
	}

	public int getScu() {
		return scu;
	}

	public void setScu(int scu) {
		this.scu = scu;
	}

	public int getNumSections() {
		return numSections;
	}

	public void setNumSections(int numSections) {
		this.numSections = numSections;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getMaxEnroll() {
		return maxEnroll;
	}

	public void setMaxEnroll(int maxEnroll) {
		this.maxEnroll = maxEnroll;
	}

	public int getLabId() {
		return labId;
	}

	public void setLabId(int labId) {
		this.labId = labId;
	}


	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public String toString() {
		return this.courseName + " " + Integer.toString(this.catalogNum);
	}
	
}
