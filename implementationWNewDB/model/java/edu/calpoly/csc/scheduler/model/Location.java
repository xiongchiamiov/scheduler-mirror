package edu.calpoly.csc.scheduler.model;

import edu.calpoly.csc.scheduler.model.db.IDBLocation;

public class Location {
	private final IDBLocation underlyingLocation;
	
	Location(final IDBLocation underlyingLocation) {
		this.underlyingLocation = underlyingLocation;
	}
}
