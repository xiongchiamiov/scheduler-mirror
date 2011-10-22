package edu.calpoly.csc.scheduler.model.db.cdb;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import edu.calpoly.csc.scheduler.model.db.DatabaseAPI;
import edu.calpoly.csc.scheduler.model.db.SQLDB;
import edu.calpoly.csc.scheduler.model.db.cdb.Course.CourseType;

import edu.calpoly.csc.scheduler.model.schedule.*;

public class CourseDB implements DatabaseAPI<Course>
{
   private ArrayList<Course> data;
   private SQLDB             sqldb;

   public CourseDB(SQLDB sqldb)
   {
      this.sqldb = sqldb;
      initDB();
   }

   @Override
   public ArrayList<Course> getData()
   {
      return data;
   }

   private void initDB()
   {
      data = new ArrayList<Course>();
      pullData();
   }

   @Override
   public void pullData()
   {
      ResultSet rs = sqldb.getSQLCourses();
      try
      {
         while (rs.next())
         {
            // Retrieve by column name
            int id = rs.getInt("id");
            String name = rs.getString("name");
            int catalogNum = rs.getInt("coursenum");
            // String dept = rs.getString("dept");
            int wtus = rs.getInt("wtus");
            int scus = rs.getInt("scus");
            // int numOfSections = rs.getInt("numofsections");
            String courseType = rs.getString("classType");
            // int length = rs.getInt("length");
            int enrollment = rs.getInt("maxEnrollment");
            int labId = rs.getInt("labPairing");
            boolean smartroom = rs.getBoolean("smartroom");
            boolean laptop = rs.getBoolean("laptop");
            boolean overhead = rs.getBoolean("overhead");
            int hoursPerWeek = rs.getInt("hoursperweek");
            String ctPrefix = rs.getString("ctPrefix");
            String prefix = rs.getString("prefix");

            // Put items into Course object and add to data
            Course toAdd = new Course(id, name, catalogNum, wtus, scus,
                  courseType, enrollment, labId, smartroom, laptop, overhead,
                  hoursPerWeek, ctPrefix, prefix);

            // TODO: Check what value null ints are stored as and change this
            /*
             * if (labId > -1) { lab = new Course(); lab.setId(labId);
             * lab.setType(CourseType.LAB); } toAdd.setLab(lab);
             */
            data.add(toAdd);
         }
      }
      catch (SQLException e)
      {
         e.printStackTrace();
      }
      linkLabs();
   }

   /**
    * Links all of the courses that have labs with their labs
    */
   private void linkLabs()
   {
      for (Course course : data)
      {
         // Course has a lab
         if (course.getLab() != null)
         {
            // Find lab data and put it into the object
            course.setLab(data.get(data.indexOf(course.getLab())));
         }
      }
   }

   @Override
   public void addData(Course data)
   {
      // Create insert string
      String insertString = "insert into courses ("
            + "name, catalognum, dept, wtu, scu, numofsections, coursetype, "
            + "length, enrollment, labid)"
            + "values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
      // Create prepared statement
      PreparedStatement stmt = sqldb.getPrepStmt(insertString);
      // TODO: Clean this next part up. It's gross.
      // Check if it has a lab
      int labid = -1;
      try
      {
         if (data.getLab() != null)
         {
            // Insert lab into DB and get id
            stmt.setString(1, data.getLab().getName());
            stmt.setInt(2, data.getLab().getCatalogNum());
            stmt.setString(3, data.getLab().getDept());
            stmt.setInt(4, data.getLab().getWtu());
            stmt.setInt(5, data.getLab().getScu());
            stmt.setInt(6, data.getLab().getNumOfSections());
            stmt.setString(7, data.getLab().getType().toString());
            stmt.setInt(8, data.getLab().getLength());
            stmt.setInt(9, data.getLab().getEnrollment());
            labid = sqldb.executePrepStmt(stmt);
         }
         // Set values
         stmt.setString(1, data.getName());
         stmt.setInt(2, data.getCatalogNum());
         stmt.setString(3, data.getDept());
         stmt.setInt(4, data.getWtu());
         stmt.setInt(5, data.getScu());
         stmt.setInt(6, data.getNumOfSections());
         stmt.setString(7, data.getType().toString());
         stmt.setInt(8, data.getLength());
         stmt.setInt(9, data.getEnrollment());
         if (labid != -1)
         {
            stmt.setInt(10, labid);
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
   public void editData(Course data)
   {
      // Create update string
      String updateString = "update courses set name = ?, catalognum = ?, "
            + "dept = ?, wtu = ?, scu = ?, numofsections = ?, "
            + "coursetype = ?, length = ?, enrollment = ?, "
            + "labid = ? where id = ?";
      // Create prepared statement
      PreparedStatement stmt = sqldb.getPrepStmt(updateString);
      // Set values
      try
      {
         stmt.setString(1, data.getName());
         stmt.setInt(2, data.getCatalogNum());
         stmt.setString(3, data.getDept());
         stmt.setInt(4, data.getWtu());
         stmt.setInt(5, data.getScu());
         stmt.setInt(6, data.getNumOfSections());
         stmt.setString(7, data.getType().toString());
         stmt.setInt(8, data.getLength());
         stmt.setInt(9, data.getEnrollment());
         stmt.setInt(10, data.getLab().getId());
         stmt.setInt(11, data.getId());
      }
      catch (SQLException e)
      {
         e.printStackTrace();
      }
      // Execute
      sqldb.executePrepStmt(stmt);
   }

   @Override
   public void removeData(Course data)
   {
      // Create delete string
      String deleteString = "delete from courses where id = ?";
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
}
