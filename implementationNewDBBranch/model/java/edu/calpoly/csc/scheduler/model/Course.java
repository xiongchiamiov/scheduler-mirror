package edu.calpoly.csc.scheduler.model;

import java.util.Collection;
import java.util.Set;

import edu.calpoly.csc.scheduler.model.db.IDBCourse;

public class Course {
	IDBCourse underlyingCourse;
	Collection<String> usedEquipment;
	Collection<Set<Integer>> dayPatterns;
	
	Course(IDBCourse underlyingCourse, Collection<String> neededEquipment, Collection<Set<Integer>> dayPatterns) {
		this.underlyingCourse = underlyingCourse;
		this.usedEquipment = neededEquipment;
		this.dayPatterns = dayPatterns;
	}
	
	String getName() { return underlyingCourse.getName(); }
	void setName(String name) { underlyingCourse.setName(name); }
	
	String getCalatogNumber() { return underlyingCourse.getCalatogNumber(); }
	void setCatalogNumber(String catalogNumber) { underlyingCourse.setCatalogNumber(catalogNumber); }
	
	String getDepartment() { return underlyingCourse.getDepartment(); }
	void setDepartment(String department) { underlyingCourse.setDepartment(department); }
	
	String getWTU() { return underlyingCourse.getWTU(); }
	void setWTU(String wtu) { underlyingCourse.setWTU(wtu); }
	
	String getSCU() { return underlyingCourse.getSCU(); }
	void setSCU(String scu) { underlyingCourse.setSCU(scu); }
	
	String getNumSections() { return underlyingCourse.getNumSections(); }
	void setNumSections(String numSections) { underlyingCourse.setNumSections(numSections); }
	
	String getType() { return underlyingCourse.getType(); }
	void setType(String type) { underlyingCourse.setType(type); }
	
	String getMaxEnrollment() { return underlyingCourse.getMaxEnrollment(); }
	void setMaxEnrollment(String maxEnrollment) { underlyingCourse.setMaxEnrollment(maxEnrollment); }
	
	String getNumHalfHoursPerWeek() { return underlyingCourse.getNumHalfHoursPerWeek(); }
	void setNumHalfHoursPerWeek(String numHalfHoursPerWeek) { underlyingCourse.setNumHalfHoursPerWeek(numHalfHoursPerWeek); }

	boolean isSchedulable() { return underlyingCourse.isSchedulable(); }
	void setIsSchedulable(boolean isSchedulable) { underlyingCourse.setIsSchedulable(isSchedulable); }
	

	public Collection<String> getNeededEquipment() { return usedEquipment; }
	public void setNeededEquipment(Collection<String> neededEquipment) { this.usedEquipment = neededEquipment; }
	
	public Collection<Set<Integer>> getDayPatterns() { return dayPatterns; }
	public void setDayPatterns(Collection<Set<Integer>> dayPatterns) { this.dayPatterns = dayPatterns; }
}
