package scheduler.view.web.client.views.resources.instructors;

import java.util.List;

import scheduler.view.web.client.GreetingServiceAsync;
import scheduler.view.web.client.UnsavedDocumentStrategy;
import scheduler.view.web.shared.DocumentGWT;
import scheduler.view.web.shared.InstructorGWT;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.Autofit;
import com.smartgwt.client.types.ListGridEditEvent;
import com.smartgwt.client.types.RowEndEditAction;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.events.KeyPressEvent;
import com.smartgwt.client.widgets.events.KeyPressHandler;
import com.smartgwt.client.widgets.form.validator.CustomValidator;
import com.smartgwt.client.widgets.form.validator.IntegerRangeValidator;
import com.smartgwt.client.widgets.grid.CellFormatter;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;

public class InstructorsView extends VerticalPanel {
	private GreetingServiceAsync service;
	private final DocumentGWT document;
//	private ViewFrame frame;
	
	public InstructorsView(final GreetingServiceAsync service, final DocumentGWT document, final UnsavedDocumentStrategy unsavedDocumentStrategy) {
		this.service = service;
		this.document = document;
		// this.addStyleName("iViewPadding");
		

		this.setWidth("100%");
		this.setHeight("100%");
		
		// this.add(new HTML("<h2>Instructors</h2>"));
		
		final ListGrid grid = new ListGrid() {
			@Override
			protected Canvas createRecordComponent(final ListGridRecord record, Integer colNum) {
				String fieldName = this.getFieldName(colNum);
				if (fieldName.equals("instructorPrefs")) {
					IButton button = new IButton();
					button.setHeight(18);
					button.setWidth(65);
					button.setTitle("Preferences");
					button.addClickHandler(new ClickHandler() {
						public void onClick(ClickEvent event) {
							final int instructorID = record.getAttributeAsInt("id");
							service.getInstructorsForDocument(document.getID(), new AsyncCallback<List<InstructorGWT>>() {
								public void onFailure(Throwable caught) {
									com.google.gwt.user.client.Window.alert("Failed to get instructors!");
								}
								public void onSuccess(List<InstructorGWT> result) {
									for (InstructorGWT instructor : result) {
										if (instructor.getID().equals(instructorID)) {
											preferencesButtonClicked(instructor, unsavedDocumentStrategy);
											break;
										}
									}
								}
							});
						}
					});
					return button;
				}
				else {
					return null;
				}
			}
		};
		
		grid.setWidth("100%");
		grid.setAutoFitData(Autofit.VERTICAL);
		grid.setShowAllRecords(true);
		grid.setAutoFetchData(true);
		grid.setCanEdit(true);
		grid.setEditEvent(ListGridEditEvent.CLICK);
		grid.setEditByCell(true);
		grid.setListEndEditAction(RowEndEditAction.NEXT);
		// grid.setCellHeight(22);
		grid.setDataSource(new InstructorsDataSource(service, document, unsavedDocumentStrategy));
		grid.setShowRecordComponents(true);
		grid.setShowRecordComponentsByCell(true);

		ListGridField idField = new ListGridField("id", "&nbsp;");
		idField.setCanEdit(false);
		idField.setCellFormatter(new CellFormatter() {
			public String format(Object value, ListGridRecord record, int rowNum, int colNum) {
				return "\u22EE";
			}
		});
		idField.setWidth(20);
		idField.setAlign(Alignment.CENTER);
		
		IntegerRangeValidator nonnegativeInt = new IntegerRangeValidator();  
		nonnegativeInt.setMin(0);  
		
		ListGridField schedulableField = new ListGridField("isSchedulable", "Schedulable");
		ListGridField lastNameField = new ListGridField("lastName", "Last Name");
		ListGridField firstNameField = new ListGridField("firstName", "First Name");
		ListGridField usernameField = new ListGridField("username", "Username");
		usernameField.setValidators(new CustomValidator() {
			protected boolean condition(Object value) {
				if (value == null) {
					setErrorMessage("Username must be present!");
					return false;
				}
				
				assert(value instanceof String);
				String username = (String)value;
				if (username.trim().length() == 0) {
					setErrorMessage("Username must be present!");
					return false;
				}
				
				for (Record record : grid.getDataAsRecordList().getRange(0, grid.getDataAsRecordList().getLength())) {
					if (username.equals(record.getAttribute("username"))) {
						setErrorMessage("Username \"" + username + "\" already exists!");
						return false;
					}
				}
				return true;
			}
		});
		
		ListGridField maxWTUField = new ListGridField("maxWTU", "Max WTU");
		maxWTUField.setValidators(nonnegativeInt);
		ListGridField instructorPrefsField = new ListGridField("instructorPrefs", "Preferences");
		instructorPrefsField.setAlign(Alignment.CENTER);
		
		grid.setFields(idField, schedulableField, lastNameField, firstNameField, usernameField,
				maxWTUField, instructorPrefsField);
		
		this.add(grid);
		
		this.add(new Button("Add New Instructor", new com.google.gwt.event.dom.client.ClickHandler() {
			@Override
			public void onClick(com.google.gwt.event.dom.client.ClickEvent event) {
				Record defaultValues = new Record();
				defaultValues.setAttribute("maxWTU", 0);
            grid.startEditingNew(defaultValues);
			}
		}));

		this.add(new Button("Duplicate Selected Instructors", new com.google.gwt.event.dom.client.ClickHandler() {
			public void onClick(com.google.gwt.event.dom.client.ClickEvent event) {
            ListGridRecord[] selectedRecords = grid.getSelectedRecords();  
            for(ListGridRecord rec: selectedRecords) {
					rec.setAttribute("id", (Integer)null);
					rec.setAttribute("instructorPrefs", (Integer)null);
					grid.startEditingNew(rec);
            }
			}
		}));

		grid.addKeyPressHandler(new KeyPressHandler() {
			public void onKeyPress(KeyPressEvent event) {
				if (event.getKeyName().equals("Backspace") || event.getKeyName().equals("Delete"))
					if (com.google.gwt.user.client.Window.confirm("Are you sure you want to remove this instructor?"))
						grid.removeSelectedData();
			}
		});
		
		this.add(new Button("Remove Selected Instructors", new com.google.gwt.event.dom.client.ClickHandler() {
			@Override
			public void onClick(com.google.gwt.event.dom.client.ClickEvent event) {
				ListGridRecord[] selectedRecords = grid.getSelectedRecords();
				for (ListGridRecord rec : selectedRecords) {
					grid.removeData(rec);
				}
			}
		}));
	}
	
	public void preferencesButtonClicked(InstructorGWT instructor, UnsavedDocumentStrategy unsavedDocumentStrategy) {
		InstructorPreferencesView iipv = new InstructorPreferencesView(
				service, document.getID(), document.getName(), instructor, unsavedDocumentStrategy);
		final Window window = new Window();
		window.setAutoSize(true);
		window.setTitle("Instructor Preferences");
		window.setCanDragReposition(true);
		window.setCanDragResize(true);
		iipv.setParent(window);
		iipv.afterPush();
		
		
		final ScrollPanel weewee = new ScrollPanel();
		weewee.setWidget(iipv);
		weewee.setSize("700px", "600px");
		window.addItem(weewee);
		
		ClickListener listener = new ClickListener() {
			public void onClick(Widget sender) {
				window.hide();
			}
		};
		Button button = new Button("Close", listener);
		iipv.add(button);
		button.setStyleName("centerness");

		window.setAutoSize(true);
		window.show();
	}
}
