package scheduler.model.db;

public interface IDBTimePreference extends IDBObject {
	int getPreference();
	void setPreference(int preference);
}
