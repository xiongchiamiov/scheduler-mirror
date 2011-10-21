package edu.calpoly.csc.scheduler.view.web.client.views;

import java.util.ArrayList;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.ScrollPanel;

import edu.calpoly.csc.scheduler.view.web.client.GreetingServiceAsync;
import edu.calpoly.csc.scheduler.view.web.client.table.EditableTable;
import edu.calpoly.csc.scheduler.view.web.client.table.EditableTableEntry;
import edu.calpoly.csc.scheduler.view.web.client.table.EditableTableFactory;
import edu.calpoly.csc.scheduler.view.web.shared.InstructorGWT;

public class InstructorsView extends ScrollPanel {
	private Panel container;
	private GreetingServiceAsync service;
	private EditableTable instructorTable;
	private String quarterID;

	public InstructorsView(Panel container, GreetingServiceAsync service, String quarterID) {
		assert(service != null);
		
		this.container = container;
		this.service = service;
		this.quarterID = quarterID;
	}
	
	@Override
	public void onLoad() {
		super.onLoad();

		setWidth("100%");
		setHeight("100%");
		
		instructorTable = EditableTableFactory.createProfessors();
		this.add(instructorTable.getWidget());	
		populateInstructors();
	}

	public void populateInstructors() {
		instructorTable.clear();
		
		service.getInstructorNames(new AsyncCallback<ArrayList<InstructorGWT>>() {
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
}
