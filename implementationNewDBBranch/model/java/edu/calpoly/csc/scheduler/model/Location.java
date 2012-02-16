package edu.calpoly.csc.scheduler.model;

import java.util.Collection;

import edu.calpoly.csc.scheduler.model.db.IDBLocation;

public class Location {
	IDBLocation underlyingLocation;
	Collection<String> providedEquipment;
	
	Location(final IDBLocation underlyingLocation, Collection<String> providedEquipment) {
		this.underlyingLocation = underlyingLocation;
		this.providedEquipment = providedEquipment;
	}

	String getRoom() { return underlyingLocation.getRoom(); }
	void setRoom(String room) { underlyingLocation.setRoom(room); }
	
	String getType() { return underlyingLocation.getType(); }
	void setType(String type) { underlyingLocation.setType(type); }
	
	String getMaxOccupancy() { return underlyingLocation.getMaxOccupancy(); }
	void setMaxOccupancy(String maxOccupancy) { underlyingLocation.setMaxOccupancy(maxOccupancy); }
	
	boolean isSchedulable() { return underlyingLocation.isSchedulable(); }
	void setIsSchedulable(boolean isSchedulable) { underlyingLocation.setIsSchedulable(isSchedulable); }
}
