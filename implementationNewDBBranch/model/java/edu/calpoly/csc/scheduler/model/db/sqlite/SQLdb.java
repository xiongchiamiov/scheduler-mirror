package edu.calpoly.csc.scheduler.model.db.sqlite;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import edu.calpoly.csc.scheduler.model.Day;
import edu.calpoly.csc.scheduler.model.db.DatabaseException;
import edu.calpoly.csc.scheduler.model.db.IDBCourse;
import edu.calpoly.csc.scheduler.model.db.IDBCourseAssociation;
import edu.calpoly.csc.scheduler.model.db.IDBCoursePreference;
import edu.calpoly.csc.scheduler.model.db.IDBDayPattern;
import edu.calpoly.csc.scheduler.model.db.IDBDocument;
import edu.calpoly.csc.scheduler.model.db.IDBEquipmentType;
import edu.calpoly.csc.scheduler.model.db.IDBInstructor;
import edu.calpoly.csc.scheduler.model.db.IDBLocation;
import edu.calpoly.csc.scheduler.model.db.IDBObject;
import edu.calpoly.csc.scheduler.model.db.IDBOfferedDayPattern;
import edu.calpoly.csc.scheduler.model.db.IDBProvidedEquipment;
import edu.calpoly.csc.scheduler.model.db.IDBSchedule;
import edu.calpoly.csc.scheduler.model.db.IDBScheduleItem;
import edu.calpoly.csc.scheduler.model.db.IDBTime;
import edu.calpoly.csc.scheduler.model.db.IDBTimePreference;
import edu.calpoly.csc.scheduler.model.db.IDBUsedEquipment;
import edu.calpoly.csc.scheduler.model.db.IDBUser;
import edu.calpoly.csc.scheduler.model.db.IDatabase;
import edu.calpoly.csc.scheduler.model.db.simple.DBUsedEquipment;

public class SQLdb implements IDatabase {
	
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


	@Override
	public IDBUser findUserByUsername(String username) throws DatabaseException {
		IDBUser user = null;
		PreparedStatement stmnt = null;
		
		try {
			stmnt = conn.prepareStatement("select * from userdata where userid = ?");
			stmnt.setString(1, username);
			
			ResultSet rs = stmnt.executeQuery();
			if (rs.next())
				user = new SQLUser(rs.getInt("id"), rs.getString("username"), rs.getBoolean("isAdmin"));
			else
				throw new NotFoundException();
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DatabaseException(e);
		}
		return user;
	}

	@Override
	public IDBUser assembleUser(String username, boolean isAdmin) {
		return new SQLUser(null, username, isAdmin);
	}


	@Override
	public void insertUser(IDBUser user) throws DatabaseException {
		PreparedStatement stmnt = null;
		
		try {
			stmnt = conn.prepareStatement("insert into userdata (username, isAdmin) values (?, ?)");
			stmnt.setString(1, user.getUsername());
			stmnt.setBoolean(2, user.isAdmin());
			
			stmnt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DatabaseException(e);
		}
	}


	@Override
	public void updateUser(IDBUser user) throws DatabaseException {
		PreparedStatement stmnt = null;
		
		try {
			stmnt = conn.prepareStatement("update userdata set username = ?, isAdmin = ?," +
					" where id = ?");
			stmnt.setString(1, user.getUsername());
			stmnt.setBoolean(2, user.isAdmin());
			stmnt.setInt(3, user.getID());
			
			stmnt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DatabaseException(e);
		}
	}


	@Override
	public void deleteUser(IDBUser user) throws DatabaseException {
		PreparedStatement stmnt = null;
		
		try {
			stmnt = conn.prepareStatement("delete from userdata where id = ?");
			stmnt.setInt(1, user.getID());
			
			stmnt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DatabaseException(e);
		}
	}


	@Override
	public Collection<IDBDocument> findAllDocuments() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public IDBDocument findDocumentByID(int id) throws NotFoundException {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public void insertDocument(IDBDocument document) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public IDBDocument assembleDocument(String name, int startHalfHour,
			int endHalfHour) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public void updateDocument(IDBDocument document) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void deleteDocument(IDBDocument document) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public IDBDocument findDocumentForSchedule(IDBSchedule schedule)
			throws NotFoundException {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public boolean isOriginalDocument(IDBDocument doc) {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public boolean documentIsWorkingCopy(IDBDocument document) {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public IDBDocument getOriginalForWorkingCopyDocument(IDBDocument rawDocument)
			throws NotFoundException {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public IDBDocument getWorkingCopyForOriginalDocumentOrNull(
			IDBDocument document) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public void associateWorkingCopyWithOriginal(
			IDBDocument underlyingDocument, IDBDocument underlyingDocument2) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void disassociateWorkingCopyWithOriginal(
			IDBDocument underlyingDocument, IDBDocument underlyingDocument2) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public Collection<IDBSchedule> findAllSchedulesForDocument(
			IDBDocument document) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public IDBSchedule findScheduleByID(int id) throws NotFoundException {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public IDBSchedule assembleSchedule() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public void insertSchedule(IDBDocument containingDocument, IDBSchedule schedule) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void updateSchedule(IDBSchedule schedule) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void deleteSchedule(IDBSchedule schedule) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public Collection<IDBScheduleItem> findScheduleItemsBySchedule(
			IDBSchedule schedule) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Collection<IDBScheduleItem> findAllScheduleItemsForSchedule(
			IDBSchedule schedule) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public IDBScheduleItem findScheduleItemByID(int id)
			throws NotFoundException {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public IDBScheduleItem assembleScheduleItem(int section, Set<Day> days, int startHalfHour, int endHalfHour,
			boolean isPlaced, boolean isConflicted) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public void insertScheduleItem(IDBSchedule schedule,
			IDBCourse course, IDBInstructor instructor, IDBLocation location,
			IDBScheduleItem item) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void updateScheduleItem(IDBScheduleItem schedule) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void deleteScheduleItem(IDBScheduleItem schedule) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public IDBLocation getScheduleItemLocation(IDBScheduleItem item) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public IDBCourse getScheduleItemCourse(IDBScheduleItem item) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public IDBInstructor getScheduleItemInstructor(IDBScheduleItem item) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public void setScheduleItemCourse(IDBScheduleItem underlying,
			IDBCourse findCourseByID) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void setScheduleItemLocation(IDBScheduleItem underlying,
			IDBLocation findLocationByID) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void setScheduleItemInstructor(IDBScheduleItem underlying,
			IDBInstructor findInstructorByID) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public Collection<IDBLocation> findLocationsForDocument(IDBDocument document) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public IDBLocation findLocationByID(int id) throws NotFoundException {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public IDBLocation assembleLocation(
			String room, String type, String maxOccupancy, boolean isSchedulable) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public void insertLocation(IDBDocument containingDocument, IDBLocation location) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void updateLocation(IDBLocation location) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void deleteLocation(IDBLocation location) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public Collection<IDBCourse> findCoursesForDocument(IDBDocument document) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public IDBCourse findCourseByID(int id) throws NotFoundException {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public IDBCourse assembleCourse(
			String name, String catalogNumber, String department, String wtu,
			String scu, String numSections, String type, String maxEnrollment,
			String numHalfHoursPerWeek, boolean isSchedulable) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public void insertCourse(IDBDocument underlyingDocument, IDBCourse course) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void updateCourse(IDBCourse course) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void deleteCourse(IDBCourse course) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public IDBDocument findDocumentForCourse(IDBCourse underlyingCourse) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public IDBCourseAssociation getAssociationForLabOrNull(IDBCourse underlying) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Collection<IDBCourseAssociation> getAssociationsForLecture(
			IDBCourse lectureCourse) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public IDBCourse getAssociationLecture(IDBCourseAssociation association) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public IDBCourse getAssociationLab(IDBCourseAssociation association) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public void associateLectureAndLab(IDBCourse lecture, IDBCourse lab) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public Collection<IDBInstructor> findInstructorsForDocument(
			IDBDocument document) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public IDBInstructor findInstructorByID(int id) throws NotFoundException {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public IDBInstructor assembleInstructor(
			String firstName, String lastName, String username, String maxWTU, boolean isSchedulable) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public void insertInstructor(IDBDocument containingDocument, IDBInstructor instructor) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void updateInstructor(IDBInstructor instructor) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void deleteInstructor(IDBInstructor instructor) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public Map<IDBTime, IDBTimePreference> findTimePreferencesByTimeForInstructor(
			IDBInstructor instructor) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public IDBTimePreference findTimePreferenceByID(int id)
			throws NotFoundException {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public IDBTimePreference findTimePreferenceForInstructorAndTime(
			IDBInstructor instructor, IDBTime time) throws NotFoundException {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public IDBTimePreference assembleTimePreference(int preference) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public void insertTimePreference(IDBInstructor ins,
			IDBTime time, IDBTimePreference timePreference) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void updateTimePreference(IDBTimePreference timePreference) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void deleteTimePreference(IDBTimePreference timePreference) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public Map<IDBCourse, IDBCoursePreference> findCoursePreferencesByCourseForInstructor(
			IDBInstructor instructor) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public IDBCoursePreference findCoursePreferenceByID(int id)
			throws NotFoundException {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public IDBCoursePreference findCoursePreferenceForInstructorIDAndCourse(
			IDBInstructor instructor, IDBCourse course)
			throws NotFoundException {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public IDBCoursePreference assembleCoursePreference(
			int preference) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public void insertCoursePreference(IDBInstructor instructor, IDBCourse course, IDBCoursePreference coursePreference) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void updateCoursePreference(IDBCoursePreference coursePreference) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void deleteCoursePreference(IDBCoursePreference coursePreference) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public IDBTime findTimeByDayAndHalfHour(int day, int halfHour) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public IDBEquipmentType findEquipmentTypeByDescription(
			String equipmentTypeDescription) throws NotFoundException {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Collection<IDBEquipmentType> findAllEquipmentTypes() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Map<IDBEquipmentType, IDBUsedEquipment> findUsedEquipmentByEquipmentForCourse(
			IDBCourse course) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public void deleteUsedEquipment(IDBUsedEquipment usedEquipment) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public DBUsedEquipment assembleUsedEquipment() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public void insertUsedEquipment(IDBCourse course,
			IDBEquipmentType equipmentType, IDBUsedEquipment equip) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public Map<IDBEquipmentType, IDBProvidedEquipment> findProvidedEquipmentByEquipmentForLocation(
			IDBLocation location) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public void deleteProvidedEquipment(IDBProvidedEquipment providedEquipment) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public IDBProvidedEquipment assembleProvidedEquipment() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public void insertProvidedEquipment(IDBLocation location,
			IDBEquipmentType equipmentType, IDBProvidedEquipment equip) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public IDBDayPattern findDayPatternByDays(Set<Integer> dayPattern)
			throws NotFoundException {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Collection<IDBOfferedDayPattern> findOfferedDayPatternsForCourse(
			IDBCourse underlying) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public IDBDayPattern getDayPatternForOfferedDayPattern(
			IDBOfferedDayPattern offered) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public void deleteOfferedDayPattern(IDBOfferedDayPattern offered) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public IDBOfferedDayPattern assembleOfferedDayPattern(
			) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public void insertOfferedDayPattern(IDBCourse underlying, IDBDayPattern dayPattern, IDBOfferedDayPattern pattern) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public IDBScheduleItem assembleScheduleItemCopy(IDBScheduleItem underlying) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public IDBSchedule getScheduleItemSchedule(IDBScheduleItem underlying)
			throws NotFoundException {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public boolean isInserted(IDBScheduleItem underlying) {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public IDBObject findDocumentForLocation(IDBLocation underlyingLocation)
			throws NotFoundException {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public IDBObject findDocumentForInstructor(
			IDBInstructor underlyingInstructor) throws NotFoundException {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public void writeState(ObjectOutputStream oos) throws IOException {
		throw new UnsupportedOperationException();
	}


	@Override
	public void readState(ObjectInputStream ois) throws IOException {
		throw new UnsupportedOperationException();
	}
	
}
