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
    * The current section we've most recently added
    */
   private int curSection = 0;
   /**
    * List of section numbers that've been added
    */
   private BitSet sections;
   /**
    * The course this class tracks sections for
    */
   private Course c;
   /**
    * Max number of sections
    */
   private int maxSections;
   
   public SectionTracker (Course c)
   {
      this.c = c;
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
         this.curSection = getNextSection();
         sections.set(this.curSection, true);
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
      this.sections.set(s, false);
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
      return this.sections.cardinality() != this.maxSections;
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
