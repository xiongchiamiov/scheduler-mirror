package scheduler.view;

import scheduler.db.instructordb.*;

/****
 * InstructorFilterObj contains a string name represent the instructor's name
 * and a boolean value represents the user selection for viewing a schedule
 * @author Sasiluk Ruangrongsorakai, sruangro@calpoly.edu
 */
 
public class InstructorFilterObj{

   /** instructor object */
	protected Instructor instructor;
   
	/** true if the instructor is displaying in a view */
	protected boolean selected;
	
    /**
     * Construct this with the given instructor's name.
     * default value for selected is false.
     */	
	public InstructorFilterObj(Instructor i, boolean selected){
		this.instructor = i;
		this.selected = selected;
	}
	
	/**
	 * Set the filter value for for displaying in the schedule 
	 * 
	 * pre: 
	 *    none
	 *    
	 * post:
	 *    selected == s
	 * 
	 */
	public void setSelected(boolean s){
		this.selected = s;
	}
	
	/**
	 * Return the instructor obj
	 * 
	 * pre:
	 *    none
	 * post:
	 *    return == selected
	 * 
	 */
	public Instructor getInstructor(){
		return this.instructor;
	}
	
	/** 
	 * Return true if the instructor is in the schedule view
	 * or false otherwise.
	 * 
	 * pre:
	 *    none;
	 * 
	 * post:
	 *    return == Instructor
	 */
	public boolean isSelected(){
		return this.selected;
	}
	
}
