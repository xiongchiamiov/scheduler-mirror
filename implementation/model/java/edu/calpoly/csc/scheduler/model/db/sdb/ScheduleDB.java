package edu.calpoly.csc.scheduler.model.db.sdb;

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
import java.util.Random;

import edu.calpoly.csc.scheduler.model.db.DatabaseAPI;
import edu.calpoly.csc.scheduler.model.db.SQLDB;
import edu.calpoly.csc.scheduler.model.schedule.Schedule;

public class ScheduleDB implements DatabaseAPI<Schedule> {

	private ArrayList<Schedule> data;
	private SQLDB sqldb;
	private int scheduleID;
	private String dept;

	public ScheduleDB(SQLDB sqldb, String dept) {
		this.sqldb = sqldb;
		initDB();
	}

	public ScheduleDB(SQLDB sqldb, int scheduleID, String dept) {
		this.sqldb = sqldb;
		this.scheduleID = scheduleID;
		initDB();
	}

	private void initDB() {
		data = new ArrayList<Schedule>();
		pullData();
	}

	@Override
	public ArrayList<Schedule> getData() {
		return data;
	}

	@Override
	public void pullData() {
		System.err.println("SID: " + scheduleID);
		ResultSet rs = sqldb.getSQLSchedules(scheduleID);
		try {
			while (rs.next()) {
				Schedule toAdd = new Schedule();
				// Deserialize ALL THE SCHEDULE!
				byte[] scheduleBuf = rs.getBytes("schedule");
				if (scheduleBuf != null) {
					try {
						ObjectInputStream objectIn;
						objectIn = new ObjectInputStream(
								new ByteArrayInputStream(scheduleBuf));
						toAdd = (Schedule) objectIn.readObject();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				// Get ID since database maintains it
				toAdd.setId(rs.getInt("scheduleid"));
				data.add(toAdd);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public int createNewSchedule(String name) {
		// Create insert string
		String insertString = "insert into schedules ("
				+ "name, quarterid, schedule, dept) values (?, ?, ?, ?)";
		// Create prepared statement
		PreparedStatement stmt = sqldb.getPrepStmt(insertString);
		// Set values
		try {
			stmt.setString(1, name);
			stmt.setString(2, "blank");
			// Get Schedule through Serializable
			Schedule data = new Schedule();
			data.setDept(dept);
			data.setName(name);
			data.setQuarterId("blank");
			try {
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				ObjectOutput out = new ObjectOutputStream(baos);
				out.writeObject(data);
				out.close();
				stmt.setBytes(3, baos.toByteArray());
			} catch (IOException e) {
				e.printStackTrace();
			}
			stmt.setString(4, dept);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		// Execute
		int newid = sqldb.executePrepStmt(stmt);
		this.scheduleID = newid;
		return newid;
	}

	public void saveSchedule(Schedule data) {
		saveSchedule(data, data.getName(), data.getQuarterId());
	}

	public void saveSchedule(Schedule data, String name, String quarterID) {
		// Make sure data in schedule and given name are correct
		data.setName(name);
		data.setQuarterId(quarterID);
		// Create update string
		String updateString = "update schedules set name = ?, quarterid = ?, schedule = ?, dept = ? where scheduleid = ?";
		// Create prepared statement
		PreparedStatement stmt = sqldb.getPrepStmt(updateString);
		// Set values
		try {
			stmt.setString(1, data.getName());
			stmt.setString(2, data.getQuarterId());
			// Get Schedule through Serializable
			try {
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				ObjectOutput out = new ObjectOutputStream(baos);
				out.writeObject(data);
				out.close();
				stmt.setBytes(3, baos.toByteArray());
			} catch (IOException e) {
				e.printStackTrace();
			}
			stmt.setString(4, data.getDept());
		} catch (SQLException e) {
			e.printStackTrace();
		}
		// Execute
		sqldb.executePrepStmt(stmt);
	}

	@Override
	public void addData(Schedule data) {

		// Create insert string
		String insertString = "insert into schedules (name, quarterid, schedule, dept"
				+ "values (?, ?, ?, ?)";
		// Create prepared statement
		PreparedStatement stmt = sqldb.getPrepStmt(insertString);
		// Set values
		try {
			stmt.setString(1, data.getName());
			stmt.setString(2, data.getQuarterId());
			// Get Schedule through Serializable
			try {
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				ObjectOutput out = new ObjectOutputStream(baos);
				out.writeObject(data);
				out.close();
				stmt.setBytes(3, baos.toByteArray());
			} catch (IOException e) {
				e.printStackTrace();
			}
			stmt.setString(4, data.getDept());
		} catch (SQLException e) {
			e.printStackTrace();
		}
		// Execute
		sqldb.executePrepStmt(stmt);
	}

	@Override
	public void editData(Schedule data) {
		// Create insert string
		String updateString = "update schedules set name = ?, quarterid = ?, schedule = ?, dept = ? where scheduleid = ?";
		// Create prepared statement
		PreparedStatement stmt = sqldb.getPrepStmt(updateString);
		// Set values
		try {
			stmt.setString(1, data.getName());
			stmt.setString(2, data.getQuarterId());
			// Get Schedule through Serializable
			try {
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				ObjectOutput out = new ObjectOutputStream(baos);
				out.writeObject(data);
				out.close();
				stmt.setBytes(3, baos.toByteArray());
			} catch (IOException e) {
				e.printStackTrace();
			}
			// Where clause
			stmt.setString(4, data.getDept());
			stmt.setInt(5, data.getId());
		} catch (SQLException e) {
			e.printStackTrace();
		}
		// Execute
		sqldb.executePrepStmt(stmt);
	}

	@Override
	public void removeData(Schedule data) {
		// Create delete string
		String deleteString = "delete from schedules where scheduleid = ?";
		// Create prepared statement
		PreparedStatement stmt = sqldb.getPrepStmt(deleteString);
		try {
			stmt.setInt(1, data.getId());
		} catch (SQLException e) {
			e.printStackTrace();
		}
		// Execute
		sqldb.executePrepStmt(stmt);
	}
}
