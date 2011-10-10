package edu.calpoly.csc.scheduler.model.db.cdb;

import java.io.Serializable;

/**
 * Represents a course to be scheduled in the Scheduler.
 * 
 * @author Eric Liebowitz
 * @version Oct 10, 2011
 */
public class NewCourse implements Serializable
{
   /**
    * Represents the type of a course. The values currently defined are for
    * lecutres (LEC) and labs (LAB)
    * 
    * @author jasonkilroy
    * @version Oct 10, 2011
    */
   public enum CourseType
   {
      LEC, LAB,
   }

   /**
    * Name of this course (Like "Fundamentals of Computer Science I")
    */
   private String name = "";
   /**
    * Course's number in the catalog (like "101")
    */
   private int catalogNum = 0;
   /**
    * Course's department prefix (like "CPE")
    */
   private String dept = "";
   /**
    * Number of work time units this course is worth. This value is important
    * for figuring out how much an instructor is allowed to teach
    */
   private int wtu = 0;
   /**
    * Number of student credit units. This value is the number of units students
    * get for taking this course.
    */
   private int scu = 0;
   /**
    * Number of sections of this course that'll be offered
    */
   private int numOfSections = 0;
   /**
    * The type of this course (i.e. lecture or lab). Default is lecture.
    */
   private CourseType type = CourseType.LEC;
   /**
    * How long a course is taught in a week. Measured in number of half hours it
    * is taught in a given week.
    */
   private int length = 0;

   /**
    * Default constructor. Does nothing.
    */
   public NewCourse ()
   {
   }

   /**
    * Creates a course w/ the given name, dept prefix, and catalog number. If
    * you want to instantiate the numerous other fields of this class, call its
    * setters.
    * 
    * @param name
    *           Name you want to give the class
    * @param dept
    *           Course department prefix
    * @param catalogNum
    *           Catalog number of the class
    */
   public NewCourse (String name, String dept, int catalogNum)
   {
      this.name = name;
      this.catalogNum = catalogNum;
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
   public boolean equals (NewCourse c)
   {
      return (this.getName().equals(c.getName())
         && this.getCatalogNum() == c.getCatalogNum() && this.getType() == c
         .getType());
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
   @Override
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

   /**
    * Returns whether this course can be taught for at least 1 hour a day for a
    * given number of days/week.
    * 
    * @param days
    *           Number of days per week the course would be taught
    * 
    * @return (this.length / days) > 1;
    */
   public boolean canBeTaughtForDays (int days)
   {
      return (this.length / days) > 1;
   }
}
