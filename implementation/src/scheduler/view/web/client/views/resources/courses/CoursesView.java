package scheduler.view.web.client.views.resources.courses;

import java.util.LinkedHashMap;

import scheduler.view.web.client.views.resources.ResourceCache.Observer;
import scheduler.view.web.shared.CourseGWT;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.Autofit;
import com.smartgwt.client.types.ListGridEditEvent;
import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.types.Positioning;
import com.smartgwt.client.types.RowEndEditAction;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.Img;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.events.KeyPressEvent;
import com.smartgwt.client.widgets.events.KeyPressHandler;
import com.smartgwt.client.widgets.form.fields.FormItem;
import com.smartgwt.client.widgets.form.fields.FormItemIcon;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.events.ChangedEvent;
import com.smartgwt.client.widgets.form.fields.events.ChangedHandler;
import com.smartgwt.client.widgets.form.fields.events.IconClickEvent;
import com.smartgwt.client.widgets.form.fields.events.IconClickHandler;
import com.smartgwt.client.widgets.form.validator.IntegerRangeValidator;
import com.smartgwt.client.widgets.grid.CellFormatter;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridEditorContext;
import com.smartgwt.client.widgets.grid.ListGridEditorCustomizer;
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
		}
		else {
			coursesCache.addObserver(new Observer() {
				public void onModify() { }
				@Override
				public void onPopulate() {
					CoursesView.this.onPopulate();
					coursesCache.removeObserver(this);
				}
			});
		}
	}
	
	private void onPopulate() {
		final LectureOptionsDataSource lectureOptionsDataSource = new LectureOptionsDataSource(coursesCache);

		final ListGrid grid = new ListGrid() {
			protected Canvas createRecordComponent(final ListGridRecord record, Integer colNum) {
				String fieldName = this.getFieldName(colNum);
				if (fieldName.equals("associations")) {
					if (record.getAttribute("type").equals("LAB")) {
						return new Label("Associations");
					}
				}
				
				return null;
			}
			
			protected String getCellCSSText(ListGridRecord record, int rowNum,
					int colNum) {
				if (getFieldName(colNum).equals("selector")) {
					return "cursor: pointer; background: #D8D8D8;";
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
		ListGridField catalogNumberField = new ListGridField("catalogNumber",
				"Catalog Number");
		catalogNumberField.setAlign(Alignment.CENTER);
		ListGridField nameField = new ListGridField("name", "Name");
		nameField.setAlign(Alignment.CENTER);
		ListGridField numSectionsField = new ListGridField("numSections",
				"Number of Sections");
		numSectionsField.setDefaultValue(1);
		numSectionsField.setAlign(Alignment.CENTER);
		numSectionsField.setValidators(nonnegativeInt);
		ListGridField wtuField = new ListGridField("wtu", "WTU");
		wtuField.setAlign(Alignment.CENTER);
		wtuField.setDefaultValue(0);
		wtuField.setValidators(nonnegativeInt);
		ListGridField scuField = new ListGridField("scu", "SCU");
		scuField.setAlign(Alignment.CENTER);
		scuField.setDefaultValue(0);
		scuField.setValidators(nonnegativeInt);
		ListGridField dayCombinationsField = new ListGridField(
				"dayCombinations", "Day Combinations");
		dayCombinationsField.setAlign(Alignment.CENTER);
		ListGridField hoursPerWeekField = new ListGridField("hoursPerWeek",
				"Hours per Week");
		hoursPerWeekField.setAlign(Alignment.CENTER);
		hoursPerWeekField.setDefaultValue(0);
		hoursPerWeekField.setValidators(nonnegativeInt);
		ListGridField maxEnrollmentField = new ListGridField("maxEnrollment",
				"Max Enrollment");
		maxEnrollmentField.setAlign(Alignment.CENTER);
		maxEnrollmentField.setDefaultValue(0);
		maxEnrollmentField.setValidators(nonnegativeInt);
		ListGridField courseTypeField = new ListGridField("type", "Type");
		courseTypeField.setAlign(Alignment.CENTER);
		courseTypeField.setDefaultValue("LEC");
		ListGridField usedEquipmentField = new ListGridField("usedEquipment",
				"Used Equipment");
		usedEquipmentField.setAlign(Alignment.CENTER);
		
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
		
		grid.setEditorCustomizer(new ListGridEditorCustomizer() {  
			public FormItem getEditor(final ListGridEditorContext context) {  
				ListGridField field = context.getEditField();  
				if (field.getName().equals("associations")) {
					final Record record = context.getEditedRecord();
					if (record.getAttribute("type").equals("LAB")) {
//						CanvasItem canvas = new CanvasItem();
//						
//						DynamicForm form = new DynamicForm();
//						
						
						final SelectItem select = new SelectItem();
						
//						select.setOptionDataSource(lectureOptionsDataSource);
//						select.setDisplayField("displayField");
//						select.setValueField("valueField");
//						
						LinkedHashMap<String, String> valueMap = new LinkedHashMap<String, String>();
						valueMap.put("-1", "(none)");
						for (CourseGWT course : coursesCache.getAll())
							if (course.getType().equals("LEC"))
								valueMap.put(Integer.toString(course.getID()), course.getDept() + " " + course.getCatalogNum());
						select.setValueMap(valueMap);
						
						select.setEndRow(false);
						select.setShowTitle(false);

						String initialLectureID = record.getAttribute("lectureID");
						Window.alert("initial lecture id is " + initialLectureID);
						select.setValue(initialLectureID);

						final FormItemIcon initialIcon = new FormItemIcon();
						boolean isTetheredInitially = "true".equals(record.getAttribute("isTethered"));
						initialIcon.setSrc(isTetheredInitially ? "tethered.png" : "untethered.png");
						select.setIcons(initialIcon);
						
						select.addChangedHandler(new ChangedHandler() {
							public void onChanged(ChangedEvent event) {
								Window.alert("setting lecture id to " + event.getValue());
								record.setAttribute("lectureID", event.getValue());
							}
						});

						select.addIconClickHandler(new IconClickHandler() {
							public void onIconClick(IconClickEvent event) {

								boolean isTethered = "true".equals(record.getAttribute("isTethered"));
								isTethered = !isTethered;
								record.setAttribute("isTethered", isTethered ? "true" : "false");
//								Window.alert("tethered is now " + isTethered);

								final int row = context.getRowNum();
								
								Scheduler.get().scheduleDeferred(new Command() {
									public void execute() {
										grid.endEditing();
										grid.startEditing(row, 13, false);
									}
								});
								

//								initialIcon.setSrc(isTethered ? "tethered.png" : "untethered.png");
////								select.setIcons(icon);
//								
//								select.setWidth(74);
//								select.setShowIcons(false);
//								select.updateState();
//								select.redraw();
//								select.setWidth(73);
//								select.setShowIcons(true);
//								select.updateState();
//								select.redraw();
//								select.setWidth(72);
							}
						});
						
//						
//						CheckboxItem checkbox = new CheckboxItem();
//						checkbox.setWidth(25);
//						checkbox.setStartRow(false);
//						checkbox.setShowTitle(false);
//						checkbox.setShowLabel(false);
//						checkbox.addChangedHandler(new ChangedHandler() {
//							public void onChanged(ChangedEvent event) {
//								record.setAttribute("isTethered", event.getValue());
//							}
//						});
//						
//						form.setFields(select, checkbox);
//
//						canvas.setCanvas(form);
//						
//						return canvas;
						
						return select;
					}
				}

				return context.getDefaultProperties();
			}
		});


		// DOM.setElementAttribute(this.getElement(), "id", "s_coursesTab");

		// grid.getElement().setId("s_gridCoursesTbl");
		// this.setHorizontalAlignment(ALIGN_CENTER);
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
