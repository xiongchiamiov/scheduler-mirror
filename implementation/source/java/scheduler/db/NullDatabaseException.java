package scheduler.db;



/**
 * This exception is thrown if there is a problem with
 * the database
 * 
 * @author Jan Lorenz Soliman
 **/
public class NullDatabaseException extends Exception
{
   /** Makes javac happy*/
   public static final long serialVersionUID = 0;
   
   /**
    *  Constructor calls Exception constructor.
    *
    */
   protected NullDatabaseException ()
   {
      super ();
   }
}
