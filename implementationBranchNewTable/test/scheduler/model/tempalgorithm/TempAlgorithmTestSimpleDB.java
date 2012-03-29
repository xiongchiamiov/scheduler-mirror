package scheduler.model.tempalgorithm;


import scheduler.model.db.IDatabase;

public class TempAlgorithmTestSimpleDB extends TempAlgorithmTest {
	public IDatabase createBlankDatabase() { return new scheduler.model.db.simple.Database(); }
}
