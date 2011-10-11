package edu.calpoly.csc.scheduler.view.web.shared;

import java.io.Serializable;


public class LocationGWT implements Serializable{
	private static final long serialVersionUID = -4158472135773670339L;

	/**
	 * Building number of location.
	 */
	private String building;

	/**
	 * Room number of location.
	 */
	private String room;

	/**
	 * Type of this location.
	 */
	private String type;

	/**
	 * Maximum occupancy of this location.
	 */
	private int maxOccupancy;

	/**
	 * Provided equuipment in this location.
	 */
//	private ProvidedEquipment providedEquipment;

	/**
	 * Whether this location is compliant to those with disabilities.
	 */
//	private boolean adaCompliant;

	/**
    * Represents a location's availabilty throughout the week.
	 */
//   private WeekAvail availability;

	public LocationGWT(){}
	
	public LocationGWT(String building, String room, String type, int maxOccupancy) {
		super();
		this.building = building;
		this.room = room;
		this.type = type;
		this.maxOccupancy = maxOccupancy;
	}

	public String getBuilding() {
		return building;
	}

	public void setBuilding(String building) {
		this.building = building;
	}

	public String getRoom() {
		return room;
	}

	public void setRoom(String room) {
		this.room = room;
	}

	public int getMaxOccupancy() {
		return maxOccupancy;
	}

	public void setMaxOccupancy(int maxOccupancy) {
		this.maxOccupancy = maxOccupancy;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
}
