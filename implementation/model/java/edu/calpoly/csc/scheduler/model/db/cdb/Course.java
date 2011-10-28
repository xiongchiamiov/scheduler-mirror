package edu.calpoly.csc.scheduler.model.db.cdb;

import java.io.Serializable;

import edu.calpoly.csc.scheduler.model.db.*;
import edu.calpoly.csc.scheduler.model.schedule.Day;
import edu.calpoly.csc.scheduler.model.schedule.Week;

/**
 * Represents a course to be scheduled in the Scheduler.
 * 
 * @author Eric Liebowitz
 * @version Oct 10, 2011
 */
public class Course extends DbData implements Serializable
{
   /**
    * Represents the type of a course. The values currently defined are for
    * lecutres (LEC) and labs (LAB)
    * 
    * @author Eric Liebowitz
    * @version Oct 10, 2011
    */
   public enum CourseType
   {
      LEC, LAB;
      
      @Override
      public String toString ()
      {
         return (this == LEC) ? "LEC" : "LAB";
      }
   }
   
   /**
    * Name of this course (Like "Fundamentals of Computer Science I")
    */
   private String name = "";
   /**
    * Course's number in the catalog (like "101")
    */
   private Integer catalogNum = 0;
   /**
    * Course's department prefix (like "CPE")
    */
   private String dept = "";
   /**
    * Number of work time units this course is worth. This value is important
    * for figuring out how much an instructor is allowed to teach
    */
   private Integer wtu = 0;
   /**
    * Number of student credit units. This value is the number of units students
    * get for taking this course.
    */
   private Integer scu = 0;
   /**
    * Number of sections of this course that'll be offered
    */
   private Integer numOfSections = 0;
   /**
    * The type of this course (i.e. lecture or lab). Default is lecture.
    */
   private CourseType type = CourseType.LEC;
   /**
    * How long a course is taught in a week. Measured in number of half hours it
    * is taught in a given week.
    */
   private Integer length = 0;
   /**
    * The days in the week this course is to be taught
    */
   private Week days;
   /**
    * Maximum number of students that can be enrolled in this course
    */
   private Integer enrollment = 0;
   /**
    * This course's lab. Can be null, which means it has no lab
    */
   private Lab lab = null;
   /**
    * How many half hours before/after this course that a lab can be taught. 
    * Default is 0, which'll put a lab right after the lecture.
    */
   private Integer labPad = 0;
   
   /**
    * The quarter this course applies to
    */
   private String quarterId = "";
   /**
    * The scheduler this course applies to
    */
   private Integer scheduleId;
   
   /**
    * Default constructor. Does nothing.
    */
   public Course () { }
   
   /**
    * Creates a course w/ the given name, dept prefix, and catalog number. If
    * you want to instantiate the numerous other fields of this class, call its
    * setters.
    * 
    * @param name Name you want to give the class
    * @param dept Course department prefix
    * @param catalogNum Catalog number of the class
    */
   public Course (String name, String dept, int catalogNum)
   {
      this.name = name;
      this.dept = dept;
      this.catalogNum = catalogNum;
   }

   /**
    * Creates a new Course object whose fields are identical to the given 
    * parameter. This makes it easy to "clone" course objects
    * 
    * @param c Coure to copy the information from
    * 
    * @.todo Write this
    */
   public Course (Course c)
   {
      
   }
   
   /**
    * Returns whether a course is equal to this one. Courses are equal if their
    * name, catalog number, and type are equal.
    * 
    * @param c
    *           Course to compare
    * 
    * @return True if both courses have the same name, catalog number, and type.
    */
   public boolean equals (Course c)
   {
      return (this.getName().equals(c.getName())         && 
              this.getCatalogNum() == c.getCatalogNum()  && 
              this.getType() == c.getType());
   }

   /**
    * Returns a hashcode for this object. It's composed of the addition of our
    * name's hashcode, our catalog number, and our type's hash code
    * 
    * @return this.getName().hashCode() + this.getCatalogNum() +
    *         this.getType().hashCode().
    * 
    * @see java.lang.Object#hashCode()
    */
   public int hashCode ()
   {
      return this.getName().hashCode() + this.getCatalogNum()
         + this.getType().hashCode();
   }

   /**
    * Returns the name
    * 
    * @return the name
    */
   public String getName ()
   {
      return name;
   }

   /**
    * Sets the name to the given parameter.
    * 
    * @param name
    *           the name to set
    */
   public void setName (String name)
   {
      this.name = name;
   }

   /**
    * Returns the catalogNum
    * 
    * @return the catalogNum
    */
   public int getCatalogNum ()
   {
      return catalogNum;
   }

   /**
    * Sets the catalogNum to the given parameter.
    * 
    * @param catalogNum
    *           the catalogNum to set
    */
   public void setCatalogNum (int catalogNum)
   {
      this.catalogNum = catalogNum;
   }

   /**
    * Returns the quarterId
    * 
    * @return the quarterId
    */
   public String getQuarterId ()
   {
      return quarterId;
   }

   /**
    * Sets the quarterId to the given parameter.
    *
    * @param quarterId the quarterId to set
    */
   public void setQuarterId (String quarterId)
   {
      this.quarterId = quarterId;
   }

   /**
    * Returns the scheduleId
    * 
    * @return the scheduleId
    */
   public Integer getScheduleId ()
   {
      return scheduleId;
   }

   /**
    * Sets the scheduleId to the given parameter.
    *
    * @param scheduleId the scheduleId to set
    */
   public void setScheduleId (Integer scheduleId)
   {
      this.scheduleId = scheduleId;
   }

   /**
    * Returns the dept
    * 
    * @return the dept
    */
   public String getDept ()
   {
      return dept;
   }

   /**
    * Sets the dept to the given parameter.
    * 
    * @param dept
    *           the dept to set
    */
   public void setDept (String dept)
   {
      this.dept = dept;
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
    * @param wtu
    *           the wtu to set
    */
   public void setWtu (int wtu)
   {
      this.wtu = wtu;
   }

   /**
    * Returns the scu
    * 
    * @return the scu
    */
   public int getScu ()
   {
      return scu;
   }

   /**
    * Sets the scu to the given parameter.
    * 
    * @param scu
    *           the scu to set
    */
   public void setScu (int scu)
   {
      this.scu = scu;
   }

   /**
    * Returns the numOfSections
    * 
    * @return the numOfSections
    */
   public int getNumOfSections ()
   {
      return numOfSections;
   }

   /**
    * Sets the numOfSections to the given parameter.
    * 
    * @param numOfSections
    *           the numOfSections to set
    */
   public void setNumOfSections (int numOfSections)
   {
      this.numOfSections = numOfSections;
   }

   /**
    * Returns the enrollment
    * 
    * @return the enrollment
    */
   public int getEnrollment ()
   {
      return enrollment;
   }

   /**
    * Sets the enrollment to the given parameter.
    *
    * @param enrollment the enrollment to set
    */
   public void setEnrollment (int enrollment)
   {
      this.enrollment = enrollment;
   }

   /**
    * Returns the type
    * 
    * @return the type
    */
   public CourseType getType ()
   {
      return type;
   }

   /**
    * Sets the type to the given parameter.
    * 
    * @param type
    *           the type to set
    */
   public void setType (CourseType type)
   {
      this.type = type;
   }
   
   /**
    * Sets the type to the given parameter.
    * 
    * @param type
    *           the string type to set
    */
   public void setType (String type)
   {
      if(type.equalsIgnoreCase("LEC"))
      {
         this.type = CourseType.LEC;
      }
      else if(type.equalsIgnoreCase("LAB"))
      {
         this.type = CourseType.LEC;
      }
   }

   /**
    * Returns how many half hours this course is taught in a week
    * 
    * @return number of hours this course is taught
    */
   public int getLength ()
   {
      return length;
   }

   /**
    * Sets the length to the given parameter. Note that the number you pass is
    * taken to be the number of <b>half hours</b> this course is taught in a
    * week.
    * 
    * @param length
    *           number of half hours this course is taught
    */
   public void setLength (int length)
   {
      this.length = length;
   }

   public int getDayLength ()
   {
      return this.length / this.days.size();
   }
   
   /**
    * Returns the days
    * 
    * @return the days
    */
   public Week getDays ()
   {
      return days;
   }

   /**
    * Sets the days to the given parameter.
    *
    * @param days the days to set
    */
   public void setDays (Week days)
   {
      this.days = days;
   }

   /**
    * Stubbed. Returns the lab paired w/ this course. Can be null.
    * 
    * @.todo Write this. It just returned 'null'.
    * 
    * @return The Course representing the lab. If there is none, null.
    */
   public Lab getLab ()
   {
      return this.lab;
   }
   
   /**
    * Sets this course's lab
    * 
    * @param lab The lab this course is to now have
    */
   public void setLab (Lab lab)
   {
      this.lab = lab;
   }
 
   /**
    * Stubbed. Returns whether this course has a lab or not. 
    * 
    * @.todo Write this. It always returns false right now.
    * 
    * @return getLab() != null
    */
   public boolean hasLab ()
   {
      return getLab() != null;
   }
   
   /**
    * Returns the number of half hours before/after this course that a lab is
    * allowed to be taught. 
    * 
    * @return the labPad
    */
   public int getLabPad ()
   {
      return labPad;
   }

   /**
    * Sets the labPad to the given parameter. A negative value means you're 
    * specifying a pad for the lab being taught <b>before</b> the lecture. A 
    * positive value is for <b>after</b>.
    *
    * @param labPad the labPad to set
    */
   public void setLabPad (int labPad)
   {
      this.labPad = labPad;
   }
   
   /**
    * Returns whether this course can be taught evenly for at least 1 hour a 
    * day for a given number of days.
    * 
    * @param days
    *           Number of days per week the course would be taught
    * 
    * @return (this.length % days) == 0 && (this.length / days) > 1;
    */
   public boolean canBeTaughtForDays (int days)
   {
      return ((this.length % days) == 0) && ((this.length / days) > 1);
   }
   
   /**
    * Returns the number of half-hours this course could be taught for a given
    * number of days. Note that integer trunctation occurs. So, if this class
    * is to be taught for 4 days and it's only got 5 half hours of length, 
    * you'll get the silly length of 1 half-hour per day...I doubt you want 
    * that. 
    * 
    * @param days Number of days to split our length over
    * 
    * @return this.getLength() / days
    * 
    * @see #canBeTaughtForDays (int)
    */
   public int splitLengthOverDays (int days)
   {
      return this.getLength() / days;
   }
   
   /**
    * Verifies that the vital fields of this Object  (i.e. those essential 
    * for generation of identification in a DB) are not null. "Vital" fields
    * are as follows:
    * 
    * <ul>
    *    <li>catalogNum</li>
    *    <li>days</li>
    *    <li>dept</li>
    *    <li>enrollment</li>
    *    <li>labPad</li>
    *    <li>length</li>
    *    <li>name</li>
    *    <li>numOfSections</li>
    *    <li>quarterId</li>
    *    <li>scu</li>
    *    <li>type></li>
    *    <li>wtu</li>
    * </ul>
    * 
    * @throws NullDataException if any field vital to generation or storage is
    *         null
    *
    * @see edu.calpoly.csc.scheduler.model.db.DbData#verify()
    */
   public void verify () throws NullDataException
   {
      if (catalogNum    == null ||
          days          == null ||
          dept          == null ||
          enrollment    == null ||
          labPad        == null ||
          length        == null ||
          name          == null ||
          numOfSections == null ||
          quarterId     == null ||
          scu           == null ||
          type          == null ||
          wtu           == null)
      {
         throw new NullDataException ();
      }
   }
   
   public Course getCannedData()
   {
      Course c = new Course();
      c.setName("Test");
      c.setCatalogNum(255);
      c.setDept("CSC");
      c.setWtu(4);
      c.setScu(4);
      c.setNumOfSections(2);
      c.setType("LEC");
      c.setLength(3);
      c.setDays(new Week());
      c.setEnrollment(50);
      c.setLab(new Lab());
      c.setLabPad(1);
      c.setQuarterId("w2011");
      c.setScheduleId(1);
      return c;
   }
}
