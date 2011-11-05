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
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import edu.calpoly.csc.scheduler.view.web.client.GreetingServiceAsync;
import edu.calpoly.csc.scheduler.view.web.client.HTMLUtilities;

public class SelectScheduleView extends ScrollPanel {
	private GreetingServiceAsync service;
	private Panel container;
	private ListBox listBox;
	private VerticalPanel vp;
	
	Map<String, Integer> schedulesIDsAndNames;
	
	public SelectScheduleView(Panel container, GreetingServiceAsync service) {
		this.container = container;
		this.service = service;

		addStyleName("homeView");
		
        vp = new VerticalPanel();
		add(vp);
		vp.setWidth("100%");
		vp.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		
		vp.add(new HTMLPanel("<h2>Select a Schedule</h2>"));
		
		listBox = new ListBox();
		vp.add(listBox);

		vp.add(new Button("Open", new ClickHandler() {
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
		
		vp.add(new Button("New Schedule", new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				displayNewSchedPopup();
			}
		}));
	}
	
	@Override
	public void onLoad() {
		super.onLoad();

		service.getScheduleNames(new AsyncCallback<Map<String,Integer>>() {
			@Override
			public void onFailure(Throwable caught) {
				Window.alert("There was an error getting the schedules: " + caught.getMessage());
			}
			
			@Override
			public void onSuccess(Map<String, Integer> result) {
				schedulesIDsAndNames = result;
				
				listBox.clear();
				for (String scheduleName : schedulesIDsAndNames.keySet())
					listBox.addItem(scheduleName, schedulesIDsAndNames.get(scheduleName).toString());
			}
		});
	}
	
	private void selectSchedule(final int scheduleID, final String scheduleName) {		
		final SelectScheduleView self = this;

		final LoadingPopup popup = new LoadingPopup();
		popup.show();
		
		service.openExistingSchedule(scheduleID, new AsyncCallback<Void>() {
			@Override
			public void onFailure(Throwable caught) {
				popup.hide();
				Window.alert("Failed to open schedule in: " + caught.getMessage());
			}
			@Override
			public void onSuccess(Void derp) {
				popup.hide();
				container.clear();
				container.add(new ScheduleNavView(self, container, service, scheduleID, scheduleName));
			}
		});
	}
	
	public void displayNewSchedPopup() {
		final SelectScheduleView self = this;
		
		final TextBox tb = new TextBox();
		final DialogBox db = new DialogBox(false);
		VerticalPanel vp = new VerticalPanel();
		final Button butt = new Button("Create Schedule", new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {		
				db.hide();
				
			    final LoadingPopup popup = new LoadingPopup();
			    popup.show();
			    
			    final String scheduleName = tb.getText();
				
				service.openNewSchedule(scheduleName, new AsyncCallback<Integer>() {
					@Override
					public void onFailure(Throwable caught) {
						popup.hide();
						Window.alert("Failed to open new schedule in: " + caught.getMessage());
					}
					
					@Override
					public void onSuccess(Integer newScheduleID) {
						popup.hide();
						container.clear();
						container.add(new ScheduleNavView(self, container, service, newScheduleID, scheduleName));
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
		vp.add(butt);
		
		db.setWidget(vp);
		db.center();
	}

	public Widget createMiniSelectWidget(int selectedScheduleID) {
		final ListBox box = new ListBox();

		int index = 0;
		for (String scheduleName : schedulesIDsAndNames.keySet()) {
			Integer id = schedulesIDsAndNames.get(scheduleName);
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
}
