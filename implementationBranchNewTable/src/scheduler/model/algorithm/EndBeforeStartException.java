package scheduler.model.algorithm;

/**
 * Thrown when a start time is after an end time, or vice versa
 *
 * @author Eric Liebowitz
 * @version 06jun10
 */
public class EndBeforeStartException extends RuntimeException
{
   /** Makes javac happy */
   public static final long serialVersionUID = 0;

   public EndBeforeStartException ()
   {
      super ();
   }
}
