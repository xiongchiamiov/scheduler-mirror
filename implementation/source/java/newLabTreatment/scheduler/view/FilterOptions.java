package scheduler.view;

import scheduler.view.view_ui.FilterOptionsUI;

/****
 * Class FilterOptions consists of boolean variables that determine which
 * information is displayed in a schedule view.
 *
 * @author Jason Mak
 */
public class FilterOptions {

    /**
     * Default construction with all values set to false.
     */
    public FilterOptions() {
        filterOptions = new boolean[21];
        filterOptionsUI = new FilterOptionsUI(this);
        filterOptionsUI.compose();
        
    }

    /** 
     * Construct this with an array of boolean values.
     *
     * @param boolArray all filter option states
     */
    public FilterOptions(boolean[] boolArray) {
        filterOptions = new boolean[21];
        System.arraycopy(boolArray, 0, filterOptions, 0, boolArray.length);
        filterOptionsUI = new FilterOptionsUI(this);
        filterOptionsUI.compose();

    }

    /**
     * Set the filterOption corresonding to option to value.
     *
     * @param option index of filter option
     * @param value new selection state
     */
    public void setFilterOption(int option, boolean value) {
        filterOptions[option] = value;
    }

    /**
     * Return the filterOptionsUI panel.
     *
     * @return the companion view
     */
    public FilterOptionsUI getFilterOptionsUI() {
        return filterOptionsUI;
    }

    /**
     * Return the filter options as an array of booleans.
     *
     * @return the list of filter option states
     */
    public boolean[] toArray() {
        return filterOptions;
    }

   /** An array that determines the on or off of:
     The name of the course. 
     The abbreviated name of the course. 
     A course's section number. 
     A course's number of work-time units. 
     The type of course: lab or lecture. 
     The maximum allowed enrollment in a course. 
     A lecture's courses display of a corresponding lab. 
     Display of a course's required equipment.  
     Display of a course's instructor. 
     Display of an instructor's ID. 
     Display of an instructor's office. 
     Display of an instructor's WTUs. 
     Display of an instructor's disability status. 
     Display of a location's building number. 
     Display of a location's room number. 
     Display of a location's maximum occupancy. 
     Display of a location's type of room. 
     Display of a location's disability Compilance. 
     Display of a course's start time. 
     Display of a course's end time. 
     Display of the days a course is offered. */
    protected boolean filterOptions[];

    /** The panel of for setting filter options. */
    protected FilterOptionsUI filterOptionsUI;

    /** The names of each filterOption. */
    public static String[] filterNames = {
        "Course Name",
        "Course Number",
        "Section",
        "Course WTU",
        "Course Type",
        "Max Enrollment",
        "Has Lab",
        "Equipment",
        "Instructor Name",
        "Instructor ID",
        "Office",
        "Instructor WTU",
        "Instructor Disabilities",
        "Building",
        "Room",
        "Max Occupancy",
        "Room Type",
        "Disabilities Compliance",
        "Start",
        "End",
        "Days Offered"};

}
