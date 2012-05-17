package scheduler.model.algorithm;

import scheduler.model.Course;


/**
 * Thrown when invalid data is found in an instructor or instructor field while constructing a decorator
 * for it.
 */
public class BadCourseDataException extends Exception
{
   /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

/**
    */
   public enum ConflictType {
      IS_NULL, NOT_SCHEDULABLE, NULL_DPTMT, BAD_CAT_NUM, NULL_CNAME, BAD_NUMSECTS, BAD_NUMWTU, BAD_NUMSCU, BAD_HHR_WEEK,
      BAD_MAXENR, BAD_CTYPE, NULL_DOC, DONT_CARE, TETHERED_NULL_LECT;
      
      public String toString ()
      {
         switch (this)
         {
            case IS_NULL:
                return "Null Instructor";
            case NOT_SCHEDULABLE:
                return "Unschedulable Instructor detected";
            case NULL_DPTMT: 
            	return "Null Department Field";
            case BAD_CAT_NUM: 
            	return "Bad Catalog Number Field";
            case NULL_CNAME: 
            	return "NULL Course Name Field";
            case BAD_NUMSECTS:
                return "Number sections null or < 0";
            case BAD_NUMWTU:
                return "Number WTU null or < 0";
            case BAD_NUMSCU:
                return "Number SCU null or < 0";
            case BAD_HHR_WEEK:
                return "Number Half-Hours per week null or < 0";
            case BAD_MAXENR:
                return "Number Max Enrollment null or < 0";
            case BAD_CTYPE:
            	return "Course Type null or empty";
            case TETHERED_NULL_LECT:
            	return "Tethered with null lecture course or lecture course ID"
            case DONT_CARE:
            	return "Choose For Me detected";
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
   private Course course;
   
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
   public BadCourseDataException (ConflictType type, Course course, String field, String result)
   {
      this.type = type;
      this.course = course;
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
      
      r += "Course Could Not be Used: " + this.type + "\n\n";
      r += "Invalid value [ " + this.result;
      r += " ] in field [ " + this.field + " ] ";
      
      return r;
   }
   
   /**
    * Returns the si
    * 
    * @return the si
    */
   public Course getCourse () {return course;}
}
