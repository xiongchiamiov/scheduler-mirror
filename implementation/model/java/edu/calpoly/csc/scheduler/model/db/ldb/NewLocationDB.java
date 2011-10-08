package edu.calpoly.csc.scheduler.model.db.ldb;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import edu.calpoly.csc.scheduler.model.db.Database;
import edu.calpoly.csc.scheduler.model.db.SQLDB;

public class NewLocationDB implements Database
{
   private ArrayList<Location> data;

   public NewLocationDB()
   {
      initDB();
   }

   @Override
   public ArrayList<Location> getData()
   {
      return data;
   }

   private void initDB()
   {
      data = new ArrayList<Location>();
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
            String bldg = rs.getString("building");
            String room = rs.getString("room");
            int occupancy = rs.getInt("maxoccupancy");
            String type = rs.getString("type");
            boolean smartroom = rs.getBoolean("smartroom");
            boolean laptopconnectivity = rs.getBoolean("laptopconnectivity");
            boolean adacompliant = rs.getBoolean("adacompliant");
            boolean overhead = rs.getBoolean("overhead");
            // TODO: Put items into Instructor object and add to data
         }
      }
      catch (SQLException e)
      {
         e.printStackTrace();
      }
   }

}
