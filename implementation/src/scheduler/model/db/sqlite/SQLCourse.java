package scheduler.model.db.sqlite;

import scheduler.model.db.IDBCourse;

public class SQLCourse extends SQLObject implements IDBCourse {
	Integer id, docID, enrollment, wtu, scu, numSections, numHalfHours;
	String type, dept, name, catalogNum;
	Boolean schedulable;
	
	public SQLCourse(Integer id, Integer docID, Integer enrollment, Integer wtu,
			   Integer scu, Integer numSections, Integer numHalfHours,
			   String type, String dept, String name, String catalogNum,
			   Boolean schedulable) {
		super(id);
		this.id = id;
		this.docID = docID;
		this.enrollment = enrollment;
		this.wtu = wtu;
		this.scu = scu;
		this.numSections = numSections;
		this.numHalfHours = numHalfHours;
		this.type = type;
		this.dept = dept;
		this.name = name;
		this.catalogNum = catalogNum;
		this.schedulable = schedulable;
	}
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String getCalatogNumber() {
		return catalogNum;
	}

	@Override
	public void setCatalogNumber(String catalogNumber) {
		this.catalogNum = catalogNumber;
	}

	@Override
	public String getDepartment() {
		return dept;
	}

	@Override
	public void setDepartment(String department) {
		this.dept = department;
	}

	@Override
	public String getWTU() {
		return wtu.toString();
	}

	@Override
	public void setWTU(String wtu) {
		this.wtu = Integer.parseInt(wtu);
	}

	@Override
	public String getSCU() {
		return scu.toString();
	}

	@Override
	public void setSCU(String scu) {
		this.scu = Integer.parseInt(scu);
	}

	@Override
	public String getNumSections() {
		return numSections.toString();
	}

	@Override
	public void setNumSections(String numSections) {
		this.numSections = Integer.parseInt(numSections);
	}

	@Override
	public String getType() {
		return type;
	}

	@Override
	public void setType(String type) {
		this.type = type;
	}

	@Override
	public String getMaxEnrollment() {
		return enrollment.toString();
	}

	@Override
	public void setMaxEnrollment(String maxEnrollment) {
		this.enrollment = Integer.parseInt(maxEnrollment);
	}

	@Override
	public String getNumHalfHoursPerWeek() {
		return numHalfHours.toString();
	}

	@Override
	public void setNumHalfHoursPerWeek(String numHalfHoursPerWeek) {
		this.numHalfHours = Integer.parseInt(numHalfHoursPerWeek);
	}

	@Override
	public boolean isSchedulable() {
		return schedulable;
	}

	@Override
	public void setIsSchedulable(boolean isSchedulable) {
		this.schedulable = isSchedulable;
	}

}
