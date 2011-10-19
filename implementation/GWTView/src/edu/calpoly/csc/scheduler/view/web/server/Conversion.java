package edu.calpoly.csc.scheduler.view.web.server;

import edu.calpoly.csc.scheduler.model.db.idb.Instructor;
import edu.calpoly.csc.scheduler.view.web.shared.InstructorGWT;

public abstract class Conversion {
	public static InstructorGWT toGWT(Instructor instructor) {
		return new InstructorGWT(instructor.getFirstName() + instructor.getLastName(), instructor.getId(), instructor.getMaxWTU(), instructor.getOffice().getBuilding() + "-" + instructor.getOffice().getRoom());
	}
}
