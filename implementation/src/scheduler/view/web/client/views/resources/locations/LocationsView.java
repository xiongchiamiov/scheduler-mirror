package scheduler.view.web.client.views.resources.locations;

import scheduler.view.web.client.GreetingServiceAsync;
import scheduler.view.web.client.UnsavedDocumentStrategy;
import scheduler.view.web.shared.DocumentGWT;

import com.google.gwt.user.client.Window;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.Autofit;
import com.smartgwt.client.types.ListGridEditEvent;
import com.smartgwt.client.types.RowEndEditAction;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.events.KeyPressEvent;
import com.smartgwt.client.widgets.events.KeyPressHandler;
import com.smartgwt.client.widgets.grid.CellFormatter;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;

public class LocationsView extends VLayout {
	public LocationsView(final GreetingServiceAsync service,
			final DocumentGWT document,
			UnsavedDocumentStrategy unsavedDocumentStrategy) {
		setID("s_locationviewTab");
		this.setWidth100();
		this.setHeight100();
//		this.setHorizontalAlignment(ALIGN_CENTER);
		// this.add(new HTML("<h2>Locations</h2>"));

		final ListGrid grid = new ListGrid();
		grid.setWidth("98%");
		grid.setAutoFitData(Autofit.VERTICAL);
		grid.setShowAllRecords(true);
		grid.setAutoFetchData(true);
		grid.setCanEdit(true);
		grid.setEditEvent(ListGridEditEvent.CLICK);
		grid.setEditByCell(true);
		grid.setListEndEditAction(RowEndEditAction.NEXT);
		// grid.setCellHeight(22);
		grid.setDataSource(new LocationsDataSource(service, document,
				unsavedDocumentStrategy));

		ListGridField idField = new ListGridField("id", "&nbsp;");
		idField.setCanEdit(false);
		idField.setCellFormatter(new CellFormatter() {
			public String format(Object value, ListGridRecord record,
					int rowNum, int colNum) {
				return "\u22EE";
			}
		});
		idField.setWidth(20);
		idField.setAlign(Alignment.CENTER);

		ListGridField schedulableField = new ListGridField("isSchedulable",
				"Schedulable");
		schedulableField.setDefaultValue(true);
		schedulableField.setAlign(Alignment.CENTER);
		ListGridField roomField = new ListGridField("room", "Room");
		roomField.setAlign(Alignment.CENTER);
		ListGridField typeField = new ListGridField("type", "Type");
		typeField.setAlign(Alignment.CENTER);
		ListGridField maxOccupancyField = new ListGridField("maxOccupancy",
				"Max Occupancy");
		maxOccupancyField.setAlign(Alignment.CENTER);
		ListGridField equipmentField = new ListGridField("equipment",
				"Equipment");
		equipmentField.setAlign(Alignment.CENTER);

		grid.setFields(idField, schedulableField, roomField, typeField,
				maxOccupancyField, equipmentField);

		this.addMember(grid);
//		this.setHorizontalAlignment(ALIGN_DEFAULT);

		grid.addKeyPressHandler(new KeyPressHandler() {
			public void onKeyPress(KeyPressEvent event) {
				if (event.getKeyName().equals("Backspace")
						|| event.getKeyName().equals("Delete"))
					if (Window
							.confirm("Are you sure you want to remove this location?"))
						grid.removeSelectedData();
			}
		});
		layoutBottomButtonBar(grid);
	}

	/**
	 * Lays out the buttons which will appear on this widget
	 */
	private void layoutBottomButtonBar(final ListGrid grid) {
		HLayout bottomButtonFlowPanel = new HLayout();
//		bottomButtonFlowPanel.addStyleName("floatingScheduleButtonBar");

		IButton newButton = new IButton("Add New Location", new ClickHandler() {
			public void onClick(ClickEvent event) {
				grid.startEditingNew();
			}
		});
//		newButton.getElement().setId("addLocationButton");
		// DOM.setElementAttribute(course.getElement(), "id", "s_newCourseBtn");
//		newButton.setStyleName("floatingScheduleButtonBarItemLeft");
		newButton.setID("addLocationButton");
		bottomButtonFlowPanel.addMember(newButton);

		IButton dupeBtn = new IButton("Duplicate Selected Locations",
				new ClickHandler() {
					public void onClick(ClickEvent event) {
						ListGridRecord[] selectedRecords = grid
								.getSelectedRecords();
						for (ListGridRecord rec : selectedRecords) {
							rec.setAttribute("id", (Integer) null);
							grid.startEditingNew(rec);
						}
					}
				});
		// DOM.setElementAttribute(dupeBtn.getElement(), "id", "s_dupeBtn");
//		dupeBtn.setStyleName("floatingScheduleButtonBarItemLeft");
		dupeBtn.setID("s_dupeBtn");
		bottomButtonFlowPanel.addMember(dupeBtn);

		IButton remove = new IButton("Remove Selected Locations", new ClickHandler() {
			public void onClick(ClickEvent event) {
				ListGridRecord[] selectedRecords = grid.getSelectedRecords();
				for (ListGridRecord rec : selectedRecords) {
					grid.removeData(rec);
				}
			}
		});
//		DOM.setElementAttribute(remove.getElement(), "id", "s_removeBtn");
//		remove.setStyleName("floatingScheduleButtonBarItemLeft");
		remove.setID("s_removeBtn");
		bottomButtonFlowPanel.addMember(remove);

		this.addMember(bottomButtonFlowPanel);
	}
}