package edu.calpoly.csc.scheduler.view.web.client.views;

import java.util.ArrayList;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.calpoly.csc.scheduler.view.web.client.GreetingServiceAsync;
import edu.calpoly.csc.scheduler.view.web.client.table.Table;
import edu.calpoly.csc.scheduler.view.web.client.table.TableFactory;
import edu.calpoly.csc.scheduler.view.web.shared.LocationGWT;

public class RoomsView extends ScrollPanel {
	private Panel container;
	private GreetingServiceAsync service;
	private String quarterID;
	private Table<LocationGWT> lTable;

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
		
		VerticalPanel vp = new VerticalPanel();
		this.add(vp);
		
		lTable = TableFactory.location(quarterID, service);
		vp.add(lTable.getWidget());
		populateLocations();
	}
	
	public void populateLocations() {
		lTable.clear();
		
		service.getLocationNames(new AsyncCallback<ArrayList<LocationGWT>>() {
			public void onFailure(Throwable caught) {
				Window.alert("Failed to get courses: " + caught.toString());
			}
			
			public void onSuccess(ArrayList<LocationGWT> result){
				if (result != null) {
					lTable.set(result);
				}
			}
		});
	}
}
