package scheduler.model.db.simple;

import scheduler.model.db.IDBCourse;
import scheduler.model.db.IDBCourseAssociation;

public class DBCourseAssociation extends DBObject implements IDBCourseAssociation {
	// In this object, ID is the same as courseID
	
	public DBCourseAssociation(int labID, int lectureID, boolean isTethered) {
		super(labID);
		this.lectureID = lectureID;
		this.isTethered = isTethered;
	}

	int lectureID;
	boolean isTethered;
	
	int getLabID() { return id; }
	
	@Override
	public boolean isTethered() { return isTethered; }
	@Override
	public void setIsTethered(boolean isTethered) { this.isTethered = isTethered; }

	public void sanityCheck() {
	}

}
