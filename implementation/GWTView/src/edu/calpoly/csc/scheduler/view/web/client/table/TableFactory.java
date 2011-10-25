package edu.calpoly.csc.scheduler.view.web.client.table;

import edu.calpoly.csc.scheduler.view.web.shared.CourseGWT;
import edu.calpoly.csc.scheduler.view.web.shared.InstructorGWT;
import edu.calpoly.csc.scheduler.view.web.shared.LocationGWT;

public class TableFactory {

	public static Table<InstructorGWT> instructor(){
		return new Table<InstructorGWT>(new ITableBuilder());
	}
	
	public static Table<CourseGWT> course(){
		return new Table<CourseGWT>(new CTableBuilder());
	}
	
	public static Table<LocationGWT> location(){
		return new Table<LocationGWT>(new LTableBuilder());
	}
}
