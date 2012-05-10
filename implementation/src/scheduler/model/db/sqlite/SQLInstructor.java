package scheduler.model.db.sqlite;

import scheduler.model.db.IDBInstructor;

public class SQLInstructor extends SQLObject implements IDBInstructor {
	Integer id, docID, maxWTU;
	String firstName, lastName, username;
	Boolean schedulable;
	
	public SQLInstructor(Integer id, Integer docID, String firstName, String lastName, 
			String username, Integer maxWTU, Boolean schedulable) {
		super(id);
		this.id = id;
		this.docID = docID;
		this.maxWTU = maxWTU;
		this.firstName = firstName;
		this.lastName = lastName;
		this.username = username;
		this.schedulable = schedulable;
	}
	
	@Override
	public String getFirstName() {
		return firstName;
	}

	@Override
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	@Override
	public String getLastName() {
		return lastName;
	}

	@Override
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	@Override
	public String getUsername() {
		return username;
	}

	@Override
	public void setUsername(String username) {
		this.username = username;
	}

	@Override
	public String getMaxWTU() {
		return maxWTU.toString();
	}

	@Override
	public void setMaxWTU(String maxWTU) {
		this.maxWTU = Integer.valueOf(maxWTU);
	}

	@Override
	public boolean isSchedulable() {
		return schedulable;
	}

	@Override
	public void setIsSchedulable(boolean isSchedulable) {
		this.schedulable = isSchedulable;
	}

}
