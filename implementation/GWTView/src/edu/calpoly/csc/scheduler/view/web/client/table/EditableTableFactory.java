package edu.calpoly.csc.scheduler.view.web.client.table;

import java.util.ArrayList;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

import edu.calpoly.csc.scheduler.view.web.client.GreetingService;
import edu.calpoly.csc.scheduler.view.web.client.GreetingServiceAsync;
import edu.calpoly.csc.scheduler.view.web.shared.CourseGWT;
import edu.calpoly.csc.scheduler.view.web.shared.InstructorGWT;
import edu.calpoly.csc.scheduler.view.web.shared.LocationGWT;

public class EditableTableFactory {

	private static final GreetingServiceAsync service = GWT
			.create(GreetingService.class);
	
	/**
	 * Create an editable table for professors
	 * @return the newly created table
	 */
	public static EditableTable createProfessors(){
		
		ArrayList<String> attr = new ArrayList<String>();
		
		// attributes		
		attr.add(EditableTableConstants.INSTR_FIRSTNAME);

		attr.add(EditableTableConstants.INSTR_LASTNAME);

		attr.add(EditableTableConstants.INSTR_ID);

		attr.add(EditableTableConstants.INSTR_WTU);
		
		attr.add(EditableTableConstants.INSTR_BUILDING);

		attr.add(EditableTableConstants.INSTR_ROOMNUMBER);

		attr.add(EditableTableConstants.INSTR_DISABILITIES);

		// save handler
		final EditableTable table = new EditableTable(attr);
		table.addSaveHandler(new ClickHandler(){
			public void onClick(ClickEvent event){
				
				ArrayList<InstructorGWT> instructors = new ArrayList<InstructorGWT>();
				ArrayList<EditableTableEntry> entries = table.getEntries();
				for(EditableTableEntry e : entries){
					InstructorGWT i = e.getInstructor();
					if(i != null){
						instructors.add(i);
					}
				}
				
				service.saveInstructors(instructors, new AsyncCallback<Void>(){
					public void onFailure(Throwable caught){ 
						Window.alert("Error saving instructors:\n" + caught.getMessage());
					}
					public void onSuccess(Void result){}
				});
			}
		});
		
		return table;
	}
	
	
	/**
	 * Create an editable table for courses
	 * @return the newly created table
	 */
	public static EditableTable createCourses(){
		
		ArrayList<String> attr = new ArrayList<String>();
		
		// attributes
		attr.add(EditableTableConstants.COURSE_ID);

		attr.add(EditableTableConstants.COURSE_NAME);
		
		attr.add(EditableTableConstants.COURSE_CATALOG_NUM);
				
		attr.add(EditableTableConstants.COURSE_WTU);
		
		attr.add(EditableTableConstants.COURSE_SCU);
				
		attr.add(EditableTableConstants.COURSE_TYPE);
		
		attr.add(EditableTableConstants.COURSE_MAX_ENROLLMENT);
		
		attr.add(EditableTableConstants.COURSE_LABID);

		attr.add(EditableTableConstants.COURSE_SMARTROOM);

		attr.add(EditableTableConstants.COURSE_LAPTOP);

		attr.add(EditableTableConstants.COURSE_OVERHEAD);

		attr.add(EditableTableConstants.COURSE_LENGTH);

		attr.add(EditableTableConstants.COURSE_CTPREFIX);

		attr.add(EditableTableConstants.COURSE_PREFIX);

		// save handler
		final EditableTable table = new EditableTable(attr);
		table.addSaveHandler(new ClickHandler(){
			public void onClick(ClickEvent event){

				ArrayList<CourseGWT> courses = new ArrayList<CourseGWT>();
				ArrayList<EditableTableEntry> entries = table.getEntries();
				for(EditableTableEntry e : entries){
					CourseGWT c = e.getCourse();
					if(c != null){
						courses.add(c);
					}
				}
				
				service.saveCourses(courses, new AsyncCallback<Void>(){
					public void onFailure(Throwable caught){ 
						Window.alert("Error saving courses:\n" + caught.getMessage());
					}
					public void onSuccess(Void result){}
				});
			}
		});
		
		return table;
	}
	
	
	/**
	 * Create an editable table for locations
	 * @return the newly created table
	 */
	public static EditableTable createLocations(){
		
		ArrayList<String> attr = new ArrayList<String>();
		
		// attributes
		attr.add(EditableTableConstants.LOC_BUILDING);
				
		attr.add(EditableTableConstants.LOC_ROOM);
		
		attr.add(EditableTableConstants.LOC_MAX_OCCUPANCY);
		
		attr.add(EditableTableConstants.LOC_TYPE);
		
		attr.add(EditableTableConstants.LOC_SMARTROOM);
		
		attr.add(EditableTableConstants.LOC_LAPTOPCONNECTIVITY);

		attr.add(EditableTableConstants.LOC_ADACOMPLIANT);

		attr.add(EditableTableConstants.LOC_OVERHEAD);

		// save handler
		final EditableTable table = new EditableTable(attr);
		table.addSaveHandler(new ClickHandler(){
			public void onClick(ClickEvent event){
				
				ArrayList<LocationGWT> locations = new ArrayList<LocationGWT>();
				ArrayList<EditableTableEntry> entries = table.getEntries();
				for(EditableTableEntry e : entries){
					LocationGWT l = e.getLocation();
					if(l != null){
						locations.add(l);
					}
				}
				
				service.saveLocations(locations, new AsyncCallback<Void>(){
					public void onFailure(Throwable caught){ 
						Window.alert("Error saving locations:\n" + caught.getMessage());
					}
					public void onSuccess(Void result){}
				});
			}
		});
		
		return table;
	}
}
