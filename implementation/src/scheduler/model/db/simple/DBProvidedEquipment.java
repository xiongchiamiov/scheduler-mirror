package scheduler.model.db.simple;

import scheduler.model.db.IDBProvidedEquipment;

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

	public void sanityCheck() {
		assert(locationID != null);
		assert(equipmentTypeID != null);
	}
}
