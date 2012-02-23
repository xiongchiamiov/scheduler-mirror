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
			Document doc = model.createTransientDocument("doc", START_HALF_HOUR, END_HALF_HOUR).insert();
			locationID = model.createTransientLocation("roomlol", "LEC", "30", true).setDocument(doc).insert().getID();
		}
		
		Location found = model.findLocationByID(locationID);
		assertTrue(found.getRoom().equals("roomlol"));
		assertTrue(found.getType().equals("LEC"));
		assertTrue(found.getMaxOccupancy().equals("30"));
	}

	public void testInsertAndFindLocationSameInstance() throws NotFoundException {
		Model model = createBlankModel();
		
		Document doc = model.createTransientDocument("doc", START_HALF_HOUR, END_HALF_HOUR).insert();
		Location insertedLocation = model.createTransientLocation("roomlol", "LEC", "30", true).setDocument(doc).insert();
		
		Location found = model.findLocationByID(insertedLocation.getID());
		assert(insertedLocation == found);
	}
	
	public void testUpdateLocation() throws NotFoundException {
		Model model = createBlankModel();

		int locationID;
		
		{
			Document doc = model.createTransientDocument("doc", START_HALF_HOUR, END_HALF_HOUR).insert();
			Location ins = model.createTransientLocation("roomlol", "LEC", "30", true).setDocument(doc).insert();
			ins.setRoom("hark");
			ins.setType("derp");
			ins.setMaxOccupancy("40");
			locationID = ins.getID();
			ins.update();
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
			Document doc = model.createTransientDocument("doc", START_HALF_HOUR, END_HALF_HOUR).insert();
			Location ins = model.createTransientLocation("roomlol", "LEC", "30", true).setDocument(doc).insert();
			locationID = ins.getID();
			ins.delete();
		}
		
		try {
			model.findLocationByID(locationID);
			assertTrue(false); // should have failed
		}
		catch (NotFoundException e) { }
	}
	
	public void testFindAllLocationsForDocument() throws NotFoundException {
		Model model = createBlankModel();

		Set<Integer> locationIDs = new HashSet<Integer>();
		
		Document doc = model.createTransientDocument("doc", START_HALF_HOUR, END_HALF_HOUR).insert();
		locationIDs.add(model.createTransientLocation("roomlol", "LEC", "30", true).setDocument(doc).insert().getID());
		locationIDs.add(model.createTransientLocation("room2lol", "LEC", "20", true).setDocument(doc).insert().getID());
		
		Collection<Location> returnedLocations = model.findLocationsForDocument(doc);
		for (Location returnedDoc : returnedLocations) {
			assertTrue(locationIDs.contains(returnedDoc.getID()));
			locationIDs.remove(returnedDoc.getID());
		}
		assertTrue(locationIDs.isEmpty());
	}

	public void testFindAllLocationsInMultipleDocuments() throws NotFoundException {
		Model model = createBlankModel();

		{
			Set<Integer> locationIDs1 = new HashSet<Integer>();
			
			Document doc1 = model.createTransientDocument("doc1", START_HALF_HOUR, END_HALF_HOUR).insert();
			locationIDs1.add(model.createTransientLocation("roomlol", "LEC", "30", true).setDocument(doc1).insert().getID());
			locationIDs1.add(model.createTransientLocation("room2lol", "LEC", "20", true).setDocument(doc1).insert().getID());
			
			Collection<Location> returnedLocations1 = model.findLocationsForDocument(doc1);
			for (Location returnedDoc : returnedLocations1) {
				assertTrue(locationIDs1.contains(returnedDoc.getID()));
				locationIDs1.remove(returnedDoc.getID());
			}
			assertTrue(locationIDs1.isEmpty());
		}
		
		{
			Set<Integer> locationIDs2 = new HashSet<Integer>();
			
			Document doc2 = model.createTransientDocument("doc2", START_HALF_HOUR, END_HALF_HOUR).insert();
			locationIDs2.add(model.createTransientLocation("room3lol", "LEC", "35", true).setDocument(doc2).insert().getID());
			locationIDs2.add(model.createTransientLocation("room4lol", "LEC", "30", true).setDocument(doc2).insert().getID());
			
			Collection<Location> returnedLocations2 = model.findLocationsForDocument(doc2);
			for (Location returnedDoc : returnedLocations2) {
				assertTrue(locationIDs2.contains(returnedDoc.getID()));
				locationIDs2.remove(returnedDoc.getID());
			}
			assertTrue(locationIDs2.isEmpty());
		}
	}
}
