package scheduler.view.view_ui;

import scheduler.Scheduler;
import scheduler.view.View;
import scheduler.view.ViewType;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

/****
 * Class ViewTypeFilterCheckBoxListener defines the item listener that is
 * attached to each course, instructor, or location checkbox in a schedule view.
 * When the class is constructed, it is passed parameters that need to be
 * accessed in the itemStateChanged method. In this case, the parameter is the
 * number index of the checkbox that had its state changed.
 *
 * On an event, the listener calls a mutator in either ViewCourseFilter,
 * ViewInstructorFilter, or ViewLocationFilter depending on the current
 * view type. 
 *
 * @author Jason Mak  (jamak3@gmail.com)
 */
public class ViewTypeFilterCheckBoxListener implements ItemListener {

    /**
     * Construct this with a number to store the filterOption
     * an object of this class communicates with.
     *
     * @param filterID  the index of the checkbox
     */
    public ViewTypeFilterCheckBoxListener(View view, int filterID) {
        this.view = view;
        viewType = view.getViewSettings().getViewType();
        this.filterID = filterID;
    }

    /**
     * If the checkbox is selected or unselected, update the
     * listView if it is active, otherwise update the calendar view.
     */
    public void itemStateChanged(ItemEvent e) {
        if (viewType == ViewType.COURSE) {
            view.getViewCourseFilter()
             .setCourseFilter(filterID, e.getStateChange() == ItemEvent.SELECTED);
        } else if (viewType == ViewType.INSTRUCTOR) {
            view.getViewInstructorFilter()
             .setInstructorFilter(filterID, e.getStateChange() == ItemEvent.SELECTED);
        } else {
            view.getViewLocationFilter()
             .setLocationFilter(filterID, e.getStateChange() == ItemEvent.SELECTED);
        }
    }

    /** The parent view. */
    protected View view;

    /** The humber corresponding to the filter checkbox. */
    protected int filterID;

    /** The current view type, course, instructor, or location. */
    protected ViewType viewType;
}
