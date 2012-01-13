package edu.calpoly.csc.scheduler.model.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * 
 * @author Eric Liebowitz
 * @version Nov 7, 2011
 */
public abstract class AbstractDatabase<T extends DbData>
{
   public static final long                serialVersionUID = 42;
   //SQL Data types
   public static final String SQLINT = "int";
   public static final String SQLVARCHAR = "varchar(255)";
   public static final String SQLBLOB = "blob";
   public static final String SQLBOOLEAN = "tinyint";
   //SQL extra conditions
   public static final String SQLNOTNULL = "NOT NULL";
   public static final String SQLDEFAULTNULL = "DEFAULT NULL";
   public static final String SQLAUTOINC = "AUTO_INCREMENT";
   public static final String SQLPRIMARYKEY = "PRIMARY KEY";
   public static final String SQLUNIQUEKEY = "UNIQUE KEY";

   protected ArrayList<T>                  data;
   protected int                           scheduleDBId;
   protected SQLDB                         sqldb;
   protected LinkedHashMap<String, Object> fields;
   protected LinkedHashMap<String, Object> wheres;

   public ArrayList<T> getData()
   {
      data = new ArrayList<T>();
      pullData();
      return data;
   }

   public void saveData(T data)
   {
      data.verify();

      data.setScheduleDBId(this.scheduleDBId);
      assert (this.scheduleDBId != -1);
      if (exists(data))
      {
         editData(data);
      }
      else
      {
         addData(data);
      }
   }

   protected void addData(T data)
   {
      fillFields(data);
      sqldb.executeInsert(getTableName(), fields);
      data.setDbid(sqldb.getLastGeneratedKey());
   }

   protected void editData(T data)
   {
      fillFields(data);
      fillWheres(data);
      sqldb.executeUpdate(getTableName(), fields, wheres);
   }

   public void removeData(T data)
   {
      data.verify();
      fillWheres(data);
      sqldb.executeDelete(getTableName(), wheres);
   }

   public boolean exists(T data)
   {
      fillWheres(data);
      return sqldb.doesItExist(sqldb.executeSelect(getTableName(), wheres,
            wheres));
   }

   protected void pullData()
   {
      data = new ArrayList<T>();

      ResultSet rs = sqldb.getDataByScheduleID(this.getTableName(), scheduleDBId);
      try
      {
         while (rs.next())
         {
            data.add(make(rs));
         }
      }
      catch (SQLException e)
      {
         e.printStackTrace();
      }
   }

   protected void fillWheres(T data)
   {
      wheres = new LinkedHashMap<String, Object>();
      wheres.put(DbData.DBID, data.getDbid());
   }

   protected abstract void fillFields(T data);

   protected abstract T make(ResultSet rs);

   protected abstract String getTableName();
   
   protected abstract String getCreateTableString();
}
