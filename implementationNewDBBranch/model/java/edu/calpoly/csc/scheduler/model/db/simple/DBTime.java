package edu.calpoly.csc.scheduler.model.db.simple;

import edu.calpoly.csc.scheduler.model.db.IDBTime;

public class DBTime extends DBObject implements IDBTime {
	private static int idFromDayAndHalfHour(int day, int halfHour) {
		assert(halfHour >= 0 && halfHour < 48);
		assert(day >= 0 && day < 7);
		return day * 48 + halfHour;
	}
	
	private static int idFromCode(int id) {
		assert(id >= 0 && id < 7 * 48);
		return id / 48;
	}
	
	private static int halfHourFromID(int id) {
		assert(id >= 0 && id < 7 * 48);
		return id % 48;
	}
	
	
	public DBTime(int id) {
		super(id);
	}
	public DBTime(int day, int halfHour) {
		super(idFromDayAndHalfHour(day, halfHour));
	}
	public DBTime(DBTime that) {
		this(that.getDay(), that.getHalfHour());
	}
	
	public int getID() { return id; }

	public int getDay() { return idFromCode(id); }
	public void setDay(int day) {
		this.id = idFromDayAndHalfHour(day, getHalfHour());
	}
	
	public int getHalfHour() { return halfHourFromID(id); }
	public void setHalfHour(int halfHour) {
		this.id = idFromDayAndHalfHour(getDay(), halfHour);
	}
	
	@Override
	public boolean equals(Object obj) { return ((DBTime)obj).id == id; }
	@Override
	public int hashCode() { return id; }
}
