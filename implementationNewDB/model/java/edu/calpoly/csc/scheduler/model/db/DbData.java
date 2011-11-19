package edu.calpoly.csc.scheduler.model.db;

/**
 * 
 * @author Eric Liebowitz
 * @version Oct 22, 2011
 */
public abstract class DbData
{
   public static final String SCHEDULEID = "scheduleid";
   public static final String NOTE       = "note";

   private Integer            scheduleId;
   private String             note;

   /**
    * Returns the scheduleId
    * 
    * @return the scheduleId
    */
   public Integer getScheduleId()
   {
      return scheduleId;
   }

   /**
    * Sets the scheduleId to the given parameter.
    * 
    * @param scheduleId
    *           the scheduleId to set
    */
   public void setScheduleId(int scheduleId)
   {
      this.scheduleId = scheduleId;
   }

   /**
    * Returns the note
    * 
    * @return the note
    */
   public String getNote()
   {
      if (note == null)
      {
         note = "";
      }
      return note;
   }

   /**
    * Sets the note to the given parameter.
    * 
    * @param note
    *           the note to set
    */
   public void setNote(String note)
   {
      if (note == null)
      {
         note = "";
      }
      this.note = note;
   }

   public abstract void verify() throws NullDataException;

}
