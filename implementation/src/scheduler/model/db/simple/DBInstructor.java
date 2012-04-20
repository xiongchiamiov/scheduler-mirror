package scheduler.model.db.simple;

import scheduler.model.db.IDBInstructor;

public class DBInstructor extends DBObject implements IDBInstructor {
	Integer documentID;
	String firstName, lastName;
	String username;
	String maxWTU;
	boolean isSchedulable;
	
	public DBInstructor(Integer id, Integer documentID, String firstName, String lastName, String username,
			String maxWTU, boolean isSchedulable) {
		super(id);
		this.documentID = documentID;
		this.firstName = firstName;
		this.lastName = lastName;
		this.username = username;
		this.maxWTU = maxWTU;
		this.isSchedulable = isSchedulable;
	}
	
	public DBInstructor(DBInstructor that) {
		this(that.id, that.documentID, that.firstName, that.lastName, that.username, that.maxWTU, that.isSchedulable);
	}

	public void sanityCheck() {
		assert(documentID != null);
		assert(firstName != null);
		assert(lastName != null);
		assert(username != null);
		assert(maxWTU != null);
	}

	@Override
	public String getFirstName() { return firstName; }
	@Override
	public void setFirstName(String firstName) { this.firstName = firstName; }
	@Override
	public String getLastName() { return lastName; }
	@Override
	public void setLastName(String lastName) { this.lastName = lastName; }
	@Override
	public String getUsername() { return username; }
	@Override
	public void setUsername(String username) { this.username = username; }
	@Override
	public String getMaxWTU() { return maxWTU; }
	@Override
	public void setMaxWTU(String maxWTU) { this.maxWTU = maxWTU; }
	@Override
	public boolean isSchedulable() { return isSchedulable; }
	@Override
	public void setIsSchedulable(boolean isSchedulable) { this.isSchedulable = isSchedulable; }
}
