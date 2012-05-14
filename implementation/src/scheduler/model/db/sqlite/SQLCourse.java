package scheduler.model.db.sqlite;

import scheduler.model.db.IDBCourse;

public class SQLCourse extends SQLObject implements IDBCourse {
	Integer documentID;
	Boolean isSchedulable;
	String name;
	String catalogNumber;
	String department;
	String wtu;
	String scu;
	String numSections;
	String type;
	String maxEnrollment;
	String numHalfHoursPerWeek;
	Integer lectureID;  //TODO: Not sure how this is used!
//	Boolean tetheredToLecture;
	
	public SQLCourse(Integer id, Integer docID, String enrollment, String wtu, String scu, String type, String numSections,
			String dept, String catalogNum, String name, Boolean schedulable, String numHalfHours/*, Integer lectureID*/)	//can't have 
	{
		
		super(id);
		//SEE ABOVE TODO
		//this.id = id;
		
		this.documentID = docID;
		this.name = name;
		this.catalogNumber = catalogNum;
		this.department = dept;
		this.wtu = wtu;
		this.scu = scu;
		this.numSections = numSections;
		this.type = type;
		this.maxEnrollment = enrollment;
		this.numHalfHoursPerWeek = numHalfHours;
		this.isSchedulable = schedulable;
//		this.lectureID = lectureID;
//		this.tetheredToLecture = tetheredToLecture;
	}
	
//	public SQLCourse(Integer id, Integer docID, String enrollment, String wtu, String scu, String type, String numSections,
//			String dept, String catalogNum, String name, Boolean schedulable, String numHalfHours, 	Integer lectureID, Boolean tetheredToLecture)	
//	{
//		
//		super(id);
//		//SEE ABOVE TODO
//		//this.id = id;
//		
//		this.documentID = docID;
//		this.name = name;
//		this.catalogNumber = catalogNum;
//		this.department = dept;
//		this.wtu = wtu;
//		this.scu = scu;
//		this.numSections = numSections;
//		this.type = type;
//		this.maxEnrollment = enrollment;
//		this.numHalfHoursPerWeek = numHalfHours;
//		this.isSchedulable = schedulable;
//		this.lectureID = lectureID;
//		this.tetheredToLecture = tetheredToLecture;
//	}

	public void sanityCheck() {
		assert(documentID != null);
		assert(name != null);
		assert(catalogNumber != null);
		assert(wtu != null);
		assert(scu != null);
		assert(numSections != null);
		assert(type != null);
		assert(maxEnrollment != null);
		assert(numHalfHoursPerWeek != null);
	}

	@Override
	public String getName() { return name; }
	@Override
	public void setName(String name) { this.name = name; }
	@Override
	public String getCalatogNumber() { return catalogNumber; }
	@Override
	public void setCatalogNumber(String catalogNumber) { this.catalogNumber = catalogNumber; }
	@Override
	public String getDepartment() { return department; }
	@Override
	public void setDepartment(String department) { this.department = department; }
	@Override
	public String getWTU() { return wtu; }
	@Override
	public void setWTU(String wtu) { this.wtu = wtu; }
	@Override
	public String getSCU() { return scu; }
	@Override
	public void setSCU(String scu) { this.scu = scu; }
	@Override
	public String getNumSections() { return numSections; }
	@Override
	public void setNumSections(String numSections) { this.numSections = numSections; }
	@Override
	public String getType() { return type; }
	@Override
	public void setType(String type) { this.type = type; }
	@Override
	public String getMaxEnrollment() { return maxEnrollment; }
	@Override
	public void setMaxEnrollment(String maxEnrollment) { this.maxEnrollment = maxEnrollment; }
	@Override
	public String getNumHalfHoursPerWeek() { return numHalfHoursPerWeek; }
	@Override
	public void setNumHalfHoursPerWeek(String numHalfHoursPerWeek) { this.numHalfHoursPerWeek = numHalfHoursPerWeek; }
	@Override
	public boolean isSchedulable() { return isSchedulable; }
	@Override
	public void setIsSchedulable(boolean isSchedulable) { this.isSchedulable = new Boolean(isSchedulable); }
}
