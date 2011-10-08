package edu.calpoly.csc.scheduler.model.db.cdb;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import edu.calpoly.csc.scheduler.model.db.*;

public class NewCourseDB implements Database
{
   private ArrayList<Course> data;

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
      pullData();
   }

   @Override
   public void pullData()
   {
      SQLDB db = new SQLDB();
      ResultSet rs = db.getSQLCourses();
      try
      {
         while (rs.next())
         {
            // Retrieve by column name
            String name = rs.getString("name");
            int wtus = rs.getInt("wtus");
            int scus = rs.getInt("scus");
            String classType = rs.getString("classType");
            int maxEnrollment = rs.getInt("maxEnrollment");
            int labId = rs.getInt("labPairing");
            int hoursPerWeek = rs.getInt("hoursPerWeek");
            String ctPrefix = rs.getString("ctPrefix");
            boolean overhead = rs.getBoolean("overhead");
            boolean smartroom = rs.getBoolean("smartroom");
            boolean laptop = rs.getBoolean("laptop");
            String prefix = rs.getString("prefix");
            String dfcString = rs.getString("courses_to_preferences.prefid");
            // TODO: Put items into Course object and add to data
         }
      }
      catch (SQLException e)
      {
         e.printStackTrace();
      }
   }
}
