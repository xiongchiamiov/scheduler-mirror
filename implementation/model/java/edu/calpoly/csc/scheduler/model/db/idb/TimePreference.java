package edu.calpoly.csc.scheduler.model.db.idb;

import java.io.Serializable;
import java.lang.UnsupportedOperationException;

import edu.calpoly.csc.scheduler.model.db.Time;


/**
 * This method will house the time preference for an instructor for some  time.
 * 
 * @author Cedric Wienold & Eric Liebowitz
 *
 */
public class TimePreference implements Serializable
{
	/**
    * 
    */
   private static final long serialVersionUID = 3504977515321766168L;

   /** The desired time */
	private Time time;
	
	/** The desire value */
	private int desire;
	
	/**
	 * This constructor will create a preference for a particular time.
	 * 
	 * @param dayofweek the day of the week, 0=Sunday, 6=Saturday
	 * @param time the desired time.
	 * @param desire the level of desire, between 0 and 10.
	 */
	public TimePreference(Time time, int desire)
   {
		this.time = time;
		this.desire = desire;
	}
	
	/**
	 * This method returns the time of this preference.
	 * @return
	 */
	public Time getTime() 
   {
		return time;
	}
	
	/**
	 * This method returns the desire value of this preference.
	 * 
	 * @return the desire value of this preference.
	 */
	public int getDesire() 
   {
		return desire;
	}

   /**
    * @return a nice, String representation of this TimePreference
    */
   public String toString ()
   {
      return "Time: " + time + "; Desire: " + desire;
   }

   /**
    * Returns true if both TPrefs times are equal
    *
    * @param tp TimePreference to compare to this one
    *
    * @return true if all TPrefs times are equal
    *
    * Written by: Eric Liebowitz
    */
   public boolean equals (TimePreference tp)
   {
      return ((tp.time.equals(this.time)));
   }

}
