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
	
	private ArrayList<String> currValues;
	
	
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
		
		values.add(instructor.getFirstName());
		values.add(instructor.getLastName());
		values.add(instructor.getUserID());
		values.add("" + instructor.getWtu());
		values.add(instructor.getBuilding());
		values.add(instructor.getRoomNumber());
		values.add(""+instructor.getDisabilities());
		
		// initialize object and set instructor
		initialize(values);
	}
	
	/**
	 * Returns the instructor represented by the table entry
	 * @return the represented instructor, null if there is an error
	 */
	public InstructorGWT getInstructor(){
		
		if(currValues.size() < 4){
			return null;
		}
		
		int wtu;
		try{
			wtu = Integer.parseInt(currValues.get(2));
		}catch(Exception e){wtu = 0;}
		
		return new InstructorGWT(currValues.get(0), currValues.get(1),
				wtu, currValues.get(3));
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
		
		values.add("" + course.getID());
		values.add(course.getCourseName());
		values.add("" + course.getCatalogNum());
		values.add("" + course.getWtu());
		values.add("" + course.getScu());
		values.add(course.getType());
		values.add("" + course.getMaxEnroll());
		values.add("" + course.getLabID());
		values.add(""+course.getSmartroom());
		values.add(""+course.getLaptop());
		values.add(""+course.getOverhead());
		values.add(""+course.getLength());
		values.add(course.getCTPrefix());
		values.add(course.getPrefix());


		
		// initialize object and set instructor
		initialize(values);
	}
	
	/**
	 * Returns the course represented by the table entry
	 * @return the represented course, null if there is an error
	 */
	public CourseGWT getCourse(){
		
		if(currValues.size() < 9){
			return null;
		}
		
		int catalogName, wtu, scu, numSections, maxEnroll;
		
		try{
			catalogName = Integer.parseInt(currValues.get(1));
			wtu = Integer.parseInt(currValues.get(3));
			scu = Integer.parseInt(currValues.get(4));
			numSections = Integer.parseInt(currValues.get(5));
			maxEnroll = Integer.parseInt(currValues.get(7));
			
		}catch(Exception e){
			catalogName = 0;
			wtu = 0;
			scu = 0;
			numSections = 0;
			maxEnroll = 0;
		}
		
		return new CourseGWT(currValues.get(0), catalogName, currValues.get(2), wtu, scu, numSections, currValues.get(6), maxEnroll, currValues.get(8));
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
		values.add("" + location.getMaxOccupancy());
		values.add(location.getType());
		values.add("" + location.isSmartRoom());
		values.add("" + location.hasLaptopConnectivity());
		values.add("" + location.isADACompliant());
		values.add("" + location.hasOverhead());

		// initialize object and set instructor
		initialize(values);
	}
	
	/**
	 * Returns the location represented by the table entry
	 * @return the represented course, null if there is an error
	 */
	public LocationGWT getLocation(){
		
		if(currValues.size() < 7){
			return null;
		}
		
		int maxOccupancy;
		
		try{
			maxOccupancy = Integer.parseInt(currValues.get(4));
			
		}catch(Exception e){
			maxOccupancy = 0;
		}
		
		return new LocationGWT(currValues.get(0), currValues.get(1),
				currValues.get(2), currValues.get(3), maxOccupancy, currValues.get(5), currValues.get(6));
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
	public EditableTableEntry(ArrayList<String> values){
		initialize(values);
	}
	
	/**
	 * Returns a list of the current values
	 */
	public ArrayList<String> getValues(){
		return currValues;
	}
	
	public void setValue(int index, String value){
		currValues.remove(index);
		currValues.add(index, value);
	}

	
	/**
	 * Initialize the object and populate with the given values
	 * @param values original values for the object
	 */
	private void initialize(ArrayList<String> values){
		
		currValues = new ArrayList<String>();
		
		for(String str : values){
			currValues.add(str);
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
		
		initialize(values);
	}
}
