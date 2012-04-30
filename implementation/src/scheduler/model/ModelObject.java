package scheduler.model;

public abstract class ModelObject {
	public abstract Integer getID();
	
	public abstract void preInsertOrUpdateSanityCheck();

	public boolean isTransient() { return getID() == null; }
}
