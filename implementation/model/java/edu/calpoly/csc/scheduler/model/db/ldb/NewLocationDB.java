package edu.calpoly.csc.scheduler.model.db.ldb;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import edu.calpoly.csc.scheduler.model.db.Database;
import edu.calpoly.csc.scheduler.model.db.SQLDB;

public class NewLocationDB implements Database
{
   private ArrayList<Location> data;
   private SQLDB               sqldb;

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
      sqldb = new SQLDB();
      pullData();
   }

   @Override
   public void pullData()
   {
      ResultSet rs = sqldb.getSQLLocations();
      try
      {
         while (rs.next())
         {
            String bldg = rs.getString("building");
            String room = rs.getString("room");
            int occupancy = rs.getInt("maxoccupancy");
            String type = rs.getString("type");
            boolean adacompliant = rs.getBoolean("adacompliant");
            boolean smartroom = rs.getBoolean("smartroom");
            boolean laptopconnectivity = rs.getBoolean("laptopconnectivity");
            boolean overhead = rs.getBoolean("overhead");
            // Put items into Location object and add to data
            Location toAdd = new Location(bldg, room, occupancy, type,
                  adacompliant, smartroom, laptopconnectivity, overhead);
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
      Location loc = (Location) data;
      // Create insert strings
      String insertString = "insert into locations ("
            + "building, room, maxoccupancy, type, smartroom, "
            + "laptopconnectivity, adacompliant, overhead)"
            + "values (?, ?, ?, ?, ?, ?, ?, ?)";
      // Create prepared statement
      PreparedStatement stmt = sqldb.getPrepStmt(insertString);
      // Set values
      try
      {
         stmt.setString(1, loc.getBuilding());
         stmt.setString(2, loc.getRoom());
         stmt.setInt(3, loc.getMaxOccupancy());
         stmt.setString(4, loc.getType());
         stmt.setBoolean(5, loc.isSmartRoom());
         stmt.setBoolean(6, loc.hasLaptopConnectivity());
         stmt.setBoolean(7, loc.isADACompliant());
         stmt.setBoolean(8, loc.hasOverhead());
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
