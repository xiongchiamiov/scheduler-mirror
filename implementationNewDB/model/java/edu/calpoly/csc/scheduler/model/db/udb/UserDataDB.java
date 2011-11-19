package edu.calpoly.csc.scheduler.model.db.udb;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;

import edu.calpoly.csc.scheduler.model.db.AbstractDatabase;
import edu.calpoly.csc.scheduler.model.db.DbData;
import edu.calpoly.csc.scheduler.model.db.SQLDB;

public class UserDataDB extends AbstractDatabase<UserData>
{
   public static final String TABLENAME    = "userdata";
   public static final String USERID       = "userid";
   public static final String SCHEDULENAME = "schedulename";
   public static final String PERMISSION   = "permission";

   public UserDataDB(SQLDB sqldb, int scheduleID)
   {
      this.sqldb = sqldb;
      this.scheduleId = scheduleID;
   }

   protected void fillFields(UserData data)
   {
      // Set fields and values
      fields = new LinkedHashMap<String, Object>();
      fields.put(USERID, data.getUserId());
      fields.put(SCHEDULENAME, data.getScheduleName());
      fields.put(PERMISSION, data.getPermission());
      fields.put(DbData.SCHEDULEID, scheduleId);
   }

   protected UserData make(ResultSet rs)
   {
      // Retrieve by column name
      UserData toAdd = new UserData();
      try
      {
         toAdd.setUserId(rs.getString(USERID));
         toAdd.setPermission(rs.getInt(PERMISSION));
         toAdd.setScheduleName(rs.getString(SCHEDULENAME));
         toAdd.setScheduleId(rs.getInt(DbData.SCHEDULEID));
         toAdd.setDbid(rs.getInt(DbData.DBID));
      }
      catch (SQLException e)
      {
         e.printStackTrace();
      }
      toAdd.verify();
      return toAdd;
   }

   protected String getTableName()
   {
      return TABLENAME;
   }

}
