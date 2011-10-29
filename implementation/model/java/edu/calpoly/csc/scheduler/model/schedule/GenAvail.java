package edu.calpoly.csc.scheduler.model.schedule;

import java.util.BitSet;
import java.util.Vector;
import java.io.Serializable;

import edu.calpoly.csc.scheduler.model.db.Time;


public abstract class GenAvail<T> extends Vector<BitSet> 
                                   implements Serializable
{
   public static final long serialVersionUID = 42;

   /**
    * Constructs a single day of availability, with 48 BitSets to represent the
    * 48 half-hours in a day.
    */
   public GenAvail ()//==>
   {
      //Need 48 BitSets, one for every half hour of the day
      super ();
      for (int i = 0; i < 48; i ++)
      {
         this.add(new BitSet());
      }
   }//<==

   /**
    * Books a given thing over a given span of time.
    *
    * @param t The the to be booked. This object must provide a "getBit" method
    *          which returns the bit which represents it in a BitSet.
    * @param s The start time
    * @param e The end time
    *
    * @return true if the booking was made. False otherwise. 
    */
   public boolean book (boolean b, T t, Time s, Time e)//==>
   {
      /*
       * If they want to try and book, see if they can
       */
      if (b)
      {
         if (!isFree(t, s, e)) { return false; }
      }
      
      /*
       * Times will have been rounded in "isFree"
       */
      int slots = computeSlots(s, e);
      int baseSlot = computeStartSlot(s);
      int bit = getBit(t);

      for (int i = 0; i < slots; i ++)
      {
         this.get(baseSlot + i).set(bit, b);
      }
      return true;
   }//<==

   /**
    * Checks whether its is possible to book something within a given time 
    * range. This method simply does the prep work of checking: the real method
    * which makes this decision is "amIReallyFree", which must be defined by 
    * extending children. 
    * 
    * @param t The thing that would otherwise be booked. This object must 
    *          provide a "getBit" method which returns the bit to represent it
    *          in the BitSet.
    * @param s The start time
    * @param e The end time
    * 
    * @return true if the booking could be made. False otherwise.
    */
   public boolean isFree (T t, Time s, Time e)//==>
   {
      roundTimeDown(s);
      roundTimeUp(e);
      int slots = computeSlots (s, e);
      int baseSlot = computeStartSlot(s);

      BitSet timeSlot = new BitSet ();
      for (int i = 0; i < slots; i ++)
      {
         timeSlot.or(this.get(baseSlot + i));
      }

      BitSet tSlot = new BitSet();
      tSlot.set(getBit(t), true);

      return this.amIReallyFree (t, s, e, timeSlot, tSlot);
   }//<==

   /**
    * Makes the decision as to whether or not a given span of time can allow 
    * for a given object to be booked. Must be overridden by child classes. 
    *
    * @param t The thing to be booked. This object must provide a "getBit" 
    *          method which returns the bit to represent it in the BitSet.
    * @param s The start time
    * @param e The end time
    * @param timeSlot BitSet representing all the already-booked bits in the 
    *                 given time range
    * @param tSlot BitSet with the single bit of the "t" object asserted
    *
    * @return true if a booking could be made over this given time range. False
    *         otherwise.
    */
   public abstract boolean amIReallyFree (T t, Time s, Time e, BitSet timeSlot, 
                                  BitSet tSlot);

   /**
    * Truncates a time down to the previous half-hour. (7:55 becomes 7:30, 
    * 7:29 become 7:00, etc).
    *
    * @param t The time to round down.
    */
   public void roundTimeDown (Time t)//==>
   {
      t.setMinute((t.getMinute() < 30) ? 0 : 30);
   }//<==

   /**
    * Rounds time up to the next half-hour. (7:01 becomes 7:30, 7:31 becomes 
    * 8:00, etc.).
    *
    * @param t Time to round up
    */
   public void roundTimeUp (Time t)//==>
   {
      int extraHour = (t.getMinute() < 31) ? 0 : 1;
      t.setHour(t.getHour() + extraHour);

      int min = t.getMinute();
      t.setMinute((min < 31 && min > 0) ? 30 : 0);
   }//<==

   /**
    * Computes the number of half hours there in a given time range. These half
    * hours are given based on the times after they have been rounded down 
    * (start) and down (end) accordingly. So, if you wanted the number of half 
    * hours gave it the times 8:15 and 10:45, you'd get 6.
    *
    * @param s The start time
    * @param e The end range
    *
    * @return the number of half hours it would take to go from "s" (rounded 
    *         down according to the criteria of "roundTimeDown") to "e" 
    *         (rounded up according to the criteria of "roundTimeUp").
    */
   protected int computeSlots (Time s, Time e)//==>
   {
      int slots = (e.getHour() - s.getHour()) * 2;
      slots += ((e.getMinute() - s.getMinute()) / 30);
      return slots;
   }//<==

   /**
    * Determines which of the 48 half-hour slots a given time will begin on
    *
    * @param s The time in question
    *
    * @return The half-hour slot "s" will start on
    */
   protected int computeStartSlot (Time s)//==>
   {
      return ((s.getHour() * 2) + (s.getMinute() / 30));
   }//<==

   /**
    * Determines how you want the objects represented in this object to look 
    * like when they're in the BitSet. 
    *
    * @param t The thing which is being represented as a bit
    */
   public abstract int getBit (T t);
}
