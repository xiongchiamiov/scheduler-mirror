package edu.calpoly.csc.scheduler.model;

import java.util.HashSet;
import java.util.Set;

import edu.calpoly.csc.scheduler.model.db.IDBEquipmentType;
import edu.calpoly.csc.scheduler.model.db.IDBLocation;
import edu.calpoly.csc.scheduler.model.db.IDBProvidedEquipment;
import edu.calpoly.csc.scheduler.model.db.IDatabase.NotFoundException;

public class Location implements Identified {
	private final Model model;
	
	IDBLocation underlyingLocation;

	private boolean documentLoaded;
	private Document document;
	
	boolean providedEquipmentLoaded;
	private Set<String> providedEquipmentDescriptions;
	
	Location(Model model, final IDBLocation underlyingLocation) {
		this.model = model;
		this.underlyingLocation = underlyingLocation;
	}

	// PERSISTENCE FUNCTIONS

	public Location insert() throws NotFoundException {
		model.locationCache.insert(this);
		putProvidedEquipmentIntoDB();
		return this;
	}

	public void update() {
		removeProvidedEquipmentFromDB(underlyingLocation);
		model.database.updateLocation(underlyingLocation);
		putProvidedEquipmentIntoDB();
	}
	
	public void delete() {
		removeProvidedEquipmentFromDB(underlyingLocation);
		model.locationCache.delete(this);
	}


	// ENTITY ATTRIBUTES

	public int getID() { return underlyingLocation.getID(); }

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

	private void putProvidedEquipmentIntoDB() {
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
	
	public Set<String> getProvidedEquipment() {
		if (!providedEquipmentLoaded) {
			providedEquipmentDescriptions = new HashSet<String>();
			for (IDBEquipmentType derp : model.database.findProvidedEquipmentByEquipmentForLocation(underlyingLocation).keySet())
				providedEquipmentDescriptions.add(derp.getDescription());
			providedEquipmentLoaded = true;
		}
		return providedEquipmentDescriptions;
	}

	private void removeProvidedEquipmentFromDB(IDBLocation location) {
		for (IDBProvidedEquipment providedEquipment : model.database.findProvidedEquipmentByEquipmentForLocation(location).values())
			model.database.deleteProvidedEquipment(providedEquipment);
	}

	public void setProvidedEquipment(Set<String> equipment) {
		providedEquipmentLoaded = true;
		this.providedEquipmentDescriptions = equipment;
	}
	

	// Document
	
	public Document getDocument() throws NotFoundException {
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
