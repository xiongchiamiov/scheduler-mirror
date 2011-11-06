package edu.calpoly.csc.scheduler.model.db.idb;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Vector;

import edu.calpoly.csc.scheduler.model.db.DatabaseAPI;
import edu.calpoly.csc.scheduler.model.db.SQLDB;
import edu.calpoly.csc.scheduler.model.db.Time;
import edu.calpoly.csc.scheduler.model.db.cdb.Course;
import edu.calpoly.csc.scheduler.model.db.ldb.Location;
import edu.calpoly.csc.scheduler.model.schedule.Day;
import edu.calpoly.csc.scheduler.model.schedule.ScheduleItem;
import edu.calpoly.csc.scheduler.model.schedule.WeekAvail;

public class InstructorDB implements DatabaseAPI<Instructor>
{
   // String constants to describe the database
   public static final String            TABLENAME         = "instructors";
   public static final String            FIRSTNAME         = "firstname";
   public static final String            LASTNAME          = "lastname";
   public static final String            USERID            = "userid";
   public static final String            MAXWTU            = "maxwtu";
   public static final String            CURWTU            = "curwtu";
   public static final String            OFFICE            = "office";
   public static final String            FAIRNESS          = "fairness";
   public static final String            DISABILITY        = "disability";
   public static final String            GENEROSITY        = "generosity";
   public static final String            AVAILABILITY      = "availability";
   public static final String            COURSEPREFERENCES = "coursepreferences";
   public static final String            TPREFS            = "tprefs";
   public static final String            ITEMSTAUGHT       = "itemstaught";
   public static final String            SCHEDULEID        = "scheduleid";
   // Other data
   private ArrayList<Instructor>         data;
   private SQLDB                         sqldb;
   private int                           scheduleID;
   private LinkedHashMap<String, Object> fields;
   private LinkedHashMap<String, Object> wheres;

   public InstructorDB(SQLDB sqldb, int scheduleID)
   {
      this.sqldb = sqldb;
      this.scheduleID = scheduleID;
   }

   @Override
   public ArrayList<Instructor> getData()
   {
      pullData();
      return data;
   }

   @Override
   public void saveData(Instructor data)
   {
      data.verify();
      data.setScheduleId(scheduleID);
      if (sqldb.doesInstructorExist(data))
      {
         editData(data);
      }
      else
      {
         addData(data);
      }
   }

   private void pullData()
   {
      data = new ArrayList<Instructor>();
      ResultSet rs = sqldb.getSQLInstructors(scheduleID);
      try
      {
         while (rs.next())
         {
            data.add(makeInstructor(rs));
         }
      }
      catch (SQLException e)
      {
         e.printStackTrace();
      }
   }

   private Instructor makeInstructor(ResultSet rs)
   {
      Instructor toAdd = new Instructor();
      try
      {
         // Retrieve by column name
         String fname = rs.getString("firstname");
         toAdd.setFirstName(fname);

         String lname = rs.getString("lastname");
         toAdd.setLastName(lname);

         String userid = rs.getString("userid");
         toAdd.setUserID(userid);

         int maxwtu = rs.getInt("maxwtu");
         toAdd.setMaxWtu(maxwtu);

         int curwtu = rs.getInt("curwtu");
         toAdd.setCurWtu(curwtu);

         byte[] officeBuf = rs.getBytes("office");
         toAdd.setOffice((Location) sqldb.deserialize(officeBuf));

         int fairness = rs.getInt("fairness");
         toAdd.setFairness(fairness);

         boolean disability = rs.getBoolean("disability");
         toAdd.setDisability(disability);

         int generosity = rs.getInt("generosity");
         toAdd.setGenerosity(generosity);

         byte[] availBuf = rs.getBytes("availability");
         toAdd.setAvailability((WeekAvail) sqldb.deserialize(availBuf));

         byte[] coursePrefBuf = rs.getBytes("coursepreferences");
         toAdd.setCoursePreferences((HashMap<Course, Integer>) sqldb
               .deserialize(coursePrefBuf));

         byte[] tprefBuf = rs.getBytes("tprefs");
         toAdd.settPrefs((HashMap<Day, LinkedHashMap<Time, TimePreference>>) sqldb
               .deserialize(tprefBuf));

         byte[] taughtBuf = rs.getBytes("itemstaught");
         toAdd.setItemsTaught((Vector<ScheduleItem>) sqldb
               .deserialize(taughtBuf));

         int scheduleid = rs.getInt("scheduleid");
         toAdd.setScheduleId(scheduleid);
      }
      catch (SQLException e)
      {
         e.printStackTrace();
      }
      toAdd.verify();
      return toAdd;
   }

   private void addData(Instructor data)
   {
      fillMaps(data);
      sqldb.executeInsert(TABLENAME, fields);
   }

   private void editData(Instructor data)
   {
      fillMaps(data);
      sqldb.executeUpdate(TABLENAME, fields, wheres);
   }

   @Override
   public void removeData(Instructor data)
   {
      data.verify();
      fillMaps(data);
      sqldb.executeDelete(TABLENAME, wheres);
   }

   private void fillMaps(Instructor data)
   {
      // Set fields and values
      fields = new LinkedHashMap<String, Object>();
      fields.put(FIRSTNAME, data.getFirstName());
      fields.put(LASTNAME, data.getLastName());
      fields.put(USERID, data.getUserID());
      fields.put(MAXWTU, data.getMaxWtu());
      fields.put(CURWTU, data.getCurWtu());
      fields.put(OFFICE, sqldb.serialize(data.getOffice()));
      fields.put(FAIRNESS, data.getFairness());
      fields.put(DISABILITY, data.getDisability());
      fields.put(GENEROSITY, data.getGenerosity());
      fields.put(AVAILABILITY, sqldb.serialize(data.getAvailability()));
      fields.put(COURSEPREFERENCES,
            sqldb.serialize(data.getCoursePreferences()));
      fields.put(TPREFS, sqldb.serialize(data.gettPrefs()));
      fields.put(ITEMSTAUGHT, sqldb.serialize(data.getItemsTaught()));
      fields.put(SCHEDULEID, scheduleID);
      // Where clause
      wheres = new LinkedHashMap<String, Object>();
      wheres.put(USERID, data.getUserID());
      wheres.put(SCHEDULEID, scheduleID);
   }
}
