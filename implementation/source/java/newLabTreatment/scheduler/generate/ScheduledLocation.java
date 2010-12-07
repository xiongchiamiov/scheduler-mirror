package scheduler.generate;

import scheduler.db.locationdb.Location;

/**
 * Represents a Location which has been assigned a DaysAndTime object.
 *
 * @author Eric Liebowitz
 * @version 19apr10
 */
public class ScheduledLocation
{
   /** Represents a location which could not be given a time to be occupied */
   public static ScheduledLocation TBA; 
      //new ScheduledLocation (Location.TBA, DaysAndTime.TBA);

   
   /**
    * The location
    */
   protected Location l;
   /**
    * The day and time the location was assigned
    */
   protected DaysAndTime dat;

   /**
    * Creates an object which will represent a location which is occupied at a 
    * certain time over a given set of days. 
    *
    * @param l The location
    * @param dat The day and time
    */
   public ScheduledLocation (Location l, DaysAndTime dat)
   {
      this.l = l;
      this.dat = dat;
   }

   /**
    * Returns a string representing the object's data
    */
   public String toString ()
   {
      return "" + this.l + this.dat;
   }
}
