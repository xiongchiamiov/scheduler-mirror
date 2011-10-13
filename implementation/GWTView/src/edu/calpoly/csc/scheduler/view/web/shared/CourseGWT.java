package edu.calpoly.csc.scheduler.view.web.shared;

import java.io.Serializable;


public class CourseGWT implements Serializable{
	private static final long serialVersionUID = 5390601887732802996L;
	
	String name, dept, type;
	int id, wtu, scu, maxEnrollment, numOfSections;
	
	public CourseGWT(){}
	
	

	public CourseGWT(String name, String dept, String type, int id, int wtu,
			int scu, int maxEnrollment, int numOfSections) {
		super();
		this.name = name;
		this.dept = dept;
		this.type = type;
		this.id = id;
		this.wtu = wtu;
		this.scu = scu;
		this.maxEnrollment = maxEnrollment;
		this.numOfSections = numOfSections;
	}



	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDept() {
		return dept;
	}

	public void setDept(String dept) {
		this.dept = dept;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
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

	public int getMaxEnrollment() {
		return maxEnrollment;
	}

	public void setMaxEnrollment(int maxEnrollment) {
		this.maxEnrollment = maxEnrollment;
	}

	public int getNumOfSections() {
		return numOfSections;
	}

	public void setNumOfSections(int numOfSections) {
		this.numOfSections = numOfSections;
	}
}
