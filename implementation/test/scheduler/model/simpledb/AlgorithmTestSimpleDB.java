package scheduler.model.simpledb;


import scheduler.model.AlgorithmTest;
import scheduler.model.db.IDatabase;

public class AlgorithmTestSimpleDB extends AlgorithmTest {
	public IDatabase createBlankDatabase() { return new scheduler.model.db.simple.Database(); }
}
