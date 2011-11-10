package edu.calpoly.csc.scheduler.model.db.cdb;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;

import edu.calpoly.csc.scheduler.model.db.AbstractDatabase;
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
   public static final String            SCHEDULEID    = "scheduleid";
   public static final String NOTE = "note";

   public CourseDB(SQLDB sqldb, int scheduleID)
   {
      this.sqldb = sqldb;
      this.scheduleId = scheduleID;
   }

   
   protected boolean exists(Course data)
   {
      return sqldb.doesCourseExist(data);
   }
   
   protected void fillMaps(Course data)
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
      fields.put(SCHEDULEID, scheduleId);
      fields.put(NOTE, data.getNote());
      // Where clause
      wheres = new LinkedHashMap<String, Object>();
      wheres.put(CATALOGNUM, data.getCatalogNum());
      wheres.put(DEPT, data.getDept());
      wheres.put(TYPE, data.getType().toString());
      wheres.put(SCHEDULEID, scheduleId);
   }
   
   protected ResultSet getDataByScheduleId(int sid)
   {
      return this.sqldb.getSQLCourses(sid);
   }
   
   protected Course make(ResultSet rs)
   {
      // Retrieve by column name
      Course toAdd = new Course();
      try
      {
         String name = rs.getString("name");
         toAdd.setName(name);
         
         int catalogNum = rs.getInt("catalognum");
         toAdd.setCatalogNum(catalogNum);
         
         String dept = rs.getString("dept");
         toAdd.setDept(dept);
         
         int wtu = rs.getInt("wtu");
         toAdd.setWtu(wtu);
         
         int scu = rs.getInt("scu");
         toAdd.setScu(scu);
         
         int numOfSections = rs.getInt("numofsections");
         toAdd.setNumOfSections(numOfSections);
         
         String courseType = rs.getString("type");
         toAdd.setType(courseType);
         
         int length = rs.getInt("length");
         toAdd.setLength(length);
         
         byte[] daysBuf = rs.getBytes("days");
         // toAdd.setDays((Week) sqldb.deserialize(daysBuf));
         // TODO: Remove this later
         Week temp = new Week(new Day[]
               { Day.MON, Day.WED, Day.FRI });
         toAdd.setDays(temp);
         
         int enrollment = rs.getInt("enrollment");
         toAdd.setEnrollment(enrollment);
         
         // Deserialize Lab
         byte[] labBuf = rs.getBytes("lab");
         toAdd.setLab((Lab) sqldb.deserialize(labBuf));
         
         int scheduleid = rs.getInt("scheduleid");
         toAdd.setScheduleId(scheduleid);
         
         String note = rs.getString(NOTE);
         toAdd.setNote(note);
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
