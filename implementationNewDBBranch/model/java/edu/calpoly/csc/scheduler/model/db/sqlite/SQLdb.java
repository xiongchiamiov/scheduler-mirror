package edu.calpoly.csc.scheduler.model.db.sqlite;

import java.sql.*;

public class SQLdb {
	
	Connection conn = null;
	
	public static void main(String[] args) throws Exception {
		SQLdb db = new SQLdb();
		db.openConnection();
	}
	

	public void openConnection() throws SQLException, Exception
	{
		Class.forName("org.sqlite.JDBC");
		conn =
			DriverManager.getConnection("jdbc:sqlite:database.db");
		System.out.println("Connected to database");
	}
	
	public void closeConnection() throws SQLException
	{
		if(conn != null)
		{
			conn.close();
			System.out.println("Database connection closed");
		}
		else
		{
			System.out.println("Connection is null, never opened");
		}
	}
	
	public Connection getConnection()
	{
		return conn;
	}
	
}
