package edu.calpoly.csc.scheduler.model.schedule;

import edu.calpoly.csc.scheduler.model.db.cdb.Course;

import java.util.Vector;

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
   private Vector<Integer> sections = new Vector<Integer>();
   /**
    * The course this class tracks sections for
    */
   private Course c;
   
   public SectionTracker (Course c)
   {
      this.c = c;
   }
   
   /**
    * Adds the next available section to our list of sections taught
    */
   public void addSection ()
   {
      this.curSection = getNextSection();
      
      if (canBookAnotherSection())
      {
         sections.add(curSection);
      }
   }
   
   /**
    * Removes the given section from our list of sections taught
    * 
    * @param s Section to remove
    * 
    * @return true if the section exists and is thusly removed
    */
   public boolean removeSection (int s)
   {
      boolean r = false;
      
      if (this.sections.contains(s))
      {
         r = true;
         sections.remove((Integer)s);
      }
      
      return r;
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
      int r = 0;
      int cur = 0;
      
      for (int i: this.sections)
      {
         if (i != (cur + 1))
         {
            r = cur;
         }
         cur = i;
      }
      
      return r;
   }

   /**
    * Tells us whether another section can be booked 
    * 
    * @return this.sections.size() == this.c.getNumOfSections()
    */
   public boolean canBookAnotherSection ()
   {
      return this.sections.size() == this.c.getNumOfSections();
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
    * Returns the Course this tracker tracks
    * 
    * @return the Course this tracker tracks
    */
   public Course getCourse()
   {
      return this.c;
   }
}
