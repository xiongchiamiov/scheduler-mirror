package scheduler.model.db.sqlite;

import scheduler.model.db.IDBDocument;
import scheduler.model.db.IDBSchedule;
import scheduler.model.db.simple.DBDocument;

public class SQLDocument extends SQLObject implements IDBDocument, IDBSchedule {
	String name;
	boolean isTrashed;
	Integer startHalfHour;
	Integer endHalfHour;
	
	public SQLDocument(Integer id, String name, Integer startHalfHour, Integer endHalfHour) {
		super(id);
		this.name = name;
		this.startHalfHour = startHalfHour;
		this.endHalfHour = endHalfHour;
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
	
}
