package edu.calpoly.csc.scheduler.view.web.client.views;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import edu.calpoly.csc.scheduler.view.web.client.GreetingServiceAsync;
import edu.calpoly.csc.scheduler.view.web.client.IViewContents;
import edu.calpoly.csc.scheduler.view.web.client.ViewFrame;
import edu.calpoly.csc.scheduler.view.web.client.schedule.CSVButton;
import edu.calpoly.csc.scheduler.view.web.shared.InstructorGWT;
import edu.calpoly.csc.scheduler.view.web.shared.Pair;
import edu.calpoly.csc.scheduler.view.web.shared.UserDataGWT;

public class SelectScheduleView extends VerticalPanel implements IViewContents {
	private final GreetingServiceAsync service;
	
	private final MenuBar menuBar;
	MenuItem fileMenuItem;
	
	private final int userID;
	private final String username;
	private ListBox listBox;
	
	private ViewFrame myFrame;

	Map<String, UserDataGWT> availableSchedulesByName;
	
	public SelectScheduleView(final GreetingServiceAsync service, final MenuBar menuBar, final int userID, final String username) {
		this.service = service;
		this.menuBar = menuBar;
		this.userID = userID;
		this.username = username;


		MenuBar fileMenu = new MenuBar(true);
		
		fileMenu.addItem(new MenuItem("New", true, new Command() {
			public void execute() {
				final String tempName = "Untitled";
				final LoadingPopup popup = new LoadingPopup();
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
						myFrame.frameViewAndPushAboveMe(new AdminScheduleNavView(service, menuBar, userID, username, newSchedID, tempName));
					}
				});
			}
		}));
		
		fileMenu.addItem(new MenuItem("Open", true, new Command() {
			public void execute() {
				displayOpenPopup();
			}
		}));
		
		
		fileMenu.addItem(new MenuItem("Import", true, new Command() {
			public void execute() {
				Window.alert("Import!  As long as it's not from China...");
			}
		}));
		fileMenu.addItem(new MenuItem("Save", true, new Command() {
			public void execute() {
				final LoadingPopup popup = new LoadingPopup();
				popup.show();
				service.saveSchedule(new AsyncCallback<Void>() {
					@Override
					public void onFailure(Throwable caught) {
						popup.hide();
						Window.alert("There was an error saving the schedule: " + caught.getMessage());
					}
					@Override
					public void onSuccess(Void derp) {
						popup.hide();
						Window.alert("Schedule has been saved successfully.");
					}
				});
			}
		}));
		fileMenu.addItem(new MenuItem("Save As...", true, new Command() {
			public void execute() {
				displaySaveAsPopup();
			}
		}));
		fileMenu.addItem(new MenuItem("Export", true, new Command() {
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
		}));
		
		fileMenuItem = new MenuItem("File v", true, fileMenu);
		
		this.addStyleName("homeView");
		
		this.setWidth("100%");
		this.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		
		Button newSchedButton = new Button("Create New Schedule", new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				final String tempName = "Untitled";
				final LoadingPopup popup = new LoadingPopup();
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
						myFrame.frameViewAndPushAboveMe(new AdminScheduleNavView(service, menuBar, userID, username, newSchedID, tempName));
					}
				});
			}
		});
		
		this.add(new HTMLPanel("<h3>Open a Schedule</h3>"));
		
		listBox = new ListBox();
		listBox.setVisibleItemCount(5);
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
						myFrame.frameViewAndPushAboveMe(new AdminScheduleNavView(service, menuBar, userID, username, scheduleID, scheduleName));
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
		final TextBox tb = new TextBox();
		final DialogBox db = new DialogBox();
		FlowPanel fp = new FlowPanel();
		final Button saveButton = new Button("Save", new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {		
				db.hide();
				
			    final String scheduleName = tb.getText();
			    
			    //TODO - Need to discuss the implementation of this 
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
		
		db.setText("Name Schedule");
		fp.add(new HTML("<center>Specify a name to save the schedule as...</center>"));
		fp.add(tb);
		fp.add(saveButton);
		fp.add(cancelButton);
		
		db.setWidget(fp);
		db.center();
		db.show();
	}

	@Override
	public boolean canPop() { return true; }
	@Override
	public void beforeViewPushedAboveMe() { }
	@Override
	public void afterViewPoppedFromAboveMe() { }
	@Override
	public Widget getContents() { return this; }
}
