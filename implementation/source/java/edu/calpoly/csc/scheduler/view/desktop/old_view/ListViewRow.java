package edu.calpoly.csc.scheduler.view.desktop.old_view;

import edu.calpoly.csc.scheduler.model.schedule.ScheduleItem;

/****
 * The  class ListViewRow is a container of a ScheduleItem and its form as
 * row data in the list view table. The data is modelled as a vector
 * of objects. This class is only used within ListView.
 *
 * @author Jason Mak (jamak3@gmail.com)
 */
public class ListViewRow {


    /**
     * Construct this with the ScheduleItem and a vector of objects.
     *                                                                 <pre>
     * pre: ;
     *
     * post: this.scheduleItem' == scheduleItem &&
     *       this.rowData' == rowData;
     *                                                                </pre>
     * @param scheduleItem the ScheduleItem
     * @param rowData the ScheduleItem's data as a vector of objects
     */
    public ListViewRow(ScheduleItem scheduleItem, Object[] rowData) {
        this.scheduleItem = scheduleItem;
        this.rowData = rowData;
    }

    /** The ScheduleItem's data as a vector of objects. */
    public Object[] rowData;  

    /** The ScheduleItem. */
    public ScheduleItem scheduleItem;
}
