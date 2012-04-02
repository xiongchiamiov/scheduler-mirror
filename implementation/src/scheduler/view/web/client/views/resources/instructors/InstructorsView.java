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
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.IButton;  
import com.smartgwt.client.widgets.ImgButton;  
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
			protected Canvas createRecordComponent(final ListGridRecord record,
					Integer colNum) {
				String fieldName = this.getFieldName(colNum);
				if (fieldName.equals("instructorPrefs")) {
					IButton button = new IButton();  
                    button.setHeight(18);  
                    button.setWidth(65);                      
                    button.setTitle("Preferences");  
                    button.addClickHandler(new ClickHandler() {  
                        public void onClick(ClickEvent event) {  
							final int instructorID = record
									.getAttributeAsInt("id");
							service.getInstructorsForDocument(
									document.getID(),
									new AsyncCallback<List<InstructorGWT>>() {
										public void onFailure(
												Throwable caught) {
											com.google.gwt.user.client.Window.alert("failed to get instructors!");
										}

										@Override
										public void onSuccess(
												List<InstructorGWT> result) {
											for (InstructorGWT instructor : result) {
												if (instructor
														.getID()
														.equals(instructorID)) {
													preferencesButtonClicked(instructor);
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
	public void beforePop() {
	}

	@Override
	public void beforeViewPushedAboveMe() {
	}

	@Override
	public void afterViewPoppedFromAboveMe() {
	}

	@Override
	public Widget getContents() {
		return this;
	}

	public void preferencesButtonClicked(InstructorGWT instructor) {
		if (frame.canPopViewsAboveMe()) {
			// viewFrame.popFramesAboveMe();
			InstructorPreferencesView iipv = new InstructorPreferencesView(
					service, document.getID(), document.getName(), instructor);
			iipv.afterPush();
			final Window window = new Window();
			window.setAutoSize(true);
			window.setTitle("Instructor Preferences");
			window.setCanDragReposition(true);
			window.setCanDragResize(true);
			
			final ScrollPanel weewee = new ScrollPanel();
			weewee.setWidget(iipv);
			weewee.setSize("700px", "600px");
			window.addItem(weewee);

			ClickListener listener = new ClickListener() {
				public void onClick(Widget sender) {
					System.out.println("Got here +++++++++++++++++++++++++++++");
					window.hide();
				}
			};
			Button button = new Button("Close", listener);
			iipv.add(button);
			button.setStyleName("centerness");
			System.out.println(iipv.checkSize());
			if(iipv.checkSize())
			{
				System.out.println("Hey there........");
				window.show();
			}
			else
			{

				final Window messageWindow = new Window();
				messageWindow.centerInPage();

				VerticalPanel vp = new VerticalPanel();
				ClickListener dlgListener = new ClickListener() {
					public void onClick(Widget sender) {
						System.out.println("Got here +++++++++++++++++++++++++++++");
						//dlg.hide();
						messageWindow.hide();
						//dlg.hide();
						window.show();
					}
				};
				
				ClickListener dlgNoListener = new ClickListener() {
					public void onClick(Widget sender) {
						System.out.println("Got here *************************");
						//dlg.hide();
						messageWindow.hide();
						//dlg.hide();
					}
				};
				Button dlgButton = new Button("Yes", dlgListener);
				Button noButton = new Button("No", dlgNoListener);
				vp.add(dlgButton);
				vp.add(noButton);
				messageWindow.addItem(vp);

				window.setAutoSize(true);
				messageWindow.setSize("500px", "300px");
				messageWindow.show();	
			}
		}
		else
		{
			System.out.println("ABANDON SHIP CAPTAIN");
		}
	}

}
