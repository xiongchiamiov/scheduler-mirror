package edu.calpoly.csc.scheduler.model.db.cdb;

/**
 *
 * @author Eric Liebowitz
 * @version Oct 25, 2011
 */
public class Lab extends Course
{
   private boolean tethered;
   private Course component;

   public Lab () { }
   
   public Lab (String name, String dept, int catalogNum)
   {
      super(name, dept, catalogNum);
      setType(Course.CourseType.LAB);
   }

   /**
    * Returns whether we're tethered to our lecture or not
    * 
    * @return whether we're tethered to our lecture or not
    */
   public boolean isTethered ()
   {
      return tethered;
   }

   /**
    * Sets whether we're tethered to our lecture or not
    *
    * @param whether we're tethered to our lecture or not
    */
   public void setTethered (boolean tethered)
   {
      this.tethered = tethered;
   }

   /**
    * Returns the lecture component to this lab
    * 
    * @return the lecture component to this lab
    */
   public Course getComponent ()
   {
      return component;
   }

   /**
    * Sets our lecture component to the given parameter.
    *
    * @param component the lecture component to set
    */
   public void setComponent (Course component)
   {
      this.component = component;
   }
}
