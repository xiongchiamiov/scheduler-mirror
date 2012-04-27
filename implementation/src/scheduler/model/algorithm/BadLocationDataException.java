package scheduler.model.algorithm;

import scheduler.model.Location;


/**
 * Thrown when invalid data is found in an instructor or instructor field while constructing a decorator
 * for it.
 */
public class BadLocationDataException extends Exception
{
   /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

/**
    */
   public enum ConflictType
   {
      IS_TBA, IS_NULL;
      
      public String toString ()
      {
         switch (this)
         {
            case IS_TBA:
               return "TBA Location detected";
            
            case IS_NULL:
                return "Null Location data detected";
            
            default:
               System.err.println ("BAD DATA TYPE");
               return "Unknown error";
         }
      }
   }
   
   /**
    * Our conflict type
    */
   private ConflictType type;
   /**
    * Instructor item that has problems
    */
   private Location loc;
   
   /**
    * String field the field that caused the failure 
    * String result the value that caused it.
    */

   private String field;
   private String result;
   
   /**
    * Constructs an exception for why a given InstructorDecorator couldn't be 
    * created.
    * 
    * @param type The type of conflict this was. 
    * @param ins the Instructor that has problems
    */
   public BadLocationDataException (ConflictType type, Location loc, String field, String result)
   {
      this.type = type;
      this.loc = loc;
      this.field = field;
      this.result = result;
   }

   /**
    * Returns the String explanation of why this exception was thrown.
    * 
    * @return the String explanation of why this exception was thrown.
    *
    * @see java.lang.Throwable#toString()
    */
   public String toString ()
   {
      String r = "";
      
      r += "Location Could Not be Used: " + this.type + "\n\n";
      r += "Invalid value [ " + this.result;
      r += " ] in field [ " + this.field + " ] ";
      
      return r;
   }
   
   /**
    * Returns the si
    * 
    * @return the si
    */
   public Location getLoc () {return loc;}
}