package edu.calpoly.csc.scheduler.model.db.ldb;

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
import java.util.List;
import java.util.Vector;

import edu.calpoly.csc.scheduler.model.db.DatabaseAPI;
import edu.calpoly.csc.scheduler.model.db.NullDataException;
import edu.calpoly.csc.scheduler.model.db.SQLDB;
import edu.calpoly.csc.scheduler.model.db.TimeRange;
import edu.calpoly.csc.scheduler.model.db.cdb.Course;
import edu.calpoly.csc.scheduler.model.db.ldb.Location.ProvidedEquipment;
import edu.calpoly.csc.scheduler.model.schedule.WeekAvail;

public class LocationDB implements DatabaseAPI<Location>
{
   private ArrayList<Location> data;
   private SQLDB               sqldb;
   private int                 scheduleID;
   private String              quarterID;

   public LocationDB(SQLDB sqldb)
   {
      this.sqldb = sqldb;
      initDB();
   }

   public LocationDB(SQLDB sqldb, int scheduleID, String quarterID)
   {
      this.sqldb = sqldb;
      this.scheduleID = scheduleID;
      this.quarterID = quarterID;
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
      // TODO: REMOVE
      // addData(new Location().getCannedData());
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
            data.add(makeLocation(rs));
         }
      }
      catch (SQLException e)
      {
         e.printStackTrace();
      }
   }

   private Location makeLocation(ResultSet rs)
   {
      Location toAdd = new Location();
      try
      {
         String bldg = rs.getString("building");
         toAdd.setBuilding(bldg);

         String room = rs.getString("room");
         toAdd.setRoom(room);

         int occupancy = rs.getInt("maxoccupancy");
         toAdd.setMaxOccupancy(occupancy);

         String type = rs.getString("type");
         toAdd.setType(type);

         // Deserialize ProvidedEquipment
         byte[] equipBuf = rs.getBytes("providedequipment");
         if (equipBuf != null)
         {
            try
            {
               ObjectInputStream objectIn;
               objectIn = new ObjectInputStream(new ByteArrayInputStream(
                     equipBuf));
               toAdd.setProvidedEquipment((Location.ProvidedEquipment) objectIn
                     .readObject());
            }
            catch (Exception e)
            {
               e.printStackTrace();
            }
         }

         boolean adacompliant = rs.getBoolean("adacompliant");
         toAdd.setAdaCompliant(adacompliant);

         // Deserialize Availability
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
   public void addData(Location data)
   {
	   System.out.println("derp");
      data.verify();
      // Create insert string
      String insertString = "insert into locations ("
            + "building, room, maxoccupancy, type, providedequipment,"
            + " adacompliant, availability, quarterid, scheduleid)"
            + "values (?,?,?,?,?,?,?,?,?)";
      // Create prepared statement
      PreparedStatement stmt = sqldb.getPrepStmt(insertString);
      // Set values
	   System.out.println("lerp");
      try
      {
         stmt.setString(1, data.getBuilding());
         stmt.setString(2, data.getRoom());
         stmt.setInt(3, data.getMaxOccupancy());
         stmt.setString(4, data.getType());
         // Serialize ProvidedEquipment
         try
         {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(baos);
            out.writeObject(data.getProvidedEquipment());
            out.close();
            stmt.setBytes(5, baos.toByteArray());
         }
         catch (IOException e)
         {
            e.printStackTrace();
         }
         stmt.setBoolean(6, data.getAdaCompliant());
         // Serialize Availability
         try
         {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(baos);
            out.writeObject(data.getAvailability());
            out.close();
            stmt.setBytes(7, baos.toByteArray());
         }
         catch (IOException e)
         {
            e.printStackTrace();
         }
         stmt.setString(8, data.getQuarterId());
         stmt.setInt(9, data.getScheduleId());
  	   	System.out.println("sherp");
      }
      catch (SQLException e)
      {
         e.printStackTrace();
      }
      // Execute
	   System.out.println("defrp");
      sqldb.executePrepStmt(stmt);
	   System.out.println("dkerp");
   }

   @Override
   public void editData(Location data)
   {
       data.verify();
      // Create update string
      String updateString = "update locations set building = ?, room = ?,"
            + "maxoccupancy = ?, type = ?, providedequipment = ?, "
            + "adacompliant = ?, availability = ?, quarterid = ?, scheduleid = ? "
            + "where building = ? and room = ?";
      // Create prepared statement
      PreparedStatement stmt = sqldb.getPrepStmt(updateString);
      // Set values
      try
      {
         stmt.setString(1, data.getBuilding());
         stmt.setString(2, data.getRoom());
         stmt.setInt(3, data.getMaxOccupancy());
         stmt.setString(4, data.getType());
         // Serialize ProvidedEquipment
         try
         {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(baos);
            out.writeObject(data.getProvidedEquipment());
            out.close();
            stmt.setBytes(5, baos.toByteArray());
         }
         catch (IOException e)
         {
            e.printStackTrace();
         }
         stmt.setBoolean(6, data.getAdaCompliant());
         // Serialize Availability
         try
         {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(baos);
            out.writeObject(data.getAvailability());
            out.close();
            stmt.setBytes(7, baos.toByteArray());
         }
         catch (IOException e)
         {
            e.printStackTrace();
         }
         stmt.setString(8, data.getQuarterId());
         stmt.setInt(9, data.getScheduleId());

         // Where clause
         stmt.setString(10, data.getBuilding());
         stmt.setString(11, data.getRoom());

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
      PreparedStatement stmt = sqldb.getPrepStmt("delete from locations;");
      sqldb.executePrepStmt(stmt);
   }

   @Override
   public void removeData(Location data)
   {
       data.verify();
      // Create delete string
      String deleteString = "delete from locations where building = ? and room = ?";
      // Create prepared statement
      PreparedStatement stmt = sqldb.getPrepStmt(deleteString);
      try
      {
         stmt.setString(1, data.getBuilding());
         stmt.setString(2, data.getRoom());
      }
      catch (SQLException e)
      {
         e.printStackTrace();
      }
      // Execute
      sqldb.executePrepStmt(stmt);
   }

   public Location getLocation(String id)
   {
      String[] stuff = id.split("-");

      Location l = new Location(stuff[0], stuff[1]);
      if (!this.data.contains(l))
      {
         data.add(l);
      }
      else
      {
         l = this.data.get(this.data.indexOf(l));
      }

      return l;
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
