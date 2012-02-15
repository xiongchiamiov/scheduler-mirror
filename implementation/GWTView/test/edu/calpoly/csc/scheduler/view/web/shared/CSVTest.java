package edu.calpoly.csc.scheduler.view.web.shared;

import java.util.Map;

import edu.calpoly.csc.scheduler.model.Model;
import edu.calpoly.csc.scheduler.model.db.udb.UserData;
import edu.calpoly.csc.scheduler.model.schedule.Schedule;
import junit.framework.TestCase;
import junit.framework.Test.*;

public class CSVTest extends TestCase {

	Model model;
	Map<String, UserData> availableSchedules;
	
	public void setUp() {
		model = new Model("chem");
		availableSchedules = model.getSchedules();
	}
	
	public void tearDown() {
		
	}
	
	public void testCSVContainsSchedule() {
		for (String avail : availableSchedules.keySet())
			System.out.println(avail);
				
		assertTrue(availableSchedules.containsKey("Example Chem Schedule"));
	}
	
	public void testExportCSV() {
		model.openExistingSchedule("Example Chem Schedule");
		Schedule oldSchedule = model.getSchedule();
		String csv = model.exportToCSV();
		model.openNewScheduleFromCSV("My New Schedule", csv);
		Schedule newSchedule = model.getSchedule();
		assertEquals(newSchedule, oldSchedule);
	}
}
