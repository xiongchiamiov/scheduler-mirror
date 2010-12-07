package scheduler.view;

import scheduler.Scheduler;
import scheduler.view.view_ui.ViewModeUI;

import java.util.Observable;

/****
 * Class ViewMode specifies two ways to view a schedule:
 * as a calendar, as a list.
 *
 * @author Jason Mak (jamak3@gmail.com)
 */

public class ViewMode extends Observable {

    /** Enums to represent the two different view modes. */
    public enum Mode {CALENDAR, LIST}

    /**
     * Construct this my setting the current mode to the
     * parameter. Instantiate a new companion view for the new ViewMode.
     *
     *                                                                 <pre>
     * pre: ;
     *
     * post: this.mode' == mode && this.viewModeUI' == viewModeUI
     *       && countObservers() > 0;
     *                                                                </pre>     
     * @param mode the mode after instantiation
     */
    public ViewMode(View view, Mode mode) {
        this.mode = mode;
        viewModeUI = new ViewModeUI(this);
        addObserver(view);
    }

    /**
     * Set the current mode. Notifies the schedule view to switch to
     * calendar or list mode.
     *
     *                                                                 <pre>
     * pre: ;
     *
     * post: this.mode' == mode;
     *                                                                </pre>
     * @param mode the new view mode
     */
    public void setMode(Mode mode) {
        this.mode = mode;
        setChanged();
        notifyObservers();
    }

    /**
     * Return the current mode.
     *
     *                                                                 <pre>
     * post: return == mode;
     *                                                                </pre>
     * @return the current view mode
     */
    public Mode getMode() {
        return mode;
    }

    /**
     * Return the companion view.
     *
     *                                                                 <pre>
     * post: return == viewModeUI;
     *                                                                </pre>
     * @return the companion view
     */
    public ViewModeUI getViewModeUI() {
        return viewModeUI;
    }

    /** The mode value of this object. */
    protected Mode mode;

    /** The companion view. */
    protected ViewModeUI viewModeUI;
}
