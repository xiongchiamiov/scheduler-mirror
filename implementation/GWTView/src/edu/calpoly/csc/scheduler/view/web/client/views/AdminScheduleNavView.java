package edu.calpoly.csc.scheduler.view.web.client.views;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import edu.calpoly.csc.scheduler.view.web.client.GreetingServiceAsync;
import edu.calpoly.csc.scheduler.view.web.client.IViewContents;
import edu.calpoly.csc.scheduler.view.web.client.Import;
import edu.calpoly.csc.scheduler.view.web.client.ViewFrame;
import edu.calpoly.csc.scheduler.view.web.client.views.resources.courses.CoursesView;
import edu.calpoly.csc.scheduler.view.web.client.views.resources.instructors.InstructorsView;
import edu.calpoly.csc.scheduler.view.web.client.views.resources.locations.LocationsView;

public class AdminScheduleNavView extends SimplePanel implements IViewContents {
	public interface OtherFilesStrategy {
		void fileNewPressed();
		void fileOpenPressed();
		void fileImportPressed();
		void fileMergePressed();
		void fileSaveAsPressed(Integer existingDocumentID);
	}
	
	final GreetingServiceAsync service;
	final String username;
	final Integer scheduleID;
	final String scheduleName;
	final MenuBar menuBar;
	final OtherFilesStrategy otherFilesStrategy;
	
	MenuBar fileMenu, settingsMenu;
	MenuItem instructorsMenuItem, locationsMenuItem, coursesMenuItem, scheduleMenuItem;

	public AdminScheduleNavView(GreetingServiceAsync service, OtherFilesStrategy otherFilesStrategy, MenuBar MenuBar,
			String username, Integer scheduleID, String scheduleName) {
		this.otherFilesStrategy = otherFilesStrategy;
		this.service = service;
		this.username = username;
		this.scheduleID = scheduleID;
		this.scheduleName = scheduleName;
		this.menuBar = MenuBar;
		
		System.out.println("ASNV constructor");
	}

	@Override
	public Widget getContents() { return this; }

	@Override
	public void afterPush(final ViewFrame viewFrame) {
		System.out.println("ASNV afterpush begin");
		
		makeFileMenu(viewFrame);
		makeSettingsMenu();
		makeResourcesAndScheduleViewsMenuItems(viewFrame);

		addMenus();
		
		coursesMenuItem.getCommand().execute();

		System.out.println("ASNV afterpush end");
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
				otherFilesStrategy.fileNewPressed();
			}
		});
		DOM.setElementAttribute(newItem.getElement(), "id", "newItem");
		fileMenu.addItem(newItem);

		MenuItem importItem = new MenuItem("Import", true, new Command() {
			public void execute() {
				otherFilesStrategy.fileImportPressed();
			}
		});

		DOM.setElementAttribute(importItem.getElement(), "id", "importItem");
		fileMenu.addItem(importItem);
		
		MenuItem saveAsItem = new MenuItem("Save As...", true, new Command() {
			public void execute() {
				otherFilesStrategy.fileSaveAsPressed(scheduleID);
			}
		});
		
		DOM.setElementAttribute(saveAsItem.getElement(), "id", "saveAsItem");
		fileMenu.addItem(saveAsItem);
		
		MenuItem exportItem = new MenuItem("Export...", true, new Command() {
			public void execute() {
				displayExportPopup();
			}
			
		});
		
		DOM.setElementAttribute(exportItem.getElement(), "id", "exportItem");
		fileMenu.addItem(exportItem);
		
		MenuItem mergeItem = new MenuItem("Merge", true, new Command() {
			public void execute() {
				otherFilesStrategy.fileMergePressed();
			}
		});
		
		DOM.setElementAttribute(saveAsItem.getElement(), "id", "mergeItem");
		fileMenu.addItem(mergeItem);
		
//		fileMenuItem = new MenuItem("File v", true, fileMenu);
//		DOM.setElementAttribute(fileMenuItem.getElement(), "id", "FileVIitem");
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
							viewFrame.frameViewAndPushAboveMe(new InstructorsView(
									service, scheduleName));
						}
					}
				}));

		menuBar.addItem(locationsMenuItem = new MenuItem("Locations", true,
				new Command() {
					public void execute() {
						if (viewFrame.canPopViewsAboveMe()) {
							viewFrame.popFramesAboveMe();
							viewFrame.frameViewAndPushAboveMe(new LocationsView(service, scheduleName));
						}
					}
				}));

		menuBar.addItem(coursesMenuItem = new MenuItem("Courses", true,
				new Command() {
					public void execute() {
						if (viewFrame.canPopViewsAboveMe()) {
							viewFrame.popFramesAboveMe();
							viewFrame.frameViewAndPushAboveMe(new CoursesView(service, scheduleName));
						}
					}
				}));

		menuBar.addItem(scheduleMenuItem = new MenuItem("Schedule", true,
				new Command() {
					public void execute() {
						if (viewFrame.canPopViewsAboveMe()) {
							viewFrame.popFramesAboveMe();
							viewFrame.frameViewAndPushAboveMe(new ScheduleView(service, scheduleName));
						}
					}
				}));
	}
	
	private void addMenus() {
		menuBar.addItem("File v", fileMenu);
		menuBar.addItem("Settings v", settingsMenu);
		menuBar.addItem(coursesMenuItem);
		menuBar.addItem(locationsMenuItem);
		menuBar.addItem(instructorsMenuItem);
		menuBar.addItem(scheduleMenuItem);
	}
	
	private void removeMenus() {
		fileMenu.removeFromParent();
		settingsMenu.removeFromParent();
		menuBar.removeItem(coursesMenuItem);
		menuBar.removeItem(locationsMenuItem);
		menuBar.removeItem(instructorsMenuItem);
		menuBar.removeItem(scheduleMenuItem);
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
}
