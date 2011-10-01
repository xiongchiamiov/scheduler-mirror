package edu.calpoly.csc.scheduler.view.desktop.old_view;

/****
 * Class ViewSettings consists of data used to determine
 * the way a schedule is drawn. This includes the
 * the type of view, course, instructor, or location, the view mode,
 * calendar or list, the view level, weekly or daily, and various
 * viewing filters.
 *
 * @author Jason Mak (jamak3@gmail.com)
 */

public class ViewSettings {

    /**
     * Set the ViewType object.
     *
     * @param viewType the type of view; course, instructor, or location
     */
    public void setViewType(ViewType viewType) {
        this.viewType = viewType;
    }

    /**
     * Set the ViewMode object.
     *
     * @param viewMode list or calendar
     */
    public void setViewMode(ViewMode viewMode) {
        this.viewMode = viewMode;
    }

    /**
     * Set the ViewLevel object.
     *
     * @param viewLevel weekly or daily
     */
    public void setViewLevel(ViewLevel viewLevel) {
        this.viewLevel = viewLevel;
    }

    /**
     * Set the FilterOptions object.
     *
     * @param filterOptions filter options
     */
    public void setFilterOptions(FilterOptions filterOptions) {
        this.filterOptions = filterOptions;
    }

    /**
     * Returns the view type.
     *
     * @return the current view type
     */
    public ViewType getViewType() {
        return viewType;
    }

    /**
     * Returns the view mode.
     *
     * @return the current view mode
     */
    public ViewMode getViewMode() {
        return viewMode;
    }

    /**
     * Returns the view level.
     *
     * @return the current view level
     */
    public ViewLevel getViewLevel() {
        return viewLevel;
    }

    /**
     * Returns the filter options.
     *
     * @return the current filter options
     */
    public FilterOptions getFilterOptions() {
        return filterOptions;
    }

    /** Type of schedule view, course, instructor, or location */
    protected ViewType viewType;

    /** Mode of schedule view, calendar or list */
    protected ViewMode viewMode;

    /** Viewing level, daily or weekly */
    protected ViewLevel viewLevel;

    /** Viewing filter options */
    protected FilterOptions filterOptions;
}
