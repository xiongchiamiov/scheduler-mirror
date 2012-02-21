package edu.calpoly.csc.scheduler.model.db.idb;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.ArrayList;

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
   /**
    * 
    */
   private static final long serialVersionUID = 7424550318179981929L;
   private static final Staff me = new Staff();
   
   private Staff ()
   {
      super ();
      setFirstName("STAFF");
      setLastName("STAFF");
      setUserID("staff");
      setMaxWtu(-1);
      setOffice(Tba.getTba());
      setDisability(false);
   }
   
   public static Staff getStaff () 
   {
      return me;
   }
   
   @Override
   public boolean isAvailable (Week w, TimeRange tr, ScheduleDecorator sd)
   {
      return true;
   }
   
   @Override
   public boolean canTeach (Course c, int wtu)
   {
      return true;
   }
   
   @Override
   public boolean book (boolean b, Week w, TimeRange tr, ScheduleDecorator sd)
   {
      return true;
   }

   @Override
   public boolean book (boolean b, Day dayOfWeek, Time starttime, Time endtime, ScheduleDecorator sd)
   {
      return true;
   }
   
   @Override
   public int getPreference (Course c)
   {
      return 1;
   }
   
   @Override
   public int getPreference (Day d, Time t)
   {
      return 1;
   }
   
   @Override
   public double getAvgPrefForTimeRange (Week w, Time s, Time e)
   {
      return 1;
   }
   
   @Override
   public double getAvgPrefForTimeRange (Week w, TimeRange tr)
   {
      return 1;
   }
   
   //TODO ensure it accepts all times
   @Override
   public HashMap<Integer, LinkedHashMap<Integer, TimePreference>> getTimePreferences ()
   {
		HashMap<Integer, LinkedHashMap<Integer, TimePreference>> tps = new HashMap<Integer, LinkedHashMap<Integer, TimePreference>>();
		LinkedHashMap<Integer, TimePreference> times = new LinkedHashMap<Integer, TimePreference>();
		
		times.put(1000, new TimePreference(new Time(10, 0), 10));
		times.put(1030, new TimePreference(new Time(10, 30), 10));
		times.put(1100, new TimePreference(new Time(11, 0), 10));
		times.put(1130, new TimePreference(new Time(11, 30), 10));
		times.put(1200, new TimePreference(new Time(12, 0), 10));
		times.put(1230, new TimePreference(new Time(12, 30), 10));
		times.put(1300, new TimePreference(new Time(13, 0), 10));
		times.put(1330, new TimePreference(new Time(13, 30), 10));
		times.put(1400, new TimePreference(new Time(14, 0), 10));
		times.put(1430, new TimePreference(new Time(14, 30), 10));
		times.put(1500, new TimePreference(new Time(15, 0), 10));
		times.put(1530, new TimePreference(new Time(15, 30), 10));
		times.put(1600, new TimePreference(new Time(16, 0), 10));
		times.put(1630, new TimePreference(new Time(16, 30), 10));
		times.put(1700, new TimePreference(new Time(17, 0), 10));
		times.put(1730, new TimePreference(new Time(17, 30), 10));
		times.put(1800, new TimePreference(new Time(18, 0), 10));
		times.put(1830, new TimePreference(new Time(18, 30), 10));
		
		tps.put(0, times);
		tps.put(1, times);
		tps.put(2, times);
		tps.put(3, times);
		tps.put(4, times);
	    tps.put(5, times);
	    tps.put(6, times);
	    
	   return tps;
   }
}
