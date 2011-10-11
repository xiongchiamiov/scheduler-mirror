package edu.calpoly.csc.scheduler.model.schedule;

import java.util.BitSet;
import java.util.Vector;
import java.io.Serializable;

import edu.calpoly.csc.scheduler.model.db.Time;
import edu.calpoly.csc.scheduler.model.db.cdb.Course;
import edu.calpoly.csc.scheduler.model.db.pdb.NoClassOverlap;

import edu.calpoly.csc.scheduler.model.db.cdb.*;

public class CourseDayAvail extends GenAvail<Course>
{
   private static final int serialVersionUID = 42;

   private BitSet ncoBitSet;

   public CourseDayAvail (NoClassOverlap nco)
   {
      super ();
      ncoBitSet = nco.getBitSet();
   }

   /**
    * Removes the Course being checked from consideration before checking 
    * whether it overlaps w/ any other Courses. A Course should never be 
    * prevented from being booked b/c it overlaps itself. This method will 
    * see whether a provided BitSet of the Courses taught for a span of time
    * intersects with the BitSet representation of the NCO associated with this
    * object.
    *
    * @param c The course not allowed to overlap anything (except itself)
    * @param s Start time of the time range
    * @param e End time of the end range
    * @param timeSlot The fully OR'd BitSet of other Course bits already booked
    *                 over the given time range
    * @param tSlot The BitSet containing the asserted bit for the Course "c". 
    *              Not currently needed.
    *
    * @return True if the Course doesn't overlap anything it should. False 
    *         otherwise.
    */
   public boolean amIReallyFree (Course c, Time s, Time e, BitSet timeSlot,
                                  BitSet tSlot)
   {
      /*
       * A course can always overlap itself
       */
      timeSlot.set(getBit(c), false);
      if (!timeSlot.isEmpty())
      {
         System.err.println ("==========HERE==========");
         System.err.println (s + " - " + e);
         System.err.println ("Booked: " + timeSlot);
         System.err.println ("vs");
         System.err.println ("Filter: " + this.ncoBitSet);
         System.err.println ("Can book: " + !timeSlot.intersects(this.ncoBitSet));
      }
      return !timeSlot.intersects(this.ncoBitSet);
   }
   
   public void setNCOBitSet (BitSet bitSet)
   {
      this.ncoBitSet = bitSet;
   }

   public int getBit (Course c)
   {
      return c.hashCode();
   }
}
