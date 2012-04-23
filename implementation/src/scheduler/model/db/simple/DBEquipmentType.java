package scheduler.model.db.simple;

import scheduler.model.db.IDBEquipmentType;

public class DBEquipmentType extends DBObject implements IDBEquipmentType {
	private static final long serialVersionUID = 1337L;
	
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

	public void sanityCheck() {
		assert(description != null);
	}
}
