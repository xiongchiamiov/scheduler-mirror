package edu.calpoly.csc.scheduler.model;

import java.util.Set;
import java.util.TreeSet;

import edu.calpoly.csc.scheduler.model.db.DatabaseException;

public abstract class ScheduleItemsTest extends ModelTestCase {
	private Model createModelWithStuff() throws DatabaseException {
		Model model = createBlankModel();
		
		Document doc = model.createTransientDocument("doc", 14, 44).insert();
		
		Location tbaLocation = model.createTransientLocation("room", "LEC", "20", true).setDocument(doc).insert();
		doc.setTBALocation(tbaLocation);
		tbaLocation.update();
		
		Instructor staffInstructor = model.createTransientInstructor("e", "o", "eo", "20", true).setDocument(doc).insert();
		doc.setStaffInstructor(staffInstructor);
		staffInstructor.update();
		
		doc.update();
		
		Schedule schedule = model.createTransientSchedule();
		schedule.setDocument(doc);
		schedule.insert();
		
		return model;
	}
	
	public void testInsertAndFindScheduleItem() throws DatabaseException {
		Model model = createModelWithStuff();
		Document doc = model.findAllDocuments().iterator().next();
		Schedule schedule = doc.getSchedules().iterator().next();

		Course course = model.createTransientCourse("Graphics", "101", "GRC", "4", "4", "2", "LEC", "30", "6", true);
		course.setDocument(doc);
		course.insert();
		
		int itemID;
		
		{
			Set<Day> days = new TreeSet<Day>();
			days.add(Day.MONDAY);
			ScheduleItem item = model.createTransientScheduleItem(1, days, 14, 16, true, false);
			item.setInstructor(doc.getStaffInstructor());
			item.setLocation(doc.getTBALocation());
			item.setCourse(course);
			item.setSchedule(schedule);
			item.insert();
			itemID = item.getID();
		}
		
		ScheduleItem found = model.findScheduleItemByID(itemID);
		assertTrue(found.getSection() == 1);
		assertTrue(found.getDays().size() == 1);
		assertTrue(found.getDays().iterator().next() == Day.MONDAY);
		assertTrue(found.getStartHalfHour() == 14);
		assertTrue(found.getEndHalfHour() == 16);
		assertTrue(found.isConflicted() == false);
	}

	public void testScheduleItemLab() throws DatabaseException {

		Model model = createModelWithStuff();
		Document doc = model.findAllDocuments().iterator().next();
		Schedule schedule = doc.getSchedules().iterator().next();

		Set<Day> mwf = new TreeSet<Day>();
		mwf.add(Day.MONDAY);
		mwf.add(Day.WEDNESDAY);
		mwf.add(Day.FRIDAY);

		ScheduleItem item1, item2;
		
		{
			Course course1 = model.createTransientCourse("Graphics", "101", "GRC", "4", "4", "2", "LEC", "30", "6", true);
			course1.setDocument(doc);
			course1.insert();
			
			item1 = model.createTransientScheduleItem(1, mwf, 14, 16, true, false);
			item1.setInstructor(doc.getStaffInstructor());
			item1.setLocation(doc.getTBALocation());
			item1.setCourse(course1);
			item1.setSchedule(schedule);
			item1.insert();
		}

		{
			Course course2 = model.createTransientCourse("Graphics", "101", "GRC", "4", "4", "2", "LEC", "30", "6", true);
			course2.setDocument(doc);
			course2.insert();
			
			item2 = model.createTransientScheduleItem(1, mwf, 14, 16, true, false);
			item2.setInstructor(doc.getStaffInstructor());
			item2.setLocation(doc.getTBALocation());
			item2.setCourse(course2);
			item2.setSchedule(schedule);
			item2.insert();
		}

		item1.setLecture(item2);
		item1.update();
		item2.update();
		
		assertTrue(item1.getLecture() == item2);
		assertTrue(item2.getLabs().contains(item1));
		
		item1.setLecture(null);
		item1.update();
		item2.update();

		assertFalse(item1.getLecture() == item2);
		assertFalse(item2.getLabs().contains(item1));
	}
	
//	public void testInsertAndFindLocationSameInstance() throws DatabaseException {
//		Model model = createBlankModel();
//		
//		Document doc = model.createTransientDocument("doc", START_HALF_HOUR, END_HALF_HOUR).insert();
//		Location insertedLocation = model.createTransientLocation("roomlol", "LEC", "30", true).setDocument(doc).insert();
//		
//		Location found = model.findLocationByID(insertedLocation.getID());
//		assert(insertedLocation == found);
//	}
//	
//	public void testUpdateLocation() throws DatabaseException {
//		Model model = createBlankModel();
//
//		int locationID;
//		
//		{
//			Document doc = model.createTransientDocument("doc", START_HALF_HOUR, END_HALF_HOUR).insert();
//			Location ins = model.createTransientLocation("roomlol", "LEC", "30", true).setDocument(doc).insert();
//			ins.setRoom("hark");
//			ins.setType("derp");
//			ins.setMaxOccupancy("40");
//			locationID = ins.getID();
//			ins.update();
//		}
//		
//		Location ins = model.findLocationByID(locationID);
//		assertTrue(ins.getRoom().equals("hark"));
//		assertTrue(ins.getType().equals("derp"));
//		assertTrue(ins.getMaxOccupancy().equals("40"));
//	}
//
//	public void testDeleteLocation() throws Exception {
//		Model model = createBlankModel();
//
//		int locationID;
//		
//		{
//			Document doc = model.createTransientDocument("doc", START_HALF_HOUR, END_HALF_HOUR).insert();
//			Location ins = model.createTransientLocation("roomlol", "LEC", "30", true).setDocument(doc).insert();
//			locationID = ins.getID();
//			ins.delete();
//		}
//		
//		try {
//			model.findLocationByID(locationID);
//			assertTrue(false); // should have failed
//		}
//		catch (NotFoundException e) { }
//	}
//	
//	public void testFindAllLocationsForDocument() throws DatabaseException {
//		Model model = createBlankModel();
//
//		Set<Integer> locationIDs = new HashSet<Integer>();
//		
//		Document doc = model.createTransientDocument("doc", START_HALF_HOUR, END_HALF_HOUR).insert();
//		locationIDs.add(model.createTransientLocation("roomlol", "LEC", "30", true).setDocument(doc).insert().getID());
//		locationIDs.add(model.createTransientLocation("room2lol", "LEC", "20", true).setDocument(doc).insert().getID());
//		
//		Collection<Location> returnedLocations = model.findLocationsForDocument(doc);
//		for (Location returnedDoc : returnedLocations) {
//			assertTrue(locationIDs.contains(returnedDoc.getID()));
//			locationIDs.remove(returnedDoc.getID());
//		}
//		assertTrue(locationIDs.isEmpty());
//	}
//
//	public void testFindAllLocationsInMultipleDocuments() throws DatabaseException {
//		Model model = createBlankModel();
//
//		{
//			Set<Integer> locationIDs1 = new HashSet<Integer>();
//			
//			Document doc1 = model.createTransientDocument("doc1", START_HALF_HOUR, END_HALF_HOUR).insert();
//			locationIDs1.add(model.createTransientLocation("roomlol", "LEC", "30", true).setDocument(doc1).insert().getID());
//			locationIDs1.add(model.createTransientLocation("room2lol", "LEC", "20", true).setDocument(doc1).insert().getID());
//			
//			Collection<Location> returnedLocations1 = model.findLocationsForDocument(doc1);
//			for (Location returnedDoc : returnedLocations1) {
//				assertTrue(locationIDs1.contains(returnedDoc.getID()));
//				locationIDs1.remove(returnedDoc.getID());
//			}
//			assertTrue(locationIDs1.isEmpty());
//		}
//		
//		{
//			Set<Integer> locationIDs2 = new HashSet<Integer>();
//			
//			Document doc2 = model.createTransientDocument("doc2", START_HALF_HOUR, END_HALF_HOUR).insert();
//			locationIDs2.add(model.createTransientLocation("room3lol", "LEC", "35", true).setDocument(doc2).insert().getID());
//			locationIDs2.add(model.createTransientLocation("room4lol", "LEC", "30", true).setDocument(doc2).insert().getID());
//			
//			Collection<Location> returnedLocations2 = model.findLocationsForDocument(doc2);
//			for (Location returnedDoc : returnedLocations2) {
//				assertTrue(locationIDs2.contains(returnedDoc.getID()));
//				locationIDs2.remove(returnedDoc.getID());
//			}
//			assertTrue(locationIDs2.isEmpty());
//		}
//	}
}
