package edu.calpoly.csc.scheduler.model.db.idb;

/**
 * Exception thrown if an instructor selected for generation is not part of the
 * collection of instructors the user selected for consideration in a scedule.
 *
 * @author Eric Liebowitz
 * @version 24feb10
 */
public class InstructorNotInDatabaseException extends Exception
{
   /** Makes javac happy */
   public static final long serialVersionUID = 0;
   /**
    * Calls the exception constructor.
    */
   public InstructorNotInDatabaseException ()
   {
      super ();
   }
}
