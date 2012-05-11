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
	Integer lectureID;
	Boolean tetheredToLecture;
	
	public SQLCourse(Integer id, Integer documentID, String name, String catalogNumber, String department,
			String wtu, String scu, String numSections, String type,
			String maxEnrollment, String numHalfHoursPerWeek, Boolean isSchedulable, Integer lectureID, Boolean tetheredToLecture) {
		
		super(id);
		//SEE ABOVE TODO
		this.id = id;
		
		this.documentID = documentID;
		this.name = name;
		this.catalogNumber = catalogNumber;
		this.department = department;
		this.wtu = wtu;
		this.scu = scu;
		this.numSections = numSections;
		this.type = type;
		this.maxEnrollment = maxEnrollment;
		this.numHalfHoursPerWeek = numHalfHoursPerWeek;
		this.isSchedulable = isSchedulable;
		this.lectureID = lectureID;
		this.tetheredToLecture = tetheredToLecture;
	}
	
	public SQLCourse(SQLCourse that) {
		this(that.id, that.documentID, that.name, that.catalogNumber, that.department, that.wtu, that.scu, that.numSections, that.type, that.maxEnrollment, that.numHalfHoursPerWeek, that.isSchedulable, that.lectureID, that.tetheredToLecture);
	}

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
	public void setIsSchedulable(boolean isSchedulable) { this.isSchedulable = isSchedulable; }
}
