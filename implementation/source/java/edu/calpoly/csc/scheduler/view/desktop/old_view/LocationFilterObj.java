package edu.calpoly.csc.scheduler.view.desktop.old_view;

import edu.calpoly.csc.scheduler.model.db.ldb.Location;
import scheduler.db.locationdb.*;
/****
 * LocationFilterObj contains strings represent the location: building number
 * and room number
 * @author Sasiluk Ruangrongsorakai, sruangro@calpoly.edu
 */
 
public class LocationFilterObj{
   /** Location Object*/
	protected Location location;
	
	/** true if the location is displaying in a view */
	protected boolean selected;
	
	/**
	 * Construct with the given building number, room number,
    * default value for selected is false.
    */
	public LocationFilterObj(Location l, boolean selected){
		this.location = l;
		this.selected = selected;
	}
	
	/**
	 * Returns the location obj
	 *
	 * 
	 * pre:
	 *    none
	 * post:
	 *    return == location
	 *    
	 */
	public Location getLocation(){
		return this.location;
	}

	/**
	 * Set the filter value for for displaying in the schedule 
	 * 
	 * pre: 
	 *    none
	 *    
	 * post:
	 *    return == selected
	 * 
	 */ 
	
	public boolean isSelected(){
		return this.selected;
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
	
}
