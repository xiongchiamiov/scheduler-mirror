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
   private boolean useLectureInstructor;

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
    * @param tethered whether we're tethered to our lecture or not
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

   /**
    * Sets the 'useLectureInstructor' field
    * 
    * @param b whether you want this lab to use the same instructor as its 
    *        lecture
    */
   public void setUseLectureInstructor (boolean b)
   {
      this.useLectureInstructor = b;
   }
   
   /**
    * Returns whether this lab should be taught by the same instructor as its
    * lecture.
    * 
    * @return whether this lab should be taught by the same instructor as its
    * lecture.
    */
   public boolean shouldUseLectureInstructor ()
   {
      return this.useLectureInstructor;
   }
}
