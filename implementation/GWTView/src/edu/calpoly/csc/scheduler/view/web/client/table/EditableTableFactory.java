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
		
		ArrayList<AttributeInfo> attr = new ArrayList<AttributeInfo>();
		
		// attributes		
		attr.add(new AttributeInfo(EditableTableConstants.INSTR_FIRSTNAME, AttributeInfo.STR));

		attr.add(new AttributeInfo(EditableTableConstants.INSTR_LASTNAME, AttributeInfo.STR));

		attr.add(new AttributeInfo(EditableTableConstants.INSTR_ID, AttributeInfo.STR));

		attr.add(new AttributeInfo(EditableTableConstants.INSTR_WTU, AttributeInfo.INT));
		
		attr.add(new AttributeInfo(EditableTableConstants.INSTR_BUILDING, AttributeInfo.INT));

		attr.add(new AttributeInfo(EditableTableConstants.INSTR_ROOMNUMBER, AttributeInfo.INT));

		attr.add(new AttributeInfo(EditableTableConstants.INSTR_DISABILITIES, AttributeInfo.BOOL));

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
		
		ArrayList<AttributeInfo> attr = new ArrayList<AttributeInfo>();
		
		// attributes
		attr.add(new AttributeInfo(EditableTableConstants.COURSE_ID, AttributeInfo.INT));

		attr.add(new AttributeInfo(EditableTableConstants.COURSE_NAME, AttributeInfo.INT));
		
		attr.add(new AttributeInfo(EditableTableConstants.COURSE_CATALOG_NUM, AttributeInfo.INT));
				
		attr.add(new AttributeInfo(EditableTableConstants.COURSE_WTU, AttributeInfo.INT));
		
		attr.add(new AttributeInfo(EditableTableConstants.COURSE_SCU, AttributeInfo.INT));
				
		attr.add(new AttributeInfo(EditableTableConstants.COURSE_TYPE, AttributeInfo.INT));
		
		attr.add(new AttributeInfo(EditableTableConstants.COURSE_MAX_ENROLLMENT, AttributeInfo.INT));
		
		attr.add(new AttributeInfo(EditableTableConstants.COURSE_LABID, AttributeInfo.INT));

		attr.add(new AttributeInfo(EditableTableConstants.COURSE_SMARTROOM, AttributeInfo.INT));

		attr.add(new AttributeInfo(EditableTableConstants.COURSE_LAPTOP, AttributeInfo.INT));

		attr.add(new AttributeInfo(EditableTableConstants.COURSE_OVERHEAD, AttributeInfo.INT));

		attr.add(new AttributeInfo(EditableTableConstants.COURSE_LENGTH, AttributeInfo.INT));

		attr.add(new AttributeInfo(EditableTableConstants.COURSE_CTPREFIX, AttributeInfo.INT));

		attr.add(new AttributeInfo(EditableTableConstants.COURSE_PREFIX, AttributeInfo.INT));

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
		
		ArrayList<AttributeInfo> attr = new ArrayList<AttributeInfo>();
		
		// attributes
		attr.add(new AttributeInfo(EditableTableConstants.LOC_BUILDING, AttributeInfo.INT));
				
		attr.add(new AttributeInfo(EditableTableConstants.LOC_ROOM, AttributeInfo.INT));
		
		attr.add(new AttributeInfo(EditableTableConstants.LOC_MAX_OCCUPANCY, AttributeInfo.INT));
		
		attr.add(new AttributeInfo(EditableTableConstants.LOC_TYPE, AttributeInfo.INT));
		
		attr.add(new AttributeInfo(EditableTableConstants.LOC_SMARTROOM, AttributeInfo.INT));
		
		attr.add(new AttributeInfo(EditableTableConstants.LOC_LAPTOPCONNECTIVITY, AttributeInfo.INT));

		attr.add(new AttributeInfo(EditableTableConstants.LOC_ADACOMPLIANT, AttributeInfo.INT));

		attr.add(new AttributeInfo(EditableTableConstants.LOC_OVERHEAD, AttributeInfo.INT));

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
