package scheduler.model.db.sqlite;

import scheduler.model.db.IDBInstructor;
/**
 * The Class SQLInstructor implements all methods of the IDBInstructor class (part of the IDatabase interface).
 * This class represents an instructor in the SQLite database. This maps to the "Instructors" tab in the 
 * resource table view.
 * @author kayleneS
 *
 */
public class SQLInstructor extends SQLObject implements IDBInstructor {
	Integer id, documentID;
	String maxWTU;
	String firstName, lastName, username;
	Boolean schedulable;
	
	public SQLInstructor(Integer id, Integer documentID, String firstname, String lastname,
			String username, String maxWTU, Boolean isSchedulable) {
		super(id);
		//TODO: I don't think this is being used locally, so why's there a var for it?
		this.id = id;
		
		this.documentID = documentID;
		this.firstName = firstname;
		this.lastName = lastname;
		this.username = username;
		this.maxWTU = maxWTU;
		this.schedulable = isSchedulable;
	}
	
	//TODO: just because it may be useful though we should not get null data ever
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
	public boolean isSchedulable() { return schedulable; }
	@Override
	public void setIsSchedulable(boolean isSchedulable) { this.schedulable = isSchedulable; }
	@Override
	public Integer getID(){ return id; };
	
	//Need this method for comparisons in the algorithm
	public boolean isSameInstructorNameAndID(Object other) {
		if(this == other)
			return true;
		if((other == null) || (this.getClass() != other.getClass()))
			return false;
		SQLInstructor instructor = (SQLInstructor)other;
		return this.firstName == instructor.firstName &&
			   this.lastName == instructor.lastName &&
			   this.username == instructor.username;
	}

}
