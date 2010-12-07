package scheduler.db;

import java.io.Serializable;

public class TimeRange implements Serializable
{
   private Time s;
   private Time e;

   public TimeRange (Time s, Time e)
   {
      if (s.compareTo(e) != -1)
      {
         throw new EndBeforeStartException();
      }
      this.s = s;
      this.e = e;
   }

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

   public boolean overlaps (TimeRange tr)
   {
      /*
       * If our start is between the other's start and end, they must overlap.
       * (The start must be >= the other's start and the other's end)
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

   public Time getS () { return this.s; }
   public Time getE () { return this.e; }

   public String toString ()
   {
      return "Start: " + this.s + "\nEnd  : " + this.e;
   }
}
