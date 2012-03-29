package scheduler.model.db;

public interface IDBCoursePreference extends IDBObject {
	int getPreference();
	void setPreference(int preference);
}
