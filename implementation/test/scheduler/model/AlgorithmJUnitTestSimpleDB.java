package scheduler.model;

import scheduler.model.db.IDatabase;

public class AlgorithmJUnitTestSimpleDB extends AlgorithmLoopJunitTest {
	public IDatabase createBlankDatabase() { return new scheduler.model.db.simple.Database(); }
}
