package scheduler.model;

import scheduler.model.db.DatabaseTestCase;

public abstract class ModelTestCase extends DatabaseTestCase {
	protected Model createBlankModel() {
		return new Model(createBlankDatabase());
	}
}
