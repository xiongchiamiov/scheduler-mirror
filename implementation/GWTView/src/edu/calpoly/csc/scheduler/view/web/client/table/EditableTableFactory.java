package edu.calpoly.csc.scheduler.view.web.client.table;

import java.util.ArrayList;

public class EditableTableFactory {

	/**
	 * Create an editable table for professors
	 * @return the newly created table
	 */
	public static EditableTable createProfessors(EditableTable.CancelHandler cancelHandler, EditableTable.SaveHandler saveHandler){
		
		ArrayList<String> attr = new ArrayList<String>();
		
		attr.add(EditableTableConstants.INSTR_ID);
		
		attr.add(EditableTableConstants.INSTR_NAME);
		
		attr.add(EditableTableConstants.INSTR_WTU);
		
		attr.add(EditableTableConstants.INSTR_OFFICE);
		
		return new EditableTable(cancelHandler, saveHandler, attr);
	}
	
	
	/**
	 * Create an editable table for courses
	 * @return the newly created table
	 */
	public static EditableTable createCourses(EditableTable.CancelHandler cancelHandler, EditableTable.SaveHandler saveHandler){
		
		ArrayList<String> attr = new ArrayList<String>();
		
		attr.add(EditableTableConstants.COURSE_NAME);
		
		attr.add(EditableTableConstants.COURSE_CATALOG_NUM);
		
		attr.add(EditableTableConstants.COURSE_DEPARTMENT);
		
		attr.add(EditableTableConstants.COURSE_WTU);
		
		attr.add(EditableTableConstants.COURSE_SCU);
		
		attr.add(EditableTableConstants.COURSE_NUM_SECTIONS);
		
		attr.add(EditableTableConstants.COURSE_TYPE);
		
		attr.add(EditableTableConstants.COURSE_MAX_ENROLLMENT);
		
		attr.add(EditableTableConstants.COURSE_LAB);
		
		return new EditableTable(cancelHandler, saveHandler, attr);
	}
	
	
	/**
	 * Create an editable table for locations
	 * @return the newly created table
	 */
	public static EditableTable createLocations(EditableTable.CancelHandler cancelHandler, EditableTable.SaveHandler saveHandler){
		
		ArrayList<String> attr = new ArrayList<String>();
		
		attr.add(EditableTableConstants.LOC_BUILDING);
		
		attr.add(EditableTableConstants.LOC_NAME);
		
		attr.add(EditableTableConstants.LOC_ROOM);
		
		attr.add(EditableTableConstants.LOC_TYPE);
		
		attr.add(EditableTableConstants.LOC_MAX_OCCUPANCY);
		
		attr.add(EditableTableConstants.LOC_EQIPMENT_LIST);
		
		attr.add(EditableTableConstants.LOC_ADDITIONAL_DETAILS);
		
		return new EditableTable(cancelHandler, saveHandler, attr);
	}
}
