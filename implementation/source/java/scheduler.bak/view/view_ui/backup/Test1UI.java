package scheduler.view.view_ui;

import scheduler.Scheduler;
import scheduler.db.Time;
import scheduler.db.coursedb.Course;
import scheduler.db.instructordb.Instructor;
import scheduler.db.locationdb.Location;
import scheduler.generate.Schedule;
import scheduler.generate.ScheduleItem;
import scheduler.view.DaysInWeek;
import scheduler.view.View;

import java.util.Vector;
/**
 * Canned Data for Testing view
 * The data is from Appendix A in the requirement page
 * 
 * @author Sasiluk Ruangrongsorakai (sruangro@calpoly.edu)
 */

public class Test1UI {
	Schedule schedule = new Schedule(new Vector<ScheduleItem>());

    public Test1UI(View view) {

        this.view = view;

        DaysInWeek mwf = new DaysInWeek();
        DaysInWeek tth = new DaysInWeek();
		Course course1 = new Course("Introduction to Computer Science I",
			101, 4, 4, "lecture", 40, 3, null, true, false, false);
		Course course2 = new Course("Introduction to Computer Science I",
			101, 4, 4, "lab", 40, 3, null, true, false, false);
        Course course3 = new Course("Introduction to Computer Science II",
    		102, 4, 4, "lecture", 40, 1, null, true, false, false);
        Course course4 = new Course("Introduction to Computer Science II",
    		102, 4, 4, "lab", 40, 1, null, true, false, false);
        Course course5 = new Course("Introduction to Computer Science III",
    		103, 4, 4, "lecture", 40, 2, null, true, false, false);
        Course course6 = new Course("Introduction to Computer Science III",
    		103, 4, 4, "lab", 40, 2, null, true, false, false);
        Course course7 = new Course("Computer Organization",
    		225, 4, 4, "lecture", 40, 1, null, true, false, false);
        Course course8 = new Course("Computer Organization",
    		225, 4, 4, "lab", 40, 1, null, true, false, false);
        Course course9 = new Course("Systems Programming",
    		357, 4, 4, "lecture", 40, 3, null, true, false, false);
        Course course10 = new Course("Systems Programming",
    		357, 4, 4, "lab", 40, 3, null, true, false, false);
        Course course11 = new Course("Operating Systems",
    		453, 4, 4, "lecture", 40, 2, null, true, false, false);
        Course course12 = new Course("Operating Systems",
    		453, 4, 4, "lab", 40, 2, null, true, false, false);
        Course course13 = new Course("Operating Systems II",
    		500, 4, 4, "lecture", 40, 1, null, true, false, false);
        Course course14 = new Course("Operating Systems II",
    		500, 4, 4, "lab", 40, 1, null, true, false, false);
        Course course15 = new Course("Operating Systems III",
    		520, 4, 4, "lecture", 40, 1, null, true, false, false);
        Course course16 = new Course("Operating Systems III",
    		520, 4, 4, "lab", 40, 1, null, true, false, false);

        try {
        	mwf.setDay(DaysInWeek.Day.MON, true);
        	mwf.setDay(DaysInWeek.Day.TUE, false);
        	mwf.setDay(DaysInWeek.Day.WED, true);
        	mwf.setDay(DaysInWeek.Day.THU, false);
        	mwf.setDay(DaysInWeek.Day.FRI, true);
            
        	tth.setDay(DaysInWeek.Day.MON, false);
            tth.setDay(DaysInWeek.Day.TUE, true);
            tth.setDay(DaysInWeek.Day.WED, false);
            tth.setDay(DaysInWeek.Day.THU, true);
            tth.setDay(DaysInWeek.Day.FRI, false);

            this.schedule.add(new ScheduleItem(new Instructor("Gene", "Fisher", "gfisher", 8, new Location("14","123")),
        		course1, new Location("14","256"), 1, mwf, new Time(7,0), new Time (8,0)));
            this.schedule.add(new ScheduleItem(new Instructor("Gene", "Fisher", "gfisher", 8, new Location("14","123")),
        		course2, new Location("14","301"), 2, mwf, new Time(8,0), new Time (9,0)));
            this.schedule.add(new ScheduleItem(new Instructor("Phillip", "Nico", "pnico", 8, new Location("14","123")),
        		course1, new Location("14","256"), 3, mwf, new Time(9,0), new Time (10,0)));
            this.schedule.add(new ScheduleItem(new Instructor("Phillip", "Nico", "pnico", 8, new Location("14","123")),
        		course2, new Location("14","301"), 4, mwf, new Time(10,0), new Time (11,0)));
            this.schedule.add(new ScheduleItem(new Instructor("Kurt", "Mammen", "kmammen", 8, new Location("14","123")),
        		course1, new Location("14","256"), 5, mwf, new Time(11,0), new Time (12,0)));
            this.schedule.add(new ScheduleItem(new Instructor("Kurt", "Mammen", "kmammen", 8, new Location("14","123")),
        		course2, new Location("14","301"), 6, mwf, new Time(12,0), new Time (13,0)));
            this.schedule.add(new ScheduleItem(new Instructor("Kurt", "Mammen", "kmammen", 8, new Location("14","123")),
        		course3, new Location("14","257"), 1, mwf, new Time(9,0), new Time (10,0)));
            this.schedule.add(new ScheduleItem(new Instructor("Kurt", "Mammen", "kmammen", 8, new Location("14","123")),
        		course4, new Location("14","302"), 2, mwf, new Time(10,0), new Time (11,0)));
            this.schedule.add(new ScheduleItem(new Instructor("Julie", "Workman", "jworkman", 8, new Location("14","123")),
        		course5, new Location("14","258"), 1, mwf, new Time(9,0), new Time (10,0)));
            this.schedule.add(new ScheduleItem(new Instructor("Julie", "Workman", "jworkman", 8, new Location("14","123")),
        		course6, new Location("14","303"), 2, mwf, new Time(10,0), new Time (11,0)));
            this.schedule.add(new ScheduleItem(new Instructor("Julie", "Workman", "jworkman", 8, new Location("14","123")),
        		course5, new Location("14","257"), 3, tth, new Time(11,0), new Time (12,30)));
            this.schedule.add(new ScheduleItem(new Instructor("Julie", "Workman", "jworkman", 8, new Location("14","123")),
        		course6, new Location("14","302"), 4, tth, new Time(12,30), new Time (14,0)));
            this.schedule.add(new ScheduleItem(new Instructor("Julie", "Workman", "jworkman", 8, new Location("14","123")),
        		course7, new Location("14","256"), 1, tth, new Time(8,0), new Time (9,30)));
            this.schedule.add(new ScheduleItem(new Instructor("Julie", "Workman", "jworkman", 8, new Location("14","123")),
        		course8, new Location("14","301"), 2, tth, new Time(9,30), new Time (11,0)));
            this.schedule.add(new ScheduleItem(new Instructor("Gene", "Fisher", "gfisher", 8, new Location("14","123")),
        		course9, new Location("14","259"), 1, mwf, new Time(9,0), new Time (10,00)));
            this.schedule.add(new ScheduleItem(new Instructor("Gene", "Fisher", "gfisher", 8, new Location("14","123")),
        		course10, new Location("14","305"), 2, mwf, new Time(10,0), new Time (11,0)));
            this.schedule.add(new ScheduleItem(new Instructor("Kurt", "Mammen", "kmammen", 8, new Location("14","123")),
        		course9, new Location("14","257"), 3, tth, new Time(8,0), new Time (9,30)));
            this.schedule.add(new ScheduleItem(new Instructor("Kurt", "Mammen", "kmammen", 8, new Location("14","123")),
        		course10, new Location("14","302"), 4, tth, new Time(9,30), new Time (11,0)));
            this.schedule.add(new ScheduleItem(new Instructor("Staff", "ffats", "Staff", 8, new Location("14","123")),
        		course9, new Location("-1","-1"), 5, tth, new Time(8,30), new Time (10,0)));
            this.schedule.add(new ScheduleItem(new Instructor("Staff", "ffats", "Staff", 8, new Location("14","123")),
        		course10, new Location("-1","-1"), 6, tth, new Time(10,0), new Time (11,30)));
            this.schedule.add(new ScheduleItem(new Instructor("Phillip", "Nico", "pnico", 8, new Location("14","123")),
        		course11, new Location("14","257"), 1, mwf, new Time(11,0), new Time (12,00)));
            this.schedule.add(new ScheduleItem(new Instructor("Phillip", "Nico", "pnico", 8, new Location("14","123")),
        		course12, new Location("14","302"), 2, mwf, new Time(12,0), new Time (13,0)));
            this.schedule.add(new ScheduleItem(new Instructor("Gene", "Fisher", "gfisher", 8, new Location("14","123")),
        		course11, new Location("14","258"), 3, tth, new Time(8,0), new Time (9,30)));
            this.schedule.add(new ScheduleItem(new Instructor("Gene", "Fisher", "gfisher", 8, new Location("14","123")),
        		course12, new Location("14","303"), 4, tth, new Time(9,30), new Time (11,0)));
            this.schedule.add(new ScheduleItem(new Instructor("Phillip", "Nico", "pnico", 8, new Location("14","123")),
        		course13, new Location("14","258"), 1, tth, new Time(11,0), new Time (12,30)));
            this.schedule.add(new ScheduleItem(new Instructor("Phillip", "Nico", "pnico", 8, new Location("14","123")),
        		course14, new Location("14","303"), 2, tth, new Time(12,30), new Time (14,0)));
            this.schedule.add(new ScheduleItem(new Instructor("Staff", "ffats", "Staffs", 8, new Location("14","123")),
	    		course15, new Location("14","259"), 1, tth, new Time(8,30), new Time (10,0)));
	        this.schedule.add(new ScheduleItem(new Instructor("Staff", "ffats", "Staffs", 8, new Location("14","123")),
	    		course16, new Location("14","305"), 2, tth, new Time(10,0), new Time (11,30)));
        } catch (Exception e) {
            System.out.println("Schedule.add  " +e);
        }
        view.setSchedule(this.schedule);
        view.autoView();
    }
    
    /** The parent view. */
    protected View view;
}

