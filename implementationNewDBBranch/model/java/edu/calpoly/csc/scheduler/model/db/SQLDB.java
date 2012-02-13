package edu.calpoly.csc.scheduler.model.db;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Set;

import edu.calpoly.csc.scheduler.model.db.sdb.ScheduleDB;
import edu.calpoly.csc.scheduler.model.db.udb.UserData;
import edu.calpoly.csc.scheduler.model.db.udb.UserDataDB;

/**
 * This class provides for direct access to a MySQL database. Though it a user
 * can connect to a database, get different tables related to the scheduler, and
 * insert SQL statements to be run in the database.
 * 
 * @author Tyler Holland, Cedric Wienold, Jan Lorenz Soliman, and Leland Garofalo
 **/

public class SQLDB {

	/** The connection */
	private Connection conn = null;

	/** The last generated key (from an auto_increment) */
	private int lastGeneratedKey;

	/** Name of the database we are using in MYSQL */
	private static final String DATABASENAME = "dev";

	/**
	 * @return the lastGeneratedKey
	 */
	public int getLastGeneratedKey() {
		return lastGeneratedKey;
	}

	/**
	 * @param lastGeneratedKey
	 *            the lastGeneratedKey to set
	 */
	public void setLastGeneratedKey(int lastGeneratedKey) {
		this.lastGeneratedKey = lastGeneratedKey;
	}

	/**
	 * This constructor will create the SQLDB object.
	 * 
	 **/
	public SQLDB() {
		lastGeneratedKey = -7;
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
	 * The open() method opens a connection to the database.
	 **/
	public void open() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			DriverManager.setLoginTimeout(5);
			conn = DriverManager.getConnection(
					"jdbc:mysql://cslvm215.csc.calpoly.edu/" + DATABASENAME, "root",
					"Abcd1234");
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
	 * Returns a ResultSet of all of the data in the given table by scheduleID
	 * 
	 * @return The result of a query for instructors in this schedule
	 */
	public ResultSet getDataByScheduleID(String tablename, int scheduleid) {
		// Where clause
		LinkedHashMap<String, Object> wheres = new LinkedHashMap<String, Object>();
		// Because Evan wanted schedules to have dbid and not scheduleid
		if (tablename.equals(ScheduleDB.TABLENAME)) {
			wheres.put(DbData.DBID, scheduleid);
		} else {
			wheres.put(DbData.SCHEDULEDBID, scheduleid);
		}

		return executeSelect(tablename, null, wheres);
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
			ResultSet rs = stmt.getGeneratedKeys();
			if (rs.next()) {
				lastGeneratedKey = rs.getInt(1);
			}
			stmt.close();
		} catch (SQLException e) {
			System.out.println("SQLException: " + e.getMessage());
			e.printStackTrace();
		}
		return result;
	}

	public HashMap<String, UserData> getSchedulePermissions(String userid) {
		// Object to eventually return
		HashMap<String, UserData> schedules = new HashMap<String, UserData>();
		// Create fields and where clause for select statement
		LinkedHashMap<String, Object> fields = new LinkedHashMap<String, Object>();
		fields.put(ScheduleDB.SCHEDULENAME, 0);
		fields.put(UserDataDB.PERMISSION, 0);
		fields.put(UserDataDB.TABLENAME + "." + DbData.DBID, 0);
		fields.put(UserDataDB.TABLENAME + "." + DbData.SCHEDULEDBID, 0);
		LinkedHashMap<String, Object> wheres = new LinkedHashMap<String, Object>();
		wheres.put(UserDataDB.USERID, userid);

		// Make table join
		String tablejoin = ScheduleDB.TABLENAME + " join "
				+ UserDataDB.TABLENAME + " on (" + ScheduleDB.TABLENAME + "."
				+ DbData.DBID + " = " + UserDataDB.TABLENAME + "."
				+ DbData.SCHEDULEDBID + ")";

		// Execute select statement
		ResultSet rs = executeSelect(tablejoin, fields, wheres);
		try {
			while (rs.next()) {
				UserData p = new UserData();
				p.setUserId(userid);
				p.setPermission(rs.getInt(UserDataDB.PERMISSION));
				p.setDbid(rs.getInt(DbData.DBID));
				p.setScheduleDBId(rs.getInt(DbData.SCHEDULEDBID));
				schedules.put(rs.getString(ScheduleDB.SCHEDULENAME), p);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return schedules;
	}

	// Helper methods for creating sql strings
	public String insertHelper(String table, Set<String> fields) {
		int counter = 0;
		String result = "insert into " + table + " (";
		// Put in fields
		for (String field : fields) {
			result = result.concat(field + ",");
			counter++;
		}
		// Remove last comma
		result = result.substring(0, result.length() - 1);
		// Move on to values
		result = result.concat(") values (");
		while (counter > 0) {
			result = result.concat("?,");
			counter--;
		}
		// Remove last comma
		result = result.substring(0, result.length() - 1);
		// Finish
		result = result.concat(")");
		return result;
	}

	public String updateHelper(String table, Set<String> fields,
			Set<String> wheres) {
		String result = "update " + table + " set ";
		// Put in sets
		for (String field : fields) {
			result = result.concat(field + " = ?,");
		}
		// Remove last comma
		result = result.substring(0, result.length() - 1);
		// Do where clause
		result = result.concat(" where ");
		for (String where : wheres) {
			result = result.concat(where + " = ? and ");
		}
		// Remove last comma and "and"
		result = result.substring(0, result.length() - 5);
		return result;
	}

	public String deleteHelper(String table, Set<String> wheres) {
		String result = "delete from " + table + " where ";
		for (String where : wheres) {
			result = result.concat(where + " = ? and ");
		}
		// Remove last comma and "and "
		result = result.substring(0, result.length() - 5);
		return result;
	}

	public String selectHelper(String table, Set<String> fields,
			Set<String> wheres) {
		String result = "select ";
		// Put in data to get back
		// If fields is null, select *
		if (fields == null || fields.size() <= 0) {
			result = result.concat("*");
		} else {
			for (String field : fields) {
				result = result.concat(field + ",");
			}
			// Remove last comma
			result = result.substring(0, result.length() - 1);
		}
		// Where stuff
		result = result.concat(" from " + table + " where ");
		for (String where : wheres) {
			result = result.concat(where + " = ? and ");
		}
		// Remove last comma and "and "
		result = result.substring(0, result.length() - 5);
		return result;
	}

	// Methods for creating and filling PreparedStatements
	public void executeInsert(String table, LinkedHashMap<String, Object> fields) {
		int counter = 1;
		Set<String> fieldkeys = fields.keySet();
		PreparedStatement stmt = getPrepStmt(insertHelper(table, fieldkeys));
		// Set ?'s
		for (String key : fieldkeys) {
			try {
				stmt.setObject(counter, fields.get(key));
				counter++;
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		executePrepStmt(stmt);
	}

	public void executeUpdate(String table,
			LinkedHashMap<String, Object> fields,
			LinkedHashMap<String, Object> wheres) {
		int counter = 1;
		Set<String> fieldkeys = fields.keySet();
		Set<String> wherekeys = wheres.keySet();
		PreparedStatement stmt = getPrepStmt(updateHelper(table, fieldkeys,
				wherekeys));
		// Set ?'s
		for (String key : fieldkeys) {
			try {
				stmt.setObject(counter, fields.get(key));
				counter++;
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		for (String key : wherekeys) {
			try {
				stmt.setObject(counter, wheres.get(key));
				counter++;
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		executePrepStmt(stmt);
	}

	public void executeDelete(String table, LinkedHashMap<String, Object> wheres) {
		int counter = 1;
		Set<String> wherekeys = wheres.keySet();
		PreparedStatement stmt = getPrepStmt(deleteHelper(table, wherekeys));
		// Set ?'s
		for (String key : wherekeys) {
			try {
				stmt.setObject(counter, wheres.get(key));
				counter++;
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		executePrepStmt(stmt);
	}

	public ResultSet executeSelect(String table,
			LinkedHashMap<String, Object> fields,
			LinkedHashMap<String, Object> wheres) {
		ResultSet rs = null;
		int counter = 1;
		Set<String> wherekeys = wheres.keySet();
		PreparedStatement stmt;
		if (fields == null) {
			stmt = getPrepStmt(selectHelper(table, null, wherekeys));
		} else {
			Set<String> fieldkeys = fields.keySet();
			stmt = getPrepStmt(selectHelper(table, fieldkeys, wherekeys));
		}
		// Set ?'s
		for (String key : wherekeys) {
			try {
				stmt.setObject(counter, wheres.get(key));
				counter++;
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		try {
			rs = stmt.executeQuery();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return rs;
	}

	public boolean doesItExist(ResultSet rs) {
		try {
			if (rs.next()) {
				return true;
			} else {
				return false;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	// Serialization methods
	public byte[] serialize(Object data) {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutput out = new ObjectOutputStream(baos);
			out.writeObject(data);
			out.close();
			return baos.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.err.println("Serialize method error");
		return null;
	}

	public Object deserialize(byte[] buffer) {
		Object result = null;
		if (buffer != null) {
			try {
				ObjectInputStream objectIn;
				objectIn = new ObjectInputStream(new ByteArrayInputStream(
						buffer));
				result = objectIn.readObject();
				objectIn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return result;
	}
}
