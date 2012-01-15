package edu.calpoly.csc.scheduler.view.web.shared;

import java.io.Serializable;


public class CourseGWT implements Serializable, Identified {
	private static final long serialVersionUID = -3337091550673507081L;
	
	private String courseName;
	private String catalogNum;
	private String dept;
	private Integer wtu, scu, numSections;
	private String type; //will be an object
	private Integer maxEnroll;
	private Integer labId;
	private String labName;
	private String labDept;
	private Integer labCatalogNum;
	private Integer length;
	private WeekGWT days;
	private Integer scheduleID;
	private Integer id;
	
	public CourseGWT() { }
	
	public CourseGWT(String courseName, String catalogNum, String dept,
			Integer wtu, Integer scu, Integer numSections, String type,
			Integer maxEnroll, Integer labId, String labName, String labDept,
			Integer labCatalogNum, Integer length, WeekGWT days,
			Integer scheduleID, Integer id) {
		super();
		this.courseName = courseName;
		this.catalogNum = catalogNum;
		this.dept = dept;
		this.wtu = wtu;
		this.scu = scu;
		this.numSections = numSections;
		this.type = type;
		this.maxEnroll = maxEnroll;
		this.labId = labId;
		this.labName = labName;
		this.labDept = labDept;
		this.labCatalogNum = labCatalogNum;
		this.length = length;
		this.days = days;
		this.scheduleID = scheduleID;
		this.id = id;
	}



	public CourseGWT(CourseGWT that) {
		this(that.courseName, that.catalogNum, that.dept, that.wtu, that.scu,
				that.numSections, that.type, that.maxEnroll, that.labId,
				that.labName, that.labDept, that.labCatalogNum, that.length,
				that.days, that.scheduleID, that.id);
	}

	public void verify() {
		assert(courseName != null);
		assert(dept != null);
		assert(type != null);
		assert(days != null);
	}
	
	public void setScheduleID(Integer schedule)
	{
		this.scheduleID = schedule;
	}
	
	public Integer getScheduleID()
	{
		return scheduleID;
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

	public String getCatalogNum() {
		return catalogNum;
	}

	public void setCatalogNum(String catalogNum) {
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
	

	public Integer getID() {
		return id;
	}

	public void setID(int id) {
		this.id = id;
	}

	public String toString() {
		return this.courseName + " " + this.catalogNum;
	}
	
	@Override
	public int hashCode() {
		assert(false); // don't use this object in a hash! use its ID instead please.
		return 0;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof CourseGWT))
			return false;
		CourseGWT that = (CourseGWT)obj;
		return id == that.id;
//		assert(this.id.equals(that.id));
//		assert(this.scheduleID.equals(that.scheduleID));
//		return this.catalogNum.equals(that.catalogNum) &&
//				this.courseName.equals(that.courseName) &&
//				this.days.equals(that.days) &&
//				this.dept.equals(that.dept) &&
//				this.labCatalogNum.equals(that.labCatalogNum) &&
//				this.labDept.equals(that.labDept) &&
//				this.labId.equals(that.labId) &&
//				this.labName.equals(that.labName) &&
//				this.length.equals(that.length) &&
//				this.maxEnroll.equals(that.maxEnroll) &&
//				this.numSections.equals(that.numSections) &&
//				this.scu.equals(that.scu) &&
//				this.type.equals(that.type) &&
//				this.wtu.equals(that.wtu);
	}
}
