package scheduler.model.db.sqlite;

import scheduler.model.db.IDBUsedEquipment;
/**
 * The Class SQLUsedEquipment implements all methods of the IDBUsedEquipment class (part of the IDatabase interface).
 * This class represents the relationship between a course and it's required equipment in the SQLite database.
 * @author kayleneS
 *
 */
public class SQLUsedEquipment extends SQLObject implements IDBUsedEquipment {
	Integer courseID;
	Integer equipmentTypeID;
	
	public SQLUsedEquipment(Integer id, Integer courseID, Integer equipmentTypeID) {
		super(id);
		this.courseID = courseID;
		this.equipmentTypeID = equipmentTypeID;
	}
	
	public SQLUsedEquipment(SQLUsedEquipment that) {
		this(that.id, that.courseID, that.equipmentTypeID);
	}
}
