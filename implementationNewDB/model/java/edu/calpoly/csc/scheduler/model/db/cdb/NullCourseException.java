package edu.calpoly.csc.scheduler.model.db.cdb;


/**
 * Exception to be thrown when a null course occurs. 
 *
 * @author Jan Lorenz Soliman
 */
public class NullCourseException extends Exception
{

   /**
    * 
    */
   private static final long serialVersionUID = -3042849948466801486L;

   /**
    * Calls the exception constructor.
    */
   protected NullCourseException ()
   {
      super ();
   }
}
