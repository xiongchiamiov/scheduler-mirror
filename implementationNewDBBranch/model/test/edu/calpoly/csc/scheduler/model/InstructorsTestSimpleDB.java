package edu.calpoly.csc.scheduler.model;

import edu.calpoly.csc.scheduler.model.db.IDatabase;

public class InstructorsTestSimpleDB extends InstructorsTest {
	public IDatabase createDatabase() { return new edu.calpoly.csc.scheduler.model.db.simple.Database(); }
}