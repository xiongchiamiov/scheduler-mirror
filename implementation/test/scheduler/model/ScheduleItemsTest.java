package scheduler.model;

import java.util.Set;
import java.util.TreeSet;

import scheduler.model.db.DatabaseException;

public abstract class ScheduleItemsTest extends ModelTestCase {
	private Model createModelWithStuff() throws DatabaseException {
		Model model = createBlankModel();
		
		model.createAndInsertDocumentWithSpecialInstructorsAndLocations("doc", 14, 44);
		
		return model;
	}
	
	public void testInsertAndFindScheduleItem() throws DatabaseException {
		Model model = createModelWithStuff();
		Document doc = model.findAllDocuments().iterator().next();

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
			item.setDocument(doc);
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
		model.closeModel();
	}

	public void testScheduleItemLab() throws DatabaseException {

		Model model = createModelWithStuff();
		Document doc = model.findAllDocuments().iterator().next();

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
			item1.setDocument(doc);
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
			item2.setDocument(doc);
			item2.insert();
		}

		item1.setLecture(item2);
		item1.update();
		item2.update();
		
		assertTrue(item1.getLectureOrNull() == item2);
		assertTrue(item2.getLabs().contains(item1));
		
		item1.setLecture(null);
		item1.update();
		item2.update();

		assertFalse(item1.getLectureOrNull() == item2);
		assertFalse(item2.getLabs().contains(item1));
		model.closeModel();
	}
}
