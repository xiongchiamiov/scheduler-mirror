package edu.calpoly.csc.scheduler.view.desktop.old_view;


/**
 * Exception to be thrown when a null Day is passed to 
 * isDaySelected() or setDay() in DaysInWeek method
 *
 * @author Sasiluk Ruangrongsorakai (sruangro@calpoly.edu)
 */
public class NullDayException extends Exception
{
   protected NullDayException ()
   {
      super ();
   }
}
