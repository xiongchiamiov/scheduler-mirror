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

import edu.calpoly.csc.scheduler.model.db.DatabaseAPI;
import edu.calpoly.csc.scheduler.model.db.SQLDB;
import edu.calpoly.csc.scheduler.model.schedule.NewSchedule;
import edu.calpoly.csc.scheduler.model.schedule.Schedule;

public class ScheduleDB implements DatabaseAPI<NewSchedule> {
	private ArrayList<NewSchedule> data;
	private SQLDB sqldb;

	public ScheduleDB(SQLDB sqldb) {
		this.sqldb = sqldb;
		initDB();
	}

	private void initDB() {
		data = new ArrayList<NewSchedule>();
		pullData();
	}

	@Override
	public ArrayList<NewSchedule> getData() {
		return data;
	}

	@Override
	public void pullData() {
		ResultSet rs = sqldb.getSQLSchedules();
		try {
			while (rs.next()) {
			   NewSchedule toAdd = new NewSchedule();
				// Deserialize ALL THE SCHEDULE!
				byte[] scheduleBuf = rs.getBytes("schedule");
				if (scheduleBuf != null) {
					try {
						ObjectInputStream objectIn;
						objectIn = new ObjectInputStream(
								new ByteArrayInputStream(scheduleBuf));
						toAdd = (NewSchedule) objectIn.readObject();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				// Get ID since database maintains it
				toAdd.setId(rs.getInt("id"));
				data.add(toAdd);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void saveSchedule(NewSchedule data, String name, String quarterID) {
		// Make sure data in schedule and given name are correct
		data.setName(name);
		data.setQuarterId(quarterID);
		// Create insert string
		String insertString = "insert into schedules ("
				+ "name, quarterid, schedule" + "values (?, ?, ?)";
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
		} catch (SQLException e) {
			e.printStackTrace();
		}
		// Execute
		sqldb.executePrepStmt(stmt);
	}
	
	

	@Override
	public void addData(NewSchedule data) {
		// Create insert string
		String insertString = "insert into schedules (name, quarterid, schedule"
				+ "values (?, ?, ?)";
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
		} catch (SQLException e) {
			e.printStackTrace();
		}
		// Execute
		sqldb.executePrepStmt(stmt);
	}

	@Override
	public void editData(NewSchedule data) {
		// Create insert string
		String updateString = "update schedules set name = ?, quarterid = ?, schedule = ? where scheduleid = ?";
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
			//Where clause
			stmt.setInt(4, data.getId());
		} catch (SQLException e) {
			e.printStackTrace();
		}
		// Execute
		sqldb.executePrepStmt(stmt);
	}

	@Override
	public void removeData(NewSchedule data) {
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
