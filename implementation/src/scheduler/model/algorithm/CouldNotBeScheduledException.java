package scheduler.model.algorithm;


/**
 * Thrown when the Scheduler is unable to find a single day-time combination 
 * (DaysAndTime object) for a given course. 
 *
 * @author Eric Liebowitz
 * @version 26jun10
 */
public class CouldNotBeScheduledException extends Exception
{
   /**
    * Represents the types of conflicts that can happen in schedule creation.
    *
    * @author Eric Liebowitz
    * @version Nov 10, 2011
    */
   public enum ConflictType
   {
      I_DBL_BK, L_DBL_BK, NO_DESIRE, CANNOT_TEACH, NO_SECTIONS_LEFT;
      
      public String toString ()
      {
         switch (this)
         {
            case I_DBL_BK:
               return "Instructor already teaching at this time";
            case L_DBL_BK:
               return "Location already in use at this time";
            case NO_DESIRE:
               return "Instructor has no desire to teach the course";
            case CANNOT_TEACH:
               return "Instructor cannot teach this course";
            case NO_SECTIONS_LEFT:
               return "No more sections of the course can be scheduled";
            default:
               System.err.println ("BAD CONFLICT TYPE");
               return "Unknown error";
         }
      }
   }
   
   private static final long serialVersionUID = -6621453458031832605L;
   
   /**
    * Our conflict type
    */
   private ConflictType type;
   /**
    * ScheduleItem which made the conflict
    */
   private ScheduleItemDecorator si;

   /**
    * Constructs an exception for why a given ScheduleItem couldn't be 
    * scheduled.
    * 
    * @param type The type of conflict this was. 
    * @param si The ScheduleItem which made the conflict
    */
   public CouldNotBeScheduledException (ConflictType type, ScheduleItemDecorator si)
   {
      this.type = type;
      this.si = si;
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
      
      r += "Could Not be Scheduled: " + this.type + "\n\n";
//      r += "Instructor: " + this.si.item.getInstructor().getID() + "\n";
      r += "Time:       " + ((float)this.si.getItem().getStartHalfHour()) / 2 + " to " + ((float)this.si.getItem().getEndHalfHour()) / 2 +  "\n";
      r += "Days:       " + this.si.getItem().getDays() + "\n";
//      r += "Location:   " + this.si.item.getLocation().getID() + "\n";
      
      return r;
   }
   
   /**
    * Returns the si
    * 
    * @return the si
    */
   public ScheduleItemDecorator getSi ()
   {
      return si;
   }
}
