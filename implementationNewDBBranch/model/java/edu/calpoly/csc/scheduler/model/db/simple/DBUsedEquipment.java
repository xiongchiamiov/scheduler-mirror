package edu.calpoly.csc.scheduler.model.db.simple;

import edu.calpoly.csc.scheduler.model.db.IDBUsedEquipment;

public class DBUsedEquipment extends DBObject implements IDBUsedEquipment {
	int courseID;
	int equipmentTypeID;
	
	public DBUsedEquipment(Integer id, int courseID, int equipmentTypeID) {
		super(id);
		this.courseID = courseID;
		this.equipmentTypeID = equipmentTypeID;
	}
	
	public DBUsedEquipment(DBUsedEquipment that) {
		this(that.id, that.courseID, that.equipmentTypeID);
	}
}
