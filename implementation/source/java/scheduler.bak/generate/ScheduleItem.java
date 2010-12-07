package scheduler.generate;

import java.io.PrintStream;
import java.io.Serializable;

import scheduler.db.*;
import scheduler.db.instructordb.*;
import scheduler.db.coursedb.*;
import scheduler.db.locationdb.*;

/**
 * Represents a single, scheduled course. Contains information regarding: which
 * course it represents; which instructor teaches the course, and its section;
 * time the course will start/end.
 *
 * @author Eric Liebowitz
 * @version 28jan10
 */
public class ScheduleItem implements Serializable
{
   public static final int serialVersionUID = 42;
   /**
    * The instructor for this scheduled item
    */
   public Instructor i;
   /**
    * The course for this scheduled item
    */
   public Course c;
   /**
    * The location for this scheduled item
    */
   public Location l;
   /**
    * Course section (will someday be removed)
    */
   public int section;
   /**
    * The days in the week this item is scheduled
    */
   public Week days;
   /**
    * The time at which this scheduled item starts
    */
   public Time start;
   /**
    * The time at which this scheduled item ends
    */
   public Time end;
   /**
    * Whether this SI is locked into the schedule or not
    */
   public boolean locked; 
   /**
    * The lab that goes with the class this SI represents, if any
    */
   public ScheduleItem lab;

   /**
    * Creates a scheduled course, complete with instructor, location
    * section, days taught, and start/end time.
    *
    * @param i Instructor teaching the course
    * @param c Course being taught
    * @param l Location course will be taught
    * @param days Days of the week this course will be taught
    * @param s Time the course starts
    * @param e Time the course ends
    */
   public ScheduleItem (Instructor i,
                        Course c,
                        Location l, 
                        int section, 
                        Week days,
                        Time s,
                        Time e)
   {
      this.i = i;
      this.c = c;
      this.l = l;
      this.section = section;
      this.days = days;
      this.start = s;
      this.end = e;
      this.locked = false;
      this.lab = null;
   }

   /**
    * Compares to ScheduleItem's. 
    *
    * @param s The object to compare with "this"
    *
    * @return true if all the fields in "s" are equal to those in "this". False
    *         otherwise. Note that the "locked" value is not incorporated in
    *         this check.
    */
   public boolean equals (ScheduleItem s)
   {
      return (this.i.equals(s.i)          &&
              this.c.equals(s.c)          &&
              this.l.equals(s.l)          &&
              this.section == s.section   &&
              this.days.equals(s.days)    &&
              this.start.equals(s.start)  &&
              this.end.equals(s.end)      &&
              this.lab.equals(s.lab));
   }

   

   /**
    * Displays all this object's fields in a visually easy-to-read form.
    */
   public String toString ()
   {
      return (this.c + " - Section: " + this.section + " - " + 
              this.c.getCourseType() + "\n" + 
              "Instructor:\t" + this.i + "\n" + 
              "In:\t\t" + this.l + "\n" + 
              "On:\t\t" + this.days + "\n" + 
              "Starts:\t\t" + this.start + "\n" + 
              "Ends:\t\t" + this.end + "\n" + 
              "Locked:\t\t" + this.locked + "\n");
   }

   /**
    * Dumps the data of this object to text as easy-to-parse Perl code.
    *
    * @param ps PrintStream to dump text to
    */
   public void dumpAsPerlText (PrintStream ps)
   {
      ps.println
      (
         "id => \"" + this.c + "_" + this.section + "_"   + 
            this.c.getCourseType() + "\",\n" +
         "course => \"" + this.c + "\",\n"                +
         "instructor => \""   + this.i.getId() + "\",\n"  +
         "location => \""     + this.l + "\",\n"          +
         "days => \""         + this.days + "\",\n"       +
         "s => \""            + this.start + "\",\n"      +
         "e => \""            + this.end   + "\","
      );
   }
}
