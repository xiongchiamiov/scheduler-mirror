package edu.calpoly.csc.scheduler.model.algorithm;



import java.util.Set;

import edu.calpoly.csc.scheduler.model.Course;
import edu.calpoly.csc.scheduler.model.Day;
import edu.calpoly.csc.scheduler.model.Instructor;
import edu.calpoly.csc.scheduler.model.Model;
import edu.calpoly.csc.scheduler.model.ScheduleItem;
import edu.calpoly.csc.scheduler.model.db.DatabaseException;
import edu.calpoly.csc.scheduler.model.db.IDatabase.NotFoundException;

/**
 * Represents a single, scheduled item in the overall schedule. Contains 
 * information regarding:
 * <ul>
 *    <li>The instructor teaching</li>
 *    <li>The course he/she will teach</li>
 *    <li>Where the course will be taught</li>
 *    <li>When (time and day(s)) it'll be taught</li>
 *    <li>If applicable, an additional SI for the "lab" component</li>
 * </ul>
 *
 * Each ScheduleItem has a "value", which represents how valuable it is to the
 * instructor it represents. A higher value means equates to a higher 
 * desirability. A desire of 0 is considered impossible. In fact, there's a 
 * special constant used to represent an "impossible" SI.
 *
 * @author Eric Liebowitz
 * @version 12nov10
 */
public class ScheduleItemDecorator implements Comparable<ScheduleItemDecorator>
{
	public static int IMPOSSIBLE = -1;
	
	
	private ScheduleItem item;
	
	private double value;
	
   /**
    * Builds an empty Schedule Item. None of its fields will be initialized.
    */
   public ScheduleItemDecorator (ScheduleItem item) {
	   this.item = item;
   }
   
   /**
    * Creates a new ScheduleItem whose fields are identical to a given 
    * ScheduleItem
    * 
    * @param si ScheduleItem we'll get all our fields from
 * @throws NotFoundException 
    */
   protected ScheduleItemDecorator (Model model, ScheduleItemDecorator si) throws DatabaseException
   {
	   this.item = si.item.createTransientCopy();
   }

   /**
    * Compares to ScheduleItem's. 
    *
    * @param that The object to compare with "this"
    *
    * @return true if all the fields in "s" are equal to those in "this". False
    *         otherwise. Note that the "locked" value is not incorporated in
    *         this check.
    */
   public boolean equals (ScheduleItem that)
   {
	   assert(false);
//      return (this.getInstructor().equals(that.getInstructor())   &&
//              this.getCourse().equals(that.getCourse())           &&
//              this.getLocation().equals(that.getLocation())       &&
//              this.getSection() == that.getSection()              &&
//              this.getDays().equals(that.getDays())               &&
//              this.getTimeRange().equals(that.getTimeRange())     &&
//              this.getLabs().equals(that.getLabs()));
	   return true;
   }
   
    public ScheduleItem getItem() { return item; }

	public double getValue() { return value; }

	/*public Integer getWtuTotal() {
		assert(false);
		// TODO Auto-generated method stub
		return null;
	}*/

	/*public boolean hasLabs() {
		assert(false);
		// TODO Auto-generated method stub
		return false;
	}*/
	
	//TODO Add ALL the methods from ScheduleItem here and just call through to the ScheduleItem.
	
	public void setEndHalfHour(int halfHour) throws DatabaseException {
		item.setEndHalfHour(halfHour);
		updateValue();
	}
	
	public void setStartHalfHour(int halfHour) throws DatabaseException {
		item.setStartHalfHour(halfHour);
		updateValue();
	}
	
	public void setDays(Set<Day> days) throws DatabaseException {
		item.setDays(days);
		updateValue();
	}
	
	public void setCourse(Course c) throws DatabaseException {
		item.setCourse(c);
		updateValue();
		
	}
	
	public String toString() {
		return item.toString();
	}
	
	public int compareTo(ScheduleItemDecorator other) {
		return Double.compare(this.getValue(), other.getValue());
	}
	
	private void updateValue() throws DatabaseException
	   {
	      double course = 0, time = 0;
	      boolean hasCourse = false, hasTime = false;

	      /*
	       * You can call "getValue" on an SI regarldess of whether all its fields
	       * are ready. If they aren't, they just won't be considered.
	       */
	      if (item.getInstructor() != null)
	      {
	         if (hasCourse = (item.getCourse() != null))
	         {
	            course = item.getInstructor().getCoursePreferences().get(item.getCourse().getID());
	         }
	         if (hasTime = (item.getDays() != null && item.getStartHalfHour() != 0 && item.getEndHalfHour() != 0))
	         {
	            time = this.getAvgPrefForTimeRange(item.getDays(),
	                                            item.getStartHalfHour(),
	                                            item.getEndHalfHour(),
	                                            item.getInstructor());
	         }
	      }

	      /*
	       * If any one part of the value was 0, its impossible for the instructor
	       * should teach this SI, so our value should reflect that with the 
	       * IMPOSSIBLE.
	       */
	      if ((hasCourse && ((int)course == 0))  ||
	          (hasTime   && ((int)time    == 0)))
	      {
	         this.value = IMPOSSIBLE;
	      }
	      else
	      {
	         this.value = course + time;
	      }
	   }
	
	private double getAvgPrefForTimeRange (Set<Day> days, int s, int e, Instructor instructor) throws DatabaseException
	   {
	      double total = 0;
	      int length = e - s;

	      /*
	       * Go over every day in the week
	       */
	      for (Day d : days)
	      {
	         int tempS = s;
	         double dayTotal = 0;
	         /*
	          * Get the desire from each half hour slot in the Time range
	          */
	         for (int i = 0; i < length; i++, tempS++)
	         {
	            int desire = instructor.getTimePreferences(d, tempS);
	            /*
	             * If a zero is encountered, immediately break out and return 0 to
	             * let the caller know that this Time range (or some part of it)
	             * cannot be applied to this Instructor
	             */
	            if (desire < 1)
	            {
	               return 0;
	            }
	            else
	            {
	               dayTotal += desire;
	            }
	         }
	         total += (dayTotal / length);
	      }

	      return total;
	   }

}