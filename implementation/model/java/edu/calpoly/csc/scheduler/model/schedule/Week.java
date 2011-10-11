package edu.calpoly.csc.scheduler.model.schedule;

import java.util.Collection;
import java.util.Vector;
import java.io.Serializable;

/**
 * Represents a selection of days of the week. No duplicate days are allowed.
 * (No weeks w/ more than 7 days). 
 *
 * @author Eric Liebowitz
 * @version 13apr10
 */
public class Week implements Serializable
{
   /**
    * For serializing
    */
   public static final int serialVersionUID = 42;

   /** A 5-day week (Mon-Fri) */
   public static final Week fiveDayWeek = 
      new Week (new Day[]{Day.MON, Day.TUE, Day.WED, Day.THU, Day.FRI});

   private Vector<Day> days = new Vector<Day>();
   
   /**
    * Creates a Week w/ no days in it
    */
   public Week ()
   {
      super ();
   }

   public Week (Day[] days)
   {

   }

   public boolean add (Day d)
   {
      boolean r = false;
      if (!this.contains(d))
      {
         r = this.days.add(d);
      }
      return r;
   }
   
   /**
    * Removes the given day from this week.
    * 
    * @param d Day to remove
    * @return true if the day was removed. False if it was not (most likely b/c
    *         it didn't exist in this week).
    */
   public boolean del (Day d)
   {
      return days.remove(d);
   }

   /**
    * Blindly removes the last element of this Week. If none exist, nothing is
    * done.
    *
    * @return True if an element was removed. False otherwise. 
    */
   public boolean chop ()
   {
      boolean r = false;
      if (this.days.size() > 0)
      {
         /*
          * "size" isn't 0-indexed, so I have to adjust
          */
         this.days.remove(this.days.size() - 1);
         r = true;
      }
      return r;
   }

   /**
    * Returns a String representing this object
    */
   public String toString ()
   {
      String r = new String();
      for (Day d: this.days)
      {
         r += d.toString() + " ";
      }
      return r;
   }

   /**
    * Returns a list representation of this Week's days, in order from 
    * Sunday to Saturday.
    *
    * @return a list (Vector) of Integers represeting that days in this Week
    */
   public Vector<Day> getDays ()
   {
      return this.days;
   }

   /**
    * Determines whether this week contains the specified day
    * 
    * @param d Day to check for
    * 
    * @return true if this week contains the given day. False otherwise. 
    */
   public boolean contains (Day d)
   {
      return this.days.contains(d);
   }
   
   /** 
    * Determines whether this Week shares any days with a given one.
    *
    * @param w The week to check for overlap
    *
    * @return true if this Week contains any of the days in "w". False 
    *         othwerise.
    */
   public boolean overlaps (Week w)
   {
      boolean r = false;
      
      for (Day d: w.getDays())
      {
         r |= this.days.contains(d);
      }

      return r;
   }

   /**
    * Tells you if two Weeks are equal (have the same days).
    * 
    * @param w The Week to test for equality.
    *
    * @return True if two weeks have the same days. False otherwise. 
    */
   public boolean equals (Week w)
   {
      return this.getDays().equals(w.getDays());
   }

   /**
    * Simple hash code comprised on the values of each day in this Week. It
    * is guaranteed that a Week which returns true from "equals" will have the
    * same hash code.
    *
    * @return the hash code on the return from this object's toString
    *
    */
   public int hashCode()
   {
      return this.toString().hashCode();
   }
}
