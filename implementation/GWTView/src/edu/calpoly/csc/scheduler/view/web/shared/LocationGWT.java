package edu.calpoly.csc.scheduler.view.web.shared;

import java.io.Serializable;


public class LocationGWT implements Serializable, Comparable<LocationGWT> {
	private static final long serialVersionUID = 1015108352203434920L;

	private int id;
	private String building;
	private String room;
	private String type;
	private int maxOccupancy;
	private boolean adacompliant;
	
	public LocationGWT() { }

	public LocationGWT(int id, String building, String room, String type, int maxOccupancy, boolean adacompliant) {
		super();
		this.id = id;
		this.building = building;
		this.room = room;
		this.type = type;
		this.maxOccupancy = maxOccupancy;
		this.adacompliant = adacompliant;
	}

	public LocationGWT(int id, LocationGWT other) {
		this.id = id;
		this.building = other.building;
		this.room = other.room;
		this.type = other.type;
		this.maxOccupancy = other.maxOccupancy;
		this.adacompliant = other.adacompliant;
	}

	@Override
	public int compareTo(LocationGWT that) {
		if (building.compareTo(that.building) != 0)
			return building.compareTo(that.building);
		if (room.compareTo(that.room) != 0)
			return room.compareTo(that.room);
		if (type.compareTo(that.type) != 0)
			return type.compareTo(that.type);
		if (maxOccupancy != that.maxOccupancy)
			return maxOccupancy - that.maxOccupancy;
		if (adacompliant != that.adacompliant)
			return (adacompliant ? 1 : 0) - (that.adacompliant ? 1 : 0);
		return 0;
	}
	
	@Override
	public boolean equals(Object obj) {
		return obj instanceof LocationGWT && id == ((LocationGWT)obj).id;
	}
	
	@Override
	public int hashCode() {
		return id;
	}
	
	public int getID() { return id; }
	public boolean isADACompliant() { return adacompliant; }
	public void setADACompliant(boolean compliant) { this.adacompliant = compliant; }
	public String getBuilding() { return building; }
	public void setBuilding(String building) { this.building = building; }
	public String getRoom() { return room; }
	public void setRoom(String room) { this.room = room; }
	public String getType() { return type; }
	public void setType(String type) { this.type = type; }
	public int getMaxOccupancy() { return maxOccupancy; }
	public void setMaxOccupancy(int maxOccupancy) { this.maxOccupancy = maxOccupancy; }

}
