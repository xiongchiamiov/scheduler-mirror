package edu.calpoly.csc.scheduler.view.desktop.old_view;


import java.util.*;

import edu.calpoly.csc.scheduler.model.db.idb.InstructorDB;
import scheduler.*;
import scheduler.db.instructordb.*;

/****
 *
 * ViewInstructorFilter contains a list of InstructorFilterObj 
 * and information which instructors are displaying in the schedule view.
 * @author Sasiluk Ruangrongsorakai (sruangro@calpoly.edu)
 *
 */
public class ViewInstructorFilter extends Observable {

   /** list of instructors to indicate which instructor is displaying in the view */
	protected ArrayList<InstructorFilterObj> instrFil;
    protected InstructorDB iDB;
   
   /**
   * Construct 
   */
   public ViewInstructorFilter(View view, ArrayList<InstructorFilterObj> instructorFilterList) {
       /* create InstructorFilterObj for each instructor	*/
       instrFil = instructorFilterList;

       viewTypeFilterUI = new ViewTypeFilterUI(view, this);
       viewTypeFilterUI.compose();
       addObserver(view);
   }


    /**
     * Sets the instructor to be visible or filtered out.
     */
    public void setInstructorFilter (int instrNum, boolean visible) {
        instrFil.get(instrNum).setSelected(visible);
        setChanged();
        notifyObservers(instrFil.get(instrNum));
    }

    /**
     * Returns the companion view for this class.
     */
    public ViewTypeFilterUI getViewInstructorFilterUI() {
        return viewTypeFilterUI;
    }


   
   /**
   * Get an ArrayList of InstructorFilterObj
   *
   * pre:
   *    none
   * post:
   *    return == instrFil
    *
    */
   public ArrayList<InstructorFilterObj> getInstructorFilterList() {
       return this.instrFil;
   }

    /** The panel of courses to be filtered. */
    protected ViewTypeFilterUI viewTypeFilterUI;
}
