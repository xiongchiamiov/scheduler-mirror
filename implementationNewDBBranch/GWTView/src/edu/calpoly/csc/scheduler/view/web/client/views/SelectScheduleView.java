package edu.calpoly.csc.scheduler.view.web.client.views;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

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
import edu.calpoly.csc.scheduler.view.web.client.HTMLUtilities;
import edu.calpoly.csc.scheduler.view.web.client.IViewContents;
import edu.calpoly.csc.scheduler.view.web.client.Import;
import edu.calpoly.csc.scheduler.view.web.client.ViewFrame;
import edu.calpoly.csc.scheduler.view.web.shared.DocumentGWT;

public class SelectScheduleView extends VerticalPanel implements IViewContents, AdminScheduleNavView.OtherFilesStrategy {
	protected final GreetingServiceAsync service;
	
	private final MenuBar menuBar;
	
	private final String username;
	private String newDocName;
	private ListBox listBox;
	MenuItem trashTab, homeTab;
	
	private VerticalPanel vdocholder;
	
	private ViewFrame myFrame;

	Collection<DocumentGWT> availableDocuments;
	
	public SelectScheduleView(final GreetingServiceAsync service, final MenuBar menuBar, final String username) {
		this.service = service;
		this.menuBar = menuBar;
		this.username = username;
		this.newDocName = "Untitled";

		menuBar.clearItems();
		//Put tabs in menu bar
		homeTab = new MenuItem("Home", true, new Command() {
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
	
		
		trashTab = new MenuItem("Trash", true, new Command() {
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
		
		service.getAllOriginalDocumentsByID(new AsyncCallback<Collection<DocumentGWT>>() {
			@Override
			public void onFailure(Throwable caught) {
				Window.alert("There was an error getting the schedules: " + caught.getMessage());
			}
			
			@Override
			public void onSuccess(Collection<DocumentGWT> result) {
				availableDocuments = result;
				
				vdocholder.clear();
				for (DocumentGWT doc : availableDocuments)
				{
				   addNewDocument(doc.getName(), doc.getID().toString());
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
	   docname.add(HTMLUtilities.createLink(name, "inAppLink", new ClickHandler()
	   {
			@Override
			public void onClick(ClickEvent event) {
			}
		}));
	   docname.addClickHandler(new ClickHandler()
      {
         @Override
         public void onClick(ClickEvent event)
         {
            String baseHref = Window.Location.getHref();
            if(Window.Location.getHref().contains("?userid="))
            {
               baseHref = Window.Location.getHref().substring(0, Window.Location.getHref().lastIndexOf('?'));
            }
            Window.open(baseHref + "?scheduleid=" + scheduleid + "&schedulename=" + name + "&userid=" + username, "_new", null);
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
	 * @param document Document to open
	 */
	private void selectSchedule(DocumentGWT document) {
		final LoadingPopup popup = new LoadingPopup();
		popup.show();
		
		System.out.println("SelectScheduleView.selectSchedule(" + document.getID() + ", " + document.getName() + ")");
		
		openDocument(document);
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

		for (DocumentGWT doc : this.availableDocuments)
			listBox.addItem(doc.getName(), doc.getID().toString());
		
		listBox.setVisibleItemCount(5);
		fp.add(listBox);
		
		fp.add(new HTML("<br />"));
		
		fp.add(new Button("Open", new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				db.hide();
				
				int index = listBox.getSelectedIndex();
				for (DocumentGWT document : availableDocuments) {
					if (document.getID().toString().equals(listBox.getValue(index))) {
						selectSchedule(document);
						return;
					}
				}
				
				assert(false);
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
				
				assert(false);
				//        	service.saveCurrentScheduleAsAndOpen(scheduleName, allowOverwrite, new AsyncCallback<Integer>() {
				//				public void onFailure(Throwable caught) {
				//					Window.alert("Failed to save schedule: " + caught.getMessage());
				//				}
				//				public void onSuccess(Integer newScheduleID) {
				//					openLoadedSchedule(newScheduleID, scheduleName);
				//				}
				//			});
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
		
		for (DocumentGWT doc : this.availableDocuments)
			saveAsListBox.addItem(doc.getName(), doc.getID().toString());
		
		
		db.setText("Name Schedule");
		fp.add(new HTML("<center>Specify a name to save the schedule as...</center>"));
		saveAsListBox.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				tb.setText(saveAsListBox.getItemText(saveAsListBox.getSelectedIndex()));
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
	public void beforeViewPushedAboveMe() {
		menuBar.removeItem(homeTab);
		menuBar.removeItem(trashTab);
	}
	
	@Override
	public void afterViewPoppedFromAboveMe() {
		menuBar.addItem(homeTab);
		menuBar.addItem(trashTab);
	}
	
	@Override
	public Widget getContents() { return this; }

	@Override
	public void fileNewPressed() {
		createNewSchedule();
	}

	@Override
	public void fileOpenPressed() {
	   String baseHref = Window.Location.getHref().substring(0, Window.Location.getHref().lastIndexOf('?'));
	   Window.open(baseHref + "?userid=" + username, "_new", null);
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
				    Window.alert("Unimplemented.");
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
		
		for (DocumentGWT doc : availableDocuments) {
			CheckBox checkBox = new CheckBox(doc.getName());
			checkBoxList.add(checkBox);
			checkBoxPanel.add(checkBox);
		}
		
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
	public void fileSaveAsPressed(DocumentGWT existingDocument) {
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
		    	assert(false); // implement
//	        	service.saveCurrentScheduleAsAndOpen(scheduleName, allowOverwrite, new AsyncCallback<Integer>() {
//					public void onFailure(Throwable caught) {
//						Window.alert("Failed to save schedule: " + caught.getMessage());
//					}
//					public void onSuccess(Integer newScheduleID) {
//						openLoadedSchedule(newScheduleID, scheduleName);
//					}
//				});
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
		
		for (DocumentGWT doc : availableDocuments)
			saveAsListBox.addItem(doc.getName(), doc.getID().toString());
		
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
            if(!availableDocuments.contains(name))
            {
               newDocName = name;
               final LoadingPopup popup = new LoadingPopup();
               popup.show();
               
               DOM.setElementAttribute(popup.getElement(), "id", "failSchedPopup");
               
               service.createDocument(newDocName, new AsyncCallback<DocumentGWT>() {
                  @Override
                  public void onFailure(Throwable caught) {
                     popup.hide();
                     Window.alert("Failed to open new schedule in: " + caught.getMessage());
                  }
                  
                  @Override
                  public void onSuccess(DocumentGWT newSched) {
                     popup.hide();
                     openDocument(newSched);
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

	protected void openDocument(DocumentGWT document) {
		service.createWorkingCopyForOriginalDocument(document.getID(), new AsyncCallback<DocumentGWT>() {
			@Override
			public void onSuccess(DocumentGWT workingCopyDocument) {
				if (myFrame.canPopViewsAboveMe()) {
					System.out.println("canpop");
					
					myFrame.popFramesAboveMe();
					System.out.println("popped");
					
					myFrame.frameViewAndPushAboveMe(new AdminScheduleNavView(service, SelectScheduleView.this, menuBar, username, workingCopyDocument));
					System.out.println("pushed");
				}
			}
			
			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Failed to open document.");
			}
		});
	}

	@Override
	public void fileSavePressed(DocumentGWT document) {
		service.saveWorkingCopyToOriginalDocument(document.getID(), new AsyncCallback<Void>() {
			@Override
			public void onSuccess(Void result) {
				Window.alert("Success");
			}
			
			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Failed to save.");
			}
		});
	}

	@Override
	public void fileClosePressed(final DocumentGWT document) {
		if (Window.confirm("Save changes?")) {
			service.saveWorkingCopyToOriginalDocument(document.getID(), new AsyncCallback<Void>() {
				@Override
				public void onSuccess(Void result) {
					service.deleteWorkingCopyDocument(document.getID(), new AsyncCallback<Void>() {
						@Override
						public void onSuccess(Void result) {
							assert(myFrame.canPopViewsAboveMe());
							myFrame.popFramesAboveMe();
						}
						
						@Override
						public void onFailure(Throwable caught) {
							Window.alert("Failed to delete working copy!");
							assert(myFrame.canPopViewsAboveMe());
							myFrame.popFramesAboveMe();
						}
					});
				}
				
				@Override
				public void onFailure(Throwable caught) {
					Window.alert("Failed to save!");
				}
			});
		}
		else {
			service.deleteWorkingCopyDocument(document.getID(), new AsyncCallback<Void>() {
				@Override
				public void onFailure(Throwable caught) {
					Window.alert("Failed to revert");
				}
				public void onSuccess(Void result) {
					assert(myFrame.canPopViewsAboveMe());
					myFrame.popFramesAboveMe();
				}
			});
		}
	}
}
