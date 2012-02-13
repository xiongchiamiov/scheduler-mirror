package edu.calpoly.csc.scheduler.model;

import edu.calpoly.csc.scheduler.model.db.IDBInstructor;

public class Instructor {
	IDBInstructor underlyingInstructor;
	
	Instructor(IDBInstructor underlyingInstructor) {
		this.underlyingInstructor = underlyingInstructor;
	}
	
	public int getID() { return underlyingInstructor.getID(); }

	public String getFirstName() { return underlyingInstructor.getFirstName(); }
	public void setFirstName(String string) { underlyingInstructor.setFirstName(string); }

	public String getLastName() { return underlyingInstructor.getLastName(); }
	public void setLastName(String lastName) { underlyingInstructor.setLastName(lastName); }
	
	public String getUsername() { return underlyingInstructor.getUsername(); }
	public void setUsername(String username) { underlyingInstructor.setUsername(username); }
	
	public String getMaxWTU() { return underlyingInstructor.getMaxWTU(); }
	public void setMaxWTU(String maxWTU) { underlyingInstructor.setMaxWTU(maxWTU); }
}
