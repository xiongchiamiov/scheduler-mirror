package scheduler.generate;

import java.io.Serializable;
import java.util.BitSet;
import java.util.Vector;

import scheduler.db.Time;
import scheduler.db.coursedb.*;
import scheduler.db.preferencesdb.*;

public class CourseWeekAvail extends GenWeekAvail<Course, CourseDayAvail> 
                             implements Serializable
{
   private static final int serialVersionUID = 42;

   public CourseWeekAvail (NoClassOverlap nco)
   {
      super ();

      // 7 days in a Week
      for (int i = 0; i < 7; i ++)
      {
         this.add(new CourseDayAvail(nco));
      }
   }
}
