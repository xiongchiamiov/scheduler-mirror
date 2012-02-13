package edu.calpoly.csc.scheduler.model.db.cdb;

/**
 * Exception thrown when a given collection of courses is empty. The collection
 * of courses is the only collection not allowed to be empty for generation
 *
 * @author Eric Liebowitz
 * @version 24feb10
 */
public class EmptyCourseDatabaseException extends Exception
{
   /** Makes javac happy */
   public static final long serialVersionUID = 0;

   /**
    * Calls the exception constructor.
    */
   public EmptyCourseDatabaseException ()
   {
      super ();
   }
}
