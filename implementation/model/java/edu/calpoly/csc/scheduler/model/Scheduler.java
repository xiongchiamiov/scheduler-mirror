package edu.calpoly.csc.scheduler.model;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import edu.calpoly.csc.scheduler.model.db.*;
import edu.calpoly.csc.scheduler.model.schedule.*;

/**
 * The top-level model. Views will be passed this object when they are 
 * instantiated. Any view interaction w/ underlying data will be done via
 * this class. Thus, the getters/setters here will determine how the view and
 * the model can interact. 
 * 
 * @author Eric Liebowitz
 * @version Oct 3, 2011
 */
public class Scheduler implements Serializable
{
   public static final int serialVersionUID = 42;
   
   private Database db;
   
   public Scheduler (String username, Integer sid)
   {
      db = new Database();
   }
   
   public Map<Integer, String> getSchedules (String userId)
   {
      String dept = db.getDept(userId);
      
      return db.getSchedules(dept);
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

   public static void main (String[] args)
   {
      Database db = new Database();
   }
}
