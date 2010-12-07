package scheduler.generate;

import java.io.Serializable;
import java.util.Vector;
import scheduler.db.*;
import scheduler.db.instructordb.*;
import scheduler.db.coursedb.*;

/**
 * Used as the object for a hash of how an instructor has been treated by the
 * scheduling aglorithm, indexed by instructor name, in the Generate class. 
 * Contains information regarding the times and courses an instructor
 * has been given, along with how many WTU's he/she currently has.
 *
 * @author Eric Liebowitz
 * @version 07feb10
 */
public class Treatment implements Serializable
{
   /** 
    * Which time slots this instructor has been assigned to teach
    */
   protected Vector<Time> times;
   /**
    * Which courses this instructor has been assigned to teach
    */
   protected Vector<Course> courses;
   /**
    * How many wtu's an instructor has accrued
    */
   protected int wtu;

   /**
    * Constructs a Fairness object with no time assignments, no course
    * assignments, and a WTU count of 0.
    */
   protected Treatment ()
   {
      times = new Vector<Time>();
      courses = new Vector<Course>();
      wtu = 0;
   }
}
