package edu.calpoly.csc.scheduler.model.db.ldb;

import java.io.Serializable;

import edu.calpoly.csc.scheduler.model.db.*;
import edu.calpoly.csc.scheduler.model.db.cdb.Course;
import edu.calpoly.csc.scheduler.model.schedule.*;

/**
 * This class contains the place and informaton about a location.
 * 
 * @author Cedric Wienold
 */

public class Location extends DbData implements Serializable
{
   private static final long serialVersionUID = 42;

   /**
    * This class contains the equipment provided at a location.
    */
   public class ProvidedEquipment implements Serializable
   {
      private static final long serialVersionUID = 42;
      public boolean            hasOverhead = false;
      public boolean            isSmartRoom = false;
      public boolean            hasLaptopConnectivity = false;
   }

   /**
    * Building number of location.
    */
   private String            building;

   /**
    * Room number of location.
    */
   private String            room;

   /**
    * Maximum occupancy of this location.
    */
   private Integer           maxOccupancy;

   /**
    * Type of this location.
    */
   private String            type;

   /**
    * Provided equipment in this location.
    */
   private ProvidedEquipment providedEquipment = new ProvidedEquipment();

   /**
    * Whether this location is compliant to those with disabilities.
    */
   private Boolean           adaCompliant;

   /**
    * Represents a location's availabilty throughout the week.
    */
   private WeekAvail         availability = new WeekAvail();

   /**
    * Quarter this location is a part of
    */
   private String            quarterId;
   /**
    * Schedule this location is a part of
    */
   private Integer           scheduleId;

   /**
    * Default constructor
    */
   public Location()
   {
   }

   /**
    * This constructor creates a location at a particular room and building.
    * 
    * @param bldg
    *           the building number.
    * @param room
    *           the room number.
    */
   public Location(int bldg, int room)
   {
      this.building = Integer.toString(bldg);
      this.room = Integer.toString(room);

      this.availability = new WeekAvail();
   }

   /**
    * Creates a location which is a copy of a given location. Note that a new
    * availability object is created (thus, not copied).
    * 
    * Written by: Eric Liebowitz
    * 
    * @param l
    *           Location to copy
    */
   public Location(Location l)
   {
      this.building = l.building;
      this.room = l.room;
      this.maxOccupancy = l.maxOccupancy;
      this.providedEquipment = new ProvidedEquipment();
      this.providedEquipment.hasOverhead = l.providedEquipment.hasOverhead;
      this.providedEquipment.isSmartRoom = l.providedEquipment.isSmartRoom;
      this.providedEquipment.hasLaptopConnectivity = l.providedEquipment.hasLaptopConnectivity;
      this.adaCompliant = l.adaCompliant;
      this.type = l.type;

      this.availability = new WeekAvail();
   }

   /**
    * This constructor will make a location at a building and room number.
    * 
    * @param building
    * @param room
    */
   public Location(String building, String room)
   {
      this.building = building;
      this.room = room;
      this.providedEquipment = new ProvidedEquipment();

      this.availability = new WeekAvail();
   }

   /**
    * This constructor will make a location at a building and room number with
    * other fields.
    * 
    * @deprecated Stop using this
    */
   public Location(String building, String room, int maxOccupancy, String type,
         boolean disabilities, boolean smartroom, boolean laptop,
         boolean overhead)
   {
      this.building = building;
      this.room = room;
      this.maxOccupancy = maxOccupancy;
      this.providedEquipment = new ProvidedEquipment();
      providedEquipment.hasOverhead = overhead;
      providedEquipment.isSmartRoom = smartroom;
      providedEquipment.hasLaptopConnectivity = laptop;
      adaCompliant = disabilities;
      this.type = type;

      this.availability = new WeekAvail();
   }

   /**
    * Returns true if the argument is not null, is an instance of the Location
    * class, and its building and room numbers are the same as this object's.
    * 
    * @param other
    *           the object to compare with
    * @return whether the two objects are equal
    */
   public boolean equals(Object other)
   {
      if (other == null) return false;
      if (!(other instanceof Location)) return false;

      Location obj = (Location) other;

      return (this.building.equals(obj.building) && this.room.equals(obj.room));
   }

   /**
    * Returns this Location's hash code (a combination of its bldg and room hash
    * codes)
    */
   public int hashCode()
   {
      return this.building.hashCode() + this.room.hashCode();
   }

   /**
    * Returns the building number of this location.
    * 
    * @return the building number of this location.
    */
   public String getBuilding()
   {
      return this.building;
   }

   /**
    * Returns the maximum occupancy of this location.
    * 
    * @return the maximum occupancy of this location.
    */
   public int getMaxOccupancy()
   {
      return this.maxOccupancy;
   }

   /**
    * Returns the room number of this location.
    * 
    * @return the room number of this location.
    */
   public String getRoom()
   {
      return this.room;
   }

   public String getType()
   {
      return this.type;
   }

   /**
    * Returns the quarterId
    * 
    * @return the quarterId
    */
   public String getQuarterId()
   {
      return quarterId;
   }

   /**
    * Sets the quarterId to the given parameter.
    * 
    * @param quarterId
    *           the quarterId to set
    */
   public void setQuarterId(String quarterId)
   {
      this.quarterId = quarterId;
   }

   /**
    * Returns the scheduleId
    * 
    * @return the scheduleId
    */
   public Integer getScheduleId()
   {
      return scheduleId;
   }

   /**
    * @return the providedEquipment
    */
   public ProvidedEquipment getProvidedEquipment()
   {
      return providedEquipment;
   }

   /**
    * @return the adaCompliant
    */
   public Boolean getAdaCompliant()
   {
      return adaCompliant;
   }

   /**
    * @param building
    *           the building to set
    */
   public void setBuilding(String building)
   {
      this.building = building;
   }

   /**
    * @param room
    *           the room to set
    */
   public void setRoom(String room)
   {
      this.room = room;
   }

   /**
    * @param maxOccupancy
    *           the maxOccupancy to set
    */
   public void setMaxOccupancy(Integer maxOccupancy)
   {
      this.maxOccupancy = maxOccupancy;
   }

   /**
    * @param type
    *           the type to set
    */
   public void setType(String type)
   {
      this.type = type;
   }

   /**
    * @param providedEquipment
    *           the providedEquipment to set
    */
   public void setProvidedEquipment(ProvidedEquipment providedEquipment)
   {
      this.providedEquipment = providedEquipment;
   }

   /**
    * @param adaCompliant
    *           the adaCompliant to set
    */
   public void setAdaCompliant(Boolean adaCompliant)
   {
      this.adaCompliant = adaCompliant;
   }

   /**
    * Sets the scheduleId to the given parameter.
    * 
    * @param scheduleId
    *           the scheduleId to set
    */
   public void setScheduleId(Integer scheduleId)
   {
      this.scheduleId = scheduleId;
   }

   /**
    * This method returns whether this room has laptop connectivity.
    * 
    * @return whether this room has laptop connectivity.
    */
   public boolean hasLaptopConnectivity()
   {
      return providedEquipment.hasLaptopConnectivity;
   }

   /**
    * This method returns whether this room has an overhead.
    * 
    * @return whether this room has an overhead.
    */
   public boolean hasOverhead()
   {
      return this.providedEquipment.hasOverhead;
   }

   /**
    * This method returns whether this room is disabled-accessible.
    * 
    * @return whether this rom is disabled-accessible.
    */
   public boolean isADACompliant()
   {
      return this.adaCompliant;
   }

   /**
    * @return the availability
    */
   public WeekAvail getAvailability()
   {
      return availability;
   }

   /**
    * @param availability
    *           the availability to set
    */
   public void setAvailability(WeekAvail availability)
   {
      this.availability = availability;
   }

   /**
    * This method will tell whether this location is availble during the given
    * time slot.
    * 
    * @param dayOfWeek
    *           The day (0 = Sun; 6 = Sat)
    * @param s
    *           The start time
    * @param e
    *           The end time
    * 
    * @return True if the given span of time is available. False otherwise.
    * 
    *         Written by: Eric Liebowitz
    */
   public boolean isAvailable(Day dayOfWeek, Time s, Time e)
   {
      return this.availability.isFree(s, e, dayOfWeek);
   }

   /**
    * Determines whether a location is available during the given span of time,
    * over the given week of days.
    * 
    * @param week
    *           The week of days that must be free
    * @param s
    *           The start time
    * @param e
    *           The end time
    * 
    * @return True if the time between "s" and "e" is free on all days of "week"
    * 
    *         Written by: Eric Liebowitz
    */
   public boolean isAvailable(Week week, Time s, Time e)
   {
      return this.availability.isFree(s, e, week);
   }

   /**
    * Determines whether a location is available during the given span of time,
    * over the given week of days.
    * 
    * @param week
    *           The week of days that must be free
    * @param tr
    *           TimeRange to check
    * 
    * @return True if the TimeRange is free on all days of "week"
    */
   public boolean isAvailable(Week week, TimeRange tr)
   {
      return this.availability.isFree(tr, week);
   }

   /**
    * This method will take in a day, start time, and end time and set that time
    * interval as busy for this location.
    * 
    * @param dayOfWeek
    *           The day (0 = Sun; 6 = Sat)
    * @param s
    *           The start time
    * @param e
    *           The end time
    * 
    *           Written by: Eric Liebowitz
    */
   public boolean book(boolean b, Day dayOfWeek, Time s, Time e)
   {
      return this.availability.book(b, s, e, dayOfWeek);
   }

   /**
    * Books this location for a given time over a given span of days (Week).
    * 
    * @param week
    *           The span of days to book
    * @param s
    *           The start time
    * @param e
    *           The end time
    * 
    * @return if the time was booked, and thus free beforehand.
    * 
    *         Written by: Eric Liebowitz
    */
   public boolean book(boolean b, Week week, Time s, Time e)
   {
      return this.availability.book(b, s, e, week);
   }

   public boolean book(boolean b, Week week, TimeRange tr)
   {
      return this.availability.book(b, week, tr);
   }

   /**
    * Returns whether this location is a lab room.
    * 
    * @return whether this location is a lab room.
    */
   public boolean isLab()
   {
      return (this.type.equalsIgnoreCase("Lab"));
   }

   /**
    * Returns whether this location is a lecture room.
    * 
    * @return whether this location is a lecture room.
    */
   public boolean isLecture()
   {
      return (this.type.equalsIgnoreCase("Lecture"));
   }

   /**
    * This method returns whether this is a smart room.
    * 
    * @return whether this is a smart room.
    */
   public boolean isSmartRoom()
   {
      return providedEquipment.isSmartRoom;
   }

   /**
    * Returns the building-dash-room string representation of this location.
    * 
    * @return the string representation of this building
    */
   public String toString()
   {
      return new String("BLDG: " + this.building + " Room: " + this.room);
   }

   /**
    * Determines whether this location provides the required equipment for a
    * given course, and is of a compatible type.
    * 
    * @param c
    *           The course to provide for
    * 
    * @return true if this location has enough seats to support the given
    *         course.
    */
   public boolean providesFor(Course c)
   {
      boolean r = false;
      if (c.getEnrollment() <= this.getMaxOccupancy())
      {
         r = true;
      }
      return false;
   }

   public Location getCannedData()
   {
      Location l = new Location();
      l.setBuilding("14");
      l.setRoom("256");
      l.setMaxOccupancy(123);
      l.setType("LEC");
      ProvidedEquipment e = new ProvidedEquipment();
      e.hasLaptopConnectivity = true;
      e.hasOverhead = false;
      e.isSmartRoom = true;
      l.setProvidedEquipment(e);
      l.setAdaCompliant(true);
      l.setAvailability(new WeekAvail());
      l.setQuarterId("w2011");
      l.setScheduleId(1);
      return l;
   }

   /**
    * Verifies that the vital fields of this Object (i.e. those essential for
    * generation of identification in a DB) are not null. "Vital" fields are as
    * follows:
    * 
    * <ul>
    * <li>adaCompliant</li>
    * <li>building</li>
    * <li>maxOccupancy</li>
    * <li>providedEquipment</li>
    * <li>quarterId</li>
    * <li>room</li>
    * <li>scheduleId</li>
    * <li>type</li>
    * </ul>
    * 
    * @throws NullDataException
    *            if any field vital to generation or storage is null
    * 
    * @see edu.calpoly.csc.scheduler.model.db.DbData#verify()
    */
   public void verify() throws NullDataException
   {
      if (adaCompliant == null || building == null || maxOccupancy == null
            || providedEquipment == null || quarterId == null || room == null
            || scheduleId == null || type == null)
      {
         throw new NullDataException();
      }

   }
}
