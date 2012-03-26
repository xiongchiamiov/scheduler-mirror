package edu.calpoly.csc.scheduler.view.web.client.calendar;

import java.util.ArrayList;

import edu.calpoly.csc.scheduler.view.web.shared.OldScheduleItemGWT;

/**
 * A dummy schedule for testing
 */
public class DummySchedule extends ArrayList<OldScheduleItemGWT> {
	
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
		
		add(new OldScheduleItemGWT(null, "", "", "ENGL", "101", 1, MWF, 1, 0, 2, 0, "", false));
		add(new OldScheduleItemGWT(null, "", "", "ENGL", "102", 1, TT, 3, 30, 5, 0, "", false));
		add(new OldScheduleItemGWT(null, "", "", "TESTING Long", "10113", 1, TT, 4, 30, 5, 30, "", false));
		add( new OldScheduleItemGWT(null, "", "", "ENGL", "104", 1, MWF, 1, 0, 6, 0, "", false));
		add( new OldScheduleItemGWT(null, "", "", "ENGL", "105", 1, MWF, 1, 0, 2, 50, "", false));
		add( new OldScheduleItemGWT(null, "", "", "ENGL", "106", 1, MWF, 1, 30, 3, 30, "", false));
		add( new OldScheduleItemGWT(null, "", "", "ENGL", "106", 1, MW, 1, 40, 2, 0, "", false));
		add(new OldScheduleItemGWT(null, "", "", "ENGL", "201", 1, MWF, 0,30, 2, 30, "", false));
		add(new OldScheduleItemGWT(null, "", "", "ENGL", "202", 1, TT, 2, 0, 4, 0, "", false));
		add( new OldScheduleItemGWT(null, "", "", "TESTING Long", "10113", 1, TT, 4, 30, 5, 30, "", false));
		add( new OldScheduleItemGWT(null, "", "", "ENGL", "204", 1, MWF, 4, 0, 5, 0, "", false));
		add( new OldScheduleItemGWT(null, "", "", "ENGL", "205", 1, MWF, 5, 0, 7, 0, "", false));
		add(new OldScheduleItemGWT(null, "", "", "ENGL", "206", 1, MWF, 6, 0, 7, 0, "", false));
		add(new OldScheduleItemGWT(null, "", "", "ENGL", "306", 1, MW, 4, 20, 7, 0, "", false));
		add(new OldScheduleItemGWT(null, "", "", "ENGL", "406", 1, MWF, 7, 0, 9, 0, "", false));
		add( new OldScheduleItemGWT(null, "", "", "ENGL", "506", 1, MW, 1, 40, 2, 0, "", false));
		add( new OldScheduleItemGWT(null, "", "", "ENGL", "301", 1, MWF, 0, 0, 1, 30, "", false));
		add( new OldScheduleItemGWT(null, "", "", "ENGL", "402", 1, TT, 6, 0, 9, 0, "", false));
		add( new OldScheduleItemGWT(null, "", "", "TESTING Long", "10113", 1, TT, 2, 30, 4, 0, "", false));
		add( new OldScheduleItemGWT(null, "", "", "ENGL", "504", 1, MWF, 4, 0, 5, 0, "", false));
		add( new OldScheduleItemGWT(null, "", "", "ENGL", "501", 1, MWF, 1, 0, 2, 0, "", false));
		add( new OldScheduleItemGWT(null, "", "", "ENGL", "162", 1, TT, 3, 0, 5, 0, "", false));
		add( new OldScheduleItemGWT(null, "", "", "TESTING Long", "10113", 1, TT, 3, 30, 4, 30, "", false));
		add( new OldScheduleItemGWT(null, "", "", "ENGL", "164", 1, MWF, 0, 0, 3, 0, "", false));
		add( new OldScheduleItemGWT(null, "", "", "ENGL", "155", 1, MWF, 2, 0, 3, 0, "", false));
		add( new OldScheduleItemGWT(null, "", "", "ENGL", "146", 1, MWF, 1, 30, 3, 0, "", false));
		add(  new OldScheduleItemGWT(null, "", "", "ENGL", "136", 1, MW, 0, 40, 2, 0, "", false));
		add(  new OldScheduleItemGWT(null, "", "", "ENGL", "121", 1, MWF, 1, 0, 2, 0, "", false));
		add( new OldScheduleItemGWT(null, "", "", "ENGL", "112", 1, TT, 3, 0, 5, 0, "", false));
		add(  new OldScheduleItemGWT(null, "", "", "TESTING Long", "10113", 1, TT, 2, 0, 5, 30, "", false));
		add(  new OldScheduleItemGWT(null, "", "", "ENGL", "154", 1, MWF, 5, 60, 7, 0, "", false));
		add( new OldScheduleItemGWT(null, "", "", "ENGL", "175", 1, MWF, 5, 0, 7, 0, "", false));
		add( new OldScheduleItemGWT(null, "", "", "ENGL", "186", 1, MWF, 4, 0, 6, 30, "", false));
		add( new OldScheduleItemGWT(null, "", "", "ENGL", "196", 1, MW, 4, 20, 5, 0, "", false));
		add( new OldScheduleItemGWT(null, "", "", "ENGL", "236", 1, MWF, 1, 30, 2, 30, "", false));
		add( new OldScheduleItemGWT(null, "", "", "ENGL", "256", 1, MW, 4, 40, 8, 0, "", false));
		add( new OldScheduleItemGWT(null, "", "", "ENGL", "241", 1, MWF, 3, 0, 5, 0, "", false));
		add( new OldScheduleItemGWT(null, "", "", "ENGL", "222", 1, TT, 3, 0, 5, 0, "", false));
		add( new OldScheduleItemGWT(null, "", "", "TESTING Long", "10113", 1, TT, 3, 30, 5, 30, "", false));
		add( new OldScheduleItemGWT(null, "", "", "ENGL", "344", 1, MWF, 4, 40, 6, 40, "", false));
	}
}
