package scheduler.model.db.sqlite;

import scheduler.model.db.IDBProvidedEquipment;

public class SQLProvidedEquipment extends SQLObject implements IDBProvidedEquipment {
	Integer locationID;
	Integer equipmentTypeID;
	
	public SQLProvidedEquipment(Integer id, Integer locationID, Integer equipmentTypeID) {
		super(id);
		this.locationID = locationID;
		this.equipmentTypeID = equipmentTypeID;
	}
	
	public SQLProvidedEquipment(SQLProvidedEquipment that) {
		this(that.id, that.locationID, that.equipmentTypeID);
	}
}
