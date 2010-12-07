package scheduler.view;

import java.util.*;

import scheduler.Scheduler;
import scheduler.view.view_ui.ViewTypeFilterUI;

/****
 *
 * Class ViewCourseFilter contains a list of CourseFilterObj
 * that determine which courses are displayed in a schedule view.
 *
 * @author Jason Mak (jamak@calpoly.edu)
 */
public class ViewCourseFilter extends Observable {


    /**
     * Construct this with the given CourseFilterObj list.
     *
     * @param courseFilterList the list to be stored in this object
     */
    public ViewCourseFilter(View view, ArrayList<CourseFilterObj> courseFilterList) {
        this.courseFilterList = courseFilterList;
        viewTypeFilterUI = new ViewTypeFilterUI(view, this);
        viewTypeFilterUI.compose();
        addObserver(view);
    }

    /**
     * Returns the ArrayList of CourseFilterObj
     *
     * @return the list of CourseFilterObj
     */
    public ArrayList<CourseFilterObj> getCourseFilterList() {
        return courseFilterList;
    }

    /**
     * Returns the companion view for this class.
     *
     * @return the companion view
     */
    public ViewTypeFilterUI getViewCourseFilterUI() {
        return viewTypeFilterUI;
    }

    /**
     * Sets the course to be visible or filtered out. The view is notified
     * to display or hide scheduleItems containing the course.
     *
     * @param courseNum the index of the course in the list of CourseFilterObj
     * @param visible the new visibility state of the course
     */
    public void setCourseFilter (int courseNum, boolean visible) {
        courseFilterList.get(courseNum).setSelected(visible);
        setChanged();
        notifyObservers(courseFilterList.get(courseNum));
    }


    /** list of courses and their visibility statuses in a view */
    protected ArrayList<CourseFilterObj> courseFilterList;

    /** The panel of courses to be filtered. */
    protected ViewTypeFilterUI viewTypeFilterUI;
}
