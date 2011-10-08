package edu.calpoly.csc.scheduler.model.db.idb;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import edu.calpoly.csc.scheduler.model.db.Database;
import edu.calpoly.csc.scheduler.model.db.SQLDB;

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
}
