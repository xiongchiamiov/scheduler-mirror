package edu.calpoly.csc.scheduler.model.db;

import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Vector;
import java.io.Serializable;

/**
 *
 * @author Eric Liebowitz
 * @version Nov 7, 2011
 */
public abstract class AbstractDatabase<T extends DbData> 
{
   public static final long serialVersionUID = 42;
   
   protected ArrayList<T> data;
   protected int scheduleId;
   protected SQLDB sqldb;
   protected LinkedHashMap<String, Object> fields;
   protected LinkedHashMap<String, Object> wheres;
   
   public ArrayList<T> getData()
   {
      pullData();
      return data;
   }
   
   public void saveData (T data)
   {
      data.verify();

      data.setScheduleId(this.scheduleId);
      //TODO: Add this method to SQLDB.java
//      if (sqldb.exists(data))
      {
         editData(data);
      }
//      else
      {
//         addData(data);
      }
   }

   private void addData (T data)
   {
      fillMaps (data);
      sqldb.executeInsert(getTableName(), fields);
   }
   
   private void editData (T data)
   {
      fillMaps (data);
      sqldb.executeUpdate(getTableName(), fields, wheres);
   }
   
   public void removeData (T data)
   {
      data.verify();
      fillMaps(data);
      sqldb.executeDelete(getTableName(), wheres);
   }
   
   protected void pullData ()
   {
      data = new ArrayList<T>();

      ResultSet rs = this.getDataByScheduleId(scheduleId);
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
   
   protected abstract void fillMaps (T data);
   protected abstract ResultSet getDataByScheduleId (int sid);
   protected abstract T make (ResultSet rs);
   protected abstract String getTableName();
}
