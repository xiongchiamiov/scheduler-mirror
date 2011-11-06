package edu.calpoly.csc.scheduler.model.schedule;

import java.util.TreeMap;
import java.util.List;
import java.util.Vector;

/**
 * A sorted map of ScheduleItems which'll make it easy to find the SI which is
 * most valuable to a particular instructor. This class was written to provide
 * a few convenience methods to make the sorting process easier/more 
 * modularized.
 *
 * You'll notice that this has "ScheduleItem" keys and the odd "Void" value for
 * each of said keys. This class isn't concerned with the keys...only with 
 * sorting those ScheduleItems, so it doesn't really matter what value I use. 
 * Yes, this is a bit wasteful, but it seemed the easiest way to get the SI's
 * sorted. Furthermore, should I ever need to supply more information to this
 * object, I can use the values if need be. 
 *
 * @author Eric Liebowitz
 * @version 12nov10
 */
public class SiMap extends TreeMap<ScheduleItem, Void>
{
   private static final long serialVersionUID = -8623034875284606343L;
   
   /**
    * Used for debugging. Toggle it to get debugging output
    */
   public static final boolean DEBUG = !true;
   /**
    * Prints a message if DEBUG is true
    * 
    * @param s String to print
    */
   public static void debug (String s)
   {
      if (DEBUG)
      {
         System.err.println (s);
      }
   }
   
   public SiMap () { }
   
   /**
    * Creates a new sorted mapping and adds each ScheduleItem in the list 
    * provided as keys in the map. 
    *
    * @param sis List of all ScheduleItems to add to the map as keys and sort
    */
   public SiMap (Vector<ScheduleItem> sis)
   {
      super();
      for (ScheduleItem si: sis)
      {
         this.put(si);
      }
   }

   /**
    * Puts a provided ScheduleItem in the map, so long as its "getValue" method
    * does not return ScheduleItem.IMPOSSIBLE, as that means that the SI is 
    * not able to be taught by its instructor
    *
    * @param si The ScheduleItem to add
    *
    * @return true if "si" was added. False if its value is 
    *         ScheduleItem.IMPOSSIBLE.
    * 
    * @see ScheduleItem#getValue()
    */
   public boolean put (ScheduleItem si)
   {
      if (si.getValue() == ScheduleItem.IMPOSSIBLE)
      {
         return false;
      }
      super.put(si, null);
      return true;
   }
   
   /**
    * Puts all elements in the given list into this map. 
    * 
    * @param list List of ScheduleItems to add to this map
    * 
    * @return true if all items were added. False if at least one was not
    */
   public boolean putAll (List<ScheduleItem> list)
   {
      boolean r = true;
      for (ScheduleItem si: list)
      {
         r &= this.put(si);
      }
      return r;
   }
   
   /**
    * Returns the "best" ScheduleItem in this map, where the "best" ScheduleItem
    * is the one whose value is at least as high as all the others in this map.
    * 
    * @return this.lastKey();
    */
   public ScheduleItem getBest ()
   {
      return this.lastKey();
   }
}
