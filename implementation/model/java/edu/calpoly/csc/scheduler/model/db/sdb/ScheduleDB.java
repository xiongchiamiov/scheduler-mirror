package edu.calpoly.csc.scheduler.model.db.sdb;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import edu.calpoly.csc.scheduler.model.db.AbstractDatabase;
import edu.calpoly.csc.scheduler.model.db.SQLDB;
import edu.calpoly.csc.scheduler.model.schedule.Schedule;

public class ScheduleDB extends AbstractDatabase<Schedule>
{
   // String constants
   public static final String TABLENAME  = "schedules";
   public static final String SCHEDULEID = "scheduleid";
   public static final String NAME       = "name";
   public static final String SCHEDULE   = "schedule";
   public static final String DEPT       = "dept";

   /**
    * Schedules are unique by scheduleid. Also, they are unique by name per
    * department.
    * 
    * @param sqldb
    * @param dept
    */
   public ScheduleDB(SQLDB sqldb)
   {
      this.sqldb = sqldb;
   }

   protected boolean exists(Schedule data)
   {
      return sqldb.doesScheduleExist(data);
   }

   protected void fillMaps(Schedule data)
   {
      // Set fields and values
      fields = new LinkedHashMap<String, Object>();
      fields.put(NAME, data.getName());
      fields.put(SCHEDULE, sqldb.serialize(data));
      fields.put(DEPT, data.getDept());
      // Where clause
      wheres = new LinkedHashMap<String, Object>();
      wheres.put(SCHEDULEID, scheduleId);
   }

   protected ResultSet getDataByScheduleId(int sid)
   {
      return sqldb.getSQLSchedules(scheduleId);
   }

   protected Schedule make(ResultSet rs)
   {
      Schedule toAdd = new Schedule();
      try
      {
         while (rs.next())
         {
            // Deserialize ALL THE SCHEDULE!
            byte[] scheduleBuf = rs.getBytes(SCHEDULE);
            toAdd = (Schedule) sqldb.deserialize(scheduleBuf);
            // Get ID since database maintains it
            toAdd.setId(rs.getInt(SCHEDULEID));
            data.add(toAdd);
         }
      }
      catch (SQLException e)
      {
         e.printStackTrace();
      }
      toAdd.verify();
      return toAdd;
   }

   @Override
   protected String getTableName()
   {
      return TABLENAME;
   }

   /**
    * @return the scheduleID
    */
   public int getScheduleID(Schedule data)
   {
      this.scheduleId = sqldb.getScheduleIDByName(data.getName(),
            data.getDept());
      return scheduleId;
   }

   /**
    * @param scheduleID
    *           the scheduleID to set
    */
   public void setScheduleID(int scheduleID)
   {
      this.scheduleId = scheduleID;
   }

}
