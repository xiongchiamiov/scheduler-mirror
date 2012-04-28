package scheduler.view.web.client.views.resources.locations;

import scheduler.view.web.client.GreetingServiceAsync;
import scheduler.view.web.client.UnsavedDocumentStrategy;
import scheduler.view.web.shared.DocumentGWT;

import com.google.gwt.user.client.Window;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.Autofit;
import com.smartgwt.client.types.ListGridEditEvent;
import com.smartgwt.client.types.Overflow;
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
		// this.setHorizontalAlignment(ALIGN_CENTER);
		// this.add(new HTML("<h2>Locations</h2>"));

		final ListGrid grid = new ListGrid() {
			protected String getCellCSSText(ListGridRecord record, int rowNum,
					int colNum) {
				if (getFieldName(colNum).equals("id")) {
					return "cursor: pointer; background: #C0C0C0;";
				} else {
					return super.getCellCSSText(record, rowNum, colNum);
				}
			}
		};
		grid.setWidth100();
		grid.setAutoFitData(Autofit.VERTICAL);
		grid.setShowAllRecords(true);
		grid.setAutoFetchData(true);
		grid.setCanEdit(true);
		grid.setEditEvent(ListGridEditEvent.CLICK);
		grid.setEditByCell(true);
		grid.setListEndEditAction(RowEndEditAction.NEXT);
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
		typeField.setDefaultValue("LEC");
		ListGridField maxOccupancyField = new ListGridField("maxOccupancy",
				"Max Occupancy");
		maxOccupancyField.setDefaultValue(0);
		maxOccupancyField.setAlign(Alignment.CENTER);
		ListGridField equipmentField = new ListGridField("equipment",
				"Equipment");
		equipmentField.setAlign(Alignment.CENTER);

		grid.setFields(idField, schedulableField, roomField, typeField,
				maxOccupancyField, equipmentField);

		this.addMember(grid);
		// this.setHorizontalAlignment(ALIGN_DEFAULT);

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
		bottomButtonFlowPanel.setMembersMargin(10);
		bottomButtonFlowPanel.setStyleName("floatingScheduleButtonBar");

		IButton newButton = new IButton("Add New Location", new ClickHandler() {
			public void onClick(ClickEvent event) {
				grid.startEditingNew();
			}
		});
		// newButton.getElement().setId("addLocationButton");
		// DOM.setElementAttribute(course.getElement(), "id", "s_newCourseBtn");
		newButton.setAutoWidth();
		newButton.setOverflow(Overflow.VISIBLE);
		//DON'T CHANGE THIS ID IT WILL BREAK THE BUTTONS
		newButton.setID("s_newLocationBtn");
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
		dupeBtn.setAutoWidth();
		dupeBtn.setOverflow(Overflow.VISIBLE);
		//DON'T CHANGE THIS ID IT WILL BREAK THE BUTTONS
		dupeBtn.setID("s_dupeLocationBtn");
		bottomButtonFlowPanel.addMember(dupeBtn);

		IButton remove = new IButton("Remove Selected Locations",
				new ClickHandler() {
					public void onClick(ClickEvent event) {
						ListGridRecord[] selectedRecords = grid
								.getSelectedRecords();
						for (ListGridRecord rec : selectedRecords) {
							grid.removeData(rec);
						}
					}
				});
		// DOM.setElementAttribute(remove.getElement(), "id", "s_removeBtn");

		remove.setAutoWidth();
		remove.setOverflow(Overflow.VISIBLE);
		//DON'T CHANGE THIS ID IT WILL BREAK THE BUTTONS
		remove.setID("s_removeLocationBtn");

		bottomButtonFlowPanel.addMember(remove);

		this.addMember(bottomButtonFlowPanel);
	}
}