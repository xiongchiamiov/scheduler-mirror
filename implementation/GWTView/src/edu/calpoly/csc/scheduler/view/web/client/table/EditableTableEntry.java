package edu.calpoly.csc.scheduler.view.web.client.table;

import java.util.ArrayList;

import edu.calpoly.csc.scheduler.view.web.shared.InstructorGWT;

/**
 * Class represents an entry (row) for an editable table
 * @author David Seltzer
 *
 */
public class EditableTableEntry {

	private ArrayList<String> prevValues;
	
	private ArrayList<String> newValues;
	
	private boolean deleted;
	
	private InstructorGWT instructor;
	
	/**********************
	 * Instructor methods *
	 **********************/
	/**
	 * Creates an entry for an editable table from an instructor
	 * @param instructor the instructor to create an entry from
	 */
	public EditableTableEntry(InstructorGWT instructor){

		// get the original values
		ArrayList<String> values = new ArrayList<String>();
		
		values.add(instructor.getLastName());
		values.add(instructor.getFirstName());
		values.add(instructor.getUserID());
		values.add(instructor.getOfficeBldg());
		values.add(instructor.getOfficeRoom());
		
		// initialize object and set instructor
		initialize(values);
		this.instructor = instructor;
	}
	
	/**
	 * Returns the instructor represented by the table entry
	 * @return the represented instructor, null if the type is not instructor
	 */
	public InstructorGWT getInstructor(){
		/** TODO update the instructor */
		return instructor;
	}
	
	
	/*********************
	 * Universal methods *
	 *********************/
	/**
	 * Returns a list of the current values
	 */
	public ArrayList<String> getValues(){
		return newValues;
	}
	
	public void setValue(int index, String value){
		newValues.remove(index);
		newValues.add(index, value);
	}
	
	/**
	 * Deletes the entry from the table
	 */
	public void delete(){
		deleted = true;
	}
	
	
	/**
	 * Get whether the entry has been deleted from the table
	 * @return true if the entry has been deleted, false if it has not been deleted
	 */
	public boolean isDeleted() {
		return deleted;
	}

	/**
	 * Returns whether the entry at the given index has been modified
	 * @param index the index to compare
	 * @return true if there are changes or index is out of bounds, false if no changes have been made
	 */
	public boolean isChanged(int index){
		
		// check array sizes
		if(newValues.size() <= index ||
			prevValues.size() <= index){
			return true;
		}
		
		// compare items
		return !newValues.get(index).equals(prevValues.get(index));
	}

	
	/**
	 * Initialize the object and populate with the given values
	 * @param values original values for the object
	 */
	private void initialize(ArrayList<String> values){
		deleted = false;
		
		prevValues = new ArrayList<String>();
		newValues = new ArrayList<String>();
		
		for(String str : values){
			prevValues.add(str);
			newValues.add(str);
		}
		
		instructor = null;
	}
	
	public EditableTableEntry(int columns){
		
		ArrayList<String> values = new ArrayList<String>();
		
		for(int i = 0; i < columns; i++){
			values.add("");
		}
		
		initialize(values);
	}
}
