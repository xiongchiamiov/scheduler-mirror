package edu.calpoly.csc.scheduler.model.db.idb;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import edu.calpoly.csc.scheduler.model.db.*;
import edu.calpoly.csc.scheduler.model.db.cdb.Course;

public class NewInstructorDB implements Database<Instructor>
{
   private ArrayList<Instructor> data;

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
            String fname = rs.getString("firstname");
            String lname = rs.getString("lastname");
            String userid = rs.getString("userid");
            int wtu = rs.getInt("wtu");
            String building = rs.getString("building");
            String room = rs.getString("room");
            boolean disabilities = rs.getBoolean("disabilities");
            // TODO: Put items into Instructor object and add to data
         }
      }
      catch (SQLException e)
      {
         e.printStackTrace();
      }
   }
   
   /**
    * Stubbed. Returns an instructor who wants to teach this course. In particular, 
    * there should be no other instructor other than the one returned who wants
    * to teach the given Course <b>more</b> than this one.<br>
    * <br>
    * Also, the returned instructor must be able to teach the course. I.e. he 
    * must have enough WTU's available to take on the course.<br>
    * <br>
    * If no instructor can be found to teach the given course, null is 
    * returned.
    * 
    * @.todo Write this
    * 
    * @param c Course which the returned instructor wants to teach
    * 
    * @return an Instructor who wants to teach the given Course at least as much
    *         as every other instructor and who is able to add the course to 
    *         his workload. If no instructor can be found, null is returned.
    */
   public Instructor getInstructor (Course c)
   {
      return null;
   }
}
