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
import java.util.List;
import java.util.Vector;

import edu.calpoly.csc.scheduler.model.db.Database;
import edu.calpoly.csc.scheduler.model.db.SQLDB;
import edu.calpoly.csc.scheduler.model.db.cdb.Course;
import edu.calpoly.csc.scheduler.model.db.ldb.Location;
import edu.calpoly.csc.scheduler.model.schedule.WeekAvail;

public class NewInstructorDB implements Database<Instructor>
{
   private ArrayList<Instructor> data;
   private SQLDB                 sqldb;

   public NewInstructorDB()
   {
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
      sqldb = new SQLDB();
      pullData();
   }

   @Override
   public void pullData()
   {
      ResultSet rs = sqldb.getSQLInstructors();
      try
      {
         while (rs.next())
         {
            // Retrieve by column name
            String fname = rs.getString("firstname");
            String lname = rs.getString("lastname");
            String userid = rs.getString("userid");
            int maxwtu = rs.getInt("maxwtu");
            int currentwtu = rs.getInt("availablewtu");
            String building = rs.getString("building");
            String room = rs.getString("room");
            boolean disabilities = rs.getBoolean("disabilities");
            // Put items into Instructor object and add to data
            Location office = new Location(building, room);
            Instructor toAdd = new Instructor(fname, lname, userid, maxwtu,
                  office, disabilities);
            toAdd.setMaxWTU(maxwtu);
            toAdd.setAvailableWTU(currentwtu);
            // Deserialize week availiability
            byte[] buf = rs.getBytes("weekavail");
            if (buf != null)
            {
               try
               {
                  ObjectInputStream objectIn;
                  objectIn = new ObjectInputStream(
                        new ByteArrayInputStream(buf));
                  toAdd.setAvailability((WeekAvail) objectIn.readObject());
               }
               catch (Exception e)
               {
                  e.printStackTrace();
               }
            }
            data.add(toAdd);
         }
      }
      catch (SQLException e)
      {
         e.printStackTrace();
      }
   }

   @Override
   public void addData(Instructor data)
   {
      // Create insert strings
      String insertString = "insert into instructors ("
            + "firstname, lastname, userid, maxwtu,, currentwtu building, room, "
            + "disabilities, weekavail)" + "values (?, ?, ?, ?, ?, ?, ?, ?, ?)";
      // Create prepared statement
      PreparedStatement stmt = sqldb.getPrepStmt(insertString);
      // Set values
      try
      {
         stmt.setString(1, data.getFirstName());
         stmt.setString(2, data.getLastName());
         stmt.setString(3, data.getId());
         stmt.setInt(4, data.getMaxWTU());
         stmt.setInt(5, data.getAvailableWTU());
         stmt.setString(6, data.getOffice().getBuilding());
         stmt.setString(7, data.getOffice().getRoom());
         stmt.setBoolean(8, data.getDisability());
         // Get WeekAvail and CoursePrefs through Serializable
         try
         {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(baos);
            out.writeObject(data.getAvailability());
            out.close();
            stmt.setBytes(9, baos.toByteArray());
            // TODO: get coursepreferences
         }
         catch (IOException e)
         {
            e.printStackTrace();
         }
      }
      catch (SQLException e)
      {
         e.printStackTrace();
      }
      // Execute
      sqldb.executePrepStmt(stmt);

   }

   @Override
   public void editData(Instructor newData)
   {
      // TODO Auto-generated method stub

   }

   @Override
   public void removeData(Instructor data)
   {
      // TODO Auto-generated method stub

   }

   /**
    * Stubbed. Returns an instructor who wants to teach this course. In
    * particular, there should be no other instructor other than the one
    * returned who wants to teach the given Course <b>more</b> than this one.<br>
    * <br>
    * Also, the returned instructor must be able to teach the course. I.e. he
    * must have enough WTU's available to take on the course.<br>
    * <br>
    * If no instructor can be found to teach the given course, null is returned.
    * 
    * @.todo Write this
    * 
    * @param c
    *           Course which the returned instructor wants to teach
    * 
    * @return an Instructor who wants to teach the given Course at least as much
    *         as every other instructor and who is able to add the course to his
    *         workload. If no instructor can be found, null is returned.
    */
   public Instructor getInstructor(Course c)
   {
      return null;
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
