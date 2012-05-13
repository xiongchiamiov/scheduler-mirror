package scheduler.model.db.sqlite;

import scheduler.model.db.IDBDocument;

public class SQLDocument extends SQLObject implements IDBDocument {
	String name;
	Boolean isTrashed;
	Integer startHalfHour;
	Integer endHalfHour;
	Integer staffInstructorID; // null if there is none
	Integer tbaLocationID; // null if there is none
	Integer chooseForMeInstructorID; //null if there is none
	Integer chooseForMeLocationID; //null if there is none
	
	
	public SQLDocument(Integer id, String name, Boolean isTrashed, Integer startHalfHour, Integer endHalfHour,
			Integer staffInstructorID, Integer tbaLocationID, Integer chooseForMeInstructorID,
			Integer chooseForMeLocationID) {
		
		super(id);
		this.name = name;
		this.startHalfHour = startHalfHour;
		this.endHalfHour = endHalfHour;
		this.staffInstructorID = staffInstructorID;
		this.tbaLocationID = tbaLocationID;
		this.chooseForMeInstructorID = chooseForMeInstructorID;
		this.chooseForMeLocationID = chooseForMeLocationID;
		this.isTrashed = isTrashed;
	}
	
	public SQLDocument(Integer id, String name, Boolean isTrashed, Integer startHalfHour, Integer endHalfHour) {
		super(id);
		
		this.name = name;
		this.startHalfHour = startHalfHour;
		this.endHalfHour = endHalfHour;
		this.staffInstructorID = null;
		this.tbaLocationID = null;
		this.chooseForMeInstructorID = null;
		this.chooseForMeLocationID = null;
		this.isTrashed = isTrashed;
	}
	
	@Override
	public String getName() { return name; }
	@Override
	public void setName(String name) { this.name = name; }

	@Override
	public boolean isTrashed() { return isTrashed; }
	@Override
	public void setIsTrashed(boolean isTrashed) { this.isTrashed = isTrashed; }

	@Override
	public int getStartHalfHour() { return startHalfHour; }
	@Override
	public void setStartHalfHour(int halfHour) { startHalfHour = halfHour; }

	@Override
	public int getEndHalfHour() { return endHalfHour; }
	@Override
	public void setEndHalfHour(int halfHour) { endHalfHour = halfHour; }
	
	public void sanityCheck() {
		assert(name != null);
		assert(staffInstructorID != null);
		assert(tbaLocationID != null);
		assert(chooseForMeInstructorID != null);
		assert(chooseForMeLocationID != null);
	}
}
