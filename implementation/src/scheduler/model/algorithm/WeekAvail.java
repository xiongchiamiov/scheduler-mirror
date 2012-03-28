package scheduler.model.algorithm;

import scheduler.model.Day;

/**
 * Represents a Week's availability. For constant-definitions of the days in 
 * a week, look in generate.Week.java. 
 *
 * This class is used for general availability: you can't have any two things 
 * overlapping. Ever. Thus, the type of things booked is "Void", which will
 * always map to the 0th bit of the BitSet used to determine availability. 
 * The methods here have signature which omit this "Void" parameter, though it
 * is part of the "GenAvail" object. To make up for this (since every booking
 * will be the same Void type), the Void parameter is added here.
 *
 * @author Eric Liebowitz
 * @version 11apr10
 */
public class WeekAvail extends GenWeekAvail<Void, DayAvail>
{
   public static final long serialVersionUID = 42;

   /**
    * Creates a 7-entry array of availability for the day (one entry for every
    * day in the week). The week starts out as completely available.
    */
   public WeekAvail ()/*==>*/
   {
      //7 days
      super ();
      for (int i = 0; i < 7; i ++)
      {
         this.add(new DayAvail ());
      }
   }/*<==*/

   /**
    * Book a given time range over a given day.
    *
    * @param s The start time
    * @param e The end time
    * @param d The day to book.
    *
    * @return True if the time was booked. False otherwise. 
    *
    * @throws NotADayException if "d" is not a valid day as defined in 
    *         generate.Week.java
    */
   public boolean book (boolean b, int s, int e, Day d) /*==>*/
      throws EndBeforeStartException
   {
      return this.book(b, null, s, e, d);
   }/*<==*/

   /**
    * Book a given time range over a given list of days.
    *
    * @param s The start time
    * @param e The end time
    * @param w The days to book
    *
    * @return True if the time was booked. False otherwise. 
    *
    * @throws NotADayException if "d" is not a valid day as defined in 
    *         generate.Week.java
    */
   public boolean book (boolean b, int s, int e, Week w) /*==>*/
      throws EndBeforeStartException
   {
      return this.book(b, null, s, e, w);
   }/*<==*/
   
   public boolean book (boolean b, Week w, TimeRange tr)
   {
      return this.book(b, tr.getS(), tr.getE(), w);
   }
   
   /**
    * Determines whether a given span of time is free for a given day.
    *
    * @param s The start time
    * @param e The end time
    * @param day The day
    *
    * @return True if the time specified is free on the day. False otherwise. 
    *
    * @throws NotADayException if "d" is not a valid day as defined in 
    *         generate.Week.java
    */
   public boolean isFree (int s, int e, Day day) /*==>*/
      throws EndBeforeStartException
   {
      return this.isFree(null, s, e, day);
   }/*<==*/

   /**
    * Determines whether a given span of time is free for a given list of dyas.
    *
    * @param s The start time
    * @param e The end time
    * @param days The list of days
    *
    * @return True if the time specified is free on all days. False otherwise.
    */
   public boolean isFree (int s, int e, Week days) /*==>*/
      throws EndBeforeStartException

   {
      return this.isFree(null, s, e, days);
   }
  
   /**
    * Determines whether a given span of time is free for a given list of dyas.
    *
    * @param tr TimeRange to check over 'days'
    * @param days The list of days
    *
    * @return True if the time specified is free on all days. False otherwise.
    */
   public boolean isFree (TimeRange tr, Week days)
   {
      return this.isFree(tr.getS(), tr.getE(), days);
   }
   /*<==*/
}
