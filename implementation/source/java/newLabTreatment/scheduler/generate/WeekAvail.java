package scheduler.generate;

import scheduler.*;
import scheduler.db.Time;
import java.util.Vector;

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
   public static final int serialVersionUID = 42;

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
   public boolean book (Time s, Time e, int d) /*==>*/
      throws EndBeforeStartException,
             NotADayException
   {
      return this.book(null, s, e, d);
   }/*<==*/

   /**
    * Book a given time range over a given list of days.
    *
    * @param s The start time
    * @param e The end time
    * @param d The days to book (0 = Sun; 6 = Sat)
    *
    * @return True if the time was booked. False otherwise. 
    *
    * @throws NotADayException if "d" is not a valid day as defined in 
    *         generate.Week.java
    */
   public boolean book (Time s, Time e, Vector<Integer> d) /*==>*/
      throws EndBeforeStartException,
             NotADayException
   {
      return this.book(null, s, e, d);
   }/*<==*/
   
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
   public boolean isFree (Time s, Time e, int day) /*==>*/
      throws EndBeforeStartException,
             NotADayException
   {
      return this.isFree(null, s, e, day);
   }/*<==*/

   /**
    * Determines whether a given span of time is free for a given list of dyas.
    *
    * @param s The start time
    * @param e The end time
    * @param d The list of days
    *
    * @return True if the time specified is free on all days. False otherwise.
    *
    * @throws NotADayException if "d" is not a valid day as defined in 
    *         generate.Week.java
    */
   public boolean isFree (Time s, Time e, Vector<Integer> d) /*==>*/
      throws EndBeforeStartException,
             NotADayException

   {
      return this.isFree(null, s, e, d);
   }/*<==*/
}
