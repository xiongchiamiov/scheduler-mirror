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
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.StatusCodeException;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.ScrollPanel;
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

public class SelectScheduleView extends VerticalPanel implements IViewContents, AdminScheduleNavView.OtherFilesStrategy {
	protected final GreetingServiceAsync service;
	
	private final MenuBar menuBar;
	
	private final String username;
	private String newDocName;
	private ArrayList<String> scheduleNames;
	private ListBox listBox;
	
	private VerticalPanel vdocholder;
	
	private ViewFrame myFrame;

	Map<String, UserDataGWT> availableSchedulesByName;
	
	public SelectScheduleView(final GreetingServiceAsync service, final MenuBar menuBar, final String username) {
		this.service = service;
		this.menuBar = menuBar;
		this.username = username;
		this.newDocName = "Untitled";
		this.scheduleNames = new ArrayList<String>();

		menuBar.clearItems();
		//Put tabs in menu bar
		MenuItem homeTab = new MenuItem("Home", true, new Command() {
			@Override
			public void execute() {
			   if(myFrame.canPopViewsAboveMe())
            {
               myFrame.popFramesAboveMe();
               myFrame.frameViewAndPushAboveMe(new SelectScheduleView(service, menuBar, username));
            }
			}
		});
		
		DOM.setElementAttribute(homeTab.getElement(), "id", "hometab");
		menuBar.addItem(homeTab);
	
		
		MenuItem trashTab = new MenuItem("Trash", true, new Command() {
			public void execute() {
			   if(myFrame.canPopViewsAboveMe())
			   {
			      myFrame.popFramesAboveMe();
			      myFrame.frameViewAndPushAboveMe(new ScheduleTrashView(service, menuBar, username));
			   }
			}
		});
		
		DOM.setElementAttribute(trashTab.getElement(), "id", "trashtab");
		menuBar.addItem(trashTab);
		
		//Home panel
		this.addStyleName("homeView");
		
		this.setWidth("100%");
		HorizontalPanel toprow = new HorizontalPanel();
		toprow.setWidth("100%");
		toprow.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		toprow.add(new HTMLPanel("<h3>My Scheduling Documents:</h3>"));
		
		//Buttons to top right
		toprow.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		Button newSchedButton = new Button("Create New Schedule", new ClickHandler() {
		   @Override
		   public void onClick(ClickEvent event) {
			   createNewSchedule();
		   }
		});
		DOM.setElementAttribute(newSchedButton.getElement(), "id", "newScheduleButton");
		
		Button importButton = new Button("Import", new ClickHandler()
      {
         @Override
         public void onClick(ClickEvent event)
         {
        	 Window.alert("This feature is not yet implemented");
         }
      });
		DOM.setElementAttribute(importButton.getElement(), "id", "importButton");
		FlowPanel flow = new FlowPanel();
		flow.add(newSchedButton);
		flow.add(importButton);
		toprow.add(flow);
		this.add(toprow);
		
		//Document selector
		this.setHorizontalAlignment(ALIGN_LEFT);
		ScrollPanel scroller = new ScrollPanel();
		this.add(scroller);
		vdocholder = new VerticalPanel();
		scroller.add(vdocholder);
		
		//Archive button
		this.setHorizontalAlignment(ALIGN_LEFT);
		Button archiveButton = new Button("Archive Selected Documents", new ClickHandler()
		{
		   @Override
		   public void onClick(ClickEvent event)
		   {
			   Window.alert("This feature is not yet implemented");
		   }
		});
		DOM.setElementAttribute(importButton.getElement(), "id", "archiveButton");
		this.add(archiveButton);
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
				
				vdocholder.clear();
				for (String scheduleName : availableSchedulesByName.keySet())
				{
				   addNewDocument(scheduleName, availableSchedulesByName.get(scheduleName).getScheduleID().toString());
				   scheduleNames.add(scheduleName);
				}
				
				doneAddingDocuments();
			}
		});
	}
	
	// For subclasses
	protected void doneAddingDocuments() { }
	
	private void addNewDocument(final String name, final String scheduleid)
	{
	   HorizontalPanel doc = new HorizontalPanel();
	   doc.setHorizontalAlignment(ALIGN_LEFT);
	   doc.add(new CheckBox());
	   FocusPanel docname = new FocusPanel();
	   docname.add(new HTML(name));
	   docname.addClickHandler(new ClickHandler()
      {
         @Override
         public void onClick(ClickEvent event)
         {
            Window.open(Window.Location.getHref() + "?scheduleid=" + scheduleid + "&schedulename=" + name + "&userid=" + username, "_new", null);
//            openInNewWindow(Window.Location.getHref(), scheduleid);
//            selectSchedule(Integer.parseInt(scheduleid), name);
         }
      });
	   doc.add(docname);
	   
	   vdocholder.add(doc);
	}
	
	private static native void openInNewWindow(String url, String scheduleDBID) /*-{
	   window.open(url, 'target=schedule' + scheduleDBID);
	}-*/;
	
	@Override
	public void beforePop() {
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
		
		System.out.println("SelectScheduleView.selectSchedule(" + scheduleID + ", " + scheduleName + ")");
		
		service.openExistingSchedule(scheduleID, new AsyncCallback<String>() {
			@Override
			public void onFailure(Throwable caught) {
				popup.hide();

				System.out.println("selectSchedule onFailure");
				
				// This is a workaround, see http://code.google.com/p/google-web-toolkit/issues/detail?id=2858
				if (caught instanceof StatusCodeException && ((StatusCodeException)caught).getStatusCode() == 0) {
					// Do nothing
				}
				else {
					Window.alert("Failed to open schedule in: " + caught.getMessage());
				}
			}
			@Override
			public void onSuccess(String name) {
				popup.hide();
				
				System.out.println("selectSchedule onSuccess");

				openLoadedSchedule(scheduleID, scheduleName);
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
	private void displayNewSchedPopup(String buttonLabel, final NameScheduleCallback callback) {
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
	private void displayOpenPopup() {
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
	private void displaySaveAsPopup() {
		final ListBox saveAsListBox = new ListBox();
		final ArrayList<String> schedNames = new ArrayList<String>();
		final TextBox tb = new TextBox();
		final DialogBox db = new DialogBox();
		FlowPanel fp = new FlowPanel();
		
		final Button saveButton = new Button("Save", new ClickHandler() {
		@Override
		public void onClick(ClickEvent event) {		
			db.hide();
			
		    final String scheduleName = tb.getText();
		    if(scheduleName.isEmpty())
		    	return;

		    boolean allowOverwrite = false;
	    	if (schedNames.contains(scheduleName)) {
	    		if (Window.confirm("The schedule \"" + scheduleName + "\" already exists.  Are you sure you want to replace it?"))
	    			allowOverwrite = true;
	    		else
	    			return;
	    	}
	    	
        	service.saveCurrentScheduleAsAndOpen(scheduleName, allowOverwrite, new AsyncCallback<Integer>() {
				public void onFailure(Throwable caught) {
					Window.alert("Failed to save schedule: " + caught.getMessage());
				}
				public void onSuccess(Integer newScheduleID) {
					openLoadedSchedule(newScheduleID, scheduleName);
				}
			});
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
	
	@Override
	public boolean canPop() { return true; }
	
	@Override
	public void beforeViewPushedAboveMe() { }
	
	@Override
	public void afterViewPoppedFromAboveMe() { }
	
	@Override
	public Widget getContents() { return this; }

	@Override
	public void fileNewPressed() {
		createNewSchedule();
	}

	@Override
	public void fileOpenPressed() {
		Window.alert("implement");
	}

	@Override
	public void fileImportPressed() {
		Import.showImport();
	}

	@Override
	public void fileMergePressed() {

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
	public void fileSaveAsPressed(Integer existingDocumentID) {
		final ListBox saveAsListBox = new ListBox();
		final ArrayList<String> schedNames = new ArrayList<String>();
		final TextBox tb = new TextBox();
		final DialogBox db = new DialogBox();
		FlowPanel fp = new FlowPanel();
		final Button saveButton = new Button("Save", new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {		
				db.hide();
				
			    final String scheduleName = tb.getText();
			    if(scheduleName.isEmpty())
			    	return;

			    boolean allowOverwrite = false;
		    	if (schedNames.contains(scheduleName)) {
		    		if (Window.confirm("The schedule \"" + scheduleName + "\" already exists.  Are you sure you want to replace it?"))
		    			allowOverwrite = true;
		    		else
		    			return;
		    	}
		    	
	        	service.saveCurrentScheduleAsAndOpen(scheduleName, allowOverwrite, new AsyncCallback<Integer>() {
					public void onFailure(Throwable caught) {
						Window.alert("Failed to save schedule: " + caught.getMessage());
					}
					public void onSuccess(Integer newScheduleID) {
						openLoadedSchedule(newScheduleID, scheduleName);
					}
				});
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
	

	private void createNewSchedule() {

	      displayNewSchedPopup("Create", new NameScheduleCallback()
      {
         @Override
         public void namedSchedule(final String name)
         {
            if(!scheduleNames.contains(name))
            {
               newDocName = name;
               final LoadingPopup popup = new LoadingPopup();
               popup.show();
               
               DOM.setElementAttribute(popup.getElement(), "id", "failSchedPopup");
               
               service.openNewSchedule(newDocName, new AsyncCallback<Integer>() {
                  @Override
                  public void onFailure(Throwable caught) {
                     popup.hide();
                     Window.alert("Failed to open new schedule in: " + caught.getMessage());
                  }
                  
                  @Override
                  public void onSuccess(Integer newSchedID) {
                     popup.hide();
                     openLoadedSchedule(newSchedID, name);
                  }
               });
            }
            else
            {
               Window.alert("Error: Schedule named " + name + " already exists. Please enter a different name.");
            }
         }
      });
	}

	protected void openLoadedSchedule(Integer scheduleID, String scheduleName) {
		System.out.println("openloadedschedule?");
		
		if (myFrame.canPopViewsAboveMe()) {
			System.out.println("canpop");
			
			myFrame.popFramesAboveMe();
			System.out.println("popped");
			
			myFrame.frameViewAndPushAboveMe(new AdminScheduleNavView(service, this, menuBar, username, scheduleID, scheduleName));
			System.out.println("pushed");
			
		}
	}

}
