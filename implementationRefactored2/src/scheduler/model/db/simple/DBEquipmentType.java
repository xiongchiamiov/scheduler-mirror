package scheduler.model.db.simple;

import scheduler.model.db.IDBEquipmentType;

public class DBEquipmentType extends DBObject implements IDBEquipmentType {
	int id;
	String description;
	
	public DBEquipmentType(Integer id, String description) {
		super(id);
		this.description = description;
	}
	
	public DBEquipmentType(DBEquipmentType that) {
		this(that.id, that.description);
	}

	@Override
	public void setDescription(String description) { this.description = description; }
	@Override
	public String getDescription() { return description; }
}
