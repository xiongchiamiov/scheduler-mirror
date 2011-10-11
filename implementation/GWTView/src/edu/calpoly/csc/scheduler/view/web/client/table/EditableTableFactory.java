package edu.calpoly.csc.scheduler.view.web.client.table;

import java.util.ArrayList;

public class EditableTableFactory {

	/**
	 * Create an editable table for professors
	 * @return the newly created table
	 */
	public static EditableTable createProfessors(){
		
		ArrayList<String> attr = new ArrayList<String>();
		
		attr.add(EditableTableConstants.LAST_NAME);
		
		attr.add(EditableTableConstants.FIRST_NAME);
		
		attr.add(EditableTableConstants.OFFICE_BLDG);
		
		attr.add(EditableTableConstants.OFFICE_ROOM);
		
		return new EditableTable(attr);
	}
	
	
	/**
	 * Create an editable table for courses
	 * @return the newly created table
	 */
	public static EditableTable createCourses(){
		
		ArrayList<String> attr = new ArrayList<String>();
		
		attr.add(EditableTableConstants.COURSE_NAME);
		
		attr.add(EditableTableConstants.DEPARTMENT);
		
		attr.add(EditableTableConstants.COURSE_TYPE);
		
		attr.add(EditableTableConstants.ID);
		
		attr.add(EditableTableConstants.WTU);
		
		attr.add(EditableTableConstants.SCU);
		
		attr.add(EditableTableConstants.MAX_ENROLLMENT);
		
		attr.add(EditableTableConstants.NUM_SECTIONS);
		
		return new EditableTable(attr);
	}
	
	
	/**
	 * Create an editable table for locations
	 * @return the newly created table
	 */
	public static EditableTable createLocations(){
		
		ArrayList<String> attr = new ArrayList<String>();
		
		attr.add(EditableTableConstants.BUILDING);
		
		attr.add(EditableTableConstants.ROOM);
		
		attr.add(EditableTableConstants.TYPE);
		
		attr.add(EditableTableConstants.MAX_OCCUPANCY);
		
		return new EditableTable(attr);
	}
}
