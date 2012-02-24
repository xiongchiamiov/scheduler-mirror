package edu.calpoly.csc.scheduler.model;

import java.util.HashSet;
import java.util.Set;

import edu.calpoly.csc.scheduler.model.db.DatabaseException;
import edu.calpoly.csc.scheduler.model.db.IDBEquipmentType;
import edu.calpoly.csc.scheduler.model.db.IDBLocation;
import edu.calpoly.csc.scheduler.model.db.IDBProvidedEquipment;
import edu.calpoly.csc.scheduler.model.db.IDatabase.NotFoundException;

public class Location extends Identified {
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
		model.locationCache.insert(this);
		putProvidedEquipmentIntoDB();
		return this;
	}

	public void update() throws DatabaseException {
		removeProvidedEquipmentFromDB(underlyingLocation);
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
		document = newDocument;
		documentLoaded = true;
		return this;
	}
	
}
