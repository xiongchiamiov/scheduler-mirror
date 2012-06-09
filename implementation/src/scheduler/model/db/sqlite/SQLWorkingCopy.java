package scheduler.model.db.sqlite;
/**
 * The Class SQLWorkingCopy implements all methods of the IDBObject class (part of the IDatabase interface).
 * This class represents the relationship between a working copy and an original document in the SQLite database.
 * @author kayleneS
 *
 */
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
