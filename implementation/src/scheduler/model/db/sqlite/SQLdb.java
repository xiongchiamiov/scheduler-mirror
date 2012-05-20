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
//TODO: Any input with a negative ID must not happen. Ensure this does not happen.
public class SQLdb implements IDatabase {
	
	static Connection conn = null;
	
	Table<SQLDocument> documentTable = new Table<SQLDocument>(SQLDocument.class, "document",
			new Table.Column[] {
					new Table.Column("id", Integer.class),
					new Table.Column("name", String.class),
					new Table.Column("isTrash", Boolean.class),
					new Table.Column("startHalfHour", Integer.class),
					new Table.Column("endHalfHour", Integer.class)
	});
	Table<SQLWorkingCopy> workingcopyTable = new Table<SQLWorkingCopy>(SQLWorkingCopy.class, "workingcopy",
			new Table.Column[] {
					new Table.Column("id", Integer.class),
					new Table.Column("originalDocID", Integer.class)
	});
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
	Table<SQLLocation> locationTable = new Table<SQLLocation>(SQLLocation.class, "location",
			new Table.Column[] {
					new Table.Column("id", Integer.class),
					new Table.Column("docID", Integer.class),
					new Table.Column("maxOccupancy", String.class),
					new Table.Column("type", String.class),
					new Table.Column("room", String.class),
					new Table.Column("schedulable", Boolean.class)	
	});
	Table<SQLLocationEquipment> locationequipmentTable = new Table<SQLLocationEquipment>(SQLLocationEquipment.class, "locationequipment",
			new Table.Column[] {
					new Table.Column("id", Integer.class),
					new Table.Column("locID", Integer.class),
					new Table.Column("equipID", Integer.class)
	});
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
	Table<SQLUsedEquipment> courseequipmentTable = new Table<SQLUsedEquipment>(SQLUsedEquipment.class, "courseequipment",
			new Table.Column[] {
					new Table.Column("id", Integer.class),
					new Table.Column("courseID", Integer.class),
					new Table.Column("equipID", Integer.class)
	});
	Table<SQLOfferedDayPattern> coursepatternsTable = new Table<SQLOfferedDayPattern>(SQLOfferedDayPattern.class, "coursepatterns",
			new Table.Column[] {
					new Table.Column("id", Integer.class),
					new Table.Column("courseID", Integer.class),
					new Table.Column("patternID", Integer.class)
	});
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
	Table<SQLCourseAssociation> labassociationsTable = new Table<SQLCourseAssociation>(SQLCourseAssociation.class, "labassociations",
			new Table.Column[] {
					new Table.Column("id", Integer.class),
					new Table.Column("lecID", Integer.class), 
					new Table.Column("isTethered", Boolean.class)
	});
	Table<SQLTimePreference> timeslotprefTable = new Table<SQLTimePreference>(SQLTimePreference.class, "timeslotpref",
			new Table.Column[] {
					new Table.Column("id", Integer.class),
					new Table.Column("timeID", Integer.class),
					new Table.Column("instID", Integer.class),
					new Table.Column("prefLevel", Integer.class)
	});
	Table<SQLCoursePreference> courseprefTable = new Table<SQLCoursePreference>(SQLCoursePreference.class, "coursepref",
			new Table.Column[] {
					new Table.Column("id", Integer.class),
					new Table.Column("instID", Integer.class),
					new Table.Column("courseID", Integer.class),
					new Table.Column("prefLevel", Integer.class)
	});
	Table<SQLDayPattern> patternTable = new Table<SQLDayPattern>(SQLDayPattern.class, "pattern",
			new Table.Column[] {
					new Table.Column("id", Integer.class),
					new Table.Column("days", String.class)
	});
	Table<SQLEquipmentType> equipmentTable = new Table<SQLEquipmentType>(SQLEquipmentType.class, "equipment",
			new Table.Column[] {
					new Table.Column("id", Integer.class),  /*it's unique id generated*/
					new Table.Column("desc", String.class)
	});
	Table<SQLUser> userdataTable = new Table<SQLUser>(SQLUser.class, "userdata",
			new Table.Column[] {
					new Table.Column("id", Integer.class),
					new Table.Column("username", String.class),
					new Table.Column("isAdmin", Boolean.class)
	});

	public SQLdb() {
		try {
			openConnection();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void openConnection() throws SQLException, Exception
	{
		Class.forName("org.sqlite.JDBC");
		conn =
			DriverManager.getConnection("jdbc:sqlite:database.db");
//		System.out.println("Connected to database");
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

	static class Table<T extends IDBObject> {
		public enum ColumnType {
			VARCHAR,
			INTEGER,
			TEXT,
			BOOLEAN;
		}
		
		static class Column {
			String name;
 			Class classs;
 			Column(String name, Class classs) {
 				this.name = name;
 				this.classs = classs;
 			}
		}
		
		String name;
		Column[] columns;
		Class classs;
		
		public Table(Class classs, String name, Column[] columns) {
			  this.classs = classs;
			  this.name = name;
			  this.columns = columns;
		}
		
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

	@Override
	public IDBUser findUserByUsername(String username) throws DatabaseException {
		List<SQLUser> result;
		HashMap<String, Object> wheres = new HashMap<String, Object>();
		wheres.put("username", username);
		
		result = userdataTable.select(wheres);
		
		if (result.size() == 0)
			throw new DatabaseException("No user found in SQLdb.findUserByUsername");
		
		return result.get(0);
	}

	@Override
	public IDBUser assembleUser(String username, boolean isAdmin) {
		return new SQLUser(null, username, isAdmin);
	}

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


	@Override
	public void updateUser(IDBUser user) throws DatabaseException {
		userdataTable.update(new Object[] {user.getUsername(), user.isAdmin()}, user.getID());
	}

	@Override
	public void deleteUser(IDBUser user) throws DatabaseException {
		userdataTable.delete(user.getID());
	}

	@Override
	public Collection<IDBDocument> findAllDocuments() throws DatabaseException {
		ArrayList<IDBDocument> result = new ArrayList<IDBDocument>();

		for(SQLDocument docItem : documentTable.selectAll())
			result.add(docItem);
		
		if (result.size() == 0) 
			throw new DatabaseException("No documents found in SQLdb.findAllDocuments");
		
		return result;
	}

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

	@Override
	public void insertDocument(IDBDocument document) throws DatabaseException {
		SQLDocument doc = (SQLDocument) document;
		doc.id = documentTable.insert(new Object[]{ doc.getName(), doc.isTrashed(), doc.getStartHalfHour(), doc.getEndHalfHour() });
	}

	@Override
	public IDBDocument assembleDocument(String name, int startHalfHour,
			int endHalfHour) throws DatabaseException {
		return new SQLDocument(null, name, false, startHalfHour, endHalfHour, null, null, null, null);
	}

	@Override
	public void updateDocument(IDBDocument document) throws DatabaseException {		
		documentTable.update(new Object[] {document.getName(), document.isTrashed(), document.getStartHalfHour(), 
				document.getEndHalfHour()}, document.getID());
	}

	@Override
	public void deleteDocument(IDBDocument document) throws DatabaseException {
		assert(document != null);
		assert(document.getID() != null);
		documentTable.delete(document.getID());
	}

	@Override
	public boolean isOriginalDocument(IDBDocument doc) throws DatabaseException {
		List<SQLWorkingCopy> result;
		HashMap<String, Object> wheres = new HashMap<String, Object>();
		wheres.put("id", doc.getID());
		result = workingcopyTable.select(wheres);
		return (result.size() == 0);
	}

	@Override
	public boolean documentIsWorkingCopy(IDBDocument document)
			throws DatabaseException {
		List<SQLWorkingCopy> result;
		HashMap<String, Object> wheres = new HashMap<String, Object>();
		wheres.put("id", document.getID());
		result = workingcopyTable.select(wheres);
		return (result.size() != 0);
	}

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

	@Override
	public void associateWorkingCopyWithOriginal(
			IDBDocument underlyingDocument, IDBDocument underlyingDocument2)
			throws DatabaseException {
		workingcopyTable.insertWithID(new Object[] {underlyingDocument.getID(), underlyingDocument2.getID()});
	}

	@Override
	public void disassociateWorkingCopyWithOriginal(
			IDBDocument underlyingDocument, IDBDocument underlyingDocument2)
			throws DatabaseException {
		workingcopyTable.delete(underlyingDocument.getID());
	}

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


	@Override
	public IDBScheduleItem assembleScheduleItem(int section, Set<Day> days,
			int startHalfHour, int endHalfHour, boolean isPlaced,
			boolean isConflicted) throws DatabaseException {
		return new SQLScheduleItem(null, null, null, null, null, startHalfHour, endHalfHour, null,
				section, isConflicted, isPlaced, days);
	}

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


	@Override
	public void updateScheduleItem(IDBScheduleItem schedule)
			throws DatabaseException {
		SQLScheduleItem schedItem = (SQLScheduleItem) schedule;
		scheduleItemTable.update(new Object[] {schedItem.getDocID(), schedItem.getInstID(),
				schedItem.getLocID(), schedItem.getCourseID(), schedItem.getStartHalfHour(),
				schedItem.getEndHalfHour(), schedItem.getDayPattern(), schedItem.getSection(),
				schedItem.isPlaced(), schedItem.isConflicted()}, schedItem.getID());
	}


	@Override
	public void deleteScheduleItem(IDBScheduleItem schedule)
			throws DatabaseException {
		scheduleItemTable.delete(schedule.getID());
	}


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

	@Override
	public IDBLocation findLocationByID(int id) throws DatabaseException {
		HashMap<String, Object> wheres = new HashMap<String, Object>();
		wheres.put("id", id);
		if(locationTable.select(wheres).isEmpty())
			throw new NotFoundException("Location not found with ID: " + id);
		return locationTable.select(wheres).get(0);
	}

	@Override
	public IDBLocation assembleLocation(String room, String type,
			String maxOccupancy, boolean isSchedulable)
			throws DatabaseException {
		return new SQLLocation(null, null, maxOccupancy, type, 
				room, new Boolean(isSchedulable));
	}


	@Override
	public void insertLocation(IDBDocument containingDocument,
			IDBLocation location) throws DatabaseException {
		SQLLocation sqlLocation = (SQLLocation) location;
		assert(sqlLocation.id == null);
		//sqlLocation.docID = containingDocument.getID();
		sqlLocation.id = locationTable.insert(new Object[]{ containingDocument.getID(), sqlLocation.maxOccupancy,
				sqlLocation.type, sqlLocation.room, sqlLocation.schedulable});
	}


	@Override
	public void updateLocation(IDBLocation location) throws DatabaseException {
		assert(location!=null) : "null location";
		assert(location.getID() != null) : "null location id";
		assert(location.getMaxOccupancy() != null);
		assert(location.getType() != null);
		assert(location.getRoom() != null);
		
		SQLLocation sqlLocation = (SQLLocation)location;
		locationTable.update(new Object[] {sqlLocation.docID, sqlLocation.maxOccupancy, sqlLocation.type, sqlLocation.room, 
				sqlLocation.schedulable}, sqlLocation.getID());
	}


	@Override
	public void deleteLocation(IDBLocation location) throws DatabaseException {
		locationTable.delete(location.getID());
	}


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


	@Override
	public IDBCourse findCourseByID(int id) throws DatabaseException {
		HashMap<String, Object> wheres = new HashMap<String, Object>();
		wheres.put("id", id);
		if(courseTable.select(wheres).isEmpty())
			throw new NotFoundException("Course with ID " + id + " not found");
		return courseTable.select(wheres).get(0);
	}


	@Override
	public IDBCourse assembleCourse(String name, String catalogNumber,
			String department, String wtu, String scu, String numSections,
			String type, String maxEnrollment, String numHalfHoursPerWeek,
			boolean isSchedulable) throws DatabaseException {
		return new SQLCourse(null, null, maxEnrollment, wtu, scu, type, numSections, department, catalogNumber,
				name, new Boolean(isSchedulable), numHalfHoursPerWeek);
	}


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


	@Override
	public void updateCourse(IDBCourse course) throws DatabaseException {
		assert(course!=null);
		assert(course.getID() != null);
		SQLCourse sqlCourse = (SQLCourse)course;
		courseTable.update(new Object[] {sqlCourse.getID(), sqlCourse.documentID, sqlCourse.maxEnrollment, sqlCourse.wtu, sqlCourse.scu, sqlCourse.type, sqlCourse.numSections,
				sqlCourse.department, sqlCourse.catalogNumber, sqlCourse.name, sqlCourse.isSchedulable, sqlCourse.numHalfHoursPerWeek}, sqlCourse.getID());
	}


	@Override
	public void deleteCourse(IDBCourse course) throws DatabaseException {
		courseTable.delete(course.getID());
	}


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

	@Override
	public IDBInstructor findInstructorByID(int id) throws DatabaseException {
		HashMap<String, Object> instID = new HashMap<String, Object>();
		instID.put("id", id);
		if(instructorTable.select(instID).isEmpty())
			throw new NotFoundException("Instructor with ID " + id + " was not found");
		return instructorTable.select(instID).get(0);
	}

	@Override
	public IDBInstructor assembleInstructor(String firstName, String lastName,
			String username, String maxWTU, boolean isSchedulable)
			throws DatabaseException {
		return new SQLInstructor(null, null, firstName, lastName, username, maxWTU, isSchedulable);
	}

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

	@Override
	public void deleteInstructor(IDBInstructor instructor) throws DatabaseException {
		assert(instructor != null) : "Specified instructor null";
		if(instructor == null || instructor.getID() == null)
			throw new DatabaseException("Instructor not found");
		instructorTable.delete(instructor.getID());	
	}

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
	public IDBTime findTimeByID(int id) {
		return new SQLTime(id);
	}
	
	@Override
	public IDBTimePreference findTimePreferenceByID(int id)
			throws DatabaseException {
		if(id < 0)
			throw new DatabaseException("Invalid Time Preference ID");
		HashMap<String, Object> timeID = new HashMap<String, Object>();
		timeID.put("id", id);
		return timeslotprefTable.select(timeID).get(0);
	}

	@Override
	public IDBTimePreference assembleTimePreference(int preflevel)
			throws DatabaseException {
		return new SQLTimePreference(null, null, null, preflevel);
	}

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


	@Override
	public void updateTimePreference(IDBTimePreference timePreference)
			throws DatabaseException {
		SQLTimePreference pref = (SQLTimePreference) timePreference;
		//SQLTime time = (SQLTime)findTimeByID(pref.timeID);
		timeslotprefTable.update(new Object[]{ pref.timeID, pref.instructorID, pref.getPreference()}, pref.getID());
		
	}


	@Override
	public void deleteTimePreference(IDBTimePreference timePreference)
			throws DatabaseException {
		timeslotprefTable.delete(timePreference.getID());	
	}


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


	@Override
	public IDBCoursePreference findCoursePreferenceByID(int id)
			throws DatabaseException {
		IDBCoursePreference result;
		HashMap<String, Object> wheres = new HashMap<String, Object>();
		wheres.put("id", id);
		result = courseprefTable.select(wheres).get(0);
		return result;
	}


	@Override
	public IDBCoursePreference assembleCoursePreference(int preference)
			throws DatabaseException {
		//Integer id, Integer instructorID, Integer courseID, int preference
		return new SQLCoursePreference(null, null, null, preference);
	}

	@Override
	public void insertCoursePreference(IDBInstructor instructor,
			IDBCourse course, IDBCoursePreference coursePreference)
			throws DatabaseException {
		SQLCoursePreference sqlcoursepreference = (SQLCoursePreference) coursePreference;
		//(Integer id, Integer instructorID, Integer courseID, int preference)
		sqlcoursepreference.id = courseprefTable.insert(new Object[]{ instructor.getID(), course.getID(), sqlcoursepreference.getPreference()});
		
	}


	@Override
	public void updateCoursePreference(IDBCoursePreference coursePreference)
			throws DatabaseException {
		SQLCoursePreference sqlcoursepreference = (SQLCoursePreference) coursePreference;
		//(Integer id, Integer instructorID, Integer courseID, int preference)
		courseprefTable.update(new Object[]{ sqlcoursepreference.instructorID, sqlcoursepreference.courseID, sqlcoursepreference.getPreference()}, sqlcoursepreference.getID());
	}


	@Override
	public void deleteCoursePreference(IDBCoursePreference coursePreference)
			throws DatabaseException {
		courseprefTable.delete(coursePreference.getID());
	}


	@Override
	public IDBTime findTimeByDayAndHalfHour(int day, int halfHour)
			throws DatabaseException {
		return new SQLTime(day, halfHour);
	}

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


	@Override
	public Collection<IDBEquipmentType> findAllEquipmentTypes()
			throws DatabaseException {
		// TODO Auto-generated method stub
		return null;
	}


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


	@Override
	public void deleteUsedEquipment(IDBUsedEquipment usedEquipment)
			throws DatabaseException {
		courseequipmentTable.delete(usedEquipment.getID());
		
	}


	@Override
	public IDBUsedEquipment assembleUsedEquipment() throws DatabaseException {
		//(Integer id, Integer courseID, Integer equipmentTypeID)
		return new SQLUsedEquipment(null, null, null);
	}


	@Override
	public void insertUsedEquipment(IDBCourse course,
			IDBEquipmentType equipmentType, IDBUsedEquipment equip)
			throws DatabaseException {
		SQLUsedEquipment sqlusedequip = (SQLUsedEquipment) equip;
		//(Integer id, Integer courseID, Integer equipmentTypeID)
		sqlusedequip.id = courseequipmentTable.insert(new Object[]{ sqlusedequip.courseID, sqlusedequip.equipmentTypeID});
		
		
	}


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


	@Override
	public void deleteProvidedEquipment(IDBProvidedEquipment providedEquipment)
			throws DatabaseException {
		assert(providedEquipment != null);
		assert(providedEquipment.getID() != null);
		locationequipmentTable.delete(providedEquipment.getID());
		
	}


	@Override
	public IDBProvidedEquipment assembleProvidedEquipment()
			throws DatabaseException {
		//Integer id, Integer locationID, Integer equipmentTypeID
		return new SQLLocationEquipment(null, null, null);
	}



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


	@Override
	public IDBDayPattern findDayPatternByDays(Set<Integer> dayPattern)
			throws DatabaseException {
		return new SQLDayPattern(dayPattern);
	}


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


	@Override
	public IDBDayPattern getDayPatternForOfferedDayPattern(
			IDBOfferedDayPattern offered) throws DatabaseException {
		return new SQLDayPattern(((SQLOfferedDayPattern)offered).dayPatternID);
	}


	@Override
	public void deleteOfferedDayPattern(IDBOfferedDayPattern offered)
			throws DatabaseException {
		coursepatternsTable.delete(offered.getID());
	}


	@Override
	public IDBOfferedDayPattern assembleOfferedDayPattern()
			throws DatabaseException {
		//Integer id, Integer courseID, Integer patternID
		return new SQLCoursePattern(null, null, null);
	}


	@Override
	public void insertOfferedDayPattern(IDBCourse underlying,
			IDBDayPattern dayPattern, IDBOfferedDayPattern pattern)
			throws DatabaseException {
		SQLCoursePattern sqloffereddaypattern = (SQLCoursePattern) pattern;
		//Integer id, Integer courseID, Integer patternID
		sqloffereddaypattern.id = coursepatternsTable.insert(new Object[]{sqloffereddaypattern.courseID, sqloffereddaypattern.patternID});
		
		
	}


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

	@Override
	public IDBScheduleItem assembleScheduleItemCopy(IDBScheduleItem underlying)
			throws DatabaseException {
		//TODO
		//return new SQLScheduleItem(null, null, null);
		return null;
	}


	@Override
	public IDBDocument getScheduleItemDocument(IDBScheduleItem underlying)
			throws DatabaseException {
		/* IDBSchedule is not part of the SQLdb framework*/
		assert(false);
		return null;
	}


	@Override
	public boolean isInserted(IDBScheduleItem underlying)
			throws DatabaseException {
		assert(false);
		return false;
	}


	@Override
	public IDBObject findDocumentForLocation(IDBLocation underlyingLocation)
			throws DatabaseException {
		SQLDocument result;
		HashMap<String, Object> wheres = new HashMap<String, Object>();
		wheres.put("docID", ((SQLLocation)underlyingLocation).docID);
		result = documentTable.select(wheres).get(0);

		return result;
	}


	@Override
	public IDBObject findDocumentForInstructor(
			IDBInstructor underlyingInstructor) throws DatabaseException {
		SQLDocument result;
		HashMap<String, Object> wheres = new HashMap<String, Object>();
		wheres.put("docID", ((SQLInstructor)underlyingInstructor).documentID);
		result = documentTable.select(wheres).get(0);

		return result;
	}


	@Override
	public void writeState(ObjectOutputStream oos) throws IOException {
		/** Not used for SQLDB*/
		assert(false);
	}


	@Override
	public void readState(ObjectInputStream ois) throws IOException {
		/**
		 * Not used for SQLDB
		 */
		assert(false);
		
	}

	@Override
	public IDBInstructor getDocumentStaffInstructorOrNull(
			IDBDocument underlyingDocument) throws DatabaseException {
		Integer id = ((SQLDocument)underlyingDocument).staffInstructorID;
		if (id == null)
			return null;
		return findInstructorByID(((SQLDocument)underlyingDocument).staffInstructorID);
	}

	@Override
	public IDBLocation getDocumentTBALocationOrNull(
			IDBDocument underlyingDocument) throws DatabaseException {
		Integer id = ((SQLDocument)underlyingDocument).tbaLocationID;
		if (id == null)
			return null;
		return findLocationByID(id);
	}

	@Override
	public void setDocumentStaffInstructorOrNull(IDBDocument underlyingDocument,
			IDBInstructor underlyingInstructor) throws DatabaseException {
		if (underlyingInstructor == null)
			((SQLDocument)underlyingDocument).staffInstructorID = null;
		else
			((SQLDocument)underlyingDocument).staffInstructorID = underlyingInstructor.getID();
	}

	@Override
	public void setDocumentTBALocationOrNull(IDBDocument underlyingDocument,
			IDBLocation underlyingLocation) throws DatabaseException {
		if (underlyingLocation == null)
			((SQLDocument)underlyingDocument).tbaLocationID = null;
		else
			((SQLDocument)underlyingDocument).tbaLocationID = underlyingLocation.getID();
	}

	@Override
	public void setDocumentChooseForMeInstructorOrNull(IDBDocument underlyingDocument, IDBInstructor underlyingInstructor)
			throws DatabaseException {
		if(underlyingInstructor == null)
			((SQLDocument)underlyingDocument).chooseForMeInstructorID = null;
		else
			((SQLDocument)underlyingDocument).chooseForMeInstructorID = underlyingInstructor.getID();
	}

	@Override
	public void setDocumentChooseForMeLocationOrNull(IDBDocument underlyingDocument, IDBLocation underlyingLocation)
			throws DatabaseException {
		if (underlyingLocation == null)
			((SQLDocument)underlyingDocument).chooseForMeLocationID = null;
		else
			((SQLDocument)underlyingDocument).chooseForMeLocationID = underlyingLocation.getID();
	}

	@Override
	public IDBInstructor getDocumentChooseForMeInstructorOrNull(
			IDBDocument underlyingDocument) throws DatabaseException {
		Integer id = ((SQLDocument)underlyingDocument).chooseForMeInstructorID;
		if(id == null)
			return null;
		return findInstructorByID(id);
	}

	@Override
	public IDBLocation getDocumentChooseForMeLocationOrNull(
			IDBDocument underlyingDocument) throws DatabaseException {
		Integer id = ((SQLDocument)underlyingDocument).chooseForMeLocationID;
		if(id == null)
			return null;
		return findLocationByID(id);
	}

	@Override
	public Collection<IDBScheduleItem> findAllLabScheduleItemsForScheduleItem(
			IDBScheduleItem underlying) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void associateScheduleItemLab(IDBScheduleItem lecture,
			IDBScheduleItem lab) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void disassociateScheduleItemLab(IDBScheduleItem lecture,
			IDBScheduleItem lab) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void disassociateLectureAndLab(IDBCourse lecture, IDBCourse lab) throws DatabaseException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void insertEquipmentType(String string) throws DatabaseException {
		assert(string != null);
		//TODO: Possibly not completely accurate, might need to set some id to something, yes?
		equipmentTable.insert(new Object[] {string});
	}

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

}
