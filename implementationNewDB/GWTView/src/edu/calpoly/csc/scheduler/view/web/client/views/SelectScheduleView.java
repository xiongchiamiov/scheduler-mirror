package edu.calpoly.csc.scheduler.view.web.client.views;

import java.util.Map;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import edu.calpoly.csc.scheduler.view.web.client.GreetingServiceAsync;
import edu.calpoly.csc.scheduler.view.web.shared.InstructorGWT;
import edu.calpoly.csc.scheduler.view.web.shared.Pair;
import edu.calpoly.csc.scheduler.view.web.shared.UserDataGWT;

public class SelectScheduleView extends VerticalPanel implements IView<MainView> {
	private GreetingServiceAsync service;
	private ListBox listBox;
	private MainView mainView;
	
	Map<String, UserDataGWT> availableSchedulesByName;
	
	public SelectScheduleView(final MainView mainView, final GreetingServiceAsync service) {
		this.mainView = mainView;
		this.service = service;

		addStyleName("homeView");

		setWidth("100%");
		setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		
		SimplePanel fakeTopPanel = new SimplePanel();
		fakeTopPanel.setWidth("100%");
		fakeTopPanel.addStyleName("topBarMenu");
		add(fakeTopPanel);
		
		add(new HTMLPanel("<h2>Select a Schedule</h2>"));
		
		listBox = new ListBox();
		add(listBox);

		add(new Button("Open", new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				int index = listBox.getSelectedIndex();
				if (index < 0)
					return;
				
				int existingScheduleID = Integer.parseInt(listBox.getValue(index));
				String scheduleName = listBox.getItemText(index);
				selectSchedule(existingScheduleID, scheduleName);
			}
		}));

		final SelectScheduleView self = this;
		
		add(new Button("Copy and Open", new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				int index = listBox.getSelectedIndex();
				if (index < 0)
					return;

				final int existingScheduleID = Integer.parseInt(listBox.getValue(index));

				displayNewSchedPopup("Name Schedule Copy", new NameScheduleCallback() {
					@Override
					public void namedSchedule(final String scheduleName) {
					    final LoadingPopup popup = new LoadingPopup();
					    popup.show();
					    
						service.copySchedule(existingScheduleID, scheduleName, new AsyncCallback<Integer>() {
							@Override
							public void onFailure(Throwable caught) {
								popup.hide();
								Window.alert("Failed to open new schedule in: " + caught.getMessage());
							}
							
							@Override
							public void onSuccess(Integer newScheduleID) {
								popup.hide();
								if (mainView.canCloseCurrentView())
									mainView.switchToView(new AdminScheduleNavView(self, mainView, service, newScheduleID, scheduleName));
							}
						});
					}
				});
			}
		}));

		add(new Button("New Schedule from CSV File", new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {

				final TextBox tb = new TextBox();
				final TextArea ta = new TextArea();
				final DialogBox db = new DialogBox(false);
				VerticalPanel vp = new VerticalPanel();
				final Button butt = new Button("Create", new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {		
						db.hide();
						
					    final String scheduleName = tb.getText();

					    final LoadingPopup popup = new LoadingPopup();
					    popup.show();
					    
					    service.importFromCSV(scheduleName, ta.getValue(), new AsyncCallback<Integer>() {
					    	@Override
					    	public void onFailure(Throwable caught) {
								popup.hide();
								Window.alert("Failed to open new schedule in: " + caught.getMessage());
					    		// TODO Auto-generated method stub
					    		
					    	}
					    	@Override
					    	public void onSuccess(Integer newScheduleID) {
								popup.hide();
								if (mainView.canCloseCurrentView())
									mainView.switchToView(new AdminScheduleNavView(self, mainView, service, newScheduleID, scheduleName));
					    	}
					    });
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
				vp.add(new HTML("<center>Specify a new schedule name.</center>"));
				vp.add(tb);
				vp.add(new HTML("<center>Enter CSV contents.</center>"));
				vp.add(ta);
				vp.add(butt);
				
				db.setWidget(vp);
				db.center();
			}
		}));

		add(new Button("New Schedule", new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				displayNewSchedPopup("Create Schedule", new NameScheduleCallback() {
					@Override
					public void namedSchedule(final String scheduleName) {
					    final LoadingPopup popup = new LoadingPopup();
					    popup.show();
					    
						service.openNewSchedule(scheduleName, new AsyncCallback<Integer>() {
							@Override
							public void onFailure(Throwable caught) {
								popup.hide();
								Window.alert("Failed to open new schedule in: " + caught.getMessage());
							}
							
							@Override
							public void onSuccess(Integer newScheduleID) {
								popup.hide();
								if (mainView.canCloseCurrentView())
									mainView.switchToView(new AdminScheduleNavView(self, mainView, service, newScheduleID, scheduleName));
							}
						});
					}
				});
			}
		}));
		
		add(new Button("Remove Schedule", new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				String schedName = listBox.getValue(listBox.getSelectedIndex());
				
				service.removeSchedule(schedName, new AsyncCallback<Void>() {
					@Override
					public void onFailure(Throwable caught) {
						Window.alert("Failed to remove schedule in: " + caught.getMessage());
					}
					
					@Override
					public void onSuccess(Void derp) {
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
					}
				});
			}
		}));
	}
	
	@Override
	public void onLoad() {
		super.onLoad();

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
	}
	
	private void selectSchedule(final int scheduleID, final String scheduleName) {		
		final SelectScheduleView self = this;

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
				
				if (mainView.canCloseCurrentView()) {
					int permission = permissionAndInstructor.getLeft();
					InstructorGWT instructor = permissionAndInstructor.getRight();
					
					switch (permission) {
					case 0: // todo: enumify
						mainView.switchToView(new GuestScheduleNavView(self, mainView, service, scheduleID, scheduleName));
						break;
					case 1: // todo: enumify
						mainView.switchToView(new InstructorScheduleNavView(self, mainView, service, scheduleID, scheduleName, instructor));
						break;
					case 2: // todo: enumify
						mainView.switchToView(new AdminScheduleNavView(self, mainView, service, scheduleID, scheduleName));
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
	
	public void displayNewSchedPopup(String buttonLabel, final NameScheduleCallback callback) {
		final TextBox tb = new TextBox();
		final DialogBox db = new DialogBox(false);
		VerticalPanel vp = new VerticalPanel();
		final Button butt = new Button(buttonLabel, new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {		
				db.hide();
				
			    final String scheduleName = tb.getText();
			    
			    callback.namedSchedule(scheduleName);
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
		vp.add(new HTML("<center>Specify a new schedule name.</center>"));
		vp.add(tb);
		vp.add(butt);
		
		db.setWidget(vp);
		db.center();
	}

	public Widget createMiniSelectWidget(int selectedScheduleID) {
		final ListBox box = new ListBox();

		int index = 0;
		for (String scheduleName : availableSchedulesByName.keySet()) {
			Integer id = availableSchedulesByName.get(scheduleName).getScheduleID();
			box.addItem(scheduleName, id.toString());
			if (id == selectedScheduleID)
				box.setSelectedIndex(index);
			index++;
		}
		
		box.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				selectSchedule(Integer.parseInt(box.getValue(box.getSelectedIndex())), box.getItemText(box.getSelectedIndex()));
			}
		});
		
		return box;
	}

	@Override
	public Widget getViewWidget() { return this; }

	@Override
	public void willOpenView(MainView container) { }

	@Override
	public boolean canCloseView() { return true; }
}
