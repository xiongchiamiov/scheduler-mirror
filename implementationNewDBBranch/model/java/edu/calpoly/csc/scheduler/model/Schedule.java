package edu.calpoly.csc.scheduler.model;

import edu.calpoly.csc.scheduler.model.db.IDBSchedule;
import edu.calpoly.csc.scheduler.model.db.IDatabase.NotFoundException;

public class Schedule implements Identified {
	private final Model model;
	
	IDBSchedule underlyingSchedule;

	private boolean documentLoaded;
	private Document document;
	
	Schedule(Model model, IDBSchedule underlyingSchedule) {
		this.model = model;
		this.underlyingSchedule = underlyingSchedule;
	}

	public int getID() { return underlyingSchedule.getID(); }
	

	// PERSISTENCE FUNCTIONS

	public Schedule insert() {
		assert(document != null);
		model.database.insertSchedule(document.underlyingDocument, underlyingSchedule);
		return this;
	}

	public void update() {
		model.database.updateSchedule(underlyingSchedule);
	}
	
	public void delete() {
		model.database.deleteSchedule(underlyingSchedule);
	}


	

	// Document
	
	public Document getDocument() throws NotFoundException {
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
