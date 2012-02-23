package edu.calpoly.csc.scheduler.model.algorithm;

import java.io.PrintStream;
import java.sql.Time;
import java.util.List;

import edu.calpoly.csc.scheduler.model.Course;
import edu.calpoly.csc.scheduler.model.Instructor;
import edu.calpoly.csc.scheduler.model.Location;
import edu.calpoly.csc.scheduler.model.Model;
import edu.calpoly.csc.scheduler.model.ScheduleItem;
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
public class ScheduleItemDecorator
{
	public static int IMPOSSIBLE = -1;
	
	
	ScheduleItem item;
	
	int value;
	
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
   protected ScheduleItemDecorator (Model model, ScheduleItemDecorator si) throws NotFoundException
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

	public int getValue() { return value; }

	public Integer getWtuTotal() {
		assert(false);
		// TODO Auto-generated method stub
		return null;
	}

	public boolean hasLabs() {
		assert(false);
		// TODO Auto-generated method stub
		return false;
	}
}