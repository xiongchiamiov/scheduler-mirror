package edu.calpoly.csc.scheduler.view.web.client.views;

import java.util.ArrayList;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

import edu.calpoly.csc.scheduler.view.web.client.GreetingServiceAsync;
import edu.calpoly.csc.scheduler.view.web.client.table.EditableTable;
import edu.calpoly.csc.scheduler.view.web.client.table.EditableTableEntry;
import edu.calpoly.csc.scheduler.view.web.client.table.EditableTableFactory;
import edu.calpoly.csc.scheduler.view.web.shared.InstructorGWT;

public class InstructorsView extends View {
	private GreetingServiceAsync greetingService;
	private EditableTable instructorTable;

	public InstructorsView(GreetingServiceAsync greetingService) {
		this.greetingService = greetingService;
		instructorTable = EditableTableFactory.createProfessors(new EditableTable.CancelHandler() {
			public void canceled() {
				populateInstructors();
			}
		}, new EditableTable.SaveHandler() {
			public void saved(ArrayList<InstructorGWT> existingGWTs, ArrayList<InstructorGWT> deletedGWTs) {
				saveInstructors(existingGWTs, deletedGWTs);
			}
		});
		
		this.add(instructorTable.getWidget());
	}

	public void populateInstructors() {
		instructorTable.clear();
		
		greetingService.getInstructorNames(new AsyncCallback<ArrayList<InstructorGWT>>() {
			public void onFailure(Throwable caught) {
				Window.alert("Failed to get professors: " + caught.toString());
			}
			
			public void onSuccess(ArrayList<InstructorGWT> result){
				if (result != null) {
					for (InstructorGWT s : result) {
						instructorTable.add(new EditableTableEntry(s));
					}
				}
			}
		});
	}

	public void saveInstructors(ArrayList<InstructorGWT> instructors, ArrayList<InstructorGWT> deleted){
		
		greetingService.saveInstructors(instructors, deleted, new AsyncCallback<Void>() {
			public void onFailure(Throwable caught) {
				Window.alert("Failed to save instructors: " + caught.toString());
			}
			
			public void onSuccess(Void result){
				populateInstructors();
			}
		});
	}

	@Override
	public void afterShow() {
		populateInstructors();
	}
	
	@Override
	public void beforeHide() {
		// TODO Auto-generated method stub
		
	}
}
