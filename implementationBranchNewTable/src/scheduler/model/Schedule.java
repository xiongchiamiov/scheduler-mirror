package scheduler.model;

import java.util.Collection;

import scheduler.model.db.DatabaseException;
import scheduler.model.db.IDBSchedule;

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
		for (ScheduleItem item : getItems())
			item.delete();
		
		model.database.deleteSchedule(underlyingSchedule);
	}


	
	// Schedule Items
	
	public Collection<ScheduleItem> getItems() throws DatabaseException {
		return model.findAllScheduleItemsForSchedule(this);
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
		assert(!newDocument.isTransient()); // You need to insert something before you can reference it
		document = newDocument;
		documentLoaded = true;
		return this;
	}
	
}
