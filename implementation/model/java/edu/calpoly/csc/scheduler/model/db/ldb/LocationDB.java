package edu.calpoly.csc.scheduler.model.db.ldb;

import java.io.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import edu.calpoly.csc.scheduler.model.db.*;
import edu.calpoly.csc.scheduler.model.db.cdb.Course;
import edu.calpoly.csc.scheduler.model.schedule.WeekAvail;

public class LocationDB implements DatabaseAPI<Location>
{
   private ArrayList<Location> data;
   private SQLDB               sqldb;

   public LocationDB()
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
   public void addData(Location data)
   {
      // Create insert strings
      String insertString = "insert into locations ("
            + "building, room, maxoccupancy, type, smartroom, "
            + "laptopconnectivity, adacompliant, overhead, weekavail)"
            + "values (?, ?, ?, ?, ?, ?, ?, ?, ?)";
      // Create prepared statement
      PreparedStatement stmt = sqldb.getPrepStmt(insertString);
      // Set values
      try
      {
         stmt.setString(1, data.getBuilding());
         stmt.setString(2, data.getRoom());
         stmt.setInt(3, data.getMaxOccupancy());
         stmt.setString(4, data.getType());
         stmt.setBoolean(5, data.isSmartRoom());
         stmt.setBoolean(6, data.hasLaptopConnectivity());
         stmt.setBoolean(7, data.isADACompliant());
         stmt.setBoolean(8, data.hasOverhead());
         // Get WeekAvail through Serializable
         try
         {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(baos);
            out.writeObject(data.getAvailability());
            out.close();
            stmt.setBytes(9, baos.toByteArray());
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
   public void editData(Location newData)
   {
      // TODO Auto-generated method stub

   }

   @Override
   public void removeData(Location data)
   {
      // TODO Auto-generated method stub

   }

   public List<Location> findRooms(Course course, Vector<TimeRange> times)
   {
      List<Location> rooms = new Vector<Location>();

      for (Location room : data)
      {
         // Check if course has needed utilities
         if (room.providesFor(course))
         {
            // Check if each time slot is available
            for (TimeRange slot : times)
            {
               if (room.isAvailable(course.getDays(), slot.getS(), slot.getE()))
               {
                  rooms.add(room);
               }
            }
         }
      }
      return rooms;
   }
}
