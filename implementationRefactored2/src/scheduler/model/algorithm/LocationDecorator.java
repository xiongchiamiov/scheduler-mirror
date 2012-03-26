package scheduler.model.algorithm;

import scheduler.model.Location;

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
