package edu.calpoly.csc.scheduler.model.schedule;

import java.io.PrintStream;
import java.io.Serializable;
import java.util.*;

import edu.calpoly.csc.scheduler.model.db.*;
import edu.calpoly.csc.scheduler.model.db.idb.*;
import edu.calpoly.csc.scheduler.model.db.cdb.*;
import edu.calpoly.csc.scheduler.model.db.ldb.*;

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
public class ScheduleItem implements Serializable, Cloneable, 
                                     Comparable<ScheduleItem>
{
   public static final long serialVersionUID = 42;

   /** 
    * Represents the value a ScheduleItem would have it it contains values
    * (course, time, days) which the instructor it represents cannot do (has a
    * preference of 0 for)
    */
   public static final int IMPOSSIBLE = -1;

   /**
    * The instructor for this scheduled item
    */
   private Instructor i;
   /**
    * The course for this scheduled item
    */
   private Course c;
   /**
    * The location for this scheduled item
    */
   private Location location;
   /**
    * Course section 
    */
   private int section;
   /**
    * The days in the week this item is scheduled
    */
   private Week days = new Week();

   private double value = 0;
   
   /**
    * Time range representing when this ScheduleItem starts/ends. This is what 
    * is used in place of "start" and "end". However, when I added this new 
    * field, loads of other classes already depended on the public "start" and
    * "end" fields. So, I've left them there. But be kind: use this instead
    * for all your time range needs. Or, be even more kind and use the 
    * setters/getters. (In fact, you have to be, as it's private). 
    */
   private TimeRange tr;
   /**
    * Whether this SI is locked into the schedule or not
    */
   private boolean locked; 
   /**
    * The lab that goes with the class this SI represents, if any
    */
   private List<ScheduleItem> labs = new Vector<ScheduleItem>();
   
   /**
    * Builds an empty Schedule Item. None of its fields will be initialized.
    */
   public ScheduleItem () { }
   
   /**
    * Creates a new ScheduleItem whose fields are identical to a given 
    * ScheduleItem
    * 
    * @param si ScheduleItem we'll get all our fields from
    */
   protected ScheduleItem (ScheduleItem si)
   {
      this.i = si.getInstructor();
      this.c = si.getCourse();
      this.location = si.getLocation();
      this.section = si.getSection();
      this.days = si.getDays();
      this.tr = new TimeRange(si.getStart(), si.getEnd());
   }

   /**
    * Creates a ScheduleItem w/ its fields initialized to the give values
    * 
    * @param i Instructor teaching the item
    * @param c Course the item teaches
    * @param l Location the item will be taught in
    * @param section Section of the course
    * @param days Days the item will be taught
    * @param tr TimeRange the item is taught in
    */
   protected ScheduleItem (Instructor i, Course c, Location l, int section, 
      Week days, TimeRange tr)
   {
      setInstructor(i);
      setCourse(c);
      setLocation(l);
      setSection(section);
      setDays(days);
      setTimeRange(tr);
   }
   
   public ScheduleItem clone()
   {
      ScheduleItem si = null;
      try
      {
         si = (ScheduleItem)super.clone();
      }
      catch (CloneNotSupportedException e)
      {
         e.printStackTrace();
      }

      return si;
   }
   
   /**
    * Compares this to another ScheduleItem according to the double returned
    * by their "getValue" method. The return of this method is simply the return
    * of "Double.compare(double, double)".
    *
    * @param si The other ScheduleItem whose value is to be compare with this
    *           one's.
    *
    * @return Double.compare(this.getValue(), si.getValue())
    */
   public int compareTo (ScheduleItem si)
   {
      return Double.compare(this.getValue(), si.getValue());
   }

   /**
    * Dumps the data of this object to text as easy-to-parse Perl code. In 
    * particular, the following pieces of data are dumped into a hash ref to
    * be eval'd by Perl:
    * <ul>
    *    <li>id => this.c_this.sectio_this.c.getCourseType()</li>
    *    <li>course => this.c</li>
    *    <li>instructor => this.i.getId()</li>
    *    <li>location => this.l</li>
    *    <li>days => this.days</li>
    *    <li>start => this.start</li>
    *    <li>end => this.end</li>
    *    <li>value => this.value</li>
    * </ul>
    *
    * @param ps PrintStream to dump text to
    */
   public void dumpAsPerlText (PrintStream ps)
   {
      ps.println
      (
         "id => \"" + this.c + "_" + this.section + "_"   + 
            this.c.getType() + "\",\n" +
         "course => \"" + this.c + "\",\n"                +
         "instructor => \""   + this.i.getId() + "\",\n"  +
         "location => \""     + this.location + "\",\n"          +
         "days => \""         + this.days + "\",\n"       +
         "s => \""            + this.tr.getS() + "\",\n"      +
         "e => \""            + this.tr.getE() + "\",\n"      + 
         "value => "          + this.getValue() + ","
      );
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
      return (this.getInstructor().equals(that.getInstructor())   &&
              this.getCourse().equals(that.getCourse())           &&
              this.getLocation().equals(that.getLocation())       &&
              this.getSection() == that.getSection()              &&
              this.getDays().equals(that.getDays())               &&
              this.getTimeRange().equals(that.getTimeRange())     &&
              this.getLabs().equals(that.getLabs()));
   }

   /** 
    * Gets the course associated w/ this SI. Can be null if you haven't set it
    * yet.
    *
    * @return the course associated w/ this SI. Can be null.
    */
   public Course getCourse ()
   {
      return this.c;
   }

   /**
    * Gets the Week object which represents the days this SI will be taught. Can
    * be null if you haven't set it yet. 
    *
    * @return The Week object which represents the days this SI will be taught. 
    *         Can be null.
    */
   public Week getDays ()
   {
      return this.days;
   }
   
   /** 
    * Returns the end time for this ScheduleItem. Can be null if you haven't 
    * set the time yet.
    *
    * @return the end time for this ScheduleItem. Can be null
    */
   public Time getEnd () 
   {
      return this.tr.getE();
   }

   /** 
    * Gets the instructor associated with this SI. Can be null if you haven't
    * set it yet. 
    *
    * @return the Instructor associated w/ this SI. Can be null.
    */
   public Instructor getInstructor ()
   {
      return this.i;
   }

   /** 
    * Gets the SI-lab components for this ScheduleItem. Can be null if you 
    * haven't set it yet, or if this SI isn't even supposed to have one
    *
    * @return the SI-lab component for this SI. Can be null.
    * 
    */
   public List<ScheduleItem> getLabs ()
   {
      return this.labs;
   }

   /**
    * Returns the Location associated w/ this ScheduleItem. Can be null, if you
    * havne't already set it. 
    *
    * @return the Location associated w/ this object. Can be null if you haven't
    *         yet set it.
    */
   public Location getLocation ()
   {
      return this.location;
   }

   /**
    * Returns the section
    * 
    * @return the section
    */
   public int getSection ()
   {
      return section;
   }

   /** 
    * Returns the start time for this ScheduleItem. Can be null if you haven't
    * set the time yet.
    *
    * @return the start time for this ScheduleItem. Can be null.
    */
   public Time getStart ()
   {
      if (this.tr == null)
      {
         return null;
      }
      return this.tr.getS();
   }

   /** 
    * Gets the TimeRange for this ScheduleItem. Can be null, if you haven't set
    * the times yet.
    *
    * @return the TimeRange for this SI. Can be null.
    */
   public TimeRange getTimeRange ()
   {
      return this.tr;
   }

   /**
    * Returns how "valuable" this ScheduleItem is. "value" is determined by how 
    * closely the fields of this object adhere to the preferences of the 
    * instructor it represents.
    *
    * @return the sum of all the instructors preferences for this object's 
    *         fields. Thus, a higher number means a higher value.
    */
   public double getValue ()
   {
      return value;
   }

   /**
    * Returns the wtuTotal
    * 
    * @return the wtuTotal
    */
   public int getWtuTotal ()
   {
      int wtu = 0;
      Course lec = getCourse();
      Course lab = getCourse().getLab();
      
      if (lec != null)
      {
         wtu += lec.getWtu();
         if (lab != null)
         {
            wtu += lab.getWtu();
         }
      }
      
      return 0;
   }

   /**
    * Determines whether there're any lab components to this ScheduleItem.
    * 
    * @return this.labs != null && this.labs.size() > 0
    */
   public boolean hasLabs () 
   {
      return this.labs != null && this.labs.size() > 0;
   }

   /**
    * Tells you if this ScheduleItem overlaps with any of a list of 
    * ScheduleItems.
    * 
    * @param sis List of ScheduleItems to check for overlappingness
    * 
    * @return true if this ScheduleItem overlaps with any of the ScheduleItems
    *         in the provided list
    */
   public boolean overlaps (List<ScheduleItem> sis)
   {
      boolean r = true;
      for (ScheduleItem si: sis)
      {
         r &= this.overlaps(si);
      }
      return r;
   }
   
   /**
    * Used to determine whether this SI overlaps with another one. 
    *
    * @param si the SI we'll check for overlapping-ness.
    *
    * @return (this.tr.overlaps(si.getTimeRange()) && this.days.overlaps(si.getDays()))
    */
   public boolean overlaps(ScheduleItem si)
   {
      return (this.tr.overlaps(si.getTimeRange()) &&
              this.days.overlaps(si.getDays()));
   }

   /** 
    * Sets the course for this SI, along with the "section" field (which'll be
    * whatever the "getSection" method returns for "c".
    *
    * @param c The course you wish to apply to this SI.
    */
   public void setCourse (Course c)
   {
      this.c = c;
      updateValue();
   }

   /**
    * Sets the days this ScheduleItem is taught
    * 
    * @param days the days this ScheduleItem is taught
    */
   public void setDays (Week days)
   {
      this.days = days;
      updateValue();
   }

   /** 
    * Sets the instructor for this SI.
    *
    * @param i The Instructor you wish to apply to this SI.
    */
   public void setInstructor (Instructor i)
   {
      this.i = i;
   }

   /** 
    * Sets the lab for this SI. 
    *
    * @param lab The SI you wish to apply to this SI's lab field.
    */
   public void addLab (ScheduleItem lab)
   {
      this.labs.add(lab);
   }

   /**
    * Sets the location to the given parameter.
    *
    * @param location the location to set
    */
   public void setLocation (Location location)
   {
      this.location = location;
   }

   /**
    * Sets the section to the given parameter.
    *
    * @param section the section to set
    */
   public void setSection (int section)
   {
      this.section = section;
   }

   public void setTimeRange (TimeRange tr)
   {
      this.tr = new TimeRange(tr.getS(), tr.getE());
      updateValue();
   }

   /**
    * Updates the value of this ScheduleItem. Uses the instructor's preferences
    * for this item's course and days/times to compute the value. If the 
    * instrutor is null, nothing is done. And, if there is no course and/or 
    * days/times, they are simply not considered. If any one preference is 0, 
    * IMPOSSIBLE will be returned. 
    */
   private void updateValue()
   {
      double course = 0, time = 0;
      boolean hasCourse = false, hasTime = false;

      /*
       * You can call "getValue" on an SI regarldess of whether all its fields
       * are ready. If they aren't, they just won't be considered.
       */
      if (this.i != null)
      {
         if (hasCourse = (this.c != null))
         {
            course = this.i.getPreference(this.c);
         }
         if (hasTime = (this.days != null && this.tr != null))
         {
            time = i.getAvgPrefForTimeRange(this.days, 
                                            this.tr.getS(), 
                                            this.tr.getE());
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
   
   /**
    * Displays all this object's fields in a visually easy-to-read form.
    */
   public String toString ()
   {
      String r = (this.c + " - Section: " + this.section + " - " + 
              this.c.getType() + "\n" + 
              "Instructor:\t" + this.i + "\n" + 
              "In:\t\t" + this.location + "\n" + 
              "On:\t\t" + this.days + "\n" + 
              "Starts:\t\t" + this.tr.getS() + "\n" + 
              "Ends:\t\t" + this.tr.getE() + "\n" + 
              "Locked:\t\t" + this.locked + "\n" +
              "Value :\t\t" + this.getValue() + "\n");
      if (this.hasLabs())
      {
         r += this.getLabs().toString();
      }
      return r;
   }
   
   public boolean equals(Object o)
   {
	   if(o == null)
	   {
		   return false;
	   }
	   if(this.getClass() != o.getClass())
	   {
		   return false;
	   }
	   if(this == o)
	   {
		   return true;
	   }
	   return this.equals((ScheduleItem)o);
   }
}
