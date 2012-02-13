package edu.calpoly.csc.scheduler.model;

import edu.calpoly.csc.scheduler.model.db.IDBCourse;

public class Course {
	private final IDBCourse underlyingCourse;
	
	Course(IDBCourse underlyingCourse) {
		this.underlyingCourse = underlyingCourse;
	}
}
