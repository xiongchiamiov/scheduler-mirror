package edu.calpoly.csc.scheduler.view.web.client.views;

import java.util.ArrayList;
import java.util.HashMap;
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
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import edu.calpoly.csc.scheduler.view.web.client.GreetingServiceAsync;
import edu.calpoly.csc.scheduler.view.web.client.IViewContents;
import edu.calpoly.csc.scheduler.view.web.client.Import;
import edu.calpoly.csc.scheduler.view.web.client.ViewFrame;
import edu.calpoly.csc.scheduler.view.web.shared.InstructorGWT;
import edu.calpoly.csc.scheduler.view.web.shared.Pair;
import edu.calpoly.csc.scheduler.view.web.shared.UserDataGWT;

public class SelectScheduleView extends VerticalPanel implements IViewContents {
	private final GreetingServiceAsync service;
	
	private final MenuBar menuBar;
	MenuItem fileMenuItem;
	MenuItem settingsMenuItem;
	
	private final String username;
	private ListBox listBox;
	
	private ViewFrame myFrame;

	Map<String, UserDataGWT> availableSchedulesByName;
	
	public SelectScheduleView(final GreetingServiceAsync service, final MenuBar menuBar, final String username) {
		this.service = service;
		this.menuBar = menuBar;
		this.username = username;

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
						if (myFrame.canPopViewsAboveMe()) {
							myFrame.popFramesAboveMe();
							myFrame.frameViewAndPushAboveMe(new AdminScheduleNavView(service, menuBar, username, newSchedID, tempName));
						}
					}
				});
			}
		});
		
		DOM.setElementAttribute(newItem.getElement(), "id", "newScheduleBtn");
		fileMenu.addItem(newItem);
	
		
		MenuItem openItem = new MenuItem("Open", true, new Command() {
			public void execute() {
				displayOpenPopup();
			}
		});
		
		DOM.setElementAttribute(openItem.getElement(), "id", "openItem");
		fileMenu.addItem(openItem);
		
		
		MenuItem importItem = new MenuItem("Import", true, new Command() {
			public void execute() {
				Import.showImport();
			}
		});
		
		DOM.setElementAttribute(importItem.getElement(), "id", "importItem");
		fileMenu.addItem(importItem);
		
		MenuItem saveAsItem = new MenuItem("Save As...", true, new Command() {
			public void execute() {
				displaySaveAsPopup();
			}
		});
		
		DOM.setElementAttribute(saveAsItem.getElement(), "id", "saveAsItem");
		fileMenu.addItem(saveAsItem);
		
		MenuItem exportItem = new MenuItem("Download As...", true, new Command() {
			public void execute() {
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
			}
		});
		
		DOM.setElementAttribute(exportItem.getElement(), "id", "exportItem");
		fileMenu.addItem(exportItem);
		
		MenuItem mergeItem = new MenuItem("Merge", true, new Command() {
			public void execute() {
				displayMergePopup();
			}
		});
		
		DOM.setElementAttribute(saveAsItem.getElement(), "id", "mergeItem");
		fileMenu.addItem(mergeItem);
		
		fileMenuItem = new MenuItem("File v", true, fileMenu);
		DOM.setElementAttribute(fileMenuItem.getElement(), "id", "FileVIitem");
		
		
		

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
		
		settingsMenuItem = new MenuItem("Settings v", true, settingsMenu);
		DOM.setElementAttribute(settingsMenuItem.getElement(), "id", "SettingsItem");
		
		this.addStyleName("homeView");
		
		this.setWidth("100%");
		this.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		
		Button newSchedButton = new Button("Create New Schedule", new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				final String tempName = "Untitled";
				final LoadingPopup popup = new LoadingPopup();
				popup.show();
				
				DOM.setElementAttribute(popup.getElement(), "id", "failSchedPopup");
				
				service.openNewSchedule(tempName, new AsyncCallback<Integer>() {
					@Override
					public void onFailure(Throwable caught) {
						popup.hide();
						Window.alert("Failed to open new schedule in: " + caught.getMessage());
					}
					
					@Override
					public void onSuccess(Integer newSchedID) {
						popup.hide();
						myFrame.frameViewAndPushAboveMe(new AdminScheduleNavView(service, menuBar, username, newSchedID, tempName));
					}
				});
			}
		});
		
		DOM.setElementAttribute(newSchedButton.getElement(), "id", "newScheduleButton");
		
		this.add(new HTMLPanel("<h3>Open a Schedule</h3>"));
		
		listBox = new ListBox();
		listBox.setVisibleItemCount(5);
		
		DOM.setElementAttribute(listBox.getElement(), "id", "listBox");
		
		this.add(listBox);

		Button openButton = new Button("Open", new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				int index = listBox.getSelectedIndex();
				if (index < 0)
					return;
				
				int existingScheduleID = Integer.parseInt(listBox.getValue(index));
				String scheduleName = listBox.getItemText(index);
				selectSchedule(existingScheduleID, scheduleName);
			}
		});
		
		DOM.setElementAttribute(openButton.getElement(), "id", "openButton");
		
		FlowPanel flow = new FlowPanel();
        flow.add(newSchedButton);
		flow.add(openButton);
		
		this.add(flow);
	}
	
	@Override
	public void afterPush(ViewFrame frame) {
		this.myFrame = frame;
		
		service.getScheduleNames(new AsyncCallback<Map<String,UserDataGWT>>() {
			@Override
			public void onFailure(Throwable caught) {
				Window.alert("There was an error getting the schedules: " + caught.getMessage());
			}
			
			@Override
			public void onSuccess(Map<String, UserDataGWT> result) {
				availableSchedulesByName = result;
				
				listBox.clear();
				for (String scheduleName : availableSchedulesByName.keySet())
					listBox.addItem(scheduleName, availableSchedulesByName.get(scheduleName).getScheduleID().toString());
			}
		});
		
		menuBar.addItem(fileMenuItem);
	}
	
	@Override
	public void beforePop() {
		menuBar.removeItem(fileMenuItem);
	}
	
	/**
	 * Private method for selecting a previously saved schedule from the database given the schedule ID
	 * and the schedule name.
	 * 
	 * @param scheduleID ID of the schedule to open.
	 * @param scheduleName Name of the schedule to open.
	 */
	private void selectSchedule(final int scheduleID, final String scheduleName) {
		final LoadingPopup popup = new LoadingPopup();
		popup.show();
		
		service.openExistingSchedule(scheduleID, new AsyncCallback<Pair<Integer, InstructorGWT>>() {
			@Override
			public void onFailure(Throwable caught) {
				popup.hide();
				Window.alert("Failed to open schedule in: " + caught.getMessage());
			}
			@Override
			public void onSuccess(Pair<Integer, InstructorGWT> permissionAndInstructor) {
				popup.hide();
				
				if (myFrame.canPopViewsAboveMe()) {
					myFrame.popFramesAboveMe();
					
					switch (permissionAndInstructor.getLeft()) {
					case 0: // todo: enumify
						myFrame.frameViewAndPushAboveMe(new GuestScheduleNavView(service, menuBar, scheduleName));
						break;
					case 1: // todo: enumify
						myFrame.frameViewAndPushAboveMe(new InstructorScheduleNavView(service, menuBar, scheduleName, permissionAndInstructor.getRight()));
						break;
					case 2: // todo: enumify
						myFrame.frameViewAndPushAboveMe(new AdminScheduleNavView(service, menuBar, username, scheduleID, scheduleName));
						break;
					default:
						assert(false);
					}
				}
			}
		});
	}
	
	interface NameScheduleCallback {
		void namedSchedule(String name);
	}
	
	/**
	 * Displays a popup for specifying a new schedule.
	 * @param buttonLabel
	 * @param callback
	 */
	public void displayNewSchedPopup(String buttonLabel, final NameScheduleCallback callback) {
		final TextBox tb = new TextBox();
		final DialogBox db = new DialogBox(false);
		FlowPanel fp = new FlowPanel();
		final Button butt = new Button(buttonLabel, new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {		
				db.hide();
				
			    final String scheduleName = tb.getText();
			    
			    callback.namedSchedule(scheduleName);
			}
		});
		final Button cancelButton = new Button("Cancel", new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				db.hide();
			}
		});
		
		tb.addKeyPressHandler(new KeyPressHandler() {
			@Override
			public void onKeyPress(KeyPressEvent event) {
				if (event.getCharCode() == KeyCodes.KEY_ENTER)
					butt.click();
			}
		});
		
		db.setText("Name Schedule");
		fp.add(new HTML("<center>Specify a new schedule name.</center>"));
		fp.add(tb);
		fp.add(butt);
		fp.add(cancelButton);
		
		db.setWidget(fp);
		db.center();
	}
	
	/**
	 * Displays a popup for selecting and opening a previously saved schedule.
	 */
	public void displayOpenPopup() {
		final DialogBox db = new DialogBox();
		FlowPanel fp = new FlowPanel();
		final ListBox listBox = new ListBox();
		final Map<String,UserDataGWT> schedules = new HashMap<String,UserDataGWT>();

		service.getScheduleNames(new AsyncCallback<Map<String,UserDataGWT>>() {
			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Failed to open schedule in: " + caught.getMessage());
			}
			
			@Override
			public void onSuccess(Map<String,UserDataGWT> result) {
				schedules.putAll(result);
				for(String name : result.keySet())
					listBox.addItem(name);
			}
		});
		
		listBox.setVisibleItemCount(5);
		fp.add(listBox);
		
		fp.add(new HTML("<br />"));
		
		fp.add(new Button("Open", new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				int index = listBox.getSelectedIndex();
				String scheduleName = listBox.getItemText(index);
				int scheduleID = schedules.get(scheduleName).getScheduleID();
				selectSchedule(scheduleID, scheduleName);
				db.hide();
			}
		}));
		
		fp.add(new Button("Cancel", new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				db.hide();
			}
		}));
		
		db.add(fp);
		
		db.setText("Open a Schedule");
		db.center();
		db.show();
	}
	
	/**
	 * Displays a popup to save the schedule under a different name.
	 */
	public void displaySaveAsPopup() {
		final ListBox saveAsListBox = new ListBox();
		final ArrayList<String> schedNames = new ArrayList<String>();
		final TextBox tb = new TextBox();
		final DialogBox db = new DialogBox();
		FlowPanel fp = new FlowPanel();
		final Button saveButton = new Button("Save", new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {			
			    final String scheduleName = tb.getText();
			    
			    if(!scheduleName.isEmpty()) {
			    
			        if(schedNames.contains(scheduleName)) {
			    	    boolean result = Window.confirm("The schedule \"" + scheduleName + "\" already exists.  Are you sure you want to replace it?");
			    	
			    	    if(result) {
			    		    //Save the schedule
			    	    	service.removeSchedule(scheduleName, null);
			    	    	service.saveCurrentScheduleAs(scheduleName, null);
			    	    }
			        }
			        else {
			        	service.saveCurrentScheduleAs(scheduleName, null);
			        }
			    }
			    
				db.hide();
			}
		});
		
		final Button cancelButton = new Button("Cancel", new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				db.hide();
			}
		});
		
		tb.addKeyPressHandler(new KeyPressHandler() {
			@Override
			public void onKeyPress(KeyPressEvent event) {
				if (event.getCharCode() == KeyCodes.KEY_ENTER)
					saveButton.click();
			}
		});
		
		service.getScheduleNames(new AsyncCallback<Map<String,UserDataGWT>>() {
			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Failed to open schedule in: " + caught.getMessage());
			}
			
			@Override
			public void onSuccess(Map<String,UserDataGWT> result) {
				for(String name : result.keySet()) {
					saveAsListBox.addItem(name);
					schedNames.add(name);
				}
			}
		});
		
		db.setText("Name Schedule");
		fp.add(new HTML("<center>Specify a name to save the schedule as...</center>"));
		saveAsListBox.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
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
	
	/**
	 * Display popup to merge schedules together
	 */
	public void displayMergePopup() {
		final ArrayList<CheckBox> checkBoxList = new ArrayList<CheckBox>();
		final DialogBox db = new DialogBox();
		final VerticalPanel vp = new VerticalPanel();
		final VerticalPanel checkBoxPanel = new VerticalPanel();
		FlowPanel fp = new FlowPanel();
		
		final Button mergeButton = new Button("Merge", new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				int checkCount = 0;
				
				for(CheckBox cb : checkBoxList) {
					if(cb.getValue())
						checkCount++;
				}
				
				if(checkCount >= 2) {
					//TODO - Add merge call here when functionality is implemented
				    db.hide();
				}
				else {
					Window.alert("Please select 2 or more schedules to merge.");
				}
			}
		});
		
		final Button cancelButton = new Button("Cancel", new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				db.hide();
			}
		});
		
		service.getScheduleNames(new AsyncCallback<Map<String,UserDataGWT>>() {
			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Failed to open schedule in: " + caught.getMessage());
			}
			
			@Override
			public void onSuccess(Map<String,UserDataGWT> result) {
				for(String name : result.keySet()) {
					CheckBox checkBox = new CheckBox(name);
					checkBoxList.add(checkBox);
					checkBoxPanel.add(checkBox);
				}
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

	@Override
	public boolean canPop() { return true; }
	
	@Override
	public void beforeViewPushedAboveMe() {
		menuBar.addItem(settingsMenuItem);
	}
	
	@Override
	public void afterViewPoppedFromAboveMe() {
		menuBar.removeItem(settingsMenuItem);
	}
	
	@Override
	public Widget getContents() { return this; }
}
