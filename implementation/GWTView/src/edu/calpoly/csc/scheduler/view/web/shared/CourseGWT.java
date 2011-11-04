package edu.calpoly.csc.scheduler.view.web.shared;

import java.io.Serializable;


public class CourseGWT implements Serializable, Comparable<CourseGWT> {
	private static final long serialVersionUID = -3337091550673507081L;
	
	private String courseName;
	private int catalogNum;
	private String dept;
	private int wtu, scu, numSections;
	private String type; //will be an object
	private int maxEnroll;
	private int labId;
	private String labName;
	private String labDept;
	private int labCatalogNum;
	private int labPad;
	private int length;
	private WeekGWT days;
	private String quarterID;
	private int scheduleID;
	
	public CourseGWT clone() {
		CourseGWT newCourse = new CourseGWT();
		newCourse.courseName = courseName;
		newCourse.catalogNum = catalogNum;
		newCourse.dept = dept;
		newCourse.wtu = wtu;
		newCourse.scu = scu;
		newCourse.numSections = numSections;
		newCourse.type = type;
		newCourse.maxEnroll = maxEnroll;
		newCourse.labId = labId;
		newCourse.labPad = labPad;
		newCourse.length = length;
		newCourse.days = days.clone();
		newCourse.quarterID = quarterID;
		newCourse.scheduleID = scheduleID;
		newCourse.labCatalogNum = labCatalogNum;
		newCourse.labDept = labDept;
		newCourse.labName = labName;
		return newCourse;
	}
	
	public void verify() {
		assert(courseName != null);
		assert(dept != null);
		assert(type != null);
		assert(days != null);
		assert(quarterID != null);
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

	public String getLabName() {
		return labName;
	}

	public void setLabName(String labName) {
		this.labName = labName;
	}

	public String getLabDept() {
		return labDept;
	}

	public void setLabDept(String labDept) {
		this.labDept = labDept;
	}

	public int getLabCatalogNum() {
		return labCatalogNum;
	}

	public void setLabCatalogNum(int labCatalogNum) {
		this.labCatalogNum = labCatalogNum;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public void setLength(int length) {
		this.length = length;
	}

	@Override
	public int compareTo(CourseGWT o) {
		if (catalogNum != o.catalogNum)
			return catalogNum - o.catalogNum;
		return dept.compareTo(o.dept);
	}
	public String toString() {
		return this.courseName + " " + Integer.toString(this.catalogNum);
	}
}
