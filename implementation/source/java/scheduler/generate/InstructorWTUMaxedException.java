package scheduler.generate;

/**
 * Exception thrown when an instructor's wtu count are such that no course can
 * be selected without violating the instructor's wtu limit.
 *
 * @author Eric Liebowitz
 * @version 24feb10
 */
public class InstructorWTUMaxedException extends Exception
{
   /** Makes javac happy */
   public static final long serialVersionUID = 0;

   protected InstructorWTUMaxedException ()
   {
      super ();
   }
}
