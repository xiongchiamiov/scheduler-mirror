package edu.calpoly.csc.scheduler.view.web.client;

import java.util.ArrayList;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.RootPanel;

import edu.calpoly.csc.scheduler.view.web.client.table.EditableTable;
import edu.calpoly.csc.scheduler.view.web.client.table.EditableTableEntry;
import edu.calpoly.csc.scheduler.view.web.client.table.EditableTableFactory;
import edu.calpoly.csc.scheduler.view.web.shared.InstructorGWT;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class GWTView implements EntryPoint {
	/**
	 * Create a remote service proxy to talk to the server-side Greeting service.
	 */
	private static final GreetingServiceAsync greetingService = GWT
			.create(GreetingService.class);

	private static EditableTable professorTable;
	

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		
		/* editable table (professors) */
		professorTable = EditableTableFactory.createProfessors();
		RootPanel.get().add(professorTable.getWidget());
		
		populateProfessors();
	}
	
	
	public static void populateProfessors(){
		
		professorTable.clear();
		
		greetingService.getProfessorNames(new AsyncCallback<ArrayList<InstructorGWT>>(){
			public void onFailure(Throwable caught){
				
				Window.alert("Failed to get professors: " + caught.toString());
			}
			
			public void onSuccess(ArrayList<InstructorGWT> result){
				
				if(result != null){
					for(InstructorGWT s : result){
						professorTable.add(new EditableTableEntry(s));
					}
				}
			}
		});
	}
	
	
	public static void saveProfessors(ArrayList<InstructorGWT> instructors, ArrayList<InstructorGWT> deleted){
		
		greetingService.saveProfessors(instructors, deleted,
				new AsyncCallback<Void>(){
			public void onFailure(Throwable caught){
				
				Window.alert("Failed to save professors: " + caught.toString());
			}
			
			public void onSuccess(Void result){
				
				populateProfessors();
			}
		});
	}
}
