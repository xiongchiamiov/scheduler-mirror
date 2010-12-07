package scheduler.generate;

/**
 * Exception thrown when an instructor's pref are such that no course could
 * be selected which the instrutor is qualified to teach. Thus, there is no 
 * available course for which the instructor has specified a preference
 * > 0.
 *
 * @author Eric Liebowitz
 * @version 24feb10
 */
public class InstructorCanTeachNothingException extends Exception
{
   /** Makes javac happy */
   public static final long serialVersionUID = 0;

   protected InstructorCanTeachNothingException ()
   {
      super ();
   }
}
