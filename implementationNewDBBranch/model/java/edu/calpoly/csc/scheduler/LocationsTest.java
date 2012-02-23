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
	private static final int START_HALF_HOUR = 14; // 7am
	private static final int END_HALF_HOUR = 44; // 10pm
	
	public void testInsertAndFindLocation() throws NotFoundException {
		Model model = createBlankModel();
		
		int locationID;
		
		{
			Document doc = model.insertDocument(model.assembleDocument("doc", START_HALF_HOUR, END_HALF_HOUR));
			locationID = model.insertLocation(doc, model.assembleLocation("roomlol", "LEC", "30", new HashSet<String>(), true)).getID();
		}
		
		Location found = model.findLocationByID(locationID);
		assertTrue(found.getRoom().equals("roomlol"));
		assertTrue(found.getType().equals("LEC"));
		assertTrue(found.getMaxOccupancy().equals("30"));
	}

	public void testModifyLocationValueDoesntAutomaticallyUpdateDatabase() throws NotFoundException {
		Model model = createBlankModel();
		
		int locationID;
		
		{
			Document doc = model.insertDocument(model.assembleDocument("doc", START_HALF_HOUR, END_HALF_HOUR));
			Location ins = model.insertLocation(doc, model.assembleLocation("roomlol", "LEC", "30", new HashSet<String>(), true));
			ins.setRoom("derpalisk");
			locationID = ins.getID();
		}
		
		{
			Location ins = model.findLocationByID(locationID);
			assertTrue(ins.getRoom().equals("roomlol"));
		}
	}
	
	public void testUpdateLocation() throws NotFoundException {
		Model model = createBlankModel();

		int locationID;
		
		{
			Document doc = model.insertDocument(model.assembleDocument("doc", START_HALF_HOUR, END_HALF_HOUR));
			Location ins = model.insertLocation(doc, model.assembleLocation("roomlol", "LEC", "30", new HashSet<String>(), true));
			ins.setRoom("hark");
			ins.setType("derp");
			ins.setMaxOccupancy("40");
			locationID = ins.getID();
			model.updateLocation(ins);
		}
		
		Location ins = model.findLocationByID(locationID);
		assertTrue(ins.getRoom().equals("hark"));
		assertTrue(ins.getType().equals("derp"));
		assertTrue(ins.getMaxOccupancy().equals("40"));
	}

	public void testDeleteLocation() throws Exception {
		Model model = createBlankModel();

		int locationID;
		
		{
			Document doc = model.insertDocument(model.assembleDocument("doc", START_HALF_HOUR, END_HALF_HOUR));
			Location ins = model.insertLocation(doc, model.assembleLocation("roomlol", "LEC", "30", new HashSet<String>(), true));
			locationID = ins.getID();
			model.deleteLocation(ins);
		}
		
		try {
			model.findLocationByID(locationID);
			assertTrue(false); // should have failed
		}
		catch (NotFoundException e) { }
	}
	
	public void testFindAllLocationsForDocument() {
		Model model = createBlankModel();

		Set<Integer> locationIDs = new HashSet<Integer>();
		
		Document doc = model.insertDocument(model.assembleDocument("doc", START_HALF_HOUR, END_HALF_HOUR));
		locationIDs.add(model.insertLocation(doc, model.assembleLocation("roomlol", "LEC", "30", new HashSet<String>(), true)).getID());
		locationIDs.add(model.insertLocation(doc, model.assembleLocation("room2lol", "LEC", "20", new HashSet<String>(), true)).getID());
		
		Collection<Location> returnedLocations = model.findLocationsForDocument(doc);
		for (Location returnedDoc : returnedLocations) {
			assertTrue(locationIDs.contains(returnedDoc.getID()));
			locationIDs.remove(returnedDoc.getID());
		}
		assertTrue(locationIDs.isEmpty());
	}

	public void testFindAllLocationsInMultipleDocuments() {
		Model model = createBlankModel();

		{
			Set<Integer> locationIDs1 = new HashSet<Integer>();
			
			Document doc1 = model.insertDocument(model.assembleDocument("doc1", START_HALF_HOUR, END_HALF_HOUR));
			locationIDs1.add(model.insertLocation(doc1, model.assembleLocation("roomlol", "LEC", "30", new HashSet<String>(), true)).getID());
			locationIDs1.add(model.insertLocation(doc1, model.assembleLocation("room2lol", "LEC", "20", new HashSet<String>(), true)).getID());
			
			Collection<Location> returnedLocations1 = model.findLocationsForDocument(doc1);
			for (Location returnedDoc : returnedLocations1) {
				assertTrue(locationIDs1.contains(returnedDoc.getID()));
				locationIDs1.remove(returnedDoc.getID());
			}
			assertTrue(locationIDs1.isEmpty());
		}
		
		{
			Set<Integer> locationIDs2 = new HashSet<Integer>();
			
			Document doc2 = model.insertDocument(model.assembleDocument("doc2", START_HALF_HOUR, END_HALF_HOUR));
			locationIDs2.add(model.insertLocation(doc2, model.assembleLocation("room3lol", "LEC", "35", new HashSet<String>(), true)).getID());
			locationIDs2.add(model.insertLocation(doc2, model.assembleLocation("room4lol", "LEC", "30", new HashSet<String>(), true)).getID());
			
			Collection<Location> returnedLocations2 = model.findLocationsForDocument(doc2);
			for (Location returnedDoc : returnedLocations2) {
				assertTrue(locationIDs2.contains(returnedDoc.getID()));
				locationIDs2.remove(returnedDoc.getID());
			}
			assertTrue(locationIDs2.isEmpty());
		}
	}
}
