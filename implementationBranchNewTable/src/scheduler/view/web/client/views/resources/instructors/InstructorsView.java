package scheduler.view.web.client.views.resources.instructors;

import java.util.List;

import scheduler.view.web.client.GreetingServiceAsync;
import scheduler.view.web.client.IViewContents;
import scheduler.view.web.client.ViewFrame;
import scheduler.view.web.shared.DocumentGWT;
import scheduler.view.web.shared.InstructorGWT;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.ListGridEditEvent;
import com.smartgwt.client.types.RowEndEditAction;
import com.smartgwt.client.types.VerticalAlignment;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;

public class InstructorsView extends VerticalPanel implements IViewContents {
	private GreetingServiceAsync service;
	private final DocumentGWT document;
	private ViewFrame frame;
	
	public InstructorsView(GreetingServiceAsync service, DocumentGWT document) {
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
		this.frame = frame;
		
		this.setWidth("100%");
		this.setHeight("100%");
		
		this.add(new HTML("<h2>Instructors</h2>"));
		
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
									com.google.gwt.user.client.Window.alert("failed to get instructors!");
								}
								
								@Override
								public void onSuccess(List<InstructorGWT> result) {
									for (InstructorGWT instructor : result) {
										if (instructor.getID().equals(instructorID)) {
											preferencesButtonClicked(instructor);
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
		grid.setHeight(300);
		grid.setShowAllRecords(true);
		grid.setAutoFetchData(true);
		grid.setCanEdit(true);
		grid.setEditEvent(ListGridEditEvent.CLICK);
		grid.setEditByCell(true);
		grid.setListEndEditAction(RowEndEditAction.NEXT);
		// grid.setCellHeight(22);
		grid.setDataSource(new InstructorsDataSource(service, document));
		grid.setShowRecordComponents(true);
		grid.setShowRecordComponentsByCell(true);
		
		ListGridField idField = new ListGridField("id");
		idField.setHidden(true);
		
		ListGridField usernameField = new ListGridField("username", "Username");
		ListGridField firstNameField = new ListGridField("firstName",
				"First Name");
		ListGridField lastNameField = new ListGridField("lastName", "Last Name");
		ListGridField maxWTUField = new ListGridField("maxWTU", "Max WTU");
		ListGridField instructorPrefsField = new ListGridField(
				"instructorPrefs", "Preferences");
		instructorPrefsField.setAlign(Alignment.CENTER);
		
		grid.setFields(idField, usernameField, firstNameField, lastNameField,
				maxWTUField, instructorPrefsField);
		
		this.add(grid);
		
		this.add(new Button("Add New Instructor", new com.google.gwt.event.dom.client.ClickHandler() {
			@Override
			public void onClick(com.google.gwt.event.dom.client.ClickEvent event) {
				grid.startEditingNew();
			}
		}));
		
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
	
	public static Window createWin(String title, boolean autoSizing, int width, int height, int offsetLeft) {  
      Label label = new Label(  
              "<b>Severity 1</b> - Critical problem<br/>System is unavailable in production or is corrupting data, and the error severely impacts the user's operations.<br/><br/>"  
                      + "<b>Severity 2</b> - Major problem<br/>An important function of the system is not available in production, and the user's operations are restricted.<br/><br/>"  
                      + "<b>Severity 3</b> - Minor problem<br/>Inability to use a function of the system occurs, but it does not seriously affect the user's operations.");  
      label.setWidth100();  
      label.setHeight100();  
      label.setPadding(5);  
      label.setValign(VerticalAlignment.TOP);  

      Window window = new Window();  
      window.setAutoSize(autoSizing);  
      window.setTitle(title);  
      window.setWidth(width);  
      window.setHeight(height);  
      window.setLeft(offsetLeft);  
      window.setCanDragReposition(true);  
      window.setCanDragResize(true);  
      window.addItem(label);  

      return window;  
  }  
	
	
	public void preferencesButtonClicked(InstructorGWT instructor) {
		if (frame.canPopViewsAboveMe()) {
			// viewFrame.popFramesAboveMe();
			InstructorPreferencesView iipv = new InstructorPreferencesView(
					service, document.getID(), document.getName(), instructor);
			// final SimplePanel ipv = new SimplePanel();
			// ipv.setWidth("100%");
			// ipv.setHeight("100%");
			// ipv.add(new Button("OK"));
			// ipv.setVisible(true);
			// popup.
			// popup.add(ipv);
			// popup.setSize("100%", "100%");
			// popup.show();
			// viewFrame.add(ipv);
			// ipv.setHeight("500px");
			// ipv.setWidth("500px");
			
			final Window window = new Window();
			window.setAutoSize(true);
			window.setTitle("Instructor Preferences");
			window.setCanDragReposition(true);
			window.setCanDragResize(true);
			
			
			
			iipv.afterPush();
			final ScrollPanel weewee = new ScrollPanel();
			weewee.setWidget(iipv);
			weewee.setSize("700px", "600px");
			window.addItem(weewee);
			// poopyhead.setTitle(instructor.getFirstName() + " " +
			// instructor.getLastName() + "'s Preferences");
			// iipv.setVisible(true);
			// ipv.add(new Button("OK"));
			// ipv.add(iipv);
			// poopyhead.add(iipv);
//			poopyhead.setPopupPosition(600, 0);
			// poopyhead.add
			ClickListener listener = new ClickListener() {
				public void onClick(Widget sender) {
					window.hide();
				}
			};
			Button button = new Button("Close", listener);
			iipv.add(button);
			button.setStyleName("centerness");
			window.show();
			// ipv.setWidget(iipv);
			// ipv.setVisible(true);
			// this.add(ipv);
			// ipv.center();
			// ipv.show();
			// viewFrame.frameViewAndPushAboveMe(new
			// InstructorPreferencesView(service, document.getID(),
			// document.getName(), instructor));
		}
		else
		{
			System.out.println("ABANDON SHIP CAPTAIN");
		}
	}
	
}
