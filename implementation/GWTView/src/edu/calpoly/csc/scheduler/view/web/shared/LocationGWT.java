package edu.calpoly.csc.scheduler.view.web.shared;

import java.io.Serializable;


public class LocationGWT implements Serializable{
	private static final long serialVersionUID = 1015108352203434920L;

	/**
	 * Building number of location.
	 */
	private String building;

	private String name;
	
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
	
	private String equipmentList;
	
	private String additionalDetails;
	
	private boolean smartroom;
	
	private boolean laptopconnectivity;
	
	private boolean addacompliant;
	
	private boolean overhead;

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

	public LocationGWT(String building, String name, String room, String type,
			int maxOccupancy, String equipmentList, String additionalDetails) {
		super();
		this.building = building;
		this.name = name;
		this.room = room;
		this.type = type;
		this.maxOccupancy = maxOccupancy;
		this.equipmentList = equipmentList;
		this.additionalDetails = additionalDetails;
	}
	
	public LocationGWT(String building, String room, int maxOccupancy, String type,
			boolean smartroom, boolean laptopconnectivity, boolean addacompliant,
			boolean overhead) {
		this.building = building;
		this.room = room;
		this.maxOccupancy = maxOccupancy;
		this.type = type;
		this.smartroom = smartroom;
		this.laptopconnectivity = laptopconnectivity;
		this.addacompliant = addacompliant;
		this.overhead = overhead;
	}

	public String getBuilding() {
		return building;
	}

	public void setBuilding(String building) {
		this.building = building;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getRoom() {
		return room;
	}

	public void setRoom(String room) {
		this.room = room;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getMaxOccupancy() {
		return maxOccupancy;
	}

	public void setMaxOccupancy(int maxOccupancy) {
		this.maxOccupancy = maxOccupancy;
	}

	public String getEquipmentList() {
		return equipmentList;
	}

	public void setEquipmentList(String equipmentList) {
		this.equipmentList = equipmentList;
	}

	public String getAdditionalDetails() {
		return additionalDetails;
	}

	public void setAdditionalDetails(String additionalDetails) {
		this.additionalDetails = additionalDetails;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
}
