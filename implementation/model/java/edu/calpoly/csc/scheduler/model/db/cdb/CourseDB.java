package edu.calpoly.csc.scheduler.model.db.cdb;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Set;

import edu.calpoly.csc.scheduler.model.db.AbstractDatabase;
import edu.calpoly.csc.scheduler.model.db.DbData;
import edu.calpoly.csc.scheduler.model.db.SQLDB;
import edu.calpoly.csc.scheduler.model.schedule.Day;
import edu.calpoly.csc.scheduler.model.schedule.Week;

public class CourseDB extends AbstractDatabase<Course> {
	// String constants to describe database
	public static final String TABLENAME = "courses";
	public static final String NAME = "name";
	public static final String CATALOGNUM = "catalognum";
	public static final String DEPT = "dept";
	public static final String WTU = "wtu";
	public static final String SCU = "scu";
	public static final String NUMOFSECTIONS = "numofsections";
	public static final String TYPE = "type";
	public static final String LENGTH = "length";
	public static final String DAYS = "days";
	public static final String ENROLLMENT = "enrollment";
	public static final String LECTUREID = "lectureid";
	public static final String TETHERED = "tetheredtolecture";

	public CourseDB(SQLDB sqldb, int scheduleID) {
		this.sqldb = sqldb;
		this.scheduleDBId = scheduleID;
	}

	protected void fillFields(Course data) {
		// Set fields and values
		fields = new LinkedHashMap<String, Object>();
		fields.put(NAME, data.getName());
		fields.put(CATALOGNUM, data.getCatalogNum());
		fields.put(DEPT, data.getDept());
		fields.put(WTU, data.getWtu());
		fields.put(SCU, data.getScu());
		fields.put(NUMOFSECTIONS, data.getNumOfSections());
		fields.put(TYPE, data.getType().toString());
		fields.put(LENGTH, data.getLength());
		fields.put(DAYS, sqldb.serialize(data.getDays()));
		fields.put(ENROLLMENT, data.getEnrollment());
		fields.put(DbData.SCHEDULEDBID, scheduleDBId);
		fields.put(DbData.NOTE, data.getNote());
		fields.put(LECTUREID, data.getLectureID());
		fields.put(TETHERED, data.getTetheredToLecture());
	}

	protected Course make(ResultSet rs) {
		// Retrieve by column name
		Course toAdd = new Course();
		try {
			toAdd.setName(rs.getString(NAME));
			toAdd.setCatalogNum(rs.getString(CATALOGNUM));
			toAdd.setDept(rs.getString(DEPT));
			toAdd.setWtu(rs.getInt(WTU));
			toAdd.setScu(rs.getInt(SCU));
			toAdd.setNumOfSections(rs.getInt(NUMOFSECTIONS));
			toAdd.setType(rs.getString(TYPE));
			toAdd.setLength(rs.getInt(LENGTH));

			byte[] daysBuf = rs.getBytes(DAYS);
			// toAdd.setDays((Week) sqldb.deserialize(daysBuf));
			// TODO: Remove these next 4 lines when the above one is uncommented
			Week temp = new Week(new Day[] { Day.MON, Day.WED, Day.FRI });
			Set<Week> tmpWeek = new HashSet<Week>();
			tmpWeek.add(temp);
			toAdd.setDays(tmpWeek);

			toAdd.setEnrollment(rs.getInt(ENROLLMENT));
			toAdd.setLectureID(rs.getInt(LECTUREID));
			toAdd.setScheduleDBId(rs.getInt(DbData.SCHEDULEDBID));
			toAdd.setNote(rs.getString(DbData.NOTE));
			toAdd.setDbid(rs.getInt(DbData.DBID));
			toAdd.setTetheredToLecture(rs.getBoolean(TETHERED));
		} catch (SQLException e) {
			e.printStackTrace();
		}
		toAdd.verify();
		return toAdd;
	}

	protected String getTableName() {
		return TABLENAME;
	}

	protected String getCreateTableString() {
		String sqlstring = "CREATE TABLE " + TABLENAME + " (" +
		NAME + " " + SQLVARCHAR + " " + SQLNOTNULL + "," +
		CATALOGNUM + " " + SQLINT + " " + SQLNOTNULL + "," +
		DEPT + " " + SQLVARCHAR + " " + SQLNOTNULL + "," +
		WTU + " " + SQLINT + " " + SQLNOTNULL + "," +
		SCU + " " + SQLINT + " " + SQLNOTNULL + "," +
		NUMOFSECTIONS + " " + SQLINT + " " + SQLNOTNULL + "," +
		TYPE + " " + SQLVARCHAR + " " + SQLNOTNULL + "," +
		LENGTH + " " + SQLINT + " " + SQLNOTNULL + "," +
		DAYS + " " + SQLBLOB + " " + SQLNOTNULL + "," +
		ENROLLMENT + " " + SQLINT + " " + SQLNOTNULL + "," +
		DbData.NOTE + " " + SQLVARCHAR + " " + SQLDEFAULTNULL + "," +
		DbData.DBID + " " + SQLINT + " " + SQLNOTNULL + " " + SQLAUTOINC + "," +
		DbData.SCHEDULEDBID + " " + SQLINT + " " + SQLNOTNULL + "," +
		SQLPRIMARYKEY + " (" + DbData.DBID + "), " +
		SQLUNIQUEKEY + " " + CATALOGNUM + " (" + CATALOGNUM + "," + DEPT + "," + TYPE + "," + DbData.SCHEDULEDBID + ")" +
		")";
		return sqlstring;
	}
}
