package edu.calpoly.csc.scheduler;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import edu.calpoly.csc.scheduler.model.Document;
import edu.calpoly.csc.scheduler.model.Location;
import edu.calpoly.csc.scheduler.model.Model;
import edu.calpoly.csc.scheduler.model.db.IDatabase.NotFoundException;

public abstract class LocationsTest extends ModelTestCase {
	public void testInsertAndFindLocation() throws NotFoundException {
		Model model = createBlankModel();
		
		int locationID;
		
		{
			Document doc = model.insertDocument("doc");
			locationID = model.insertLocation(doc, "roomlol", "LEC", "30", new HashSet<String>()).getID();
		}
		
		Location found = model.findLocationByID(locationID);
		assert(found.getRoom().equals("roomlol"));
		assert(found.getType().equals("LEC"));
		assert(found.getMaxOccupancy().equals("30"));
	}

	public void testModifyLocationValueDoesntAutomaticallyUpdateDatabase() throws NotFoundException {
		Model model = createBlankModel();
		
		int locationID;
		
		{
			Document doc = model.insertDocument("doc");
			Location ins = model.insertLocation(doc, "roomlol", "LEC", "30", new HashSet<String>());
			ins.setRoom("derpalisk");
			locationID = ins.getID();
		}
		
		{
			Location ins = model.findLocationByID(locationID);
			assert(ins.getRoom().equals("roomlol"));
		}
	}
	
	public void testUpdateLocation() throws NotFoundException {
		Model model = createBlankModel();

		int locationID;
		
		{
			Document doc = model.insertDocument("doc");
			Location ins = model.insertLocation(doc, "roomlol", "LEC", "30", new HashSet<String>());
			ins.setRoom("hark");
			ins.setType("derp");
			ins.setMaxOccupancy("40");
			locationID = ins.getID();
			model.updateLocation(ins);
		}
		
		Location ins = model.findLocationByID(locationID);
		assert(ins.getRoom().equals("hark"));
		assert(ins.getType().equals("derp"));
		assert(ins.getMaxOccupancy().equals("40"));
	}

	public void testDeleteLocation() throws Exception {
		Model model = createBlankModel();

		int locationID;
		
		{
			Document doc = model.insertDocument("doc");
			Location ins = model.insertLocation(doc, "roomlol", "LEC", "30", new HashSet<String>());
			locationID = ins.getID();
			model.deleteLocation(ins);
		}
		
		try {
			model.findLocationByID(locationID);
			assert(false); // should have failed
		}
		catch (NotFoundException e) { }
	}
	
	public void testFindAllLocationsForDocument() {
		Model model = createBlankModel();

		Set<Integer> locationIDs = new HashSet<Integer>();
		
		Document doc = model.insertDocument("doc");
		locationIDs.add(model.insertLocation(doc, "roomlol", "LEC", "30", new HashSet<String>()).getID());
		locationIDs.add(model.insertLocation(doc, "room2lol", "LEC", "20", new HashSet<String>()).getID());
		
		Collection<Location> returnedLocations = model.findLocationsForDocument(doc);
		for (Location returnedDoc : returnedLocations) {
			assert(locationIDs.contains(returnedDoc.getID()));
			locationIDs.remove(returnedDoc.getID());
		}
		assert(locationIDs.isEmpty());
	}

	public void testFindAllLocationsInMultipleDocuments() {
		Model model = createBlankModel();

		{
			Set<Integer> locationIDs1 = new HashSet<Integer>();
			
			Document doc1 = model.insertDocument("doc1");
			locationIDs1.add(model.insertLocation(doc1, "roomlol", "LEC", "30", new HashSet<String>()).getID());
			locationIDs1.add(model.insertLocation(doc1, "room2lol", "LEC", "20", new HashSet<String>()).getID());
			
			Collection<Location> returnedLocations1 = model.findLocationsForDocument(doc1);
			for (Location returnedDoc : returnedLocations1) {
				assert(locationIDs1.contains(returnedDoc.getID()));
				locationIDs1.remove(returnedDoc.getID());
			}
			assert(locationIDs1.isEmpty());
		}
		
		{
			Set<Integer> locationIDs2 = new HashSet<Integer>();
			
			Document doc2 = model.insertDocument("doc2");
			locationIDs2.add(model.insertLocation(doc2, "room3lol", "LEC", "35", new HashSet<String>()).getID());
			locationIDs2.add(model.insertLocation(doc2, "room4lol", "LEC", "30", new HashSet<String>()).getID());
			
			Collection<Location> returnedLocations2 = model.findLocationsForDocument(doc2);
			for (Location returnedDoc : returnedLocations2) {
				assert(locationIDs2.contains(returnedDoc.getID()));
				locationIDs2.remove(returnedDoc.getID());
			}
			assert(locationIDs2.isEmpty());
		}
	}
}
