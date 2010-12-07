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
 * All canned stuff.
 *
 * @author Jason Mak
 */
public class SampleUI  {

    Schedule schedule = new Schedule(new Vector<ScheduleItem>());

    public SampleUI(View view) {

        this.view = view;
        DaysInWeek test = new DaysInWeek();

        Course course2 = new Course("Introduction to Computer Science",
         101, 4, 4, "lab", 40, 1, null, true, false, false);
        Course course1 = new Course("Introduction to Computer Science",
         101, 4, 4, "lecture", 40, 1, course2, false, true, true);
        Course course3 = new Course("Intro to Computer Science II",
         102, 4, 4, "lecture", 40, 1, null, false, true, true);
        Course course4 = new Course("Intro to Computer Science III",
         103, 4, 4, "lecture", 40, 1, null, false, true, true);
        Course course5 = new Course("Computer Organization",
         225, 4, 4, "lecture", 40, 1, null, false, true, true);
        Course course6 = new Course("Professional Responsibilities",
         300, 4, 4, "lecture", 40, 1, null, false, true, true);
        Course course7 = new Course("Computer Architecture",
         315, 4, 4, "lecture", 40, 1, null, false, true, true);
        Course course8 = new Course("Systems Programming",
         357, 4, 4, "lecture", 40, 1, null, false, true, true);
        Course course9 = new Course("Programming Languages I",
         430, 4, 4, "lecture", 40, 1, null, false, true, true);
        Course course10 = new Course("Operating Systems",
         453, 4, 4, "lecture", 40, 1, null, true, true, true);
        Course course11 = new Course("Operating Systems",
         453, 4, 4, "lab", 40, 1, null, true, true, true);


        try {
            test.setDay(DaysInWeek.Day.MON, false);
            test.setDay(DaysInWeek.Day.TUE, true);
            test.setDay(DaysInWeek.Day.WED, false);
            test.setDay(DaysInWeek.Day.THU, true);
            test.setDay(DaysInWeek.Day.FRI, false);

            this.schedule.add(new ScheduleItem(new Instructor("Gene", "Fisher", "gfisher", 8, new Location("14","221"))
             , course1, new Location("14","123"), 1, new DaysInWeek(), new Time(7,0), new Time (8,0)));
            this.schedule.add(new ScheduleItem(new Instructor("Bellardo", "John", "bellardo", 8, new Location("14","222"))
             , course2, new Location("14","234"), 1, new DaysInWeek(), new Time(8,0), new Time (11,0)));
            this.schedule.add(new ScheduleItem(new Instructor("Bellardo", "John", "bellardo", 8, new Location("14","222"))
             , course2, new Location("14","234"), 1, new DaysInWeek(), new Time(12,0), new Time (14,0)));
            this.schedule.add(new ScheduleItem(new Instructor("Bellardo", "John", "bellardo", 8, new Location("14","222"))
             , course2, new Location("14","234"), 1, new DaysInWeek(), new Time(15,0), new Time (16,0)));
            this.schedule.add(new ScheduleItem(new Instructor("Bellardo", "John", "bellardo", 8, new Location("14","222"))
             , course1, new Location("14","234"), 1, new DaysInWeek(), new Time(17,0), new Time (18,0)));
            //this.schedule.add(new ScheduleItem(new Instructor("Aaron", "Keen", "akeen", 8, new Location("14","223"))
            // , viewCourseFilter.course3, new Location("14","235"), 1, new DaysInWeek(), new Time(7,0), new Time (8,30)));
            //this.schedule.add(new ScheduleItem(new Instructor("Philip", "Nico", "pnico", 8, new Location("14","111"))
            //  , viewCourseFilter.course4, new Location("14","236"), 1, new DaysInWeek(), new Time(7,0), new Time (9,0)));
            this.schedule.add(new ScheduleItem(new Instructor("Julie", "Workman", "jhatalsk", 8, new Location("14","155"))
             , course5, new Location("14","237"), 1, new DaysInWeek(), new Time(14,0), new Time (15,0)));
            // this.schedule.add(new ScheduleItem(new Instructor("Clint", "Staley", "cstaley", 8, new Location("14","134"))
            // , viewCourseFilter.course6, new Location("14","238"), 1, new DaysInWeek(), new Time(7,0), new Time (9,0)));
            this.schedule.add(new ScheduleItem(new Instructor("Chris", "Buckalew", "buckalew", 8, new Location("14","177"))
             , course7, new Location("14","239"), 1, test, new Time(18,0), new Time (19,0)));
            this.schedule.add(new ScheduleItem(new Instructor("Ignatios", "Valkalis", "igval", 8, new Location("14","188"))
             , course8, new Location("14","251"), 1, new DaysInWeek(), new Time(19,0), new Time (20,0)));
            this.schedule.add(new ScheduleItem(new Instructor("Chris", "Lupo", "clupo", 8, new Location("14","165"))
             , course9, new Location("14","252"), 1, new DaysInWeek(), new Time(13,0), new Time (14,0)));
            this.schedule.add(new ScheduleItem(new Instructor("Kevin", "Ogorman", "kogorman", 8, new Location("14","175"))
             , course10, new Location("14","255"), 1, new DaysInWeek(), new Time(11,0), new Time (12,0)));
            this.schedule.add(new ScheduleItem(new Instructor("New", "Teacher", "kogorman", 8, new Location("14","176"))
             , course11, new Location("14","256"), 1, new DaysInWeek(), new Time(15,0), new Time (16,0)));
            this.schedule.add(new ScheduleItem(new Instructor("Kevin7", "Ogorman", "kogorman", 8, new Location("14","177"))
             , course10, new Location("14","257"), 1, new DaysInWeek(), new Time(15,0), new Time (16,0)));
            this.schedule.add(new ScheduleItem(new Instructor("Kevin", "Ogorman", "kogorman", 8, new Location("14","175"))
             , course10, new Location("14","255"), 1, new DaysInWeek(), new Time(12,0), new Time (13,0)));
            this.schedule.add(new ScheduleItem(new Instructor("Kevin", "Ogorman", "kogorman", 8, new Location("14","175"))
             , course10, new Location("14","255"), 1, new DaysInWeek(), new Time(13,0), new Time (14,0)));
            this.schedule.add(new ScheduleItem(new Instructor("Kevin", "Ogorman", "kogorman", 8, new Location("14","175"))
             , course9, new Location("14","255"), 1, new DaysInWeek(), new Time(15,0), new Time (16,0)));
        } catch (Exception e) {
            System.out.println("Schedule.add  " +e);
        }

        view.setSchedule(this.schedule);
        view.autoView();
    }

    /** The parent view. */
    protected View view;
}
