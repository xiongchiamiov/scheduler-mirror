package scheduler.view.web.client.views.resources.courses;

import scheduler.view.web.client.views.resources.ValidatorUtil;
import scheduler.view.web.client.views.resources.ResourceCache.Observer;

import com.google.gwt.user.client.Window;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.Autofit;
import com.smartgwt.client.types.ListGridEditEvent;
import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.types.Positioning;
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
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;

public class CoursesView extends VLayout {
	Img loadingImage;

	DocumentCoursesCache coursesCache;

	public CoursesView(final DocumentCoursesCache coursesCache) {
		this.coursesCache = coursesCache;

		this.setID("s_courseviewTab");
		this.setWidth100();
		this.setHeight100();

		this.setPosition(Positioning.RELATIVE);

		loadingImage = new Img("imgs/loading.gif");
		loadingImage.setPosition(Positioning.ABSOLUTE);
		this.addMember(loadingImage);

		if (coursesCache.isPopulated()) {
			onPopulate();
		} else {
			coursesCache.addObserver(new Observer() {
				public void onModify() {
				}

				@Override
				public void onPopulate() {
					CoursesView.this.onPopulate();
					coursesCache.removeObserver(this);
				}
			});
		}
	}

	private void onPopulate() {
		final LectureOptionsDataSource lectureOptionsDataSource = new LectureOptionsDataSource(
				coursesCache);

		final ListGrid grid = new ListGrid() {

			protected String getCellCSSText(ListGridRecord record, int rowNum,
					int colNum) {
				if(record != null)
				{
					if (getFieldName(colNum).equals("selector")) {
						return "cursor: pointer; background: #D8D8D8;";
					} else if (!ValidatorUtil.isValidCourseType(getFieldName(colNum), record)) {
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
		grid.setDataSource(new CoursesDataSource(coursesCache));
		grid.setAutoSaveEdits(true);

		grid.addKeyPressHandler(new KeyPressHandler() {
			public void onKeyPress(KeyPressEvent event) {
				if (event.getKeyName().equals("Backspace")
						|| event.getKeyName().equals("Delete"))
					if (Window
							.confirm("Are you sure you want to remove this course?"))
						grid.removeSelectedData();
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

		ListGridField isTetheredField = new ListGridField("isTethered");
		isTetheredField.setDefaultValue(false);

		grid.setFields(selectorField, schedulableField, departmentField,
				catalogNumberField, nameField, numSectionsField, wtuField,
				scuField, dayCombinationsField, hoursPerWeekField,
				maxEnrollmentField, courseTypeField, usedEquipmentField,
				lectureIDField, isTetheredField);

		addMember(grid);
		// this.setHorizontalAlignment(ALIGN_LEFT);
		layoutBottomButtonBar(grid);

		removeMember(loadingImage);
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
				grid.startEditingNew(defaultValues);
			}
		});
		// DOM.setElementAttribute(course.getElement(), "id", "s_newCourseBtn");
		course.setAutoWidth();
		course.setOverflow(Overflow.VISIBLE);
		// DON'T CHANGE THIS ID IT WILL BREAK THE BUTTONS
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
		// DON'T CHANGE THIS ID IT WILL BREAK THE BUTTONS
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
		// DON'T CHANGE THIS ID IT WILL BREAK THE BUTTONS
		remove.setID("s_removeCourseBtn");
		bottomButtonFlowPanel.addMember(remove);

		this.addMember(bottomButtonFlowPanel);
	}
}
