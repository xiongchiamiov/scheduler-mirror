package edu.calpoly.csc.scheduler.model.db.sdb;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import edu.calpoly.csc.scheduler.model.db.DatabaseAPI;
import edu.calpoly.csc.scheduler.model.db.SQLDB;
import edu.calpoly.csc.scheduler.model.schedule.Schedule;

public class ScheduleDB implements DatabaseAPI<Schedule>
{
   // String constants
   public static final String            TABLENAME  = "schedules";
   public static final String            SCHEDULEID = "scheduleid";
   public static final String            NAME       = "name";
   public static final String            SCHEDULE   = "schedule";
   public static final String            DEPT       = "dept";

   // Other data
   private ArrayList<Schedule>           data;
   private SQLDB                         sqldb;
   private int                           scheduleID;
   private String                        dept;
   private LinkedHashMap<String, Object> fields;
   private LinkedHashMap<String, Object> wheres;

   /**
    * Schedules are unique by scheduleid. Also, they are unique by name per
    * department.
    * 
    * @param sqldb
    * @param dept
    */
   public ScheduleDB(SQLDB sqldb, String dept)
   {
      this.sqldb = sqldb;
      this.dept = dept;
   }

   public ScheduleDB(SQLDB sqldb, int scheduleID, String dept)
   {
      this.sqldb = sqldb;
      this.scheduleID = scheduleID;
      this.dept = dept;
   }

   @Override
   public ArrayList<Schedule> getData()
   {
      pullData();
      return data;
   }

   @Override
   public void saveData(Schedule data)
   {
      if (sqldb.doesScheduleExist(data))
      {
         saveSchedule(data);
      }
      else
      {
         createNewSchedule(data.getName());
      }
   }

   private void pullData()
   {
      data = new ArrayList<Schedule>();
      System.err.println("SID: " + scheduleID);
      ResultSet rs = sqldb.getSQLSchedules(scheduleID);
      try
      {
         while (rs.next())
         {
            Schedule toAdd = new Schedule();
            // Deserialize ALL THE SCHEDULE!
            byte[] scheduleBuf = rs.getBytes("schedule");
            toAdd = (Schedule) sqldb.deserialize(scheduleBuf);
            // Get ID since database maintains it
            toAdd.setId(rs.getInt("scheduleid"));
            data.add(toAdd);
         }
      }
      catch (SQLException e)
      {
         e.printStackTrace();
      }
   }

   public int createNewSchedule(String name)
   {
      Schedule data = new Schedule();
      data.setDept(dept);
      data.setName(name);
      fillMaps(data);
      sqldb.executeInsert(TABLENAME, fields);
      this.scheduleID = sqldb.getScheduleIDByName(name, dept);
      return this.scheduleID;
   }

   public void saveSchedule(Schedule data)
   {
      fillMaps(data);
      sqldb.executeUpdate(TABLENAME, fields, wheres);
   }

   public void saveSchedule(Schedule data, String name)
   {
      data.setName(name);
      saveSchedule(data);
   }

   @Override
   public void removeData(Schedule data)
   {
      fillMaps(data);
      sqldb.executeDelete(TABLENAME, wheres);
   }

   private void fillMaps(Schedule data)
   {
      // Set fields and values
      fields = new LinkedHashMap<String, Object>();
      fields.put(NAME, data.getName());
      fields.put(SCHEDULE, sqldb.serialize(data));
      fields.put(DEPT, data.getDept());
      // Where clause
      wheres = new LinkedHashMap<String, Object>();
      wheres.put(SCHEDULEID, scheduleID);
   }

   /**
    * @return the scheduleID
    */
   public int getScheduleID()
   {
      return scheduleID;
   }

   /**
    * @param scheduleID
    *           the scheduleID to set
    */
   public void setScheduleID(int scheduleID)
   {
      this.scheduleID = scheduleID;
   }
}
