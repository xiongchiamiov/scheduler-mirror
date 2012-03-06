package edu.calpoly.csc.scheduler.model.tempalgorithm;


import edu.calpoly.csc.scheduler.model.db.IDatabase;

public class TempAlgorithmTestSimpleDB extends TempAlgorithmTest {
	public IDatabase createBlankDatabase() { return new edu.calpoly.csc.scheduler.model.db.simple.Database(); }
}
