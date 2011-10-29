package edu.calpoly.csc.scheduler.model.db.idb;

import edu.calpoly.csc.scheduler.model.schedule.*;
import edu.calpoly.csc.scheduler.model.db.*;
import edu.calpoly.csc.scheduler.model.db.cdb.Course;
import edu.calpoly.csc.scheduler.model.db.ldb.Tba;

/**
 * A made-up instructor who can always teach any course at any time, guaranteed.
 * 
 * @author Eric Liebowitz
 * @version Oct 22, 2011
 */
public class Staff extends Instructor
{
   private static final Staff me = new Staff();
   
   private Staff ()
   {
      super ();
      setFirstName("STAFF");
      setLastName("STAFF");
      setUserID("staff");
      setCurWtu(-1);
      setMaxWtu(-1);
      setOffice(Tba.getTba());
   }
   
   public static Staff getStaff () 
   {
      return me;
   }
   
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
   public boolean book (boolean b, Week w, TimeRange tr)
   {
      return true;
   }

   @Override
   public boolean book (boolean b, Day dayOfWeek, Time starttime, Time endtime)
   {
      return true;
   }
   
   @Override
   public int getPreference (Course c)
   {
      return 10;
   }
   
   @Override
   public int getPreference (Day d, Time t)
   {
      return 10;
   }
   
   @Override
   public double getAvgPrefForTimeRange (Week w, Time s, Time e)
   {
      return 10;
   }
   
   @Override
   public double getAvgPrefForTimeRange (Week w, TimeRange tr)
   {
      return 10;
   }
}
