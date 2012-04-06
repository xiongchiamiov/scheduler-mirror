package scheduler.view.web.client.views.resources.courses;

import scheduler.view.web.client.GreetingServiceAsync;
import scheduler.view.web.client.UnsavedDocumentStrategy;
import scheduler.view.web.shared.DocumentGWT;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.Autofit;
import com.smartgwt.client.types.ListGridEditEvent;
import com.smartgwt.client.types.RowEndEditAction;
import com.smartgwt.client.widgets.form.validator.IntegerRangeValidator;
import com.smartgwt.client.widgets.grid.CellFormatter;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;

public class CoursesView extends VerticalPanel {
	private GreetingServiceAsync service;
	private final DocumentGWT document;
	
	public CoursesView(GreetingServiceAsync service, DocumentGWT document, UnsavedDocumentStrategy unsavedDocumentStrategy) {
		this.service = service;
		this.document = document;
		// this.addStyleName("iViewPadding");
		
		this.setWidth("100%");
		this.setHeight("100%");
		
//		this.add(new HTML("<h2>Courses</h2>"));
		HorizontalPanel gridPanel = new HorizontalPanel();
		
		gridPanel.setHorizontalAlignment(ALIGN_CENTER);
		final ListGrid grid = new ListGrid();
		grid.setWidth100();
		
		grid.setAutoFitData(Autofit.VERTICAL);
		
		grid.setShowAllRecords(true);
		grid.setAutoFetchData(true);
		grid.setCanEdit(true);
		grid.setEditEvent(ListGridEditEvent.CLICK);
		grid.setEditByCell(true);
		grid.setListEndEditAction(RowEndEditAction.NEXT);
		//grid.setCellHeight(22);
		grid.setDataSource(new CoursesDataSource(service, document, unsavedDocumentStrategy));
		
		ListGridField idField = new ListGridField("id", "&nbsp;");
		idField.setCanEdit(false);
		idField.setCellFormatter(new CellFormatter() {
			public String format(Object value, ListGridRecord record, int rowNum, int colNum) {
				return "\u22EE";
			}
		});
		idField.setWidth(20);
		idField.setAlign(Alignment.CENTER);
		idField.setAlign(Alignment.CENTER);
		
		IntegerRangeValidator nonnegativeInt = new IntegerRangeValidator();  
		nonnegativeInt.setMin(0);  
		
		ListGridField scheduleableField = new ListGridField("isSchedulable", "Schedulable");
		scheduleableField.setAlign(Alignment.CENTER);
		ListGridField departmentField = new ListGridField("department", "Department");
		departmentField.setAlign(Alignment.CENTER);
		ListGridField catalogNumberField = new ListGridField("catalogNumber", "Catalog Number");
		catalogNumberField.setAlign(Alignment.CENTER);
		ListGridField nameField = new ListGridField("name", "Name");
		nameField.setAlign(Alignment.CENTER);
		ListGridField numSectionsField = new ListGridField("numSections", "Number of Sections");
		numSectionsField.setValidators(nonnegativeInt);
		ListGridField wtuField = new ListGridField("wtu", "WTU");
		wtuField.setValidators(nonnegativeInt);
		ListGridField scuField = new ListGridField("scu", "SCU");
		scuField.setValidators(nonnegativeInt);
		ListGridField dayCombinationsField = new ListGridField("dayCombinations", "Day Combinations");
		dayCombinationsField.setAlign(Alignment.CENTER);
		ListGridField hoursPerWeekField = new ListGridField("hoursPerWeek", "Hours per Week");
		hoursPerWeekField.setValidators(nonnegativeInt);
		ListGridField maxEnrollmentField = new ListGridField("maxEnrollment", "Max Enrollment");
		maxEnrollmentField.setValidators(nonnegativeInt);
		ListGridField courseTypeField = new ListGridField("type", "Type");
		courseTypeField.setAlign(Alignment.CENTER);
		ListGridField usedEquipmentField = new ListGridField("usedEquipment", "Used Equipment");
		usedEquipmentField.setAlign(Alignment.CENTER);
		ListGridField associationsField = new ListGridField("associations", "Associations");
		associationsField.setAlign(Alignment.CENTER);

		grid.setFields(idField, scheduleableField, departmentField, catalogNumberField, nameField, numSectionsField, wtuField, scuField,
				dayCombinationsField, hoursPerWeekField, maxEnrollmentField, courseTypeField, usedEquipmentField, associationsField);
		gridPanel.add(grid);
		this.add(gridPanel);
		
		this.add(new Button("Add New Course", new ClickHandler() {
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
		}));
		
		this.add(new Button("Duplicate Selected Courses", new ClickHandler() {
			public void onClick(ClickEvent event) {
            ListGridRecord[] selectedRecords = grid.getSelectedRecords();  
            for(ListGridRecord rec: selectedRecords) {
					rec.setAttribute("id", (Integer)null);
					grid.startEditingNew(rec);
            }
			}
		}));
		
		this.add(new Button("Remove Selected Courses", new ClickHandler() {
			public void onClick(ClickEvent event) {
            ListGridRecord[] selectedRecords = grid.getSelectedRecords();  
            for(ListGridRecord rec: selectedRecords) {  
                grid.removeData(rec);
            }
			}
		}));
	}
}
