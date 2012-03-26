package edu.calpoly.csc.scheduler.model.algorithm;

import java.io.Serializable;

import edu.calpoly.csc.scheduler.model.Course;

public class CourseWeekAvail extends GenWeekAvail<Course, CourseDayAvail> 
                             implements Serializable
{
   private static final long serialVersionUID = 42;

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
