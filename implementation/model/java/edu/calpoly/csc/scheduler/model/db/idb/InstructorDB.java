package edu.calpoly.csc.scheduler.model.db.idb;

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
   private ArrayList<Instructor> data;
   private SQLDB                 sqldb;
   private int                   scheduleID;

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
      if(sqldb.doesInstructorExist(data))
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

         // Deserialize office
         byte[] officeBuf = rs.getBytes("office");
         if (officeBuf != null)
         {
            try
            {
               ObjectInputStream objectIn;
               objectIn = new ObjectInputStream(new ByteArrayInputStream(
                     officeBuf));
               toAdd.setOffice((Location) objectIn.readObject());
            }
            catch (Exception e)
            {
               e.printStackTrace();
            }
         }

         int fairness = rs.getInt("fairness");
         toAdd.setFairness(fairness);

         boolean disability = rs.getBoolean("disability");
         toAdd.setDisability(disability);

         int generosity = rs.getInt("generosity");
         toAdd.setGenerosity(generosity);

         // Deserialize availability
         byte[] availBuf = rs.getBytes("availability");
         if (availBuf != null)
         {
            try
            {
               ObjectInputStream objectIn;
               objectIn = new ObjectInputStream(new ByteArrayInputStream(
                     availBuf));
               toAdd.setAvailability((WeekAvail) objectIn.readObject());
            }
            catch (Exception e)
            {
               e.printStackTrace();
            }
         }

         // Deserialize coursepreferences
         byte[] coursePrefBuf = rs.getBytes("coursepreferences");
         if (coursePrefBuf != null)
         {
            try
            {
               ObjectInputStream objectIn;
               objectIn = new ObjectInputStream(new ByteArrayInputStream(
                     coursePrefBuf));
               toAdd.setCoursePreferences((HashMap<Course, Integer>) objectIn
                     .readObject());
            }
            catch (Exception e)
            {
               e.printStackTrace();
            }
         }

         // Deserialize tprefs
         byte[] tprefBuf = rs.getBytes("tprefs");
         if (tprefBuf != null)
         {
            try
            {
               ObjectInputStream objectIn;
               objectIn = new ObjectInputStream(new ByteArrayInputStream(
                     tprefBuf));
               toAdd.settPrefs((HashMap<Day, LinkedHashMap<Time, TimePreference>>) objectIn
                     .readObject());
            }
            catch (Exception e)
            {
               e.printStackTrace();
            }
         }

         // Deserialize itemstaught
         byte[] taughtBuf = rs.getBytes("itemstaught");
         if (taughtBuf != null)
         {
            try
            {
               ObjectInputStream objectIn;
               objectIn = new ObjectInputStream(new ByteArrayInputStream(
                     taughtBuf));
               toAdd.setItemsTaught((Vector<ScheduleItem>) objectIn
                     .readObject());
            }
            catch (Exception e)
            {
               e.printStackTrace();
            }
         }
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
      data.verify();
      // Create insert string
      String insertString = "insert into instructors ("
            + "firstname, lastname, userid, maxwtu, curwtu, office, "
            + "fairness, disability, generosity, availability, coursepreferences, "
            + "tprefs, itemstaught, scheduleid)"
            + "values (?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
      // Create prepared statement
      PreparedStatement stmt = sqldb.getPrepStmt(insertString);
      // Set values
      try
      {
         stmt.setString(1, data.getFirstName());
         stmt.setString(2, data.getLastName());
         stmt.setString(3, data.getUserID());
         stmt.setInt(4, data.getMaxWTU());
         stmt.setInt(5, data.getCurWtu());
         // Serialize office
         try
         {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(baos);
            out.writeObject(data.getOffice());
            out.close();
            stmt.setBytes(6, baos.toByteArray());
         }
         catch (IOException e)
         {
            e.printStackTrace();
         }

         stmt.setInt(7, data.getFairness());
         stmt.setBoolean(8, data.getDisability());
         stmt.setInt(9, data.getGenerosity());
         // Serialize lots of things
         try
         {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(baos);
            // Serialize availability
            out.writeObject(data.getAvailability());
            out.close();
            stmt.setBytes(10, baos.toByteArray());

            // Serialize coursepreferences
            baos = new ByteArrayOutputStream();
            out = new ObjectOutputStream(baos);
            out.writeObject(data.getCoursePreferences());
            out.close();
            stmt.setBytes(11, baos.toByteArray());

            // Serialize tprefs
            baos = new ByteArrayOutputStream();
            out = new ObjectOutputStream(baos);
            out.writeObject(data.gettPrefs());
            out.close();
            stmt.setBytes(12, baos.toByteArray());

            // Serialize itemstaught
            baos = new ByteArrayOutputStream();
            out = new ObjectOutputStream(baos);
            out.writeObject(data.getItemsTaught());
            out.close();
            stmt.setBytes(13, baos.toByteArray());
         }
         catch (IOException e)
         {
            e.printStackTrace();
         }

         stmt.setInt(14, scheduleID);

      }
      catch (SQLException e)
      {
         e.printStackTrace();
      }
      // Execute
      sqldb.executePrepStmt(stmt);
   }

   private void editData(Instructor data)
   {
      data.verify();
      // Create update string
      String updateString = "update instructors set firstname = ?, lastname = ?,"
            + "userid = ?, maxwtu = ?, curwtu = ?, office = ?, "
            + "fairness = ?, disability = ?, generosity = ?, availability = ?, "
            + "coursepreferences = ?, tprefs = ?, itemstaught = ?, "
            + "scheduleid = ? where userid = ? and scheduleid = ?";
      // Create prepared statement
      PreparedStatement stmt = sqldb.getPrepStmt(updateString);
      // Set values
      try
      {
         stmt.setString(1, data.getFirstName());
         stmt.setString(2, data.getLastName());
         stmt.setString(3, data.getUserID());
         stmt.setInt(4, data.getMaxWTU());
         stmt.setInt(5, data.getCurWtu());
         // Serialize office
         try
         {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(baos);
            out.writeObject(data.getOffice());
            out.close();
            stmt.setBytes(6, baos.toByteArray());
         }
         catch (IOException e)
         {
            e.printStackTrace();
         }

         stmt.setInt(7, data.getFairness());
         stmt.setBoolean(8, data.getDisability());
         stmt.setInt(9, data.getGenerosity());
         // Serialize lots of things
         try
         {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(baos);
            // Serialize availability
            out.writeObject(data.getAvailability());
            out.close();
            stmt.setBytes(10, baos.toByteArray());

            // Serialize coursepreferences
            baos = new ByteArrayOutputStream();
            out = new ObjectOutputStream(baos);
            out.writeObject(data.getCoursePreferences());
            out.close();
            stmt.setBytes(11, baos.toByteArray());

            // Serialize tprefs
            baos = new ByteArrayOutputStream();
            out = new ObjectOutputStream(baos);
            out.writeObject(data.gettPrefs());
            out.close();
            stmt.setBytes(12, baos.toByteArray());

            // Serialize itemstaught
            baos = new ByteArrayOutputStream();
            out = new ObjectOutputStream(baos);
            out.writeObject(data.getItemsTaught());
            out.close();
            stmt.setBytes(13, baos.toByteArray());
         }
         catch (IOException e)
         {
            e.printStackTrace();
         }

         stmt.setInt(14, scheduleID);

         // Where clause
         stmt.setString(15, data.getUserID());
         stmt.setInt(16, scheduleID);
      }
      catch (SQLException e)
      {
         e.printStackTrace();
      }
      // Execute
      sqldb.executePrepStmt(stmt);

   }

   @Override
   public void removeData(Instructor data)
   {
      data.verify();
      // Create delete string
      String deleteString = "delete from instructors where userid = ? and scheduleid = ?";
      // Create prepared statement
      PreparedStatement stmt = sqldb.getPrepStmt(deleteString);
      try
      {
         stmt.setString(1, data.getUserID());
         stmt.setInt(2, scheduleID);
      }
      catch (SQLException e)
      {
         e.printStackTrace();
      }
      // Execute
      sqldb.executePrepStmt(stmt);
   }

   public void clearData()
   {
      PreparedStatement stmt = sqldb.getPrepStmt("delete from instructors");
      sqldb.executePrepStmt(stmt);
   }
}
