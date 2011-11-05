package edu.calpoly.csc.scheduler.model.db.sdb;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import edu.calpoly.csc.scheduler.model.db.DatabaseAPI;
import edu.calpoly.csc.scheduler.model.db.SQLDB;
import edu.calpoly.csc.scheduler.model.schedule.Schedule;

public class ScheduleDB implements DatabaseAPI<Schedule>
{
	//String constants
	public static final String TABLENAME = "schedules";
	public static final String SCHEDULEID = "scheduleid";
	public static final String NAME = "name";
	public static final String SCHEDULE = "schedule";
	public static final String DEPT = "dept";
	
	//Other data
   private ArrayList<Schedule> data;
   private SQLDB               sqldb;
   private int                 scheduleID;
   private String              dept;

   /**
    * Schedules are unique by scheduleid. Also, they are unique by name per department.
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
      if(sqldb.doesScheduleExist(data))
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
            if (scheduleBuf != null)
            {
               try
               {
                  ObjectInputStream objectIn;
                  objectIn = new ObjectInputStream(new ByteArrayInputStream(
                        scheduleBuf));
                  toAdd = (Schedule) objectIn.readObject();
               }
               catch (Exception e)
               {
                  e.printStackTrace();
               }
            }
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
      // Create insert string and set fields
	   ArrayList<String> fields = new ArrayList<String>();
	   fields.add(NAME);
	   fields.add(SCHEDULE);
	   fields.add(DEPT);
	   String insertString = sqldb.insertHelper(TABLENAME, fields);
      // Create prepared statement
      PreparedStatement stmt = sqldb.getPrepStmt(insertString);
      // Set values
      try
      {
         stmt.setString(1, name);
         // Get Schedule through Serializable
         Schedule data = new Schedule();
         data.setDept(dept);
         data.setName(name);
         try
         {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(baos);
            out.writeObject(data);
            out.close();
            stmt.setBytes(2, baos.toByteArray());
         }
         catch (IOException e)
         {
            e.printStackTrace();
         }
         stmt.setString(3, dept);
      }
      catch (SQLException e)
      {
         e.printStackTrace();
      }
      // Execute
      sqldb.executePrepStmt(stmt);
      this.scheduleID = sqldb.getScheduleIDByName(name, dept);
      return this.scheduleID;
   }

   public void saveSchedule(Schedule data)
   {
      saveSchedule(data, data.getName());
   }

   public void saveSchedule(Schedule data, String name)
   {
      // Make sure data in schedule and given name are correct
      data.setName(name);
      // Create update string
      ArrayList<String> fields = new ArrayList<String>();
      ArrayList<String> wheres = new ArrayList<String>();
      //Set fields
      fields.add(NAME);
      fields.add(SCHEDULE);
      fields.add(DEPT);
      //Set where clause
      wheres.add(SCHEDULEID);
      String updateString = sqldb.updateHelper(TABLENAME, fields, wheres);
      // Create prepared statement
      PreparedStatement stmt = sqldb.getPrepStmt(updateString);
      // Set values
      try
      {
         stmt.setString(1, data.getName());
         // Get Schedule through Serializable
         try
         {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(baos);
            out.writeObject(data);
            out.close();
            stmt.setBytes(2, baos.toByteArray());
         }
         catch (IOException e)
         {
            e.printStackTrace();
         }
         stmt.setString(3, data.getDept());
         stmt.setInt(4, scheduleID);
      }
      catch (SQLException e)
      {
         e.printStackTrace();
      }
      // Execute
      sqldb.executePrepStmt(stmt);
   }

   @Override
   public void removeData(Schedule data)
   {
      // Create delete string
	   ArrayList<String> wheres = new ArrayList<String>();
	   wheres.add(SCHEDULEID);
	   String deleteString = sqldb.deleteHelper(TABLENAME, wheres);
      // Create prepared statement
      PreparedStatement stmt = sqldb.getPrepStmt(deleteString);
      try
      {
         stmt.setInt(1, data.getId());
      }
      catch (SQLException e)
      {
         e.printStackTrace();
      }
      // Execute
      sqldb.executePrepStmt(stmt);
   }

   /**
    * @return the scheduleID
    */
   public int getScheduleID()
   {
      return scheduleID;
   }

   /**
    * @param scheduleID the scheduleID to set
    */
   public void setScheduleID(int scheduleID)
   {
      this.scheduleID = scheduleID;
   }
}
