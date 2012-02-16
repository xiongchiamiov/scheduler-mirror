package edu.calpoly.csc.scheduler.model.db;

public interface IDBCourse extends IDBObject {
	String getName();
	void setName(String name);
	
	String getCalatogNumber();
	void setCatalogNumber(String catalogNumber);
	
	String getDepartment();
	void setDepartment(String department);
	
	String getWTU();
	void setWTU(String wtu);
	
	String getSCU();
	void setSCU(String scu);
	
	String getNumSections();
	void setNumSections(String numSections);
	
	String getType();
	void setType(String type);
	
	String getMaxEnrollment();
	void setMaxEnrollment(String maxEnrollment);
	
	String getNumHalfHoursPerWeek();
	void setNumHalfHoursPerWeek(String numHalfHoursPerWeek);

	boolean isSchedulable();
	void setIsSchedulable(boolean isSchedulable);
}
