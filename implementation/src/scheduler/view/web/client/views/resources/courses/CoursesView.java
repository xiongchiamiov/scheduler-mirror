package scheduler.view.web.client.views.resources.courses;

import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import scheduler.view.web.client.CachedOpenWorkingCopyDocument;
import scheduler.view.web.client.views.resources.ValidatorUtil;
import scheduler.view.web.shared.CourseGWT;
import scheduler.view.web.shared.ScheduleItemGWT;
import scheduler.view.web.shared.WeekGWT;

import com.google.gwt.user.client.Window;
import com.smartgwt.client.data.DSRequest;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.Autofit;
import com.smartgwt.client.types.ListGridEditEvent;
import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.types.RowEndEditAction;
import com.smartgwt.client.types.SelectionAppearance;
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
import com.smartgwt.client.widgets.grid.events.EditorEnterEvent;
import com.smartgwt.client.widgets.grid.events.EditorEnterHandler;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;

public class CoursesView extends VLayout {
	Img loadingImage;

	CachedOpenWorkingCopyDocument document;
	ListGrid grid;
	private static final int DEFAULT_SCU = 4;

	/**
	 * CoursesView constructor. Takes in a document to show and edit.
	 * @param document The document
	 */
	public CoursesView(final CachedOpenWorkingCopyDocument document) {
		this.document = document;

		this.setWidth100();
		this.setHeight100();

		onPopulate();
	}

	/**
	 * Populates the view
	 */
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

		grid.setSelectionAppearance(SelectionAppearance.CHECKBOX);
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
		wtuField.setDefaultValue(4);
		ListGridField scuField = new ListGridField("scu", "SCU");
		scuField.setAlign(Alignment.CENTER);
		scuField.setDefaultValue(DEFAULT_SCU);

		ListGridField dayCombinationsField = new ListGridField(
				"dayCombinations", "Day Combinations");
		dayCombinationsField.setAlign(Alignment.CENTER);
		dayCombinationsField
				.setEditorValueMapFunction(new PossibleDayPatternsFunction());
		dayCombinationsField.setCellFormatter(new CellFormatter() {
			public String format(Object rawValue, ListGridRecord record, int rowNum, int colNum) {
				String[] value = record.getAttributeAsStringArray("dayCombinations");
				if (value == null)
					return "";
				Set<WeekGWT> weeks = new TreeSet<WeekGWT>();
				for (String week : value)
					weeks.add(WeekGWT.parse(week));
				String result = "";
				for (WeekGWT week : weeks) {
					if (!result.equals(""))
						result += ",";
					result += week.toString();
				}
				return result;
			}
		});
		
		ListGridField hoursPerWeekField = new ListGridField("hoursPerWeek",
				"Hours per Week");
		hoursPerWeekField.setAlign(Alignment.CENTER);
		hoursPerWeekField.setDefaultValue(4);
		ListGridField maxEnrollmentField = new ListGridField("maxEnrollment",
				"Max Enrollment");
		maxEnrollmentField.setAlign(Alignment.CENTER);
		maxEnrollmentField.setDefaultValue(0);
		ListGridField courseTypeField = new ListGridField("type", "Type");
		courseTypeField.setAlign(Alignment.CENTER);
		courseTypeField.setDefaultValue("LEC");
		
		ListGridField lectureIDField = new ListGridField("lectureID", "Associations");
		lectureIDField.setDefaultValue(-1);
		lectureIDField.setOptionDataSource(lectureOptionsDataSource);
		lectureIDField.setDisplayField("displayField");
		lectureIDField.setValueField("valueField");

		ListGridField isTetheredField = new ListGridField("isTethered");
		isTetheredField.setDefaultValue(false);

		//Add the fields to the grid
		grid.setFields(departmentField,
				catalogNumberField, nameField, courseTypeField,
				numSectionsField, wtuField, scuField, dayCombinationsField,
				hoursPerWeekField, maxEnrollmentField,
				lectureIDField, isTetheredField, schedulableField);

		//Add handlers in order to change dayCombinations correctly based on SCUs
		grid.addEditCompleteHandler(new EditCompleteHandler() {
			public void onEditComplete(EditCompleteEvent event) {
				boolean recalculateDayCombos = false;
				
				Object dayComboValue = grid.getEditedCell(event.getRowNum(), "dayCombinations");
				
				if (grid.getFieldName(event.getColNum()).equals("scu")) {
					if (dayComboValue != null && dayComboValue.toString().length() > 0)
						Window.alert("Warning: By changing your SCU value your day combination data has been changed");
					
					recalculateDayCombos = true;
				}
				
				if (dayComboValue == null)
					recalculateDayCombos = true;
				
				if (recalculateDayCombos) {
					// Change day combos
					String scuString = grid.getEditedCell(event.getRowNum(), "scu").toString();
					String type = grid.getEditedCell(event.getRowNum(), "type").toString();

					DSRequest requestProperties = new DSRequest();
					requestProperties.setOldValues(grid.getEditedRecord(event.getRowNum()));

					String[] values = PossibleDayPatternsFunction
							.getValues(type, scuString).values()
							.toArray(new String[0]);
					Record record = grid.getEditedRecord(event.getRowNum());
					assert (record.getAttributeAsInt("id") != null);
					record.setAttribute("dayCombinations", values);
					// Update hours per week if it is still on default
					Integer hours = (Integer) grid.getEditedCell(event.getRowNum(), "hoursPerWeek");
					if (hours.equals(4)) {
						// It is on default value, set to SCU
						record.setAttribute("hoursPerWeek", scuString);
					}

					grid.updateData(record, null, requestProperties);
				}
			}
		});
		
		//Add handlers in order to change dayCombinations correctly based on SCUs
		grid.addEditorEnterHandler(new EditorEnterHandler() {
			public void onEditorEnter(EditorEnterEvent event) {

				Object dayComboValue = grid.getEditedCell(event.getRowNum(), "dayCombinations");
				
				if (dayComboValue == null || dayComboValue.toString().length() == 0) {
					String scuString = grid.getEditedCell(event.getRowNum(), "scu").toString();
					String type = grid.getEditedCell(event.getRowNum(), "type").toString();

					String[] patterns = PossibleDayPatternsFunction
							.getValues(type, scuString).values()
							.toArray(new String[0]);
					
					Map values = grid.getEditValues(event.getRowNum());
					values.put("dayCombinations", patterns);
					grid.setEditValues(event.getRowNum(), values);
				}
			}
		});
		
		addMember(grid);
		// this.setHorizontalAlignment(ALIGN_LEFT);
		layoutBottomButtonBar(grid);
	}

	/**
	 * Lays out the buttons which will appear on this widget
	 */
	private void layoutBottomButtonBar(final ListGrid grid) {
		HLayout bottomButtonFlowPanel = new HLayout();
		bottomButtonFlowPanel.setMembersMargin(10);
		bottomButtonFlowPanel.addStyleName("floatingResourcesButtonBar");

		//Add button
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

		//Duplicate button
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

		//Remove button
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

	/**
	 * Method to delete the selected course
	 */
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

	/**
	 * Returns true if the user can close the window, false if they can't
	 * @return true
	 */
	public boolean canClose() {
		// If you want to keep the user from navigating away, return false here

		return true;
	}

	/**
	 * Closes everything
	 */
	public void close() {
		this.clear();
	}
}
