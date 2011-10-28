package edu.calpoly.csc.scheduler.view.web.client.table;

import edu.calpoly.csc.scheduler.view.web.client.GreetingServiceAsync;
import edu.calpoly.csc.scheduler.view.web.shared.CourseGWT;
import edu.calpoly.csc.scheduler.view.web.shared.InstructorGWT;
import edu.calpoly.csc.scheduler.view.web.shared.LocationGWT;

public class TableFactory {

	public static Table<InstructorGWT> instructor(GreetingServiceAsync service){
		return new Table<InstructorGWT>(new ITableBuilder(service));
	}
	
	public static Table<CourseGWT> course(GreetingServiceAsync service){
		return new Table<CourseGWT>(new CTableBuilder(service));
	}
	
	public static Table<LocationGWT> location(GreetingServiceAsync service){
		return new Table<LocationGWT>(new LTableBuilder(service));
	}
}
