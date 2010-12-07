package scheduler.view;


import java.util.*;

import scheduler.Scheduler;
import scheduler.db.locationdb.*;
import scheduler.view.view_ui.ViewTypeFilterUI;

/****
 *
 * ViewLocationFilter contains a list of LocationFilterObj 
 * and information which Locations are displaying in the schedule view.
 * @author Sasiluk Ruangrongsorakai (sruangro@calpoly.edu)
 */
public class ViewLocationFilter extends Observable {

   /** list of locations to indicate which location is displaying in the view */
	protected ArrayList<LocationFilterObj> locFil;

    /**
     * Construct this with parent view.
     */
    public ViewLocationFilter(View view, ArrayList<LocationFilterObj> locationFilterList) {
      /* create LocationFilterObj for each Location	*/
    	locFil = locationFilterList;
        
        viewTypeFilterUI = new ViewTypeFilterUI(view, this);
        viewTypeFilterUI.compose();
        addObserver(view);
   }

    /**
     * Sets the location to be visible or filtered out.
     */
    public void setLocationFilter (int locationNum, boolean visible) {
        locFil.get(locationNum).setSelected(visible);
        setChanged();
        notifyObservers(locFil.get(locationNum));
    }

    /**
     * Returns the companion view for this class.
     */
    public ViewTypeFilterUI getViewLocationFilterUI() {
        return viewTypeFilterUI;
    }

    /**
     * Get an ArrayList of Locations
     */
    public ArrayList<LocationFilterObj> getLocationFilterList() {
		return this.locFil;
    }

    /** The panel of locations to be filtered. */
    protected ViewTypeFilterUI viewTypeFilterUI;

}
