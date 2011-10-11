package edu.calpoly.csc.scheduler.view.web.client.table;

import java.util.ArrayList;

import edu.calpoly.csc.scheduler.view.web.shared.InstructorGWT;
import edu.calpoly.csc.scheduler.view.web.shared.LocationGWT;

/**
 * Class represents an entry (row) for an editable table
 * @author David Seltzer
 *
 */
public class EditableTableEntry {

	private ArrayList<String> prevValues;
	
	private ArrayList<String> newValues;
	
	private boolean deleted;
	
	private String key;
	
	
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
		values.add(instructor.getOfficeBldg());
		values.add(instructor.getOfficeRoom());
		
		// initialize object and set instructor
		initialize(values);
	}
	
	public EditableTableEntry(LocationGWT location) {
		ArrayList<String> values = new ArrayList<String>();

		values.add(location.getBuilding());
		values.add(location.getRoom());
		values.add(String.valueOf(location.getMaxOccupancy()));
		values.add(location.getType());
		
		initialize(values);
	}
	
	/**
	 * Returns the instructor represented by the table entry
	 * @return the represented instructor, null if there is an error
	 */
	public InstructorGWT getInstructor(){
		
		if(newValues.size() < 4){
			return null;
		}
		
		return new InstructorGWT(newValues.get(0), newValues.get(1), key,
				newValues.get(2), newValues.get(3));
	}
	
	public LocationGWT getLocation() {
		if (newValues.size() < 4) {
			return null;
		}
		
		return new LocationGWT(newValues.get(0), newValues.get(1), newValues.get(2), Integer.valueOf(newValues.get(3)));
	}
	
	
	/**
	 * Converts a list of editable table entries into a list of instructors
	 * @param entries entries to convert
	 * @return list of instructors
	 */
	public static ArrayList<InstructorGWT> getInstructors(ArrayList<EditableTableEntry> entries){
		
		ArrayList<InstructorGWT> results = new ArrayList<InstructorGWT>();
		
		for(EditableTableEntry e : entries){
			results.add(e.getInstructor());
		}
		
		return results;
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
	}
	
	/**
	 * Constructot for a new entry. Key will be set to default key
	 * @param columns number of columns in the table
	 */
	public EditableTableEntry(int columns){
		
		ArrayList<String> values = new ArrayList<String>();
		
		for(int i = 0; i < columns; i++){
			values.add("");
		}
		
		key = EditableTableConstants.DEFAULT_KEY;
		
		initialize(values);
	}
	
	/**
	 * Returns the entry's key
	 * @return the key
	 */
	public String getKey(){
		return key;
	}
}
