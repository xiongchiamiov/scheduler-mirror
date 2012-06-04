package scheduler.model.db.sqlite;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Constructor;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import scheduler.model.Day;
import scheduler.model.db.DatabaseException;
import scheduler.model.db.IDBCourse;
import scheduler.model.db.IDBCourseAssociation;
import scheduler.model.db.IDBCoursePreference;
import scheduler.model.db.IDBDayPattern;
import scheduler.model.db.IDBDocument;
import scheduler.model.db.IDBEquipmentType;
import scheduler.model.db.IDBInstructor;
import scheduler.model.db.IDBLocation;
import scheduler.model.db.IDBObject;
import scheduler.model.db.IDBOfferedDayPattern;
import scheduler.model.db.IDBProvidedEquipment;
import scheduler.model.db.IDBScheduleItem;
import scheduler.model.db.IDBTime;
import scheduler.model.db.IDBTimePreference;
import scheduler.model.db.IDBUsedEquipment;
import scheduler.model.db.IDBUser;
import scheduler.model.db.IDatabase;
import scheduler.model.db.IDatabase.NotFoundException;
import scheduler.model.db.simple.DBCourse;
import scheduler.model.db.simple.DBDocument;
import scheduler.model.db.simple.DBUsedEquipment;
// TODO: Auto-generated Javadoc

/**
 * The Class SQLdb implements all methods of the IDatabase class
 * to provide an API for connection to a SQLlite database. This class
 * can be used to insert and retrieve all necessary information pertaining
 * to the scheduler, and allows for storage of all data in a sqlite
 * database.
 *
 * @author Jonathan
 */
public class SQLdb implements IDatabase {
	
	/** The connection to the database. */
	static Connection conn = null;
	
	/** The document table, which is a mirror of the table as it exists in the db. */
	Table<SQLDocument> documentTable = new Table<SQLDocument>(SQLDocument.class, "document",
			new Table.Column[] {
					new Table.Column("id", Integer.class),
					new Table.Column("name", String.class),
					new Table.Column("isTrash", Boolean.class),
					new Table.Column("startHalfHour", Integer.class),
					new Table.Column("endHalfHour", Integer.class)
	});
	
	/** The workingcopy table, which is a mirror of the table as it exists in the db. */
	Table<SQLWorkingCopy> workingcopyTable = new Table<SQLWorkingCopy>(SQLWorkingCopy.class, "workingcopy",
			new Table.Column[] {
					new Table.Column("id", Integer.class),
					new Table.Column("originalDocID", Integer.class)
	});
	
	/** The instructor table, which is a mirror of the table as it exists in the db. */
	Table<SQLInstructor> instructorTable = new Table<SQLInstructor>(SQLInstructor.class, "instructor",
			new Table.Column[] {
					new Table.Column("id", Integer.class),
					new Table.Column("docID", Integer.class),
					new Table.Column("firstName", String.class),
					new Table.Column("lastName", String.class),
					new Table.Column("username", String.class),
					new Table.Column("maxWTU", String.class),
					new Table.Column("schedulable", Boolean.class)
	});
	
	/** The location table, which is a mirror of the table as it exists in the db. */
	Table<SQLLocation> locationTable = new Table<SQLLocation>(SQLLocation.class, "location",
			new Table.Column[] {
					new Table.Column("id", Integer.class),
					new Table.Column("docID", Integer.class),
					new Table.Column("maxOccupancy", String.class),
					new Table.Column("type", String.class),
					new Table.Column("room", String.class),
					new Table.Column("schedulable", Boolean.class)	
	});
	
	/** The locationequipment table, which is a mirror of the table as it exists in the db. */
	Table<SQLLocationEquipment> locationequipmentTable = new Table<SQLLocationEquipment>(SQLLocationEquipment.class, "locationequipment",
			new Table.Column[] {
					new Table.Column("id", Integer.class),
					new Table.Column("locID", Integer.class),
					new Table.Column("equipID", Integer.class)
	});
	
	/** The course table, which is a mirror of the table as it exists in the db. */
	Table<SQLCourse> courseTable = new Table<SQLCourse>(SQLCourse.class, "course",
			new Table.Column[] {
					new Table.Column("id", Integer.class),
					new Table.Column("docID", Integer.class),
					new Table.Column("enrollment", String.class),
					new Table.Column("wtu", String.class),
					new Table.Column("scu", String.class),
					new Table.Column("type", String.class),
					new Table.Column("numSections", String.class),
					new Table.Column("dept", String.class),
					new Table.Column("catalogNum", String.class),
					new Table.Column("name", String.class),
					new Table.Column("schedulable", Boolean.class),
					new Table.Column("numHalfHours", String.class)
	});
	
	/** The courseequipment table, which is a mirror of the table as it exists in the db. */
	Table<SQLUsedEquipment> courseequipmentTable = new Table<SQLUsedEquipment>(SQLUsedEquipment.class, "courseequipment",
			new Table.Column[] {
					new Table.Column("id", Integer.class),
					new Table.Column("courseID", Integer.class),
					new Table.Column("equipID", Integer.class)
	});
	
	/** The coursepatterns table, which is a mirror of the table as it exists in the db. */
	Table<SQLOfferedDayPattern> coursepatternsTable = new Table<SQLOfferedDayPattern>(SQLOfferedDayPattern.class, "coursepatterns",
			new Table.Column[] {
					new Table.Column("id", Integer.class),
					new Table.Column("courseID", Integer.class),
					new Table.Column("patternID", Integer.class)
	});
	
	/** The schedule item table, which is a mirror of the table as it exists in the db. */
	Table<SQLScheduleItem> scheduleItemTable = new Table<SQLScheduleItem>(SQLScheduleItem.class, "scheduleitem",
			new Table.Column[] {
					new Table.Column("id", Integer.class),
					new Table.Column("docID", Integer.class),
					new Table.Column("instID", Integer.class),
					new Table.Column("locID", Integer.class),
					new Table.Column("courseID", Integer.class),
					new Table.Column("startTime", Integer.class),
					new Table.Column("endTime", Integer.class),
					new Table.Column("dayPattern", String.class),
					new Table.Column("sectionNum", String.class),
					new Table.Column("isPlaced", Boolean.class),
					new Table.Column("isConflicted", Boolean.class)
	});
	
	/** The labassociations table, which is a mirror of the table as it exists in the db. */
	Table<SQLCourseAssociation> labassociationsTable = new Table<SQLCourseAssociation>(SQLCourseAssociation.class, "labassociations",
			new Table.Column[] {
					new Table.Column("id", Integer.class),
					new Table.Column("lecID", Integer.class), 
					new Table.Column("isTethered", Boolean.class)
	});
	
	/** The timeslotpref table, which is a mirror of the table as it exists in the db. */
	Table<SQLTimePreference> timeslotprefTable = new Table<SQLTimePreference>(SQLTimePreference.class, "timeslotpref",
			new Table.Column[] {
					new Table.Column("id", Integer.class),
					new Table.Column("timeID", Integer.class),
					new Table.Column("instID", Integer.class),
					new Table.Column("prefLevel", Integer.class)
	});
	
	/** The coursepref table, which is a mirror of the table as it exists in the db. */
	Table<SQLCoursePreference> courseprefTable = new Table<SQLCoursePreference>(SQLCoursePreference.class, "coursepref",
			new Table.Column[] {
					new Table.Column("id", Integer.class),
					new Table.Column("instID", Integer.class),
					new Table.Column("courseID", Integer.class),
					new Table.Column("prefLevel", Integer.class)
	});
	
	/** The pattern table, which is a mirror of the table as it exists in the db. */
	Table<SQLDayPattern> patternTable = new Table<SQLDayPattern>(SQLDayPattern.class, "pattern",
			new Table.Column[] {
					new Table.Column("id", Integer.class),
					new Table.Column("days", String.class)
	});
	
	/** The equipment table, which is a mirror of the table as it exists in the db. */
	Table<SQLEquipmentType> equipmentTable = new Table<SQLEquipmentType>(SQLEquipmentType.class, "equipment",
			new Table.Column[] {
					new Table.Column("id", Integer.class),  /*it's unique id generated*/
					new Table.Column("desc", String.class)
	});
	
	/** The userdata table, which is a mirror of the table as it exists in the db. */
	Table<SQLUser> userdataTable = new Table<SQLUser>(SQLUser.class, "userdata",
			new Table.Column[] {
					new Table.Column("id", Integer.class),
					new Table.Column("username", String.class),
					new Table.Column("isAdmin", Boolean.class)
	});

	/**
	 * Instantiates a new SQLdb and opens the connection to the DB.
	 */
	public SQLdb() {
		try {
			openConnection();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Open connection to sqlite database.
	 *
	 * @throws SQLException SQL exception if SQLite fails
	 * @throws Exception any other exception
	 */
	public void openConnection() throws SQLException, Exception
	{
		Class.forName("org.sqlite.JDBC");
		conn =
			DriverManager.getConnection("jdbc:sqlite:database.db");
//		System.out.println("Connected to database");
	}
	
	/**
	 * Close connection to the sqlite database.
	 *
	 * @throws SQLException the sQL exception
	 */
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

	/**
	 * The Class Table mimics a table in the database. It provides
	 * select, delete, update, and insert methods to do the actual work
	 * of connecting to the database. This abstraction allows for Table 
	 * classes to be used instead of directly connecting to sqlite
	 * in each method of SQLdb.java.
	 *
	 * @param <T> the generic type which is a type inserted in the database
	 */
	static class Table<T extends IDBObject> {
		
		/**
		 * The Enum ColumnType can be one of four types.
		 */
		public enum ColumnType {
			
			/** The VARCHAR. */
			VARCHAR,
			
			/** The INTEGER. */
			INTEGER,
			
			/** The TEXT. */
			TEXT,
			
			/** The BOOLEAN. */
			BOOLEAN;
		}
		
		/**
		 * The Class Column. Each Column has a name and the
		 * class type.
		 */
		static class Column {
			
			/** The name. */
			String name;
 			
			 /** The classs. */
			 Class classs;
 			
			 /**
			  * Instantiates a new column.
			  *
			  * @param name the name
			  * @param classs the classs
			  */
			 Column(String name, Class classs) {
 				this.name = name;
 				this.classs = classs;
 			}
		}
		
		/** The name. */
		String name;
		
		/** The columns. */
		Column[] columns;
		
		/** The classs. */
		Class classs;
		
		/**
		 * Instantiates a new table.
		 *
		 * @param classs the classs of the objects in the table
		 * @param name the name of the table in the database
		 * @param columns the columns of the table in the database
		 */
		public Table(Class classs, String name, Column[] columns) {
			  this.classs = classs;
			  this.name = name;
			  this.columns = columns;
		}
		
		/**
		 * Deletes a row from the database with the given id.
		 *
		 * @param id the sqlite database row id
		 * @throws DatabaseException the database exception
		 */
		public void delete(Integer id) throws DatabaseException {
			assert(id != null) : "trying to delete null ID";
			PreparedStatement stmnt = null;
			String queryString = "DELETE FROM " + name + " WHERE ID = ?";
			
			try {
				stmnt = conn.prepareStatement(queryString);
				stmnt.setInt(1, id);
				stmnt.executeUpdate();
			} catch (SQLException e) {
				throw new DatabaseException(e);
			}
		}
		
		/**
		 * Updates a row in the database with the given id and values.
		 *
		 * @param values the new values for the row
		 * @param id the sqlite database row id
		 * @throws DatabaseException the database exception
		 */
		public void update(Object[] values, Integer id) throws DatabaseException {
			assert(values != null);
			assert(values.length == columns.length - 1);
			PreparedStatement stmnt = null;
			String queryString = "UPDATE " + name + " SET ";
			
			for (Column column : columns)
				if (!column.name.equals("id"))
					queryString += column.name + " = ?,";
			queryString = queryString.substring(0, queryString.length() - 1);
			queryString += " WHERE ID = ?";
			
			try {
				stmnt = conn.prepareStatement(queryString);
				
				int count = 0;
				for (Object val : values) {
					setStatement(stmnt, val.getClass(), count+1, val);
					count++;
					//System.out.print(count + " " + val + " ");
				}
				
				stmnt.setInt(count, id);
				//System.out.println();
				stmnt.executeUpdate();
			} catch (SQLException e) {
				throw new DatabaseException(e);
			}
			
		}
		
		/**
		 * Selects all rows in the database table.
		 *
		 * @return the list of all rows in the database
		 * @throws DatabaseException the database exception
		 */
		public List<T> selectAll() throws DatabaseException {
			PreparedStatement stmnt = null;
			String queryString = "SELECT * FROM " + name;
			
			ResultSet rs = null;
			
			try {
				stmnt = conn.prepareStatement(queryString);
				rs = stmnt.executeQuery();
			} catch (SQLException e) {
				throw new DatabaseException(e);
			}
			
			return parseResultSet(rs);
  		}
		
		/**
		 * Selects all rows in the database for a set of given
		 * wheres.
		 * 
		 * @param wheres the wheres to be selected, which is a map of Strings to Objects
		 * @return the list of all database rows matching the given wheres
		 * @throws DatabaseException the database exception
		 */
		public List<T> select(Map<String, Object> wheres) throws DatabaseException {
			PreparedStatement stmnt = null;  
			String queryString = "SELECT * FROM " + name + " WHERE "; 
			
			for (String col : wheres.keySet())
				queryString += col + " = ?,";
			
			queryString = queryString.substring(0, queryString.length() - 1);
			ResultSet rs = null;
			
			try {
				stmnt = conn.prepareStatement(queryString);
				
				int count = 0;
				for (String col : wheres.keySet()) {
					Object val = wheres.get(col);
					setStatement(stmnt, val.getClass(), count+1, val);
					count++;
				}
				
				rs = stmnt.executeQuery();
			} catch (SQLException e) {
				throw new DatabaseException(e);
			}
			
			return parseResultSet(rs);
		}
		
		/**
		 * Inserts an object with the given values into the database
		 * and assigns it a unique id based on rows in the database.
		 *
		 * @param values the values for the object to be inserted
		 * @return the unique id of the object in the database
		 * @throws DatabaseException the database exception
		 */
		public Integer insert(Object[] values) throws DatabaseException {
			//assert(values != null);
			assert(values.length == columns.length-1);
			PreparedStatement stmnt = null;
  
			String queryString = "INSERT INTO " + name + " (";
  
			for (Column column : columns)
				if (!column.name.equals("id")) {
					queryString += column.name + ",";
					//System.out.println("column name: " + column.name);
				}
  
			queryString = queryString.substring(0, queryString.length() - 1);
			queryString += ") VALUES (";
  
			for (int columnI = 0; columnI < columns.length; columnI++) {
				if (!columns[columnI].name.equals("id"))
					queryString += "?,";
			}
			
			queryString = queryString.substring(0, queryString.length() - 1);
			queryString += ")";

			try {
				stmnt = conn.prepareStatement(queryString);
				
				for (int columnI = 1; columnI < columns.length; columnI++) {
					setStatement(stmnt, columns[columnI].classs, columnI, values[columnI-1]);
				}
				stmnt.executeUpdate();
				
				stmnt = conn.prepareStatement("select last_insert_rowid()");
				ResultSet id = stmnt.executeQuery();
				return id.getInt("last_insert_rowid()");
			}
			catch (SQLException e) {
				throw new DatabaseException(e);
			}
		}
		
		/**
		 * Inserts an object with the given values into the database with 
		 * the specified id.
		 *
		 * @param values the values for the object to be inserted
		 * @return the id of the last item inserted into the database
		 * @throws DatabaseException the database exception
		 */
		public Integer insertWithID(Object[] values) throws DatabaseException {
			assert(values.length == columns.length);
			PreparedStatement stmnt = null;
  
			String queryString = "INSERT INTO " + name + " (";
  
			for (Column column : columns)
				queryString += column.name + ",";
  
			queryString = queryString.substring(0, queryString.length() - 1);
			queryString += ") VALUES (";
  
			for (int columnI = 0; columnI < columns.length; columnI++)
				queryString += "?,";
			
			queryString = queryString.substring(0, queryString.length() - 1);
			queryString += ")";

			try {
				stmnt = conn.prepareStatement(queryString);
				
				for (int columnI = 0; columnI < columns.length; columnI++) {
					setStatement(stmnt, columns[columnI].classs, columnI+1, values[columnI]);
				}
				stmnt.executeUpdate();
				
				stmnt = conn.prepareStatement("select last_insert_rowid()");
				ResultSet id = stmnt.executeQuery();
				return id.getInt("last_insert_rowid()");
			}
			catch (SQLException e) {
				throw new DatabaseException(e);
			}
		}
		
		/**
		 * Sets the sql statement to be used by the sqlite connection.
		 *
		 * @param stmnt the statement that will be executed
		 * @param objClass the class of the object being assigned
		 * @param idx the column number for the object
		 * @param val the object to be set
		 * @throws SQLException the sQL exception
		 */
		private void setStatement(PreparedStatement stmnt, Class objClass, int idx, Object val)
				throws SQLException 
		{
			if (objClass == Integer.class)
				stmnt.setInt(idx, (Integer) val);
			else if (objClass == String.class) 
				stmnt.setString(idx, (String) val);
			else if (objClass == Boolean.class)
				stmnt.setBoolean(idx, (Boolean) val);
		}
 
		/**
		 * Used to pull the right type of information out of the resultset. 
		 * This is used to get either an Integer, String, or Boolean from the
		 * resultset.
		 *
		 * @param resultSet the result set from the sqlite query
		 * @param columnIndex the column index in the database table
		 * @param classs the class of the Object to find
		 * @return the Object from the resultset
		 * @throws SQLException the sQL exception
		 */
		private Object getFromResultWithType(ResultSet resultSet, int columnIndex, Class classs) throws SQLException {
			if (classs == Integer.class)
				return resultSet.getInt(columnIndex);
			else if (classs == String.class) {
				return resultSet.getString(columnIndex);
			}
			else if (classs == Boolean.class) {
				return resultSet.getBoolean(columnIndex);
			}
			
			return null;
		}
		
		/**
		 * Parses the result set and constructs a list of type T
		 * with the appropriately constructed objects.
		 *
		 * @param resultSet the result set from the sqlite query
		 * @return the list of type constructed objects of type T gathered from the resultset
		 * @throws DatabaseException the database exception
		 */
		private List<T> parseResultSet(ResultSet resultSet) throws DatabaseException {
			try {
				List<T> list = new LinkedList<T>();
				
				while (resultSet.next()) {
					Class[] constructorParameters = new Class[columns.length];
					Object[] constructorArguments = new Object[columns.length];
					for (int i = 0; i < columns.length; i++) {
						constructorParameters[i] = columns[i].classs;
						constructorArguments[i] = getFromResultWithType(resultSet, i+1, columns[i].classs);
					}
					
					Constructor derp = classs.getConstructor(constructorParameters);
					T object = (T)derp.newInstance(constructorArguments);
					list.add(object);
				}
				
				return list;
			}
			catch (Exception e) {
				throw new DatabaseException(e);
			}
		}
	}


	/* (non-Javadoc)
	 * @see scheduler.model.db.IDatabase#findUserByUsername(java.lang.String)
	 */
	public IDBUser findUserByUsername(String username) throws DatabaseException {
		List<SQLUser> result;
		HashMap<String, Object> wheres = new HashMap<String, Object>();
		wheres.put("username", username);
		
		result = userdataTable.select(wheres);
		
		if (result.size() == 0)
			throw new DatabaseException("No user found in SQLdb.findUserByUsername");
		
		return result.get(0);
	}

	/* (non-Javadoc)
	 * @see scheduler.model.db.IDatabase#assembleUser(java.lang.String, boolean)
	 */
	public IDBUser assembleUser(String username, boolean isAdmin) {
		return new SQLUser(null, username, isAdmin);
	}

	/* (non-Javadoc)
	 * @see scheduler.model.db.IDatabase#insertUser(scheduler.model.db.IDBUser)
	 */
	@Override
	public void insertUser(IDBUser user) throws DatabaseException {
		SQLUser sqluser = (SQLUser) user;
//		if(user == null)
//			throw new DatabaseException("Invalid user");
		System.out.println("Inserting user: " + user.getUsername() + " " + user.isAdmin());
		sqluser.id = userdataTable.insert(new Object[]{ user.getUsername(), user.isAdmin()});
		if(sqluser.id.intValue() < 0)
			System.out.println("This is NOT SUPPOSED TO HAPPEN");
	}


	/* (non-Javadoc)
	 * @see scheduler.model.db.IDatabase#updateUser(scheduler.model.db.IDBUser)
	 */
	@Override
	public void updateUser(IDBUser user) throws DatabaseException {
		userdataTable.update(new Object[] {user.getUsername(), user.isAdmin()}, user.getID());
	}

	/* (non-Javadoc)
	 * @see scheduler.model.db.IDatabase#deleteUser(scheduler.model.db.IDBUser)
	 */
	@Override
	public void deleteUser(IDBUser user) throws DatabaseException {
		userdataTable.delete(user.getID());
	}

	/* (non-Javadoc)
	 * @see scheduler.model.db.IDatabase#findAllDocuments()
	 */
	@Override
	public Collection<IDBDocument> findAllDocuments() throws DatabaseException {
		ArrayList<IDBDocument> result = new ArrayList<IDBDocument>();

		for(SQLDocument docItem : documentTable.selectAll())
			result.add(docItem);
		
		if (result.size() == 0) 
			throw new DatabaseException("No documents found in SQLdb.findAllDocuments");
		
		return result;
	}

	/* (non-Javadoc)
	 * @see scheduler.model.db.IDatabase#findDocumentByID(int)
	 */
	@Override
	public IDBDocument findDocumentByID(int id) throws DatabaseException, NotFoundException {	
		List<SQLDocument> result;
		HashMap<String, Object> wheres = new HashMap<String, Object>();
		wheres.put("id", id);
		result = documentTable.select(wheres);
		
		if (result.size() == 0)
			throw new NotFoundException("No document found in SQLdb.findDocumentByID");//DatabaseException("No document found in SQLdb.findDocumentByID");

		return result.get(0);
	}

	/* (non-Javadoc)
	 * @see scheduler.model.db.IDatabase#insertDocument(scheduler.model.db.IDBDocument)
	 */
	@Override
	public void insertDocument(IDBDocument document) throws DatabaseException {
		SQLDocument doc = (SQLDocument) document;
		doc.id = documentTable.insert(new Object[]{ doc.getName(), doc.isTrashed(), doc.getStartHalfHour(), doc.getEndHalfHour() });
	}

	/* (non-Javadoc)
	 * @see scheduler.model.db.IDatabase#assembleDocument(java.lang.String, int, int)
	 */
	@Override
	public IDBDocument assembleDocument(String name, int startHalfHour,
			int endHalfHour) throws DatabaseException {
		return new SQLDocument(null, name, false, startHalfHour, endHalfHour, null, null, null, null);
	}

	/* (non-Javadoc)
	 * @see scheduler.model.db.IDatabase#updateDocument(scheduler.model.db.IDBDocument)
	 */
	@Override
	public void updateDocument(IDBDocument document) throws DatabaseException {		
		documentTable.update(new Object[] {document.getName(), document.isTrashed(), document.getStartHalfHour(), 
				document.getEndHalfHour()}, document.getID());
	}

	/* (non-Javadoc)
	 * @see scheduler.model.db.IDatabase#deleteDocument(scheduler.model.db.IDBDocument)
	 */
	@Override
	public void deleteDocument(IDBDocument document) throws DatabaseException {
		assert(document != null);
		assert(document.getID() != null);
		documentTable.delete(document.getID());
	}

	/* (non-Javadoc)
	 * @see scheduler.model.db.IDatabase#isOriginalDocument(scheduler.model.db.IDBDocument)
	 */
	@Override
	public boolean isOriginalDocument(IDBDocument doc) throws DatabaseException {
		List<SQLWorkingCopy> result;
		HashMap<String, Object> wheres = new HashMap<String, Object>();
		wheres.put("id", doc.getID());
		result = workingcopyTable.select(wheres);
		return (result.size() == 0);
	}

	/* (non-Javadoc)
	 * @see scheduler.model.db.IDatabase#documentIsWorkingCopy(scheduler.model.db.IDBDocument)
	 */
	@Override
	public boolean documentIsWorkingCopy(IDBDocument document)
			throws DatabaseException {
		List<SQLWorkingCopy> result;
		HashMap<String, Object> wheres = new HashMap<String, Object>();
		wheres.put("id", document.getID());
		result = workingcopyTable.select(wheres);
		return (result.size() != 0);
	}

	/* (non-Javadoc)
	 * @see scheduler.model.db.IDatabase#getOriginalForWorkingCopyDocumentOrNull(scheduler.model.db.IDBDocument)
	 */
	@Override
	public IDBDocument getOriginalForWorkingCopyDocumentOrNull(IDBDocument rawDocument)
			throws DatabaseException {
		List<SQLDocument> doc = null;
		List<SQLWorkingCopy> wc = null;
		HashMap<String, Object> wheres = new HashMap<String, Object>();

		wheres.put("id", rawDocument.getID());
		wc = workingcopyTable.select(wheres);
		if (wc != null && wc.size() != 0) {
			wheres.clear();
			wheres.put("id", wc.get(0).getOriginalDocID());
			doc = documentTable.select(wheres);
			
			if (doc.size() != 0)
				return doc.get(0);
		}
		return null;
	}


	/* (non-Javadoc)
	 * @see scheduler.model.db.IDatabase#getWorkingCopyForOriginalDocumentOrNull(scheduler.model.db.IDBDocument)
	 */
	@Override
	public IDBDocument getWorkingCopyForOriginalDocumentOrNull(
			IDBDocument document) throws DatabaseException {
		SQLDocument doc = null;
		SQLWorkingCopy wc = null;
		HashMap<String, Object> wheres = new HashMap<String, Object>();
		
		wheres.put("originalDocID", document.getID());
		wc = workingcopyTable.select(wheres).get(0);
		if (wc != null) {
			wheres.clear();
			wheres.put("id", wc.getID());
			doc = documentTable.select(wheres).get(0);
		}
		return doc;
	}

	/* (non-Javadoc)
	 * @see scheduler.model.db.IDatabase#associateWorkingCopyWithOriginal(scheduler.model.db.IDBDocument, scheduler.model.db.IDBDocument)
	 */
	@Override
	public void associateWorkingCopyWithOriginal(
			IDBDocument underlyingDocument, IDBDocument underlyingDocument2)
			throws DatabaseException {
		workingcopyTable.insertWithID(new Object[] {underlyingDocument.getID(), underlyingDocument2.getID()});
	}

	/* (non-Javadoc)
	 * @see scheduler.model.db.IDatabase#disassociateWorkingCopyWithOriginal(scheduler.model.db.IDBDocument, scheduler.model.db.IDBDocument)
	 */
	@Override
	public void disassociateWorkingCopyWithOriginal(
			IDBDocument underlyingDocument, IDBDocument underlyingDocument2)
			throws DatabaseException {
		workingcopyTable.delete(underlyingDocument.getID());
	}

	/* (non-Javadoc)
	 * @see scheduler.model.db.IDatabase#findScheduleItemsByDocument(scheduler.model.db.IDBDocument)
	 */
	@Override
	public Collection<IDBScheduleItem> findScheduleItemsByDocument(
			IDBDocument document) throws DatabaseException {
		Collection<IDBScheduleItem> result = new LinkedList<IDBScheduleItem>();
		SQLDocument sched = (SQLDocument) document;
		HashMap<String, Object> wheres = new HashMap<String, Object>();
		
		wheres.put("id", sched.getID());
		
		for(SQLScheduleItem schedItem : scheduleItemTable.select(wheres))
			result.add(schedItem);
		
		if (result.size() == 0)
			throw new DatabaseException("No scheduleItems found in SQLdb.findScheduleItemsBySchedule");
		
		return result;
	}

	/* (non-Javadoc)
	 * @see scheduler.model.db.IDatabase#findAllScheduleItemsForDocument(scheduler.model.db.IDBDocument)
	 */
	@Override
	public Collection<IDBScheduleItem> findAllScheduleItemsForDocument(
			IDBDocument document) throws DatabaseException {
		Collection<IDBScheduleItem> result = new LinkedList<IDBScheduleItem>();
		SQLDocument sched = (SQLDocument) document;
		HashMap<String, Object> wheres = new HashMap<String, Object>();
		
		wheres.put("id", sched.getID());
		
		for(SQLScheduleItem schedItem : scheduleItemTable.select(wheres))
			result.add(schedItem);
		
		if (result.size() == 0)
			throw new DatabaseException("No scheduleItems found in SQLdb.findScheduleItemsForDocument");
		
		return result;
	}

	/* (non-Javadoc)
	 * @see scheduler.model.db.IDatabase#findScheduleItemByID(int)
	 */
	@Override
	public IDBScheduleItem findScheduleItemByID(int id)
			throws DatabaseException {
		List<SQLScheduleItem> result;
		HashMap<String, Object> wheres = new HashMap<String, Object>();
		wheres.put("id", id);
		
		result = scheduleItemTable.select(wheres);
		
		if (result.size() == 0)
			throw new DatabaseException("No scheduleItems found in SQLdb.findScheduleItemsBySchedule");
		
		return result.get(0);
	}


	/* (non-Javadoc)
	 * @see scheduler.model.db.IDatabase#assembleScheduleItem(int, java.util.Set, int, int, boolean, boolean)
	 */
	@Override
	public IDBScheduleItem assembleScheduleItem(int section, Set<Day> days,
			int startHalfHour, int endHalfHour, boolean isPlaced,
			boolean isConflicted) throws DatabaseException {
		return new SQLScheduleItem(null, null, null, null, null, startHalfHour, endHalfHour, null,
				section, isConflicted, isPlaced, days);
	}

	/* (non-Javadoc)
	 * @see scheduler.model.db.IDatabase#insertScheduleItem(scheduler.model.db.IDBDocument, scheduler.model.db.IDBCourse, scheduler.model.db.IDBInstructor, scheduler.model.db.IDBLocation, scheduler.model.db.IDBScheduleItem)
	 */
	@Override
	public void insertScheduleItem(IDBDocument document, IDBCourse course,
			IDBInstructor instructor, IDBLocation location, IDBScheduleItem item)
			throws DatabaseException {
		SQLScheduleItem sqlItem = (SQLScheduleItem) item;
		Object[] insert = {document.getID(), instructor.getID(), location.getID(), 
				course.getID(), sqlItem.getStartHalfHour(), sqlItem.getEndHalfHour(),
				sqlItem.getDayPattern(), sqlItem.getSection(), sqlItem.isPlaced(), sqlItem.isConflicted()};
		scheduleItemTable.insert(insert);	
	}


	/* (non-Javadoc)
	 * @see scheduler.model.db.IDatabase#updateScheduleItem(scheduler.model.db.IDBScheduleItem)
	 */
	@Override
	public void updateScheduleItem(IDBScheduleItem schedule)
			throws DatabaseException {
		SQLScheduleItem schedItem = (SQLScheduleItem) schedule;
		scheduleItemTable.update(new Object[] {schedItem.getDocID(), schedItem.getInstID(),
				schedItem.getLocID(), schedItem.getCourseID(), schedItem.getStartHalfHour(),
				schedItem.getEndHalfHour(), schedItem.getDayPattern(), schedItem.getSection(),
				schedItem.isPlaced(), schedItem.isConflicted()}, schedItem.getID());
	}


	/* (non-Javadoc)
	 * @see scheduler.model.db.IDatabase#deleteScheduleItem(scheduler.model.db.IDBScheduleItem)
	 */
	@Override
	public void deleteScheduleItem(IDBScheduleItem schedule)
			throws DatabaseException {
		scheduleItemTable.delete(schedule.getID());
	}


	/* (non-Javadoc)
	 * @see scheduler.model.db.IDatabase#getScheduleItemLocation(scheduler.model.db.IDBScheduleItem)
	 */
	@Override
	public IDBLocation getScheduleItemLocation(IDBScheduleItem item)
			throws DatabaseException {
		SQLScheduleItem schedItem = (SQLScheduleItem) item;
		List<SQLLocation> ret = null;
		HashMap<String, Object> wheres = new HashMap<String, Object>();
		wheres.put("id", schedItem.getLocID());
		
		ret = locationTable.select(wheres);
		if (ret.size() == 0) {
			throw new DatabaseException("No location found for schedule item");
		}
		
		return ret.get(0);
	}


	/* (non-Javadoc)
	 * @see scheduler.model.db.IDatabase#getScheduleItemCourse(scheduler.model.db.IDBScheduleItem)
	 */
	@Override
	public IDBCourse getScheduleItemCourse(IDBScheduleItem item)
			throws DatabaseException {
		SQLScheduleItem schedItem = (SQLScheduleItem) item;
		List<SQLCourse> ret = null;
		HashMap<String, Object> wheres = new HashMap<String, Object>();
		wheres.put("id", schedItem.getCourseID());
		
		ret = courseTable.select(wheres);
		if (ret.size() == 0) {
			throw new DatabaseException("No course found for schedule item");
		}
		
		return ret.get(0);
	}


	/* (non-Javadoc)
	 * @see scheduler.model.db.IDatabase#getScheduleItemInstructor(scheduler.model.db.IDBScheduleItem)
	 */
	@Override
	public IDBInstructor getScheduleItemInstructor(IDBScheduleItem item)
			throws DatabaseException {
		SQLScheduleItem schedItem = (SQLScheduleItem) item;
		List<SQLInstructor> ret = null;
		HashMap<String, Object> wheres = new HashMap<String, Object>();
		wheres.put("id", schedItem.getInstID());
		
		ret = instructorTable.select(wheres);
		if (ret.size() == 0) {
			throw new DatabaseException("No instructor found for schedule item");
		}
		
		return ret.get(0);
	}


	/* (non-Javadoc)
	 * @see scheduler.model.db.IDatabase#setScheduleItemCourse(scheduler.model.db.IDBScheduleItem, scheduler.model.db.IDBCourse)
	 */
	@Override
	public void setScheduleItemCourse(IDBScheduleItem underlying,
			IDBCourse findCourseByID) throws DatabaseException {
		SQLScheduleItem schedItem = (SQLScheduleItem) underlying;
		schedItem.setCourseID(findCourseByID.getID());
		
		scheduleItemTable.update(new Object[] {schedItem.getDocID(), schedItem.getInstID(),
				schedItem.getLocID(), schedItem.getCourseID(), schedItem.getStartHalfHour(),
				schedItem.getEndHalfHour(), schedItem.getDayPattern(), schedItem.getSection(),
				schedItem.isPlaced(), schedItem.isConflicted()}, schedItem.getID());
	}


	/* (non-Javadoc)
	 * @see scheduler.model.db.IDatabase#setScheduleItemLocation(scheduler.model.db.IDBScheduleItem, scheduler.model.db.IDBLocation)
	 */
	@Override
	public void setScheduleItemLocation(IDBScheduleItem underlying,
			IDBLocation findLocationByID) throws DatabaseException {
		SQLScheduleItem schedItem = (SQLScheduleItem) underlying;
		schedItem.setLocID(findLocationByID.getID());
		
		scheduleItemTable.update(new Object[] {schedItem.getDocID(), schedItem.getInstID(),
				schedItem.getLocID(), schedItem.getCourseID(), schedItem.getStartHalfHour(),
				schedItem.getEndHalfHour(), schedItem.getDayPattern(), schedItem.getSection(),
				schedItem.isPlaced(), schedItem.isConflicted()}, schedItem.getID());
	}


	/* (non-Javadoc)
	 * @see scheduler.model.db.IDatabase#setScheduleItemInstructor(scheduler.model.db.IDBScheduleItem, scheduler.model.db.IDBInstructor)
	 */
	@Override
	public void setScheduleItemInstructor(IDBScheduleItem underlying,
			IDBInstructor findInstructorByID) throws DatabaseException {
		SQLScheduleItem schedItem = (SQLScheduleItem) underlying;
		schedItem.setInstID(findInstructorByID.getID());
		
		scheduleItemTable.update(new Object[] {schedItem.getDocID(), schedItem.getInstID(),
				schedItem.getLocID(), schedItem.getCourseID(), schedItem.getStartHalfHour(),
				schedItem.getEndHalfHour(), schedItem.getDayPattern(), schedItem.getSection(),
				schedItem.isPlaced(), schedItem.isConflicted()}, schedItem.getID());
	}


	/* (non-Javadoc)
	 * @see scheduler.model.db.IDatabase#findLocationsForDocument(scheduler.model.db.IDBDocument)
	 */
	@Override
	public Collection<IDBLocation> findLocationsForDocument(IDBDocument document)
			throws DatabaseException {
		Collection<IDBLocation> result = new LinkedList<IDBLocation>();
		SQLDocument doc = (SQLDocument) document;
		HashMap<String, Object> wheres = new HashMap<String, Object>();
		
		wheres.put("docID", doc.getID());
		
		for(SQLLocation loc : locationTable.select(wheres))
			result.add(loc);
		
		return result;
	}

	/* (non-Javadoc)
	 * @see scheduler.model.db.IDatabase#findLocationByID(int)
	 */
	@Override
	public IDBLocation findLocationByID(int id) throws DatabaseException {
		HashMap<String, Object> wheres = new HashMap<String, Object>();
		wheres.put("id", id);
		if(locationTable.select(wheres).isEmpty())
			throw new NotFoundException("Location not found with ID: " + id);
		return locationTable.select(wheres).get(0);
	}

	/* (non-Javadoc)
	 * @see scheduler.model.db.IDatabase#assembleLocation(java.lang.String, java.lang.String, java.lang.String, boolean)
	 */
	@Override
	public IDBLocation assembleLocation(String room, String type,
			String maxOccupancy, boolean isSchedulable)
			throws DatabaseException {
		return new SQLLocation(null, null, maxOccupancy, type, 
				room, new Boolean(isSchedulable));
	}


	/* (non-Javadoc)
	 * @see scheduler.model.db.IDatabase#insertLocation(scheduler.model.db.IDBDocument, scheduler.model.db.IDBLocation)
	 */
	@Override
	public void insertLocation(IDBDocument containingDocument,
			IDBLocation location) throws DatabaseException {
		SQLLocation sqlLocation = (SQLLocation) location;
		assert(sqlLocation.id == null);
		//sqlLocation.docID = containingDocument.getID();
		sqlLocation.id = locationTable.insert(new Object[]{ containingDocument.getID(), sqlLocation.maxOccupancy,
				sqlLocation.type, sqlLocation.room, sqlLocation.schedulable});
	}


	/* (non-Javadoc)
	 * @see scheduler.model.db.IDatabase#updateLocation(scheduler.model.db.IDBLocation)
	 */
	@Override
	public void updateLocation(IDBLocation location) throws DatabaseException {
		assert(location!=null) : "null location";
		assert(location.getID() != null) : "null location id";
		assert(location.getMaxOccupancy() != null);
		assert(location.getType() != null);
		assert(location.getRoom() != null);
		//assert(location. != null);
		
		SQLLocation sqlLocation = (SQLLocation)location;
		System.out.println(sqlLocation.docID);
		locationTable.update(new Object[] {sqlLocation.docID, sqlLocation.maxOccupancy, sqlLocation.type, sqlLocation.room, 
				sqlLocation.schedulable}, sqlLocation.getID());
	}


	/* (non-Javadoc)
	 * @see scheduler.model.db.IDatabase#deleteLocation(scheduler.model.db.IDBLocation)
	 */
	@Override
	public void deleteLocation(IDBLocation location) throws DatabaseException {
		locationTable.delete(location.getID());
	}


	/* (non-Javadoc)
	 * @see scheduler.model.db.IDatabase#findCoursesForDocument(scheduler.model.db.IDBDocument)
	 */
	@Override
	public Collection<IDBCourse> findCoursesForDocument(IDBDocument document)
			throws DatabaseException {
		Collection<IDBCourse> result = new LinkedList<IDBCourse>();
		SQLDocument doc = (SQLDocument) document;
		HashMap<String, Object> wheres = new HashMap<String, Object>();
		
		wheres.put("docID", doc.getID());
		
		for(SQLCourse course : courseTable.select(wheres))
			result.add(course);
		
		return result;
	}


	/* (non-Javadoc)
	 * @see scheduler.model.db.IDatabase#findCourseByID(int)
	 */
	@Override
	public IDBCourse findCourseByID(int id) throws DatabaseException {
		HashMap<String, Object> wheres = new HashMap<String, Object>();
		wheres.put("id", id);
		if(courseTable.select(wheres).isEmpty())
			throw new NotFoundException("Course with ID " + id + " not found");
		return courseTable.select(wheres).get(0);
	}


	/* (non-Javadoc)
	 * @see scheduler.model.db.IDatabase#assembleCourse(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, boolean)
	 */
	@Override
	public IDBCourse assembleCourse(String name, String catalogNumber,
			String department, String wtu, String scu, String numSections,
			String type, String maxEnrollment, String numHalfHoursPerWeek,
			boolean isSchedulable) throws DatabaseException {
		return new SQLCourse(null, null, maxEnrollment, wtu, scu, type, numSections, department, catalogNumber,
				name, new Boolean(isSchedulable), numHalfHoursPerWeek);
	}


	/* (non-Javadoc)
	 * @see scheduler.model.db.IDatabase#insertCourse(scheduler.model.db.IDBDocument, scheduler.model.db.IDBCourse)
	 */
	@Override
	public void insertCourse(IDBDocument underlyingDocument, IDBCourse course)
			throws DatabaseException {
		assert(course != null) : "Specified course null";
		SQLCourse sqlCourse = (SQLCourse) course;
		assert(sqlCourse.id == null);
		sqlCourse.documentID = underlyingDocument.getID();
		//this works with sqldocument.documentID, forDocumentsTest
		sqlCourse.id = courseTable.insert(new Object[]{ sqlCourse.documentID, sqlCourse.maxEnrollment, sqlCourse.wtu, sqlCourse.scu, sqlCourse.type, sqlCourse.numSections,
				sqlCourse.department, sqlCourse.catalogNumber, sqlCourse.name, sqlCourse.isSchedulable, sqlCourse.numHalfHoursPerWeek});
		//System.out.println("inserting course. id is: " + sqlCourse.id);
	}


	/* (non-Javadoc)
	 * @see scheduler.model.db.IDatabase#updateCourse(scheduler.model.db.IDBCourse)
	 */
	@Override
	public void updateCourse(IDBCourse course) throws DatabaseException {
		assert(course!=null);
		assert(course.getID() != null);
		SQLCourse sqlCourse = (SQLCourse)course;
		courseTable.update(new Object[] {sqlCourse.getID(), sqlCourse.documentID, sqlCourse.maxEnrollment, sqlCourse.wtu, sqlCourse.scu, sqlCourse.type, sqlCourse.numSections,
				sqlCourse.department, sqlCourse.catalogNumber, sqlCourse.name, sqlCourse.isSchedulable, sqlCourse.numHalfHoursPerWeek}, sqlCourse.getID());
	}


	/* (non-Javadoc)
	 * @see scheduler.model.db.IDatabase#deleteCourse(scheduler.model.db.IDBCourse)
	 */
	@Override
	public void deleteCourse(IDBCourse course) throws DatabaseException {
		courseTable.delete(course.getID());
	}


	/* (non-Javadoc)
	 * @see scheduler.model.db.IDatabase#findDocumentForCourse(scheduler.model.db.IDBCourse)
	 */
	@Override
	public IDBDocument findDocumentForCourse(IDBCourse underlyingCourse)
			throws DatabaseException {

		if(underlyingCourse == null || underlyingCourse.getID() == null)
			throw new DatabaseException("Course not found");

		
		HashMap<String, Object> wheres = new HashMap<String, Object>();	
		wheres.put("docID", ((SQLCourse)underlyingCourse).documentID);

		if(documentTable.selectAll().isEmpty())
			throw new DatabaseException("Course not found"); 

		return documentTable.select(wheres).get(0);
	}


	/* (non-Javadoc)
	 * @see scheduler.model.db.IDatabase#getAssociationForLabOrNull(scheduler.model.db.IDBCourse)
	 */
	@Override
	public IDBCourseAssociation getAssociationForLabOrNull(IDBCourse underlying)
			throws DatabaseException {
		SQLCourseAssociation courseAssoc;
		SQLCourse labcourse = (SQLCourse)underlying;
		HashMap<String, Object> labID = new HashMap<String, Object>();
		labID.put("id", labcourse.id);
		
		if (!labcourse.getType().equals("LAB")) { //perhaps ACT also, I'll double check how it's handled in alg
			//then make sure it's not considered as a labID anywhere, it's a lec-type object
			assert(labassociationsTable.select(labID).isEmpty());
			return null; // TODO: I think better to throw a database exception, opinions?
		}
		//it does not need to have an association
		else if(labassociationsTable.select(labID).isEmpty())
			return null;
		else 
			return labassociationsTable.select(labID).get(0);
	}

	/* (non-Javadoc)
	 * @see scheduler.model.db.IDatabase#getAssociationsForLecture(scheduler.model.db.IDBCourse)
	 */
	@Override
	public Collection<IDBCourseAssociation> getAssociationsForLecture(
			IDBCourse lectureCourse) throws DatabaseException {
		
		SQLCourse sqlLectureCourse = (SQLCourse)lectureCourse;
		Collection<IDBCourseAssociation> result = new LinkedList<IDBCourseAssociation>();
		
		assert(sqlLectureCourse!=null);
		assert(sqlLectureCourse.getID() != null);
		//make sure it's a lecture type specifically
		assert(sqlLectureCourse.type == "LEC");
		HashMap<String, Object> checkNotLabID = new HashMap<String, Object>();
		checkNotLabID.put("id", sqlLectureCourse.id);
		assert(labassociationsTable.select(checkNotLabID).isEmpty());
		
		//get all the course 'labs' that share a lecID with our input course ID
		for(SQLCourseAssociation assoc : labassociationsTable.selectAll())
			if(assoc.lectureID == sqlLectureCourse.id)
				result.add(assoc);		
		return result;
	}


	/* (non-Javadoc)
	 * @see scheduler.model.db.IDatabase#getAssociationLecture(scheduler.model.db.IDBCourseAssociation)
	 */
	@Override
	public IDBCourse getAssociationLecture(IDBCourseAssociation association)
			throws DatabaseException {
		//Get the lecture course component of the association link
		SQLCourseAssociation sqlAssoc = (SQLCourseAssociation)association;
		HashMap<String, Object> assocID = new HashMap<String, Object>();
		assocID.put("lecID", sqlAssoc.lectureID);
		try {
			return courseTable.select(assocID).get(0);
		}
		catch (NotFoundException e){
			throw new AssertionError(e);
		}
	}


	/* (non-Javadoc)
	 * @see scheduler.model.db.IDatabase#getAssociationLab(scheduler.model.db.IDBCourseAssociation)
	 */
	@Override
	public IDBCourse getAssociationLab(IDBCourseAssociation association)
			throws DatabaseException {
		// Get the lab component of the provided course association link
		HashMap<String, Object> labCourseID = new HashMap<String, Object>();
		SQLCourseAssociation assoc = (SQLCourseAssociation)association;
		labCourseID.put("id", assoc.getLabID());
		
		try {
			return courseTable.select(labCourseID).get(0);
		} catch (NotFoundException e) {
			throw new AssertionError(e);
		}
	}

	/* (non-Javadoc)
	 * @see scheduler.model.db.IDatabase#associateLectureAndLab(scheduler.model.db.IDBCourse, scheduler.model.db.IDBCourse, boolean)
	 */
	@Override
	public void associateLectureAndLab(IDBCourse lecture, IDBCourse lab, boolean tethered)
			throws DatabaseException {
		//try to create a labassociation between the lecture and lab, tether if needed. Exception if can't happen.
		assert(lecture!=null && lecture.getID() != null && lab != null && lab.getID() != null);
		SQLCourse leccourse = (SQLCourse) lecture;
		SQLCourse labcourse = (SQLCourse)lab;
		
		//make sure lec not a lab, and lab *is* a lab, double check valid types for associations or it'll bug
		if (!labcourse.getType().equals("LAB")) 
			throw new DatabaseException("Course " + labcourse.getName() + " with ID: " + labcourse.getID() + " is not a lab");
		if (!leccourse.getType().equals("LEC")) 
			throw new DatabaseException("Course " + leccourse.getName() + " with ID: " + leccourse.getID() + " is not a lecture");
		
		labassociationsTable.insertWithID(new Object[] {labcourse.getID(), leccourse.getID(), tethered});
	}
	
	/* (non-Javadoc)
	 * @see scheduler.model.db.IDatabase#findInstructorsForDocument(scheduler.model.db.IDBDocument)
	 */
	@Override
	public Collection<IDBInstructor> findInstructorsForDocument(IDBDocument document) throws DatabaseException {
		assert(document != null) : "found null document";
		
		Collection<IDBInstructor> result = new LinkedList<IDBInstructor>();
		
		SQLDocument doc = (SQLDocument) document;
		HashMap<String, Object> instID = new HashMap<String, Object>();
		
		instID.put("docID", doc.getID());
		
		for(SQLInstructor ins : instructorTable.select(instID))
			result.add(ins);
		
		return result;
	}

	/* (non-Javadoc)
	 * @see scheduler.model.db.IDatabase#findInstructorByID(int)
	 */
	@Override
	public IDBInstructor findInstructorByID(int id) throws DatabaseException {
		HashMap<String, Object> instID = new HashMap<String, Object>();
		instID.put("id", id);
		if(instructorTable.select(instID).isEmpty())
			throw new NotFoundException("Instructor with ID " + id + " was not found");
		return instructorTable.select(instID).get(0);
	}

	/* (non-Javadoc)
	 * @see scheduler.model.db.IDatabase#assembleInstructor(java.lang.String, java.lang.String, java.lang.String, java.lang.String, boolean)
	 */
	@Override
	public IDBInstructor assembleInstructor(String firstName, String lastName,
			String username, String maxWTU, boolean isSchedulable)
			throws DatabaseException {
		return new SQLInstructor(null, null, firstName, lastName, username, maxWTU, isSchedulable);
	}

	/* (non-Javadoc)
	 * @see scheduler.model.db.IDatabase#insertInstructor(scheduler.model.db.IDBDocument, scheduler.model.db.IDBInstructor)
	 */
	@Override
	public void insertInstructor(IDBDocument containingDocument, IDBInstructor instructor) throws DatabaseException {
		//assert(instructor != null) : "Specified instructor null";
		SQLInstructor sqlInstructor = (SQLInstructor) instructor;
		assert(sqlInstructor.id == null);
		sqlInstructor.documentID = containingDocument.getID();
		//this works with sqldocument.documentID, forDocumentsTest
		sqlInstructor.id = instructorTable.insert(new Object[]{ sqlInstructor.documentID, 
				sqlInstructor.getFirstName(), sqlInstructor.getLastName(), sqlInstructor.getUsername(), sqlInstructor.getMaxWTU(), 
				sqlInstructor.isSchedulable()});
		//System.out.println("inserting instructor. id is: " + sqlInstructor.id);
	}

	/* (non-Javadoc)
	 * @see scheduler.model.db.IDatabase#updateInstructor(scheduler.model.db.IDBInstructor)
	 */
	@Override
	public void updateInstructor(IDBInstructor instructor)
			throws DatabaseException {
		assert(instructor!=null);
		assert(instructor.getID() != null);
		SQLInstructor sqlInstructor = (SQLInstructor)instructor;
		instructorTable.update(new Object[] {sqlInstructor.documentID, 
				sqlInstructor.getFirstName(), sqlInstructor.getLastName(), sqlInstructor.getUsername(), sqlInstructor.getMaxWTU(), 
				sqlInstructor.isSchedulable()}, sqlInstructor.getID());
	}

	/* (non-Javadoc)
	 * @see scheduler.model.db.IDatabase#deleteInstructor(scheduler.model.db.IDBInstructor)
	 */
	@Override
	public void deleteInstructor(IDBInstructor instructor) throws DatabaseException {
		assert(instructor != null) : "Specified instructor null";
		if(instructor == null || instructor.getID() == null)
			throw new DatabaseException("Instructor not found");
		instructorTable.delete(instructor.getID());	
	}

	/* (non-Javadoc)
	 * @see scheduler.model.db.IDatabase#findTimePreferencesByTimeForInstructor(scheduler.model.db.IDBInstructor)
	 */
	@Override
	public Map<IDBTime, IDBTimePreference> findTimePreferencesByTimeForInstructor(
			IDBInstructor instructor) throws DatabaseException {
		HashMap<IDBTime, IDBTimePreference> result = new HashMap<IDBTime, IDBTimePreference>();
		if(instructor== null || instructor.getID() == null)
			throw new DatabaseException("Instructor not found");
		try {System.out.println(timeslotprefTable.selectAll());
		} catch (Exception e) {
			e.printStackTrace();
		}
		for(SQLTimePreference tp : timeslotprefTable.selectAll())
			if(tp.instructorID == instructor.getID())
				result.put(findTimeByID(tp.timeID), tp);
		return result;
	}

	//possibly temporary helper method
	/**
	 * Find time by id.
	 *
	 * @param id the id
	 * @return the iDB time
	 */
	public IDBTime findTimeByID(int id) {
		return new SQLTime(id);
	}
	
	/* (non-Javadoc)
	 * @see scheduler.model.db.IDatabase#findTimePreferenceByID(int)
	 */
	@Override
	public IDBTimePreference findTimePreferenceByID(int id)
			throws DatabaseException {
		if(id < 0)
			throw new DatabaseException("Invalid Time Preference ID");
		HashMap<String, Object> timeID = new HashMap<String, Object>();
		timeID.put("id", id);
		return timeslotprefTable.select(timeID).get(0);
	}

	/* (non-Javadoc)
	 * @see scheduler.model.db.IDatabase#assembleTimePreference(int)
	 */
	@Override
	public IDBTimePreference assembleTimePreference(int preflevel)
			throws DatabaseException {
		return new SQLTimePreference(null, null, null, preflevel);
	}

	/* (non-Javadoc)
	 * @see scheduler.model.db.IDatabase#insertTimePreference(scheduler.model.db.IDBInstructor, scheduler.model.db.IDBTime, scheduler.model.db.IDBTimePreference)
	 */
	@Override
	public void insertTimePreference(IDBInstructor ins, IDBTime time, IDBTimePreference timePreference) throws DatabaseException {
		
		SQLTimePreference sqlTP = (SQLTimePreference)timePreference;
		assert(sqlTP.id == null);
		SQLTime thisTime = (SQLTime)time;
		assert(thisTime.getID() != null);
		//System.out.println("Inserting instructor ID, timeID : " + ins.getID() + " " + thisTime.getID());
		//try {
		sqlTP.id = timeslotprefTable.insert(new Object[]{thisTime.getID(), ins.getID(), sqlTP.preference});
//		} catch (Exception e) {
//			e.printStackTrace();
//			System.out.println("error: " +  ins.getID() + " " + thisTime.getID());
//		}

	}


	/* (non-Javadoc)
	 * @see scheduler.model.db.IDatabase#updateTimePreference(scheduler.model.db.IDBTimePreference)
	 */
	@Override
	public void updateTimePreference(IDBTimePreference timePreference)
			throws DatabaseException {
		SQLTimePreference pref = (SQLTimePreference) timePreference;
		//SQLTime time = (SQLTime)findTimeByID(pref.timeID);
		timeslotprefTable.update(new Object[]{ pref.timeID, pref.instructorID, pref.getPreference()}, pref.getID());
		
	}


	/* (non-Javadoc)
	 * @see scheduler.model.db.IDatabase#deleteTimePreference(scheduler.model.db.IDBTimePreference)
	 */
	@Override
	public void deleteTimePreference(IDBTimePreference timePreference)
			throws DatabaseException {
		timeslotprefTable.delete(timePreference.getID());	
	}


	/* (non-Javadoc)
	 * @see scheduler.model.db.IDatabase#findCoursePreferencesByCourseForInstructor(scheduler.model.db.IDBInstructor)
	 */
	@Override
	public Map<IDBCourse, IDBCoursePreference> findCoursePreferencesByCourseForInstructor(
			IDBInstructor instructor) throws DatabaseException {
		HashMap<IDBCourse, IDBCoursePreference> result = new HashMap<IDBCourse, IDBCoursePreference>();
		HashMap<String, Object> wheres = new HashMap<String, Object>();
		wheres.put("id", instructor.getID());
		instructorTable.select(wheres);
		for(SQLCoursePreference coursepref : courseprefTable.select(wheres))
		{
			result.put(findCourseByID(coursepref.courseID), coursepref);
		}
		return result;
	}


	/* (non-Javadoc)
	 * @see scheduler.model.db.IDatabase#findCoursePreferenceByID(int)
	 */
	@Override
	public IDBCoursePreference findCoursePreferenceByID(int id)
			throws DatabaseException {
		IDBCoursePreference result;
		HashMap<String, Object> wheres = new HashMap<String, Object>();
		wheres.put("id", id);
		result = courseprefTable.select(wheres).get(0);
		return result;
	}


	/* (non-Javadoc)
	 * @see scheduler.model.db.IDatabase#assembleCoursePreference(int)
	 */
	@Override
	public IDBCoursePreference assembleCoursePreference(int preference)
			throws DatabaseException {
		//Integer id, Integer instructorID, Integer courseID, int preference
		return new SQLCoursePreference(null, null, null, preference);
	}

	/* (non-Javadoc)
	 * @see scheduler.model.db.IDatabase#insertCoursePreference(scheduler.model.db.IDBInstructor, scheduler.model.db.IDBCourse, scheduler.model.db.IDBCoursePreference)
	 */
	@Override
	public void insertCoursePreference(IDBInstructor instructor,
			IDBCourse course, IDBCoursePreference coursePreference)
			throws DatabaseException {
		SQLCoursePreference sqlcoursepreference = (SQLCoursePreference) coursePreference;
		//(Integer id, Integer instructorID, Integer courseID, int preference)
		sqlcoursepreference.id = courseprefTable.insert(new Object[]{ instructor.getID(), course.getID(), sqlcoursepreference.getPreference()});
		
	}


	/* (non-Javadoc)
	 * @see scheduler.model.db.IDatabase#updateCoursePreference(scheduler.model.db.IDBCoursePreference)
	 */
	@Override
	public void updateCoursePreference(IDBCoursePreference coursePreference)
			throws DatabaseException {
		SQLCoursePreference sqlcoursepreference = (SQLCoursePreference) coursePreference;
		//(Integer id, Integer instructorID, Integer courseID, int preference)
		courseprefTable.update(new Object[]{ sqlcoursepreference.instructorID, sqlcoursepreference.courseID, sqlcoursepreference.getPreference()}, sqlcoursepreference.getID());
	}


	/* (non-Javadoc)
	 * @see scheduler.model.db.IDatabase#deleteCoursePreference(scheduler.model.db.IDBCoursePreference)
	 */
	@Override
	public void deleteCoursePreference(IDBCoursePreference coursePreference)
			throws DatabaseException {
		courseprefTable.delete(coursePreference.getID());
	}


	/* (non-Javadoc)
	 * @see scheduler.model.db.IDatabase#findTimeByDayAndHalfHour(int, int)
	 */
	@Override
	public IDBTime findTimeByDayAndHalfHour(int day, int halfHour)
			throws DatabaseException {
		return new SQLTime(day, halfHour);
	}

	/* (non-Javadoc)
	 * @see scheduler.model.db.IDatabase#findEquipmentTypeByDescription(java.lang.String)
	 */
	@Override
	public IDBEquipmentType findEquipmentTypeByDescription(
			String equipmentTypeDescription) throws DatabaseException {
		
		//TODO
		IDBEquipmentType result = null;
		HashMap<String, Object> wheres = new HashMap<String, Object>();
		wheres.put("desc", equipmentTypeDescription);
		//result = courseequipmentTable.select(wheres).get(0);

		return result;
	}


	/* (non-Javadoc)
	 * @see scheduler.model.db.IDatabase#findAllEquipmentTypes()
	 */
	@Override
	public Collection<IDBEquipmentType> findAllEquipmentTypes()
			throws DatabaseException {
		// TODO Auto-generated method stub
		return null;
	}


	/* (non-Javadoc)
	 * @see scheduler.model.db.IDatabase#findUsedEquipmentByEquipmentForCourse(scheduler.model.db.IDBCourse)
	 */
	@Override
	public Map<IDBEquipmentType, IDBUsedEquipment> findUsedEquipmentByEquipmentForCourse(
			IDBCourse course) throws DatabaseException {
		HashMap<IDBEquipmentType, IDBUsedEquipment> result = new HashMap<IDBEquipmentType, IDBUsedEquipment>();
		HashMap<String, Object> wheres = new HashMap<String, Object>();
		wheres.put("courseID", course.getID());
		courseequipmentTable.select(wheres);
		for(SQLUsedEquipment equip : courseequipmentTable.select(wheres))
		{
			IDBEquipmentType theType = null;
			for(IDBEquipmentType eq : findAllEquipmentTypes())
			{
				if(equip.equipmentTypeID == eq.getID())
				{
					theType = eq;
				}
			}
			result.put(theType, equip);
		}
		return result;
	}


	/* (non-Javadoc)
	 * @see scheduler.model.db.IDatabase#deleteUsedEquipment(scheduler.model.db.IDBUsedEquipment)
	 */
	@Override
	public void deleteUsedEquipment(IDBUsedEquipment usedEquipment)
			throws DatabaseException {
		courseequipmentTable.delete(usedEquipment.getID());
		
	}


	/* (non-Javadoc)
	 * @see scheduler.model.db.IDatabase#assembleUsedEquipment()
	 */
	@Override
	public IDBUsedEquipment assembleUsedEquipment() throws DatabaseException {
		//(Integer id, Integer courseID, Integer equipmentTypeID)
		return new SQLUsedEquipment(null, null, null);
	}


	/* (non-Javadoc)
	 * @see scheduler.model.db.IDatabase#insertUsedEquipment(scheduler.model.db.IDBCourse, scheduler.model.db.IDBEquipmentType, scheduler.model.db.IDBUsedEquipment)
	 */
	@Override
	public void insertUsedEquipment(IDBCourse course,
			IDBEquipmentType equipmentType, IDBUsedEquipment equip)
			throws DatabaseException {
		SQLUsedEquipment sqlusedequip = (SQLUsedEquipment) equip;
		//(Integer id, Integer courseID, Integer equipmentTypeID)
		sqlusedequip.id = courseequipmentTable.insert(new Object[]{ sqlusedequip.courseID, sqlusedequip.equipmentTypeID});
		
		
	}


	/* (non-Javadoc)
	 * @see scheduler.model.db.IDatabase#findProvidedEquipmentByEquipmentForLocation(scheduler.model.db.IDBLocation)
	 */
	@Override
	public Map<IDBEquipmentType, IDBProvidedEquipment> findProvidedEquipmentByEquipmentForLocation(
			IDBLocation location) throws DatabaseException {
		assert(location != null);
		assert(location.getID() != null);
		
		HashMap<IDBEquipmentType, IDBProvidedEquipment> result = new HashMap<IDBEquipmentType, IDBProvidedEquipment>();
		HashMap<String, Object> wheres = new HashMap<String, Object>();
		wheres.put("locID", location.getID());
		locationequipmentTable.select(wheres);
		for(SQLLocationEquipment equip : locationequipmentTable.select(wheres))
		{
			IDBEquipmentType eType = null;
			for(IDBEquipmentType equipType : findAllEquipmentTypes())
			{
				if(equip.equipID == equipType.getID())
					eType = equipType;
			}
			result.put(eType, equip);
		}
		return result;
	}


	/* (non-Javadoc)
	 * @see scheduler.model.db.IDatabase#deleteProvidedEquipment(scheduler.model.db.IDBProvidedEquipment)
	 */
	@Override
	public void deleteProvidedEquipment(IDBProvidedEquipment providedEquipment)
			throws DatabaseException {
		assert(providedEquipment != null);
		assert(providedEquipment.getID() != null);
		locationequipmentTable.delete(providedEquipment.getID());
		
	}


	/* (non-Javadoc)
	 * @see scheduler.model.db.IDatabase#assembleProvidedEquipment()
	 */
	@Override
	public IDBProvidedEquipment assembleProvidedEquipment()
			throws DatabaseException {
		//Integer id, Integer locationID, Integer equipmentTypeID
		return new SQLLocationEquipment(null, null, null);
	}



	/* (non-Javadoc)
	 * @see scheduler.model.db.IDatabase#insertProvidedEquipment(scheduler.model.db.IDBLocation, scheduler.model.db.IDBEquipmentType, scheduler.model.db.IDBProvidedEquipment)
	 */
	@Override
	public void insertProvidedEquipment(IDBLocation location,
			IDBEquipmentType equipmentType, IDBProvidedEquipment equip)
			throws DatabaseException {
		//System.out.println("location id: " + location.getID());
		SQLLocationEquipment sqlusedequip = (SQLLocationEquipment) equip;
		//Integer id, Integer locationID, Integer equipmentTypeID
		sqlusedequip.id = locationequipmentTable.insert(new Object[]{ sqlusedequip.locID, sqlusedequip.equipID});
		//System.out.println("inserted equipment: " + sqlusedequip.id);
	}


	/* (non-Javadoc)
	 * @see scheduler.model.db.IDatabase#findDayPatternByDays(java.util.Set)
	 */
	@Override
	public IDBDayPattern findDayPatternByDays(Set<Integer> dayPattern)
			throws DatabaseException {
		return new SQLDayPattern(dayPattern);
	}


	/* (non-Javadoc)
	 * @see scheduler.model.db.IDatabase#findOfferedDayPatternsForCourse(scheduler.model.db.IDBCourse)
	 */
	@Override
	public Collection<IDBOfferedDayPattern> findOfferedDayPatternsForCourse(
			IDBCourse underlying) throws DatabaseException {
		ArrayList<IDBOfferedDayPattern> result = new ArrayList<IDBOfferedDayPattern>();
		HashMap<String, Object> wheres = new HashMap<String, Object>();
		wheres.put("courseID", underlying.getID());
		coursepatternsTable.select(wheres);
		for(SQLOfferedDayPattern pat : coursepatternsTable.select(wheres))
		{
			result.add(pat);
		}
		return result;
	}


	/* (non-Javadoc)
	 * @see scheduler.model.db.IDatabase#getDayPatternForOfferedDayPattern(scheduler.model.db.IDBOfferedDayPattern)
	 */
	@Override
	public IDBDayPattern getDayPatternForOfferedDayPattern(
			IDBOfferedDayPattern offered) throws DatabaseException {
		return new SQLDayPattern(((SQLOfferedDayPattern)offered).dayPatternID);
	}


	/* (non-Javadoc)
	 * @see scheduler.model.db.IDatabase#deleteOfferedDayPattern(scheduler.model.db.IDBOfferedDayPattern)
	 */
	@Override
	public void deleteOfferedDayPattern(IDBOfferedDayPattern offered)
			throws DatabaseException {
		coursepatternsTable.delete(offered.getID());
	}


	/* (non-Javadoc)
	 * @see scheduler.model.db.IDatabase#assembleOfferedDayPattern()
	 */
	@Override
	public IDBOfferedDayPattern assembleOfferedDayPattern()
			throws DatabaseException {
		//Integer id, Integer courseID, Integer patternID
		return new SQLCoursePattern(null, null, null);
	}


	/* (non-Javadoc)
	 * @see scheduler.model.db.IDatabase#insertOfferedDayPattern(scheduler.model.db.IDBCourse, scheduler.model.db.IDBDayPattern, scheduler.model.db.IDBOfferedDayPattern)
	 */
	@Override
	public void insertOfferedDayPattern(IDBCourse underlying,
			IDBDayPattern dayPattern, IDBOfferedDayPattern pattern)
			throws DatabaseException {
		SQLCoursePattern sqloffereddaypattern = (SQLCoursePattern) pattern;
		//Integer id, Integer courseID, Integer patternID
		sqloffereddaypattern.id = coursepatternsTable.insert(new Object[]{sqloffereddaypattern.courseID, sqloffereddaypattern.patternID});
		
		
	}


	/* (non-Javadoc)
	 * @see scheduler.model.db.IDatabase#isEmpty()
	 */
	@Override
	public boolean isEmpty() throws DatabaseException {
		if(documentTable.selectAll().size() == 0 &&
				workingcopyTable.selectAll().size() == 0 &&
				instructorTable.selectAll().size() == 0 &&
				locationTable.selectAll().size() == 0 &&
				locationequipmentTable.selectAll().size() == 0 &&
				courseTable.selectAll().size() == 0 &&
				courseequipmentTable.selectAll().size() == 0 &&
				coursepatternsTable.selectAll().size() == 0 &&
				scheduleItemTable.selectAll().size() == 0 &&
				labassociationsTable.selectAll().size() == 0 &&
				timeslotprefTable.selectAll().size() == 0 &&
				courseprefTable.selectAll().size() == 0 &&
				patternTable.selectAll().size() == 0 &&
				equipmentTable.selectAll().size() == 0 &&
				userdataTable.selectAll().size() == 0)
		{ return true; }
		System.out.println(" doc table " + documentTable.selectAll().size() + 
				" working copy table " + workingcopyTable.selectAll().size() +
				" inst table " + instructorTable.selectAll().size() +
				" location table " + locationTable.selectAll().size() +
				" loc equip table " + locationequipmentTable.selectAll().size() + 
				" course table " + courseTable.selectAll().size() +
				" courseequip table " + courseequipmentTable.selectAll().size() +
				" coursepatterns table " + coursepatternsTable.selectAll().size() +
				" scheduleitem table " + scheduleItemTable.selectAll().size() +
				" timeslotpref table " + timeslotprefTable.selectAll().size() +
				" coursepref table " + courseprefTable.selectAll().size() +
				" pattern table " + patternTable.selectAll().size() +
				" equipment table " + equipmentTable.selectAll().size() +
				" userdata table " + userdataTable.selectAll().size());
		return false;
	}

	/* (non-Javadoc)
	 * @see scheduler.model.db.IDatabase#assembleScheduleItemCopy(scheduler.model.db.IDBScheduleItem)
	 */
	@Override
	public IDBScheduleItem assembleScheduleItemCopy(IDBScheduleItem underlying)
			throws DatabaseException {
		//TODO
		//return new SQLScheduleItem(null, null, null);
		return null;
	}


	/* (non-Javadoc)
	 * @see scheduler.model.db.IDatabase#getScheduleItemDocument(scheduler.model.db.IDBScheduleItem)
	 */
	@Override
	public IDBDocument getScheduleItemDocument(IDBScheduleItem underlying)
			throws DatabaseException {
		/* IDBSchedule is not part of the SQLdb framework*/
		assert(false);
		return null;
	}


	/* (non-Javadoc)
	 * @see scheduler.model.db.IDatabase#isInserted(scheduler.model.db.IDBScheduleItem)
	 */
	@Override
	public boolean isInserted(IDBScheduleItem underlying)
			throws DatabaseException {
		assert(false);
		return false;
	}


	/* (non-Javadoc)
	 * @see scheduler.model.db.IDatabase#findDocumentForLocation(scheduler.model.db.IDBLocation)
	 */
	@Override
	public IDBObject findDocumentForLocation(IDBLocation underlyingLocation)
			throws DatabaseException {
		SQLDocument result;
		HashMap<String, Object> wheres = new HashMap<String, Object>();
		wheres.put("docID", ((SQLLocation)underlyingLocation).docID);
		result = documentTable.select(wheres).get(0);

		return result;
	}


	/* (non-Javadoc)
	 * @see scheduler.model.db.IDatabase#findDocumentForInstructor(scheduler.model.db.IDBInstructor)
	 */
	@Override
	public IDBObject findDocumentForInstructor(
			IDBInstructor underlyingInstructor) throws DatabaseException {
		SQLDocument result;
		HashMap<String, Object> wheres = new HashMap<String, Object>();
		wheres.put("docID", ((SQLInstructor)underlyingInstructor).documentID);
		result = documentTable.select(wheres).get(0);

		return result;
	}


	/* (non-Javadoc)
	 * @see scheduler.model.db.IDatabase#writeState(java.io.ObjectOutputStream)
	 */
	@Override
	public void writeState(ObjectOutputStream oos) throws IOException {
		/** Not used for SQLDB*/
		assert(false);
	}


	/* (non-Javadoc)
	 * @see scheduler.model.db.IDatabase#readState(java.io.ObjectInputStream)
	 */
	@Override
	public void readState(ObjectInputStream ois) throws IOException {
		/**
		 * Not used for SQLDB
		 */
		assert(false);
		
	}

	/* (non-Javadoc)
	 * @see scheduler.model.db.IDatabase#getDocumentStaffInstructorOrNull(scheduler.model.db.IDBDocument)
	 */
	@Override
	public IDBInstructor getDocumentStaffInstructorOrNull(
			IDBDocument underlyingDocument) throws DatabaseException {
		Integer id = ((SQLDocument)underlyingDocument).staffInstructorID;
		if (id == null)
			return null;
		return findInstructorByID(((SQLDocument)underlyingDocument).staffInstructorID);
	}

	/* (non-Javadoc)
	 * @see scheduler.model.db.IDatabase#getDocumentTBALocationOrNull(scheduler.model.db.IDBDocument)
	 */
	@Override
	public IDBLocation getDocumentTBALocationOrNull(
			IDBDocument underlyingDocument) throws DatabaseException {
		Integer id = ((SQLDocument)underlyingDocument).tbaLocationID;
		if (id == null)
			return null;
		return findLocationByID(id);
	}

	/* (non-Javadoc)
	 * @see scheduler.model.db.IDatabase#setDocumentStaffInstructorOrNull(scheduler.model.db.IDBDocument, scheduler.model.db.IDBInstructor)
	 */
	@Override
	public void setDocumentStaffInstructorOrNull(IDBDocument underlyingDocument,
			IDBInstructor underlyingInstructor) throws DatabaseException {
		if (underlyingInstructor == null)
			((SQLDocument)underlyingDocument).staffInstructorID = null;
		else
			((SQLDocument)underlyingDocument).staffInstructorID = underlyingInstructor.getID();
	}

	/* (non-Javadoc)
	 * @see scheduler.model.db.IDatabase#setDocumentTBALocationOrNull(scheduler.model.db.IDBDocument, scheduler.model.db.IDBLocation)
	 */
	@Override
	public void setDocumentTBALocationOrNull(IDBDocument underlyingDocument,
			IDBLocation underlyingLocation) throws DatabaseException {
		if (underlyingLocation == null)
			((SQLDocument)underlyingDocument).tbaLocationID = null;
		else
			((SQLDocument)underlyingDocument).tbaLocationID = underlyingLocation.getID();
	}

	/* (non-Javadoc)
	 * @see scheduler.model.db.IDatabase#setDocumentChooseForMeInstructorOrNull(scheduler.model.db.IDBDocument, scheduler.model.db.IDBInstructor)
	 */
	@Override
	public void setDocumentChooseForMeInstructorOrNull(IDBDocument underlyingDocument, IDBInstructor underlyingInstructor)
			throws DatabaseException {
		if(underlyingInstructor == null)
			((SQLDocument)underlyingDocument).chooseForMeInstructorID = null;
		else
			((SQLDocument)underlyingDocument).chooseForMeInstructorID = underlyingInstructor.getID();
	}

	/* (non-Javadoc)
	 * @see scheduler.model.db.IDatabase#setDocumentChooseForMeLocationOrNull(scheduler.model.db.IDBDocument, scheduler.model.db.IDBLocation)
	 */
	@Override
	public void setDocumentChooseForMeLocationOrNull(IDBDocument underlyingDocument, IDBLocation underlyingLocation)
			throws DatabaseException {
		if (underlyingLocation == null)
			((SQLDocument)underlyingDocument).chooseForMeLocationID = null;
		else
			((SQLDocument)underlyingDocument).chooseForMeLocationID = underlyingLocation.getID();
	}

	/* (non-Javadoc)
	 * @see scheduler.model.db.IDatabase#getDocumentChooseForMeInstructorOrNull(scheduler.model.db.IDBDocument)
	 */
	@Override
	public IDBInstructor getDocumentChooseForMeInstructorOrNull(
			IDBDocument underlyingDocument) throws DatabaseException {
		Integer id = ((SQLDocument)underlyingDocument).chooseForMeInstructorID;
		if(id == null)
			return null;
		return findInstructorByID(id);
	}

	/* (non-Javadoc)
	 * @see scheduler.model.db.IDatabase#getDocumentChooseForMeLocationOrNull(scheduler.model.db.IDBDocument)
	 */
	@Override
	public IDBLocation getDocumentChooseForMeLocationOrNull(
			IDBDocument underlyingDocument) throws DatabaseException {
		Integer id = ((SQLDocument)underlyingDocument).chooseForMeLocationID;
		if(id == null)
			return null;
		return findLocationByID(id);
	}

	/* (non-Javadoc)
	 * @see scheduler.model.db.IDatabase#findAllLabScheduleItemsForScheduleItem(scheduler.model.db.IDBScheduleItem)
	 */
	@Override
	public Collection<IDBScheduleItem> findAllLabScheduleItemsForScheduleItem(
			IDBScheduleItem underlying) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see scheduler.model.db.IDatabase#associateScheduleItemLab(scheduler.model.db.IDBScheduleItem, scheduler.model.db.IDBScheduleItem)
	 */
	@Override
	public void associateScheduleItemLab(IDBScheduleItem lecture,
			IDBScheduleItem lab) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see scheduler.model.db.IDatabase#disassociateScheduleItemLab(scheduler.model.db.IDBScheduleItem, scheduler.model.db.IDBScheduleItem)
	 */
	@Override
	public void disassociateScheduleItemLab(IDBScheduleItem lecture,
			IDBScheduleItem lab) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see scheduler.model.db.IDatabase#disassociateLectureAndLab(scheduler.model.db.IDBCourse, scheduler.model.db.IDBCourse)
	 */
	@Override
	public void disassociateLectureAndLab(IDBCourse lecture, IDBCourse lab) throws DatabaseException {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see scheduler.model.db.IDatabase#insertEquipmentType(java.lang.String)
	 */
	@Override
	public void insertEquipmentType(String string) throws DatabaseException {
		assert(string != null);
		//TODO: Possibly not completely accurate, might need to set some id to something, yes?
		equipmentTable.insert(new Object[] {string});
	}

	/* (non-Javadoc)
	 * @see scheduler.model.db.IDatabase#getScheduleItemLectureOrNull(scheduler.model.db.IDBScheduleItem)
	 */
	@Override
	public IDBScheduleItem getScheduleItemLectureOrNull(
			IDBScheduleItem underlying) throws DatabaseException {
//		Integer id = ((SQLDocument)underlying).getID();
//		if(id == null)
//			return null;
//		return findInstructorByID(id);
//		
//		
//		
		
		SQLScheduleItem lab = (SQLScheduleItem) underlying;
		
		//if (lab.lectureScheduleItemID == null)
			//return null;
		return null;
	}

	/* (non-Javadoc)
	 * @see scheduler.model.db.IDatabase#findDocumentByName(java.lang.String)
	 */
	@Override
	public IDBDocument findDocumentByName(String scheduleName)
			throws DatabaseException {
		IDBDocument document = null;
		HashMap<String, Object> wheres = new HashMap<String, Object>();
		wheres.put("name", scheduleName);
		
		List<SQLDocument> list = documentTable.select(wheres);
		if (list != null)
			document = list.get(0);
		else
			throw new DatabaseException(new Throwable("Document " + scheduleName + " not found."));
		return document;
	}

	/* (non-Javadoc)
	 * @see scheduler.model.db.IDatabase#closeDatabase()
	 */
	@Override
	public void closeDatabase() {
		try {
			SQLdb.conn.close();
		} catch (SQLException e) {
			// ignore it, but print stack trace
			e.printStackTrace();
		}
	}

}
