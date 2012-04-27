package scheduler.view.web.client.views.resources.instructors;

import java.util.List;

import scheduler.view.web.client.GreetingServiceAsync;
import scheduler.view.web.client.UnsavedDocumentStrategy;
import scheduler.view.web.shared.DocumentGWT;
import scheduler.view.web.shared.InstructorGWT;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.Autofit;
import com.smartgwt.client.types.ListGridEditEvent;
import com.smartgwt.client.types.Overflow;
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
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;

public class InstructorsView extends VLayout {
	protected GreetingServiceAsync service;
	protected final DocumentGWT document;

	// protected ViewFrame frame;

	public InstructorsView(final GreetingServiceAsync service,
			final DocumentGWT document,
			final UnsavedDocumentStrategy unsavedDocumentStrategy) {
		setID("s_instructorviewTab");

		this.service = service;
		this.document = document;
		// this.addStyleName("iViewPadding");

		this.setWidth100();
		this.setHeight100();
		// this.setHorizontalAlignment(ALIGN_CENTER);
		// this.add(new HTML("<h2>Instructors</h2>"));

		final ListGrid grid = new ListGrid() {
			protected int rowCount = 0;

			protected String getCellCSSText(ListGridRecord record, int rowNum,
					int colNum) {
				if (getFieldName(colNum).equals("id")) {
					return "cursor: pointer; background: #C0C0C0;";
				} else {
					return super.getCellCSSText(record, rowNum, colNum);
				}
			}

			@Override
			protected Canvas createRecordComponent(final ListGridRecord record,
					Integer colNum) {
				String fieldName = this.getFieldName(colNum);
				if (fieldName.equals("instructorPrefs")) {
					IButton button = new IButton();
					button.setHeight(18);
					button.setWidth(65);
					button.setTitle("Preferences");
					this.rowCount++;
					button.addClickHandler(new ClickHandler() {
						public void onClick(ClickEvent event) {
							final int instructorID = record
									.getAttributeAsInt("id");
							service.getInstructorsForDocument(document.getID(),
									new AsyncCallback<List<InstructorGWT>>() {
										public void onFailure(Throwable caught) {
											com.google.gwt.user.client.Window
													.alert("Failed to get instructors!");
										}

										public void onSuccess(
												List<InstructorGWT> result) {
											for (InstructorGWT instructor : result) {
												if (instructor.getID().equals(
														instructorID)) {
													preferencesButtonClicked(
															instructor,
															unsavedDocumentStrategy);
													break;
												}
											}
										}
									});
						}
					});
					return button;
				} else {
					return null;
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
		// grid.setCellHeight(22);
		grid.setDataSource(new InstructorsDataSource(service, document,
				unsavedDocumentStrategy));
		grid.setShowRecordComponents(true);
		grid.setShowRecordComponentsByCell(true);

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

		IntegerRangeValidator nonnegativeInt = new IntegerRangeValidator();
		nonnegativeInt.setMin(0);

		ListGridField schedulableField = new ListGridField("isSchedulable",
				"Schedulable");
		schedulableField.setDefaultValue(true);
		schedulableField.setAlign(Alignment.CENTER);

		ListGridField lastNameField = new ListGridField("lastName", "Last Name");
		lastNameField.setAlign(Alignment.CENTER);
		lastNameField.setValidators(new CustomValidator() {
			@Override
			protected boolean condition(Object value) {
				if (value == null) {
					setErrorMessage("Username must be present!");
					return false;
				}
				return true;
			}
		});

		ListGridField firstNameField = new ListGridField("firstName",
				"First Name");
		firstNameField.setAlign(Alignment.CENTER);
		firstNameField.setValidators(new CustomValidator() {
			@Override
			protected boolean condition(Object value) {
				if (value == null) {
					setErrorMessage("Username must be present!");
					return false;
				}
				return true;
			}
		});

		ListGridField usernameField = new ListGridField("username", "Username");
		usernameField.setAlign(Alignment.CENTER);
		usernameField.setValidators(new CustomValidator() {
			protected boolean condition(Object value) {
				if (value == null) {
					setErrorMessage("Username must be present!");
					return false;
				}

				assert (value instanceof String);
				String username = (String) value;
				if (username.trim().length() == 0) {
					setErrorMessage("Username must be present!");
					return false;
				}

				for (Record record : grid.getDataAsRecordList().getRange(0,
						grid.getDataAsRecordList().getLength())) {
					if (username.equals(record.getAttribute("username"))) {
						setErrorMessage("Username \"" + username
								+ "\" already exists!");
						return false;
					}
				}
				return true;
			}
		});

		ListGridField maxWTUField = new ListGridField("maxWTU", "Max WTU");
		maxWTUField.setValidators(nonnegativeInt);
		maxWTUField.setAlign(Alignment.CENTER);
		maxWTUField.setDefaultValue(0);

		ListGridField instructorPrefsField = new ListGridField(
				"instructorPrefs", "Preferences");
		instructorPrefsField.setAlign(Alignment.CENTER);
		instructorPrefsField.setCanEdit(false);

		grid.setFields(idField, schedulableField, lastNameField,
				firstNameField, usernameField, maxWTUField,
				instructorPrefsField);

		this.addMember(grid);
		// this.setHorizontalAlignment(ALIGN_DEFAULT);

		grid.addKeyPressHandler(new KeyPressHandler() {
			public void onKeyPress(KeyPressEvent event) {
				if (event.getKeyName().equals("Backspace")
						|| event.getKeyName().equals("Delete"))
					if (com.google.gwt.user.client.Window
							.confirm("Are you sure you want to remove this instructor?"))
						grid.removeSelectedData();
			}
		});
		layoutBottomButtonBar(grid);
	}

	public void preferencesButtonClicked(InstructorGWT instructor,
			UnsavedDocumentStrategy unsavedDocumentStrategy) {
		InstructorPreferencesView iipv = new InstructorPreferencesView(service,
				document.getID(), document.getName(), instructor,
				unsavedDocumentStrategy);
		final Window window = new Window();
		window.setAutoSize(true);
		window.setTitle("Instructor Preferences - <i>"
				+ instructor.getUsername() + "</i> ("
				+ instructor.getFirstName() + " " + instructor.getLastName()
				+ ")");
		window.setCanDragReposition(true);
		window.setCanDragResize(true);
		window.setSize("700px", "600px");

		/*
		 * IButton button = new IButton("Close", new ClickHandler() { public
		 * void onClick(ClickEvent event) { window.hide(); } });
		 */
		// window.addMember(button);
		iipv.setParent(window);
		iipv.afterPush();

		window.setSize("700px", "600px");
		final ScrollPanel weewee = new ScrollPanel();
		weewee.setWidget(iipv);
		weewee.setSize("700px", "600px");
		window.addItem(weewee);
		// window.addItem(button);

		/*
		 * com.google.gwt.event.dom.client.ClickHandler handler = new
		 * com.google.gwt.event.dom.client.ClickHandler() {
		 * 
		 * @Override public void
		 * onClick(com.google.gwt.event.dom.client.ClickEvent event) {
		 * 
		 * } };
		 */
		/*
		 * IButton button = new IButton("Close", new ClickHandler() { public
		 * void onClick(ClickEvent event) { window.hide(); } });
		 */
		// button.setTitle("Close");
		// weewee.add(button);
		// window.addMember(weewee);
		// button.setStyleName("centerness");

		window.setAutoSize(true);
		window.show();
	}

	/**
	 * Lays out the buttons which will appear on this widget
	 */
	private void layoutBottomButtonBar(final ListGrid grid) {
		HLayout bottomButtonFlowPanel = new HLayout();
		bottomButtonFlowPanel.setMembersMargin(10);
		bottomButtonFlowPanel.addStyleName("floatingScheduleButtonBar");

		IButton addBtn = new IButton("Add New Instructor", new ClickHandler() {
			public void onClick(ClickEvent event) {
				Record defaultValues = new Record();
				grid.startEditingNew(defaultValues);
			}
		});
		// DOM.setElementAttribute(addBtn.getElement(), "id",
		// "addInstructorBtn");
		addBtn.setAutoWidth();
		addBtn.setOverflow(Overflow.VISIBLE);
		//DON'T CHANGE THIS ID IT WILL BREAK THE BUTTONS
		addBtn.setID("s_newInstructorBtn");
		bottomButtonFlowPanel.addMember(addBtn);

		IButton duplicateBtn = new IButton("Duplicate Selected Instructors",
				new ClickHandler() {
					public void onClick(ClickEvent event) {
						ListGridRecord[] selectedRecords = grid
								.getSelectedRecords();
						for (ListGridRecord rec : selectedRecords) {
							rec.setAttribute("id", (Integer) null);
							rec.setAttribute("instructorPrefs", (Integer) null);
							grid.startEditingNew(rec);
						}
					}
				});
		// DOM.setElementAttribute(duplicateBtn.getElement(), "id",
		// "duplicateBtn");
		duplicateBtn.setAutoWidth();
		duplicateBtn.setOverflow(Overflow.VISIBLE);
		//DON'T CHANGE THIS ID IT WILL BREAK THE BUTTONS
		duplicateBtn.setID("s_dupeInstructorBtn");
		bottomButtonFlowPanel.addMember(duplicateBtn);

		IButton removeBtn = new IButton("Remove Selected Instructors",
				new ClickHandler() {
					public void onClick(ClickEvent event) {
						ListGridRecord[] selectedRecords = grid
								.getSelectedRecords();
						for (ListGridRecord rec : selectedRecords) {
							grid.removeData(rec);
						}
					}
				});
		// DOM.setElementAttribute(removeBtn.getElement(), "id", "removeBtn");

		removeBtn.setAutoWidth();
		removeBtn.setOverflow(Overflow.VISIBLE);
		//DON'T CHANGE THIS ID IT WILL BREAK THE BUTTONS
		removeBtn.setID("s_removeInstructorBtn");
		bottomButtonFlowPanel.addMember(removeBtn);

		this.addMember(bottomButtonFlowPanel);
	}
}
