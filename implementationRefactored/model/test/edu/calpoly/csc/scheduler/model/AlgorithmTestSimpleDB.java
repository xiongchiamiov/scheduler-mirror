package edu.calpoly.csc.scheduler.model;


import edu.calpoly.csc.scheduler.model.db.IDatabase;

public class AlgorithmTestSimpleDB extends AlgorithmTestTwo {
	public IDatabase createBlankDatabase() { return new edu.calpoly.csc.scheduler.model.db.simple.Database(); }
}
