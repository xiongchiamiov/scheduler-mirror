package edu.calpoly.csc.scheduler.view.web.shared;

import java.io.Serializable;


public class CourseGWT implements Serializable{
	private static final long serialVersionUID = -3337091550673507081L;
	
	private String courseName;
	private int catalogNum;
	private String dept;
	private int wtu, scu, numSections;
	private String type;
	private int maxEnroll;
	private String lab;
	private int id;
	private int labId;
	private int labPad;
	private int length;
	private String days;
	private String quarterID;
	private int scheduleID;
	
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
		id = 0;
		labId = 0;
		smartroom = false;
		laptop = false;
		overhead = false;
		length = 0;
		ctPrefix = "";
		prefix = "";
	}
	
	public CourseGWT(int id, String name, int catalogNum, int wtus, int scus, String courseType,
			   int enrollment, int labId, boolean smartroom, boolean laptop, boolean overhead, 
			   int hoursPerWeek, String ctPrefix, String prefix) {
		   this.id = id;
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
	
	public void setID(int id)
	{
		this.id = id;
	}
	
	public int getID()
	{
		return id;
	}
	public int getLabID()
	   {
		   return labId;
	   }
	   
	
	   public int getLength()
	   {
		   return length;
	   }
	   
	public void setLength(int length)
	{
		this.length = length;
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

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getLabId() {
		return labId;
	}

	public void setLabId(int labId) {
		this.labId = labId;
	}

	public String getCtPrefix() {
		return ctPrefix;
	}

	public void setCtPrefix(String ctPrefix) {
		this.ctPrefix = ctPrefix;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public void setSmartroom(boolean smartroom) {
		this.smartroom = smartroom;
	}

	public void setLaptop(boolean laptop) {
		this.laptop = laptop;
	}

	public void setOverhead(boolean overhead) {
		this.overhead = overhead;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}
	
}
