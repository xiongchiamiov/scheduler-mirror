package scheduler.model.db.sqlite;

import scheduler.model.db.IDBEquipmentType;
/**
 * The Class SQLEquipmentType implements all methods of the IDBEquipmentType class (part of the IDatabase interface).
 * This class represents the description of a type of equipment used in a classroom in the SQLite database.
 * @author kayleneS
 *
 */
public class SQLEquipmentType extends SQLObject implements IDBEquipmentType {
	int id;
	String description;
	
	public SQLEquipmentType(Integer id, String description) {
		super(id);
		this.description = description;
	}
	
	public SQLEquipmentType(SQLEquipmentType that) {
		this(that.id, that.description);
	}

	@Override
	public void setDescription(String description) { this.description = description; }
	@Override
	public String getDescription() { return description; }
}
