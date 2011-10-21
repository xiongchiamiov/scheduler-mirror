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
import edu.calpoly.csc.scheduler.view.web.shared.LocationGWT;

public class RoomsView extends ScrollPanel {
	private Panel container;
	private GreetingServiceAsync service;
	private EditableTable locationTable;
	private String quarterID;

	public RoomsView(Panel container, GreetingServiceAsync service, String quarterID) {
		this.container = container;
		this.service = service;
		this.quarterID = quarterID;
	}
	
	@Override
	public void onLoad() {
		super.onLoad();

		setWidth("100%");
		setHeight("100%");
		
		locationTable = EditableTableFactory.createLocations();
		this.add(locationTable.getWidget());
		populateLocations();
	}
	
	public void populateLocations() {
		locationTable.clear();
		
		service.getLocationNames(new AsyncCallback<ArrayList<LocationGWT>>() {
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
}
