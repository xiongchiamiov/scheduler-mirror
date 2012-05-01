package scheduler.model;

import junit.framework.TestCase;
import scheduler.model.db.IDatabase;


public abstract class ModelTestCase extends TestCase {
	protected Model createBlankModel() {
		return new Model(createBlankDatabase());
	}
	
	abstract protected IDatabase createBlankDatabase();
}
