package scheduler.view.web.client.views.resources.courses;

import scheduler.view.web.client.GreetingServiceAsync;
import scheduler.view.web.client.IViewContents;
import scheduler.view.web.client.ViewFrame;
import scheduler.view.web.shared.DocumentGWT;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.smartgwt.client.types.ListGridEditEvent;
import com.smartgwt.client.types.RowEndEditAction;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;

public class CoursesView extends VerticalPanel implements IViewContents {
	private GreetingServiceAsync service;
	private final DocumentGWT document;
	
	public CoursesView(GreetingServiceAsync service, DocumentGWT document) {
		this.service = service;
		this.document = document;
		// this.addStyleName("iViewPadding");
	}
	
	@Override
	public boolean canPop() {
		return true;
		// assert(table != null);
		// if (table.isSaved())
		// return true;
		// return
		// Window.confirm("You have unsaved data which will be lost. Are you sure you want to navigate away?");
	}
	
	@Override
	public void afterPush(ViewFrame frame) {
		this.setWidth("100%");
		this.setHeight("100%");
		
		this.add(new HTML("<h2>Courses</h2>"));
		
		final ListGrid grid = new ListGrid();
		grid.setWidth("100%");
		grid.setHeight(300);
		grid.setShowAllRecords(true);
		grid.setAutoFetchData(true);
		grid.setCanEdit(true);
		grid.setEditEvent(ListGridEditEvent.CLICK);
		grid.setEditByCell(true);
		grid.setListEndEditAction(RowEndEditAction.NEXT);
		//grid.setCellHeight(22);
		grid.setDataSource(new CoursesDataSource(service, document));
		
		ListGridField idField = new ListGridField("id");
		idField.setHidden(true);
		
		ListGridField departmentField = new ListGridField("department", "Department");
		ListGridField catalogNumberField = new ListGridField("catalogNumber", "Catalog Number");
		ListGridField nameField = new ListGridField("name", "Name");
		ListGridField numSectionsField = new ListGridField("numSections", "Number of Sections");
		ListGridField wtuField = new ListGridField("wtu", "WTU");
		ListGridField scuField = new ListGridField("scu", "SCU");
		ListGridField dayCombinationsField = new ListGridField("dayCombinations", "Day Combinations");
		ListGridField hoursPerWeekField = new ListGridField("hoursPerWeek", "Hours per Week");
		ListGridField maxEnrollmentField = new ListGridField("maxEnrollment", "Max Enrollment");
		ListGridField courseTypeField = new ListGridField("coursesType", "Type");
		ListGridField associationsField = new ListGridField("associations", "Associations");

		grid.setFields(idField, departmentField, catalogNumberField, nameField, numSectionsField, wtuField, scuField,
				dayCombinationsField, hoursPerWeekField, maxEnrollmentField, courseTypeField, associationsField);
		
		this.add(grid);
		
		IButton newButton = new IButton("Add New Course");
		newButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				grid.startEditingNew();
			}
		});
		this.add(newButton);
		
		
		IButton removeSelectedButton = new IButton("Remove Selected Courses");  
		removeSelectedButton.addClickHandler(new ClickHandler() {  
          public void onClick(ClickEvent event) {  
              ListGridRecord[] selectedRecords = grid.getSelectedRecords();  
              for(ListGridRecord rec: selectedRecords) {  
                  grid.removeData(rec);  
              }  
          }             
      });  
      this.add(removeSelectedButton);
	}
	
	@Override
	public void beforePop() {}
	@Override
	public void beforeViewPushedAboveMe() {}
	@Override
	public void afterViewPoppedFromAboveMe() {}
	@Override
	public Widget getContents() {
		return this;
	}
}
