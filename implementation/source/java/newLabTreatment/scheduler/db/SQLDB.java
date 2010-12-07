package scheduler.db;

import java.util.Collection;
import scheduler.db.instructordb.*;
import scheduler.db.coursedb.*;
import scheduler.db.locationdb.*;
import scheduler.db.preferencesdb.*;
import scheduler.generate.*;
import scheduler.Scheduler;

import java.lang.*;
import java.sql.*;
import java.util.*;

/**
 * This class provides for direct access to a MySQL database. Though it  a user
 * can connect to a database, get different tables related to the scheduler,
 * and insert SQL statements to be run in the database.
 * 
 * @author Cedric Wienold, Jan Lorenz Soliman, and Leland Garofalo
 **/

public class SQLDB  {

	/**The instructor database. */
	private InstructorDB instructorDB;

	/**The course database.  */
	private CourseDB     courseDB;

	/**The location databse. */
	private LocationDB   locationDB;

	/**The preferences database */
	private PreferencesDB preferencesDB;

	/**The connection */
	private Connection conn = null;

        /**Could the database connect? */
        private boolean connected = false;

        public boolean isConnected() {
            return connected;
        }

        public void clearCourses() {
            try {
                String sql = "DELETE FROM courses";

		Statement stmt = conn.createStatement();
                stmt.executeUpdate(sql);
                sql = "DELETE FROM courses_to_preferences";
                stmt.executeUpdate(sql);
            }
            catch(SQLException se) {
			System.out.println("Error clearing the database.");
			System.out.println(se.getMessage());
            }
        }

        public void clearInstructors() {
            try {
                String sql = "DELETE FROM instructors";

		Statement stmt = conn.createStatement();
                stmt.executeUpdate(sql);
            }
            catch(SQLException se) {
			System.out.println("Error clearing the database.");
			System.out.println(se.getMessage());
            }
        }

        public void clearLocations() {
            try {
                String sql = "DELETE FROM locations";

		Statement stmt = conn.createStatement();
                stmt.executeUpdate(sql);
            }
            catch(SQLException se) {
			System.out.println("Error clearing the database.");
			System.out.println(se.getMessage());
            }
        }

        public void clearPreferences() {
            try {
                String sql = "DELETE FROM preferences";

		Statement stmt = conn.createStatement();
                stmt.executeUpdate(sql);
                sql = "DELETE FROM preferences_courses";
                stmt.executeUpdate(sql);
                sql = "DELETE FROM preferences_days";
                stmt.executeUpdate(sql);
            }
            catch(SQLException se) {
			System.out.println("Error clearing the database.");
			System.out.println(se.getMessage());
            }
        }

	/**
	 * Returns the location database.
	 * @return the location database.
	 *
	 **/
	public LocationDB getLocationDB() {

        if (!this.connected) {
            LocationDB ldb = new LocationDB();
            ldb.setData(new ArrayList<Location>());
            ldb.setLocalData(new Vector<Location>());
            return ldb;
        }


		ArrayList<Location> myArr = new ArrayList<Location>();
		try {
			String sql = "SELECT building, room, maxoccupancy, type, smartroom, laptopconnectivity, adacompliant, overhead";
			sql += " FROM locations";

			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(sql);

			while(rs.next()){
				String bldg = rs.getString("building");
				String room = rs.getString("room");
                                int occupancy = rs.getInt("maxoccupancy");
                                String type = rs.getString("type");
                                boolean smartroom = rs.getBoolean("smartroom");
                                boolean laptopconnectivity = rs.getBoolean("laptopconnectivity");
                                boolean adacompliant = rs.getBoolean("adacompliant");
                                boolean overhead = rs.getBoolean("overhead");
				Location l = new Location(bldg,room, occupancy, type, adacompliant, overhead , 
                                        smartroom, laptopconnectivity);
				myArr.add(l);
			}

		} catch(SQLException se) {
			System.out.println("Error creating database");
			System.out.println(se.getMessage());
		} finally {
			locationDB = new LocationDB();
			if (!myArr.isEmpty()) {
				locationDB.setData(myArr);
			}
				locationDB.setData(myArr);
            locationDB.setLocalData(myArr);
		}
		return locationDB;
	}

	/**
	 * Returns the instructor database.
	 * @return the instructor database.
	 *
	 **/
	public InstructorDB getInstructorDB() {
        if (!this.connected) {
            InstructorDB idb = new InstructorDB();
            idb.setData(new Vector<Instructor>());
            idb.setLocalData(new Vector<Instructor>());
            return idb;
        }

                
		Vector<Instructor> myArr = null;
		try {
			myArr = new Vector<Instructor>();

			String sql = "SELECT `firstname`, `lastname`, `userid`, `wtu`, `building`, `room`, `disabilities`";
			sql += " FROM instructors";

			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(sql);

			while(rs.next()){
				//Retrieve by column name
				String fname = rs.getString("firstname");
				String lname = rs.getString("lastname");
				String userid = rs.getString("userid");
				int wtu = rs.getInt("wtu");
				String building = rs.getString("building");
				String room = rs.getString("room");
            boolean disabilities = rs.getBoolean("disabilities");
            //System.out.println (disabilities + "");
				Instructor i = 
            new Instructor(fname,lname,userid,wtu,new Location(building,room), disabilities);
				myArr.add(i);
			}

		} catch(SQLException se) {
			System.out.println("Error creating database");
			System.out.println(se.getMessage());
		} finally {
			instructorDB = new InstructorDB(new Vector<Instructor>());
			if (!myArr.isEmpty()) {
                            instructorDB = new InstructorDB(myArr);
				//instructorDB.setData(myArr);
			}
		}
		return instructorDB;
	}


	/**
	 * Returns the instructor database.
    * @param arrList An arraylist of courses
    * @param lab An integer representing a lab's id
	 * @return the instructor database.
	 **/
   public Course getLab(ArrayList<Course> arrList, int lab) {
      Iterator iterator =  arrList.iterator();
      while (iterator.hasNext()) {
         Course course = (Course) iterator.next();
         if (course.getId() == lab && course.getCourseType().contains("Lab")) {
            return course;
         }
      }
      return null;
   }

	/**
	 * Returns the course database.
	 * @return the course database.
	 **/
	public CourseDB getCourseDB() {
                if (!this.connected) {
                    CourseDB cdb = new CourseDB();
                    cdb.setData(new ArrayList<Course>());
                    cdb.setLocalData(new Vector<Course>());
                    return cdb;
                }

		ArrayList<Course> myArr = null;
		try {
			myArr = new ArrayList<Course>();
			String sql = "SELECT name, courseNum, wtus, scus, classType, maxEnrollment,";
			sql = sql + "labPairing, smartroom, overhead, laptop, prefix, hoursPerWeek, ctPrefix, courses_to_preferences.prefid FROM courses LEFT JOIN courses_to_preferences ON courses_to_preferences.courseid = courses.id ORDER BY classType";
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(sql);

			while(rs.next()){
				//Retrieve by column name
				String name  = rs.getString("name");
				int courseNum = rs.getInt("courseNum");
				int wtus = rs.getInt("wtus");
				int scus = rs.getInt("scus");
				String classType = rs.getString("classType");
				int maxEnrollment = rs.getInt("maxEnrollment");
				int labId = rs.getInt("labPairing");
            int hoursPerWeek = rs.getInt("hoursPerWeek");
            String ctPrefix = rs.getString("ctPrefix");
				boolean overhead = rs.getBoolean("overhead");
				boolean smartroom = rs.getBoolean("smartroom");
				boolean laptop = rs.getBoolean("laptop");
            Course lab = getLab(myArr, labId );
            String prefix = rs.getString("prefix");
            String dfcString = rs.getString("courses_to_preferences.prefid");
            DaysForClasses dfc = Scheduler.pdb.getDaysForClassesByName(dfcString);
            //System.out.println(":" + dfc);
            RequiredEquipment re = new RequiredEquipment(smartroom, overhead, laptop);
            Course c = new Course(name, courseNum, wtus, scus, classType, 
                                   maxEnrollment, 1, lab, 
                                   re, 
                                   prefix, dfc, hoursPerWeek, ctPrefix);
            myArr.add(c);

            /*if (classType.contains("Lecture")) {
               Lecture c = new  Lecture(name, courseNum, wtus, scus, classType, 
                                   maxEnrollment, 1, lab, 
                                   overhead, smartroom, laptop, 
                                   prefix, dfc, hoursPerWeek, ctPrefix);
               myArr.add(c);
            }
            else {
               Lab c = new  Lab(name, courseNum, wtus, scus,
                                   maxEnrollment, 1, 
                                   smartroom,  overhead, laptop, 
                                   prefix, dfc, hoursPerWeek, ctPrefix);
               myArr.add(c);
            }*/

			}

		} catch(SQLException se) {
			System.out.println("Error creating database");
			System.out.println(se.getMessage());
		} finally {
			courseDB = new CourseDB();
			//if (!myArr.isEmpty()) {
			courseDB.setData(myArr);
                        courseDB.setLocalData(myArr);
			//}
		}
		return courseDB;
	}

	/**
	 * Returns the preferences database.
	 * @return the preferences database.
	 *
	 * <pre>
	 * // ** Pre and Post conditions ** //
	 *
	 * <b><u>Pre:</u></b>
	 *
	 * // SQL conn must not be Null
	 * conn != nil
	 *
	 * <b><u>Post:</u></b>
	 *
	 * // PreferencesDB can not be Null
	 * pdb != nil
	 * 
	 *
	 * </pre>
	 */
	public PreferencesDB getPreferencesDBOld() {
		Vector<Preferences> myArr = null;
		try {
			myArr = new Vector<Preferences>();
			String sql = "SELECT name, data, type, importance, violatable FROM preferences";
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(sql);

			while(rs.next()){
				//Retrieve by column name
				String name = rs.getString("name");
				String data = rs.getString("data");
				int type = rs.getInt("type");
				int importance = rs.getInt("importance");
				int violatable = rs.getInt("violatable");
				Preferences p = new  Preferences(name, data, type, importance, violatable);
				myArr.add(p);
			}
		} catch(SQLException se) {
			System.out.println("Error creating database");
			System.out.println(se.getMessage());
		} finally {
			preferencesDB = new PreferencesDB();
			if (!myArr.isEmpty()) {
				preferencesDB.setData(myArr);
			}
		}
		return preferencesDB;
	}

	/**
	 * Returns the preferences database.
	 * @return the preferences database.
	 *
	 * <pre>
	 * // ** Pre and Post conditions ** //
	 *
	 * <b><u>Pre:</u></b>
	 *
	 * // SQL conn must not be Null
	 * conn != nil
	 *
	 * <b><u>Post:</u></b>
	 *
	 * // PreferencesDB can not be Null
	 * pdb != nil
	 * 
	 *
	 * </pre>
	 */
	public PreferencesDB getPreferencesDB() {
                if (!this.connected) {
                    PreferencesDB pdb = new PreferencesDB();
                    pdb.setDays(new Vector<DaysForClasses>());
                    pdb.setLocalDays(new Vector<DaysForClasses>());
                    return pdb;
                }

                System.out.println("Getting preference data.");
		Vector<Preferences> myArr = null;
                Vector<DaysForClasses> dayPrefs = null;
                Vector<NoClassOverlap> overlaps = null;
		try {
			myArr = new Vector<Preferences>();
			String sql = "SELECT name, data, type, importance, violatable FROM preferences";
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(sql);

			while(rs.next()){
				//Retrieve by column name
				String name = rs.getString("name");
				String data = rs.getString("data");
				int type = rs.getInt("type");
				int importance = rs.getInt("importance");
				int violatable = rs.getInt("violatable");
				Preferences p = new  Preferences(name, data, type, importance, violatable);
				myArr.add(p);
			}



			dayPrefs = new Vector<DaysForClasses>();
			String sqlDays = "SELECT name, weight, sun, mon, tues, wed, thur, fri, sat FROM preferences_days";
		   stmt = conn.createStatement();
		   rs = stmt.executeQuery(sqlDays);

			while(rs.next()){
				//Retrieve by column name
				String name = rs.getString("name");
				int weight = rs.getInt("weight");
            boolean days[] = new boolean[7];
            days[0] = rs.getBoolean("sun");
            days[1] = rs.getBoolean("mon");
            days[2] = rs.getBoolean("tues");
            days[3] = rs.getBoolean("wed");
            days[4] = rs.getBoolean("thur");
            days[5] = rs.getBoolean("fri");
            days[6] = rs.getBoolean("sat");
           

            DaysForClasses dfc = new DaysForClasses(name, weight, makeWeek(days) );
				dayPrefs.add(dfc);
			}
		} catch(SQLException se) {
			System.out.println("Error creating database");
			System.out.println(se.getMessage());
		} finally {
                        //System.out.println("WOOOOOOO!");
			preferencesDB = new PreferencesDB();
			if (!myArr.isEmpty()) {
				preferencesDB.setData(myArr);
			}
                        preferencesDB.setDays(dayPrefs);
                        preferencesDB.setLocalDays(dayPrefs);
                        //System.out.println("Day prefs is " + preferencesDB.getDayPreferences());
		}
		return preferencesDB;
	}


   /**
    * Creates an empty week object.
    * @param days A list of seven booleans representing days
    *             to add to the return value.
    *
    * @return A Week object with the corresponding days.
    *
    **/
   public Week makeWeek(boolean days[]) {
            ArrayList<Integer> weekModel = new ArrayList<Integer>();
            for (int i = 0; i < 7; i++) {
               if (days[i]) {
                  weekModel.add(Week.SUN + i);
               }
            }
            Week wk = new Week(weekModel);
            return wk;
   }

	/**
	 *  This constructor will create the SQLDB object.
	 *
	 **/
	public SQLDB () { 
	}

	/**
	 *  The open() method opens a connection to the database.
	 **/
	public void open() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
                        DriverManager.setLoginTimeout(5);
			conn = DriverManager.getConnection("jdbc:mysql://cslvm215.csc.calpoly.edu/scheduler", "jseall", "");
			System.out.println("Database connection established.");
                        connected = true;
		}
		catch (Exception e) {
			System.out.println("Error connecting to the database.");
			System.out.println(e.getMessage());
		}
	}
	
	/**
	 * This open() method will connect to a given database.
	 * 
	 * @param url mysql url given in the format such as "mysql://cedders.homelinux.net/jseall".
	 */
	public void open(String url) {
		try {
			Class.forName("com.mysql.jdbc.Driver");
         DriverManager.setLoginTimeout(1);
			conn = DriverManager.getConnection("jdbc:"+url, "jseall", "");
			System.out.println("Database connection to "+ url+" established.");
		}
		catch (Exception e) {
			System.out.println("Error connecting to the database.");
			System.out.println(e.getMessage());
		}
	}

	/**
	 *  The close() method closes a connection to the database.
	 **/
	public void close() {
                if (!this.connected) {
                    return;
                }
                else if (conn != null)
		{
			try {
				conn.close();
				System.out.println("Database connection terminated");
			}
			catch (Exception e) {
				System.out.print(e.getMessage());
			}
		}
	}

	/**
	 *  The insertStmt runs a SQL insert command.
	 *  
	 *  @param table A string representing the table that data will be added to.
	 *  @param val A string representing the data to add.
	 *
	 **/
	public void insertStmt(String table, String val) {
		try {
			String insertString1;
			String courseFields =""; 
			if (table.equals("courses")) {
				courseFields = CourseDB.courseFields;
			}
			insertString1 = "insert into " + table;
			insertString1 = insertString1 + courseFields;
			insertString1 = insertString1 + " values" + val;
			Statement stm = conn.createStatement();
			stm.executeUpdate(insertString1);
		}
		catch (SQLException e) {
			System.out.println("SQLException: " + e.getMessage());
		}
	}

	/**
	 *  The insertPrefStmt runs a SQL insert command.
	 *  @param table A string representing the table that data will be added to
	 *  @param val A string representing the data to add.
	 *
	 * <pre>
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
		}
		catch (SQLException e) {
			System.out.println("SQLException: " + e.getMessage());
		}
	}

	/**
	 *  The insertPrefStmt runs a SQL insert command.
	 *  @param table A string representing the table that data will be added to
	 *  @param val A string representing the data to add.
	 *
	 * <pre>
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
			insertString1 = insertString1 + " (name, data, type, violatable, importance)";
			insertString1 = insertString1 + " values" + val;
			Statement stm = conn.createStatement();
			stm.executeUpdate(insertString1);
		}
		catch (SQLException e) {
			System.out.println("SQLException: " + e.getMessage());
		}
	}

   /**
     The removePrefStmt method runs an SQL delete command on the preferences table.
     @param table A string representing the table that data will be removed
     @param whereClause A string representing the data to remove.
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
			System.out.println("SQLException: " + e.getMessage());     
		}
	}


	/**
	 *  The removeStmt method runs an SQL delete command.
	 *  @param table A string representing the table that data will be removed.
	 *  @param whereClause A string representing the data to remove.
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
			System.out.println("SQLException: " + e.getMessage());     
		}
	}

}
