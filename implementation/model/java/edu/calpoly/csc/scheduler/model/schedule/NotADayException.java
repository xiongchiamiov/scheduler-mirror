package edu.calpoly.csc.scheduler.model.schedule;

/**
 * <pre>
 * Thrown when an invalid day is given.
 *
 * Days are represented with integers. Valid day-ints are:
 *
 *    0 = Sun
 *    1 = Mon
 *    2 = Tue
 *    3 = Wed
 *    4 = Thu
 *    5 = Fri
 *    6 = Sat
 * </pre>
 *
 * @author Eric Liebowitz
 * @version 12arp10
 */
public class NotADayException extends RuntimeException
{
   public NotADayException ()
   {
      super ();
   }
}
