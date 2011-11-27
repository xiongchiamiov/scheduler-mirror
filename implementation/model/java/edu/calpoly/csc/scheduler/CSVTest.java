package edu.calpoly.csc.scheduler;

import edu.calpoly.csc.scheduler.model.Model;
import edu.calpoly.csc.scheduler.model.schedule.Schedule;

public class CSVTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Model model = new Model("chem");
		model.getSchedules();
		model.openExistingSchedule("Example Chem Schedule");
		Schedule schedule = new Schedule(model.getInstructors(), model.getLocations());
		schedule.setName("ScheduleNameHere");
		String csv = model.exportToCSV(schedule);
		System.out.println(csv);
		Schedule result = model.importFromCSV(csv);
		assert(schedule.equals(result));
	}
}
