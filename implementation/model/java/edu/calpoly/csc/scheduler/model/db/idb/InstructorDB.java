/*
 * NOTE TO AUTHOR (LORENZ):
 *
 * I have commented out the call to "updatePreferences()" in "getData()" and
 * "getLocalData()", as I don't think they're necessary. Leaving them there
 * was causing nasty null-pointer exception errors whenever there were 
 * instructors in the local DB not present in the global DB. See the comment 
 * above "getLocalData" for more details about why I don't like the 
 * "updatePreferences" method. 
 *
 * In the "changePreferences" and "changeTimePreferences", I've added calls to
 * two new methods "changeLocalInstructorCPrefs" and 
 * "changeLocalInstructorTPrefs" (respectively). I couldn't find a place where
 * you distinguished between changed local vs. global preferences, and the
 * local instructor GUI only alters the "preferences" instance variable within
 * the IDB. These methods change the preferences of instructor located in the 
 * "localData" vector. I'm of the opinion that associate the preferences with
 * the Instructor object they apply to will make future improvements/changes
 * easier to handle.
 *
 * Feel free to call/text/contact-me-in-some-way at eliebowi@calpoly.edu, or
 * call/text me at 818.530.3799 if you have any questions about stuff I've done
 * here. I treaded as lightly as I could, and left most all of the rest of the
 * code here untouched. 
 *
 *  - Eric
 */
package edu.calpoly.csc.scheduler.model.db.idb;

import java.util.Collection;
import java.lang.*;
import java.sql.*;
import java.util.*;
import java.io.PrintStream;

import edu.calpoly.csc.scheduler.model.db.SQLDB;
import edu.calpoly.csc.scheduler.model.db.cdb.Course;
import edu.calpoly.csc.scheduler.model.schedule.*;

/**
 *
 * This class will be the model interface to the MySQL database containing
 * instructor information.
 * 
 * This class allows its users to add, modify, and remove instructors from it.
 * One may also retrieve a collection of instructors in the database.
 * 
 * @author Jan Lorenz Soliman and Cedric Wienold
 *
 **/
public class InstructorDB extends Observable {
    /** Constant consisting of the count of rows in a typical day.*/
    private static final int ROWS = 30;

	/** A collection of Instructors */
	protected Vector<Instructor> data;

	/** A collection of Instructors */
	protected Vector<Instructor> localData;

	/** A collection of Instructors with course preferences */
	//public Vector<PreferenceTuple> preferences;

	/** A collection of Instructors with time preferences */
	public Vector<TimePreferenceTuple> timepreferences;

	/**
	 * Here's a constructor that, in fact, does nothing.
	 */
	public InstructorDB() 
   {
      /*
       * This is a bad idea! You actually can call this constructor in 
       * SQLDB.java, and the data ends up as null! Bad!
       */
	}

	/**
	 * Exception class for adding when instructor already exists.
	 * 
	 * @author Cedric Wienold
	 *
	 */
	public static class DuplicateInstructorException extends RuntimeException {
		public DuplicateInstructorException() {
			super();
		}
	}

	/**
	 * This exception is raised when a queried instructor does not exist.
	 * 
	 * @author Cedric Wienold
	 *
	 */
	public static class InstructorDoesNotExistException extends RuntimeException {
		public InstructorDoesNotExistException() {
			super();
		}
	}


	/**
	 * Exception class for invalid instructor input.
	 * 
	 * @author Cedric Wienold
	 *
	 */
	public static class InvalidInstructorException extends RuntimeException {
		public InvalidInstructorException() {
			super();
		}
	}

	/**
	 * Class to house course preference and id.
    *
	 * @author Jan Lorenz Soliman
	 *
	 * @param <E> Unused
	 */
   /*
    * Note: You could probably do this with a hash...
    *
    *  - Eric
    */
	public class PreferenceTuple<E> {

		private String id;
		private ArrayList<CoursePreference> prefList;

		public PreferenceTuple(String id,  ArrayList<CoursePreference> prefList) {
			this.id = id;
			this.prefList = prefList;
		}

		public String getId() {
			return id;
		}

		public ArrayList<CoursePreference> getPrefList() {
			return prefList;
		}

	}

	/**
	 * Class to house time preference and id.
	 * 
	 * @author Cedric Wienold
	 *
	 */
	public class TimePreferenceTuple {

		private String id;
		private ArrayList<TimePreference> prefList;

		public TimePreferenceTuple(String id,  ArrayList<TimePreference> prefList) {
			this.id = id;
			this.prefList = prefList;
		}

		public String getId() {
			return id;
		}

		public ArrayList<TimePreference> getPrefList() {
			return prefList;
		}

	}
	
	/**
	 * This constructor builds an instructordb with predefined data.
	 * @param data predefined list of instructors.
	 */
	public InstructorDB(  Vector<Instructor> data ) 
   {
      System.err.println ("Making IDB w/ data " + data);
		this.data = data;
      this.localData = new Vector<Instructor>(data);
   }

	/**
	 * Adds a given instructor to the instructor database.
	 * 
	 * pre:
	 * 		// i must be a valid instructor
	 * 		(i != null && i.isValidInstructor());
	 * 
	 * 		&&
	 * 
	 * 		// i is not in the database
	 * 		(i not in InstructorDB.data)
	 * 
	 * post:
	 * 		// Only this instructor has been added to the database
	 * 		(forall (Instructor inst) (inst' in InstructorDB'.data)
	 * 			iff ( (inst == i) or
	 * 				(inst in InstructorDB.data)))
	 * 
	 * @param	i		the new instructor to add to the database
	 **/
	public void addInstructor (Instructor i) 
      throws InvalidInstructorException, DuplicateInstructorException, 
             Instructor.NullUserIDException 
   {
		if (i == null) throw new InvalidInstructorException();

		//TODO: FIX
		//SQLDB sqldb = Scheduler.schedDB;
		String insert = "";
		System.out.println(i);
		insert = "( '" + i.getFirstName() + "', '" + i.getLastName() + "', '" +
		i.getId() + "', " + i.getMaxWTU() +  ", '" +
		i.getOffice().getBuilding() + "', '" + i.getOffice().getRoom() + "', " + 
		i.getDisability() + " )";
		//sqldb.open();
		//sqldb.insertStmt("instructors", insert);
		//this.data = sqldb.getInstructorDB().getData();
		updatePreferences();
		//sqldb.close();
		setChanged();
		notifyObservers();
	}

	/**
	 * Adds a given instructor to the local instructor database.
	 * 
	 * pre:
	 * 		// i must be a valid instructor
	 * 		(i != null && i.isValidInstructor());
	 * 
	 * 		&&
	 * 
	 * 		// i is not in the database
	 * 		(i not in InstructorDB.data)
	 * 
	 * post:
	 * 		// Only this instructor has been added to the database
	 * 		(forall (Instructor inst) (inst' in InstructorDB'.data)
	 * 			iff ( (inst == i) or
	 * 				(inst in InstructorDB.data)))
	 * 
	 * @param	i		the new instructor to add to the database
	 **/
	public void addLocalInstructor( Instructor i) throws InvalidInstructorException,
	DuplicateInstructorException, Instructor.NullUserIDException {
		if (i == null) throw new InvalidInstructorException();
      localData.add(i);
      setChanged();
		notifyObservers();
	}

	/**
	 * This method changes the course preferences of a particular instructor.
	 *  
	 * @param dataTable table of course preferences.
	 * @param i instructor whose course preference you wish to change.
	 */
   public void changePreferences(Vector<Vector> dataTable, Instructor i) 
   {
		Iterator iterator = dataTable.iterator();
		ArrayList<CoursePreference> list = new ArrayList<CoursePreference>();
		while (iterator.hasNext()) 
      {
			Vector row = (Vector) iterator.next();
			if (row.size()== 0) 
         {
				return;
			}
			String id = (String) row.get(0);
			//TODO: FIX
//			if (Scheduler.cdb == null) 
//         {
//				return;
//			}
//			Course c = Scheduler.cdb.getLocalCourse( Integer.parseInt(id.substring(3,6)) , "Lecture");
//			CoursePreference cp = new CoursePreference(c, Integer.parseInt((String)row.get(1)) );
//			list.add(cp);
			//changeSections((String) row.get(0), Integer.parseInt((String)row.get(1)) );
		}
		PreferenceTuple tup = new PreferenceTuple(i.getId() , list);
		if (getPreferences(i) != null) 
      {
			//findAndChange(tup);
		}
		else 
      {
			//preferences.add(tup);
		}

      /*
       * Name should speak for itself. To try and start fixing some bugs, I'm
       * altering the code in this class to put instructor preferences in 
       * the Instructor object itself. (This should already be done anyhow, but
       * now this class will actually use it). 
       *
       * Added by: Eric Liebowitz
       */
      changeLocalInstructorCPrefs(i, list);

		setChanged();
		notifyObservers();
	}

   /**
    * Changes a local instructor CoursePreferences to a given ArrayList of
    * CoursePreferences.
    *
    * NOTE: It is assumed that the instructor being altered already exists
    *       in "localData". If it does not, nothing will be done (thoguh an 
    *       error will be printed).
    *
    * Written by: Eric Liebowitz
    *
    * @param i The Instructor whose CPrefs are to be changed. 
    * @param cprefs The CoursePreferences "i" will now have. 
    */
   private void changeLocalInstructorCPrefs (Instructor i, 
                                             ArrayList<CoursePreference> cprefs)
   {
      /*
       * Have to change the instructor in the "localData" vector, so I need to 
       * get the index of "i" within that vector
       */
      int index = localData.indexOf(i);
      Instructor local_I = localData.get(index);

      if (local_I != null)
      {
         local_I.setCoursePreferences(cprefs);
      }
      else
      {
         System.err.println (i + " is not present in 'localData'. Cannot " + 
                            "change CPrefs");
      }
   }

   /**
    * Changes a local instructor TimePreferences to a given ArrayList of
    * TimesePreferences.
    *
    * NOTE: It is assumed that the instructor being altered already exists
    *       in "localData". If it does not, nothing will be done (though an 
    *       error will be printed). 
    *
    * Written by: Eric Liebowitz
    *
    * @param i The Instructor whose TPrefs are to be changed. 
    * @param tPrefs The TimePreferences "i" will now have. 
    */
   private void changeLocalInstructorTPrefs 
   (
      Instructor i, 
      HashMap<Day, LinkedHashMap<edu.calpoly.csc.scheduler.model.db.Time, TimePreference>> tPrefs
   )
   {
      /*
       * Have to change the instructor in the "localData" vector, so I need to 
       * get the index of "i" within that vector
       */
      int index = localData.indexOf(i);
      Instructor local_I = localData.get(index);

      if (local_I != null)
      {
         local_I.setTimePreferences(tPrefs);
      }
      else
      {
         System.err.println (i + " is not present in 'localData'. Cannot " + 
                            "change TPrefs");
      }
   }
	/** Get next and iterator stays the same */
	public Instructor checkNext() {
		return null;
	}

	/**
	 * Edits a given instructor which is already in the database.
	 * 
	 * pre:
	 *       //
	 *       // "old" and "new" cannot be the same
	 *       //
	 *       instructor != nil;
	 *
	 *      &&
	 *
	 *      //
	 *      // "new" must be a valid instructor
	 *      //
	 *      isValidInstructor (instructor, ldb);
	 *
	 * post:
	 *        //
	 *        // A instructor is in the output database iff it was already there
	 *        // to begin with, iff it was the new user added, and iff it is
	 *        // not the old instructor that was changed
	 *        //
	 *
	 *        forall (instructor':data)
	 *           (instructor' in instructordb') iff
	 *           (((instructor == instructor') or (instructor in instructordb))
	 *           and (instructor != instructor'));
	 *
	 * @param	instructor		the instructor to edit
	 **/
	public void editInstructor(Instructor i) {
		removeInstructor(i);
		addInstructor(i);
	}

	/**
	 * Edits a given instructor which is already in the local database.
	 * 
	 * pre:
	 *       //
	 *       // "old" and "new" cannot be the same
	 *       //
	 *       instructor != nil;
	 *
	 *      &&
	 *
	 *      //
	 *      // "new" must be a valid instructor
	 *      //
	 *      isValidInstructor (instructor, ldb);
	 *
	 * post:
	 *        //
	 *        // A instructor is in the output database iff it was already there
	 *        // to begin with, iff it was the new user added, and iff it is
	 *        // not the old instructor that was changed
	 *        //
	 *
	 *        forall (instructor':data)
	 *           (instructor' in instructordb') iff
	 *           (((instructor == instructor') or (instructor in instructordb))
	 *           and (instructor != instructor'));
	 *
	 * @param	instructor		the instructor to edit
	 **/
	public void editLocalInstructor(Instructor i) {
		removeLocalInstructor(i);
		addLocalInstructor(i);
	}

	/**
	 * Edits an instructor's preference of a certain id and course.
	 * 
	 * @param pt the preference tuple to change.
	 */
	/*public void findAndChange(PreferenceTuple pt) {
		Iterator iterator = preferences.iterator();
		int i = 0; 
		PreferenceTuple pti = null;
		while (iterator.hasNext()) {
			pti = (PreferenceTuple) iterator.next();
			if (pt.getId() == pti.getId()) {
				//            preferences.remove(i);
				//            preferences.add(pt);
				break;
			}
			i++;
		}
		if (preferences.remove(i) == null ) {
			System.err.println("Did not change preferences properly." + i);
		}
		else {
			if (pti != null) {
				preferences.add(pt);
			}
			else {
				System.err.println("Did not change preferences properly.");
			}
		}
	}*/

	/**
	 * Edits and instructor's preference of a certain id and time.
	 * 
	 * @param pt time preference tuple to change.
	 */
	public void findAndChangeTime(TimePreferenceTuple pt) {
		if (timepreferences == null) {
			timepreferences = new Vector<TimePreferenceTuple>();
            timepreferences.add(pt);
         return;
      }

      Iterator iterator = timepreferences.iterator();
		int i = 0; 
      boolean found = false;
		TimePreferenceTuple pti = null;
		while (iterator.hasNext()) {
			pti = (TimePreferenceTuple) iterator.next();
			if (pt.getId() == pti.getId()) {
            timepreferences.remove(i);
            timepreferences.add(pt);
            found = true;
				break;
			}
			i++;
		}
      if (!found) {
         timepreferences.add(pt);
         return;
      }
		if (timepreferences.remove(i) == null ) {
			System.err.println("Did not change time preferences properly." + i);
		}
		else {
			if (pti != null) {
				timepreferences.add(pt);
			}
			else {
				System.err.println("Did not change time preferences properly.");
			}
		}
	}

	/**
	 *  Returns the collection of instructors.
	 *  
    *  NOTE: I commented out the "updatePreferences" method for the second reason
    *        second reason I did so in the "getLocalData" method.
    *
	 *  @return	a collection of instructors in the database
	 **/
	public Vector<Instructor> getData() {
		System.out.println("Getting data! HERE1");
		//updatePreferences();
		return data;
	}

        public void localToPermanent() {
            Iterator it = this.localData.iterator();

            while (it.hasNext()) {
                Instructor i = (Instructor) it.next();
                this.addInstructor(i);
            }
        }

        public void permanentToLocal() {
            this.localData = new Vector<Instructor>(data);
        }

	/**
	 * Returns the local collection of instructors.
	 *
    * Note: I commented out the call to "updatePreferences" for two reasons. 
    *        
    *       First, the methdod doesn't work right. Calls to this method here 
    *       are meant for local data, yet the "updatePreferences" method clearly
    *       refers to the global database's "preferences" instance variable 
    *       (though I'm not sure why that's even there...don't Instructor 
    *       objects carry their preferences with them?). Thus, data in the local
    *       DB not present in the global DB will be blessed with "null" 
    *       preferences, which causes no end to my grief during generation. 
    *
    *       Second, I'm not sure why the preferences need to be updated here. 
    *       It looks like you wanted to somehow "sync" local and global DB 
    *       prefs, but I don't think that needs to be done here. I believe I'd
    *       mentioned that we'd make a button to do that manually when the user
    *       wanted to. 
    *
    *       Contact me with any questions, concerns, or explanations. 
    *
    *          -Eric
    * 
	 *  @return	a collection of instructors in the database
	 **/
	public Vector<Instructor> getLocalData() {
		//System.out.println("Getting data! HERE2");
		return localData;
	}

   /**
    * Sets the local collection of instructor to a given set of data.
    *
    * @param data The data to set the local to
    *
    * By: Eric Liebowitz (23jul10)
    */
   public void setLocalData(Collection<Instructor> data)
   {
      this.localData = new Vector<Instructor>(data);
      Collections.sort((List)localData);
      this.setChanged();
      this.notifyObservers();
   }

	/**
	 *  Returns the instructors sorted by generousity
	 *
	 *  @return  a collection of instructors in the database based on the generousity
	 **/
	public Vector<Instructor> getDataByGenerosity() {
		System.out.println("Getting data! HERE3");
		return data;
	}

	/**
	 * Returns the instructor from the database matching the inputted string.
	 * 
	 * @param id user id of the instructor to get.
	 * @return the instructor matching the id, or null.
	 */
	public Instructor getInstructor(String id) {
		if (data == null)
			return null;

		Instructor i;	

		while (data.iterator().hasNext()) {
			i = data.iterator().next();
			if(i.getId() == id)
				return i;
		}

		return null;
	}

	/**
	 * Returns the instructor from the local database matching the name given.
	 * 
	 * @param name Name of instructor, given in [last],[first] format.
	 * @return the instructor going by the given name.
	 */
	public Instructor getLocalInstructorByName(String name) {
		String delimiter = ", ";
		String[] two = name.split(delimiter);
		String last = two[0];
		String first = two[1];


		if (localData == null)
			return null;

		Iterator iterator = localData.iterator();
		Instructor i;


		while (iterator.hasNext()) {
			i = (Instructor)iterator.next();
			if(i.getFirstName().contains(first) && i.getLastName().contains(last))
				return i;
		}

		System.out.println("First is " + first);
		System.out.println("Seconds is " + last);
		return null;
	}

	/**
	 * Returns the instructor from the database matching the name given.
	 * 
	 * @param name Name of instructor, given in [last],[first] format.
	 * @return the instructor going by the given name.
	 */
	public Instructor getInstructorByName(String name) {
		String delimiter = ", ";
		String[] two = name.split(delimiter);
		String last = two[0];
		String first = two[1];


		if (data == null)
			return null;

		Iterator iterator = data.iterator();
		Instructor i;


		while (iterator.hasNext()) {
			i = (Instructor)iterator.next();
			if(i.getFirstName().contains(first) && i.getLastName().contains(last))
				return i;
		}

		System.out.println("First is " + first);
		System.out.println("Seconds is " + last);
		return null;
	}

	/** Get next and increase iterator */
	public Instructor getNext() {
		return null;
	}

	/**
	 * Gets the preference of a particular course for a particular instructor.
	 * 
	 * @param i instructor whose preference to get.
	 * @param c course to use to get the preference for this instructor.
	 * @return the course preference for this instructor and course.
	 */
	/*public CoursePreference getPreference(Instructor i, Course c ) {
		if (preferences == null) {
			preferences = new Vector<PreferenceTuple>();
		}


		Iterator iterator = preferences.iterator();
		ArrayList<CoursePreference> prefList = null;

		if (i.getId().equals("STAFF")) {
			CoursePreference returnVal = new CoursePreference(c,10 );
			return returnVal ;
		}

		while (iterator.hasNext()) {
			PreferenceTuple pt = (PreferenceTuple) iterator.next();
			if (pt.getId() == i.getId()) {
				prefList = pt.getPrefList();
				break;
			}
		}

		if (prefList != null ) {
			iterator = prefList.iterator(); 
			while (iterator.hasNext()) {
				CoursePreference cp = (CoursePreference) iterator.next();
				if (cp.getCourse().equals(c )) {
					return cp; 
				}
			}        

		}

		return new CoursePreference(c, 5);
	}*/

	/**
	 * This method will return all course preferences for a given instructor.
	 * 
	 * @param i the instructor for whom to get course preferences.
	 * @return the course preference list for this instructor.
	 */
	public ArrayList<CoursePreference> getPreferences(Instructor i ) {
		/*if (preferences == null) {
         System.err.println ("has no CPrefs");
			preferences = new Vector<PreferenceTuple>();
		}

		Iterator iterator = preferences.iterator();

		while (iterator.hasNext()) {
			PreferenceTuple pt = (PreferenceTuple) iterator.next();
         System.err.println (pt.getId() + " vs. " + i.getId());
         if (pt.getId().equals(i.getId())) {
            System.err.println ("==");
				return pt.getPrefList();
			}
		}
      System.err.println ("Get Pref returning null");*/
		return null;
	}

	/**
	 * Returns a table of instructors and courses in the database.
	 * 
	 * TODO: REWRITE
	 * 
	 * @return a table of instructors and courses in the database.
	 */
	public Vector<Vector> getTable() {
		ArrayList<Vector> tabledata = new ArrayList<Vector>();
//		ArrayList<Course> data = (ArrayList) Scheduler.cdb.getLocalData();
//		if (Scheduler.cdb.getData() == null) {
//			Vector<String> row = new Vector<String>();
//			tabledata.add(row);
//			return new Vector(tabledata);
//		}
//		Iterator iterator = Scheduler.cdb.getData().iterator();

//		while (iterator.hasNext()) {
//			Vector<String> row = new Vector<String>();
//			Course c = (Course)iterator.next();
//			if (c.getCourseType().contains("Lecture")) {
//				row.add("CPE" + c.getId());
//				row.add(5 + "");
//				tabledata.add(row);
//			}
//		}
		Vector<Vector> returnVal = new Vector(tabledata );
		return returnVal;
	}

   /**
    * Returns a table of times and preferences
    *
    * @return A table of times and preferences.
    */
   public Vector<Vector> getTimeTable() {
        Vector<Vector> returnVal = new Vector<Vector>();
        for (int i = 0; i < ROWS; i++) {
           edu.calpoly.csc.scheduler.model.db.Time start = getStartTime(i);
           edu.calpoly.csc.scheduler.model.db.Time end = getEndTime(i);
           Vector<Object> row = new Vector<Object>();
           String time = start.standardString();
           row.add((Object)time);
           int daysinweek = 7;
           for (int j = 0; j < 7; j++) {
               int pref = 5;
               Integer cell = new Integer(pref);
               row.add((Object)cell);
           }
           returnVal.add(row); 
        } 
        return returnVal;
    }

    /**
     * Converts an integer to a start time.
     * @param number An integer
     * @return The start time.
     */
    private edu.calpoly.csc.scheduler.model.db.Time getStartTime(int number ) {
       int start = 7;
       int hour = (number / 2) + 7; 
       int minute = (number % 2) * 30; 
        
       return new edu.calpoly.csc.scheduler.model.db.Time(hour, minute);
    }


    /**
     * Converts an integer to an end time.
     * @param number An integer
     * @return The end time.
     */
    private edu.calpoly.csc.scheduler.model.db.Time getEndTime(int number) {
       int start = 7;
       int hour = (number / 2) + 7; 
       int minute = (number % 2) * 30; 
       if ((minute + 30) == 60) {
           return new edu.calpoly.csc.scheduler.model.db.Time(hour + 1, 0);
       }
       else {
           return new edu.calpoly.csc.scheduler.model.db.Time(hour, minute + 30);
       }
    }





	/**
	 * Returns the time preferences list for this instructor.
	 * 
	 * @param i the instructor for whom to get time preferences.
	 * @return the list of time preferences.
	 */
	public ArrayList<TimePreference> getTimePreferences(Instructor i) {
		if (timepreferences == null)
			timepreferences = new Vector<TimePreferenceTuple>();

		Iterator<TimePreferenceTuple> iterator = timepreferences.iterator();

		while (iterator.hasNext()) {
			TimePreferenceTuple pt = iterator.next();
			if (pt.getId() == i.getId()) {
				return pt.getPrefList();
			}
		}
		return null;
	}

	/**
	 * Checks if the Instructor is in the database.
	 * 
	 * @param	instructor	instructor to check for in the database
	 * @param	db			the database in which to check for the instructor
	 * 
	 * pre:
	 * 		// none
	 * 
	 * post;
	 * 		// none
	 * 
	 * @return	whether the given instructor is in the given database
	 **/
	public boolean isValidInstructor(Instructor instructor) {
		return (data.contains(instructor));
	}

	/**
	 * Checks if the Instructor Database is valid.
	 * 
	 * pre:
	 * 		// none
	 * 
	 * post:
	 * 		// none
	 * 
	 * @param	db	the database to check validity for
	 * 
	 * @return	whether the given database is valid
	 **/
	protected boolean isValidInstructorDB(InstructorDB db) {
		return false;
	}

	/**
	 * Removes a given instructor from the database. This does not deal with
	 * removal. This is handled elsewhere.
	 * 
	 * pre: 
	 *       //
	 *       // "instructor" must be in "instructordb"
	 *       //
	 *       (instructor in instructordb);
	 *
	 * post:
	 *        //
	 *        // The new database differs from the old only in the absence of
	 *        // "instructor"
	 *        //
	 *        forall (instructor':Instructor)
	 *           (instructor' in instructordb) iff ((instructor' != instructor)
	 *           and (instructor' in instructordb));
	 *
	 * @param	instructor		the instructor to remove from the database
	 **/
	public void removeLocalInstructor(Instructor in) {
		if (in == null)
			throw new NullPointerException();

		if (!localData.contains(in))
			throw new InstructorDoesNotExistException();

      Iterator iterator = localData.iterator();
      int removeInd = -1;
      for (int i = 0; iterator.hasNext(); i++) {
         Instructor it = (Instructor) iterator.next();
         if (in.equals(it)) {
            removeInd = i;
            break;
         }
      }

      if (removeInd != -1) {
         
         localData.remove(removeInd);
         
      }

		setChanged();
		notifyObservers();
	}



	/**
	 * Removes a given instructor from the database. This does not deal with
	 * removal. This is handled elsewhere.
	 * 
	 * pre: 
	 *       //
	 *       // "instructor" must be in "instructordb"
	 *       //
	 *       (instructor in instructordb);
	 *
	 * post:
	 *        //
	 *        // The new database differs from the old only in the absence of
	 *        // "instructor"
	 *        //
	 *        forall (instructor':Instructor)
	 *           (instructor' in instructordb) iff ((instructor' != instructor)
	 *           and (instructor' in instructordb));
	 *
	 * @param	instructor		the instructor to remove from the database
	 **/
	public void removeInstructor(Instructor i) {
		if (i == null)
			throw new NullPointerException();

		if (!data.contains(i))
			throw new InstructorDoesNotExistException();

		//TODO: FIX
//		SQLDB sqldb = Scheduler.schedDB;
		//sqldb.open();
		String remove = "userid = '" + i.getId() + "'";
		String insert = "userid = '" + i.getId() + "'";	
//		sqldb.removeStmt("instructors", insert);

		//sqldb.close();
//		this.data = sqldb.getInstructorDB().getData();
		setChanged();
		notifyObservers();
	}

	/** Reset iterator */
	public void reset() {

	}

	/**
	 *  Sets the data in the instructorDB.
	 *  
	 *  @param data The data to be set.
	 **/
	public void setData(Vector<Instructor> data  ) {
		this.data = data;
	}

	/**
	 * Adds a vector of preferred courses to the instructor.
	 * 
	 * @param	instructor	the instructor for whom to set preferred courses
	 * @param	course		a vector of courses representing the preference
	 */
	public void SetPreferredCourses(Instructor instructor,
			java.util.Vector<edu.calpoly.csc.scheduler.model.db.cdb.Course> course) {
		System.out.println("Setting preferred courses!");
	}

      /**
       * Update the preferences to get rid of courses that may not be there anymore.
       *
       */
      public void updateAllLocalPreferences() {
         Iterator it = this.localData.iterator();
         while (it.hasNext()) {
             Instructor i = (Instructor)it.next();
             i.updateLocalPreferences();
         }
      }

	/**
	 * This function updates the course preferences for this instructor.
    *
    * This method's documentation is worthless - Eric
    *
    * It looks like this methods forces all instructor prefs in the global DB
    * onto the preferences in the local DB. While this probably isn't a good
    * idea in the first place (what's the point of keeping them separate if we
    * end up making them the same in the end), it has an annoying side affect:
    * instructor in the local DB but not in the global DB have their preferences
    * set to "null" by this method...probably not the right thing to do. 
    *
	 */
	public void updatePreferences() {

		if (localData == null) {
         System.err.println ("Local data is null");
			return;
		}
		Iterator iterator = localData.iterator();

		while (iterator.hasNext()) {
			Instructor instructor = (Instructor) iterator.next();
         System.err.println ("Instructor: " + instructor);
			ArrayList<CoursePreference> cprefs = getPreferences(instructor); 
         System.err.println ("Updated: " + cprefs);
			instructor.setCoursePreferences(cprefs);
		}
      System.err.println ("Done");
	}

   /**
    * Sets the local data to a given collection of data, and notifies any/all
    * observers of the change.
    *
    * @param data Data to set local data to
    *
    * By: Eric Liebowitz (23jul10)
    */
   public void setLocalWithThisFromFile (Collection<Instructor> data)
   {
      this.setLocalData (data);
      this.setChanged();
      this.notifyObservers();
   }

   /**
    * Dumps Schedule-verifying-pertinent information about the local IDB to a
    * print stream. In particular, this will generate output per-Instructor in
    * an easy-to-parse-for-Perl format. If the output generated does not make
    * sense, 
    *
    * @param ps PrintStream to output text data to
    *
    * By: Eric Liebowitz (01nov10)
    */
   public void dumpLocalAsPerlText (PrintStream ps)
   {
      ps.println ("--LOCAL IDB BEGIN--");
      for (Instructor i: this.localData)
      {
         ps.println ("first => \"" + i.getFirstName() + "\",");
         ps.println ("last  => \"" + i.getLastName()  + "\",");
         ps.println ("wtu   =>  " + i.getMaxWTU()    + ",");
         ps.println ("tPrefs => \n{");
         /*
          * The tPrefs are in a HashMap<Integer, LinkedHashMap<Time, TimePreference>>
          * object. Thus, the outer loop iterates over days, and the inner one 
          * over Times.
          *
          * The random whitespace prints are for better formatting. Might as 
          * well make it nice to read, right?
          */
         for (Day d: i.getTimePreferences().keySet())
         {
            ps.print ("   ");
            ps.println (d + " => \n   {");
            for (TimePreference tp: i.getTimePreferences().get(d).values())
            {
               ps.print ("      ");
               ps.println ("\"" + tp.getTime() + "\" => " + tp.getDesire() + ",");
            }
            ps.println ("   },");
         }
         ps.println ("},");

         ps.println ("cPrefs => \n{");
         for (CoursePreference cp: i.getCoursePreferences())
         {
            ps.print ("   ");
            ps.println ("\"" + cp.getCourse() + "\" => " + cp.getDesire() + ",");
         }
         ps.println ("},");

         ps.println("===");
      }
      ps.println ("--LOCAL IDB END--");
   }

}
