package scheduler.db.locationdb;

import java.io.Serializable;

import scheduler.db.coursedb.*;
import scheduler.db.Time;
import scheduler.generate.Week;
import scheduler.generate.WeekAvail;

/**
 * This class contains the place and informaton about a location.
 * 
 * @author Cedric Wienold
 */

public class Location implements Serializable
{
   /** Represents a location who's identity is not yet known */
   public static Location TBA = new Location (-1, -1);

	/**
	 * This exception class is thrown when the day of week is not in the
	 * Sunday to Saturday format.
	 * 
	 * @author Cedric Wienold
	 *
	 */
	public class InvalidDayOfWeekException extends RuntimeException {
		public InvalidDayOfWeekException() {
			super();
		}
	}

	/**
	 * This exception is raised when invalid time inputs are entered.
	 * 
	 * @author Cedric Wienold
	 *
	 */
	public class InvalidTimeInputException extends RuntimeException {
		public InvalidTimeInputException() {
			super();
		}
	}

	/**
	 * This class contains the equipment provided at a location.
	 */
	private class ProvidedEquipment implements Serializable
   {
		public boolean hasOverhead;
		public boolean isSmartRoom;
		public boolean hasLaptopConnectivity;
	}

	/**
	 * Building number of location.
	 */
	private String building;

	/**
	 * Room number of location.
	 */
	private String room;

	/**
	 * Maximum occupancy of this location.
	 */
	private int maxOccupancy;

	/**
	 * Type of this location.
	 */
	private String type;

	/**
	 * Provided equuipment in this location.
	 */
	private ProvidedEquipment providedEquipment;

	/**
	 * Whether this location is compliant to those with disabilities.
	 */
	private boolean adaCompliant;

	/**
    * Represents a location's availabilty throughout the week.
	 */
   private WeekAvail availability;

	/**
	 * This constructor creates a location at a particular room and building.
	 * 
	 * @param bldg the building number.
	 * @param room the room number.
	 */
	public Location(int bldg, int room) 
   {
		this.building = Integer.toString(bldg);
		this.room = Integer.toString(room);
	
      this.availability = new WeekAvail ();
	}

   public int getWeekSize() {
      return availability.size();
   }

	/**
	 * Creates a location which is a copy of a given location. Note that a new
    * availability object is created (thus, not copied). 
	 * 
	 * Written by: Eric Liebowitz
	 *
	 * @param l Location to copy
	 */
	   public Location (Location l)
	   {
	      this.building = l.building;
	      this.room = l.room;
	      this.maxOccupancy = l.maxOccupancy;
	      this.providedEquipment = new ProvidedEquipment();
	      this.providedEquipment.hasOverhead = l.providedEquipment.hasOverhead;
	      this.providedEquipment.isSmartRoom = l.providedEquipment.isSmartRoom;
	      this.providedEquipment.hasLaptopConnectivity = 
	         l.providedEquipment.hasLaptopConnectivity;
	      this.adaCompliant = l.adaCompliant;
	      this.type = l.type;

         this.availability = new WeekAvail ();
	   }

	/**
	 * This constructor will make a location at a building and room number.
	 * 
	 * @param building
	 * @param room
	 */
	public Location(String building, String room) {
		this.building = building;
		this.room = room;
		this.providedEquipment = new ProvidedEquipment();

      this.availability = new WeekAvail();
	}

	/**
	 * This constructor will make a location at a building and room number
	 * with other fields.
	 * 
	 * @param building
	 * @param room
	 */
	public Location(String building, String room, int maxOccupancy,String type , boolean disabilities, 
			boolean smartroom, boolean laptop, boolean overhead ) {
		this.building = building;
		this.room = room;
		this.maxOccupancy = maxOccupancy; 
		this.providedEquipment = new ProvidedEquipment();
		providedEquipment.hasOverhead = overhead;
		providedEquipment.isSmartRoom = smartroom;
		providedEquipment.hasLaptopConnectivity = laptop;
		adaCompliant = disabilities;
		this.type = type;

      this.availability = new WeekAvail ();
	}

	/**
	 * Returns true if the argument is not null, is an instance of the
	 * Location class, and its building and room numbers are the same as this
	 * object's.
	 * 
	 * @param other the object to compare with
	 * @return whether the two objects are equal
	 */
	public boolean equals(Object other) {
		if (other == null) return false;
		if (!(other instanceof Location)) return false;

		Location obj = (Location)other;

		return (this.building.equals(obj.building) &&
              this.room.equals(obj.room));
	}

   /**
    * Returns this Location's hash code (a combination of its bldg and room 
    * hash codes)
    */
   public int hashCode ()
   {
      return this.building.hashCode() + this.room.hashCode();
   }

	/**
	 * Returns the building number of this location.
	 * 
	 * @return the building number of this location.
	 */
	public String getBuilding() {
		return this.building;
	}

	/**
	 * Returns the maximum occupancy of this location.
	 * 
	 * @return the maximum occupancy of this location.
	 */
	public int getMaxOccupancy() {
		return this.maxOccupancy;
	}

	/**
	 * Returns the room number of this location.
	 * 
	 * @return the room number of this location.
	 */
	public String getRoom() {
		return this.room;
	}

	public String getType(){
		return this.type;
	}

	/**
	 * This method returns whether this room has laptop connectivity.
	 * 
	 * @return whether this room has laptop connectivity.
	 */
	public boolean hasLaptopConnectivity() {
		return providedEquipment.hasLaptopConnectivity;
	}

	/**
	 * This method returns whether this room has an overhead.
	 *
	 * @return whether this room has an overhead.
	 */
	public boolean hasOverhead() {
		return this.providedEquipment.hasOverhead;
	}

	/**
	 * This method returns whether this room is disabled-accessible.
	 * 
	 * @return whether this rom is disabled-accessible.
	 */
	public boolean isADACompliant() {
		return this.adaCompliant;
	}

	/**
	 * This method will tell whether this location is availble during the given
	 * time slot.
	 * 
	 * @param dayOfWeek The day (0 = Sun; 6 = Sat)
	 * @param s The start time
	 * @param e The end time
    *
	 * @return True if the given span of time is available. False otherwise.
    * 
    * Written by: Eric Liebowitz
	 */
	public boolean isAvailable (int dayOfWeek, Time s, Time e) 
   {
      return this.availability.isFree(s, e, dayOfWeek);
	}

   /**
    * Determines whether a location is available during the given span of time,
    * over the given week of days.
    *
    * @param week The week of days that must be free
    * @param s The start time
    * @param e The end time
    *
    * @return True if the time between "s" and "e" is free on all days of "week"
    *
    * Written by: Eric Liebowitz
    */
   public boolean isAvailable (Week week, Time s, Time e)
   {
      return this.availability.isFree(s, e, week);
   }

	/**
    * This method will take in a day, start time, and end time and set that
    * time interval as busy for this location.
    * 
	 * @param dayOfWeek The day (0 = Sun; 6 = Sat)
	 * @param s The start time
	 * @param e The end time
    *
    * Written by: Eric Liebowitz 
    */
   public void setBusy(int dayOfWeek, Time s, Time e) 
   {
      this.availability.book(s, e, dayOfWeek);
   }

   /**
    * Books this location for a given time over a given span of days (Week).
    *
    * @param week The span of days to book
    * @param s The start time
    * @param e The end time
    *
    * @return if the time was booked, and thus free beforehand. 
    *
    * Written by: Eric Liebowitz
    */
   public boolean setBusy (Time s, Time e, Week week)
   {
      return this.availability.book(s, e, week);
   }

	/**
	 * Returns whether this location is a lab room.
	 * @return whether this location is a lab room.
	 */
	public boolean isLab() {
		return (this.type.equalsIgnoreCase("Lab"));
	}

	/**
	 * Returns whether this location is a lecture room.
	 * @return whether this location is a lecture room.
	 */
	public boolean isLecture() {
		return (this.type.equalsIgnoreCase("Lecture"));
	}

	/**
	 * This method returns whether this is a smart room.
	 * 
	 * @return whether this is a smart room.
	 */
	public boolean isSmartRoom() {
		return providedEquipment.isSmartRoom;
	}

	/**
	 * Returns whether the given location is valid.
	 */
	public boolean isValidLocation (Location location) {
		return false;
	}

	/**
	 * Returns whether the currect class is a valid LocationDB.
	 */
	public boolean isValidLocationDB () {
		return false;
	}



	/**
	 * Returns the building-dash-room string representation of this location.
	 * 
	 * @return the string representation of this building
	 */
	public String toString() {
		return new String(this.building + " - " + this.room);
	}

   /**
    * Determines whether this location provides the required equipment for a
    * given course, and is of a compatible type.
    *
    * @param c The course to provide for
    *
    * @return True if this location all the course's required equipment. False 
    *         otherwise.
    *
    * Written by: Eric Liebowitz
    */
   public boolean providesFor (Course c)
   {
      boolean r = true;
      RequiredEquipment cReqs = c.getRequiredEquipment();

      /*
       * Only check for a provision if the course requires it.
       */
      if (cReqs.isSmartroom())
      {
         //System.out.println (c + " has smart room");
         r &= this.providedEquipment.isSmartRoom;
         //System.out.println ("Does location: " + r);
      }
      if (cReqs.hasOverhead())
      {
         //System.out.println (c + " has overhead");
         r &= this.providedEquipment.hasOverhead;
         //System.out.println ("Does location: " + r);
      }
      if (cReqs.hasLaptopConnectivity())
      {
         //System.out.println (c + " has laptop");
         r &= this.providedEquipment.hasLaptopConnectivity;
         //System.out.println ("Does location: " + r);
      }
      r &= this.type.equals(c.getCourseType());

      return r;
   }
}
