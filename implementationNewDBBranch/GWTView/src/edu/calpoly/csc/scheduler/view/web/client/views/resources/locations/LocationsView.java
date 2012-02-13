package edu.calpoly.csc.scheduler.view.web.client.views.resources.locations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import edu.calpoly.csc.scheduler.view.web.client.GreetingServiceAsync;
import edu.calpoly.csc.scheduler.view.web.client.IViewContents;
import edu.calpoly.csc.scheduler.view.web.client.ViewFrame;
import edu.calpoly.csc.scheduler.view.web.client.views.LoadingPopup;
import edu.calpoly.csc.scheduler.view.web.shared.InstructorGWT;
import edu.calpoly.csc.scheduler.view.web.shared.LocationGWT;
import edu.calpoly.csc.scheduler.view.web.shared.DayCombinationGWT;

public class LocationsView extends VerticalPanel implements IViewContents, LocationsTable.Strategy {
	private GreetingServiceAsync service;
	private String scheduleName;
	int nextTableLocationID = -2;
	int transactionsPending = 0;
	Map<Integer, Integer> realIDsByTableID = new HashMap<Integer, Integer>();
	
	ArrayList<Integer> deletedTableLocationIDs = new ArrayList<Integer>();
	ArrayList<LocationGWT> editedTableLocations = new ArrayList<LocationGWT>();
	ArrayList<LocationGWT> addedTableLocations = new ArrayList<LocationGWT>();
	
	private int generateTableLocationID() {
		return nextTableLocationID--;
	}
	
	public LocationsView(GreetingServiceAsync service, String scheduleName) {
		this.service = service;
		this.scheduleName = scheduleName;
		this.addStyleName("iViewPadding");
	}

	@Override
	public boolean canPop() {
		return true;
//		assert(table != null);
//		if (table.isSaved())
//			return true;
//		return Window.confirm("You have unsaved data which will be lost. Are you sure you want to navigate away?");
	}
	
	@Override
	public void afterPush(ViewFrame frame) {		
		this.setWidth("100%");
		this.setHeight("100%");

		this.add(new HTML("<h2>" + scheduleName + " - Locations</h2>"));

		add(new LocationsTable(this));
	}

	@Override
	public void getInitialLocations(final AsyncCallback<List<LocationGWT>> callback) {
		final LoadingPopup popup = new LoadingPopup();
		popup.show();

		service.getLocations(new AsyncCallback<List<LocationGWT>>() {
			public void onFailure(Throwable caught) {
				popup.hide();
				callback.onFailure(caught);
			}
			
			public void onSuccess(List<LocationGWT> locations){
				assert(locations != null);
				popup.hide();

				for (LocationGWT location : locations)
					realIDsByTableID.put(location.getID(), location.getID());
				
				callback.onSuccess(locations);
			}
		});
	}

	@Override
	public LocationGWT createLocation() {
		LocationGWT location = new LocationGWT(generateTableLocationID(), "", "", "LEC", "20", false, new LocationGWT.ProvidedEquipmentGWT());
		
		addedTableLocations.add(location);
		
		assert(!editedTableLocations.contains(location));
		
		assert(!deletedTableLocationIDs.contains(location));

		sendUpdates();
		
		return location;
	}
	
	@Override
	public void onLocationEdited(LocationGWT location) {
		assert(!deletedTableLocationIDs.contains(location.getID()));

		if (!addedTableLocations.contains(location))
			if (!editedTableLocations.contains(location))
				editedTableLocations.add(location);
		
		sendUpdates();
	}
	
	@Override
	public void onLocationDeleted(LocationGWT location) {
		editedTableLocations.remove(location);
		
		if (addedTableLocations.contains(location)) {
			addedTableLocations.remove(location);
			return;
		}
		
		assert(!deletedTableLocationIDs.contains(location.getID()));
		deletedTableLocationIDs.add(location.getID());
		
		sendUpdates();
	}

	private void sendUpdates() {
		if (transactionsPending > 0)
			return;
		
		transactionsPending = deletedTableLocationIDs.size() + editedTableLocations.size() + addedTableLocations.size();
		if (transactionsPending == 0)
			return;

		final ArrayList<Integer> copyOfDeletedTableLocationIDs = deletedTableLocationIDs;
		deletedTableLocationIDs = new ArrayList<Integer>();
		
		final ArrayList<LocationGWT> copyOfEditedTableLocations = editedTableLocations;
		editedTableLocations = new ArrayList<LocationGWT>();
		
		final ArrayList<LocationGWT> copyOfAddedTableLocations = addedTableLocations;
		addedTableLocations = new ArrayList<LocationGWT>();
		
		for (Integer deletedTableLocationID : deletedTableLocationIDs) {
			Integer realLocationID = realIDsByTableID.get(deletedTableLocationID);
			service.removeLocation(realLocationID, new AsyncCallback<Void>() {
				public void onSuccess(Void result) { updateFinished(); }
				public void onFailure(Throwable caught) { Window.alert("Update failed: " + caught.getMessage()); }
			});
		}
		
		for (LocationGWT editedTableLocation : editedTableLocations) {
			Integer realLocationID = realIDsByTableID.get(editedTableLocation.getID());
			LocationGWT realLocation = new LocationGWT(editedTableLocation);
			realLocation.setID(realLocationID);
			service.editLocation(realLocation, new AsyncCallback<Void>() {
				public void onSuccess(Void result) { updateFinished(); }
				public void onFailure(Throwable caught) { Window.alert("Update failed: " + caught.getMessage()); }
			});
		}
		
		for (LocationGWT addedTableLocation : addedTableLocations) {
			final int tableLocationID = addedTableLocation.getID();
			LocationGWT realLocation = new LocationGWT(addedTableLocation);
			realLocation.setID(-1);
			service.addLocation(realLocation, new AsyncCallback<LocationGWT>() {
				public void onSuccess(LocationGWT result) {
					System.out.println("Putting " + tableLocationID + " into realIDsByTableID");
					realIDsByTableID.put(tableLocationID, result.getID());
					updateFinished();
				}
				public void onFailure(Throwable caught) { Window.alert("Update failed: " + caught.getMessage()); }
			});
		}
		
		copyOfDeletedTableLocationIDs.clear();
		copyOfEditedTableLocations.clear();
		copyOfAddedTableLocations.clear();
	}
	
	private void updateFinished() {
		assert(transactionsPending > 0);
		transactionsPending--;
		if (transactionsPending == 0)
			sendUpdates();
	}

	@Override
	public void beforePop() { }
	@Override
	public void beforeViewPushedAboveMe() { }
	@Override
	public void afterViewPoppedFromAboveMe() { }
	@Override
	public Widget getContents() { return this; }
}