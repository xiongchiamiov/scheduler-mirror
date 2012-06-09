package scheduler.model.db.sqlite;

import scheduler.model.db.IDBLocation;
/**
 * The Class SQLLocation implements all methods of the IDBLocation class (part of the IDatabase interface).
 * This class represents a location or room in the SQLite database.
 * @author kayleneS
 *
 */
public class SQLLocation extends SQLObject implements IDBLocation {
	Integer id, docID;
	String type, room, maxOccupancy;
	Boolean schedulable;
	
	public SQLLocation(Integer id, Integer docID, String maxOccupancy,
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
		return maxOccupancy;
	}

	@Override
	public void setMaxOccupancy(String maxOccupancy) {
		this.maxOccupancy = maxOccupancy;
	}

	@Override
	public boolean isSchedulable() {
		return schedulable;
	}

	@Override
	public void setIsSchedulable(boolean isSchedulable) {
		this.schedulable = isSchedulable;
	}
	
	@Override
	public Integer getID() {
		return this.id;
	}
}
