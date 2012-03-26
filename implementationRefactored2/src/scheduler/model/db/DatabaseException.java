package edu.calpoly.csc.scheduler.model.db;

public class DatabaseException extends Exception {
	public DatabaseException(Throwable thr) {
		super(thr);
	}
	
	public DatabaseException(){}
}
