package edu.calpoly.csc.scheduler.view.web.client.table;

import java.util.ArrayList;
import java.util.Comparator;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.cell.client.EditTextCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.SelectionCell;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;

import edu.calpoly.csc.scheduler.view.web.client.GreetingServiceAsync;
import edu.calpoly.csc.scheduler.view.web.shared.LocationGWT;

public class LTableBuilder implements TableBuilder<LocationGWT>{
	private GreetingServiceAsync service;
	
	public LTableBuilder(GreetingServiceAsync service) {
		this.service = service;
	}
	
	@Override
	public ArrayList<ColumnObject<LocationGWT>> getColumns(Widget hidden,
			ListDataProvider<LocationGWT> dataProvider, ListHandler<LocationGWT> sortHandler) {
		
		final ListDataProvider<LocationGWT> fdataProvider = dataProvider;
		
		ArrayList<ColumnObject<LocationGWT>> list = 
				new ArrayList<ColumnObject<LocationGWT>>();
		
		// building		    
		Column<LocationGWT, String> building = 
				new Column<LocationGWT, String>(new EditTextCell()) {
		      @Override
		      public String getValue(LocationGWT location) {
		        return location.getBuilding();
		      }
		      
		      @Override
		      public void onBrowserEvent(Cell.Context context, Element elem, LocationGWT object, NativeEvent event){
					super.onBrowserEvent(context, elem, object, event);
					
					TableValidate.validateLoc(true, fdataProvider, context, elem, object, event);
		      }
		};
		sortHandler.setComparator(building, new Comparator<LocationGWT>() {
	        public int compare(LocationGWT o1, LocationGWT o2) {
	          return o1.getBuilding().compareTo(o2.getBuilding());
	        }
	    });
		building.setCellStyleNames("tableColumnWidthInt");
		list.add(new ColumnObject<LocationGWT>(building, TableConstants.LOC_BUILDING));
		
		
		// room		   
		Column<LocationGWT, String> room = 
				new Column<LocationGWT, String>(new EditTextCell()) {
		      @Override
		      public String getValue(LocationGWT location) {
		        return location.getRoom();
		      }
		      
		      @Override
		      public void onBrowserEvent(Cell.Context context, Element elem, LocationGWT object, NativeEvent event){
					super.onBrowserEvent(context, elem, object, event);
					
					TableValidate.validateLoc(false, fdataProvider, context, elem, object, event);
		      }
		};
		sortHandler.setComparator(room, new Comparator<LocationGWT>() {
	        public int compare(LocationGWT o1, LocationGWT o2) {
	          return o1.getRoom().compareTo(o2.getRoom());
	        }
	    });
		room.setCellStyleNames("tableColumnWidthInt");
		list.add(new ColumnObject<LocationGWT>(room, TableConstants.LOC_ROOM));

		
		// type		    
		ArrayList<String> typeOptions = new ArrayList<String>();
		typeOptions.add(TableConstants.LEC);
		typeOptions.add(TableConstants.LAB);
		SelectionCell selectionCellType = new SelectionCell(typeOptions);
		Column<LocationGWT, String> type = 
				new Column<LocationGWT, String>(selectionCellType) {
		      @Override
		      public String getValue(LocationGWT location) {
		        return location.getType();
		      }
		};
		sortHandler.setComparator(type, new Comparator<LocationGWT>() {
	        public int compare(LocationGWT o1, LocationGWT o2) {
	          return o1.getType().compareTo(o2.getType());
	        }
	    });
		type.setFieldUpdater(new FieldUpdater<LocationGWT, String>() {
		      public void update(int index, LocationGWT object, String value) {
		        object.setType(value);
		      }
		});
		type.setCellStyleNames("tableColumnWidthString");
		list.add(new ColumnObject<LocationGWT>(type, TableConstants.LOC_TYPE));
		
		
		// max occupancy		    
		Column<LocationGWT, String> maxOcc = 
				new Column<LocationGWT, String>(TableValidate.intValidateCell(TableConstants.LOC_MAX_OCCUPANCY, true)) {
		      @Override
		      public String getValue(LocationGWT course) {
		        return "" + course.getMaxOccupancy();
		      }
		};
		sortHandler.setComparator(maxOcc, new Comparator<LocationGWT>() {
	        public int compare(LocationGWT o1, LocationGWT o2) {
	          return o1.getMaxOccupancy() - o2.getMaxOccupancy();
	        }
	    });
		maxOcc.setFieldUpdater(new FieldUpdater<LocationGWT, String>() {
		      public void update(int index, LocationGWT object, String value) {
		    	  object.setMaxOccupancy(TableValidate.positiveInt(value, true));
		      }
		});
		maxOcc.setCellStyleNames("tableColumnWidthInt");
		list.add(new ColumnObject<LocationGWT>(maxOcc, TableConstants.LOC_MAX_OCCUPANCY));
		
		
		// ada compliant
		Column<LocationGWT, Boolean> adaCompliant = 
				new Column<LocationGWT, Boolean>(new CheckboxCell(true, false)) {
		      @Override
		      public Boolean getValue(LocationGWT loc) {
		        return loc.isADACompliant();
		      }
		};
		sortHandler.setComparator(adaCompliant, new Comparator<LocationGWT>() {
	        public int compare(LocationGWT o1, LocationGWT o2) {
	          
	        	boolean b1 = o1.isADACompliant();
	        	boolean b2 = o2.isADACompliant();
	        	if(b1 == b2){ return 0;}
	        	if(b2){ return -1;}
	        	return 1;
	        }
	    });
		adaCompliant.setFieldUpdater(new FieldUpdater<LocationGWT, Boolean>() {
		      public void update(int index, LocationGWT object, Boolean value) {
		        object.setADACompliant(value);
		      }
		});
		adaCompliant.setCellStyleNames("tableColumnWidthInt");
		list.add(new ColumnObject<LocationGWT>(adaCompliant, TableConstants.LOC_ADACOMPLIANT));
		
		
		
		return list;
	}

	@Override
	public String getLabel(LocationGWT object) {
		return object.getBuilding() + " - " + object.getRoom();
	}

	@Override
	public LocationGWT newObject() {
		LocationGWT loc = new LocationGWT();
		loc.setAvailability("derp?");
		loc.setQuarterID("qid?");
		return loc;
	}

	@Override
	public void save(ArrayList<LocationGWT> list) {
		try {
			
			boolean valid = true;
			for (LocationGWT loc : list){
			
				loc.verify();
				if(loc.getBuilding().trim().equals("") ||
						loc.getRoom().trim().equals("")){
					valid = false;
				}
			}
			
			if(!valid){
				Window.alert("Building and Room cannot be empty");
			}
			else{
			service.saveLocations(list, new AsyncCallback<Void>(){
				public void onFailure(Throwable caught){ 
					
					Window.alert("Error saving:\n" + 
							caught.getLocalizedMessage());
				}
				public void onSuccess(Void result){
					Window.alert("Successfully saved");
				}
			});
			}
		}
		catch (RuntimeException e) {
			Window.alert("exception: " + e.getClass().getName() + " " + e.getMessage());
		}
	}
}
