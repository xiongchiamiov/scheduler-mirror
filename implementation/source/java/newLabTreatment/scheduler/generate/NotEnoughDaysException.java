package scheduler.generate;

/**
 * Thrown when a number of days are requested from a Week which does not have
 * enough days in it to satisfay the request. 
 *
 * @author Eric Liebowitz
 * @version 24jun10
 */
public class NotEnoughDaysException extends Exception
{
   public NotEnoughDaysException ()
   {
      super ();
   }
}
