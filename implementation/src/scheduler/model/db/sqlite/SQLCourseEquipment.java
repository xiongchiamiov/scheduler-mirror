package scheduler.model.db.sqlite;

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
