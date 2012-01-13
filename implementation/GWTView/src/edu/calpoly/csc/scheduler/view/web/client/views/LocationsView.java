package edu.calpoly.csc.scheduler.view.web.client.views;

import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import edu.calpoly.csc.scheduler.view.web.client.GreetingServiceAsync;
import edu.calpoly.csc.scheduler.view.web.client.IViewContents;
import edu.calpoly.csc.scheduler.view.web.client.ViewFrame;
import edu.calpoly.csc.scheduler.view.web.client.table.IFactory;
import edu.calpoly.csc.scheduler.view.web.client.table.IStaticGetter;
import edu.calpoly.csc.scheduler.view.web.client.table.IStaticSetter;
import edu.calpoly.csc.scheduler.view.web.client.table.IStaticValidator;
import edu.calpoly.csc.scheduler.view.web.client.table.OsmTable;
import edu.calpoly.csc.scheduler.view.web.client.table.columns.EditingCheckboxColumn;
import edu.calpoly.csc.scheduler.view.web.client.table.columns.EditingMultiselectColumn;
import edu.calpoly.csc.scheduler.view.web.client.table.columns.EditingSelectColumn;
import edu.calpoly.csc.scheduler.view.web.client.table.columns.EditingStringColumn;
import edu.calpoly.csc.scheduler.view.web.client.table.columns.IntColumn;
import edu.calpoly.csc.scheduler.view.web.shared.LocationGWT;

public class LocationsView extends VerticalPanel implements IViewContents {
	/** Location table */
	public static final String LOC_BUILDING = "Building";
	
	public static final String LOC_SMARTROOM = "Smartroom";
	
	public static final String LOC_LAPTOPCONNECTIVITY = "Laptop Connectivity";
	
	public static final String LOC_ADACOMPLIANT = "ADA Compliant";
	
	public static final String LOC_OVERHEAD = "Overhead";
	
	public static final String LOC_NAME = "Name";
	
	public static final String LOC_ROOM = "Room";
	
	public static final String LOC_TYPE = "Type";
	
	public static final String LOC_MAX_OCCUPANCY = "Max Occupancy";
	
	public static final String LOC_EQIPMENT_LIST = "Equipment List";
		
	public static final String LOC_ADDITIONAL_DETAILS = "Additional Details";
	
	private static final String LAPTOP_CONNECTIVITY = "LAPCON";
	private static final String OVERHEAD = "OVERHEAD";
	private static final String SMART_ROOM = "SMART";
	
	private GreetingServiceAsync service;
	private final String scheduleName;
	int nextLocationID = 1;
	private static OsmTable<LocationGWT> table;

	public LocationsView(GreetingServiceAsync service, String scheduleName) {
		this.service = service;
		this.scheduleName = scheduleName;
		this.addStyleName("iViewPadding");
	}

	@Override
	public boolean canPop() {
		assert(table != null);
		if (table.isSaved())
			return true;
		return Window.confirm("You have unsaved data which will be lost. Are you sure you want to navigate away?");
	}
	
	@Override
	public void afterPush(ViewFrame frame) {
		this.setWidth("100%");
		this.setHeight("100%");

		this.add(new HTML("<h2>" + scheduleName + " - Locations</h2>"));

		final LoadingPopup popup = new LoadingPopup();
		popup.show();

		table = new OsmTable<LocationGWT>(
				new IFactory<LocationGWT>() {
					public LocationGWT create() {
						return new LocationGWT(nextLocationID++, "", "", "LEC", 20, false, new LocationGWT.ProvidedEquipmentGWT());
					}
				},
				new OsmTable.ModifyHandler<LocationGWT>() {
					@Override
					public void add(LocationGWT toAdd, AsyncCallback<Integer> callback) {
						service.addLocation(toAdd, callback);
					}

					@Override
					public void edit(LocationGWT toEdit, AsyncCallback<Void> callback) {
						service.editLocation(toEdit, callback);
					}

					@Override
					public void remove(LocationGWT toRemove, AsyncCallback<Void> callback) {
						service.removeLocation(toRemove, callback);
					}
				});

		table.addEditSaveColumn();
		table.addDeleteColumn();

		table.addColumn(
				"Building",
				null,
				true,
				new Comparator<LocationGWT>() {
					public int compare(LocationGWT o1, LocationGWT o2) {
						return o1.getBuilding().compareTo(o2.getBuilding());
					}
				},
				new EditingStringColumn<LocationGWT>(
						new IStaticGetter<LocationGWT, String>() {
							public String getValueForObject(LocationGWT object) { return object.getBuilding(); }
						},
						new IStaticSetter<LocationGWT, String>() {
							public void setValueInObject(LocationGWT object, String newValue) { object.setBuilding(newValue); }
						},
						new IStaticValidator<LocationGWT, String>() {
							public void validate(LocationGWT object, String newBuilding) throws InvalidValueException {
								LocationGWT locationAtPlace = locationExists(newBuilding, object.getRoom());
								if (locationAtPlace != null && locationAtPlace != object)
									throw new InvalidValueException("Location " + newBuilding + "-" + object.getRoom() + " already exists.");
							}
						}));

		table.addColumn(
				"Room",
				null,
				true,
				new Comparator<LocationGWT>() {
					@Override
					public int compare(LocationGWT o1, LocationGWT o2) {
						return o1.getRoom().compareToIgnoreCase(o2.getRoom());
					}
				},
				new EditingStringColumn<LocationGWT>(
						new IStaticGetter<LocationGWT, String>() {
							public String getValueForObject(LocationGWT object) { return object.getRoom(); }
						},
						new IStaticSetter<LocationGWT, String>() {
							public void setValueInObject(LocationGWT object, String newValue) { object.setRoom(newValue); }
						},
						new IStaticValidator<LocationGWT, String>() {
							public void validate(LocationGWT object, String newRoom) throws InvalidValueException {
								LocationGWT locationAtPlace = locationExists(object.getBuilding(), newRoom);
								if (locationAtPlace != null && locationAtPlace != object)
									throw new InvalidValueException("Location " + object.getBuilding() + "-" + newRoom + " already exists.");
							}
						}));
		
		table.addColumn(
				"Type",
				null,
				true,
				new Comparator<LocationGWT>() {
					@Override
					public int compare(LocationGWT o1, LocationGWT o2) {
						return o1.getType().compareToIgnoreCase(o2.getType());
					}
				},
				new EditingSelectColumn<LocationGWT>(
						new String[] { "LEC", "LAB" },
						new IStaticGetter<LocationGWT, String>() {
							public String getValueForObject(LocationGWT object) { return object.getType(); }
						},
						new IStaticSetter<LocationGWT, String>() {
							public void setValueInObject(LocationGWT object, String newValue) { object.setType(newValue); }
						}));

		table.addColumn(
				"Occupancy",
				null,
				true,
				new Comparator<LocationGWT>() {
					@Override
					public int compare(LocationGWT o1, LocationGWT o2) {
						return o1.getMaxOccupancy() - o2.getMaxOccupancy();
					}
				},
				new IntColumn<LocationGWT>(
						new IStaticGetter<LocationGWT, Integer>() {
							public Integer getValueForObject(LocationGWT object) { return object.getMaxOccupancy(); }
						},
						new IStaticSetter<LocationGWT, Integer>() {
							public void setValueInObject(LocationGWT object, Integer newValue) { object.setMaxOccupancy(newValue); }
						},
						new IStaticValidator<LocationGWT, Integer>() {
							public void validate(LocationGWT object, Integer newMaxOcc) throws InvalidValueException {
								if (newMaxOcc < 0)
									throw new InvalidValueException(LOC_MAX_OCCUPANCY + " must be a positive: " + newMaxOcc + " is invalid.");
							}
						}));
		
		table.addColumn(
				"ADA",
				null,
				true,
				new Comparator<LocationGWT>() {
					@Override
					public int compare(LocationGWT o1, LocationGWT o2) {
						return (o1.isADACompliant() ? 1 : 0) - (o2.isADACompliant() ? 1 : 0);
					}
				},
				new EditingCheckboxColumn<LocationGWT>(
						new IStaticGetter<LocationGWT, Boolean>() {
							public Boolean getValueForObject(LocationGWT object) { return object.isADACompliant(); }
						},
						new IStaticSetter<LocationGWT, Boolean>() {
							public void setValueInObject(LocationGWT object, Boolean newValue) { object.setADACompliant(newValue); }
						}));
		
		LinkedHashMap<String, String> valuesByLabel = new LinkedHashMap<String, String>();
		valuesByLabel.put("Laptop Connectivity", LAPTOP_CONNECTIVITY);
		valuesByLabel.put("Overhead", OVERHEAD);
		valuesByLabel.put("Smart Room", SMART_ROOM);
		table.addColumn(
				"Equipment",
				null,
				true,
				null,
				new EditingMultiselectColumn<LocationGWT>(
						valuesByLabel,
						new IStaticGetter<LocationGWT, Set<String>>() {
							@Override
							public Set<String> getValueForObject(LocationGWT object) {
								Set<String> result = new HashSet<String>();
								if (object.getEquipment().hasLaptopConnectivity)
									result.add(LAPTOP_CONNECTIVITY);
								if (object.getEquipment().hasOverhead)
									result.add(OVERHEAD);
								if (object.getEquipment().isSmartRoom)
									result.add(SMART_ROOM);
								return result;
							}
						},
						new IStaticSetter<LocationGWT, Set<String>>() {
							public void setValueInObject(LocationGWT object, Set<String> newValue) {
								object.getEquipment().hasLaptopConnectivity = newValue.contains(LAPTOP_CONNECTIVITY);
								object.getEquipment().hasOverhead = newValue.contains(OVERHEAD);
								object.getEquipment().isSmartRoom = newValue.contains(SMART_ROOM);
							}
						}));
		
		this.add(table);
		
		service.getLocations(new AsyncCallback<List<LocationGWT>>() {
			public void onFailure(Throwable caught) {
				popup.hide();
				Window.alert("Failed to get courses: " + caught.toString());
			}
			
			public void onSuccess(List<LocationGWT> result){
				assert(result != null);
				popup.hide();
				for (LocationGWT location : result)
					nextLocationID = Math.max(nextLocationID, location.getID() + 1);
				table.addRows(result);
			}
		});
		
	}
	
	LocationGWT locationExists(String building, String room) {
		for (LocationGWT location : table.getObjects())
			if (location.getBuilding().equals(building) && location.getRoom().equals(room))
				return location;
		return null;
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
