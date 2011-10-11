package edu.calpoly.csc.scheduler.view.web.client.table;

import java.util.ArrayList;

import edu.calpoly.csc.scheduler.view.web.shared.CourseGWT;
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
		
		// set the key
		key = instructor.getUserID();
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
	
	
	/******************
	 * Course methods *
	 ******************/
	/**
	 * Creates an entry for an editable table from a course
	 * @param course the course to create an entry from
	 */
	public EditableTableEntry(CourseGWT course){

		// get the original values
		ArrayList<String> values = new ArrayList<String>();
		
		values.add(course.getName());
		values.add(course.getDept());
		values.add(course.getType());
		values.add("" + course.getId());
		values.add("" + course.getWtu());
		values.add("" + course.getScu());
		values.add("" + course.getMaxEnrollment());
		values.add("" + course.getNumOfSections());
		
		// initialize object and set instructor
		initialize(values);
		
		// set the key
		/** TODO set the key to database key for a course */
		key = "1";
	}
	
	/**
	 * Returns the course represented by the table entry
	 * @return the represented course, null if there is an error
	 */
	public CourseGWT getCourse(){
		
		if(newValues.size() < 8){
			return null;
		}
		
		int id, wtu, scu, maxEnrollment, numOfSections;
		
		try{
			id = Integer.parseInt(newValues.get(3));
			wtu = Integer.parseInt(newValues.get(4));
			scu = Integer.parseInt(newValues.get(5));
			maxEnrollment = Integer.parseInt(newValues.get(6));
			numOfSections = Integer.parseInt(newValues.get(7));
			
		}catch(Exception e){
			return null;
		}
		
		return new CourseGWT(newValues.get(0), newValues.get(1),
				newValues.get(2), id, wtu, scu, maxEnrollment, numOfSections);
	}
	
	
	/**
	 * Converts a list of editable table entries into a list of courses
	 * @param entries entries to convert
	 * @return list of courses
	 */
	public static ArrayList<CourseGWT> getCourses(ArrayList<EditableTableEntry> entries){
		
		ArrayList<CourseGWT> results = new ArrayList<CourseGWT>();
		
		for(EditableTableEntry e : entries){
			results.add(e.getCourse());
		}
		
		return results;
	}
	
	
	
	/********************
	 * Location methods *
	 ********************/
	/**
	 * Creates an entry for an editable table from a location
	 * @param location the location to create an entry from
	 */
	public EditableTableEntry(LocationGWT location){

		// get the original values
		ArrayList<String> values = new ArrayList<String>();
		
		values.add(location.getBuilding());
		values.add(location.getRoom());
		values.add(location.getType());
		values.add("" + location.getMaxOccupancy());
		
		// initialize object and set instructor
		initialize(values);
		
		// set the key
		/** TODO set the key to database key for a course */
		key = "1";
	}
	
	/**
	 * Returns the location represented by the table entry
	 * @return the represented course, null if there is an error
	 */
	public LocationGWT getLocation(){
		
		if(newValues.size() < 4){
			return null;
		}
		
		int maxOccupancy;
		
		try{
			maxOccupancy = Integer.parseInt(newValues.get(3));
			
		}catch(Exception e){
			return null;
		}
		
		return new LocationGWT(newValues.get(0), newValues.get(1),
				newValues.get(2), maxOccupancy);
	}
	
	
	/**
	 * Converts a list of editable table entries into a list of locations
	 * @param entries entries to convert
	 * @return list of locations
	 */
	public static ArrayList<LocationGWT> getLocations(ArrayList<EditableTableEntry> entries){
		
		ArrayList<LocationGWT> results = new ArrayList<LocationGWT>();
		
		for(EditableTableEntry e : entries){
			results.add(e.getLocation());
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
	 * Constructor for a new entry. Key will be set to default key
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
