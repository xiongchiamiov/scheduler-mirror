package edu.calpoly.csc.scheduler.model;

public abstract class Identified {
	public abstract Integer getID();

	public boolean isTransient() { return getID() == null; }
}
