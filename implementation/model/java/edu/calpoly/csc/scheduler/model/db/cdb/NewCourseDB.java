package edu.calpoly.csc.scheduler.model.db.cdb;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import edu.calpoly.csc.scheduler.model.db.Database;
import edu.calpoly.csc.scheduler.model.db.SQLDB;
import edu.calpoly.csc.scheduler.model.db.ldb.Location;

public class NewCourseDB implements Database
{
   private ArrayList<Course> data;
   private SQLDB             sqldb;

   public NewCourseDB()
   {
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
      sqldb = new SQLDB();
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
            String name = rs.getString("name");
            int catalogNum = rs.getInt("catalognum");
            String dept = rs.getString("dept");
            int wtus = rs.getInt("wtus");
            int scus = rs.getInt("scus");
            int numOfSections = rs.getInt("numofsections");
            String courseType = rs.getString("courseType");
            int length = rs.getInt("length");
            int enrollment = rs.getInt("enrollment");
            int labId = rs.getInt("labPairing");
            // Put items into Course object and add to data
            Course toAdd = new Course();
            toAdd.setName(name);
            toAdd.setCatalogNum(catalogNum);
            toAdd.setDept(dept);
            toAdd.setWtu(wtus);
            toAdd.setScu(scus);
            toAdd.setNumOfSections(numOfSections);
            toAdd.setType(courseType);
            toAdd.setLength(length);
            toAdd.setEnrollment(enrollment);
            // TODO: Pair with labs somehow?

            data.add(toAdd);
         }
      }
      catch (SQLException e)
      {
         e.printStackTrace();
      }
   }

   @Override
   public void addData(Object data)
   {
      Course course = (Course) data;
      // Create insert strings
      // TODO: update this with the new course fields
      String insertString = "insert into courses ("
            + "name, catalognum, dept, wtu, scu, numofsections, coursetype, "
            + "length, enrollment, labid)"
            + "values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
      // Create prepared statement
      PreparedStatement stmt = sqldb.getPrepStmt(insertString);
      // Set values
      try
      {
         stmt.setString(1, course.getName());
      }
      catch (SQLException e)
      {
         e.printStackTrace();
      }
      // Execute
      sqldb.executePrepStmt(stmt);
   }

   @Override
   public void editData(Object newData)
   {
      // TODO Auto-generated method stub

   }

   @Override
   public void removeData(Object data)
   {
      // TODO Auto-generated method stub

   }
}
