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
import java.util.Collections;
import java.util.Comparator;
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

   @Deprecated
   public InstructorDB(SQLDB sqldb)
   {
      this.sqldb = sqldb;
      initDB();
   }

   public InstructorDB(SQLDB sqldb, int scheduleID)
   {
      this.sqldb = sqldb;
      this.scheduleID = scheduleID;
      initDB();
   }

   @Override
   public ArrayList<Instructor> getData()
   {
      return data;
   }

   private void initDB()
   {
      data = new ArrayList<Instructor>();
      // TODO: REMOVE THIS
      // addData(new Instructor().getCannedData());
      pullData();
   }

   @Override
   public void pullData()
   {
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

         String quarterid = rs.getString("quarterid");
         toAdd.setQuarterId(quarterid);

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

   @Override
   public void addData(Instructor data)
   {
      data.verify();
      // Create insert string
      String insertString = "insert into instructors ("
            + "firstname, lastname, userid, maxwtu, curwtu, office, "
            + "fairness, disability, generosity, availability, coursepreferences, "
            + "tprefs, itemstaught, quarterid, scheduleid)"
            + "values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
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

         stmt.setString(14, data.getQuarterId());
         stmt.setInt(15, data.getScheduleId());

      }
      catch (SQLException e)
      {
         e.printStackTrace();
      }
      // Execute
      sqldb.executePrepStmt(stmt);
   }

   @Override
   public void editData(Instructor data)
   {
      data.verify();
      // Create update string
      String updateString = "update instructors set firstname = ?, lastname = ?,"
            + "userid = ?, maxwtu = ?, curwtu = ?, office = ?, "
            + "fairness = ?, disability = ?, generosity = ?, availability = ?, "
            + "coursepreferences = ?, tprefs = ?, itesmtaught = ?, quarterid = ?, "
            + "scheduleid = ?, where userid = ?";
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

         stmt.setString(14, data.getQuarterId());
         stmt.setInt(15, data.getScheduleId());

         // Where clause
         stmt.setString(16, data.getUserID());
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
      String deleteString = "delete from instructors where userid = ?";
      // Create prepared statement
      PreparedStatement stmt = sqldb.getPrepStmt(deleteString);
      try
      {
         stmt.setString(1, data.getUserID());
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
      PreparedStatement stmt = sqldb.getPrepStmt("delete from instructors;");
      sqldb.executePrepStmt(stmt);
   }

   /**
    * Returns a vector of instructors for teaching this course. Instructors
    * returned have the needed WTUs left to teach this course. Sorted by
    * instructors that want to teach it the most coming first.
    * 
    * @param course
    *           Course which the returned instructors can teach
    * 
    * @return Instructors that can teach this class without going over their WTU
    *         limit, in order of most wanting to teach the class first
    */
   public Vector<Instructor> getInstructorsForCourse(final Course course)
   {
      Vector<Instructor> instructors = new Vector<Instructor>();
      int courseWTU = course.getWtu();

      for (Instructor i : data)
      {
         // Check if instructor has enough WTUs
         if (i.getAvailableWTU() >= courseWTU)
         {
            // Check for 0 course preference
            if (i.getPreference(course) != 0)
            {
               instructors.add(i);
            }
         }
      }

      // Sort by teaching priority in descending order. Teachers that want to
      // teach most come first.

      Collections.<Instructor> sort(instructors, new Comparator<Instructor>()
      {
         @Override
         public int compare(Instructor o1, Instructor o2)
         {
            return o1.getPreference(course) - o2.getPreference(course);
         }
      });
      return instructors;
   }
}
