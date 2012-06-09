package scheduler.model.db.sqlite;

import scheduler.model.db.IDBProvidedEquipment;
/**
 * The Class SQLProvidedEquipment implements all methods of the IDBProvidedEquipment class (part of the IDatabase interface).
 * This class represents a the relationship between a location and the equipment at that location in the SQLite database.
 * @author kayleneS
 *
 */
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
