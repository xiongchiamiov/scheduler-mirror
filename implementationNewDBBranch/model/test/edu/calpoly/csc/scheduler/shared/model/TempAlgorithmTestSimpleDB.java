package model;


import edu.calpoly.csc.scheduler.model.db.IDatabase;

public class TempAlgorithmTestSimpleDB extends TempAlgorithmTest {
	IDatabase createDatabase() { return new edu.calpoly.csc.scheduler.model.db.simple.Database(); }
}
