package edu.calpoly.csc.scheduler;

import junit.framework.TestCase;
import edu.calpoly.csc.scheduler.model.Model;
import edu.calpoly.csc.scheduler.model.db.IDatabase;

public abstract class ModelTestCase extends TestCase{
	protected Model createBlankModel() {
		return new Model(createDatabase());
	}
	
	abstract IDatabase createDatabase();
}
