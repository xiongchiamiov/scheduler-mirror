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
   public static final String PERMISSION   = "permission";

   public UserDataDB(SQLDB sqldb, int scheduleID)
   {
      this.sqldb = sqldb;
      this.scheduleDBId = scheduleID;
   }

   protected void fillFields(UserData data)
   {
      // Set fields and values
      fields = new LinkedHashMap<String, Object>();
      fields.put(USERID, data.getUserId());
      fields.put(PERMISSION, data.getPermission());
      fields.put(DbData.SCHEDULEDBID, scheduleDBId);
   }

   protected UserData make(ResultSet rs)
   {
      // Retrieve by column name
      UserData toAdd = new UserData();
      try
      {
         toAdd.setUserId(rs.getString(USERID));
         toAdd.setPermission(rs.getInt(PERMISSION));
         toAdd.setScheduleDBId(rs.getInt(DbData.SCHEDULEDBID));
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
	protected String getCreateTableString() {
		String sqlstring = "CREATE TABLE " + TABLENAME + " (" +
		USERID + " " + SQLVARCHAR + " " + SQLNOTNULL + "," +
		PERMISSION + " " + SQLINT + " " + SQLNOTNULL + "," +
		DbData.DBID + " " + SQLINT + " " + SQLNOTNULL + " " + SQLAUTOINC + "," +
		DbData.SCHEDULEDBID + " " + SQLINT + " " + SQLNOTNULL + "," +
		SQLPRIMARYKEY + " (" + DbData.DBID + "), " +
		SQLUNIQUEKEY + " " + USERID + " (" + USERID + "," + DbData.SCHEDULEDBID + ")" +
		")";
		return sqlstring;
	}
}
