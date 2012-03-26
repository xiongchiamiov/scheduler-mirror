package scheduler.model;

import scheduler.model.Model;
import junit.framework.*;

public class CSVTest extends TestCase {

	//testcase variables
	Model model;
	//Map<String, UserData> availableSchedules;
	
	//setup called by TestCase when run
	public void setUp() {
		//model = new Model("chem");
		//availableSchedules = model.getSchedules();
	}
	
	//called by TestCase when run
	public void tearDown() {
		
	}
	
	//actual test method, requires 'test' in front of method name
	public void testCSVContainsSchedule() {
		//for (String avail : availableSchedules.keySet())
			//System.out.println(avail);
				
		//assertTrue(availableSchedules.containsKey("Example Chem Schedule"));
	}
	
	public void testExportCSV() {
		/*model.openExistingSchedule("Example Chem Schedule");
		Schedule oldSchedule = model.getSchedule();
		String csv = model.exportToCSV();
		model.openNewScheduleFromCSV("My New Schedule", csv);
		Schedule newSchedule = model.getSchedule();
		assertEquals(newSchedule, oldSchedule); */
	}
}