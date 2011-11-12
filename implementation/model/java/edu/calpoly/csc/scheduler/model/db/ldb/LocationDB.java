package edu.calpoly.csc.scheduler.model.db.ldb;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Vector;

import edu.calpoly.csc.scheduler.model.db.*;
import edu.calpoly.csc.scheduler.model.db.cdb.Course;
import edu.calpoly.csc.scheduler.model.schedule.WeekAvail;

public class LocationDB extends AbstractDatabase<Location>
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
   public static final String NOTE = "note";

   public LocationDB(SQLDB sqldb, int scheduleID)
   {
      this.sqldb = sqldb;
      this.scheduleId = scheduleID;
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

   protected boolean exists (Location l)
   {
      return sqldb.doesLocationExist(l);
   }
   
   protected void fillMaps(Location data)
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
      fields.put(SCHEDULEID, scheduleId);
      fields.put(NOTE, data.getNote());
      // Where clause
      wheres = new LinkedHashMap<String, Object>();
      wheres.put(BUILDING, data.getBuilding());
      wheres.put(ROOM, data.getRoom());
      wheres.put(SCHEDULEID, scheduleId);
   }
   
   protected ResultSet getDataByScheduleId (int sid)
   {
      return this.sqldb.getSQLLocations(sid);
   }

   protected Location make (ResultSet rs)
   {
      Location toAdd = new Location();
      try
      {
         String bldg = rs.getString("building");
         toAdd.setBuilding(bldg);

         String room = rs.getString("room");
         toAdd.setRoom(room);

         int occupancy = rs.getInt(MAXOCCUPANCY);
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
         
         String note = rs.getString(NOTE);
         toAdd.setNote(note);
      }
      catch (SQLException e)
      {
         e.printStackTrace();
      }
      toAdd.verify();
      return toAdd;
   }
   
   protected String getTableName ()
   {
      return TABLENAME;
   }
}
