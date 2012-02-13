package edu.calpoly.csc.scheduler.model.db;

public interface IDBScheduleItem extends IDBObject {
	int getSection();
	void setSection(int section);
}
