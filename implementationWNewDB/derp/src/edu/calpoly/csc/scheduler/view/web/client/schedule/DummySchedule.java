package edu.calpoly.csc.scheduler.view.web.client.schedule;

import java.util.ArrayList;
import java.util.HashMap;

import edu.calpoly.csc.scheduler.view.web.shared.ScheduleItemGWT;

/**
 * A dummy schedule for testing
 */
public class DummySchedule extends HashMap<String, ScheduleItemGWT> {
	
	public DummySchedule() {
		ArrayList<Integer> MWF = new ArrayList<Integer>();
		MWF.add(0);
		MWF.add(2);
		MWF.add(4);
		
		ArrayList<Integer> TT = new ArrayList<Integer>();
		TT.add(1);
		TT.add(3);
		
		ArrayList<Integer> MW = new ArrayList<Integer>();
		MW.add(0);
		MW.add(2);
		
		put("101", new ScheduleItemGWT(null, "", "", "ENGL", "101", 1, MWF, 1, 0, 2, 0, "", false));
		put("102", new ScheduleItemGWT(null, "", "", "ENGL", "102", 1, TT, 3, 30, 5, 0, "", false));
		put("103", new ScheduleItemGWT(null, "", "", "TESTING Long", "10113", 1, TT, 4, 30, 5, 30, "", false));
		put("104", new ScheduleItemGWT(null, "", "", "ENGL", "104", 1, MWF, 1, 0, 6, 0, "", false));
		put("105", new ScheduleItemGWT(null, "", "", "ENGL", "105", 1, MWF, 1, 0, 2, 50, "", false));
		put("106", new ScheduleItemGWT(null, "", "", "ENGL", "106", 1, MWF, 1, 30, 3, 30, "", false));
		put("107", new ScheduleItemGWT(null, "", "", "ENGL", "106", 1, MW, 1, 40, 2, 0, "", false));
		put("108", new ScheduleItemGWT(null, "", "", "ENGL", "201", 1, MWF, 0,30, 2, 30, "", false));
		put("109", new ScheduleItemGWT(null, "", "", "ENGL", "202", 1, TT, 2, 0, 4, 0, "", false));
		put("110", new ScheduleItemGWT(null, "", "", "TESTING Long", "10113", 1, TT, 4, 30, 5, 30, "", false));
		put("111", new ScheduleItemGWT(null, "", "", "ENGL", "204", 1, MWF, 4, 0, 5, 0, "", false));
		put("112", new ScheduleItemGWT(null, "", "", "ENGL", "205", 1, MWF, 5, 0, 7, 0, "", false));
		put("113", new ScheduleItemGWT(null, "", "", "ENGL", "206", 1, MWF, 6, 0, 7, 0, "", false));
		put("114", new ScheduleItemGWT(null, "", "", "ENGL", "306", 1, MW, 4, 20, 7, 0, "", false));
		put("115", new ScheduleItemGWT(null, "", "", "ENGL", "406", 1, MWF, 7, 0, 9, 0, "", false));
		put("116", new ScheduleItemGWT(null, "", "", "ENGL", "506", 1, MW, 1, 40, 2, 0, "", false));
		put("117", new ScheduleItemGWT(null, "", "", "ENGL", "301", 1, MWF, 0, 0, 1, 30, "", false));
		put("118", new ScheduleItemGWT(null, "", "", "ENGL", "402", 1, TT, 6, 0, 9, 0, "", false));
		put("119", new ScheduleItemGWT(null, "", "", "TESTING Long", "10113", 1, TT, 2, 30, 4, 0, "", false));
		put("130", new ScheduleItemGWT(null, "", "", "ENGL", "504", 1, MWF, 4, 0, 5, 0, "", false));
		put("131", new ScheduleItemGWT(null, "", "", "ENGL", "501", 1, MWF, 1, 0, 2, 0, "", false));
		put("132", new ScheduleItemGWT(null, "", "", "ENGL", "162", 1, TT, 3, 0, 5, 0, "", false));
		put("133", new ScheduleItemGWT(null, "", "", "TESTING Long", "10113", 1, TT, 3, 30, 4, 30, "", false));
		put("134", new ScheduleItemGWT(null, "", "", "ENGL", "164", 1, MWF, 0, 0, 3, 0, "", false));
		put("135", new ScheduleItemGWT(null, "", "", "ENGL", "155", 1, MWF, 2, 0, 3, 0, "", false));
		put("136", new ScheduleItemGWT(null, "", "", "ENGL", "146", 1, MWF, 1, 30, 3, 0, "", false));
		put("137", new ScheduleItemGWT(null, "", "", "ENGL", "136", 1, MW, 0, 40, 2, 0, "", false));
		put("138", new ScheduleItemGWT(null, "", "", "ENGL", "121", 1, MWF, 1, 0, 2, 0, "", false));
		put("139", new ScheduleItemGWT(null, "", "", "ENGL", "112", 1, TT, 3, 0, 5, 0, "", false));
		put("140", new ScheduleItemGWT(null, "", "", "TESTING Long", "10113", 1, TT, 2, 0, 5, 30, "", false));
		put("141", new ScheduleItemGWT(null, "", "", "ENGL", "154", 1, MWF, 5, 60, 7, 0, "", false));
		put("142", new ScheduleItemGWT(null, "", "", "ENGL", "175", 1, MWF, 5, 0, 7, 0, "", false));
		put("143", new ScheduleItemGWT(null, "", "", "ENGL", "186", 1, MWF, 4, 0, 6, 30, "", false));
		put("144", new ScheduleItemGWT(null, "", "", "ENGL", "196", 1, MW, 4, 20, 5, 0, "", false));
		put("145", new ScheduleItemGWT(null, "", "", "ENGL", "236", 1, MWF, 1, 30, 2, 30, "", false));
		put("146", new ScheduleItemGWT(null, "", "", "ENGL", "256", 1, MW, 4, 40, 8, 0, "", false));
		put("147", new ScheduleItemGWT(null, "", "", "ENGL", "241", 1, MWF, 3, 0, 5, 0, "", false));
		put("148", new ScheduleItemGWT(null, "", "", "ENGL", "222", 1, TT, 3, 0, 5, 0, "", false));
		put("149", new ScheduleItemGWT(null, "", "", "TESTING Long", "10113", 1, TT, 3, 30, 5, 30, "", false));
		put("150", new ScheduleItemGWT(null, "", "", "ENGL", "344", 1, MWF, 4, 40, 6, 40, "", false));
	}
}
