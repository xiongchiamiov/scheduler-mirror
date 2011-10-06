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
}
