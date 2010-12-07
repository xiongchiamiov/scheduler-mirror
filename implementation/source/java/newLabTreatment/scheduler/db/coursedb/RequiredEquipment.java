package scheduler.db.coursedb;

import java.io.Serializable;

/**
 * This class specifies the required equipment for a course.
 *
 * @author Cedric Wienold
 * @version 15apr10
 * 
 * Class modularized by: Eric Liebowitz
 */
public class RequiredEquipment implements Cloneable, Serializable
{
      /** Whether the course requires a smartroom or not. */
   private boolean smartroom;
      /** Whether the course requires an overhead. */
   private boolean overhead;
      /** Whether the course requires laptop connectivity.*/
   private boolean laptopconnectivity;

   /**
    * This constructor will make a class holding current required
    * equipment.
    * 
    * @param isSmartRoom whether this need be a smart room.
    * @param hasOverhead whether this need have an overhead.
    * @param hasLaptopConnectivity whether this need have laptop
    * 								connectivity.
    */
   public RequiredEquipment (boolean isSmartRoom, 
                             boolean hasOverhead,
                             boolean hasLaptopConnectivity) 
   {
      this.smartroom = isSmartRoom;
      this.laptopconnectivity = hasLaptopConnectivity;
      this.overhead = hasOverhead;
   }

   /**
    * Returns whether this need be a smart room.
    * 
    * @return whether this need be a smart room.
    */
   public boolean isSmartroom() 
   {
      return smartroom;
   }

   /**
    * Returns whether this need have an overhead.
    * 
    * @return whether this need have an overhead.
    */
   public boolean hasOverhead()
   {
      return overhead;
   }

   /**
    * Returns whether this need have laptop connectivity.
    * 
    * @return whether this need have laptop connectivity.
    */
   public boolean hasLaptopConnectivity()
   {
      return laptopconnectivity;
   }

   /**
    * Standard cloning method.
    *
    * By: Eric Liebowitz
    */
   public RequiredEquipment clone ()
   {
      try
      {
         return (RequiredEquipment)super.clone();
      }
      catch (CloneNotSupportedException e)
      {
         System.err.println (e);
      }
      return null;
   }
}
