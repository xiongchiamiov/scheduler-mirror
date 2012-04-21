package scheduler.model.db.simple;

import java.io.Serializable;

import scheduler.model.db.IDBObject;

abstract class DBObject implements IDBObject, Serializable {
	private static final long serialVersionUID = 1337L;
	
	Integer id;
	
	@Override
	public Integer getID() { return id; }
	
	public DBObject(Integer id) {
		this.id = id;
	}
	
	public boolean isTransient() { return id == null; }
	abstract public void sanityCheck();
}
