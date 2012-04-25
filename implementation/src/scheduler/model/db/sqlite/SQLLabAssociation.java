package scheduler.model.db.sqlite;

public class SQLLabAssociation extends SQLObject {	
	Integer lectureID;
	
	public SQLLabAssociation(Integer labID, Integer lectureID) {
		super(labID);
		this.lectureID = lectureID;
	}
	
	public Integer getLectureID() { return lectureID; }
	public void setLectureID(Integer lect) {
		lectureID = lect;
	}
}
