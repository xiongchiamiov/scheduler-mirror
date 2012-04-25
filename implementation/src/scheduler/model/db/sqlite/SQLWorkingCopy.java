package scheduler.model.db.sqlite;

public class SQLWorkingCopy extends SQLObject {
	Integer originalDocID;
	
	public SQLWorkingCopy(Integer id, Integer originalDocID) {
		super(id);
		this.originalDocID = originalDocID;
	}

	public Integer getOriginalDocID() {return originalDocID;}
	public void setOriginalDocID(Integer original) {
		originalDocID = original;
	}
}
