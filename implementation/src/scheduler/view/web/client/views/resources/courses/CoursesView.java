package scheduler.view.web.client.views.resources.courses;

import java.util.Set;
import java.util.TreeSet;

import scheduler.view.web.client.CachedOpenWorkingCopyDocument;
import scheduler.view.web.client.views.resources.ValidatorUtil;
import scheduler.view.web.shared.CourseGWT;
import scheduler.view.web.shared.ScheduleItemGWT;

import com.google.gwt.user.client.Window;
import com.smartgwt.client.data.DSRequest;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.Autofit;
import com.smartgwt.client.types.ListGridEditEvent;
import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.types.RowEndEditAction;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.Img;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.events.KeyPressEvent;
import com.smartgwt.client.widgets.events.KeyPressHandler;
import com.smartgwt.client.widgets.form.validator.IntegerRangeValidator;
import com.smartgwt.client.widgets.grid.CellFormatter;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.grid.events.EditCompleteEvent;
import com.smartgwt.client.widgets.grid.events.EditCompleteHandler;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;

public class CoursesView extends VLayout {
	Img loadingImage;

	CachedOpenWorkingCopyDocument document;
	ListGrid grid;

	public CoursesView(final CachedOpenWorkingCopyDocument document) {
		this.document = document;

		// this.setID("s_courseviewTab");
		this.setWidth100();
		this.setHeight100();

		onPopulate();
	}

	private void onPopulate() {
		final LectureOptionsDataSource lectureOptionsDataSource = new LectureOptionsDataSource(
				document);

		grid = new ListGrid() {
			@Override
			protected boolean canEditCell(int rowNum, int colNum) {
				if ("lectureID".equals(getFieldName(colNum))) {
					Record record = getRecord(rowNum);
					if (record != null) {
						if ("LEC".equals(record.getAttribute("type"))
								|| "IND".equals(record.getAttribute("type"))
								|| "SEM".equals(record.getAttribute("type"))) {
							return false;
						}
					}
				}

				// TODO Auto-generated method stub
				return super.canEditCell(rowNum, colNum);
			}

			@Override
			protected String getCellCSSText(ListGridRecord record, int rowNum,
					int colNum) {
				if (record != null) {
					if (getFieldName(colNum).equals("selector")) {
						return "cursor: pointer; background: #D8D8D8;";
					} else if (!ValidatorUtil.isValidCourseType(
							getFieldName(colNum), record)) {
						// Invalid data, set background to red
						return "background: #FF9999;";
					} else {
						// Valid data, do nothing
						return super.getCellCSSText(record, rowNum, colNum);
					}
				}
				return super.getCellCSSText(record, rowNum, colNum);
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
		grid.setDataSource(new CoursesDataSource(document));
		grid.setAutoSaveEdits(true);

		grid.addKeyPressHandler(new KeyPressHandler() {
			public void onKeyPress(KeyPressEvent event) {
				if (event.getKeyName().equals("Backspace")
						|| event.getKeyName().equals("Delete"))
					deleteSelected();
			}
		});

		ListGridField selectorField = new ListGridField("selector", "&nbsp;");
		selectorField.setCanEdit(false);
		selectorField.setCellFormatter(new CellFormatter() {
			public String format(Object value, ListGridRecord record,
					int rowNum, int colNum) {
				return "\u22EE";
			}
		});
		selectorField.setWidth(20);
		selectorField.setAlign(Alignment.CENTER);

		IntegerRangeValidator nonnegativeInt = new IntegerRangeValidator();
		nonnegativeInt.setMin(0);

		ListGridField schedulableField = new ListGridField("isSchedulable",
				"Schedulable");
		schedulableField.setAlign(Alignment.CENTER);
		schedulableField.setDefaultValue(true);
		ListGridField departmentField = new ListGridField("department",
				"Department");
		departmentField.setAlign(Alignment.CENTER);
		departmentField.setDefaultValue("");
		ListGridField catalogNumberField = new ListGridField("catalogNumber",
				"Catalog Number");
		catalogNumberField.setAlign(Alignment.CENTER);
		catalogNumberField.setDefaultValue("");
		ListGridField nameField = new ListGridField("name", "Name");
		nameField.setAlign(Alignment.CENTER);
		nameField.setDefaultValue("");
		ListGridField numSectionsField = new ListGridField("numSections",
				"Number of Sections");
		numSectionsField.setDefaultValue(1);
		numSectionsField.setAlign(Alignment.CENTER);
		ListGridField wtuField = new ListGridField("wtu", "WTU");
		wtuField.setAlign(Alignment.CENTER);
		wtuField.setDefaultValue(0);
		ListGridField scuField = new ListGridField("scu", "SCU");
		scuField.setAlign(Alignment.CENTER);
		scuField.setDefaultValue(0);

		ListGridField dayCombinationsField = new ListGridField(
				"dayCombinations", "Day Combinations");
		dayCombinationsField.setAlign(Alignment.CENTER);
		dayCombinationsField
				.setEditorValueMapFunction(new PossibleDayPatternsFunction());

		grid.addEditCompleteHandler(new EditCompleteHandler() {
			@Override
			public void onEditComplete(EditCompleteEvent event) {
				if (grid.getFieldName(event.getColNum()).equals("scu")) {
					if (Window
							.confirm("By changing your SCU value you will lose your day combination data for this row. Would you like to proceed?")) {
						//Change day combos
						String scuString = (String) grid.getEditedCell(
								event.getRowNum(), "scu");
						String type = (String) grid.getEditedCell(
								event.getRowNum(), "type");

						DSRequest requestProperties = new DSRequest();
						requestProperties.setOldValues(grid
								.getEditedRecord(event.getRowNum()));

						String[] values = PossibleDayPatternsFunction
								.getValues(type, scuString).values()
								.toArray(new String[0]);
						Record record = grid.getEditedRecord(event.getRowNum());
						assert (record.getAttributeAsInt("id") != null);
						record.setAttribute("dayCombinations", values);
						//Update hours per week if it is still on default
						String hours = (String) grid.getEditedCell(
								event.getRowNum(), "hoursPerWeek");
						if(hours.equals("0"))
						{
							//It is on default value, set to SCU
							record.setAttribute("hoursPerWeek", scuString);
						}

						grid.updateData(record, null, requestProperties);
					} else {
						System.out.println("oldvalue "
								+ event.getOldRecord().getAttributeAsString(
										"scu") + ", new record value: "
								+ event.getNewValues().get("scu"));
						// Revert to old scu
						Record oldrecord = event.getOldRecord();
						DSRequest requestProperties = new DSRequest();
						requestProperties.setOldValues(grid
								.getEditedRecord(event.getRowNum()));
						String scuvalue = oldrecord.getAttributeAsString("scu");
						Record record = grid.getEditedRecord(event.getRowNum());
						record.setAttribute("scu", scuvalue);
						System.out.println("Trying to set scu value to "
								+ scuvalue);
						grid.updateData(record, null, requestProperties);
					}
				}
			}
		});

		ListGridField hoursPerWeekField = new ListGridField("hoursPerWeek",
				"Hours per Week");
		hoursPerWeekField.setAlign(Alignment.CENTER);
		hoursPerWeekField.setDefaultValue(0);
		ListGridField maxEnrollmentField = new ListGridField("maxEnrollment",
				"Max Enrollment");
		maxEnrollmentField.setAlign(Alignment.CENTER);
		maxEnrollmentField.setDefaultValue(0);
		ListGridField courseTypeField = new ListGridField("type", "Type");
		courseTypeField.setAlign(Alignment.CENTER);
		courseTypeField.setDefaultValue("LEC");
		ListGridField usedEquipmentField = new ListGridField("usedEquipment",
				"Used Equipment");
		usedEquipmentField.setAlign(Alignment.CENTER);
		usedEquipmentField.setDefaultValue("");

		ListGridField lectureIDField = new ListGridField("lectureID");
		lectureIDField.setDefaultValue(-1);
		lectureIDField.setOptionDataSource(lectureOptionsDataSource);
		lectureIDField.setDisplayField("displayField");
		lectureIDField.setValueField("valueField");
		lectureIDField.setCellFormatter(new CellFormatter() {
			@Override
			public String format(Object value, ListGridRecord record,
					int rowNum, int colNum) {
				if ("lectureID".equals(grid.getFieldName(colNum))) {
					if ("LEC".equals(record.getAttribute("type"))
							|| "SEM".equals(record.getAttribute("type"))
							|| "IND".equals(record.getAttribute("type"))) {
						return "";
					}
				}
				return value.toString();
			}
		});

		ListGridField isTetheredField = new ListGridField("isTethered");
		isTetheredField.setDefaultValue(false);
		isTetheredField.setCellFormatter(new CellFormatter() {
			@Override
			public String format(Object value, ListGridRecord record,
					int rowNum, int colNum) {
				if ("isTethered".equals(grid.getFieldName(colNum))) {
					if ("LEC".equals(record.getAttribute("type"))) {
						return "";
					}
				}
				return value.toString();
			}
		});

		grid.setFields(selectorField, schedulableField, departmentField,
				catalogNumberField, nameField, courseTypeField,
				numSectionsField, wtuField, scuField, dayCombinationsField,
				hoursPerWeekField, maxEnrollmentField, usedEquipmentField,
				lectureIDField, isTetheredField);

		addMember(grid);
		// this.setHorizontalAlignment(ALIGN_LEFT);
		layoutBottomButtonBar(grid);

		grid.addEditCompleteHandler(new EditCompleteHandler() {
			@Override
			public void onEditComplete(EditCompleteEvent event) {
				grid.refreshRow(event.getRowNum());
				grid.refreshRecordComponent(event.getRowNum(),
						event.getColNum());
				grid.refreshCellStyle(event.getRowNum(), event.getColNum());
				grid.redraw();
				grid.refreshRow(event.getRowNum());
				grid.refreshRecordComponent(event.getRowNum(),
						event.getColNum());
				grid.refreshCellStyle(event.getRowNum(), event.getColNum());
			}
		});

		// grid.redraw();
	}

	/**
	 * Lays out the buttons which will appear on this widget
	 */
	private void layoutBottomButtonBar(final ListGrid grid) {
		HLayout bottomButtonFlowPanel = new HLayout();
		bottomButtonFlowPanel.setMembersMargin(10);
		bottomButtonFlowPanel.addStyleName("floatingResourcesButtonBar");

		IButton course = new IButton("Add New Course", new ClickHandler() {
			public void onClick(ClickEvent event) {
				Record defaultValues = new Record();
				grid.startEditingNew(defaultValues);
			}
		});
		course.setAutoWidth();
		course.setOverflow(Overflow.VISIBLE);
		// DON'T CHANGE THIS ID IT WILL BREAK THE BUTTONS
		// course.setID("s_newCourseBtn");
		bottomButtonFlowPanel.addMember(course);

		IButton dupeBtn = new IButton("Duplicate Selected Courses",
				new ClickHandler() {
					public void onClick(ClickEvent event) {
						grid.endEditing();

						for (ListGridRecord rec : grid.getSelectedRecords()) {
							int id = rec.getAttributeAsInt("id");
							CourseGWT course = new CourseGWT(
									document.getCourseByID(id));
							course.setID(null);
							document.addCourse(course);
						}

						grid.invalidateCache();
						grid.fetchData();
					}
				});

		dupeBtn.setAutoWidth();
		dupeBtn.setOverflow(Overflow.VISIBLE);
		// DON'T CHANGE THIS ID IT WILL BREAK THE BUTTONS
		// dupeBtn.setID("s_dupeCourseBtn");
		bottomButtonFlowPanel.addMember(dupeBtn);

		IButton remove = new IButton("Remove Selected Courses",
				new ClickHandler() {
					public void onClick(ClickEvent event) {
						deleteSelected();
					}
				});

		// DOM.setElementAttribute(remove.getElement(), "id", "s_removeBtn");
		remove.setAutoWidth();
		remove.setOverflow(Overflow.VISIBLE);
		// DON'T CHANGE THIS ID IT WILL BREAK THE BUTTONS
		// remove.setID("s_removeCourseBtn");

		bottomButtonFlowPanel.addMember(remove);

		this.addMember(bottomButtonFlowPanel);
	}

	void deleteSelected() {
		Set<Integer> referencedCourseIDs = new TreeSet<Integer>();
		for (ScheduleItemGWT item : document.getScheduleItems())
			referencedCourseIDs.add(item.getCourseID());

		Set<Integer> CoursesToDeleteIDs = new TreeSet<Integer>();
		for (ListGridRecord rec : grid.getSelectedRecords())
			if (rec.getAttribute("id") != null) {
				CoursesToDeleteIDs.add(rec.getAttributeAsInt("id"));
			}

		Set<Integer> referencedCoursesToDeleteIDs = new TreeSet<Integer>(
				CoursesToDeleteIDs);
		referencedCoursesToDeleteIDs.retainAll(referencedCourseIDs);

		if (!referencedCoursesToDeleteIDs.isEmpty()) {
			String namesCombined = "";
			for (int referencedCourseToDeleteID : referencedCoursesToDeleteIDs) {
				if (!namesCombined.equals(""))
					namesCombined += ", ";
				CourseGWT course = document
						.getCourseByID(referencedCourseToDeleteID);
				namesCombined += course.getDept() + " "
						+ course.getCatalogNum();
			}

			String messageString = referencedCoursesToDeleteIDs.size() == 1 ? "Course "
					: "Courses ";
			messageString += namesCombined;
			messageString += referencedCoursesToDeleteIDs.size() == 1 ? " is "
					: " are ";
			messageString += "scheduled already. Please unschedule, then try again.";
			com.google.gwt.user.client.Window.alert(messageString);
		} else {
			if (com.google.gwt.user.client.Window
					.confirm("Are you sure you want to remove this course?")) {
				grid.removeSelectedData();
			}
		}
	}

	public boolean canClose() {
		// If you want to keep the user from navigating away, return false here

		return true;
	}

	public void close() {
		this.clear();
	}
}
