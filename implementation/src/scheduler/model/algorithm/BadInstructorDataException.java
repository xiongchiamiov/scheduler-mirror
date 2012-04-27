package scheduler.model.algorithm;

import scheduler.model.Instructor;


/**
 * Thrown when invalid data is found in an instructor or instructor field while constructing a decorator
 * for it.
 */
public class BadInstructorDataException extends Exception
{
   /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

/**
    */
   public enum ConflictType
   {
      IS_STAFF, IS_NULL, NOT_SCHEDULABLE, NULL_I_PREFS, NULL_C_PREFS, NULL_I_PREF_MEM, NULL_C_PREF_MEM;
      
      public String toString ()
      {
         switch (this)
         {
            case IS_STAFF:
               return "Staff Instructor detected";
            case IS_NULL:
                return "Null Instructor";
            case NOT_SCHEDULABLE:
                return "Unschedulable Instructor detected";
            case NULL_I_PREFS:
                return "Null Instructor Preferences";
            case NULL_C_PREFS:
                return "Null Course Preferences";
            case NULL_I_PREF_MEM:
                return "Null Instructor Preference Member";
            case NULL_C_PREF_MEM:
                return "Null Course Preference Member";        
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
   private Instructor ins;
   
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
   public BadInstructorDataException (ConflictType type, Instructor ins, String field, String result)
   {
      this.type = type;
      this.ins = ins;
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
      
      r += "Instructor Could Not be Used: " + this.type + "\n\n";
      r += "Invalid value [ " + this.result;
      r += " ] in field [ " + this.field + " ] ";
      
      return r;
   }
   
   /**
    * Returns the si
    * 
    * @return the si
    */
   public Instructor getIns () {return ins;}
}
