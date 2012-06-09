package scheduler.model.db.sqlite;
/**
 * The Class SQLCourseEquipment implements all methods of the IDBObject class (part of the IDatabase interface).
 * This class represents course equipment in the SQLite database.
 * @author kayleneS
 *
 */
public class SQLCourseEquipment extends SQLObject {
	Integer courseID, equipID;
	
	public SQLCourseEquipment(Integer id, Integer courseID, Integer equipID) {
		super(id);
		this.courseID = courseID;
		this.equipID = equipID;
	}

	public Integer getCourseID() {return courseID;}
	public void setCourseID(Integer course) {
		courseID = course;
	}
	
	public Integer getEquipID() {return equipID;}
	public void setEquipID(Integer equip) {
		equipID = equip;
	}
}
