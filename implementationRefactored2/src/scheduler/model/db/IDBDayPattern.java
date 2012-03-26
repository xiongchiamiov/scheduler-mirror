package edu.calpoly.csc.scheduler.model.db;

import java.util.Set;

public interface IDBDayPattern extends IDBObject {
	Set<Integer> getDays();
	void setDays(Set<Integer> days);
}
