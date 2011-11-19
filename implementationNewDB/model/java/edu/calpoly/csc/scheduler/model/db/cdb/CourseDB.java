package edu.calpoly.csc.scheduler.model.db.cdb;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;

import edu.calpoly.csc.scheduler.model.db.AbstractDatabase;
import edu.calpoly.csc.scheduler.model.db.DbData;
import edu.calpoly.csc.scheduler.model.db.SQLDB;
import edu.calpoly.csc.scheduler.model.schedule.Day;
import edu.calpoly.csc.scheduler.model.schedule.Week;

public class CourseDB extends AbstractDatabase<Course>
{
   // String constants to describe database
   public static final String            TABLENAME     = "courses";
   public static final String            NAME          = "name";
   public static final String            CATALOGNUM    = "catalognum";
   public static final String            DEPT          = "dept";
   public static final String            WTU           = "wtu";
   public static final String            SCU           = "scu";
   public static final String            NUMOFSECTIONS = "numofsections";
   public static final String            TYPE          = "type";
   public static final String            LENGTH        = "length";
   public static final String            DAYS          = "days";
   public static final String            ENROLLMENT    = "enrollment";
   public static final String            LAB           = "lab";

   public CourseDB(SQLDB sqldb, int scheduleID)
   {
      this.sqldb = sqldb;
      this.scheduleId = scheduleID;
   }
   
   protected void fillFields(Course data)
   {
      // Set fields and values
      fields = new LinkedHashMap<String, Object>();
      fields.put(NAME, data.getName());
      fields.put(CATALOGNUM, data.getCatalogNum());
      fields.put(DEPT, data.getDept());
      fields.put(WTU, data.getWtu());
      fields.put(SCU, data.getScu());
      fields.put(NUMOFSECTIONS, data.getNumOfSections());
      fields.put(TYPE, data.getType().toString());
      fields.put(LENGTH, data.getLength());
      fields.put(DAYS, sqldb.serialize(data.getDays()));
      fields.put(ENROLLMENT, data.getEnrollment());
      fields.put(LAB, sqldb.serialize(data.getLab()));
      fields.put(DbData.SCHEDULEID, scheduleId);
      fields.put(DbData.NOTE, data.getNote());
   }
   
   protected Course make(ResultSet rs)
   {
      // Retrieve by column name
      Course toAdd = new Course();
      try
      {
         toAdd.setName(rs.getString(NAME));
         toAdd.setCatalogNum(rs.getInt(CATALOGNUM));
         toAdd.setDept(rs.getString(DEPT));
         toAdd.setWtu(rs.getInt(WTU));
         toAdd.setScu(rs.getInt(SCU));
         toAdd.setNumOfSections(rs.getInt(NUMOFSECTIONS));
         toAdd.setType(rs.getString(TYPE));
         toAdd.setLength(rs.getInt(LENGTH));
         
         byte[] daysBuf = rs.getBytes(DAYS);
         // toAdd.setDays((Week) sqldb.deserialize(daysBuf));
         // TODO: Remove this later
         Week temp = new Week(new Day[]
               { Day.MON, Day.WED, Day.FRI });
         toAdd.setDays(temp);
         
         toAdd.setEnrollment(rs.getInt(ENROLLMENT));
         toAdd.setLab((Lab) sqldb.deserialize(rs.getBytes(LAB)));
         toAdd.setScheduleId(rs.getInt(DbData.SCHEDULEID));
         toAdd.setNote(rs.getString(DbData.NOTE));
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
   
   public Course getCourse(String dept, int catalogNum)
   {
      return make(sqldb.getSQLCourse(dept, catalogNum, scheduleId));
   }
}
