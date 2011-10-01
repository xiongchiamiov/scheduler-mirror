package edu.calpoly.csc.scheduler.model.db.idb;

import java.io.Serializable;

import edu.calpoly.csc.scheduler.model.db.cdb.Course;
import edu.calpoly.csc.scheduler.model.db.cdb.*;

/**
 * This class will house the preference an instructor has for a particular
 * course.
 * 
 * @author Cedric Wienold
 *
 */
public class CoursePreference implements Serializable
{
	public static final int serialVersionUID = 42;

	/** The prefered course */
	private Course course;
	
	/** The desire value */
	private int desire;

	/**
	 * This constructor will create a preference for a particular course.
	 * 
	 * @param course the desired course.
	 * @param desire the desire level, between 1 and 10.
	 */
	public CoursePreference(Course course, int desire) {
		this.course = course;
		this.desire = desire;
	}
	
	/**
	 * This method returns the course in this preference.
	 * 
	 * @return the course in this preference.
	 */
	public Course getCourse() {
		return course;
	}
	
	/**
	 * This method returns the desire value of this preference.
	 * 
	 * @return the desire value of this preference.
	 */
	public int getDesire() {
		return desire;
	}

   public String toString ()
   {
      return course + " has desire " + desire;
   }
}
