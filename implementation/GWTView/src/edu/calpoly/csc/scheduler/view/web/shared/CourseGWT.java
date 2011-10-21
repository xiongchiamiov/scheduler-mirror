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
	private boolean smartroom;
	private boolean laptop;
	private boolean overhead;
	private int length;
	private String ctPrefix;
	private String prefix;
	
	public CourseGWT(){}
	
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
		   this.smartroom = smartroom;
		   this.laptop = laptop;
		   this.overhead = overhead;
		   this.length = hoursPerWeek;
		   this.ctPrefix = ctPrefix;
		   this.prefix = prefix;
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
	
	public int getID()
	{
		return id;
	}
	public int getLabID()
	   {
		   return labId;
	   }
	   
	   public boolean getSmartroom()
	   {
		   return smartroom;
	   }
	   
	   public boolean getLaptop()
	   {
		   return laptop;
	   }
	   
	   public boolean getOverhead()
	   {
		   return overhead;
	   }
	   
	   public String getCTPrefix()
	   {
		   return ctPrefix;
	   }
	   
	   public String getPrefix()
	   {
		   return prefix;
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
}
