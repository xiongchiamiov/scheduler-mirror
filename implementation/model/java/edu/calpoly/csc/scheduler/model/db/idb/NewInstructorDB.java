package edu.calpoly.csc.scheduler.model.db.idb;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import edu.calpoly.csc.scheduler.model.db.*;
import edu.calpoly.csc.scheduler.model.db.cdb.Course;
import edu.calpoly.csc.scheduler.model.db.ldb.Location;

public class NewInstructorDB implements Database<Instructor>
{
   private ArrayList<Instructor> data;
   private SQLDB                 sqldb;

   public NewInstructorDB()
   {
      initDB();
   }

   @Override
   public ArrayList<Instructor> getData()
   {
      return data;
   }

   private void initDB()
   {
      data = new ArrayList<Instructor>();
      sqldb = new SQLDB();
      pullData();
   }

   @Override
   public void pullData()
   {
      ResultSet rs = sqldb.getSQLInstructors();
      try
      {
         while (rs.next())
         {
            // Retrieve by column name
            String fname = rs.getString("firstname");
            String lname = rs.getString("lastname");
            String userid = rs.getString("userid");
            int wtu = rs.getInt("wtu");
            String building = rs.getString("building");
            String room = rs.getString("room");
            boolean disabilities = rs.getBoolean("disabilities");
            // Put items into Instructor object and add to data
            Location office = new Location(building, room);
            Instructor toAdd = new Instructor(fname, lname, userid, wtu,
                  office, disabilities);
            data.add(toAdd);
         }
      }
      catch (SQLException e)
      {
         e.printStackTrace();
      }
   }

   @Override
   public void addData(Instructor data)
   {
      Instructor instructor = (Instructor) data;
      // Create insert strings
      String insertString = "insert into instructors ("
            + "firstname, lastname, userid, wtu, building, room, disabilities)"
            + "values (?, ?, ?, ?, ?, ?, ?)";
      // Create prepared statement
      PreparedStatement stmt = sqldb.getPrepStmt(insertString);
      // Set values
      try
      {
         stmt.setString(1, instructor.getFirstName());
         stmt.setString(2, instructor.getLastName());
         stmt.setString(3, instructor.getId());
         stmt.setInt(4, instructor.getMaxWTU());
         stmt.setString(5, instructor.getOffice().getBuilding());
         stmt.setString(6, instructor.getOffice().getRoom());
         stmt.setBoolean(7, instructor.getDisability());
      }
      catch (SQLException e)
      {
         e.printStackTrace();
      }
      // Execute
      sqldb.executePrepStmt(stmt);

   }

   @Override
   public void editData(Instructor newData)
   {
      // TODO Auto-generated method stub

   }

   @Override
   public void removeData(Instructor data)
   {
      // TODO Auto-generated method stub

   }

   /**
    * Stubbed. Returns an instructor who wants to teach this course. In
    * particular, there should be no other instructor other than the one
    * returned who wants to teach the given Course <b>more</b> than this one.<br>
    * <br>
    * Also, the returned instructor must be able to teach the course. I.e. he
    * must have enough WTU's available to take on the course.<br>
    * <br>
    * If no instructor can be found to teach the given course, null is returned.
    * 
    * @.todo Write this
    * 
    * @param c
    *           Course which the returned instructor wants to teach
    * 
    * @return an Instructor who wants to teach the given Course at least as much
    *         as every other instructor and who is able to add the course to his
    *         workload. If no instructor can be found, null is returned.
    */
   public Instructor getInstructor(Course c)
   {
      return null;
   }
}
