package scheduler.model.db.sqlite;

import scheduler.model.db.IDBCourse;
import scheduler.model.db.IDBCourseAssociation;
/**
 * The Class SQLCourseAssociation implements all methods of the IDBCourseAssociation class (part of the IDatabase interface).
 * This class represents a lab association between a lecture and a lab in the SQLite database.
 * @author kayleneS
 *
 */
public class SQLCourseAssociation extends SQLObject implements IDBCourseAssociation {
	// In this object, ID is the same as courseID
	
	public SQLCourseAssociation(Integer labID, Integer lectureID, Boolean isTethered) {
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
}
