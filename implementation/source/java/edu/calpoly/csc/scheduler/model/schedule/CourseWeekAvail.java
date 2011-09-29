package edu.calpoly.csc.scheduler.model.schedule;

import java.io.Serializable;
import java.util.BitSet;
import java.util.Vector;

import edu.calpoly.csc.scheduler.model.db.Time;
import edu.calpoly.csc.scheduler.model.db.cdb.Course;
import edu.calpoly.csc.scheduler.model.db.pdb.NoClassOverlap;

import edu.calpoly.csc.scheduler.model.db.cdb.*;

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
