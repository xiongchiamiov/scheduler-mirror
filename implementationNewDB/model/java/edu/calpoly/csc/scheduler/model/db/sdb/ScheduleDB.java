package edu.calpoly.csc.scheduler.model.db.sdb;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;

import edu.calpoly.csc.scheduler.model.db.AbstractDatabase;
import edu.calpoly.csc.scheduler.model.db.DbData;
import edu.calpoly.csc.scheduler.model.db.SQLDB;
import edu.calpoly.csc.scheduler.model.db.cdb.CourseDB;
import edu.calpoly.csc.scheduler.model.db.idb.InstructorDB;
import edu.calpoly.csc.scheduler.model.db.ldb.LocationDB;
import edu.calpoly.csc.scheduler.model.db.udb.UserDataDB;
import edu.calpoly.csc.scheduler.model.schedule.Schedule;

public class ScheduleDB extends AbstractDatabase<Schedule>
{
   // String constants
   public static final String TABLENAME  = "schedules";
   public static final String SCHEDULE   = "schedule";
   public static final String SCHEDULENAME = "schedulename";

   /**
    * Schedules are unique by dbid. Also, they are unique by name per
    * department.
    * 
    * @param sqldb
    * @param dept
    */
   public ScheduleDB(SQLDB sqldb)
   {
      this.sqldb = sqldb;
      this.scheduleDBId = -1;
   }
   
   @Override
   public void saveData (Schedule data)
   {
      data.verify();
      if (exists(data))
      {
         editData(data);
      }
      else
      {
         addData(data);
      }
   }

   protected void fillFields(Schedule data)
   {
      // Set fields and values
      fields = new LinkedHashMap<String, Object>();
      fields.put(SCHEDULENAME, data.getName());
      fields.put(SCHEDULE, sqldb.serialize(data));
   }

   @Override
   protected void fillWheres(Schedule data)
   {
      // Where clause
      wheres = new LinkedHashMap<String, Object>();
      if(this.scheduleDBId > 0)
      {
    	  //Use the held scheduleDBID
    	  wheres.put(DbData.DBID, scheduleDBId);
      }
      else
      {
    	  //Use the one in data
    	  wheres.put(DbData.DBID, data.getScheduleDBId());
      }
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
            toAdd.setScheduleDBId(rs.getInt(DbData.DBID));
            toAdd.setName(rs.getString(SCHEDULENAME));
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
   public void removeData(Schedule data)
   {
      super.removeData(data);
      // Where clause
      wheres = new LinkedHashMap<String, Object>();
      wheres.put(DbData.DBID, data.getScheduleDBId());
      sqldb.executeDelete(CourseDB.TABLENAME, wheres);
      sqldb.executeDelete(InstructorDB.TABLENAME, wheres);
      sqldb.executeDelete(LocationDB.TABLENAME, wheres);
      sqldb.executeDelete(UserDataDB.TABLENAME, wheres);
   }

   @Override
   protected String getTableName()
   {
      return TABLENAME;
   }

   /**
    * @return the scheduleID
    */
   public int getScheduleDBID(Schedule data)
   {
      this.scheduleDBId = sqldb.getLastGeneratedKey();
      return scheduleDBId;
   }
   
   public void setScheduleDBID(int scheduleid)
   {
      this.scheduleDBId = scheduleid;
   }

   /**
    * Gets a schedule by scheduleid for copying
    */
   public Schedule getSchedule(int scheduleid)
   {
      return make(sqldb.getDataByScheduleID(TABLENAME, scheduleid));
   }
}
