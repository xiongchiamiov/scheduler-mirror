package scheduler.model.db.sqlite;

import scheduler.model.db.IDBUsedEquipment;

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
