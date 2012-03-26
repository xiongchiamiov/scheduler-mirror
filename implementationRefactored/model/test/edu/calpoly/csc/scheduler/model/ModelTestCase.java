package edu.calpoly.csc.scheduler.model;

import edu.calpoly.csc.scheduler.model.db.DatabaseTestCase;

public abstract class ModelTestCase extends DatabaseTestCase {
	protected Model createBlankModel() {
		return new Model(createBlankDatabase());
	}
}
