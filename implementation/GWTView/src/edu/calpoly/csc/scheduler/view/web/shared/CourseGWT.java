package edu.calpoly.csc.scheduler.view.web.shared;

import java.io.Serializable;

import com.google.gwt.user.client.Window;


public class CourseGWT implements Serializable{
	private static final long serialVersionUID = -3337091550673507081L;
	
	private String courseName;
	private int catalogNum;
	private String dept;
	private int wtu, scu, numSections;
	private String type; //will be an object
	private int maxEnroll;
	private String lab; //will be an object
	private int labId;
	private int labPad;
	private int length;
	private String days; //will be an object
	private String quarterID;
	private int scheduleID;
	
	public void verify() {
		if (courseName == null)
			Window.alert("flerp1");
		if (dept == null)
			Window.alert("flerp2");
		if (type == null)
			Window.alert("flerp3");
		if (lab == null)
			Window.alert("flerp4");
		if (days == null)
			Window.alert("flerp5");
		if (quarterID == null)
			Window.alert("flerp6");
	}
	
	public CourseGWT(){
		courseName = "";
		catalogNum = 0;
		dept = "";
		wtu = 0;
		scu = 0;
		numSections = 0;
		type = "";
		maxEnroll = 0;
		lab = "";
		labId = 0;
		length = 0;
	}
	
	public CourseGWT(int id, String name, int catalogNum, int wtus, int scus, String courseType,
			   int enrollment, int labId, boolean smartroom, boolean laptop, boolean overhead, 
			   int hoursPerWeek, String ctPrefix, String prefix) {
		   this.courseName = name;
		   this.catalogNum = catalogNum;
		   this.wtu = wtus;
		   this.scu = scus;
		   this.type = courseType;
		   this.maxEnroll = enrollment;
		   this.labId = labId;
		   this.length = hoursPerWeek;
	   }

	public CourseGWT(String courseName, int catalogNum, String dept, int wtu,
			int scu, int numSections, String type, int maxEnroll, String lab) {
		super();
		this.courseName = courseName;
		this.catalogNum = catalogNum;
		this.dept = dept;
		this.wtu = wtu;
		this.scu = scu;
		this.numSections = numSections;
		this.type = type;
		this.maxEnroll = maxEnroll;
		this.lab = lab;
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
	
	public void setDays(String days)
	{
		this.days = days;
	}
	
	public String getDays()
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

	public String getLab() {
		return lab;
	}

	public void setLab(String lab) {
		this.lab = lab;
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

	
}
