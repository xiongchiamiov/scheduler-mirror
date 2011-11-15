package edu.calpoly.csc.scheduler.view.web.client.views;

import java.util.Collection;
import java.util.LinkedList;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.calpoly.csc.scheduler.view.web.client.GreetingServiceAsync;
import edu.calpoly.csc.scheduler.view.web.client.table.CheckboxColumn;
import edu.calpoly.csc.scheduler.view.web.client.table.Factory;
import edu.calpoly.csc.scheduler.view.web.client.table.IntColumn;
import edu.calpoly.csc.scheduler.view.web.client.table.MultiselectColumn;
import edu.calpoly.csc.scheduler.view.web.client.table.OsmTable;
import edu.calpoly.csc.scheduler.view.web.client.table.SelectColumn;
import edu.calpoly.csc.scheduler.view.web.client.table.StaticGetter;
import edu.calpoly.csc.scheduler.view.web.client.table.StaticSetter;
import edu.calpoly.csc.scheduler.view.web.client.table.StaticValidator;
import edu.calpoly.csc.scheduler.view.web.client.table.StringColumn;
import edu.calpoly.csc.scheduler.view.web.client.table.TableConstants;
import edu.calpoly.csc.scheduler.view.web.client.table.StaticValidator.InvalidValueException;
import edu.calpoly.csc.scheduler.view.web.shared.InstructorGWT;
import edu.calpoly.csc.scheduler.view.web.shared.LocationGWT;

public class LocationsView extends ScrollPanel {
	private static final String LAPTOP_CONNECTIVITY = "Laptop Connectivity";
	private static final String OVERHEAD = "Overhead";
	private static final String SMART_ROOM = "Smart Room";
	
	private GreetingServiceAsync service;
	private final String scheduleName;
	int nextLocationID = 1;
	private static OsmTable<LocationGWT> table;

	public LocationsView(GreetingServiceAsync service, String scheduleName) {
		this.service = service;
		this.scheduleName = scheduleName;
	}
	
	@Override
	public void onLoad() {
		super.onLoad();

		setWidth("100%");
		setHeight("100%");
		
		VerticalPanel vp = new VerticalPanel();
		this.add(vp);

		vp.add(new HTML("<h2>" + scheduleName + " - Locations</h2>"));

		final LoadingPopup popup = new LoadingPopup();
		popup.show();

		table = new OsmTable<LocationGWT>(
				new Factory<LocationGWT>() {
					public LocationGWT create() {
						return new LocationGWT(nextLocationID++, "", "", "LEC", 20, false, new LocationGWT.ProvidedEquipmentGWT());
					}
					public LocationGWT createHistoryFor(LocationGWT location) {
						return new LocationGWT(-location.getID(), location);
					}
				},
				new OsmTable.SaveHandler<LocationGWT>() {
					public void saveButtonClicked() {
						save();
					}
				});

		table.addColumn(new StringColumn<LocationGWT>("Building", null,
				new StaticGetter<LocationGWT, String>() {
					public String getValueForObject(LocationGWT object) { return object.getBuilding(); }
				},
				new StaticSetter<LocationGWT, String>() {
					public void setValueInObject(LocationGWT object, String newValue) { object.setBuilding(newValue); }
				},
				String.CASE_INSENSITIVE_ORDER,
				new StaticValidator<LocationGWT, String>() {
					public void validate(LocationGWT object, String newBuilding) throws InvalidValueException {
						LocationGWT locationAtPlace = locationExists(newBuilding, object.getRoom());
						if (locationAtPlace != null && locationAtPlace != object)
							throw new InvalidValueException("Location " + newBuilding + "-" + object.getRoom() + " already exists.");
					}
				}));

		table.addColumn(new StringColumn<LocationGWT>("Room", null,
				new StaticGetter<LocationGWT, String>() {
					public String getValueForObject(LocationGWT object) { return object.getRoom(); }
				},
				new StaticSetter<LocationGWT, String>() {
					public void setValueInObject(LocationGWT object, String newValue) { object.setRoom(newValue); }
				},
				String.CASE_INSENSITIVE_ORDER,
				new StaticValidator<LocationGWT, String>() {
					public void validate(LocationGWT object, String newRoom) throws InvalidValueException {
						LocationGWT locationAtPlace = locationExists(object.getBuilding(), newRoom);
						if (locationAtPlace != null && locationAtPlace != object)
							throw new InvalidValueException("Location " + object.getBuilding() + "-" + newRoom + " already exists.");
					}
				}));
		
		table.addColumn(new SelectColumn<LocationGWT>("Type", null,
				new String[] { "LEC", "LAB" },
				new StaticGetter<LocationGWT, String>() {
					public String getValueForObject(LocationGWT object) { return object.getType(); }
				},
				new StaticSetter<LocationGWT, String>() {
					public void setValueInObject(LocationGWT object, String newValue) { object.setType(newValue); }
				},
				String.CASE_INSENSITIVE_ORDER));

		table.addColumn(new IntColumn<LocationGWT>("Occupancy", null,
				new StaticGetter<LocationGWT, Integer>() {
					public Integer getValueForObject(LocationGWT object) { return object.getMaxOccupancy(); }
				},
				new StaticSetter<LocationGWT, Integer>() {
					public void setValueInObject(LocationGWT object, Integer newValue) { object.setMaxOccupancy(newValue); }
				},
				new StaticValidator<LocationGWT, Integer>() {
					public void validate(LocationGWT object, Integer newMaxOcc) throws InvalidValueException {
						if (newMaxOcc < 0)
							throw new InvalidValueException(TableConstants.LOC_MAX_OCCUPANCY + " must be a positive: " + newMaxOcc + " is invalid.");
					}
				}));
		
		table.addColumn(new CheckboxColumn<LocationGWT>("ADA", null,
				new StaticGetter<LocationGWT, Boolean>() {
					public Boolean getValueForObject(LocationGWT object) { return object.isADACompliant(); }
				},
				new StaticSetter<LocationGWT, Boolean>() {
					public void setValueInObject(LocationGWT object, Boolean newValue) { object.setADACompliant(newValue); }
				}));
		
		table.addColumn(new MultiselectColumn<LocationGWT>("Equipment", null,
				new String[] { LAPTOP_CONNECTIVITY, OVERHEAD, SMART_ROOM },
				new StaticGetter<LocationGWT, Collection<String>>() {
					@Override
					public Collection<String> getValueForObject(LocationGWT object) {
						Collection<String> result = new LinkedList<String>();
						if (object.getEquipment().hasLaptopConnectivity)
							result.add(LAPTOP_CONNECTIVITY);
						if (object.getEquipment().hasOverhead)
							result.add(OVERHEAD);
						if (object.getEquipment().isSmartRoom)
							result.add(SMART_ROOM);
						return result;
					}
				},
				new StaticSetter<LocationGWT, Collection<String>>() {
					public void setValueInObject(LocationGWT object, java.util.Collection<String> newValue) {
						object.getEquipment().hasLaptopConnectivity = newValue.contains(LAPTOP_CONNECTIVITY);
						object.getEquipment().hasOverhead = newValue.contains(OVERHEAD);
						object.getEquipment().isSmartRoom = newValue.contains(SMART_ROOM);
					}
				},
				null));
		
		table.addDeleteColumn();
		
		vp.add(table);
		
		service.getLocations(new AsyncCallback<Collection<LocationGWT>>() {
			public void onFailure(Throwable caught) {
				popup.hide();
				Window.alert("Failed to get courses: " + caught.toString());
			}
			
			public void onSuccess(Collection<LocationGWT> result){
				assert(result != null);
				popup.hide();
				for (LocationGWT location : result)
					nextLocationID = Math.max(nextLocationID, location.getID() + 1);
				table.addRows(result);
			}
		});
		
	}
	
	private void save() {
		service.saveLocations(table.getAddedUntouchedAndEditedObjects(), new AsyncCallback<Collection<LocationGWT>>() {
			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub
				
			}
			@Override
			public void onSuccess(Collection<LocationGWT> result) {
				table.clear();
				table.addRows(result);
			}
		});
	}
	
	LocationGWT locationExists(String building, String room) {
		for (LocationGWT location : table.getAddedUntouchedAndEditedObjects())
			if (location.getBuilding().equals(building) && location.getRoom().equals(room))
				return location;
		return null;
	}
	
	public static boolean isSaved(){
		if(table == null){ return true; }
		return table.isSaved();
	}
	
	public static void clearChanges(){
		if(table != null){
			table.clearChanges();
		}
	}
}
