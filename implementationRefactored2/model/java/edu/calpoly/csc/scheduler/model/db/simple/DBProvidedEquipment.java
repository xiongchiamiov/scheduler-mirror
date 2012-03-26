package edu.calpoly.csc.scheduler.model.db.simple;

import edu.calpoly.csc.scheduler.model.db.IDBProvidedEquipment;

public class DBProvidedEquipment extends DBObject implements IDBProvidedEquipment {
	Integer locationID;
	Integer equipmentTypeID;
	
	public DBProvidedEquipment(Integer id, Integer locationID, Integer equipmentTypeID) {
		super(id);
		this.locationID = locationID;
		this.equipmentTypeID = equipmentTypeID;
	}
	
	public DBProvidedEquipment(DBProvidedEquipment that) {
		this(that.id, that.locationID, that.equipmentTypeID);
	}
}
