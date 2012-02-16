package edu.calpoly.csc.scheduler.model.db.simple;

import edu.calpoly.csc.scheduler.model.db.IDBProvidedEquipment;

public class DBProvidedEquipment extends DBObject implements IDBProvidedEquipment {
	int locationID;
	int equipmentTypeID;
	
	public DBProvidedEquipment(Integer id, int locationID, int equipmentTypeID) {
		super(id);
		this.locationID = locationID;
		this.equipmentTypeID = equipmentTypeID;
	}
	
	public DBProvidedEquipment(DBProvidedEquipment that) {
		this(that.id, that.locationID, that.equipmentTypeID);
	}
}
