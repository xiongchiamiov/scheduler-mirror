package edu.calpoly.csc.scheduler.model;

import java.util.Collection;
import java.util.Set;

import edu.calpoly.csc.scheduler.model.db.IDBCourse;

public class Course {
	IDBCourse underlyingCourse;
	Set<String> usedEquipment;
	Collection<Set<Integer>> dayPatterns;
	int lectureID;
	boolean tetheredToLecture;
	
	Course(IDBCourse underlyingCourse, Set<String> usedEquipment, Collection<Set<Integer>> dayPatterns, int lectureID, boolean tetheredTolecture) {
		this.underlyingCourse = underlyingCourse;
		this.usedEquipment = usedEquipment;
		this.dayPatterns = dayPatterns;
		this.lectureID = lectureID;
		this.tetheredToLecture = tetheredTolecture;
	}

	public Set<String> getUsedEquipment() {
		return usedEquipment;
	}

	public void setUsedEquipment(Set<String> usedEquipment) {
		this.usedEquipment = usedEquipment;
	}

	public int getLectureID() {
		return lectureID;
	}

	public void setLectureID(int lectureID) {
		this.lectureID = lectureID;
	}

	public boolean isTetheredToLecture() {
		return tetheredToLecture;
	}

	public void setTetheredToLecture(boolean tetheredToLecture) {
		this.tetheredToLecture = tetheredToLecture;
	}

	public int getID() { return underlyingCourse.getID(); }
	
	public String getName() { return underlyingCourse.getName(); }
	public void setName(String name) { underlyingCourse.setName(name); }
	
	public String getCalatogNumber() { return underlyingCourse.getCalatogNumber(); }
	public void setCatalogNumber(String catalogNumber) { underlyingCourse.setCatalogNumber(catalogNumber); }
	
	public String getDepartment() { return underlyingCourse.getDepartment(); }
	public void setDepartment(String department) { underlyingCourse.setDepartment(department); }
	
	public String getWTU() { return underlyingCourse.getWTU(); }
	public void setWTU(String wtu) { underlyingCourse.setWTU(wtu); }
	
	public String getSCU() { return underlyingCourse.getSCU(); }
	public void setSCU(String scu) { underlyingCourse.setSCU(scu); }
	
	public String getNumSections() { return underlyingCourse.getNumSections(); }
	public void setNumSections(String numSections) { underlyingCourse.setNumSections(numSections); }
	
	public String getType() { return underlyingCourse.getType(); }
	public void setType(String type) { underlyingCourse.setType(type); }
	
	public String getMaxEnrollment() { return underlyingCourse.getMaxEnrollment(); }
	public void setMaxEnrollment(String maxEnrollment) { underlyingCourse.setMaxEnrollment(maxEnrollment); }
	
	public String getNumHalfHoursPerWeek() { return underlyingCourse.getNumHalfHoursPerWeek(); }
	public void setNumHalfHoursPerWeek(String numHalfHoursPerWeek) { underlyingCourse.setNumHalfHoursPerWeek(numHalfHoursPerWeek); }

	public boolean isSchedulable() { return underlyingCourse.isSchedulable(); }
	public void setIsSchedulable(boolean isSchedulable) { underlyingCourse.setIsSchedulable(isSchedulable); }
	
	public Collection<Set<Integer>> getDayPatterns() { return dayPatterns; }
	public void setDayPatterns(Collection<Set<Integer>> dayPatterns) { this.dayPatterns = dayPatterns; }
}
