package scheduler.view;

import scheduler.db.coursedb.*;

/****
 * Class CourseFilterObj contains a course object and a boolean value
 * to determine if the course has been selected to be displayed in a schedule view.
 *
 * @author Jason Mak, jamak3@gmail.com
 */

public class CourseFilterObj {

    /**
     * Construct this with the given Course object and selection state.
     *
     *                                                                 <pre>
     * pre: ;
     *
     * post: this.course' == course && this.selected' == selected;
     *
     *                                                                </pre>         *
     * @param course course of this object
     * @param selected selected state
     */
	public CourseFilterObj(Course course, boolean selected){
        this.course = course;
		this.selected = selected;
	}

	/**
	 * Set the course to be displayed or hidden in a view.
     *
     *                                                                 <pre>
     * pre: ;
     *
     * post: this.selected' == s;
     *
     *                                                                </pre>
     * @param s new selected state
     */
	public void setSelected(boolean s){
		selected = s;
	}


	/**
	 * Returns true if the course is selected for display in the schedule view
	 * or false otherwise.
     *
     *
     *                                                                 <pre>
     * pre: ;
     *
     * post: return == this.selected;
     *
     *                                                                </pre>
     * @return current selection state of this course
     */
	public boolean isSelected() {
		return selected;
	}

    /**
     * Returns the course object.
     *
     *                                                                 <pre>
     * pre: ;
     *
     * post: return == this.course;
     *                                                                </pre>       
     * @return the course within this object
     */
    public Course getCourse() {
        return course;
    }

	/** True if the course is selected to be displayed in a view */
	protected boolean selected;

    /** Course associated with this filter object */
    protected Course course;

}
