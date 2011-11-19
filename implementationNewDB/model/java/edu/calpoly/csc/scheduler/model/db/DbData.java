package edu.calpoly.csc.scheduler.model.db;

/**
 * 
 * @author Eric Liebowitz
 * @version Oct 22, 2011
 */
public abstract class DbData
{
   public static final String DBID       = "dbid";
   public static final String SCHEDULEID = "scheduleid";
   public static final String NOTE       = "note";

   /**
    * DB ID is an integer that MUST be unique for each DbData inside the
    * databases. This makes sure the exists() methods always work correctly
    */
   private Integer            dbid;
   private Integer            scheduleId;
   private String             note;

   /**
    * @return the dbid
    */
   public Integer getDbid()
   {
      return dbid;
   }

   /**
    * @param dbid
    *           the dbid to set
    */
   public void setDbid(Integer dbid)
   {
      this.dbid = dbid;
   }

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
