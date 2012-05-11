package scheduler.model;

import scheduler.model.db.IDatabase;

public class AlgorithmLoopJUnitTestSimpleDB extends AlgorithmJUnitTest {
	public IDatabase createBlankDatabase() { return new scheduler.model.db.simple.Database(); }
}
