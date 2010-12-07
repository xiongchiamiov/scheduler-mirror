package scheduler.menu.schedule.allInOne;

import scheduler.db.locationdb.*;

import java.util.Vector;
import javax.swing.*;

/**
 * Represents a checkbox list of the locations in a location database. Note that
 * the locations displayed represent those in the -local- database, not the 
 * remotely-global one.
 *
 * @author Eric Liebowitz
 * @version 23jun10
 */
public class LocationList extends GenList<Location>
{
   /**
    * Location data from whence this list will grab its data to display.
    */
   private LocationDB ldb;

   /**
    * Creates a list of locations. Column headers are "Bldg/Room". 
    *
    * @param axis Direction the contents of the box are to go (hor. or vert)
    * @param ldb LocationDb you want backing this list
    */
   public LocationList (int axis, LocationDB ldb)
   {
      super (axis, new Vector<String>(), "Locations: ");
      this.ldb = ldb;

      Vector<String> colData = new Vector<String>();
      colData.add(" ");
      colData.add("Bldg/Room");
      this.table.getModel().setColumnIdentifiers(colData);

      refresh();
   }

   /**
    * Populates the list of locations. For each location, its building and room
    * number are displayed. 
    */
   protected void populate ()
   {
      for (Location l: ldb.getLocalData())
      {
         Vector<Object> row = new Vector<Object>();

         row.add(this.selections.contains(l));
         row.add(l);

         this.table.getModel().addRow(row);
      }
   }

   /**
    * What should go into the "selections" list: a deep copy of hte Location.
    *
    * @param l The Location to copy
    * 
    * @return A deep copy "l"
    */
   protected Location copy (Location l) { return new Location(l); }
}
