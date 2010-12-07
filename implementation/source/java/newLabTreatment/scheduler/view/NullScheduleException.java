package scheduler.view;

/****
 * Exception thrown when the viewSchedule method is called with
 * a null schedule.
 *
 * @author Jason Mak, jamak3@gmail.com
 */
public class NullScheduleException extends Exception {
    
   /** Construct with the super constructor. */
   public NullScheduleException (){
      super ();
   }
}
