package edu.calpoly.csc.scheduler.model.schedule;

import edu.calpoly.csc.scheduler.model.db.Time;
import edu.calpoly.csc.scheduler.model.db.cdb.Course;
import edu.calpoly.csc.scheduler.model.db.idb.Instructor;

import java.util.HashMap;
import java.util.Vector;

/**
 *
 * @author Eric Liebowitz
 * @version Oct 17, 2011
 */
public class TreatmentTracker extends HashMap<Instructor, Treatment>
{  
   public boolean addInstructor (Instructor i)
   {
      boolean r = false;
      
      if (!this.containsKey(i))
      {
         this.put(i, new Treatment());
         r = true;
      }
      
      return r;
   }
   
   public int getWtu (Instructor i)
   {
      int r = -1;
      if (this.containsKey(i))
      {
         r = this.get(i).getWtu();
      }
      return r;
   }
}
