package scheduler.view.web.client.views.resources.locations;

import java.util.List;

import scheduler.view.web.client.GreetingServiceAsync;
import scheduler.view.web.client.IViewContents;
import scheduler.view.web.client.ViewFrame;
import scheduler.view.web.shared.DocumentGWT;
import scheduler.view.web.shared.LocationGWT;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.smartgwt.client.types.ListGridEditEvent;
import com.smartgwt.client.types.RowEndEditAction;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;

public class LocationsView extends VerticalPanel implements IViewContents {
	private GreetingServiceAsync service;
	private final DocumentGWT document;
	private ViewFrame frame;
	
	public LocationsView(GreetingServiceAsync service, DocumentGWT document) {
		this.service = service;
		this.document = document;
		// this.addStyleName("iViewPadding");
	}
	
	@Override
	public boolean canPop() {
		return true;
		// assert(table != null);
		// if (table.isSaved())
		// return true;
		// return
		// Window.confirm("You have unsaved data which will be lost. Are you sure you want to navigate away?");
	}
	
	@Override
	public void afterPush(ViewFrame frame) {
		this.frame = frame;
		
		this.setWidth("100%");
		this.setHeight("100%");
		
		this.add(new HTML("<h2>Locations</h2>"));
		
		final ListGrid grid = new ListGrid();
		grid.setWidth("100%");
		grid.setHeight(300);
		grid.setShowAllRecords(true);
		grid.setAutoFetchData(true);
		grid.setCanEdit(true);
		grid.setEditEvent(ListGridEditEvent.CLICK);
		grid.setEditByCell(true);
		grid.setListEndEditAction(RowEndEditAction.NEXT);
		//grid.setCellHeight(22);
		grid.setDataSource(new LocationsDataSource(service, document));
		
		ListGridField idField = new ListGridField("id");
		idField.setHidden(true);

		ListGridField usernameField = new ListGridField("room", "Room");
		ListGridField firstNameField = new ListGridField("type", "Type");
		ListGridField lastNameField = new ListGridField("maxOccupancy", "Max Occupancy");
		ListGridField maxWTUField = new ListGridField("equipment", "Equipment");

		grid.setFields(idField, usernameField, firstNameField, lastNameField, maxWTUField);
		
		this.add(grid);
		
		this.add(new Button("Add New Location", new ClickHandler() {
			public void onClick(ClickEvent event) {
            grid.startEditingNew();
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