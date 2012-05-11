package scheduler.model;

import java.util.HashSet;
import java.util.Set;

import scheduler.model.db.DatabaseException;
import scheduler.model.db.IDBEquipmentType;
import scheduler.model.db.IDBLocation;
import scheduler.model.db.IDBProvidedEquipment;
import scheduler.model.db.IDatabase.NotFoundException;

public class Location extends ModelObject {
	private final Model model;
	
	IDBLocation underlyingLocation;

	private boolean documentLoaded;
	private Document document;
	
	boolean providedEquipmentLoaded;
	private Set<String> providedEquipmentDescriptions;
	
	Location(Model model, final IDBLocation underlyingLocation) {
		this.model = model;
		this.underlyingLocation = underlyingLocation;

		if (!underlyingLocation.isTransient())
			assert(!model.locationCache.inCache(underlyingLocation)); // make sure its not in the cache yet (how could it be, we're not even done with the constructor)
	}

	// PERSISTENCE FUNCTIONS

	public Location insert() throws DatabaseException {
		preInsertOrUpdateSanityCheck();
		model.locationCache.insert(this);
		putProvidedEquipmentIntoDB();
		return this;
	}

	public void update() throws DatabaseException {
		removeProvidedEquipmentFromDB(underlyingLocation);
		preInsertOrUpdateSanityCheck();
		model.database.updateLocation(underlyingLocation);
		putProvidedEquipmentIntoDB();
	}
	
	public void delete() throws DatabaseException {
		removeProvidedEquipmentFromDB(underlyingLocation);
		model.locationCache.delete(this);
	}


	// ENTITY ATTRIBUTES

	public Integer getID() { return underlyingLocation.getID(); }

	public boolean isSchedulable() { return underlyingLocation.isSchedulable(); }
	public void setIsSchedulable(boolean isSchedulable) { underlyingLocation.setIsSchedulable(isSchedulable); }

	public String getRoom() { return underlyingLocation.getRoom(); }
	public void setRoom(String room) { underlyingLocation.setRoom(room); }
	
	public String getType() { return underlyingLocation.getType(); }
	public void setType(String type) { underlyingLocation.setType(type); }
	
	public String getMaxOccupancy() { return underlyingLocation.getMaxOccupancy(); }
	public void setMaxOccupancy(String maxOccupancy) { underlyingLocation.setMaxOccupancy(maxOccupancy); }
	public int getMaxOccupancyInt() { return Integer.parseInt(getMaxOccupancy()); }
	
	
	// RELATIONS
	
	// Provided Equipment

	private void putProvidedEquipmentIntoDB() throws DatabaseException {
		if (!providedEquipmentLoaded)
			return;
		try {
			for (String providedEquipmentDescription : providedEquipmentDescriptions) {
				model.database.insertProvidedEquipment(underlyingLocation, model.database.findEquipmentTypeByDescription(providedEquipmentDescription), model.database.assembleProvidedEquipment());
			}
		} catch (NotFoundException e) {
			throw new AssertionError(e);
		}
	}
	
	public Set<String> getProvidedEquipment() throws DatabaseException {
		if (!providedEquipmentLoaded) {
			providedEquipmentDescriptions = new HashSet<String>();
			for (IDBEquipmentType derp : model.database.findProvidedEquipmentByEquipmentForLocation(underlyingLocation).keySet())
				providedEquipmentDescriptions.add(derp.getDescription());
			providedEquipmentLoaded = true;
		}
		return providedEquipmentDescriptions;
	}

	private void removeProvidedEquipmentFromDB(IDBLocation location) throws DatabaseException {
		assert(location!=null);
		assert(location.getID() != null);
		for (IDBProvidedEquipment providedEquipment : model.database.findProvidedEquipmentByEquipmentForLocation(location).values())
			model.database.deleteProvidedEquipment(providedEquipment);
	}

	public void setProvidedEquipment(Set<String> equipment) {
		providedEquipmentLoaded = true;
		this.providedEquipmentDescriptions = equipment;
	}
	

	// Document
	
	public Document getDocument() throws DatabaseException {
		if (!documentLoaded) {
			assert(document == null);
			document = model.findDocumentByID(model.database.findDocumentForLocation(underlyingLocation).getID());
			documentLoaded = true;
		}
		return document;
	}

	public Location setDocument(Document newDocument) {
		assert(!newDocument.isTransient()); // You need to insert something before you can reference it
		document = newDocument;
		documentLoaded = true;
		return this;
	}
	
	public String toString() {
		return this.getRoom();
	}
	
	public boolean equals(Object other) {
		if(this == other)
			return true;
		if((other == null) || (this.getClass() != other.getClass()))
			return false;
		Location loc = (Location)other;
		return this.underlyingLocation.equals(loc.underlyingLocation);
	}

	@Override
	public void preInsertOrUpdateSanityCheck() {
		assert getRoom() != null : "room null";
		assert getType() != null : "type null";
		assert getMaxOccupancy() != null : "maxocc null";
		
		if (documentLoaded)
			assert document != null : "doc null";
		
		if (providedEquipmentLoaded)
			assert providedEquipmentDescriptions != null : "provided equipment descs null";
	}
	
}
