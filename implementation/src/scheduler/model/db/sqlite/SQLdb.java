package scheduler.model.db.sqlite;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
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
import scheduler.model.db.IDBSchedule;
import scheduler.model.db.IDBScheduleItem;
import scheduler.model.db.IDBTime;
import scheduler.model.db.IDBTimePreference;
import scheduler.model.db.IDBUsedEquipment;
import scheduler.model.db.IDBUser;
import scheduler.model.db.IDatabase;
import scheduler.model.db.simple.DBUsedEquipment;

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
	Table<SQLDocument> workingcopyTable = new Table<SQLDocument>(SQLDocument.class, "workingcopy",
			new Table.Column[] {
					new Table.Column("id", Integer.class),
					new Table.Column("originalDocID", Integer.class)
			});
	Table<SQLUser> userdataTable = new Table<SQLUser>(SQLUser.class, "userdata",
			new Table.Column[] {
					new Table.Column("id", Integer.class),
					new Table.Column("username", String.class),
					new Table.Column("isAdmin", Boolean.class)
			});
	Table<SQLLocation> locationTable = new Table<SQLLocation>(SQLLocation.class, "location",
			new Table.Column[] {
					new Table.Column("id", Integer.class),
					new Table.Column("docID", Integer.class),
					new Table.Column("maxOccupancy", Integer.class),
					new Table.Column("type", String.class),
					new Table.Column("room", String.class),
					new Table.Column("schedulable", Boolean.class)	
			});
	Table<SQLInstructor> instructorTable = new Table<SQLInstructor>(SQLInstructor.class, "instructor",
			new Table.Column[] {
					new Table.Column("id", Integer.class),
					new Table.Column("docID", Integer.class),
					new Table.Column("firstName", String.class),
					new Table.Column("lastName", String.class),
					new Table.Column("username", String.class),
					new Table.Column("maxWTU", Integer.class),
					new Table.Column("schedulable", Boolean.class)
	});
	Table<SQLCourse> courseTable = new Table<SQLCourse>(SQLCourse.class, "course",
			new Table.Column[] {
					new Table.Column("id", Integer.class),
					new Table.Column("docID", Integer.class),
					new Table.Column("enrollment", Integer.class),
					new Table.Column("wtu", Integer.class),
					new Table.Column("scu", Integer.class),
					new Table.Column("type", String.class),
					new Table.Column("numSections", Integer.class),
					new Table.Column("dept", String.class),
					new Table.Column("catalogNum", String.class),
					new Table.Column("name", String.class),
					new Table.Column("schedulable", Boolean.class),
					new Table.Column("numHalfHours", Integer.class)
	});
	Table<SQLScheduleItem> scheduleItemTable = new Table<SQLScheduleItem>(SQLScheduleItem.class, "scheduleitem",
			new Table.Column[] {
					new Table.Column("id", Integer.class),
					new Table.Column("docID", Integer.class),
					new Table.Column("instID", Integer.class),
					new Table.Column("locID", Integer.class),
					new Table.Column("courseID", Integer.class),
					new Table.Column("startTime", Integer.class),
					new Table.Column("endTime", String.class),
					new Table.Column("dayPatternID", String.class),
					new Table.Column("sectionNum", String.class)
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
			assert(values.length == columns.length);
			
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
			assert(values.length == columns.length-1);
			PreparedStatement stmnt = null;
  
			String queryString = "INSERT INTO " + name + " (";
  
			for (Column column : columns)
				if (!column.name.equals("id"))
					queryString += column.name + ",";
  
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
						constructorArguments[i] = getFromResultWithType(resultSet, i, columns[i].classs);
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
		IDBUser result;
		HashMap<String, Object> wheres = new HashMap<String, Object>();
		wheres.put("username", username);
		result = userdataTable.select(wheres).get(0);
		return result;
	}

	@Override
	public IDBUser assembleUser(String username, boolean isAdmin) {
		return new SQLUser(null, username, isAdmin);
	}


	@Override
	public void insertUser(IDBUser user) throws DatabaseException {
		SQLUser sqluser = (SQLUser) user;
		
		sqluser.id = userdataTable.insert(new Object[]{ user.getUsername(), user.isAdmin()});
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
		documentTable.selectAll();
		for(SQLDocument docItem : documentTable.selectAll())
			result.add(docItem);
		
		return result;
	}


	@Override
	public IDBDocument findDocumentByID(int id) throws DatabaseException {		
		IDBDocument result;
		HashMap<String, Object> wheres = new HashMap<String, Object>();
		wheres.put("id", id);
		result = documentTable.select(wheres).get(0);
		return result;
	}


	@Override
	public void insertDocument(IDBDocument document) throws DatabaseException {
		SQLDocument doc = (SQLDocument) document;
		doc.id = documentTable.insert(new Object[]{ doc.getName(), doc.isTrashed(), doc.getStartHalfHour(), doc.getEndHalfHour() });
	}


	@Override
	public IDBDocument assembleDocument(String name, int startHalfHour,
			int endHalfHour) throws DatabaseException {
		return new SQLDocument(null, name, startHalfHour, endHalfHour);
	}


	@Override
	public void updateDocument(IDBDocument document) throws DatabaseException {		
		documentTable.update(new Object[] {document.getName(), document.isTrashed(), document.getStartHalfHour(), 
				document.getEndHalfHour()}, document.getID());
	}


	@Override
	public void deleteDocument(IDBDocument document) throws DatabaseException {
		documentTable.delete(document.getID());
	}


	@Override
	public IDBDocument findDocumentForSchedule(IDBSchedule schedule)
			throws DatabaseException {		
		SQLDocument ret = (SQLDocument) schedule;	
		return ret;
	}


	@Override
	public boolean isOriginalDocument(IDBDocument doc) throws DatabaseException {
//		IDBDocument result;
//		HashMap<String, Object> wheres = new HashMap<String, Object>();
//		wheres.put("id", doc.getID());
//		result = documentTable.select(wheres).get(0);
//		return result;
		return !(((SQLDocument)doc).isWorkingCopy());
	}


	@Override
	public boolean documentIsWorkingCopy(IDBDocument document)
			throws DatabaseException {
		return ((SQLDocument)document).isWorkingCopy();
	}


	@Override
	public IDBDocument getOriginalForWorkingCopyDocumentOrNull(IDBDocument rawDocument)
			throws DatabaseException {
		//return ((SQLDocument)rawDocument).getWorkingCopyID();
		return null;
	}


	@Override
	public IDBDocument getWorkingCopyForOriginalDocumentOrNull(
			IDBDocument document) throws DatabaseException {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public void associateWorkingCopyWithOriginal(
			IDBDocument underlyingDocument, IDBDocument underlyingDocument2)
			throws DatabaseException {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void disassociateWorkingCopyWithOriginal(
			IDBDocument underlyingDocument, IDBDocument underlyingDocument2)
			throws DatabaseException {
		// TODO Auto-generated method stub
		
	}


	@Override
	public Collection<IDBSchedule> findAllSchedulesForDocument(
			IDBDocument document) throws DatabaseException {
		ArrayList<IDBSchedule> ret = new ArrayList<IDBSchedule>();
		ret.add((IDBSchedule)document);
		
		return ret;
	}


	//We can't have this method, ScheduleID isn't enough to identify a Schedule
	@Override
	public IDBSchedule findScheduleByID(int id) throws DatabaseException {
		return null;
	}


	@Override
	public IDBSchedule assembleSchedule() throws DatabaseException {
		return new SQLDocument(null, null, null, null);
	}


	@Override
	public void insertSchedule(IDBDocument containingDocument,
			IDBSchedule schedule) throws DatabaseException {
		((SQLScheduleItem)schedule).docID = containingDocument.getID();

		
	}

	/**
	 * What is this even supposed to do?
	 */
	@Override
	public void updateSchedule(IDBSchedule schedule) throws DatabaseException {
		
	}


	@Override
	public void deleteSchedule(IDBSchedule schedule) throws DatabaseException {
		// TODO Auto-generated method stub
		
	}


	@Override
	public Collection<IDBScheduleItem> findScheduleItemsBySchedule(
			IDBSchedule schedule) throws DatabaseException {
		Collection<IDBScheduleItem> result = new LinkedList<IDBScheduleItem>();
		SQLDocument sched = (SQLDocument) schedule;
		HashMap<String, Object> wheres = new HashMap<String, Object>();
		
		wheres.put("id", sched.getID());
		
		for(SQLScheduleItem schedItem : scheduleItemTable.select(wheres))
			result.add(schedItem);
		
		return result;
	}


	@Override
	public Collection<IDBScheduleItem> findAllScheduleItemsForSchedule(
			IDBSchedule schedule) throws DatabaseException {
		Collection<IDBScheduleItem> result = new LinkedList<IDBScheduleItem>();
		SQLDocument sched = (SQLDocument) schedule;
		HashMap<String, Object> wheres = new HashMap<String, Object>();
		
		wheres.put("id", sched.getID());
		
		for(SQLScheduleItem schedItem : scheduleItemTable.select(wheres))
			result.add(schedItem);
		
		return result;
	}


	@Override
	public IDBScheduleItem findScheduleItemByID(int id)
			throws DatabaseException {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public IDBScheduleItem assembleScheduleItem(int section, Set<Day> days,
			int startHalfHour, int endHalfHour, boolean isPlaced,
			boolean isConflicted) throws DatabaseException {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public void insertScheduleItem(IDBSchedule schedule, IDBCourse course,
			IDBInstructor instructor, IDBLocation location, IDBScheduleItem item)
			throws DatabaseException {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void updateScheduleItem(IDBScheduleItem schedule)
			throws DatabaseException {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void deleteScheduleItem(IDBScheduleItem schedule)
			throws DatabaseException {
		// TODO Auto-generated method stub
		
	}


	@Override
	public IDBLocation getScheduleItemLocation(IDBScheduleItem item)
			throws DatabaseException {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public IDBCourse getScheduleItemCourse(IDBScheduleItem item)
			throws DatabaseException {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public IDBInstructor getScheduleItemInstructor(IDBScheduleItem item)
			throws DatabaseException {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public void setScheduleItemCourse(IDBScheduleItem underlying,
			IDBCourse findCourseByID) throws DatabaseException {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void setScheduleItemLocation(IDBScheduleItem underlying,
			IDBLocation findLocationByID) throws DatabaseException {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void setScheduleItemInstructor(IDBScheduleItem underlying,
			IDBInstructor findInstructorByID) throws DatabaseException {
		// TODO Auto-generated method stub
		
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
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public IDBLocation assembleLocation(String room, String type,
			String maxOccupancy, boolean isSchedulable)
			throws DatabaseException {
		return new SQLLocation(null, null, Integer.valueOf(maxOccupancy), type, 
				room, isSchedulable);
	}


	@Override
	public void insertLocation(IDBDocument containingDocument,
			IDBLocation location) throws DatabaseException {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void updateLocation(IDBLocation location) throws DatabaseException {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void deleteLocation(IDBLocation location) throws DatabaseException {
		// TODO Auto-generated method stub
		
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
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public IDBCourse assembleCourse(String name, String catalogNumber,
			String department, String wtu, String scu, String numSections,
			String type, String maxEnrollment, String numHalfHoursPerWeek,
			boolean isSchedulable) throws DatabaseException {
		return new SQLCourse(null, null, Integer.valueOf(maxEnrollment), 
				Integer.valueOf(wtu), Integer.valueOf(scu), 
				Integer.valueOf(numSections), Integer.valueOf(numHalfHoursPerWeek), 
				type, department, name, catalogNumber, isSchedulable);
	}


	@Override
	public void insertCourse(IDBDocument underlyingDocument, IDBCourse course)
			throws DatabaseException {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void updateCourse(IDBCourse course) throws DatabaseException {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void deleteCourse(IDBCourse course) throws DatabaseException {
		// TODO Auto-generated method stub
		
	}


	@Override
	public IDBDocument findDocumentForCourse(IDBCourse underlyingCourse)
			throws DatabaseException {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public IDBCourseAssociation getAssociationForLabOrNull(IDBCourse underlying)
			throws DatabaseException {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Collection<IDBCourseAssociation> getAssociationsForLecture(
			IDBCourse lectureCourse) throws DatabaseException {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public IDBCourse getAssociationLecture(IDBCourseAssociation association)
			throws DatabaseException {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public IDBCourse getAssociationLab(IDBCourseAssociation association)
			throws DatabaseException {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public void associateLectureAndLab(IDBCourse lecture, IDBCourse lab)
			throws DatabaseException {
		// TODO Auto-generated method stub
		
	}


	@Override
	public Collection<IDBInstructor> findInstructorsForDocument(
			IDBDocument document) throws DatabaseException {
		Collection<IDBInstructor> result = new LinkedList<IDBInstructor>();
		SQLDocument doc = (SQLDocument) document;
		HashMap<String, Object> wheres = new HashMap<String, Object>();
		
		wheres.put("docID", doc.getID());
		
		for(SQLInstructor ins : instructorTable.select(wheres))
			result.add(ins);
		
		return result;
	}


	@Override
	public IDBInstructor findInstructorByID(int id) throws DatabaseException {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public IDBInstructor assembleInstructor(String firstName, String lastName,
			String username, String maxWTU, boolean isSchedulable)
			throws DatabaseException {
		return new SQLInstructor(null, null, Integer.valueOf(maxWTU), firstName, lastName,
				username, isSchedulable);
	}


	@Override
	public void insertInstructor(IDBDocument containingDocument,
			IDBInstructor instructor) throws DatabaseException {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void updateInstructor(IDBInstructor instructor)
			throws DatabaseException {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void deleteInstructor(IDBInstructor instructor)
			throws DatabaseException {
		// TODO Auto-generated method stub
		
	}


	@Override
	public Map<IDBTime, IDBTimePreference> findTimePreferencesByTimeForInstructor(
			IDBInstructor instructor) throws DatabaseException {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public IDBTimePreference findTimePreferenceByID(int id)
			throws DatabaseException {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public IDBTimePreference assembleTimePreference(int preference)
			throws DatabaseException {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public void insertTimePreference(IDBInstructor ins, IDBTime time,
			IDBTimePreference timePreference) throws DatabaseException {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void updateTimePreference(IDBTimePreference timePreference)
			throws DatabaseException {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void deleteTimePreference(IDBTimePreference timePreference)
			throws DatabaseException {
		// TODO Auto-generated method stub
		
	}


	@Override
	public Map<IDBCourse, IDBCoursePreference> findCoursePreferencesByCourseForInstructor(
			IDBInstructor instructor) throws DatabaseException {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public IDBCoursePreference findCoursePreferenceByID(int id)
			throws DatabaseException {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public IDBCoursePreference assembleCoursePreference(int preference)
			throws DatabaseException {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public void insertCoursePreference(IDBInstructor instructor,
			IDBCourse course, IDBCoursePreference coursePreference)
			throws DatabaseException {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void updateCoursePreference(IDBCoursePreference coursePreference)
			throws DatabaseException {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void deleteCoursePreference(IDBCoursePreference coursePreference)
			throws DatabaseException {
		// TODO Auto-generated method stub
		
	}


	@Override
	public IDBTime findTimeByDayAndHalfHour(int day, int halfHour)
			throws DatabaseException {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public IDBEquipmentType findEquipmentTypeByDescription(
			String equipmentTypeDescription) throws DatabaseException {
		// TODO Auto-generated method stub
		return null;
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
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public void deleteUsedEquipment(IDBUsedEquipment usedEquipment)
			throws DatabaseException {
		// TODO Auto-generated method stub
		
	}


	@Override
	public DBUsedEquipment assembleUsedEquipment() throws DatabaseException {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public void insertUsedEquipment(IDBCourse course,
			IDBEquipmentType equipmentType, IDBUsedEquipment equip)
			throws DatabaseException {
		// TODO Auto-generated method stub
		
	}


	@Override
	public Map<IDBEquipmentType, IDBProvidedEquipment> findProvidedEquipmentByEquipmentForLocation(
			IDBLocation location) throws DatabaseException {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public void deleteProvidedEquipment(IDBProvidedEquipment providedEquipment)
			throws DatabaseException {
		// TODO Auto-generated method stub
		
	}


	@Override
	public IDBProvidedEquipment assembleProvidedEquipment()
			throws DatabaseException {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public void insertProvidedEquipment(IDBLocation location,
			IDBEquipmentType equipmentType, IDBProvidedEquipment equip)
			throws DatabaseException {
		// TODO Auto-generated method stub
		
	}


	@Override
	public IDBDayPattern findDayPatternByDays(Set<Integer> dayPattern)
			throws DatabaseException {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Collection<IDBOfferedDayPattern> findOfferedDayPatternsForCourse(
			IDBCourse underlying) throws DatabaseException {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public IDBDayPattern getDayPatternForOfferedDayPattern(
			IDBOfferedDayPattern offered) throws DatabaseException {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public void deleteOfferedDayPattern(IDBOfferedDayPattern offered)
			throws DatabaseException {
		// TODO Auto-generated method stub
		
	}


	@Override
	public IDBOfferedDayPattern assembleOfferedDayPattern()
			throws DatabaseException {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public void insertOfferedDayPattern(IDBCourse underlying,
			IDBDayPattern dayPattern, IDBOfferedDayPattern pattern)
			throws DatabaseException {
		// TODO Auto-generated method stub
		
	}


	@Override
	public boolean isEmpty() {
		return false;
		// TODO Auto-generated method stub
	}


	@Override
	public IDBScheduleItem assembleScheduleItemCopy(IDBScheduleItem underlying)
			throws DatabaseException {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public IDBSchedule getScheduleItemSchedule(IDBScheduleItem underlying)
			throws DatabaseException {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public boolean isInserted(IDBScheduleItem underlying)
			throws DatabaseException {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public IDBObject findDocumentForLocation(IDBLocation underlyingLocation)
			throws DatabaseException {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public IDBObject findDocumentForInstructor(
			IDBInstructor underlyingInstructor) throws DatabaseException {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public void writeState(ObjectOutputStream oos) throws IOException {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void readState(ObjectInputStream ois) throws IOException {
		// TODO Auto-generated method stub
		
	}


	@Override
	public IDBInstructor getDocumentStaffInstructorOrNull(
			IDBDocument underlyingDocument) throws NotFoundException {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public IDBLocation getDocumentTBALocationOrNull(IDBDocument underlyingDocument)
			throws NotFoundException {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public void setDocumentStaffInstructor(IDBDocument underlyingDocument,
			IDBInstructor underlyingInstructor) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void setDocumentTBALocation(IDBDocument underlyingDocument,
			IDBLocation underlyingLocation) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void disassociateLectureAndLab(IDBCourse lecture, IDBCourse lab) {
		// TODO Auto-generated method stub
		
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
	public IDBScheduleItem getScheduleItemLectureOrNull(IDBScheduleItem underlying)
			throws DatabaseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void insertEquipmentType(String string) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public IDBDocument findDocumentByName(String scheduleName) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
