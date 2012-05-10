package scheduler.view.web.shared;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;


public class LocationGWT implements Serializable, Identified {
	private static final long serialVersionUID = 1015108352203434920L;

	private Integer id;
	private String room;
	private String type;
	private String maxOccupancy;
	private Set<String> equipment;

	private boolean isSchedulable;
	
	public LocationGWT() { }

	public LocationGWT(Integer id, String room, String type, String maxOccupancy, Set<String> equipment, boolean isSchedulable) {
		super();
		this.id = id;
		this.room = room;
		this.type = type;
		this.maxOccupancy = maxOccupancy;
		this.equipment = equipment;
		this.isSchedulable = isSchedulable;
	}

	public LocationGWT(int id, LocationGWT other) {
		this.id = id;
		this.room = other.room;
		this.type = other.type;
		this.maxOccupancy = other.maxOccupancy;
		this.equipment = new HashSet<String>(other.equipment);
		this.isSchedulable = other.isSchedulable;
	}
	
	public LocationGWT(LocationGWT that) {
		this(that.id, that.room, that.type, that.maxOccupancy, new HashSet<String>(that.equipment), that.isSchedulable);
		// TODO Auto-generated constructor stub
	}

	public Integer getID() { return id; }
	public void setID(Integer id) { this.id = id; }
	public String getRoom() { return room; }
	public void setRoom(String room) { this.room = room; }
	public String getType() { return type; }
	public void setType(String type) { this.type = type; }
	public String getRawMaxOccupancy() { return maxOccupancy; }
	public int getMaxOccupancy() {
		try { return Integer.parseInt(maxOccupancy); }
		catch (NumberFormatException e) { return 0; }
	}
	public void setMaxOccupancy(String maxOccupancy) { this.maxOccupancy = maxOccupancy; }
	public Set<String> getEquipment() { return equipment; }
	public void setEquipment(Set<String> equipment) { this.equipment = equipment; }
	
	@Override
	public int hashCode() { return getID(); }
	
	@Override
	public boolean equals(Object obj) {
		assert(false); // DONT USE EQUALS! equals() is a gateway to bugs because the word "equals" is very ambiguous.
		return false;//((LocationGWT)obj).getID() == getID();
	}

	public boolean isSchedulable() { return this.isSchedulable; }

	public boolean attributesEqual(LocationGWT that) {
		return id.equals(that.id)&&
				room.equals(that.room) &&
				type.equals(that.type) &&
				maxOccupancy.equals(that.maxOccupancy) &&
				equipment.equals(that.equipment);
	}
}
