package edu.calpoly.csc.scheduler.model.schedule;

import edu.calpoly.csc.scheduler.model.db.cdb.Course;

import java.util.Vector;
import java.util.BitSet;

/**
 *
 * @author Eric Liebowitz
 * @version Oct 29, 2011
 */
public class SectionTracker
{
   /**
    * List of section numbers that've been added
    */
   private BitSet sections;
   /**
    * The course this class tracks sections for
    */
   private Course c;
   /**
    * Current number of sections that have been scheduled
    */
   private int numSections;
   /**
    * Current section that was just added
    */
   private int curSection;
   /**
    * Max number of sections
    */
   private int maxSections;
   
   public SectionTracker (Course c)
   {
      this.c = c;
      this.numSections = 0;
      this.curSection = 0;
      this.maxSections = c.getNumOfSections();
      sections = new BitSet(this.maxSections);
   }
   
   /**
    * Adds the next available section to our list of sections taught
    * 
    * @return true if a section was actually added. False otherwise. 
    * 
    * @see #canBookAnotherSection()
    */
   public boolean addSection ()
   {
      boolean r;
      
      if (r = canBookAnotherSection())
      {
         sections.set(curSection = getNextSection(), true);
         numSections ++;
      }
      
      return r;
   }
   
   /**
    * Removes the given section from our list of sections taught
    * 
    * @param s Section to remove
    * 
    * @return true if the section exists and is thusly removed
    */
   public void removeSection (int s)
   {
      if (this.sections.get(s))
      {
         this.sections.set(s, false);
         numSections --;
      }
   }
   
   /**
    * Gets the next section which can be taught. Note that this is not just 
    * 'curSection + 1', as this class supports non-consecutive section removal.
    * 
    * @return The lowest number missing in the sequence of section numbers we
    *         have stored
    */
   private int getNextSection ()
   {
      return this.sections.nextClearBit(0);
   }

   /**
    * Tells us whether another section can be booked 
    * 
    * @return this.sections.size() == this.c.getNumOfSections()
    */
   public boolean canBookAnotherSection ()
   {
      return this.numSections < this.maxSections;
   }
  
   /**
    * Resets section counting information for this tracker. Current section
    * is set to zero, and max number of sections is set to the given value
    * 
    * @param newMax New maximum number of sections
    */
   public void resetSectionCount (int newMax)
   {
      this.curSection = 0;
      this.numSections = 0;
      this.maxSections = newMax;
   }
   
   /**
    * Returns the curSection
    * 
    * @return the curSection
    */
   public int getCurSection ()
   {
      return curSection;
   }

   /**
    * Returns the current number of sections that have been added by this 
    * tracker.
    * 
    * @return the current number of sections that have been added by this 
    *         tracker.
    */
   public int getNumSections ()
   {
      return this.numSections;
   }
   
   /**
    * Returns the maxSections
    * 
    * @return the maxSections
    */
   public int getMaxSections ()
   {
      return maxSections;
   }

   /**
    * Returns the Course this tracker tracks
    * 
    * @return the Course this tracker tracks
    */
   public Course getCourse()
   {
      return this.c;
   }
}
