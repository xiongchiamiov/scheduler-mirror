package edu.calpoly.csc.scheduler.model.udb;

import edu.calpoly.csc.scheduler.model.db.DbData;
import edu.calpoly.csc.scheduler.model.db.NullDataException;

public class UserData extends DbData
{
   public static final int READONLY    = 0;
   public static final int EDIT_PREFERENCES    = 1;
   public static final int ADMIN    = 2;
   
   private String userId;
   private String scheduleName;
   private int    permission = -1;

   @Override
   public void verify() throws NullDataException
   {
      if (userId == null)
      {
         throw new NullDataException();
      }
      if (scheduleName == null)
      {
         throw new NullDataException();
      }
      if (permission < 0 || permission > 2)
      {
         throw new NullDataException();
      }
   }

   public int getPermission()
   {
      return permission;
   }

   public void setPermission(int permission)
   {
      this.permission = permission;
   }

   /**
    * @return the userId
    */
   public String getUserId()
   {
      return userId;
   }

   /**
    * @param userId
    *           the userId to set
    */
   public void setUserId(String userId)
   {
      this.userId = userId;
   }

   /**
    * @return the scheduleName
    */
   public String getScheduleName()
   {
      return scheduleName;
   }

   /**
    * @param scheduleName
    *           the scheduleName to set
    */
   public void setScheduleName(String scheduleName)
   {
      this.scheduleName = scheduleName;
   }

}
