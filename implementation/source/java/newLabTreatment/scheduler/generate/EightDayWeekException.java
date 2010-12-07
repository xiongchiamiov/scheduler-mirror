package scheduler.generate;

/**
 * Thrown when too many days are added to a week.
 *
 * @author Eric Liebowitz
 * @version 15apr10
 */
public class EightDayWeekException extends RuntimeException
{
   public EightDayWeekException ()
   {
      super ();
   }
}
