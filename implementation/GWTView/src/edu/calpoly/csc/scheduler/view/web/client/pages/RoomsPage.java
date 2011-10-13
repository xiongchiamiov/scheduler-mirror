package edu.calpoly.csc.scheduler.view.web.client.pages;

import java.util.ArrayList;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

import edu.calpoly.csc.scheduler.view.web.client.GreetingServiceAsync;
import edu.calpoly.csc.scheduler.view.web.client.table.EditableTable;
import edu.calpoly.csc.scheduler.view.web.client.table.EditableTableEntry;
import edu.calpoly.csc.scheduler.view.web.client.table.EditableTableFactory;
import edu.calpoly.csc.scheduler.view.web.shared.InstructorGWT;
import edu.calpoly.csc.scheduler.view.web.shared.LocationGWT;

public class RoomsPage extends View {
	private GreetingServiceAsync greetingService;
	private EditableTable locationTable;

	public RoomsPage(GreetingServiceAsync greetingService) {
		this.greetingService = greetingService;
		locationTable = EditableTableFactory.createLocations(new EditableTable.CancelHandler() {
			public void canceled() {
				populateLocations();
			}
		}, new EditableTable.SaveHandler() {
			public void saved(ArrayList<InstructorGWT> existingGWTs, ArrayList<InstructorGWT> deletedGWTs) {
				/* TODO */
				populateLocations();
			}
		});
		
		this.add(locationTable.getWidget());
	}
	
	public void populateLocations() {
		locationTable.clear();
		
		greetingService.getLocationNames(new AsyncCallback<ArrayList<LocationGWT>>() {
			public void onFailure(Throwable caught) {
				Window.alert("Failed to get courses: " + caught.toString());
			}
			
			public void onSuccess(ArrayList<LocationGWT> result){
				if (result != null) {
					for (LocationGWT s : result) {
						locationTable.add(new EditableTableEntry(s));
					}
				}
			}
		});
	}
	
	@Override
	public void beforeHide() {
		// TODO Auto-generated method stub

	}

	@Override
	public void afterShow() {
		populateLocations();
	}

}
