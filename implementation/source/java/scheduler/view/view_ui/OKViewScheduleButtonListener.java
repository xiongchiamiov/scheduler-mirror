package scheduler.view.view_ui;

import scheduler.Scheduler;
import scheduler.db.coursedb.Course;
import scheduler.db.instructordb.Instructor;
import scheduler.db.locationdb.Location;
import scheduler.view.*;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.LinkedList;

/****
 * Class OKViewScheduleButtonListener defines the action listener that is
 * attached to the OK button in the course schedule dialog. When the class
 * is constructed, it is passed references to the classes that need to be
 * accessed in the actionPerformed method. In this case, the required classes
 * are the View model class the ViewSettingsUI view class.
 *
 * Access to the model is for calling the viewSchedule method. Access to the
 * view is for changing its data values.
 *
 * @author Jason Mak
 */
public class OKViewScheduleButtonListener implements ActionListener {
    /**
     * Construct this with the given View model and parent dialog view.
     *
     * @param view parent view
     * @param dialog parent dialog
     */
    public OKViewScheduleButtonListener(View view, ViewSettingsUI dialog) {
        this.view = view;
        this.dialog = dialog;
    }

    /**
     * Respond to a press of the OK button by calling viewSchedule with
     * a new ViewSettings.  The data of ViewSettings are first set with
     * radio buttons and checkboxes in the parent dialog.
     */
    public void actionPerformed(ActionEvent e) {
        JCheckBox[] cbList = dialog.getFilterCheckBoxlist();
        ArrayList<JCheckBox> selectedFilters = dialog.getDataCheckBoxlist();
        boolean[] boolArray = new boolean[cbList.length];

        dialog.setVisible(false);
        if (view.getSchedule() == null)  {
            return;
        }

        for (int k = 0; k < cbList.length; k++) {
            boolArray[k] = cbList[k].isSelected();
        }

        view.getViewSettings().setFilterOptions(new FilterOptions(boolArray));
        view.getViewSettings().setViewType(dialog.getViewType());

        if (view.getViewSettings().getViewType() == ViewType.COURSE) {
            LinkedList<Course> courseList = view.getSchedule().getCourseList();
            ArrayList<CourseFilterObj> courseFilterList = new ArrayList<CourseFilterObj>();

            for (int k = 0; k < selectedFilters.size(); k++) {
                if (!courseList.get(k).getCourseType().equals("Lab"))
                courseFilterList.add(
                 new CourseFilterObj(courseList.get(k), selectedFilters.get(k).isSelected()));
            }
            view.setViewCourseFilter(new ViewCourseFilter(view, courseFilterList));
        } else if (view.getViewSettings().getViewType() == ViewType.INSTRUCTOR) {
            LinkedList<Instructor> instructorList = view.getSchedule().getInstructorList();
            ArrayList<InstructorFilterObj> instructorFilterList = new ArrayList<InstructorFilterObj>();

            for (int k = 0; k < selectedFilters.size(); k++) {
                instructorFilterList.add(
                 new InstructorFilterObj(instructorList.get(k), selectedFilters.get(k).isSelected()));
            }
            view.setViewInstructorFilter(new ViewInstructorFilter(view, instructorFilterList));
        } else {
            LinkedList<Location> locationList = view.getSchedule().getLocationList();
            ArrayList<LocationFilterObj> locationFilterList = new ArrayList<LocationFilterObj>();

            for (int k = 0; k < selectedFilters.size(); k++) {
                locationFilterList.add(
                 new LocationFilterObj(locationList.get(k), selectedFilters.get(k).isSelected()));
            }
            view.setViewLocationFilter(new ViewLocationFilter(view, locationFilterList));
        }

        view.getViewSettings().setViewLevel(dialog.getViewLevel());
        view.getViewSettings().setViewMode(dialog.getViewMode());
        try {
            view.viewSchedule();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

    /** The companion model */
    protected View view;

    /** The parent view */
    protected ViewSettingsUI dialog;
}
