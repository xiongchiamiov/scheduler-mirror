package edu.calpoly.csc.scheduler.model;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import edu.calpoly.csc.scheduler.model.db.*;


/**
 * The top-level model. Views will be passed this object when they are 
 * instantiated. Any view interaction w/ underlying data will be done via
 * this class. Thus, the getters/setters here will determine how the view and
 * the model can interact. 
 * 
 * @author Eric Liebowitz
 * @version Oct 3, 2011
 */
public class Model implements Serializable
{
   public static final int serialVersionUID = 42;
   
   private Database db;
   private String dept;
   
   public Model (String userId)
   {
      db = new Database();
      this.dept = db.getDept(userId);
   }
   
   /**
    * Returns a list of schedules which a given user has access to.
    * 
    * @param userId Userid of the person who's asking for the schedules
    * 
    * @return Map, keyed by schedule ids which yields schedule names.
    * 
    * @see Database#getSchedules(String)
    */
   public Map<String, Integer> getSchedules ()
   {
      return db.getSchedules(this.dept);
   }
   
   public Database openExistingSchedule (Integer sid)
   {
      db.openDB(sid, "");
      return this.db;
   }

   public Database openNewSchedule (String scheduleName)
   {
      db.openDB(-1, scheduleName);
      return this.db;
   }
   
   /**
    * Returns the db
    * 
    * @return the db
    */
   public Database getDb ()
   {
      return db;
   }

   /**
    * Sets the db to the given parameter.
    *
    * @param db the db to set
    */
   public void setDb (Database db)
   {
      this.db = db;
   }
}
