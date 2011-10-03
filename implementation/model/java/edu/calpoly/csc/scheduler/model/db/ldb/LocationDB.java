package edu.calpoly.csc.scheduler.model.db.ldb;

import java.util.Collection;
import java.lang.*;
import java.sql.*;
import java.util.*;

import edu.calpoly.csc.scheduler.model.db.SQLDB;
import edu.calpoly.csc.scheduler.model.db.cdb.Course;
import edu.calpoly.csc.scheduler.model.db.cdb.CourseDB;
import edu.calpoly.csc.scheduler.model.db.cdb.CourseDB.CourseDoesNotExistException;
import edu.calpoly.csc.scheduler.model.db.cdb.CourseDB.CourseExistsException;
import edu.calpoly.csc.scheduler.model.schedule.DayAvail;

/**
 *
 * The class representing the location database. 
 * 
 * @author Jan Lorenz Soliman 
 *
 **/

public class LocationDB extends Observable {

   /** Constant for the number of days in a week */
   private static final int NUMDAYSINWEEK = 7;
   /** Constant for the number of rows in a table */
   private static final int ROWS = 30;

	/** A collection of locations.  */
	protected ArrayList<Location> data;

	/** A collection of locations.  */
	protected ArrayList<Location> localData;

   /** A collection of available days. */
   protected Collection<AvailabilityTuple> availability;

   /**
    * Location database constructor
    */
   public LocationDB() {
      availability = new ArrayList<AvailabilityTuple>();
      //initializeAvailability(( ArrayList<DayAvail> )availability);
   }

   /** Class that holds a building, room, and list of availability.  */
	public class AvailabilityTuple {

      /** The location's building id */
		private String building;
      /** The location's room id*/
      private String room;
      /** A list for the location's availability*/
		private ArrayList<DayAvail> weekAvailability;

      /**
       *  Constructor for the AvailabilityTuple
       *
       *
       *  @param building The location's building id
       *  @param room The location's room id
       *  @param availability The location's availability
       *
       */
		public AvailabilityTuple(String building, String room, ArrayList<DayAvail> availability) {
			this.building = building;
         this.room = room;
			this.weekAvailability = availability;
		}

      /**
       *  Returns the building id
       *  @return The location's building id
       *
       **/
		public String getBuilding() {
			return building;
		}

      /**
       *  Returns the room id
       *  @return The location's room id
       *
       **/
      public String getRoom() {
         return room;
      }

      /**
       *  Returns the location's availability
       *  @return The location's availability
       *
       **/
		public ArrayList<DayAvail> getAvailability() {
			return weekAvailability;
		}

      /**
       *  Sets the AvailabilityTuple's availability
       *  @param avail The availability to set
       **/
      public void setAvailability(ArrayList<DayAvail> avail) {
         this.weekAvailability = avail;
      }

	}

   /**
    *  Initializes seven DayAvail objects and adds them to a list.
    *
    *  @param avail A list of day availability
    *  @return A list with seven days of availability.
    **/
   private ArrayList<DayAvail> initializeAvailability(ArrayList<DayAvail> avail) {
      ArrayList<DayAvail> returnVal = new ArrayList<DayAvail>();
      for (int i = 0; i < NUMDAYSINWEEK; i++ ) {
         DayAvail day = new DayAvail();
         returnVal.add(day);
      }
      return returnVal;
   }

	/**
	 * This exception is raised when a location already exists in the collection.
	 * 
	 * @author Cedric Wienold
	 *
	 */
	public static class LocationExistsException extends RuntimeException {
		public LocationExistsException() {
			super();
		}
	}

	/**
	 * This exception is raised when a location is queried but does not exist.
	 *  
	 * @author Cedric Wienold
	 *
	 */
	public static class LocationDoesNotExistException extends RuntimeException {
		public LocationDoesNotExistException() {
			super();
		}
	}

	/**
	 *  Returns the collection of the data.
    *  @return the collection of location
	 **/
	public ArrayList<Location> getData() {
		return data;
	}

        public void localToPermanent() {
            Iterator it = this.localData.iterator();

            while (it.hasNext()) {
                Location l = (Location) it.next();
                this.addLocation(l);
            }
        }

        public void permanentToLocal() {
            this.localData = new ArrayList<Location>(data);
        }

	/**
	 *  Returns the collection of the local data.
    *  @return the collection of location
	 **/
	public Collection<Location> getLocalData() {
      Iterator it = localData.iterator();

      while (it.hasNext()) {
         Location l = (Location) it.next();
         
      }
 
		return localData;
	}

	/**
	 *  Sets the local data in the locationDB
    *  <pre>
    *  pre:   //
    *         // The data field is not null
    *         //
    *         (data != null)
    *
    *  post:
    *         //
    *         // The data field now equals the parameter. 
    *         //
    *         (this.data == data)
    *  </pre>
    *
	 *  @param data The data to be set.
	 **/
	public void setLocalData(Collection<Location> data  ) {
		if (data != null) {
         this.localData = new ArrayList<Location>( data);
         this.setChanged();
         this.notifyObservers();
      }
      else {
         throw new NullPointerException();
      }
	}

	/**
	 *  Sets the data in the locationDB
    *  <pre>
    *  pre:   //
    *         // The data field is not null
    *         //
    *         (data != null)
    *
    *  post:
    *         //
    *         // The data field now equals the parameter. 
    *         //
    *         (this.data == data)
    *  </pre>
    *
	 *  @param data The data to be set.
	 **/
	public void setData(ArrayList<Location> data  ) {
		if (data != null) {
         this.data = data;
      }
      else {
         throw new NullPointerException();
      }
	}

	/** 
	 * addLocation Adds a given location to the location database 
	 *
	 * pre: 
	 *       //
	 *       // "location" must be a valid Location 
	 *       //
	 *       isValidLocation (location, locationdb);
	 *
	 * post: 
	 *      //
	 *      // Only "location" was added to "locationdb"
	 *      //
	 *      forall (location' in locationdb')
	 *         (location' in locationdb') iff ((location' = location) or (location' in locationdb));*
	 **/
	public void addLocation(Location l) {
		if (data != null) {
			if (data.contains(l)) {
				throw new LocationExistsException();
			}
		}
		// TODO FIX
		//SQLDB sqldb = Scheduler.schedDB;		
		String insert = "";
		insert += "( " + " '" + l.getBuilding() + "', '" + l.getRoom() + "', ";
		insert += l.getMaxOccupancy() + ", '" + l.getType()  +  "', " + l.isSmartRoom() + ", ";
		insert += l.hasLaptopConnectivity() + ", " + l.isADACompliant() + "," + l.hasOverhead() + ")";

		//sqldb.insertStmt("locations", insert);
		//LocationDB temp = sqldb.getLocationDB();
		//this.data = temp.getData();
      updateAvailability();
		setChanged();
		notifyObservers();
	}

	/** 
	 * Adds a given location to the local location database 
	 *
	 * pre: 
	 *       //
	 *       // "location" must be a valid Location 
	 *       //
	 *       isValidLocation (location, locationdb);
	 *
	 * post: 
	 *      //
	 *      // Only "location" was added to "locationdb"
	 *      //
	 *      forall (location' in locationdb')
	 *         (location' in locationdb') iff ((location' = location) or (location' in locationdb));*
	 **/
	public void addLocalLocation(Location l) {
		if (localData != null) {
			if (localData.contains(l)) {
				throw new LocationExistsException();
			}
		}
      localData.add(l);
		setChanged();
		notifyObservers();
	}


	/** 
	 * editLocation edits a given, already-existing Location in the database.
	 *
	 * 
	 * pre:
	 *       //
	 *       // "old" and "new" cannot be the same
	 *       //
	 *       location != nil;
	 *
	 *      &&
	 *
	 *      //
	 *      // "new" must be a valid location
	 *      //
	 *      isValidLocation (location, ldb);
	 *
	 * post:
	 *        //
	 *        // A location is in the output database iff it was already there
	 *        // to begin with, iff it was the new user added, and iff it is
	 *        // not the old instructor that was changed
	 *        //
	 *
	 *        forall (location':data)
	 *           (location' in locationdb') iff (((location == location') or (location in locationdb)) and (location != location'));
	 *
	 **/

	public void editLocation(Location location) {
		removeLocation(location);
		addLocation(location);
	}


	/** 
	 * Edits a given, already-existing Location in the local database.
	 *
	 * 
	 * pre:
	 *       //
	 *       // "old" and "new" cannot be the same
	 *       //
	 *       location != nil;
	 *
	 *      &&
	 *
	 *      //
	 *      // "new" must be a valid location
	 *      //
	 *      isValidLocation (location, ldb);
	 *
	 * post:
	 *        //
	 *        // A location is in the output database iff it was already there
	 *        // to begin with, iff it was the new user added, and iff it is
	 *        // not the old instructor that was changed
	 *        //
	 *
	 *        forall (location':data)
	 *           (location' in locationdb') iff (((location == location') or (location in locationdb)) and (location != location'));
	 *
	 **/

	public void editLocalLocation(Location location) {
		removeLocalLocation(location);
		addLocalLocation(location);
	}


	/** 
	 * removeLocation removes a given, already existing location from the
	 * location database
	 * 
	 * pre: 
	 *       //
	 *       // "location" must be in "locationdb"
	 *       //
	 *       (location in locationdb);
	 *
	 * post:
	 *        //
	 *        // The new database differs from teh old only in the absence of "location"
	 *        //
	 *        forall (location':Location)
	 *           (location' in locationdb) iff ((location' != location) and (location' in locationdb));
	 * 
	 *
	 **/

	public void removeLocation(Location l) {
		if (data != null) {
         System.out.println ("Data is " + data);
         System.out.println("Building is " + l.getBuilding() + " Room is " + l.getRoom());
			if (!data.contains(l)) {
				throw new LocationDoesNotExistException();
			}
		}
		System.out.println("In LocationDB.removeLocation");
		// TODO FIX
		//SQLDB sqldb = Scheduler.schedDB;
		String insert = "building = '" + l.getBuilding() + "' AND room = '" + l.getRoom() + "'";
		//sqldb.open();
		//sqldb.removeStmt("locations", insert);
		//LocationDB temp = sqldb.getLocationDB();
		//this.data = temp.getData();
		//sqldb.close();
      //updateAvailability();
		setChanged();
		notifyObservers();
	}

	/** 
	 * Removes a given, already existing location from the
	 * local location database
	 * 
	 * pre: 
	 *       //
	 *       // "location" must be in "locationdb"
	 *       //
	 *       (location in locationdb);
	 *
	 * post:
	 *        //
	 *        // The new database differs from teh old only in the absence of "location"
	 *        //
	 *        forall (location':Location)
	 *           (location' in locationdb) iff ((location' != location) and (location' in locationdb));
	 * 
	 *
	 **/

	public void removeLocalLocation(Location l) {
		if (localData != null) {
         System.out.println ("Data is " + localData);
         System.out.println("Building is " + l.getBuilding() + " Room is " + l.getRoom());
			if (!localData.contains(l)) {
				throw new LocationDoesNotExistException();
			}
		}


      Iterator iterator = localData.iterator();
      int removeInd = -1;
      for (int i = 0; iterator.hasNext(); i++) {
         Location it = (Location) iterator.next();
         if (l.equals(it)) {
            removeInd = i;
            break;
         }
      }

      if (removeInd != -1) {
         
         localData.remove(removeInd);
         
      }


      updateAvailability();
		setChanged();
		notifyObservers();
	}

	/** 
	 * isValidLocation checks if the location is in the database. 
	 *
	 * pre:
	 *       //
	 *       // none
	 *       // 
	 *
	 *
	 * post:
	 *        //
	 *        // none
	 *        //
	 *
	 * **/
	/*protected boolean isValidLocation(Location l, LocationDB[] ldb) {
		return false;

	}*/

	/** 
	 *
	 *  Gets the location based on its string representation 
	 *
    *  <pre>
    *  pre:   //
    *         //  location contains the '-' character
    *         //
    *         (location.contains('-') )
    *
    *  post:
    *         //
    *         //  the returned location's toString() method is equal to
    *         //  the parameter.
    *         //
    *         ( returnVal.toString().equals(location)  ) 
    *  </pre>
	 *
	 **/
   public Location getLocation(String location) {
           String[] tokens = location.split("-");
           String building = tokens[0];
           String room = tokens[1];
           Iterator iterator;
           if (data != null) {
               iterator = data.iterator();
           }
           else {
                return null;
           }
         while (iterator.hasNext()) {
            Location l = (Location) iterator.next();
            if (l.getBuilding().contains(building) && l.getRoom().contains(room )) {
            return l;
            }
      }

      return null;
   }


	/** 
	 *
	 *  Gets the local location based on its string representation 
	 *
    *  <pre>
    *  pre:   //
    *         //  location contains the '-' character
    *         //
    *         (location.contains('-') )
    *
    *  post:
    *         //
    *         //  the returned location's toString() method is equal to
    *         //  the parameter.
    *         //
    *         ( returnVal.toString().equals(location)  ) 
    *  </pre>
	 *
	 **/
   public Location getLocalLocation(String location) {
           String[] tokens = location.split("-");
           String building = tokens[0];
           String room = tokens[1];
           Iterator iterator;
           if (localData != null) {
               iterator = localData.iterator();
           }
           else {
                return null;
           }
         while (iterator.hasNext()) {
            Location l = (Location) iterator.next();
            if (l.getBuilding().equals(building) && l.getRoom().equals(room )) {

            return l;
            }
      }

      return null;
   }

	/** 
	 *
	 * isValidLocationDB checks if the location database is valid.
	 *
	 * pre:
	 *       //
	 *       // none
	 *       //
	 *
	 * post: 
	 *
	 *      //
	 *      // none
	 *      //
	 *
	 **/
	/*protected boolean isValidLocationDB(LocationDB[] ldb) {
		return false;
	}*/




   /**
    *  Changes the availability stored in the database.
    *
    *
    *  @param data A list of day availability
    *  @param location The method will change the specified location's 
    *  availability
    *
    **/
   public void changeAvailability(ArrayList<DayAvail> data, Location location) {
      String building = location.getBuilding();
      String room = location.getRoom();
      Iterator iterator = availability.iterator();
      while (iterator.hasNext()) {
         AvailabilityTuple row = (AvailabilityTuple) iterator.next();
         if (row.getBuilding() == building && row.getRoom() == room) {
            row.setAvailability(data);
            break;
         }
      }

      availability.add(
      new AvailabilityTuple(building, room, data));
      updateAvailability();
   }


   /**
    *  Returns the specified location's availability 
    *
    *  @param location The method will change the specified location's 
    *  availability
    *
    *  @return A list representing a location's availability
    **/
   public ArrayList<DayAvail> getAvailability(Location location) {

      String building = location.getBuilding();
      String room = location.getRoom();
      Iterator iterator = availability.iterator();

      while (iterator.hasNext()) {
         AvailabilityTuple row = (AvailabilityTuple) iterator.next();
         if (row.getBuilding().equals(building) && row.getRoom().equals(room)) {
            return row.getAvailability();
         }
      }

      return initializeAvailability(new ArrayList<DayAvail>());
   }

   /**
    *  Updates the availability of the location objects in the local database.
    *
    **/
   public void updateAvailability() {
      Iterator iterator = localData.iterator();

      while (iterator.hasNext()) {
         Location location = (Location) iterator.next();

         ArrayList<DayAvail> avail = getAvailability(location);

         for (int i = 0; i < NUMDAYSINWEEK; i++ ) {
            DayAvail day = avail.get(i);

            for (int j = 0; j < ROWS; j++) {
               edu.calpoly.csc.scheduler.model.db.Time start = getStartTime(j);
               edu.calpoly.csc.scheduler.model.db.Time end = getEndTime(j);
               try {
                  if (!day.isFree(start, end)) {
                     
                     location.setBusy(i, start, end);
                  }
               }
               catch (Exception e) {
                  System.err.println("Error updating availability.");
                  e.printStackTrace();
               }
            }
         }
      }

   }


	/** 
	 *
	 * Converts the inputted value into a corresponding Time
    * object rounded to the earliest half-hour.
	 *
    *  <pre>
    *  pre:   //
    *         //  number is between 0 and 30
    *         //
    *         (number >= 0 && number < 30)
    *
    *  post:
    *         //
    *         //  the returned time corresponds to the number
    *         //
    *         (time.hour = ((number / 2) + 7) && time.minute = (number % 2) * 30 )
    *  </pre>
	 *
	 **/
   public edu.calpoly.csc.scheduler.model.db.Time getStartTime(int number ) {
       if (number >= 30 || number < 0) {
           throw new NumberInvalidException();
       }
       int start = 7;
       int hour = (number / 2) + 7; 
       int minute = (number % 2) * 30; 
       try { 
         return new edu.calpoly.csc.scheduler.model.db.Time(hour, minute);
       }
       catch (Exception e) {
         return null;
       }
   }


	/** 
	 *
	 * Converts the inputted value into a corresponding Time
    * object rounded up to the latest half-hour.
	 *
    *  <pre>
    *  pre:   //
    *         //  number is between 0 and 30
    *         //
    *         (number >= 0 && number < 30)
    *
    *  post:
    *         //
    *         //  the returned time corresponds to the number
    *         //
    *         (time.hour = ((number / 2) + 7) && time.minute = (number % 2) * 30 )
    *  </pre>
	 *
	 **/
   public edu.calpoly.csc.scheduler.model.db.Time getEndTime(int number) {
       if (number >= 30 || number < 0) {
           throw new NumberInvalidException();
       }
       int start = 7;
       int hour = (number / 2) + 7; 
       int minute = (number % 2) * 30; 
       if ((minute + 30) == 60) {
           try {
               return new edu.calpoly.csc.scheduler.model.db.Time(hour + 1, 0);
           }
           catch (Exception e) {
               return null;
           }
       }
       else {
           try {
               return new edu.calpoly.csc.scheduler.model.db.Time(hour, minute + 30);
           }
           catch (Exception e) {
               return null;
           }
       }
   }

	/**
	 * This exception is raised when the inputted number for converting
	 * to time is not correct.
    * 
	 * @author Jan Lorenz Soliman
	 *
	 */
	public static class NumberInvalidException extends RuntimeException {
      /**
       * Calls the exception constructor.
       */
		public NumberInvalidException() {
			super();
		}
	}

   /**
    * Sets local data to a given collection of data, and notifies any/all 
    * observers of the change.
    *
    * @param data Data to set local data to
    *
    * By: Eric Liebowitz (22jul10)
    */
   public void setLocalWithThisFromFile (Collection<Location> data)
   {
      this.setLocalData(data);
      this.setChanged();
      this.notifyObservers();
   }
}
