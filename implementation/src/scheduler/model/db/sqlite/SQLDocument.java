package scheduler.model.db.sqlite;

import scheduler.model.db.IDBDocument;
import scheduler.model.db.IDBSchedule;
import scheduler.model.db.simple.DBDocument;

public class SQLDocument extends SQLObject implements IDBDocument, IDBSchedule {
	String name;
	boolean isTrashed;
	Integer startHalfHour;
	Integer endHalfHour;
	boolean isWorkingCopy;
	Integer workingCopyID;
	
	public SQLDocument(Integer id, String name, Integer startHalfHour, Integer endHalfHour) {
		super(id);
		this.name = name;
		this.startHalfHour = startHalfHour;
		this.endHalfHour = endHalfHour;
		isWorkingCopy = false;
		workingCopyID = null;
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
	
	public boolean isWorkingCopy()
	{
		return this.isWorkingCopy;
	}
	
	public void setWorkingCopy(boolean isWorkingCopy)
	{
		this.isWorkingCopy = isWorkingCopy;
	}
	public void setWorkingCopyID(Integer id)
	{
		this.workingCopyID = id;
	}
	public Integer getWorkingCopyID()
	{
		return this.workingCopyID;
	}
}
