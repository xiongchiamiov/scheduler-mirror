package scheduler.model.db;

public interface IDBLocation extends IDBObject {
	String getRoom();
	void setRoom(String room);
	
	String getType();
	void setType(String type);
	
	String getMaxOccupancy();
	void setMaxOccupancy(String maxOccupancy);
	
	boolean isSchedulable();
	void setIsSchedulable(boolean isSchedulable);
}
