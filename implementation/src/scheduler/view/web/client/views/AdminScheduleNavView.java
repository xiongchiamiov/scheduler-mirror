package scheduler.view.web.client.views;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import scheduler.view.web.client.GreetingServiceAsync;
import scheduler.view.web.client.IViewContents;
import scheduler.view.web.client.Import;
import scheduler.view.web.client.NewScheduleCreator;
import scheduler.view.web.client.ViewFrame;
import scheduler.view.web.client.views.resources.courses.CoursesView;
import scheduler.view.web.client.views.resources.instructors.InstructorsView;
import scheduler.view.web.client.views.resources.locations.LocationsView;
import scheduler.view.web.shared.DocumentGWT;
import scheduler.view.web.shared.UserDataGWT;

public class AdminScheduleNavView extends SimplePanel implements IViewContents {
	public interface OtherFilesStrategy {
		void fileNewPressed();
		void fileOpenPressed();
		void fileImportPressed();
		void fileMergePressed();
	}
	
	final GreetingServiceAsync service;
	final String username;
	final DocumentGWT document;
	final MenuBar menuBar;
	
	MenuBar fileMenu, settingsMenu;
	MenuItem instructorsMenuItem, locationsMenuItem, coursesMenuItem, scheduleMenuItem;

	public AdminScheduleNavView(GreetingServiceAsync service, MenuBar menuBar,
			String username, DocumentGWT document) {
		this.service = service;
		this.username = username;
		this.document = document;
		this.menuBar = menuBar;
	}

	@Override
	public Widget getContents() { return this; }

	@Override
	public void afterPush(final ViewFrame viewFrame) {
		makeFileMenu(viewFrame);
		makeSettingsMenu();
		makeResourcesAndScheduleViewsMenuItems(viewFrame);

		addMenus();
		
		coursesMenuItem.getCommand().execute();
	}

	@Override
	public boolean canPop() { return true; }
	
	@Override
	public void beforePop() {
		removeMenus();
	}

	@Override
	public void beforeViewPushedAboveMe() { }

	@Override
	public void afterViewPoppedFromAboveMe() { }
	
	
	
	private void makeFileMenu(final ViewFrame viewFrame) {
		fileMenu = new MenuBar(true);
		DOM.setElementAttribute(fileMenu.getElement(), "id", "fileMenu");
		
		MenuItem newItem = new MenuItem("New", true, new Command() {
			public void execute() {
				fileNewPressed();
			}
		});
		DOM.setElementAttribute(newItem.getElement(), "id", "newItem");
		fileMenu.addItem(newItem);

		MenuItem openItem = new MenuItem("Open", true, new Command() {
			public void execute() {
				fileOpenPressed();
			}
		});
		DOM.setElementAttribute(openItem.getElement(), "id", "openItem");
		fileMenu.addItem(openItem);
		
		fileMenu.addSeparator();

		MenuItem closeItem = new MenuItem("Close", true, new Command() {
			public void execute() {
				afterClosePressed();
			}
		});
		
		DOM.setElementAttribute(closeItem.getElement(), "id", "closeItem");
		fileMenu.addItem(closeItem);

		MenuItem saveItem = new MenuItem("Save", true, new Command() {
			public void execute() {
				afterSavePressed();
			}
		});
		
		DOM.setElementAttribute(saveItem.getElement(), "id", "saveItem");
		fileMenu.addItem(saveItem);
		
		MenuItem saveAsItem = new MenuItem("Save As", true, new Command() {
			public void execute() {
				afterSaveAsPressed();
			}
		});
		
		DOM.setElementAttribute(saveAsItem.getElement(), "id", "saveAsItem");
		fileMenu.addItem(saveAsItem);
		
		fileMenu.addSeparator();
		
		MenuItem importItem = new MenuItem("Import", true, new Command() {
			public void execute() {
				fileImportPressed();
			}
		});

		DOM.setElementAttribute(importItem.getElement(), "id", "importItem");
		fileMenu.addItem(importItem);
		
		MenuItem exportItem = new MenuItem("Export", true, new Command() {
			public void execute() {
				displayExportPopup();
			}
			
		});
		
		DOM.setElementAttribute(exportItem.getElement(), "id", "exportItem");
		fileMenu.addItem(exportItem);
		
		fileMenu.addSeparator();
		
		MenuItem printItem = new MenuItem("Print", true, new Command() {
			public void execute() {
				Window.alert("Not yet implemented");
			}
		});
		
		DOM.setElementAttribute(printItem.getElement(), "id", "printItem");
		fileMenu.addItem(printItem);
		
		
		MenuItem mergeItem = new MenuItem("Merge", true, new Command() {
			public void execute() {
				fileMergePressed();
			}
		});
		
		DOM.setElementAttribute(mergeItem.getElement(), "id", "mergeItem");
		fileMenu.addItem(mergeItem);
		
//		fileMenuItem = new MenuItem("File v", true, fileMenu);
//		DOM.setElementAttribute(fileMenuItem.getElement(), "id", "FileVIitem");
	}
	
	protected void afterClosePressed() {
		assert(false);
	}

	protected void afterSavePressed() {
		service.saveWorkingCopyToOriginalDocument(document.getID(), new AsyncCallback<Void>() {
			@Override
			public void onSuccess(Void result) {
				Window.alert("Successfully saved!");
			}
			
			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Failed to save! " + caught.getMessage());
			}
		});
	}

	protected void afterSaveAsPressed() {

	      final ListBox saveAsListBox = new ListBox();
	      final ArrayList<String> schedNames = new ArrayList<String>();
	      final TextBox tb = new TextBox();
	      final DialogBox db = new DialogBox();
	      FlowPanel fp = new FlowPanel();
	      final Button saveButton = new Button("Save", new ClickHandler()
	      {
	         @Override
	         public void onClick(ClickEvent event)
	         {
	            db.hide();

	            final String scheduleName = tb.getText();
	            if (scheduleName.isEmpty()) return;

	            boolean allowOverwrite = false;
	            if (schedNames.contains(scheduleName))
	            {
	               if (Window.confirm("The schedule \"" + scheduleName
	                     + "\" already exists.  Are you sure you want to replace it?"))
	                  allowOverwrite = true;
	               else return;
	            }

	            service.moveWorkingCopyToNewOriginalDocument(document.getID(), scheduleName, allowOverwrite, new AsyncCallback<Void>() {
	            	@Override
	            	public void onFailure(Throwable caught) {
	            		// TODO Auto-generated method stub
	            		
	            	}
	            	@Override
	            	public void onSuccess(Void v) {
	            		Window.alert("Successfully saved.");
	            	}
	            });
	         }
	      });

	      final Button cancelButton = new Button("Cancel", new ClickHandler()
	      {
	         @Override
	         public void onClick(ClickEvent event)
	         {
	            db.hide();
	         }
	      });

	      tb.addKeyPressHandler(new KeyPressHandler()
	      {
	         @Override
	         public void onKeyPress(KeyPressEvent event)
	         {
	            if (event.getCharCode() == KeyCodes.KEY_ENTER) saveButton.click();
	         }
	      });

	      service.getAllOriginalDocuments(new AsyncCallback<Collection<DocumentGWT>>() {
			
			@Override
			public void onSuccess(Collection<DocumentGWT> result) {
	            for (DocumentGWT doc : result)
	            {
	               saveAsListBox.addItem(doc.getName());
	               schedNames.add(doc.getName());
	            }
			}
			
			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Failed to get existing document names.");
			}
		});

	      db.setText("Name Schedule");
	      fp.add(new HTML("<center>Specify a name to save the schedule as...</center>"));
	      saveAsListBox.addClickHandler(new ClickHandler()
	      {
	         @Override
	         public void onClick(ClickEvent event)
	         {
	            tb.setText(saveAsListBox.getValue(saveAsListBox.getSelectedIndex()));
	         }
	      });
	      saveAsListBox.setVisibleItemCount(5);
	      fp.add(saveAsListBox);
	      fp.add(tb);
	      fp.add(saveButton);
	      fp.add(cancelButton);

	      db.setWidget(fp);
	      db.center();
	      db.show();
	}

	private void makeSettingsMenu() {
		settingsMenu = new MenuBar(true);
		DOM.setElementAttribute(settingsMenu.getElement(), "id", "settingsMenu");

		MenuItem timesItem = new MenuItem("Times", true, new Command() {
			public void execute() {
				Window.alert("Unimplemented");
			}
		});
		DOM.setElementAttribute(timesItem.getElement(), "id", "timesItem");
		settingsMenu.addItem(timesItem);

		MenuItem preferencesItem = new MenuItem("Preferences", true, new Command() {
			public void execute() {
				Window.alert("Unimplemented");
			}
		});
		DOM.setElementAttribute(preferencesItem.getElement(), "id", "preferencesItem");
		settingsMenu.addItem(preferencesItem);

		MenuItem permissionsItem = new MenuItem("Permissions/Roles", true, new Command() {
			public void execute() {
				Window.alert("Unimplemented");
			}
		});
		DOM.setElementAttribute(permissionsItem.getElement(), "id", "timesItem");
		settingsMenu.addItem(permissionsItem);
	}
	
	private void makeResourcesAndScheduleViewsMenuItems(final ViewFrame viewFrame) {
		menuBar.addItem(instructorsMenuItem = new MenuItem("Instructors", true,
				new Command() {
					public void execute() {
						if (viewFrame.canPopViewsAboveMe()) {
							viewFrame.popFramesAboveMe();
							viewFrame.frameViewAndPushAboveMe(new InstructorsView(service, document));
						}
					}
				}));

		menuBar.addItem(locationsMenuItem = new MenuItem("Locations", true,
				new Command() {
					public void execute() {
						if (viewFrame.canPopViewsAboveMe()) {
							viewFrame.popFramesAboveMe();
							viewFrame.frameViewAndPushAboveMe(new LocationsView(service, document));
						}
					}
				}));

		menuBar.addItem(coursesMenuItem = new MenuItem("Courses", true,
				new Command() {
					public void execute() {
						if (viewFrame.canPopViewsAboveMe()) {
							viewFrame.popFramesAboveMe();
							viewFrame.frameViewAndPushAboveMe(new CoursesView(service, document));
						}
					}
				}));

		menuBar.addItem(scheduleMenuItem = new MenuItem("Schedule", true,
				new Command() {
					public void execute() {
						if (viewFrame.canPopViewsAboveMe()) {
							viewFrame.popFramesAboveMe();
							viewFrame.frameViewAndPushAboveMe(new CalendarView(service, document));
						}
					}
				}));
	}
	
	private void addMenus() {
	   menuBar.clearItems();
		menuBar.addItem("File v", fileMenu);
		menuBar.addItem("Settings v", settingsMenu);
		menuBar.addItem(coursesMenuItem);
		menuBar.addItem(instructorsMenuItem);
		menuBar.addItem(locationsMenuItem);
		menuBar.addItem(scheduleMenuItem);
	}
	
	private void removeMenus() {
		menuBar.removeItem(scheduleMenuItem);
		menuBar.removeItem(instructorsMenuItem);
		menuBar.removeItem(locationsMenuItem);
		menuBar.removeItem(coursesMenuItem);
		settingsMenu.removeFromParent();
		fileMenu.removeFromParent();
	}
	

	/**
	 * Displays a popup to export schedule.
	 */
	public void displayExportPopup()
	{	
		final DialogBox db = new DialogBox();
		VerticalPanel mainVerticalPanel = new VerticalPanel();
		
		VerticalPanel verticalPanel = new VerticalPanel();
		mainVerticalPanel.add(verticalPanel);
		
		HorizontalPanel typeSelectorPanel = new HorizontalPanel();
		verticalPanel.add(typeSelectorPanel);
		
		final FocusPanel csvFocusPanel = new FocusPanel();
		csvFocusPanel.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				//TODO Save Selection
			}
		});
		
		FocusPanel pdfFocusPanel = new FocusPanel();
		pdfFocusPanel.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				//TODO Save Selection
			}
		});
		
		pdfFocusPanel.setStyleName("exportChoice");
		typeSelectorPanel.add(pdfFocusPanel);
		pdfFocusPanel.setSize("", "");
		
		VerticalPanel pdfPanel = new VerticalPanel();
		pdfFocusPanel.setWidget(pdfPanel);
		pdfPanel.setSize("100px", "100px");
		
		Image PDFIcon = new Image("imgs/pdf-icon.png");
		pdfPanel.add(PDFIcon);
		PDFIcon.setSize("100px", "100px");
		
		Label lblCSV = new Label("PDF");
		pdfPanel.add(lblCSV);
		pdfPanel.setCellHorizontalAlignment(lblCSV, HasHorizontalAlignment.ALIGN_CENTER);
		csvFocusPanel.setStyleName("exportChoice");
		typeSelectorPanel.add(csvFocusPanel);
		typeSelectorPanel.setCellHorizontalAlignment(csvFocusPanel, HasHorizontalAlignment.ALIGN_CENTER);
		csvFocusPanel.setSize("", "");

		VerticalPanel csvPanel = new VerticalPanel();
		csvFocusPanel.setWidget(csvPanel);
		csvPanel.setSize("100px", "100px");

		Image CSVIcon = new Image("imgs/csv-icon.png");
		csvPanel.add(CSVIcon);
		CSVIcon.setSize("100px", "100px");

		Label lblNewLabel = new Label("Excel (CSV)");
		csvPanel.add(lblNewLabel);
		csvPanel.setCellHorizontalAlignment(lblNewLabel, HasHorizontalAlignment.ALIGN_CENTER);
		
		final HorizontalPanel Buttons = new HorizontalPanel();
		Buttons.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);

		
		
		mainVerticalPanel.add(Buttons);
		Buttons.setWidth("102px");
		mainVerticalPanel.setCellHorizontalAlignment(Buttons, HasHorizontalAlignment.ALIGN_CENTER);
		
		final Button cancelButton = new Button("Cancel", new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				db.hide();
			}
		});
		
		
		final Button nextButton = new Button("Next", new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {

				db.hide();
				//TODO Add Selection between PDF and CSV options

				//Temporary dialogue box until CSV functionality is officially integrate
				
				final DialogBox TODOdb = new DialogBox();
				VerticalPanel TODOverticalPanel = new VerticalPanel();
				VerticalPanel TODOmainVerticalPanel = new VerticalPanel();

				TODOmainVerticalPanel.add(TODOverticalPanel);
				TODOmainVerticalPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
				
				Label lblTODO = new Label("This feature is not yet implemented.");
				TODOmainVerticalPanel.add(lblTODO);
				
				TODOdb.setText("Not yet implemented");
				
				final Button TODOcancelButton = new Button("Cancel", new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
						TODOdb.hide();
					}
				});
				
				TODOmainVerticalPanel.add(TODOcancelButton);

				
				TODOdb.setWidget(TODOmainVerticalPanel);
				TODOdb.center();
				TODOdb.show();
			}
		});
		
		
		Buttons.add(cancelButton);
		Buttons.add(nextButton);
		nextButton.setWidth("65px");
		
		db.setText("Export As");
		db.setWidget(mainVerticalPanel);
		mainVerticalPanel.setSize("103px", "23px");
		
	
		db.center();
		db.show();

	}
	

   private void fileNewPressed()
   {
      NewScheduleCreator.createNewSchedule(service, username);
   }

   private void fileOpenPressed()
   {
      String baseHref = Window.Location.getHref().substring(0, Window.Location.getHref().lastIndexOf('?'));
      Window.open(baseHref + "?userid=" + username, "_new", null);
   }

   private void fileImportPressed()
   {
      Import.showImport();
   }

   private void fileMergePressed()
   {

      final ArrayList<CheckBox> checkBoxList = new ArrayList<CheckBox>();
      final DialogBox db = new DialogBox();
      final VerticalPanel vp = new VerticalPanel();
      final VerticalPanel checkBoxPanel = new VerticalPanel();
      FlowPanel fp = new FlowPanel();

      final Button mergeButton = new Button("Merge", new ClickHandler()
      {
         @Override
         public void onClick(ClickEvent event)
         {
            int checkCount = 0;

            for (CheckBox cb : checkBoxList)
            {
               if (cb.getValue()) checkCount++;
            }

            if (checkCount >= 2)
            {
               // TODO - Add merge call here when functionality is implemented
               db.hide();
            }
            else
            {
               Window.alert("Please select 2 or more schedules to merge.");
            }
         }
      });

      final Button cancelButton = new Button("Cancel", new ClickHandler()
      {
         @Override
         public void onClick(ClickEvent event)
         {
            db.hide();
         }
      });

      service.getAllOriginalDocuments(new AsyncCallback<Collection<DocumentGWT>>()
      {

         @Override
         public void onSuccess(Collection<DocumentGWT> result)
         {
            for (DocumentGWT doc : result)
            {
               CheckBox checkBox = new CheckBox(doc.getName());
               checkBoxList.add(checkBox);
               checkBoxPanel.add(checkBox);
            }
         }

         @Override
         public void onFailure(Throwable caught)
         {
            Window.alert("Failed to retrieve documents.");
         }
      });

      fp.add(mergeButton);
      fp.add(cancelButton);

      vp.add(checkBoxPanel);
      vp.add(fp);

      db.setText("Merge Schedules");
      db.setWidget(vp);
      db.center();
      db.show();
   }
}
