package edu.calpoly.csc.scheduler.model.algorithm;

import edu.calpoly.csc.scheduler.model.Location;

public class LocationDecorator {

	private Location location;
	private WeekAvail availability;
	
	public LocationDecorator(Location loc) {
		this.location = loc;
		this.availability = new WeekAvail();
	}
	
	public WeekAvail getAvailability() {
		return this.availability;
	}
	
	public Location getLocation() {
		return this.location;
	}
}
