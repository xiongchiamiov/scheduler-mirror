package edu.calpoly.csc.scheduler.model.algorithm;

import java.io.Serializable;
import java.util.BitSet;
import java.util.Collection;
import java.util.HashSet;
import java.util.Vector;

import edu.calpoly.csc.scheduler.model.Course;

/**
 * Represent the schedule preference which specifies which courses should not
 * be scheduled for times which overlap. 
 *
 * For example think Winter Quarter, CSC courses: most students take 102 and
 * 141 at the same time. So, those two courses should be not be scheduled at 
 * times which conflict, so that students are able to attend both courses.
 *
 * Duplicate courses are not allowed. 
 *
 * @author Eric Liebowitz
 * @version 26apr10
 */
public class NoClassOverlap extends SchedulePreference 
   implements Comparable<NoClassOverlap>, Serializable
{
   /**
    * 
    */
   private static final long serialVersionUID = -873957353656762065L;
   public HashSet<Course> cs = new HashSet<Course>();
   private BitSet bitSet = new BitSet();

   /**
    * Builds a preference for which courses should not overlap.
    *
    * @param name The name this preference will be referred to as
    * @param weight The priority (0 to 10...10 is non violatable)
    * @param cs The list of courses that cannot overlap. (Note that this can
    *           can be appended to). A deep copy of this set is made.  
    */
   public NoClassOverlap (String name, 
                          int weight, 
                          HashSet<Course> cs)
   {
      super (name, weight);
      for (Course c: cs)
      {
         this.addCourse(c);
      }
   }

   /**
    * Builds a preference for which courses should not overlap.
    *
    * @param name The name this preference will be referred to as
    * @param weigh The priority (0 to 10...10 is non-violatable)
    * @param cs A collection of courses which will be allowed to overlap. This 
    *           collection's items will be added to the AbstractCollection of 
    *           courses, so as to ensure that no duplicates are entered
    */
   public NoClassOverlap (String name, int weight, Collection<Course> cs)
   {
      super (name, weight);
      for (Course c: cs)
      {
         this.addCourse(c);
      }
   }

   /**
    * Adds a course to the list of courses which are not allowed to overlap.
    *
    * @param c Course to add
    * 
    * @return true if the list was altered as a result of the add. False 
    *         otherwise. 
    */
   public boolean addCourse (Course c)
   {
      bitSet.set(c.hashCode(), true);
      return this.cs.add(c);
   }

   /**
    * Removes a course from the list of courses which are not allowed to 
    * overlap.
    *
    * @param c Course to remove
    *
    * @return true if the list was altered as a result of the add. False
    *         otherwise.
    */
   public boolean delCourse (Course c)
   {
      bitSet.set(c.hashCode(), false);
      return this.cs.remove(c);
   }

   /**
    * Returns a Vector-representation of this object's list of courses. 
    *
    * @return a Vector-represtation of this object's list of courses. 
    */
   public Vector<Course> getCourseList ()
   {
      Vector<Course> list = new Vector<Course>();
      for (Course c: this.cs)
      {
         list.add(c);
      }
      return list;
   }

   /**
    * Returns the BitSet representation of this object. Each course within this
    * object will have a certain bit asserted in the BitSet.
    *
    * @return the BitSet representation of this object. 
    */
   public BitSet getBitSet ()
   {
      return this.bitSet;
   }

   /**
    * Returns the name of the preference...that's what you want to see, right?
    *
    * @return a string of the name of the preference
    */
   public String toString ()
   {
      return this.name + "\n";
   }

   /**
    * Compares to NCO objects, checking whether their HashSet of Courses are 
    * equals. Since this simply calls the equals method for Vector, it's 
    * really the equals method for Course's that you want to look into
    *
    * @param nco NCO to check for equality w/ this NCO
    *
    * @return this.getCourseList().equals(nco.getCourseList())
    */
   public boolean equals (NoClassOverlap nco)
   {
      return this.getCourseList().equals(nco.getCourseList());
   }
   
   /**
    * Sorts NoClassOverlap objects in descending order according to weight
    *
    * @param nco NCO to sort
    *
    * @return -1 if nco's weight is less than this, 0 if equal, 1 if greater
    */
   public int compareTo(NoClassOverlap nco) 
   {
      if (nco.weight > this.weight)
      {
          return -1;
      }
      else if (nco.weight < this.weight)
      {
          return 1;
      }
      else
      {
          return 0;
      }
   }
}
