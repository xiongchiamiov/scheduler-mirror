package edu.calpoly.csc.scheduler.model.db.cdb;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import edu.calpoly.csc.scheduler.model.db.DatabaseAPI;
import edu.calpoly.csc.scheduler.model.db.SQLDB;
import edu.calpoly.csc.scheduler.model.schedule.Day;
import edu.calpoly.csc.scheduler.model.schedule.Week;

public class CourseDB implements DatabaseAPI<Course>
{
   private ArrayList<Course> data;
   private SQLDB             sqldb;
   private int               scheduleID;

   public CourseDB(SQLDB sqldb, int scheduleID)
   {
      this.sqldb = sqldb;
      this.scheduleID = scheduleID;
      initDB();
   }

   @Override
   public ArrayList<Course> getData()
   {
      pullData();
      return data;
   }
   
   @Override
   public void saveData(Course data)
   {
      data.verify();
      data.setScheduleId(scheduleID);
      if(sqldb.doesCourseExist(data))
      {
    	  System.out.println("Editing data: course");
         editData(data);
      }
      else
      {
    	  System.out.println("Adding data: course");
         addData(data);
      }
   }

   private void initDB()
   {
      data = new ArrayList<Course>();
      // TODO: REMOVE THIS
      // addData(new Course().getCannedData());
      pullData();
   }

   private void pullData()
   {
      data = new ArrayList<Course>();
      ResultSet rs = sqldb.getSQLCourses(scheduleID);
      try
      {
         while (rs.next())
         {
            data.add(makeCourse(rs));
         }
      }
      catch (SQLException e)
      {
         e.printStackTrace();
      }
   }

   public Course getCourseByID(int id)
   {
      return makeCourse(sqldb.getSQLCourseByID(id));
   }

   public Course getCourse(String dept, int catalogNum)
   {
      return makeCourse(sqldb.getSQLCourse(dept, catalogNum, scheduleID));
   }

   private Course makeCourse(ResultSet rs)
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

         // Deserialize Days
//         byte[] daysBuf = rs.getBytes("days");
//         if (daysBuf != null)
//         {
//            try
//            {
//               ObjectInputStream objectIn;
//               objectIn = new ObjectInputStream(new ByteArrayInputStream(
//                     daysBuf));
//               toAdd.setDays((Week) objectIn.readObject());
//            }
//            catch (Exception e)
//            {
//               e.printStackTrace();
//            }
//         }
         //TODO: Remove this later
         Week temp = new Week(new Day[]{Day.MON, Day.WED, Day.FRI});
         toAdd.setDays(temp);

         int enrollment = rs.getInt("enrollment");
         toAdd.setEnrollment(enrollment);

         // Deserialize Lab
         byte[] labBuf = rs.getBytes("lab");
         if (labBuf != null)
         {
            try
            {
               ObjectInputStream objectIn;
               objectIn = new ObjectInputStream(
                     new ByteArrayInputStream(labBuf));
               // I CHANGED THIS - Eric
               toAdd.setLab((Lab) objectIn.readObject());
            }
            catch (Exception e)
            {
               e.printStackTrace();
            }
         }

         String quarterid = rs.getString("quarterid");
         toAdd.setQuarterId(quarterid);

         int scheduleid = rs.getInt("scheduleid");
         toAdd.setScheduleId(scheduleid);

      }
      catch (SQLException e)
      {
         e.printStackTrace();
      }
      toAdd.verify();
      return toAdd;
   }

   private void addData(Course data)
   {
      data.verify();
      // Create insert string
      String insertString = "insert into courses ("
            + "name, catalognum, dept, wtu, scu, "
            + "numofsections, type, length, days, "
            + "enrollment, lab, labpad, quarterid, scheduleid)"
            + "values (?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
      // Create prepared statement
      PreparedStatement stmt = sqldb.getPrepStmt(insertString);
      try
      {
         // Set values
         stmt.setString(1, data.getName());
         stmt.setInt(2, data.getCatalogNum());
         stmt.setString(3, data.getDept());
         stmt.setInt(4, data.getWtu());
         stmt.setInt(5, data.getScu());
         stmt.setInt(6, data.getNumOfSections());
         stmt.setString(7, data.getType().toString());
         stmt.setInt(8, data.getLength());
         // Serialize days
         try
         {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(baos);
            out.writeObject(data.getDays());
            out.close();
            stmt.setBytes(9, baos.toByteArray());
         }
         catch (IOException e)
         {
            e.printStackTrace();
         }
         stmt.setInt(10, data.getEnrollment());
         // Serialize Lab
         try
         {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(baos);
            out.writeObject(data.getLab());
            out.close();
            stmt.setBytes(11, baos.toByteArray());
         }
         catch (IOException e)
         {
            e.printStackTrace();
         }
         //TODO: Tyler, remove lab pad from stuff
         stmt.setInt(12, 1);
         stmt.setString(13, data.getQuarterId());
         stmt.setInt(14, scheduleID);
      }
      catch (SQLException e)
      {
         e.printStackTrace();
      }
      // Execute
      sqldb.executePrepStmt(stmt);
   }

   public void clearData()
   {
      PreparedStatement stmt = sqldb.getPrepStmt("delete from courses;");
      sqldb.executePrepStmt(stmt);
   }

   private void editData(Course data)
   {
      data.verify();
      // Create update string
      String updateString = "update courses set name = ?, catalognum = ?, "
            + "dept = ?, wtu = ?, scu = ?, numofsections = ?, "
            + "type = ?, length = ?, days = ?, enrollment = ?, "
            + "lab = ?, labpad = ?, quarterid = ?, scheduleid = ? where catalognum = ? "
            + "and dept = ? and type = ? and quarterid = ?";
      // Create prepared statement
      PreparedStatement stmt = sqldb.getPrepStmt(updateString);
      try
      {
         // Set values
         stmt.setString(1, data.getName());
         stmt.setInt(2, data.getCatalogNum());
         stmt.setString(3, data.getDept());
         stmt.setInt(4, data.getWtu());
         stmt.setInt(5, data.getScu());
         stmt.setInt(6, data.getNumOfSections());
         stmt.setString(7, data.getType().toString());
         stmt.setInt(8, data.getLength());
         // Serialize days
         try
         {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(baos);
            out.writeObject(data.getDays());
            out.close();
            stmt.setBytes(9, baos.toByteArray());
         }
         catch (IOException e)
         {
            e.printStackTrace();
         }
         stmt.setInt(10, data.getEnrollment());
         // Serialize Lab
         try
         {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(baos);
            out.writeObject(data.getLab());
            out.close();
            stmt.setBytes(11, baos.toByteArray());
         }
         catch (IOException e)
         {
            e.printStackTrace();
         }
         //TODO: Tyler, remove lab pad from stuff
         stmt.setInt(12, 1);
         stmt.setString(13, data.getQuarterId());
         stmt.setInt(14, scheduleID);

         // Where clause
         stmt.setInt(15, data.getCatalogNum());
         stmt.setString(16, data.getDept());
         stmt.setString(17, data.getType().toString());
         stmt.setString(18, data.getQuarterId());
      }
      catch (SQLException e)
      {
         e.printStackTrace();
      }
      // Execute
      sqldb.executePrepStmt(stmt);
   }

   @Override
   public void removeData(Course data)
   {
      data.verify();
      // Create delete string
      String deleteString = "delete from courses where catalognum = ? "
            + "and dept = ? and type = ? and quarterid = ?";
      // Create prepared statement
      PreparedStatement stmt = sqldb.getPrepStmt(deleteString);
      try
      {
         stmt.setInt(1, data.getCatalogNum());
         stmt.setString(2, data.getDept());
         stmt.setString(3, data.getType().toString());
         stmt.setString(4, data.getQuarterId());
      }
      catch (SQLException e)
      {
         e.printStackTrace();
      }
      // Execute
      sqldb.executePrepStmt(stmt);
   }
}
