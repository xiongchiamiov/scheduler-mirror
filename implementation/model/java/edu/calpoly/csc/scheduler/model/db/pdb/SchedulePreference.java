package edu.calpoly.csc.scheduler.model.db.pdb;

import java.io.Serializable;

/**
 * Represents the parent-class for "schedule preferences". In particular, 
 * "schedule preferences" affect how schedule items are arranged in the 
 * schedule. (Such as what days courses are taught - MWF or TR - or which 
 * classes should not be taught at overlapping times - such as grad-level 
 * courses).
 *
 * @author Eric Liebowitz
 * @version 26apr10
 */
public class SchedulePreference implements Serializable
{
   /** The name of the preference */
   public String name;
   /** The weight of the preference */
   public int weight;

   /**
    * Default constrcutor. Gives a preferences a time and weight.
    *
    * @param name The name for the preference
    * @param weight The priority (0 to 10...10 is non-violatable)
    */
   public SchedulePreference (String name, int weight)
   {
      this.name   = name;
      this.weight = weight;
   }

   /**
    * Overrides the toString method
    *
    * @return the name of the object.
    *
    */
   public String toString() {
      return name;
   }

   /**
    * @return The name
    */
   public String getName() 
   {
      return this.name;
   }
}
