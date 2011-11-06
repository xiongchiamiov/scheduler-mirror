package edu.calpoly.csc.scheduler.model.db.ldb;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Vector;

import edu.calpoly.csc.scheduler.model.db.DatabaseAPI;
import edu.calpoly.csc.scheduler.model.db.SQLDB;
import edu.calpoly.csc.scheduler.model.db.TimeRange;
import edu.calpoly.csc.scheduler.model.db.cdb.Course;
import edu.calpoly.csc.scheduler.model.schedule.WeekAvail;

public class LocationDB implements DatabaseAPI<Location>
{
   // String constants to describe the database
   public static final String            TABLENAME         = "locations";
   public static final String            BUILDING          = "building";
   public static final String            ROOM              = "room";
   public static final String            MAXOCCUPANCY      = "maxoccupancy";
   public static final String            TYPE              = "type";
   public static final String            PROVIDEDEQUIPMENT = "providedequipment";
   public static final String            ADACOMPLIANT      = "adacompliant";
   public static final String            AVAILABILITY      = "availability";
   public static final String            SCHEDULEID        = "scheduleid";
   // Other data
   private ArrayList<Location>           data;
   private SQLDB                         sqldb;
   private int                           scheduleID;
   private LinkedHashMap<String, Object> fields;
   private LinkedHashMap<String, Object> wheres;

   public LocationDB(SQLDB sqldb, int scheduleID)
   {
      this.sqldb = sqldb;
      this.scheduleID = scheduleID;
   }

   @Override
   public ArrayList<Location> getData()
   {
      pullData();
      return data;
   }

   @Override
   public void saveData(Location data)
   {
      data.verify();
      data.setScheduleId(scheduleID);
      if (sqldb.doesLocationExist(data))
      {
         System.out.println("Editing data: location");
         editData(data);
      }
      else
      {
         System.out.println("Adding data: location");
         addData(data);
      }
   }

   private void pullData()
   {
      data = new ArrayList<Location>();
      ResultSet rs = sqldb.getSQLLocations(scheduleID);
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

         byte[] equipBuf = rs.getBytes("providedequipment");
         toAdd.setProvidedEquipment((Location.ProvidedEquipment) sqldb
               .deserialize(equipBuf));

         boolean adacompliant = rs.getBoolean("adacompliant");
         toAdd.setAdaCompliant(adacompliant);

         byte[] availBuf = rs.getBytes("availability");
         toAdd.setAvailability((WeekAvail) sqldb.deserialize(availBuf));

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

   private void addData(Location data)
   {
      fillMaps(data);
      sqldb.executeInsert(TABLENAME, fields);
   }

   private void editData(Location data)
   {
      fillMaps(data);
      sqldb.executeUpdate(TABLENAME, fields, wheres);
   }

   @Override
   public void removeData(Location data)
   {
      data.verify();
      fillMaps(data);
      sqldb.executeDelete(TABLENAME, wheres);
   }

   private void fillMaps(Location data)
   {
      // Set fields and values
      fields = new LinkedHashMap<String, Object>();
      fields.put(BUILDING, data.getBuilding());
      fields.put(ROOM, data.getRoom());
      fields.put(MAXOCCUPANCY, data.getMaxOccupancy());
      fields.put(TYPE, data.getType());
      fields.put(PROVIDEDEQUIPMENT,
            sqldb.serialize(data.getProvidedEquipment()));
      fields.put(ADACOMPLIANT, data.getAdaCompliant());
      fields.put(AVAILABILITY, sqldb.serialize(data.getAvailability()));
      fields.put(SCHEDULEID, scheduleID);
      // Where clause
      wheres = new LinkedHashMap<String, Object>();
      wheres.put(BUILDING, data.getBuilding());
      wheres.put(ROOM, data.getRoom());
      wheres.put(SCHEDULEID, scheduleID);
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
