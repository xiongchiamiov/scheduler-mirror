package scheduler.db.preferencesdb;

import java.util.Collection;
import java.lang.*;
import java.sql.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import scheduler.Scheduler;
import scheduler.db.*;
import scheduler.db.coursedb.*;
import scheduler.generate.*;

/**
 *
 * The class representing the preferences database. 
 * 
 * @author Leland Garofalo. Modified by Jan Lorenz Soliman after 5/6/2010
 */
public class PreferencesDB extends Observable {

    /** Constant for the course preferences field */
    public static final String COURSE_PREFERENCES_FIELD = "(name, weight)";
    /** Constant for the course to preferences field */
    public static final String COURSES_TO_PREFERENCES = "(courseid, prefid)";
    /** Constant for the days preferences field */
    public static final String DAYS_PREFERENCES_FIELD = "(name, weight, sun, mon, tues, wed, thur, fri, sat)";

    /** Collection of preferences.  */
    protected Vector<Preferences> data;
    /** Collection of days preferences. */
    protected Vector<DaysForClasses> dPrefs;
    /** Collection of overlap preferences. */
    protected Vector<NoClassOverlap> cPrefs;

    /** Collection of local preferences.  */
    protected Vector<Preferences> localData;
    /** Collection of local days preferences. */
    protected Vector<DaysForClasses> localDPrefs;
    /** Collection of local overlap preferences. */
    protected Vector<NoClassOverlap> localCPrefs;

    /** Default Constructor */
    public PreferencesDB() {
      dPrefs = new Vector<DaysForClasses>();
      cPrefs = new Vector<NoClassOverlap>();
      localDPrefs = new Vector<DaysForClasses>();
      localCPrefs = new Vector<NoClassOverlap>();
      localData = new Vector<Preferences>();
    }

    /**
     *  Returns all preferences in the database.
     *  @return A list of all the preferences in the db.
     **/
    public Vector<SchedulePreference> getAllPreferences() {
      Vector<SchedulePreference> returnVal = new Vector<SchedulePreference>(dPrefs);
      returnVal.addAll(cPrefs);
      return returnVal;
    }

    /**
     * Returns the names of all the DaysForClasses Strings.
     *
     * @return A list of the names of all the DaysForClasses preferences.
     */
    public Vector<String> getDaysForClassesNames() {
        Vector<String> names = new Vector<String>();
        Iterator it = this.dPrefs.iterator();
        while (it.hasNext()) {
            names.add(it.next().toString());
        }
        return names;
    }

   /**
    * Returns the names of all the DaysForClasses preferences present in the 
    * local PDB. 
    *
    * @return a list of all the names of the DaysForClasses preferences present
    * in the local PDB
    *
    * Written by: Eric Liebowitz
    */
   public Vector<DaysForClasses> getLocalDaysForClasses ()
   {
      return this.localDPrefs;
   }

   /**
    * @return a list of all the names of the local DFC's
    */
   public Vector<String> getLocalDFCNames()
   {
      Vector<String> r = new Vector<String>();

      for (DaysForClasses dfc: this.localDPrefs)
      {
         r.add(dfc.getName());
      }

      return r;
   }

   /**
    * Returns the list of NoClassOverlap preferences
    *
    * @return the list of NoClassOverlap preferences
    */
   public Vector<NoClassOverlap> getLocalNoClassOverlaps ()
   {
      return this.localCPrefs;
   }

    /**
     * Find a DaysForClasses preference based on name.
     *
     * @param name The name of the preference.
     * @return The corresponding DaysForClasses preference or null if its not found.
     */
    public DaysForClasses getDaysForClassesByName(String name) {
        Iterator it = this.dPrefs.iterator();
        while (it.hasNext()) {
            DaysForClasses dfc = (DaysForClasses)it.next();
            if (dfc.toString().equals(name)) {
                return dfc;
            }
        }

        return null;
    }

    /**
     *  Returns all local preferences in the database.
     *  @return A list of all the  preferences in the local db.
     **/
    public Vector<SchedulePreference> getAllLocalPreferences() {
      Vector<SchedulePreference> returnVal = new Vector<SchedulePreference>(localDPrefs);
      returnVal.addAll(localCPrefs);
      return returnVal;
    }

    /**
     *  Returns the class day preferences in the database.
     *  @return A list of the class day preferences in the db.
     **/
    public Vector<NoClassOverlap> getClassPreferences() {
      Collections.sort( (Vector<NoClassOverlap>)cPrefs);
      return (Vector<NoClassOverlap>)cPrefs;
    }

    /**
     *  Returns the class day preferences in the local database.
     *  @return A list of the class day preferences in the db.
     **/
    public Vector<NoClassOverlap> getLocalClassPreferences() {
      Collections.sort( (Vector<NoClassOverlap>)cPrefs);
      return (Vector<NoClassOverlap>)cPrefs;
    }

    /**
     *  Returns the class day preferences in the database.
     *  @return A list of the class day preferences in the db.
     **/
    public Vector<DaysForClasses> getDayPreferences() {
      Collections.sort( (Vector<DaysForClasses>)dPrefs);
      return (Vector<DaysForClasses>)dPrefs;
    }

    /**
     *  Returns the class day preferences in the local database.
     *  @return A list of the class day preferences in the db.
     **/
   public Vector<DaysForClasses> getLocalDayPreferences() 
   {
      /*
       * I'm just going to point this to the one I wrote, which does the right
       * thing. -Eric
       */
       return this.getLocalDaysForClasses();
   }

    /**
     * Adds a Preference to the database
     *
     * @param p Preference Object
     *
     * <pre>
     * // ** Pre and Post conditions ** //
     *
     * <b><u>Pre:</u></b>
     *
     * // Preference can not be Null
     * p != nil
     *
     * &&
     *
     *
     * // Preference can not be in PreferencesDB
     * 
     * !(PreferencesDB.contains(p))
     *
     *
     * <b><u>Post:</u></b>
     *
     * //Preference must be stored into database
     * PreferencesDB.contains(p)
     *
     * 
     *
     * </pre>
     */
    public void addPreference(Preferences p) {
        SQLDB sqldb = new SQLDB();
        String insert = "";
        insert = "( " + " '" + p.name + "', '" + p.data + "', " + p.type + ", " + p.violatable + ", " + p.importance + ")";
        sqldb.open();
        sqldb.insertPrefStmt("preferences", insert);
        PreferencesDB temp = sqldb.getPreferencesDB();
        this.data = temp.getDataOld();
        sqldb.close();
        setChanged();
        notifyObservers();
    }

    /**
     * Adds a Preference to the database
     *
     * @param p Preference Object
     *
     * <pre>
     * // ** Pre and Post conditions ** //
     *
     * <b><u>Pre:</u></b>
     *
     * // Preference can not be Null
     * p != nil
     *
     * &&
     *
     *
     * // Preference can not be in PreferencesDB
     * 
     * !(PreferencesDB.contains(p))
     *
     *
     * <b><u>Post:</u></b>
     *
     * //Preference must be stored into database
     * PreferencesDB.contains(p)
     *
     * 
     *
     * </pre>
     */
    public void addPreference(SchedulePreference p) {
        if (p instanceof DaysForClasses) {
            DaysForClasses dfc = (DaysForClasses) p;
            this.addDaysForClasses(dfc); 
        }
        else {
            NoClassOverlap nco = (NoClassOverlap) p;
            this.addNoClassOverlap(nco);
        }
    }

    /**
     * Adds a DaysForClasses Preference to the database
     *
     * @param p DaysForClasses Preference Object
     *
     * <pre>
     * // ** Pre and Post conditions ** //
     *
     * <b><u>Pre:</u></b>
     *
     * // Preference can not be Null
     * p != nil
     *
     * &&
     *
     *
     * // Preference can not be in PreferencesDB
     * 
     * !(PreferencesDB.contains(p))
     *
     *
     * <b><u>Post:</u></b>
     *
     * //Preference must be stored into database
     * PreferencesDB.contains(p)
     *
     * 
     *
     * </pre>
     */
    public void addNoClassOverlap(NoClassOverlap p) {
        SQLDB sqldb = new SQLDB();
        String insert = "";
        insert = "( " + " '" + p.name + "', '" + p.weight + "')";
        HashSet<String> insert2 = new HashSet<String>();
        
        Iterator it = p.cs.iterator();

        while (it.hasNext()) {
           Course c = (Course) it.next();
           insert2.add("( " + " '" + c.getCourseName() +  "', '" + p.name + "')");
        }


        sqldb.open();
        sqldb.insertPrefStmt("preferences_courses", insert, COURSE_PREFERENCES_FIELD );

        it = insert2.iterator();
        while (it.hasNext()) {
           String holder = (String) it.next();
           sqldb.insertPrefStmt("courses_to_preferences",holder, COURSES_TO_PREFERENCES);
        }

        PreferencesDB temp = sqldb.getPreferencesDB();
        this.data = temp.getDataOld();
        sqldb.close();
        setChanged();
        notifyObservers();
    }

    /**
     * Adds a DaysForClasses Preference to the database
     *
     * @param p DaysForClasses Preference Object
     *
     * <pre>
     * // ** Pre and Post conditions ** //
     *
     * <b><u>Pre:</u></b>
     *
     * // Preference can not be Null
     * p != nil
     *
     * &&
     *
     *
     * // Preference can not be in PreferencesDB
     * 
     * !(PreferencesDB.contains(p))
     *
     *
     * <b><u>Post:</u></b>
     *
     * //Preference must be stored into database
     * PreferencesDB.contains(p)
     *
     * 
     *
     * </pre>
     */
    public void addDaysForClasses(DaysForClasses p) {

        SQLDB sqldb = new SQLDB();
        String insert = "";
        insert = "( " + " '" + p.name + "', " + p.weight + ", ";
        insert = insert + p.days.contains(Week.SUN) + ", " + p.days.contains(Week.MON); 
        insert = insert + ", " + p.days.contains(Week.TUE) + ", ";
        insert = insert + p.days.contains(Week.WED) + ", " + p.days.contains(Week.THU);
        insert = insert + ", " + p.days.contains(Week.FRI) + ", ";
        insert = insert + p.days.contains(Week.SAT) + ")";
        sqldb.open();
        sqldb.insertPrefStmt("preferences_days", insert, DAYS_PREFERENCES_FIELD );
        PreferencesDB temp = sqldb.getPreferencesDB();
        this.data = temp.getDataOld();
        sqldb.close();
        setChanged();
        notifyObservers();
    }

    /**
     * Removes a DaysForClasses object from the local database.
     *
     * @param p DaysForClasses Preference Object
     *
     */
    public void removeLocalDaysForClasses(DaysForClasses p) throws PreferenceDoesNotExistException {
        if (localDPrefs != null) {
            if (!localDPrefs.contains(p)) {
                throw new PreferenceDoesNotExistException();
            }
        }

        Iterator iterator = localDPrefs.iterator();
        int removeInd = -1;
        for (int i = 0; iterator.hasNext(); i++) {
            DaysForClasses it = (DaysForClasses) iterator.next();
            if (p.equals(it)) {
                removeInd = i;
                break;
            }
        }

        if (removeInd != -1) {
            Scheduler.debug("Removing " + removeInd);
            localDPrefs.remove(removeInd);
            Scheduler.debug(localDPrefs.toString());
        }
      
        Collections.sort(localDPrefs);
        setChanged();
        notifyObservers();
    }

    /**
     * Removes a NoClassOverlap object from the local database.
     *
     * @param p NoClassOverLap Preference Object
     *
     */
    public void removeLocalNoClassOverlap(NoClassOverlap p) throws PreferenceDoesNotExistException {
        if (localCPrefs != null) {
            if (!localCPrefs.contains(p)) {
                throw new PreferenceDoesNotExistException();
            }
        }

        Iterator iterator = localCPrefs.iterator();
        int removeInd = -1;
        for (int i = 0; iterator.hasNext(); i++) {
            NoClassOverlap it = (NoClassOverlap) iterator.next();
            if (p.equals(it)) {
                removeInd = i;
                break;
            }
        }

        if (removeInd != -1) {
            Scheduler.debug("Removing " + removeInd);
            localCPrefs.remove(removeInd);
            Scheduler.debug(localCPrefs.toString());
        }

        Collections.sort(localCPrefs);
        setChanged();
        notifyObservers();
    }


   /** Edits a given Day Preferences which is already in the local preference list.
    *  @param c The Course to be edited.
    * **/
   public void editLocalDaysForClasses (DaysForClasses d) {

      try {
         removeLocalDaysForClasses(d);
      }
      catch (PreferenceDoesNotExistException e) {

      }
      try {
         addLocalDaysForClasses(d);
      }
      catch (PreferenceExistsException e) {

      }
   }

   /** Edits a given NoClassOverlap preference which is already in the local preference list.
    *  @param c The Course to be edited.
    * **/
   public void editLocalNoClassOverlap (NoClassOverlap d) {

      try {
         removeLocalNoClassOverlap(d);
      }
      catch (PreferenceDoesNotExistException e) {

      }
      try {
         addLocalNoClassOverlap(d);
      }
      catch (PreferenceExistsException e) {

      }
   }

    /**Preference does not already exists exception thrown in the set methods.  */
    public static class PreferenceDoesNotExistException extends Exception {
         public PreferenceDoesNotExistException() {
         /**
          * Constructor calls the exception constructor.
          *
          */
            super();
         }
    }

    public static class PreferenceExistsException extends Exception {

        public PreferenceExistsException() {
            super();
        }

    }

    /**
     * Adds a NoClassOverlap Preference to the local database
     *
     * @param p NoClassOverlap Preference Object
     *
     * <pre>
     * // ** Pre and Post conditions ** //
     *
     * <b><u>Pre:</u></b>
     *
     * // Preference can not be Null
     * p != nil
     *
     * &&
     *
     *
     * // Preference can not be in PreferencesDB
     *
     * !(PreferencesDB.contains(p))
     *
     *
     * <b><u>Post:</u></b>
     *
     * //Preference must be stored into database
     * PreferencesDB.contains(p)
     *
     *
     *
     * </pre>
     */
    public void addLocalNoClassOverlap(NoClassOverlap p) throws PreferenceExistsException {
        if (localCPrefs != null) {
            if (localCPrefs.contains(p)) {
                throw new PreferenceExistsException();
            }
        }
        this.localCPrefs.add(p);
        Collections.sort(localDPrefs);
        setChanged();
        notifyObservers();
    }

    /**
     * Adds a DaysForClasses Preference to the local database
     *
     * @param p DaysForClasses Preference Object
     *
     * <pre>
     * // ** Pre and Post conditions ** //
     *
     * <b><u>Pre:</u></b>
     *
     * // Preference can not be Null
     * p != nil
     *
     * &&
     *
     *
     * // Preference can not be in PreferencesDB
     *
     * !(PreferencesDB.contains(p))
     *
     *
     * <b><u>Post:</u></b>
     *
     * //Preference must be stored into database
     * PreferencesDB.contains(p)
     *
     *
     *
     * </pre>
     */
    public void addLocalDaysForClasses(DaysForClasses p) throws PreferenceExistsException {
        if (localDPrefs != null) {
            if (localDPrefs.contains(p)) {
                throw new PreferenceExistsException();
            }
        }
        this.localDPrefs.add(p);
        Collections.sort(localDPrefs);
        setChanged();
        notifyObservers();
    }

    /**
     * Removes a Preference from the Preferences Database
     *
     * @param p Preference Object
     *
     * <pre>
     * // ** Pre and Post conditions ** //
     *
     * <b><u>Pre:</u></b>
     *
     * // Preference can not be Null
     * p != nil
     *
     * &&
     *
     * //Preference must be in the Preferences Database
     * PreferencesDB.contains(p)
     *
     *
     *
     * <b><u>Post:</u></b>
     *
     * // Preference can not be in PreferencesDB
     *
     * !(PreferencesDB.contains(p))
     *
     *
     * </pre>
     */
    public void removeLocalPreference(SchedulePreference p) {
        String table = "";
        if (p instanceof DaysForClasses) {
            DaysForClasses dfc = (DaysForClasses) p;
            table = "preferences_days";
            try {
                this.removeLocalDaysForClasses(dfc);
            } catch (PreferenceDoesNotExistException ex) {

            }
        }
        else {
            NoClassOverlap nco = (NoClassOverlap) p;
            table = "preferences_courses";
            try {
                this.removeLocalNoClassOverlap(nco);
            } catch (PreferenceDoesNotExistException ex) {

            }
        }


        setChanged();
        notifyObservers();
    }

    /**
     * Removes a Preference from the Preferences Database
     *
     * @param p Preference Object
     *
     * <pre>
     * // ** Pre and Post conditions ** //
     *
     * <b><u>Pre:</u></b>
     *
     * // Preference can not be Null
     * p != nil
     *
     * &&
     *
     * //Preference must be in the Preferences Database
     * PreferencesDB.contains(p)
     *
     *
     *
     * <b><u>Post:</u></b>
     *
     * // Preference can not be in PreferencesDB
     * 
     * !(PreferencesDB.contains(p))
     * 
     *
     * </pre>
     */
    public void removePreference(SchedulePreference p) {
        String table = "";
        if (p instanceof DaysForClasses) {
            DaysForClasses dfc = (DaysForClasses) p;
            table = "preferences_days";
        }
        else {
            NoClassOverlap nco = (NoClassOverlap) p;
            table = "preferences_courses";
        }

        System.out.println("In PreferencesDB.removePreference");
        SQLDB sqldb = new SQLDB();
        String insert = "name = '" + p.name + "'";
        sqldb.open();
        sqldb.removePrefStmt(table, insert);
        if (table.contains("preferences_courses")) {
            String s = "prefid = '" + p.name + "'";
            sqldb.removePrefStmt( "courses_to_preferences",  s);
        }
        PreferencesDB temp = sqldb.getPreferencesDB();
        this.data = temp.getDataOld();
        sqldb.close();
        setChanged();
        notifyObservers();
    }

    
    /**
     * Removes a Preference from the Preferences Database
     *
     * @param p Preference Object
     *
     * <pre>
     * // ** Pre and Post conditions ** //
     *
     * <b><u>Pre:</u></b>
     *
     * // Preference can not be Null
     * p != nil
     *
     * &&
     *
     * //Preference must be in the Preferences Database
     * PreferencesDB.contains(p)
     *
     *
     *
     * <b><u>Post:</u></b>
     *
     * // Preference can not be in PreferencesDB
     * 
     * !(PreferencesDB.contains(p))
     * 
     *
     * </pre>
     */
    public void removePreference(Preferences p) {
        System.out.println("In PreferencesDB.removePreference");
        SQLDB sqldb = new SQLDB();
        String insert = "name = '" + p.getName() + "'";
        sqldb.open();
        sqldb.removePrefStmt("preferences", insert);
        PreferencesDB temp = sqldb.getPreferencesDB();
        this.data = temp.getDataOld();
        sqldb.close();
        setChanged();
        notifyObservers();
    }

    /**
     * Returns the preference database as a collection
     * @return data the preference database as a collection.
     * <pre>
     * // ** Pre and Post conditions ** //
     *
     * <b><u>Pre:</u></b>
     *
     * // Collection<Preferences> cannot be null
     * data != nil
     *
     * <b><u>Post:</u></b>
     *
     * //Return data must match the current PreferencesDB
     * data = PreferencesDB.data
     *
     * 
     *
     * </pre>
     */
    public Vector<Preferences> getDataOld() {
        return data;
    }

    public void localToPermanent() {
        Iterator it = this.localCPrefs.iterator();

        while (it.hasNext()) {
            NoClassOverlap n = (NoClassOverlap) it.next();
            this.addNoClassOverlap(n);
        }

        it = this.localDPrefs.iterator();

        while (it.hasNext()) {
            DaysForClasses d = (DaysForClasses) it.next();
            this.addDaysForClasses(d);
        }
    }

    public void permanentToLocal() {
        this.localCPrefs = new Vector<NoClassOverlap>(this.cPrefs);
        this.localDPrefs = new Vector<DaysForClasses>(this.dPrefs);
        this.localData = new Vector<Preferences>(this.data);
    }

    /**
     *  Sets the data in the PreferencesDB
     *  @param data Collection<Preferences> The data to be set.
     * <pre>
     * // ** Pre and Post conditions ** //
     *
     * <b><u>Pre:</u></b>
     *
     * // data cannot be null
     * data != nil
     *
     * <b><u>Post:</u></b>
     *
     * // PreferencesDB data must equal the passed data
     * PreferencesDB.data = data
     *
     * </pre>
     */
    public void setData(Vector<Preferences> data) {
        this.data = data;
    }

    /**
     *  Sets the days preferences in the PreferencesDB
     *  @param data Collection<DaysForClasses> The data to be set.
     * <pre>
     * // ** Pre and Post conditions ** //
     *
     * <b><u>Pre:</u></b>
     *
     * // data cannot be null
     * data != nil
     *
     * <b><u>Post:</u></b>
     *
     * // PreferencesDB data must equal the passed data
     * PreferencesDB.data = data
     *
     * </pre>
     */
    public void setLocalDays(Vector<DaysForClasses> data) {
        this.localDPrefs = data;
    }

    /**
     *  Sets the days preferences in the PreferencesDB
     *  @param data Collection<DaysForClasses> The data to be set.
     * <pre>
     * // ** Pre and Post conditions ** //
     *
     * <b><u>Pre:</u></b>
     *
     * // data cannot be null
     * data != nil
     *
     * <b><u>Post:</u></b>
     *
     * // PreferencesDB data must equal the passed data
     * PreferencesDB.data = data
     *
     * </pre>
     */
    public void setDays(Vector<DaysForClasses> data) {
        this.dPrefs = data;
    }

    /**
     *  Iterates through the PreferencesDB to return correct one
     *  @param name String containing name of preference to return
     *  @return The preference that was being looked for, or null if it is not found
     * <pre>
     * // ** Pre and Post conditions ** //
     *
     * <b><u>Pre:</u></b>
     *
     * // name cannot be null
     * name != nil
     *
     * <b><u>Post:</u></b>
     *
     * // Name of returned preference matches the name parameter
     * p.name = name
     *
     * </pre>
     */
    public Preferences getPreferences(String name) {
        Iterator iterator = (this.data).iterator();
        while (iterator.hasNext()) {
            Preferences p = (Preferences) iterator.next();
            if ((p.name).compareTo(name) == 0) {
                return p;
            }
        }

        return null;
    }

   /**
    * Sets the local DaysForClasses vector of data to a given set of DFC data.
    * Alerts any/all observers. 
    *
    * @param data Data to set local DFC's to.
    *
    * Written by: Eric Liebowitz
    */
   public void setLocalDFCWithThisFromFile (Collection<DaysForClasses> data)
   {
      this.localDPrefs = new Vector(data);
      this.setChanged();
      this.notifyObservers();
   }
}



