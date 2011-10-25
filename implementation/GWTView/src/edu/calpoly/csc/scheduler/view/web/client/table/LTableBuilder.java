package edu.calpoly.csc.scheduler.view.web.client.table;

import java.util.ArrayList;
import java.util.Comparator;

import com.google.gwt.cell.client.EditTextCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

import edu.calpoly.csc.scheduler.view.web.client.GreetingService;
import edu.calpoly.csc.scheduler.view.web.client.GreetingServiceAsync;
import edu.calpoly.csc.scheduler.view.web.shared.LocationGWT;

public class LTableBuilder implements TableBuilder<LocationGWT>{

	private static final GreetingServiceAsync service = GWT
			.create(GreetingService.class);
	
	@Override
	public ArrayList<ColumnObject<LocationGWT>> getColumns(
			ListHandler<LocationGWT> sortHandler) {
		
		ArrayList<ColumnObject<LocationGWT>> list = 
				new ArrayList<ColumnObject<LocationGWT>>();
		
		// building		    
		Column<LocationGWT, String> building = 
				new Column<LocationGWT, String>(new EditTextCell()) {
		      @Override
		      public String getValue(LocationGWT location) {
		        return location.getBuilding();
		      }
		};
		sortHandler.setComparator(building, new Comparator<LocationGWT>() {
	        public int compare(LocationGWT o1, LocationGWT o2) {
	          return o1.getBuilding().compareTo(o2.getBuilding());
	        }
	    });
		building.setFieldUpdater(new FieldUpdater<LocationGWT, String>() {
		      public void update(int index, LocationGWT object, String value) {
		        object.setBuilding(value);
		      }
		});
		list.add(new ColumnObject<LocationGWT>(building, TableConstants.LOC_BUILDING));

		return list;
	}

	@Override
	public String getLabel(LocationGWT object) {
		return object.getBuilding() + " - " + object.getRoom();
	}

	@Override
	public LocationGWT newObject() {
		return new LocationGWT();
	}

	@Override
	public void save(ArrayList<LocationGWT> list) {
		
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
