package scheduler.view.web.client.views.resources.courses;

import scheduler.view.web.client.GreetingServiceAsync;
import scheduler.view.web.client.UnsavedDocumentStrategy;
import scheduler.view.web.shared.DocumentGWT;

import com.google.gwt.user.client.Window;
import com.smartgwt.client.data.Record;
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
import com.smartgwt.client.widgets.form.validator.IntegerRangeValidator;
import com.smartgwt.client.widgets.grid.CellFormatter;
import com.smartgwt.client.widgets.grid.HoverCustomizer;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;

public class CoursesView extends VLayout {
	private GreetingServiceAsync service;
	private final DocumentGWT document;

	public CoursesView(GreetingServiceAsync service, DocumentGWT document,
			UnsavedDocumentStrategy unsavedDocumentStrategy) {

		this.service = service;
		this.document = document;
		// this.addStyleName("iViewPadding");
		// DOM.setElementAttribute(this.getElement(), "id", "s_courseviewTab");
		this.setID("s_courseviewTab");
		this.setWidth100();
		this.setHeight100();

		// this.add(new HTML("<h2>Courses</h2>"));

		// gridPanel.setHorizontalAlignment(ALIGN_CENTER);
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
		// grid.setCellHeight(22);
		grid.setDataSource(new CoursesDataSource(service, document,
				unsavedDocumentStrategy,
				new CoursesDataSource.GetAllRecordsStrategy() {
					@Override
					public Record[] getAllRecords() {
						return grid.getRecords();
					}
				}));

		grid.addKeyPressHandler(new KeyPressHandler() {
			public void onKeyPress(KeyPressEvent event) {
				if (event.getKeyName().equals("Backspace")
						|| event.getKeyName().equals("Delete"))
					if (Window
							.confirm("Are you sure you want to remove this course?"))
						grid.removeSelectedData();
			}
		});

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
		schedulableField.setAlign(Alignment.CENTER);
		schedulableField.setDefaultValue(true);
		ListGridField departmentField = new ListGridField("department",
				"Department");
		departmentField.setAlign(Alignment.CENTER);
		ListGridField catalogNumberField = new ListGridField("catalogNumber",
				"Catalog Number");
		catalogNumberField.setAlign(Alignment.CENTER);
		ListGridField nameField = new ListGridField("name", "Name");
		nameField.setAlign(Alignment.CENTER);
		ListGridField numSectionsField = new ListGridField("numSections",
				"Number of Sections");
		numSectionsField.setDefaultFilterValue(1);
		numSectionsField.setAlign(Alignment.CENTER);
		numSectionsField.setValidators(nonnegativeInt);
		ListGridField wtuField = new ListGridField("wtu", "WTU");
		wtuField.setAlign(Alignment.CENTER);
		wtuField.setValidators(nonnegativeInt);
		ListGridField scuField = new ListGridField("scu", "SCU");
		scuField.setAlign(Alignment.CENTER);
		scuField.setValidators(nonnegativeInt);
		ListGridField dayCombinationsField = new ListGridField(
				"dayCombinations", "Day Combinations");
		dayCombinationsField.setAlign(Alignment.CENTER);
		ListGridField hoursPerWeekField = new ListGridField("hoursPerWeek",
				"Hours per Week");
		hoursPerWeekField.setAlign(Alignment.CENTER);
		hoursPerWeekField.setValidators(nonnegativeInt);
		ListGridField maxEnrollmentField = new ListGridField("maxEnrollment",
				"Max Enrollment");
		maxEnrollmentField.setAlign(Alignment.CENTER);
		maxEnrollmentField.setValidators(nonnegativeInt);
		ListGridField courseTypeField = new ListGridField("type", "Type");
		courseTypeField.setAlign(Alignment.CENTER);
		ListGridField usedEquipmentField = new ListGridField("usedEquipment",
				"Used Equipment");
		usedEquipmentField.setAlign(Alignment.CENTER);
		ListGridField associationsField = new ListGridField("associations",
				"Associations");
		associationsField.setAlign(Alignment.CENTER);

		// For a combo box associations field CURRENTLY NOT WORKING
		// final SelectItem test = new SelectItem();
		// test.setName("association");
		// test.addClickHandler(new
		// com.smartgwt.client.widgets.form.fields.events.ClickHandler() {
		//
		// @Override
		// public void onClick(
		// com.smartgwt.client.widgets.form.fields.events.ClickEvent event) {
		// ArrayList<String> lectureList = new ArrayList<String>();
		// System.out.println("About to get records");
		// for(ListGridRecord record : grid.getRecords())
		// {
		// System.out.println("Got some records");
		// if(record.getAttributeAsString("type").equals("LEC"))
		// {
		// //It is a lecture, add to possible associations
		// String lectureName = record.getAttributeAsString("catalogNumber");
		// System.out.println("Found lecture: " + lectureName);
		// lectureList.add(lectureName);
		// }
		// }
		// // grid.getField("associations").setValueMap("101", "TEST",
		// "TESTAGAION");
		// // test.setValueMap("TEST", "TESTER");
		// test.setMultiple(true);
		// }
		// } );
		// associationsField.setEditorType(test);

		grid.setFields(idField, schedulableField, departmentField,
				catalogNumberField, nameField, numSectionsField, wtuField,
				scuField, dayCombinationsField, hoursPerWeekField,
				maxEnrollmentField, courseTypeField, usedEquipmentField,
				associationsField);

		// DOM.setElementAttribute(this.getElement(), "id", "s_coursesTab");

		// grid.getElement().setId("s_gridCoursesTbl");
		// this.setHorizontalAlignment(ALIGN_CENTER);
		this.addMember(grid);
		// this.setHorizontalAlignment(ALIGN_LEFT);
		layoutBottomButtonBar(grid);
	}

	/**
	 * Lays out the buttons which will appear on this widget
	 */
	private void layoutBottomButtonBar(final ListGrid grid) {
		HLayout bottomButtonFlowPanel = new HLayout();
		bottomButtonFlowPanel.setMembersMargin(10);
		bottomButtonFlowPanel.addStyleName("floatingScheduleButtonBar");

		IButton course = new IButton("Add New Course", new ClickHandler() {
			public void onClick(ClickEvent event) {
				Record defaultValues = new Record();
				defaultValues.setAttribute("type", "LEC");
				defaultValues.setAttribute("numSections", 0);
				defaultValues.setAttribute("wtu", 0);
				defaultValues.setAttribute("scu", 0);
				defaultValues.setAttribute("hoursPerWeek", 0);
				defaultValues.setAttribute("maxEnrollment", 0);
				grid.startEditingNew(defaultValues);
			}
		});
		// DOM.setElementAttribute(course.getElement(), "id", "s_newCourseBtn");
		course.setAutoWidth();
		course.setOverflow(Overflow.VISIBLE);
		//DON'T CHANGE THIS ID IT WILL BREAK THE BUTTONS
		course.setID("s_newCourseBtn");
		bottomButtonFlowPanel.addMember(course);

		IButton dupeBtn = new IButton("Duplicate Selected Courses",
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
		dupeBtn.setID("s_dupeCourseBtn");
		bottomButtonFlowPanel.addMember(dupeBtn);

		IButton remove = new IButton("Remove Selected Courses",
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
		remove.setID("s_removeCourseBtn");
		bottomButtonFlowPanel.addMember(remove);

		this.addMember(bottomButtonFlowPanel);
	}
}
