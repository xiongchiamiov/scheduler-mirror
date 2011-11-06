package edu.calpoly.csc.scheduler.view.web.client.views;

import java.util.ArrayList;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.calpoly.csc.scheduler.view.web.client.GreetingServiceAsync;
import edu.calpoly.csc.scheduler.view.web.client.table.OsmTable;
import edu.calpoly.csc.scheduler.view.web.client.table.StringColumn;
import edu.calpoly.csc.scheduler.view.web.client.table.Table;
import edu.calpoly.csc.scheduler.view.web.client.table.TableFactory;
import edu.calpoly.csc.scheduler.view.web.shared.LocationGWT;

public class RoomsView extends ScrollPanel {
	private Panel container;
	private GreetingServiceAsync service;
	private Table<LocationGWT> lTable;
	private final String scheduleName;

	public RoomsView(Panel container, GreetingServiceAsync service, String scheduleName) {
		this.container = container;
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
		
		lTable = TableFactory.location(service);
		vp.add(lTable.getWidget());
		
		
		lTable.clear();

		final LoadingPopup popup = new LoadingPopup();
		popup.show();

//		final OsmTable<LocationGWT> table = new OsmTable<LocationGWT>(new OsmTable.IColumn<LocationGWT>[] {
//				new StringColumn<LocationGWT>("Building") {
//					public String getValue(LocationGWT object) {
//						return object.getBuilding();
//					}
//					public void setValue(LocationGWT object, String newValue) {
//						object.setBuilding(newValue);
//					}
//					public int compare(LocationGWT a, LocationGWT b) {
//						if (a.getBuilding().compareTo(b.getBuilding()) != 0)
//							return a.getBuilding().compareTo(b.getBuilding());
//						return a.getRoom().compareTo(b.getRoom());
//					}
//				}
//		});
		
//		vp.add(table);
		
		service.getLocations(new AsyncCallback<ArrayList<LocationGWT>>() {
			public void onFailure(Throwable caught) {
				popup.hide();
				Window.alert("Failed to get courses: " + caught.toString());
			}
			
			public void onSuccess(ArrayList<LocationGWT> result){
				popup.hide();
				if (result != null) {
					lTable.set(result);
				}
//				table.addRows(result);
			}
		});
		
	}
}
