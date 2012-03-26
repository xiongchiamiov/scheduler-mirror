package scheduler.model.db;

public interface IDBCourseAssociation extends IDBObject {
	boolean isTethered();
	void setIsTethered(boolean isTethered);
}
