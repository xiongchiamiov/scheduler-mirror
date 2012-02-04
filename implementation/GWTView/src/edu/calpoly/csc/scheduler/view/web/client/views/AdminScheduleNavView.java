package edu.calpoly.csc.scheduler.view.web.client.views;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

import edu.calpoly.csc.scheduler.view.web.client.CourseCache;
import edu.calpoly.csc.scheduler.view.web.client.GreetingServiceAsync;
import edu.calpoly.csc.scheduler.view.web.client.IViewContents;
import edu.calpoly.csc.scheduler.view.web.client.Import;
import edu.calpoly.csc.scheduler.view.web.client.ViewFrame;

public class AdminScheduleNavView extends SimplePanel implements IViewContents {
	final GreetingServiceAsync service;
	final String username;
	final Integer scheduleID;
	final String scheduleName;
	final MenuBar menuBar;
	
	MenuItem fileMenu, settingsMenu;
	MenuItem instructorsMenuItem, locationsMenuItem, coursesMenuItem, scheduleMenuItem;

	public AdminScheduleNavView(GreetingServiceAsync service, MenuBar MenuBar,
			String username, Integer scheduleID, String scheduleName) {
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
		MenuBar fileMenu = new MenuBar(true);
		DOM.setElementAttribute(fileMenu.getElement(), "id", "fileMenu");
		
		MenuItem newItem = new MenuItem("New", true, new Command() {
			public void execute() {
				final String tempName = "Untitled";
				final LoadingPopup popup = new LoadingPopup();
				
				DOM.setElementAttribute(popup.getElement(), "id", "failOpenSched");
				
				popup.show();
				
				service.openNewSchedule(tempName, new AsyncCallback<Integer>() {
					@Override
					public void onFailure(Throwable caught) {
						popup.hide();
						Window.alert("Failed to open new schedule in: " + caught.getMessage());
					}
					
					@Override
					public void onSuccess(Integer newSchedID) {
						popup.hide();
						if (viewFrame.canPopViewsAboveMe()) {
							viewFrame.popFramesAboveMe();
							viewFrame.frameViewAndPushAboveMe(new AdminScheduleNavView(service, menuBar, username, newSchedID, tempName));
						}
					}
				});
			}
		});
		DOM.setElementAttribute(newItem.getElement(), "id", "newItem");
		fileMenu.addItem(newItem);

		MenuItem importItem = new MenuItem("Import", true, new Command() {
			public void execute() {
				Import.showImport();
			}
		});

		DOM.setElementAttribute(importItem.getElement(), "id", "importItem");
		fileMenu.addItem(importItem);
		
		MenuItem saveAsItem = new MenuItem("Save As...", true, new Command() {
			public void execute() {
				Window.alert("implement");
//				displaySaveAsPopup();
			}
		});
		
		DOM.setElementAttribute(saveAsItem.getElement(), "id", "saveAsItem");
		fileMenu.addItem(saveAsItem);
		
		MenuItem exportItem = new MenuItem("Export...", true, new Command() {
			public void execute() {
				Window.alert("implement");
				
//				displayExportPopup();
				
				//TODO add to export popup box
				/*
				service.exportCSV(new AsyncCallback<Integer>() {
					public void onFailure(Throwable caught) {
						Window.alert("Error exporting to CSV: 1");
					}
					public void onSuccess(Integer result) {
						if(result == null)
							Window.alert("Error exporting to CSV: 2");
						else
							Window.Location.replace("export?"
								+ "param" + "=" + result);
					}
				});
			    */
			}
			
		});
		
		DOM.setElementAttribute(exportItem.getElement(), "id", "exportItem");
		fileMenu.addItem(exportItem);
		
		MenuItem mergeItem = new MenuItem("Merge", true, new Command() {
			public void execute() {
				Window.alert("implement");
//				displayMergePopup();
			}
		});
		
		DOM.setElementAttribute(saveAsItem.getElement(), "id", "mergeItem");
		fileMenu.addItem(mergeItem);
		
//		fileMenuItem = new MenuItem("File v", true, fileMenu);
//		DOM.setElementAttribute(fileMenuItem.getElement(), "id", "FileVIitem");
	}
	
	private void makeSettingsMenu() {
		MenuBar settingsMenu = new MenuBar(true);
		DOM.setElementAttribute(fileMenu.getElement(), "id", "settingsMenu");

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
							viewFrame.frameViewAndPushAboveMe(new LocationsView(
									service, scheduleName));
						}
					}
				}));

		menuBar.addItem(coursesMenuItem = new MenuItem("Courses", true,
				new Command() {
					public void execute() {
						if (viewFrame.canPopViewsAboveMe()) {
							viewFrame.popFramesAboveMe();
							viewFrame.frameViewAndPushAboveMe(new CoursesView(new CourseCache(service), scheduleName));
						}
					}
				}));

		menuBar.addItem(scheduleMenuItem = new MenuItem("Schedule", true,
				new Command() {
					public void execute() {
						if (viewFrame.canPopViewsAboveMe()) {
							viewFrame.popFramesAboveMe();
							viewFrame.frameViewAndPushAboveMe(new ScheduleView(
									service, scheduleName));
						}
					}
				}));
	}
	
	private void addMenus() {
		menuBar.addItem(fileMenu);
		menuBar.addItem(settingsMenu);
		menuBar.addItem(coursesMenuItem);
		menuBar.addItem(locationsMenuItem);
		menuBar.addItem(instructorsMenuItem);
		menuBar.addItem(scheduleMenuItem);
	}
	
	private void removeMenus() {
		menuBar.removeItem(fileMenu);
		menuBar.removeItem(settingsMenu);
		menuBar.removeItem(coursesMenuItem);
		menuBar.removeItem(locationsMenuItem);
		menuBar.removeItem(instructorsMenuItem);
		menuBar.removeItem(scheduleMenuItem);
	}
}
