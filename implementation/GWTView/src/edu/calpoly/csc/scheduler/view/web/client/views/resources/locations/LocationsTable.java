package edu.calpoly.csc.scheduler.view.web.client.views.resources.locations;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.SimplePanel;

import edu.calpoly.csc.scheduler.view.web.client.table.IFactory;
import edu.calpoly.csc.scheduler.view.web.client.table.IStaticGetter;
import edu.calpoly.csc.scheduler.view.web.client.table.IStaticSetter;
import edu.calpoly.csc.scheduler.view.web.client.table.OsmTable;
import edu.calpoly.csc.scheduler.view.web.client.table.OsmTable.ObjectChangedObserver;
import edu.calpoly.csc.scheduler.view.web.client.table.columns.DeleteColumn.DeleteObserver;
import edu.calpoly.csc.scheduler.view.web.client.table.columns.EditingCheckboxColumn;
import edu.calpoly.csc.scheduler.view.web.client.table.columns.EditingMultiselectColumn;
import edu.calpoly.csc.scheduler.view.web.client.table.columns.EditingSelectColumn;
import edu.calpoly.csc.scheduler.view.web.shared.LocationGWT;

public class LocationsTable extends SimplePanel {
	private static final String LAPTOP_CONNECTIVITY = "LAPCON";
	private static final String OVERHEAD = "OVERHEAD";
	private static final String SMART_ROOM = "SMART";
	
	private static final String BUILDING_HEADER = "Building";
	private static final String BUILDING_WIDTH = null;
	
	private static final String ROOM_HEADER = "Room";
	private static final String ROOM_WIDTH = null;
	
	private static final String TYPE_HEADER = "Type";
	private static final String TYPE_WIDTH = null;
	
	private static final String MAX_OCCUPANCY_HEADER = "Max Occupancy";
	private static final String MAX_OCCUPANCY_WIDTH = "4em";
	
	private static final String DISABILITIES_HEADER = "Disabilities";
	private static final String DISABILITIES_WIDTH = "2em";
	
	public interface Strategy {
		void getAllLocations(AsyncCallback<List<LocationGWT>> callback);
		LocationGWT createLocation();
		void onLocationEdited(LocationGWT location);
		void onLocationDeleted(LocationGWT location);
	}
	
	final OsmTable<LocationGWT> table;
	final Strategy strategy;
	final ArrayList<LocationGWT> tableLocations = new ArrayList<LocationGWT>();
	
	public LocationsTable(Strategy strategy_) {
		this.strategy = strategy_;
		
		table = new OsmTable<LocationGWT>(
				new IFactory<LocationGWT>() {
					public LocationGWT create() {
						LocationGWT newLocation = strategy.createLocation();
						tableLocations.add(newLocation);
						return newLocation;
					}
				});
		
		table.setObjectChangedObserver(new ObjectChangedObserver<LocationGWT>() {
			public void objectChanged(final LocationGWT object) {
				strategy.onLocationEdited(object);
			}
		});

		table.addDeleteColumn(new DeleteObserver<LocationGWT>() {
			@Override
			public void afterDelete(LocationGWT object) {
				tableLocations.remove(object);
				strategy.onLocationDeleted(object);
			}
		});
		
		addFieldColumns();

		this.add(table);
	}

	@Override
	public void onLoad() {
		strategy.getAllLocations(new AsyncCallback<List<LocationGWT>>() {
			public void onFailure(Throwable caught) {
				Window.alert("Failed to get locations: " + caught.toString());
			}
			
			public void onSuccess(List<LocationGWT> locations){
				assert(tableLocations.isEmpty());
				for (LocationGWT location : locations)
					tableLocations.add(new LocationGWT(location));
				
				table.addRows(tableLocations);
			}
		});
	}
	
	void addFieldColumns() {

//		table.addColumn(
//				BUILDING_HEADER,
//				BUILDING_WIDTH,
//				true,
//				new Comparator<LocationGWT>() {
//					public int compare(LocationGWT o1, LocationGWT o2) {
//						return o1.getBuilding().compareTo(o2.getBuilding());
//					}
//				},
//				new EditingStringColumn<LocationGWT>(
//						new IStaticGetter<LocationGWT, String>() {
//							public String getValueForObject(LocationGWT object) { return object.getBuilding(); }
//						},
//						new IStaticSetter<LocationGWT, String>() {
//							public void setValueInObject(LocationGWT object, String newValue) { object.setBuilding(newValue); }
//						},
//						new IStaticValidator<LocationGWT, String>() {
//							public void validate(LocationGWT object, String newBuilding) throws InvalidValueException {
//								LocationGWT locationAtPlace = locationExists(newBuilding, object.getRoom());
//								if (locationAtPlace != null && locationAtPlace != object)
//									throw new InvalidValueException("Location " + newBuilding + "-" + object.getRoom() + " already exists.");
//							}
//						}));
//
//		table.addColumn(
//				ROOM_HEADER,
//				ROOM_WIDTH,
//				true,
//				new Comparator<LocationGWT>() {
//					@Override
//					public int compare(LocationGWT o1, LocationGWT o2) {
//						return o1.getRoom().compareToIgnoreCase(o2.getRoom());
//					}
//				},
//				new EditingStringColumn<LocationGWT>(
//						new IStaticGetter<LocationGWT, String>() {
//							public String getValueForObject(LocationGWT object) { return object.getRoom(); }
//						},
//						new IStaticSetter<LocationGWT, String>() {
//							public void setValueInObject(LocationGWT object, String newValue) { object.setRoom(newValue); }
//						},
//						new IStaticValidator<LocationGWT, String>() {
//							public void validate(LocationGWT object, String newRoom) throws InvalidValueException {
//								LocationGWT locationAtPlace = locationExists(object.getBuilding(), newRoom);
//								if (locationAtPlace != null && locationAtPlace != object)
//									throw new InvalidValueException("Location " + object.getBuilding() + "-" + newRoom + " already exists.");
//							}
//						}));
		
		table.addColumn(
				TYPE_HEADER,
				TYPE_WIDTH,
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

//		table.addColumn(
//				MAX_OCCUPANCY_HEADER,
//				MAX_OCCUPANCY_WIDTH,
//				true,
//				new Comparator<LocationGWT>() {
//					@Override
//					public int compare(LocationGWT o1, LocationGWT o2) {
//						return o1.getMaxOccupancy() - o2.getMaxOccupancy();
//					}
//				},
//				new EditingIntColumn<LocationGWT>(
//						new IStaticGetter<LocationGWT, Integer>() {
//							public Integer getValueForObject(LocationGWT object) { return object.getMaxOccupancy(); }
//						},
//						new IStaticSetter<LocationGWT, Integer>() {
//							public void setValueInObject(LocationGWT object, Integer newValue) { object.setMaxOccupancy(newValue); }
//						},
//						new IStaticValidator<LocationGWT, Integer>() {
//							public void validate(LocationGWT object, Integer newMaxOcc) throws InvalidValueException {
//								if (newMaxOcc < 0)
//									throw new InvalidValueException(MAX_OCCUPANCY_HEADER + " must be a positive: " + newMaxOcc + " is invalid.");
//							}
//						}));
		
		table.addColumn(
				DISABILITIES_HEADER,
				DISABILITIES_WIDTH,
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
	}
	
	LocationGWT locationExists(String building, String room) {
		for (LocationGWT location : table.getObjects())
			if (location.getBuilding().equals(building) && location.getRoom().equals(room))
				return location;
		return null;
	}

}
