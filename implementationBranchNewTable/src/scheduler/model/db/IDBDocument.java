package scheduler.model.db;

public interface IDBDocument extends IDBObject {
	public String getName();
	public void setName(String name);
	
	public boolean isTrashed();
	public void setIsTrashed(boolean isTrashed);
	
	public int getStartHalfHour();
	public void setStartHalfHour(int halfHour);
	
	public int getEndHalfHour();
	public void setEndHalfHour(int halfHour);
}
