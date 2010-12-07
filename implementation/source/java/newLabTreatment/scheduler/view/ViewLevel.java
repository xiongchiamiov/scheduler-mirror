package scheduler.view;

import scheduler.Scheduler;
import scheduler.view.view_ui.ViewLevelUI;

import java.util.Observable;

/****
 * Class ViewLevel specifies whether a schedule is viewed
 * on per day or a weekly basis.
 *
 * @author Jason Mak (jamak3@gmail.com)
 */
public class ViewLevel extends Observable {

    /** Enums to represent the two view levels. */
    public enum Level {WEEKLY, DAILY}

    /**
     * Construct this by setting the new view level to the parameter.
     * The default day for a daily view is Monday. Instantiates
     * a companion view for the new view level.
     *
     *                                                                 <pre>
     * pre: ;
     *
     * post: this.level' == level && this.day' == DaysInWeek.Day.MON
     *        && this.viewLevelUI' != null && countObservers() > 0;
     *
     *                                                                </pre>     
     * @param level the view level after instantiation
     */
    public ViewLevel(View view, Level level) {
        this.level = level;
        this.day = DaysInWeek.Day.MON;
        viewLevelUI = new ViewLevelUI(this);
        addObserver(view);
    }

    /**
     * Set the view level. Notifies the schedule view to switch view levels.
     *
     *                                                                 <pre>
     * pre: ;
     *
     * post: this.level' == level;
     *                                                                </pre>   
     * @param level the new view level
     */
    public void setLevel(Level level) {
        this.level = level;
        setChanged();
        notifyObservers();
    }

    /**
     * Return the current view level.
     *
     *                                                                 <pre>
     * post: return == level;
     *                                                                </pre>
     * @return the current view level
     */
    public Level getLevel() {
        return level;
    }

    /**
     * Return the companion view.
     *
     *                                                                 <pre>
     * post: return == viewLevelUI;
     *                                                                </pre>
     * @return the companion view
     */
    public ViewLevelUI getViewLevelUI() {
        return viewLevelUI;
    }

    /**
     * Sets the day for a DAILY ViewLevel.
     *
     *                                                                 <pre>
     * pre: ;
     *
     * post: this.day' == day;
     *                                                                </pre>
     * @param day the new day for a daily view 
     */
    public void setDay(DaysInWeek.Day day) {
        this.day = day;
    }

    /**
     * Returns the day corresponding to a DAILY ViewLevel.
     *
     *                                                                 <pre>
     * post: return == day;
     *                                                                </pre>       
     * @return the day for a daily view
     */
    public DaysInWeek.Day getDay() {
        return day;
    }

    /** The day value relevant only to a DAILY ViewLevel. */
    protected DaysInWeek.Day day;

    /** The companion view. */
    protected ViewLevelUI viewLevelUI;

    /** Daily or weekly view. */
    protected Level level;
}
