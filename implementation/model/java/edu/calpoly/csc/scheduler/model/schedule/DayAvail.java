package edu.calpoly.csc.scheduler.model.schedule;


import java.util.BitSet;
import java.util.Vector;
import java.io.Serializable;

import edu.calpoly.csc.scheduler.model.db.Time;

/**
 * Represents a Day's availability.
 *
 * @author Eric Liebowitz
 * @version 14mar10
 */
public class DayAvail extends GenAvail<Void> implements Serializable
{
   public static final int serialVersionUID = 42;

   public DayAvail ()/*==>*/
   {
      super();

   }/*<==*/

   /**
    * Provided to support the old version of the DayAvail object's signature, 
    * which is used in other places. See documenation of "GenAvail" for details
    * as to what this method does. 
    */
   public boolean book (Time s, Time e)/*==>*/
   {
      return this.book(null, s, e);
   }/*<==*/

   /**
    * Provided to support the old version of the DayAvail object's signature, 
    * which is used in other places. See documenation of "GenAvail" for details
    * as to what this method does. 
    */
   public boolean isFree (Time s, Time e)/*==>*/
   {
      return this.isFree(null, s, e);
   }/*<==*/

   /**
    * Returns true if nothing else is booked in the given time range. Sounds
    * simple, and it is. 
    *
    * @param v "Thing" to book. Since availability in this object is a very 
    *          binary thing, this type doesn't matter much.
    * @param s The start time
    * @param e The end time
    * @param timeSlot BitSet of all bits already booked between "s" and "e"
    * @param tSlot BitSet containing the bit representation of "v" (0)
    *
    * @return !timeSlot.intersects(tSlot)
    */
   public boolean amIReallyFree (Void v, Time s, Time e, BitSet timeSlot, /*==>*/
                                 BitSet tSlot)
   {
      return !timeSlot.intersects(tSlot);
   }/*<==*/

   /**
    * Returns the bit a given "Void" is to represent in the BitSet. As this
    * object represents singular, binary availability (you're either available,
    * or you aren't, w/o regard for anything else), this bit can be a single, 
    * constant value. 
    *
    * @param v Nothing. Really, it's literally nothing. 
    *
    * @return 0
    */
   public int getBit (Void v)/*==>*/
   {
      return 0;
   }/*<==*/

   /**
    * Simple way to print out the availability of each time slot in the day.
    */
   public String toString()//==>
   {
      String r = "";
      for (int i = 0; i < this.size(); i ++)
      {
         r += i + ": " + this.get(i) + "\n";
      }
      return r;
   }//<==
}

