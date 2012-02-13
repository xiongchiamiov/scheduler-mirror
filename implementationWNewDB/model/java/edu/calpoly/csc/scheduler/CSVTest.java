//package edu.calpoly.csc.scheduler;
//
//import java.util.Map;
//
//import edu.calpoly.csc.scheduler.model.OldModel;
//import edu.calpoly.csc.scheduler.model.db.udb.UserData;
//import edu.calpoly.csc.scheduler.model.oldschedule.Schedule;
//
//public class CSVTest {
//
//	/**
//	 * @param args
//	 */
//	public static void main(String[] args) {
//		// TODO Auto-generated method stub
//		OldModel model = new OldModel("chem");
//		Map<String, UserData> availableSchedules = model.getSchedules();
//		for (String avail : availableSchedules.keySet())
//			System.out.println(avail);
//		assert(availableSchedules.containsKey("Example Chem Schedule"));
//		model.openExistingSchedule("Example Chem Schedule");
//		Schedule oldSchedule = model.getSchedule();
//		String csv = model.exportToCSV();
//		model.openNewScheduleFromCSV("My New Schedule", csv);
//		Schedule newSchedule = model.getSchedule();
//		assert(newSchedule.equals(oldSchedule));
//	}
//}
