package edu.calpoly.csc.scheduler.model.db;

/**
 *
 * @author Eric Liebowitz
 * @version Oct 22, 2011
 */
public abstract class DbData
{
   private Integer scheduleId;
   
   /**
    * Returns the scheduleId
    * 
    * @return the scheduleId
    */
   public Integer getScheduleId ()
   {
      return scheduleId;
   }

   /**
    * Sets the scheduleId to the given parameter.
    *
    * @param scheduleId the scheduleId to set
    */
   public void setScheduleId (int scheduleId)
   {
      this.scheduleId = scheduleId;
   }

   public abstract void verify () throws NullDataException;


}
