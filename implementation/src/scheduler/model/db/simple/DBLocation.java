package scheduler.model.db.simple;

import scheduler.model.db.IDBLocation;

public class DBLocation extends DBObject implements IDBLocation {
	Integer documentID;
	String room;
	String type;
	String maxOccupancy;
	boolean isSchedulable;
	
	public DBLocation(Integer id, Integer documentID, String room, String type, String maxOccupancy, boolean isSchedulable) {
		super(id);
		this.documentID = documentID;
		this.room = room;
		this.type = type;
		this.maxOccupancy = maxOccupancy;
	}
	
	public DBLocation(DBLocation that) {
		this(that.id, that.documentID, that.room, that.type, that.maxOccupancy, that.isSchedulable);
	}

	public void sanityCheck() {
		assert(documentID != null);
		assert(room != null);
		assert(type != null);
		assert(maxOccupancy != null);
	}


	@Override
	public void setRoom(String room) { this.room = room; }
	@Override
	public String getRoom() { return room; }
	@Override
	public String getType() { return type; }
	@Override
	public void setType(String type) { this.type = type; }
	@Override
	public String getMaxOccupancy() { return maxOccupancy; }
	@Override
	public void setMaxOccupancy(String maxOccupancy) { this.maxOccupancy = maxOccupancy; }
	@Override
	public boolean isSchedulable() { return isSchedulable; }
	@Override
	public void setIsSchedulable(boolean isSchedulable) { this.isSchedulable = isSchedulable; }
}
