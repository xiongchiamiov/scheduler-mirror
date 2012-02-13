package edu.calpoly.csc.scheduler.model.schedule;

import edu.calpoly.csc.scheduler.model.db.cdb.Course;

import java.io.Serializable;
import java.util.Vector;
import java.util.BitSet;

/**
 * Used to keep track of what sections of a course have been scheduled. Provides
 * methods to get unique, non-used numbers to apply to courses being taught. 
 * You can also remove sections from the tracker. Subsequent calls to 
 * 'addSection' will add the lowest, unused section number available. 
 * 
 * @author Eric Liebowitz
 * @version Nov 17, 2011
 */
public class SectionTracker implements Serializable
{
   /**
	 * 
	 */
	private static final long serialVersionUID = 7335510502297286134L;
/**
    * List of section numbers that've been added
    */
   private BitSet sections = new BitSet();
   /**
    * The course this class tracks sections for
    */
   private Course c;
   
   /**
    * Current section that was just added
    */
   private int curSection;
   
   public SectionTracker (Course c)
   {
      this.c = c;
      this.curSection = 0;
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
      sections.set(curSection = getNextSection(), true);
      return true;
   }
   
   /**
    * Adds the given section to our list of booked sections. The section is not
    * added if it is already booked. 
    * 
    * @param s Section to add
    * 
    * @return true if the section did not exist and was thus added. False 
    *         otherwise. 
    */
   public boolean addSection (int s)
   {
      boolean r = false;
      if (r = !this.sections.get(s))
      {
         this.sections.set(s, true);
      }
      return r;
   }
   
   /**
    * Removes the given section from our list of sections taught
    * 
    * @param s Section to remove
    */
   public void removeSection (int s)
   {
      if (this.sections.get(s))
      {
         this.sections.set(s, false);
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
      return this.sections.cardinality();
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
