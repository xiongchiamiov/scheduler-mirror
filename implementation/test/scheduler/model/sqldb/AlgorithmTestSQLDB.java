package scheduler.model.sqldb;


import scheduler.model.AlgorithmTest;
import scheduler.model.db.IDatabase;

public class AlgorithmTestSQLDB extends AlgorithmTest {
	public IDatabase createBlankDatabase() { return new scheduler.model.db.simple.Database(); }
}
