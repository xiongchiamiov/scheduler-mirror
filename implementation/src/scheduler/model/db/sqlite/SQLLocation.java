package scheduler.model.db.sqlite;

import scheduler.model.db.IDBLocation;

public class SQLLocation extends SQLObject implements IDBLocation {
	Integer id, docID, maxOccupancy;
	String type, room;
	Boolean schedulable;
	
	public SQLLocation(Integer id, Integer docID, Integer maxOccupancy,
					   String type, String room, Boolean schedulable) {
		super(id);
		this.id = id;
		this.docID = docID;
		this.maxOccupancy = maxOccupancy;
		this.type = type;
		this.room = room;
		this.schedulable = schedulable;
	}
	
	@Override
	public String getRoom() {
		return room;
	}

	@Override
	public void setRoom(String room) {
		this.room = room;
	}

	@Override
	public String getType() {
		return type;
	}

	@Override
	public void setType(String type) {
		this.type = type;
	}

	@Override
	public String getMaxOccupancy() {
		return maxOccupancy.toString();
	}

	@Override
	public void setMaxOccupancy(String maxOccupancy) {
		this.maxOccupancy = Integer.valueOf(maxOccupancy);
	}

	@Override
	public boolean isSchedulable() {
		return schedulable;
	}

	@Override
	public void setIsSchedulable(boolean isSchedulable) {
		this.schedulable = isSchedulable;
	}

}
