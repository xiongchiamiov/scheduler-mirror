package edu.calpoly.csc.scheduler.view.web.client;

import java.util.ArrayList;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.calpoly.csc.scheduler.view.web.client.table.EditableTable;
import edu.calpoly.csc.scheduler.view.web.client.table.EditableTableEntry;
import edu.calpoly.csc.scheduler.view.web.client.table.EditableTableFactory;
import edu.calpoly.csc.scheduler.view.web.shared.InstructorGWT;
import edu.calpoly.csc.scheduler.view.web.shared.LocationGWT;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class GWTView implements EntryPoint {
	/**
	 * Create a remote service proxy to talk to the server-side Greeting service.
	 */
	private static final GreetingServiceAsync greetingService = GWT
			.create(GreetingService.class);

	private static EditableTable instructorTable;
	private static EditableTable locationTable;
	
	VerticalPanel bodyPanel;
	
	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		
		/* editable table (professors) */
		instructorTable = EditableTableFactory.createProfessors();
		
		bodyPanel = new VerticalPanel();
		RootPanel.get().add(bodyPanel);
		
		bodyPanel.add(instructorTable.getWidget());
		populateInstructors();
		
		RootPanel.get().add(new Button("Rooms", new ClickHandler() {
			public void onClick(ClickEvent events) {
				bodyPanel.clear();
				bodyPanel.add(locationTable.getWidget());
				populateLocations();
			}
		}));

	}


	public static void populateLocations() {
		
		locationTable.clear();
		
		greetingService.getLocationNames(new AsyncCallback<ArrayList<LocationGWT>>(){
			public void onFailure(Throwable caught){
				
				Window.alert("Failed to get professors: " + caught.toString());
			}
			
			public void onSuccess(ArrayList<LocationGWT> result){
				
				if(result != null){
					for(LocationGWT s : result){
						locationTable.add(new EditableTableEntry(s));
					}
				}
			}
		});
	}

	public static void saveLocations(ArrayList<LocationGWT> locations, ArrayList<LocationGWT> deleted){
		
		greetingService.saveLocations(locations, deleted, new AsyncCallback<Void>(){
			public void onFailure(Throwable caught) {
				Window.alert("Failed to save location: " + caught.toString());
			}
			
			public void onSuccess(Void result) {
				populateLocations();
			}
		});
	}
	
	
	public static void saveInstructors(ArrayList<InstructorGWT> instructors, ArrayList<InstructorGWT> deleted){
		
		greetingService.saveInstructors(instructors, deleted,
				new AsyncCallback<Void>(){
			public void onFailure(Throwable caught){
				
				Window.alert("Failed to save instructors: " + caught.toString());
			}
			
			public void onSuccess(Void result){
				
				populateInstructors();
			}
		});
	}

	public static void populateInstructors() {
		
		instructorTable.clear();
		
		greetingService.getInstructorNames(new AsyncCallback<ArrayList<InstructorGWT>>(){
			public void onFailure(Throwable caught){
				
				Window.alert("Failed to get professors: " + caught.toString());
			}
			
			public void onSuccess(ArrayList<InstructorGWT> result){
				
				if(result != null){
					for(InstructorGWT s : result){
						instructorTable.add(new EditableTableEntry(s));
					}
				}
			}
		});
	}
	
}
