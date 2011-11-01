package edu.calpoly.csc.scheduler.model.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import edu.calpoly.csc.scheduler.model.db.cdb.Course;
import edu.calpoly.csc.scheduler.model.db.idb.Instructor;
import edu.calpoly.csc.scheduler.model.db.ldb.Location;
import edu.calpoly.csc.scheduler.model.schedule.Schedule;

/**
 * This class provides for direct access to a MySQL database. Though it a user
 * can connect to a database, get different tables related to the scheduler, and
 * insert SQL statements to be run in the database.
 * 
 * @author Cedric Wienold, Jan Lorenz Soliman, and Leland Garofalo
 **/

public class SQLDB {

	/** The connection */
	private Connection conn = null;

	/**
	 * This constructor will create the SQLDB object.
	 * 
	 **/
	public SQLDB() {
	}

	/**
	 * Checks if the database is connected.
	 * 
	 * @return connected Whether or not the db is connected.
	 */
	public boolean isConnected() {
		return true;
	}

	/**
	 * Clears all of the courses in the course database.
	 * 
	 **/
	public void clearCourses() {
		try {
			String sql = "DELETE FROM courses";

			Statement stmt = conn.createStatement();
			stmt.executeUpdate(sql);
			sql = "DELETE FROM courses_to_preferences";
			stmt.executeUpdate(sql);
		} catch (SQLException se) {
			System.out.println("Error clearing the database.");
			se.printStackTrace();
		}
	}

	/**
	 * Clears all of the instructors in the instructor database.
	 * 
	 **/
	public void clearInstructors() {
		try {
			String sql = "DELETE FROM instructors";

			Statement stmt = conn.createStatement();
			stmt.executeUpdate(sql);
		} catch (SQLException se) {
			System.out.println("Error clearing the database.");
			se.printStackTrace();
		}
	}

	/**
	 * Clears all of the locations in the location database.
	 * 
	 **/
	public void clearLocations() {
		try {
			String sql = "DELETE FROM locations";

			Statement stmt = conn.createStatement();
			stmt.executeUpdate(sql);
		} catch (SQLException se) {
			System.out.println("Error clearing the database.");
			se.printStackTrace();
		}
	}

	/**
	 * Clears all of the preferences in the preferences database.
	 * 
	 **/
	public void clearPreferences() {
		try {
			String sql = "DELETE FROM preferences";

			Statement stmt = conn.createStatement();
			stmt.executeUpdate(sql);
			sql = "DELETE FROM preferences_courses";
			stmt.executeUpdate(sql);
			sql = "DELETE FROM preferences_days";
			stmt.executeUpdate(sql);
		} catch (SQLException se) {
			System.out.println("Error clearing the database.");
			se.printStackTrace();
		}
	}

	/**
	 * Returns the instructor database.
	 * 
	 * @param arrList
	 *            An arraylist of courses
	 * @param lab
	 *            An integer representing a lab's id
	 * @return the instructor database.
	 **/
	public Course getLab(ArrayList<Course> arrList, int lab) {
		Iterator iterator = arrList.iterator();
		while (iterator.hasNext()) {
			Course course = (Course) iterator.next();
			if (course.getCatalogNum() == lab
					&& course.getType() == Course.CourseType.LAB) {
				return course;
			}
		}
		return null;
	}

	/**
	 * The open() method opens a connection to the database.
	 **/
	public void open() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			DriverManager.setLoginTimeout(5);
			conn = DriverManager.getConnection(
					"jdbc:mysql://cslvm215.csc.calpoly.edu/newscheduler",
					"root", "Abcd1234");
			System.out.println("Database connection established.");
		} catch (Exception e) {
			System.out.println("Error connecting to the database.");
			e.printStackTrace();
		}

		assert (conn != null);
	}

	/**
	 * This open() method will connect to a given database.
	 * 
	 * @param url
	 *            mysql url given in the format such as
	 *            "mysql://cedders.homelinux.net/jseall".
	 */
	public void open(String url) {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			DriverManager.setLoginTimeout(1);
			conn = DriverManager.getConnection("jdbc:" + url, "jseall", "");
			System.out.println("Database connection to " + url
					+ " established.");
		} catch (Exception e) {
			System.out.println("Error connecting to the database.");
			e.printStackTrace();
		}
	}

	/**
	 * The close() method closes a connection to the database.
	 **/
	public void close() {
		if (isConnected()) {
			return;
		} else if (conn != null) {
			try {
				conn.close();
				System.out.println("Database connection terminated");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * The insertStmt runs a SQL insert command.
	 * 
	 * @param table
	 *            A string representing the table that data will be added to.
	 * @param val
	 *            A string representing the data to add.
	 * 
	 **/
	public void insertStmt(String table, String val) {
		try {
			String insertString1;
			String courseFields = "";
			// TODO: find out why this exists
			// if (table.equals("courses"))
			// {
			// courseFields = CourseDB.courseFields;
			// }
			insertString1 = "insert into " + table;
			insertString1 = insertString1 + courseFields;
			insertString1 = insertString1 + " values" + val;
			Statement stm = conn.createStatement();
			stm.executeUpdate(insertString1);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * The insertPrefStmt runs a SQL insert command.
	 * 
	 * @param table
	 *            A string representing the table that data will be added to
	 * @param val
	 *            A string representing the data to add.
	 * 
	 *            <pre>
	 * // ** Pre and Post conditions ** //
	 * 
	 * <b><u>Pre:</u></b>
	 * 
	 * // table can not be Null
	 * table != nil
	 * 
	 * &&
	 * 
	 * // val can not be Null
	 * val != nil
	 * 
	 * <b><u>Post:</u></b>
	 * 
	 * // Preference is added to SQLDB
	 * 
	 * table.contains(val)
	 * 
	 * 
	 * </pre>
	 */
	public void insertPrefStmt(String table, String val, String fields) {
		try {
			String insertString1;
			insertString1 = "insert into " + table;
			insertString1 = insertString1 + " " + fields;
			insertString1 = insertString1 + " values" + val;
			Statement stm = conn.createStatement();
			stm.executeUpdate(insertString1);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * The insertPrefStmt runs a SQL insert command.
	 * 
	 * @param table
	 *            A string representing the table that data will be added to
	 * @param val
	 *            A string representing the data to add.
	 * 
	 *            <pre>
	 * // ** Pre and Post conditions ** //
	 * 
	 * <b><u>Pre:</u></b>
	 * 
	 * // table can not be Null
	 * table != nil
	 * 
	 * &&
	 * 
	 * // val can not be Null
	 * val != nil
	 * 
	 * <b><u>Post:</u></b>
	 * 
	 * // Preference is added to SQLDB
	 * 
	 * table.contains(val)
	 * 
	 * 
	 * </pre>
	 */
	public void insertPrefStmt(String table, String val) {
		try {
			String insertString1;
			insertString1 = "insert into " + table;
			insertString1 = insertString1
					+ " (name, data, type, violatable, importance)";
			insertString1 = insertString1 + " values" + val;
			Statement stm = conn.createStatement();
			stm.executeUpdate(insertString1);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * The removePrefStmt method runs an SQL delete command on the preferences
	 * table.
	 * 
	 * @param table
	 *            A string representing the table that data will be removed
	 * @param whereClause
	 *            A string representing the data to remove.
	 */
	public void removePrefStmt(String table, String whereClause) {
		try {
			String insertString1;
			insertString1 = "delete from " + table;
			insertString1 = insertString1 + " where " + whereClause;
			Statement stm = conn.createStatement();
			stm.executeUpdate(insertString1);

		}

		catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * The removeStmt method runs an SQL delete command.
	 * 
	 * @param table
	 *            A string representing the table that data will be removed.
	 * @param whereClause
	 *            A string representing the data to remove.
	 **/
	public void removeStmt(String table, String whereClause) {
		try {
			String insertString1;
			insertString1 = "delete from " + table;
			insertString1 = insertString1 + " where " + whereClause;
			Statement stm = conn.createStatement();
			stm.executeUpdate(insertString1);

		}

		catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/*
	 * Methods for retrieving the various database entries from the central
	 * database server.
	 * 
	 * @author Michael McMahon
	 */

	/*
	 * Retrieves all entries from the instructors database table
	 * 
	 * @return The result of a query for all instructors
	 */
	@Deprecated
	public ResultSet getSQLInstructors() {

		String queryForInstructors = "SELECT * FROM instructors";
		Statement stmt;
		ResultSet instructorsResult = null;

		try {
			stmt = conn.createStatement();
			instructorsResult = stmt.executeQuery(queryForInstructors);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return instructorsResult;
	}

	/*
	 * Retrieves all entries from the courses database table

	 * 
	 * @return The result of a query for all courses
	 */
	@Deprecated
	public ResultSet getSQLCourses() {


		String queryForCourses = "SELECT * FROM courses";
		Statement stmt;
		ResultSet coursesResult = null;

		try {
			stmt = conn.createStatement();
			coursesResult = stmt.executeQuery(queryForCourses);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return coursesResult;
	}


	/*
	 * Retrieves all entries from the locations database table
	 * 
	 * @return The result of a query for all locations
	 */
	@Deprecated
	public ResultSet getSQLLocations() {


		String queryForLocations = "SELECT * FROM locations";
		Statement stmt;
		ResultSet locationsResult = null;

		try {
			stmt = conn.createStatement();
			locationsResult = stmt.executeQuery(queryForLocations);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return locationsResult;
	}

	/*
	 * Retrieves all entries from the schedule database table
	 * 
	 * @return The result of a query for all schedules
	 */
	public ResultSet getSQLSchedules() {

		String queryForSchedules = "SELECT * FROM schedules";
		Statement stmt;
		ResultSet schedulesResult = null;

		try {
			stmt = conn.createStatement();
			schedulesResult = stmt.executeQuery(queryForSchedules);
		} catch (SQLException e) {
			e.printStackTrace();
		} 
		return schedulesResult;
	}
	
	/*
	 * Retrieves entries from the instructors database table for 
	 * given schedule id
	 * 
	 * @return The result of a query for instructors in this schedule
	 */
	public ResultSet getSQLInstructors(int scheduleid) {
	   
	   String queryForInstructors = "SELECT * FROM instructors where scheduleid = ?";
	   PreparedStatement stmt;
	   ResultSet instructorsResult = null;
	   
	   try {
	      stmt = conn.prepareStatement(queryForInstructors);
	      stmt.setInt(1, scheduleid);
	      instructorsResult = stmt.executeQuery();
	   } catch (SQLException e) {
	      e.printStackTrace();
	   } 
	   return instructorsResult;
	}
	
   /*
    * Retrieves entries from the courses database table for 
    * given schedule id
    * 
    * @return The result of a query for courses in this schedule
    */
	public ResultSet getSQLCourses(int scheduleid) {
	   
	   String queryForCourses = "SELECT * FROM courses where scheduleid = ?";
	   PreparedStatement stmt;
	   ResultSet coursesResult = null;
	   
	   try {
	      stmt = conn.prepareStatement(queryForCourses);
         stmt.setInt(1, scheduleid);
	      coursesResult = stmt.executeQuery();
	   } catch (SQLException e) {
	      e.printStackTrace();
	   }
	   
	   return coursesResult;
	}
	
	
   /*
    * Retrieves entries from the locations database table for 
    * given schedule id
    * 
    * @return The result of a query for locations in this schedule
    */
	public ResultSet getSQLLocations(int scheduleid) {
	   
	   String queryForLocations = "SELECT * FROM locations where scheduleid = ?";
	   PreparedStatement stmt;
	   ResultSet locationsResult = null;
	   
	   try {
	      stmt = conn.prepareStatement(queryForLocations);
         stmt.setInt(1, scheduleid);
	      locationsResult = stmt.executeQuery();
	   } catch (SQLException e) {
	      e.printStackTrace();
	   }
	   return locationsResult;
	}
	
   /*
    * Retrieves entries from the schedules database table for 
    * given schedule id
    * 
    * @return The result of a query for schedules in this schedule
    */
	public ResultSet getSQLSchedules(int scheduleid) {
	   
	   String queryForSchedules = "SELECT * FROM schedules where scheduleid = ?";
	   PreparedStatement stmt;
	   ResultSet schedulesResult = null;
	   
	   try {
	      stmt = conn.prepareStatement(queryForSchedules);
         stmt.setInt(1, scheduleid);
	      schedulesResult = stmt.executeQuery();
	   } catch (SQLException e) {
	      e.printStackTrace();
	   } 
	   return schedulesResult;
	}
	
	

	/**
	 * Retrieves the course with matching ID from the database
	 * 
	 * @return The result of a query for courses with given id
	 */
	public ResultSet getSQLCourseByID(int id) {

	   
	   String selectString = "select * from courses where id = ?";
	   PreparedStatement stmt = getPrepStmt(selectString);
	   ResultSet rs = null;
	   try {
	      stmt.setInt(1, id);
	      rs = stmt.executeQuery();
	   } catch (SQLException e) {
	      e.printStackTrace();
	   } 
	   return rs;
	}
	
	/**
	 * Retrieves the course with matching department and catalog number from the database
	 * 
	 * @return The result of a query for courses with given dept and catalogNum
	 */
	@Deprecated
	public ResultSet getSQLCourse(String dept, int catalogNum) {
	   
	   String selectString = "select * from courses where dept = ? and catalognum = ?";
	   PreparedStatement stmt = getPrepStmt(selectString);
	   ResultSet rs = null;
	   try {
	      stmt.setString(1, dept);
	      stmt.setInt(2, catalogNum);
	      rs = stmt.executeQuery();
	   } catch (SQLException e) {
	      e.printStackTrace();
	   }
	   return rs;
	}
	
	/**
	 * Retrieves the course with matching department and catalog number from the database
	 * 
	 * @return The result of a query for courses with given dept and catalogNum
	 */
	public ResultSet getSQLCourse(String dept, int catalogNum,int scheduleid) {
	   
	   String selectString = "select * from courses where dept = ? and catalognum = ? and scheduleid = ?";
	   PreparedStatement stmt = getPrepStmt(selectString);
	   ResultSet rs = null;
	   try {
	      stmt.setString(1, dept);
	      stmt.setInt(2, catalogNum);
	      stmt.setInt(3, scheduleid);
	      rs = stmt.executeQuery();
	   } catch (SQLException e) {
	      e.printStackTrace();
	   }
	   return rs;
	}
	
	public PreparedStatement getPrepStmt(String sql) {

		try {
			return conn.prepareStatement(sql);
		} catch (SQLException e) {
			System.out.println("SQLException: " + e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

	public int executePrepStmt(PreparedStatement stmt) {
		int result = -1;
		try {
			result = stmt.executeUpdate();
			stmt.close();
		} catch (SQLException e) {
			System.out.println("SQLException: " + e.getMessage());
			e.printStackTrace();
		}
		return result;
	}
	
	public String getDeptByUserID(String userid)
	{
	   String selectString = "select dept from users where userid = ?";
      PreparedStatement stmt = getPrepStmt(selectString);
      ResultSet rs = null;
      String dept = "";
      try {
         stmt.setString(1, userid);
         rs = stmt.executeQuery();
         System.out.println("about to call rs.next");
         if (rs.next()) {
        	 System.out.println("called rs.next");
        	 dept = rs.getString("dept");
         }
         else {
        	 System.out.println("rs empty");
        	 assert(false);
         }
      } catch (SQLException e) {
         e.printStackTrace();
      }
      return dept;
	}
	
	public ResultSet getSchedulesByDept(String dept)
	{
	   String selectString = "select name, scheduleid from schedules where dept = ?";
      PreparedStatement stmt = getPrepStmt(selectString);
      ResultSet rs = null;
      try {
         stmt.setString(1, dept);
         rs = stmt.executeQuery();
      } catch (SQLException e) {
         e.printStackTrace();
      }
      return rs;
	}
	
	@Deprecated
	public boolean doesScheduleIDExist(int scheduleid)
	{
		System.out.println("Does " + scheduleid + " exist?");
		String selectString = "select scheduleid from schedules where scheduleid = ?";
	      PreparedStatement stmt = getPrepStmt(selectString);
	      ResultSet rs;
	      try {
	         stmt.setInt(1, scheduleid);
	         rs = stmt.executeQuery();
	         if(rs.next())
	         {
	        	 if(rs.getInt("scheduleid") == scheduleid)
	        	 {
	        		 System.out.println("yes");
	        		 return true;
	        	 }
	        	 else
	        	 {
	        		 System.out.println("no");
	        		 return false;
	        	 }
	         }
	         else
	         {
	        	 System.out.println("No, nothing in resultset");
	        	 return false;
	         }
	      } catch (SQLException e) {
	    	  System.out.println("no, sqlexception");
	         return false;
	      }
	}
	
	//Does ___ exist methods
	public boolean doesCourseExist(Course data)
	{
	   //Check if dept, catalognum, and type already exist
	   String query = "select dept, catalognum, type from schedules where dept = ? and catalognum = ? and type = ?";
	   PreparedStatement stmt = getPrepStmt(query);
	   try
      {
         stmt.setString(1, data.getDept());
         stmt.setInt(2, data.getCatalogNum());
         stmt.setString(3, data.getType().toString());
      }
      catch (SQLException e)
      {
         e.printStackTrace();
      }
	   return doesItExist(stmt);
	}
	
	public boolean doesInstructorExist(Instructor data)
	{
	   //Check if userid already exists
	   String query = "select userid from instructors where userid = ?";
	   PreparedStatement stmt = getPrepStmt(query);
	   try
	   {
	      stmt.setString(1, data.getUserID());
	   }
	   catch (SQLException e)
	   {
	      e.printStackTrace();
	   }
	   return doesItExist(stmt);
	}
	
	public boolean doesLocationExist(Location data)
	{
	   //Check if building and room already exist
	   String query = "select building, room from instructors where building = ? and room = ?";
	   PreparedStatement stmt = getPrepStmt(query);
	   try
	   {
	      stmt.setString(1, data.getBuilding());
	      stmt.setString(1, data.getRoom());
	   }
	   catch (SQLException e)
	   {
	      e.printStackTrace();
	   }
	   return doesItExist(stmt);
	}
	
	public boolean doesScheduleExist(Schedule data)
	{
	   //Check if scheduleid and name already exist
	   String query = "select scheduleid, name from schedules where scheduleid = ? and name = ?";
	   PreparedStatement stmt = getPrepStmt(query);
	   try
	   {
	      stmt.setInt(1, data.getId());
	      stmt.setString(1, data.getName());
	   }
	   catch (SQLException e)
	   {
	      e.printStackTrace();
	   }
	   return doesItExist(stmt);
	}
	
	private boolean doesItExist(PreparedStatement stmt)
	{
	   ResultSet rs;
      try {
         rs = stmt.executeQuery();
         if(rs.next())
         {
            return true;
         }
         else
         {
            return false;
         }
      } catch (SQLException e) {
         e.printStackTrace();
         return false;
      }
	}
}
