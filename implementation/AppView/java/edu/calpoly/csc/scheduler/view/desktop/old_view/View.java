package edu.calpoly.csc.scheduler.view.desktop.old_view;

import edu.calpoly.csc.scheduler.model.schedule.*;

import javax.swing.*;

import edu.calpoly.csc.scheduler.Scheduler;
import edu.calpoly.csc.scheduler.model.db.cdb.Course;
import edu.calpoly.csc.scheduler.model.db.idb.Instructor;
import edu.calpoly.csc.scheduler.model.db.ldb.Location;
import edu.calpoly.csc.scheduler.model.schedule.Schedule;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;

/****
 * Class View is the top-level model class in the view package. It controls the
 * top level UI and provides methods to view a schedule as a list or a calendar.
 * There are also methods to view a schedule by selectedDay or by week.
 * Methods are provided for extended viewing through popups and dialogs.
 * Methods are provided to filter various aspects of scheduled items. 
 *
 * @author Jason Mak (jamak3@gmail.com)
 */

public class View implements Observer {

    /**
     * Constructs a shell. Default settings are initialized
     * with initSettings.
     *
     * @param scheduler the main program
     */
    public View(Scheduler scheduler) {
        this.scheduler = scheduler;
        viewSettings = new ViewSettings();
        advancedFilter = new AdvancedFilter();
        advancedFilterUI = new AdvancedFilterUI(this);
        advancedFilterButton = new JButton("Advanced Filters");
        advancedFilterButton.addActionListener(
         new ActionListener() {
             public void actionPerformed(ActionEvent ev) {
                 ViewUI.getAdvancedFilterView().setVisible(true);
             }
         }
        );
    }


    /**
     * Display a schedule view for the given schedule with the given information specified in viewSettings, filterOptions, and advancedFilters
     *
     * <b><u>Pre:</u></b>
     *
     *    //The schedule to be viewed cannot be null
     *    schedule != null
     *
     * <b><u>Post:</u></b>
     *
     *    //A view with a mode corresponding to viewMode is drawn.
     *    if (viewMode == ViewMode.LIST)
     *       then (return == new ListViewUI());
     *       else (return == new CalendarViewUI())
     *
     *    //If schedule is null, then throw exception
     *    if (schedule == null)
     *    then throw = NullScheduleException
     *
     * @throws edu.calpoly.csc.scheduler.model.db.idb.Instructor.NullUserIDException check instructor validity
     * @throws NullScheduleException check for null schedule
     */
    public void viewSchedule() throws NullScheduleException, Instructor.NullUserIDException {
        Box vbox;
  
        if (schedule == null) {
            throw new NullScheduleException();
        }

        try {
            scheduler.getView().remove(hbox);
        }  catch (Exception e) {
           // do nothing
           /*
            * Why?
            *  
            *  - Eric
            */
        }

        vbox = Box.createVerticalBox();
        hbox = Box.createHorizontalBox();

        listView = new ListView(this, schedule);
        calendarView = new CalendarView(schedule);

        if ( viewSettings.viewMode.getMode() == ViewMode.Mode.LIST) {
            calendarView.getCalViewUI().setVisible(false);
            listView.getListViewUI().setVisible(true);
        }
        else {
            listView.getListViewUI().setVisible(false);
            calendarView.getCalViewUI().setVisible(true);
        }

        hbox.add(calendarView.getCalViewUI());
        hbox.add(listView.getListViewUI());
        vbox.add(viewSettings.getViewMode().getViewModeUI());
        vbox.add(viewSettings.getViewLevel().getViewLevelUI());
        vbox.add(viewSettings.getFilterOptions().getFilterOptionsUI());
        vbox.add(advancedFilterButton);
        if (viewSettings.getViewType() == ViewType.COURSE) {
            vbox.add(viewCourseFilter.getViewCourseFilterUI());
        } else if(viewSettings.getViewType() == ViewType.INSTRUCTOR) {
            vbox.add(viewInstructorFilter.getViewInstructorFilterUI());
        } else {
            vbox.add(viewLocationFilter.getViewLocationFilterUI());
        }
        hbox.add(vbox);

        scheduler.getView().add(hbox);
        scheduler.getView().setPreferredSize(new Dimension(800, 600));
        scheduler.getView().pack();
    }
    /**
     * Initializes the View object with some default view settings.
     * The default settings views the schedule by course, by Mondays,
     * and in list mode. A few filter options are turned on.
     */
    public void initSettings() {
        boolean[] boolArray = new boolean[21];

        boolArray[1] = true;
        boolArray[2] = true;
        boolArray[18] = true;
        boolArray[19] = true;
        boolArray[20] = true;

        viewSettings.setFilterOptions(new FilterOptions(boolArray));
        viewSettings.setViewType(ViewType.COURSE);
        viewSettings.setViewLevel(new ViewLevel(this, ViewLevel.Level.WEEKLY));
        viewSettings.setViewMode(new ViewMode(this, ViewMode.Mode.LIST));

        Scheduler.schedule.addObserver(this);
        advancedFilter.addObserver(this);
    }

    /**
     * This method creates a view on the screen immediately after a
     * new schedule is generated. It uses the current view settings, which
     * are the default settings if no schedule has been viewed yet.
     * No courses, instructors, or locations of the new schedule
     * are filtered out by default. A user can replace this view with 
     * a new view by course, instructor, or location from under the view menu.
     */
    public void autoView() {
        if (viewSettings.getViewType() == ViewType.COURSE) {
            LinkedList<Course> courseList = schedule.getCourseList();
            ArrayList<CourseFilterObj> courseFilterList = new ArrayList<CourseFilterObj>();

            for (Course aCourse: courseList) {
                if (!aCourse.getCourseType().equals("Lab"))
                courseFilterList.add(
                 new CourseFilterObj(aCourse, true));
            }
            setViewCourseFilter(new ViewCourseFilter(this, courseFilterList));
        } else if (viewSettings.getViewType() == ViewType.INSTRUCTOR) {
            LinkedList<Instructor> instructorList = schedule.getInstructorList();
            ArrayList<InstructorFilterObj> instructorFilterList = new ArrayList<InstructorFilterObj>();

            for (Instructor anInstructor : instructorList) {
                instructorFilterList.add(
                 new InstructorFilterObj(anInstructor, true));
            }
            setViewInstructorFilter(new ViewInstructorFilter(this, instructorFilterList));
        } else {
            LinkedList<Location> locationList = schedule.getLocationList();
            ArrayList<LocationFilterObj> locationFilterList = new ArrayList<LocationFilterObj>();

            for (Location aLocation : locationList) {
                locationFilterList.add(
                 new LocationFilterObj(aLocation, true));
            }
            setViewLocationFilter(new ViewLocationFilter(this, locationFilterList));
        }

        try {
            viewSchedule();
        } catch (NullScheduleException e1) {
            e1.printStackTrace();
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }

    /**
     * Return the advancedFilter object.
     *
     * @return the advanced filters object
     */
    public AdvancedFilter getAdvancedFilter() {
        return advancedFilter;
    }

    /**
     * Set viewSettings to the new viewSettings object.
     *
     * @param viewSettings Viewing settings for this schedule view
     *
     * <b><u>Pre:</u></b>
     *
     * <b><u>Post:</u></b>
     *
     *    //Updated this.viewSettings must be consistent with changes made from GUI
     *    this.viewSettings == viewSettings
     *
     */
    public void setViewSettings (ViewSettings viewSettings) {
        this.viewSettings = viewSettings;
    }

    /**
     * Return the viewSettings object.
     *
     * @return ViewSettings object
     */
    public ViewSettings getViewSettings() {
        return viewSettings;
    }

    /**
     * Set the ViewCourseFilter object to a new one.
     *
     * @param newCourseFilter new ViewCourseFilter object
     */
    public void setViewCourseFilter(ViewCourseFilter newCourseFilter) {
        viewCourseFilter = newCourseFilter;
    }


    /**
     * Set the ViewInstructorFilter object to a new one;
 *
     * @param newInstrFilter new ViewInstructorFilter object
     */
    public void setViewInstructorFilter(ViewInstructorFilter newInstrFilter) {
        viewInstructorFilter = newInstrFilter;
    }

    /**
     * Set the ViewLocationFilter object to a new one;
 *
     * @param newLocationFilter new ViewLocationFilter object
     */
    public void setViewLocationFilter(ViewLocationFilter newLocationFilter) {
        viewLocationFilter = newLocationFilter;
    }

    /**
     * Return the ViewCourseFilter object.
     *
     * @return current filtered courses
     */
    public ViewCourseFilter getViewCourseFilter() {
        return viewCourseFilter;
    }

    /**
     * Return the ViewInstructorFilter object.
     *
     * @return current filtered instructors
     */
    public ViewInstructorFilter getViewInstructorFilter() {
        return viewInstructorFilter;
    }

    /**
     * Return the ViewLocationFilter object;
     *
     * @return current filtered locations
     */
    public ViewLocationFilter getViewLocationFilter() {
        return viewLocationFilter;
    }

    /**
     * Associate a new schedule with the view.
     *
     * @param schedule the schedule to be viewed
     */
    public void setSchedule(Schedule schedule) {
        this.schedule = schedule;
    }

    /**
     * Return the listView object;
     *
     * @return the list view model data.
     */
    public ListView getListView() {
        return listView;
    }

    /**
     * Return the calendarView object.
     *
     * @return the calendar view model data.
     */
    public CalendarView getCalendarView() {
        return calendarView;
    }

    /**
     * Updates the top level UI when one of its observable components changes.
     * Only the calendar or list is updated depending on the active mode.
     *
     * @param O  the observable class changed its state
     * @param arg the argument passed by the observable class through notifyAll
     */
    public void update(Observable O, Object arg) {
      System.err.println ("View updating!");
        if (ViewCourseFilter.class.isInstance(O)) {
            if (viewSettings.getViewMode().getMode() == ViewMode.Mode.LIST) {
                listView.updateCourseFilter((CourseFilterObj) arg);
            }
            else {
            	calendarView.update();
                //calendarView.updateCourseSchedule((CourseFilterObj) arg);
            }
        } else if (ViewInstructorFilter.class.isInstance(O)) {
            if (viewSettings.getViewMode().getMode() == ViewMode.Mode.LIST) {
                listView.updateInstructorFilter((InstructorFilterObj) arg);
            }
            else {
            	calendarView.update();
            }
        } else if (ViewLocationFilter.class.isInstance(O)) {
            if (viewSettings.getViewMode().getMode() == ViewMode.Mode.LIST) {
                listView.updateLocationFilter((LocationFilterObj) arg);
            }
            else {
            	calendarView.update();
            }
        } else if (ViewMode.class.isInstance(O)) {
            Dimension curDimension;
            Point curLocation;

            if (((ViewMode) O).getMode() == ViewMode.Mode.CALENDAR) {
                curDimension = listView.getListViewUI().getSize();
                curLocation = listView.getListViewUI().getLocation();
                calendarView.update();
                listView.getListViewUI().setVisible(false);
                calendarView.getCalViewUI().setVisible(true);
                calendarView.getCalViewUI().setSize(curDimension);
                calendarView.getCalViewUI().setLocation(curLocation);
            } else {
                curDimension = calendarView.getCalViewUI().getSize();
                curLocation = calendarView.getCalViewUI().getLocation();
                listView.update();
                calendarView.getCalViewUI().setVisible(false);
                listView.getListViewUI().setVisible(true);
                listView.getListViewUI().setSize(curDimension);
                listView.getListViewUI().setLocation(curLocation);
            }
        } else if (ViewLevel.class.isInstance(O)) {
            if (viewSettings.getViewMode().getMode() == ViewMode.Mode.LIST) {
                listView.update();
            } else{
                calendarView.update();
            }
        } else if (AdvancedFilter.class.isInstance(O)) {
            if (schedule != null) {
                if (viewSettings.getViewMode().getMode() == ViewMode.Mode.LIST) {
                    listView.update();
                } else {
                    calendarView.update();
                }
            }
        } else if (Schedule.class.isInstance(O)) {
            setSchedule(Scheduler.schedule);
            autoView();
        }
    }

    /**
     * Return the schedule to be viewed.
     *
     * @return current schedule associated with the view
     */
    public Schedule getSchedule() {
        return schedule;
    }


    /**
     * The viewSettings used for drawing a schedule.
     */
    protected ViewSettings viewSettings;

    /** Top level box in the viewing window. */
    protected Box hbox;

    /** The top level program. */
    protected Scheduler scheduler;

    /** The schedule to be viewed. */
    protected Schedule schedule;

    /** filter to show or hide schedule items that contain certain courses */
    protected ViewCourseFilter viewCourseFilter;

    /** filter to show or hide schedule items that contain certain instructors */
    protected ViewInstructorFilter viewInstructorFilter;

    /** filter to show or hide schedule items that contain certain locations */
    protected ViewLocationFilter viewLocationFilter;

    /** filter to show or hide times and dates */
    public static AdvancedFilter advancedFilter;

    protected AdvancedFilterUI advancedFilterUI;

    protected JButton advancedFilterButton;

    /** The view of the schedule as a list. */
    protected ListView listView;

    /** The view of the schedule as a calendar. */
    protected CalendarView calendarView;

}
