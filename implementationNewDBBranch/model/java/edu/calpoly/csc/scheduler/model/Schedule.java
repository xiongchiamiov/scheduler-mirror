package edu.calpoly.csc.scheduler.model;

import edu.calpoly.csc.scheduler.model.db.DatabaseException;
import edu.calpoly.csc.scheduler.model.db.IDBSchedule;

public class Schedule extends Identified {
	private final Model model;
	
	IDBSchedule underlyingSchedule;

	private boolean documentLoaded;
	private Document document;
	
	Schedule(Model model, IDBSchedule underlyingSchedule) {
		this.model = model;
		this.underlyingSchedule = underlyingSchedule;
		
		if (!underlyingSchedule.isTransient())
			assert(!model.scheduleCache.inCache(underlyingSchedule)); // make sure its not in the cache yet (how could it be, we're not even done with the constructor)
	}

	public Integer getID() { return underlyingSchedule.getID(); }
	

	// PERSISTENCE FUNCTIONS

	public Schedule insert() throws DatabaseException{
		assert(document != null);
		model.database.insertSchedule(document.underlyingDocument, underlyingSchedule);
		return this;
	}

	public void update() throws DatabaseException {
		model.database.updateSchedule(underlyingSchedule);
	}
	
	public void delete() throws DatabaseException {
		model.database.deleteSchedule(underlyingSchedule);
	}


	

	// Document
	
	public Document getDocument() throws DatabaseException {
		if (!documentLoaded) {
			assert(document == null);
			document = model.findDocumentByID(model.database.findDocumentForSchedule(underlyingSchedule).getID());
			documentLoaded = true;
		}
		return document;
	}

	public Schedule setDocument(Document newDocument) {
		document = newDocument;
		documentLoaded = true;
		return this;
	}
	
}
