package scheduler.view.view_ui;

import scheduler.Scheduler;
import scheduler.view.ViewMode;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

/****
 * Class FilterOptionCheckBoxListener defines the item listener that is
 * attached to each filter options checkbox in a schedule view. When the class
 * is constructed, it is passed parameters that need to be
 * accessed in the itemStateChanged method. In this case, the parameter is the
 * number index of the checkbox that had its state changed.
 *
 * On an event, the listener calls the updateColumns method for a list view
 * and the updateLabels method for a calendar view.
 *
 * @author Jason Mak  (jamak3@gmail.com)
 */
public class FilterOptionCheckBoxListener implements ItemListener {

    /**
     * Construct this with a number to store the filterOption
     * an object of this class communicates with.
     *
     * @param filterID index of the checkbox
     */
    public FilterOptionCheckBoxListener(int filterID) {
        this.filterID = filterID;
    }


    /** The humber corresponding to the filterOption checkbox. */
    protected int filterID;

    /**
     * If the checkbox is selected or unselected, update the
     * listView if it is active, otherwise update the calendar view.
     */
    public void itemStateChanged(ItemEvent e) {
        Scheduler.schedView.getViewSettings().getFilterOptions()
         .setFilterOption(filterID, e.getStateChange() == ItemEvent.SELECTED);
        if (Scheduler.schedView.getViewSettings().getViewMode().getMode() == ViewMode.Mode.LIST) {
            Scheduler.schedView.getListView().
             updateColumn(filterID, e.getStateChange() == ItemEvent.SELECTED);
        }
        else {
            Scheduler.schedView.getCalendarView().updateLabel();
        }
    }
}
