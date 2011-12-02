package edu.calpoly.csc.scheduler.view.web.shared;

import java.io.Serializable;


public class LocationGWT implements Serializable, Identified {
	public static class ProvidedEquipmentGWT implements Serializable {
		private static final long serialVersionUID = 42;
		public boolean hasOverhead = false;
		public boolean isSmartRoom = false;
		public boolean hasLaptopConnectivity = false;
		
		ProvidedEquipmentGWT(ProvidedEquipmentGWT that) {
			this.hasLaptopConnectivity = that.hasLaptopConnectivity;
			this.hasOverhead = that.hasOverhead;
			this.isSmartRoom = that.isSmartRoom;
		}
		
		public ProvidedEquipmentGWT() { }

		public int compareTo(ProvidedEquipmentGWT that) {
			if (hasOverhead != that.hasOverhead)
				return (this.hasOverhead ? 1 : 0) - (that.hasOverhead ? 1 : 0);
			if (isSmartRoom != that.isSmartRoom)
				return (this.isSmartRoom ? 1 : 0) - (that.isSmartRoom ? 1 : 0);
			if (hasOverhead != that.hasOverhead)
				return (this.hasLaptopConnectivity ? 1 : 0) - (that.hasLaptopConnectivity ? 1 : 0);
			return 0;
		}
	}

	private static final long serialVersionUID = 1015108352203434920L;

	private int id;
	private String building;
	private String room;
	private String type;
	private int maxOccupancy;
	private boolean adacompliant;
	private ProvidedEquipmentGWT equipment;
	
	public LocationGWT() { }

	public LocationGWT(int id, String building, String room, String type, int maxOccupancy, boolean adacompliant, ProvidedEquipmentGWT equipment) {
		super();
		this.id = id;
		this.building = building;
		this.room = room;
		this.type = type;
		this.maxOccupancy = maxOccupancy;
		this.adacompliant = adacompliant;
		this.equipment = equipment;
	}

	public LocationGWT(int id, LocationGWT other) {
		this.id = id;
		this.building = other.building;
		this.room = other.room;
		this.type = other.type;
		this.maxOccupancy = other.maxOccupancy;
		this.adacompliant = other.adacompliant;
		this.equipment = new ProvidedEquipmentGWT(other.equipment);
	}
	
	public LocationGWT(LocationGWT that) {
		this(that.id, that.building, that.room, that.type, that.maxOccupancy, that.adacompliant, new ProvidedEquipmentGWT(that.equipment));
		// TODO Auto-generated constructor stub
	}

	public Integer getID() { return id; }
	public void setID(Integer id) { this.id = id; }
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
	public ProvidedEquipmentGWT getEquipment() { return equipment; }
	public void setEquipment(ProvidedEquipmentGWT equipment) { this.equipment = equipment; }
}
