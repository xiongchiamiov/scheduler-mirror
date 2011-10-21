package edu.calpoly.csc.scheduler.model.schedule;

import java.io.Serializable;
import java.util.Vector;

import edu.calpoly.csc.scheduler.model.db.Time;
import edu.calpoly.csc.scheduler.model.db.cdb.Course;
import edu.calpoly.csc.scheduler.model.db.idb.*;
import edu.calpoly.csc.scheduler.model.db.cdb.*;

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
   protected Vector<Time> times = new Vector<Time>();
   /**
    * Which courses this instructor has been assigned to teach
    */
   protected Vector<Course> courses = new Vector<Course>();
   /**
    * How many wtu's an instructor has accrued
    */
   protected int wtu = 0;
   
   /**
    * Returns the times
    * 
    * @return the times
    */
   public Vector<Time> getTimes ()
   {
      return times;
   }
   /**
    * Sets the times to the given parameter.
    *
    * @param times the times to set
    */
   public void setTimes (Vector<Time> times)
   {
      this.times = times;
   }
   /**
    * Returns the courses
    * 
    * @return the courses
    */
   public Vector<Course> getCourses ()
   {
      return courses;
   }
   /**
    * Sets the courses to the given parameter.
    *
    * @param courses the courses to set
    */
   public void setCourses (Vector<Course> courses)
   {
      this.courses = courses;
   }
   /**
    * Returns the wtu
    * 
    * @return the wtu
    */
   public int getWtu ()
   {
      return wtu;
   }
   /**
    * Sets the wtu to the given parameter.
    *
    * @param wtu the wtu to set
    */
   public void setWtu (int wtu)
   {
      this.wtu = wtu;
   }
}
