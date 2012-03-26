package edu.calpoly.csc.scheduler.model.algorithm;

import java.util.BitSet;

import edu.calpoly.csc.scheduler.model.Course;

public class CourseDayAvail extends GenAvail<Course>
{
   private static final long serialVersionUID = 42;

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
   public boolean amIReallyFree (Course c, int s, int e, BitSet timeSlot,
                                  BitSet tSlot)
   {
      /*
       * A course can always overlap itself. This is gross, but here's the gist:
       * if this course already booked the slot for this time range, say 
       * that it didn't. If we end up scheduling ourselves into the slot, we'll
       * end up undoing this, so all's well w/ the world. 
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
