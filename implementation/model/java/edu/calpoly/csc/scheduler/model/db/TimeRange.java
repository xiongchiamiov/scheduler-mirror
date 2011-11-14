package edu.calpoly.csc.scheduler.model.db;

import java.io.Serializable;
import java.util.Vector;

public class TimeRange implements Serializable
{
   /**
    * 
    */
   private static final long serialVersionUID = -7492152557062000805L;
   /** Start time*/
   private Time s;
   /** End time */
   private Time e;

   /**
    * Constructor to create a range from a start and end time.
    * @param s The start time
    * @param e The end time
    */
   public TimeRange (Time s, Time e)
   {
      if (s.compareTo(e) != -1)
      {
         throw new EndBeforeStartException();
      }
      this.s = new Time(s);
      this.e = new Time(e);
   }

   public TimeRange (TimeRange tr)
   {
      this (tr.getS(), tr.getE());
   }
   
   /**
    * Creates a time range given a start time and a length, in half hours
    * 
    * @param t Start time of the range
    * @param length Number of half hours after the start time that the end time
    *        should be at
    */
   public TimeRange (Time t, int length)
   {
      Time temp = new Time(t);
      
      this.s = new Time(t);
      
      temp.addHalves(length);
      this.e = temp;
   }
   
   /**
    * Returns the half hour length.
    * @return The half hour length.
    */
   public int getHalfHourLength ()
   {
      int halfHours = ((e.getHour() - s.getHour()) * 2);
      if (e.getMinute() > s.getMinute())
      {
         halfHours ++;
      }
      if (e.getMinute() < s.getMinute())
      {
         halfHours --;
      }

      return halfHours;
   }

   /**
    * Returns whether there is overlap between this range and another range.
    * @param tr The range
    * @return true or false depending on whether they overlap.
    */
   public boolean overlaps (TimeRange tr)
   {
      /*
       * If our start is between the other's start and end, they must overlap.
       * (The start must be >= the other's start and < the other's end)
       */
      if ((this.s.compareTo(tr.getS()) > -1) && 
          (this.s.compareTo(tr.getE()) < 0))
      {
         return true;
      }
      /*
       * If our end is between the other's start and end, they must overlap.
       * (The end must be <= the other's end and > the other's start)
       */
      if ((this.e.compareTo(tr.getS()) > 0) &&
          (this.e.compareTo(tr.getE()) < 1))
      {
         return true;
      }
      /*
       * If our bounds are beyond those of the other's (our start is less than 
       * its start and our end is greater than its end), they must overlap
       */
      if ((this.s.compareTo(tr.getS()) < 0) &&
          (this.e.compareTo(tr.getE()) > 0))
      {
         return true;
      }

      /*
       * If none of the above have, they must -not- overlap
       */
      return false;
   }

   /**
    * Returns true if this TimeRange contains the given TimeRange. 
    * 
    * @param tr TimeRange which we want to see if this one contains
    * 
    * @return True if this TimeRange contains the given one. In particular, 
    *         our start time is before its start time, and our end time if 
    *         after its end time. <b>This is not the same as overlapping!</b>
    */
   public boolean contains (TimeRange tr)
   {  
      return (this.getS().compareTo(tr.getS()) > 0 &&
              this.getE().compareTo(tr.getE()) < 0);
   }
   
   public boolean addHalf ()
   {
      return this.getS().addHalf() && this.getE().addHalf();
   }
   
   /** Returns the start time
    *  @return The start time 
    */
   public Time getS () { return this.s; }
   
   /** Returns the end time 
    *  @return The start time 
    */
   public Time getE () { return this.e; }

   /** Overrides the default toString methor 
    *  @return The range as a string
    */
   public String toString ()
   {
      return this.s + " to " + this.e;
   }
   
   public boolean equals (TimeRange tr)
   {
      return this.s.equals(tr.getS()) && this.e.equals(tr.getE());
   }
}
