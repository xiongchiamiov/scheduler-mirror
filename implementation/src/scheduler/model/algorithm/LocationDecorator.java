package scheduler.model.algorithm;

import scheduler.model.Course;
import scheduler.model.Location;
import scheduler.model.db.DatabaseException;

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
	
	public boolean providesFor(Course c) {
		return (c.getMaxEnrollmentInt() <= getMaxOccupancyInt());
	}
	
	public boolean isTBALocation() throws DatabaseException {
		return (this.location.getDocument().getTBALocation().getID().equals(this.location.getID()));
	}
	
	public int getMaxOccupancyInt() {
		return location.getMaxOccupancyInt();
	}
	
	public Location getLocation() {
		return this.location;
	}
	
	public String location() {
		return location.toString();
	}
}
