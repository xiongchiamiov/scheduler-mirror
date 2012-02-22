package edu.calpoly.csc.scheduler.model;

import java.util.Set;

import edu.calpoly.csc.scheduler.model.db.IDBLocation;

public class Location implements Identified {
	IDBLocation underlyingLocation;
	Set<String> providedEquipment;
	
	Location(final IDBLocation underlyingLocation, Set<String> providedEquipment) {
		this.underlyingLocation = underlyingLocation;
		this.providedEquipment = providedEquipment;
	}

	public String getRoom() { return underlyingLocation.getRoom(); }
	public void setRoom(String room) { underlyingLocation.setRoom(room); }
	
	public String getType() { return underlyingLocation.getType(); }
	public void setType(String type) { underlyingLocation.setType(type); }
	
	public String getMaxOccupancy() { return underlyingLocation.getMaxOccupancy(); }
	public void setMaxOccupancy(String maxOccupancy) { underlyingLocation.setMaxOccupancy(maxOccupancy); }
	
	public boolean isSchedulable() { return underlyingLocation.isSchedulable(); }
	public void setIsSchedulable(boolean isSchedulable) { underlyingLocation.setIsSchedulable(isSchedulable); }

	public int getID() { return underlyingLocation.getID(); }

	public Set<String> getProvidedEquipment() { return providedEquipment; }
}
