package edu.calpoly.csc.scheduler.model;

import edu.calpoly.csc.scheduler.model.db.IDBInstructor;

public class Instructor {
	private final IDBInstructor underlyingInstructor;
	
	Instructor(IDBInstructor underlyingInstructor) {
		this.underlyingInstructor = underlyingInstructor;
	}
}
