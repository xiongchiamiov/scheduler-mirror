package edu.calpoly.csc.scheduler.model.schedule;

import java.io.Serializable;
import java.util.Vector;

import edu.calpoly.csc.scheduler.model.db.Time;


public class GenWeekAvail<T, U extends GenAvail<T>> extends Vector<U> 
                                                    implements Serializable
{
   private static final long serialVersionUID = 42;

   /**
    * Basic constructor that calls "super()". This will <b>not</b> initialize 
    * the 7 elements of the Week. As far as I can tell, this is b/c Java can't
    * know enough about the objects to instantiate them here.
    */
   public GenWeekAvail ()//==>
   {
      super ();
   }//<==

   /**
    * Book a given time range over a given day.
    *
    * @param t The thing for which to book
    * @param s The start time
    * @param e The end time
    * @param d The day to book.
    *
    * @return True if the time was booked. False otherwise. 
    *
    * @throws NotADayException if "d" is not a valid day as defined in 
    *         generate.Week.java
    */
   public boolean book (T t, Time s, Time e, Day d) /*==>*/
      throws EndBeforeStartException
   {
      return this.get(d.getNum()).book(t, s, e);
   }/*<==*/

   /**
    * Book a given time range over a given list of days.
    *
    * @param t The thing for which to book
    * @param s The start time
    * @param e The end time
    * @param d The days to book (0 = Sun; 6 = Sat)
    *
    * @return True if the time was booked. False otherwise. 
    *
    * @throws NotADayException if "d" is not a valid day as defined in 
    *         generate.Week.java
    */
   public boolean book (T t, Time s, Time e, Week days) /*==>*/
      throws EndBeforeStartException
   {
      boolean r = true;
      for (Day d: days.getDays())
      {
         r &= this.get(d.getNum()).book(t, s, e);
      }
      return r;
   }/*<==*/

   /**
    * Determines whether a given span of time is free for a given day.
    *
    * @param t The thign for which to book
    * @param s The start time
    * @param e The end time
    * @param day The day
    *
    * @return True if the time specified is free on the day. False otherwise. 
    *
    * @throws NotADayException if "d" is not a valid day as defined in 
    *         generate.Week.java
    */
   public boolean isFree (T t, Time s, Time e, Day day) /*==>*/
      throws EndBeforeStartException
   {
      return this.get(day.getNum()).isFree(t, s, e);
   }/*<==*/

   /**
    * Determines whether a given span of time is free for a given list of dyas.
    *
    * @param t The thing for which to book
    * @param s The start time
    * @param e The end time
    * @param d The list of days
    *
    * @return True if the time specified is free on all days. False otherwise.
    *
    * @throws NotADayException if "d" is not a valid day as defined in 
    *         generate.Week.java
    */
   public boolean isFree (T t, Time s, Time e, Week days) /*==>*/
      throws EndBeforeStartException
   {
      boolean free = true;
      for (Day d: days.getDays())
      {
         free &= this.get(d.getNum()).isFree(t, s, e);
      }
      return free;
   }/*<==*/
}
