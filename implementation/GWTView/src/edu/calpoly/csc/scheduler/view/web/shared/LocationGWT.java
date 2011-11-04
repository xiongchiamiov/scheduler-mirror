package edu.calpoly.csc.scheduler.view.web.shared;

import java.io.Serializable;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RootPanel;


public class LocationGWT implements Serializable{
	private static final long serialVersionUID = 1015108352203434920L;
	
	public class NullGWTDataException extends RuntimeException {
		
	}

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
	
	private String equipmentList; //will be an object
	
	private boolean adacompliant;
	
	private String availability; //will be an object
	
	private String quarterID;
	
	private Integer scheduleID;
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

	public void verify() {
		assert(building != null);
		assert(room != null);
		assert(type != null);
		assert(equipmentList != null);
		assert(availability != null);
		assert(quarterID != null);
	}

	public LocationGWT(){
		building = "";
		room = "";
		type = "";
		maxOccupancy = 0;
		equipmentList = "";
		adacompliant = false;
	}

	public LocationGWT(String building, String name, String room, String type,
			int maxOccupancy, String equipmentList, String additionalDetails) {
		super();
		this.building = building;
		this.room = room;
		this.type = type;
		this.maxOccupancy = maxOccupancy;
		this.equipmentList = equipmentList;
	}
	
	public LocationGWT(String building, String room, int maxOccupancy, String type,
			boolean smartroom, boolean laptopconnectivity, boolean adacompliant,
			boolean overhead) {
		this.building = building;
		this.room = room;
		this.maxOccupancy = maxOccupancy;
		this.type = type;
		this.adacompliant = adacompliant;
	}
	
	public String getAvailability()
	{
		return availability;
	}
	
	public void setAvailability(String availability)
	{
		this.availability = availability;
	}
	
	public String getQuarterID()
	{
		return quarterID;
	}
	
	public void setQuarterID(String quarter)
	{
		this.quarterID = quarter;
	}
	
	public Integer getScheduleID()
	{
		return scheduleID;
	}
	
	public void setScheduleID(Integer id)
	{
		this.scheduleID = id;
	}
	
	public boolean isADACompliant() {
		return adacompliant;
	}
	
	public void setADACompliant(boolean compliant)
	{
		this.adacompliant = compliant;
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

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
}
