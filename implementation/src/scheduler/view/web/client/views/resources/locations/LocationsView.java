package scheduler.view.web.client.views.resources.locations;

import scheduler.view.web.client.GreetingServiceAsync;
import scheduler.view.web.client.UnsavedDocumentStrategy;
import scheduler.view.web.shared.DocumentGWT;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.Autofit;
import com.smartgwt.client.types.ListGridEditEvent;
import com.smartgwt.client.types.RowEndEditAction;
import com.smartgwt.client.widgets.events.KeyPressEvent;
import com.smartgwt.client.widgets.events.KeyPressHandler;
import com.smartgwt.client.widgets.grid.CellFormatter;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;

public class LocationsView extends VerticalPanel {
	public LocationsView(final GreetingServiceAsync service, final DocumentGWT document, UnsavedDocumentStrategy unsavedDocumentStrategy) {
		this.setWidth("100%");
		this.setHeight("100%");
		this.setHorizontalAlignment(ALIGN_CENTER);
//		this.add(new HTML("<h2>Locations</h2>"));
		
		final ListGrid grid = new ListGrid();
		grid.setWidth("98%");
		grid.setAutoFitData(Autofit.VERTICAL);
		grid.setShowAllRecords(true);
		grid.setAutoFetchData(true);
		grid.setCanEdit(true);
		grid.setEditEvent(ListGridEditEvent.CLICK);
		grid.setEditByCell(true);
		grid.setListEndEditAction(RowEndEditAction.NEXT);
		//grid.setCellHeight(22);
		grid.setDataSource(new LocationsDataSource(service, document, unsavedDocumentStrategy));

		ListGridField idField = new ListGridField("id", "&nbsp;");
		idField.setCanEdit(false);
		idField.setCellFormatter(new CellFormatter() {
			public String format(Object value, ListGridRecord record, int rowNum, int colNum) {
				return "\u22EE";
			}
		});
		idField.setWidth(20);
		idField.setAlign(Alignment.CENTER);
		
		ListGridField schedulableField = new ListGridField("isSchedulable", "Schedulable");
		schedulableField.setDefaultValue(true);
		schedulableField.setAlign(Alignment.CENTER);
		ListGridField roomField = new ListGridField("room", "Room");
		roomField.setAlign(Alignment.CENTER);
		ListGridField typeField = new ListGridField("type", "Type");
		typeField.setAlign(Alignment.CENTER);
		ListGridField maxOccupancyField = new ListGridField("maxOccupancy", "Max Occupancy");
		maxOccupancyField.setAlign(Alignment.CENTER);
		ListGridField equipmentField = new ListGridField("equipment", "Equipment");
		equipmentField.setAlign(Alignment.CENTER);

		grid.setFields(idField, schedulableField, roomField, typeField, maxOccupancyField, equipmentField);
		
		this.add(grid);
		this.setHorizontalAlignment(ALIGN_DEFAULT);
		this.add(new Button("Add New Location", new ClickHandler() {
			public void onClick(ClickEvent event) {
            grid.startEditingNew();
			}
		}));

		this.add(new Button("Duplicate Selected Locations", new com.google.gwt.event.dom.client.ClickHandler() {
			public void onClick(com.google.gwt.event.dom.client.ClickEvent event) {
            ListGridRecord[] selectedRecords = grid.getSelectedRecords();  
            for(ListGridRecord rec: selectedRecords) {
					rec.setAttribute("id", (Integer)null);
					grid.startEditingNew(rec);
            }
			}
		}));
		
		this.add(new Button("Remove Selected Locations", new ClickHandler() {
			public void onClick(ClickEvent event) {
            ListGridRecord[] selectedRecords = grid.getSelectedRecords();  
            for(ListGridRecord rec: selectedRecords) {  
                grid.removeData(rec);  
            }
			}
		}));

		grid.addKeyPressHandler(new KeyPressHandler() {
			public void onKeyPress(KeyPressEvent event) {
				if (event.getKeyName().equals("Backspace") || event.getKeyName().equals("Delete"))
					if (Window.confirm("Are you sure you want to remove this location?"))
						grid.removeSelectedData();
			}
		});
		
	}
}