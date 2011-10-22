package edu.calpoly.csc.scheduler.model.db.idb;

import edu.calpoly.csc.scheduler.model.schedule.Week;
import edu.calpoly.csc.scheduler.model.db.*;
import edu.calpoly.csc.scheduler.model.db.cdb.Course;

/**
 * A made-up instructor who can always teach any course at any time, guaranteed.
 * 
 * @author Eric Liebowitz
 * @version Oct 22, 2011
 */
public class Staff extends Instructor
{
   @Override
   public boolean isAvailable (Week w, TimeRange tr)
   {
      return true;
   }
   
   @Override
   public boolean canTeach (Course c)
   {
      return true;
   }
   
   @Override
   public int getPreference (Course c)
   {
      return 10;
   }
   
   @Override
   public double getAvgPrefForTimeRange (Week w, Time s, Time e)
   {
      return 10;
   }
}
