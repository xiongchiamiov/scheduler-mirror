package edu.calpoly.csc.scheduler.view.web.shared;

import java.io.Serializable;
import java.util.Set;


public class CourseGWT implements Serializable, Identified {
	private static final long serialVersionUID = -3337091550673507081L;
	
	private String courseName;
	private String catalogNum;
	private String dept;
	private Integer wtu, scu, numSections;
	private String type; //will be an object
	private Integer maxEnroll;
	private Integer halfHoursPerWeek;
	private Set<DayCombinationGWT> dayCombinations;
	private Integer id;
	private Integer lectureID;
	private Boolean tetheredToLecture;
	
	public CourseGWT() { }
	
	public CourseGWT(String courseName, String catalogNum, String dept,
			Integer wtu, Integer scu, Integer numSections, String type,
			Integer maxEnroll, Integer lectureID, Integer halfHoursPerWeek, Set<DayCombinationGWT> dayCombinations,
			Integer id, Boolean tetheredToLecture) {
		super();
		this.courseName = courseName;
		this.catalogNum = catalogNum;
		this.dept = dept;
		this.wtu = wtu;
		this.scu = scu;
		this.numSections = numSections;
		this.type = type;
		this.maxEnroll = maxEnroll;
		this.lectureID = lectureID;
		this.halfHoursPerWeek = halfHoursPerWeek;
		this.dayCombinations = dayCombinations;
		this.id = id;
		this.tetheredToLecture = tetheredToLecture;
	}



	public CourseGWT(CourseGWT that) {
		this(that.courseName, that.catalogNum, that.dept, that.wtu, that.scu,
				that.numSections, that.type, that.maxEnroll, that.lectureID, that.halfHoursPerWeek,
				that.dayCombinations, that.id, that.tetheredToLecture);
	}

	public void verify() {
		assert(courseName != null);
		assert(dept != null);
		assert(type != null);
		assert(dayCombinations != null);
	}
	
	public void setDays(Set<DayCombinationGWT> days)
	{
		this.dayCombinations = days;
	}
	
	public Set<DayCombinationGWT> getDays()
	{
		return dayCombinations;
	}
   

	public int getHalfHoursPerWeek()
	{
	   return halfHoursPerWeek;
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

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public void setHalfHoursPerWeek(int halfHoursPerWeek) {
		this.halfHoursPerWeek = halfHoursPerWeek;
	}
	

	public Integer getID() {
		return id;
	}

	public void setID(int id) {
		this.id = id;
	}

	public String toString() {
		return this.courseName + " " + this.catalogNum + " " + this.type;
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
//				this.halfHoursPerWeek.equals(that.halfHoursPerWeek) &&
//				this.maxEnroll.equals(that.maxEnroll) &&
//				this.numSections.equals(that.numSections) &&
//				this.scu.equals(that.scu) &&
//				this.type.equals(that.type) &&
//				this.wtu.equals(that.wtu);
	}

	public int getLectureID() {
		return lectureID;
	}
	
	public void setLectureID(int lectureID) {
		this.lectureID = lectureID;
	}
	
	public Boolean getTetheredToLecture() {
		return tetheredToLecture;
	}

	public void setTetheredToLecture(Boolean tetheredToLecture) {
		this.tetheredToLecture = tetheredToLecture;
	}

	public void setLectureID(Integer lectureID) {
		this.lectureID = lectureID;
	}
}
