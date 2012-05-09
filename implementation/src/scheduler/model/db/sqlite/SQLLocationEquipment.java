package scheduler.model.db.sqlite;

import scheduler.model.db.IDBProvidedEquipment;

public class SQLLocationEquipment extends SQLObject implements IDBProvidedEquipment {
	Integer locID, equipID;
	
	public SQLLocationEquipment(Integer id, Integer locID, Integer equipID) {
		super(id);
		this.locID = locID;
		this.equipID = equipID;
	}

	public Integer getLocID() {return locID;}
	public void setLocID(Integer loc) {
		locID = loc;
	}
	
	public Integer getEquipID() {return equipID;}
	public void setEquipID(Integer equip) {
		equipID = equip;
	}
}
