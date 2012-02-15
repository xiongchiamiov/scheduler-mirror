package edu.calpoly.csc.scheduler.view.web.client.calendar;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import edu.calpoly.csc.scheduler.view.web.client.GreetingServiceAsync;
import edu.calpoly.csc.scheduler.view.web.shared.CourseGWT;
import edu.calpoly.csc.scheduler.view.web.shared.InstructorGWT;
import edu.calpoly.csc.scheduler.view.web.shared.LocationGWT;
import edu.calpoly.csc.scheduler.view.web.shared.ScheduleItemGWT;

/**
 * A dialog that allows the user to edit the instructor, location, and time of a
 * schedule item
 */
public class EditScheduleItemDlg extends DialogBox {

	private final VerticalPanel mMainPanel = new VerticalPanel();
	private final ListBox mInstructorsLB = new ListBox(false);
	private final ListBox mLocationsLB = new ListBox(false);
	private final List<CheckBox> mDayCheckBoxes = new ArrayList<CheckBox>();
	private final ListBox mStartTimeLB = new ListBox(false);
	private final ListBox mEndTimeLB = new ListBox(false);

	private final GreetingServiceAsync mGreetingService;
	private final ScheduleItemGWT mItem;
	private boolean mChangedItem;

	public EditScheduleItemDlg(GreetingServiceAsync service, ScheduleItemGWT item) {
		super(false);

		mGreetingService = service;
		mItem = item;
		
		
		draw();
	}

	public ScheduleItemGWT getItem() {
		if (!mChangedItem)
			return null;
		return mItem;
	}

	private void draw() {
		mMainPanel.setWidth("300px");
		mMainPanel.setSpacing(5);
		mMainPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		mMainPanel.setTitle("Edit " + mItem.getCourseString());
		mMainPanel.setStylePrimaryName("editScheduleItemDialog");
		
		mMainPanel.add(createTitlePanel());
		mMainPanel.add(new HTML("<p />"));
		
		mMainPanel.add(createInstructorsPanel());
		mMainPanel.add(createLocationsPanel());		
		
		mMainPanel.add(createStartTimePanel());		
		mMainPanel.add(createEndTimePanel());
		mMainPanel.add(createDayPanel());
		
		mMainPanel.add(new HTML("<p />"));
		mMainPanel.add(createButtonPanel());

		populateInstructors();
		populateLocations();
		
		setWidget(mMainPanel);
	}

	private void cancel() {
		mChangedItem = false;
		hide();
	}

	private void ok() {
		mChangedItem = true;
		
		// TODO: update mItem based on user input data		
		mItem.setProfessor(mInstructorsLB.getItemText(mInstructorsLB.getSelectedIndex()));		
		mItem.setStartTimeHour(7);
		
		ArrayList<Integer> newDays = new ArrayList<Integer>();		
		for (int i = 0; i < mDayCheckBoxes.size(); i++) {
			if (mDayCheckBoxes.get(i).getValue()) {
				newDays.add(i);
			}
		}		
		mItem.setDayNums(newDays);		
		
		hide();
	}

	private Widget createTitlePanel() {
		final HTML titlePanel = new HTML("<center><b>Edit " + mItem.getCourseString() + "</b></center><p>");
		titlePanel.setHeight("30px");
		return titlePanel;
	}

	private Widget createInstructorsPanel() {
		final HorizontalPanel instructorsPanel = new HorizontalPanel();
		instructorsPanel.setWidth("300px");
		
		instructorsPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		instructorsPanel.add(new Label("Instructor:"));
		mInstructorsLB.setVisibleItemCount(1);		
		mInstructorsLB.setWidth("200px");

		instructorsPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		instructorsPanel.add(mInstructorsLB);

		return instructorsPanel;
	}

	private Widget createLocationsPanel() {
		final HorizontalPanel locationsPanel = new HorizontalPanel();
		locationsPanel.setWidth("300px");

		locationsPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		locationsPanel.add(new Label("Location:"));
		
		mLocationsLB.setVisibleItemCount(1);
		mLocationsLB.setWidth("200px");

		locationsPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		locationsPanel.add(mLocationsLB);

		return locationsPanel;
	}

	private Widget createStartTimePanel() {
		final HorizontalPanel timePanel = new HorizontalPanel();

		timePanel.setWidth("300px");
		timePanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		timePanel.add(new Label("Start Time:"));
		
		mStartTimeLB.setVisibleItemCount(1);		
		for (int time = 0; time < CalendarTableView.START_TIMES.length; time++)
			mStartTimeLB.addItem(CalendarTableView.START_TIMES[time]);
		
		mStartTimeLB.setSelectedIndex(CalendarTableView.getStartRow(mItem));
		mStartTimeLB.setWidth("200px");

		timePanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		timePanel.add(mStartTimeLB);

		return timePanel;
	}

	private Widget createEndTimePanel() {
		final HorizontalPanel timePanel = new HorizontalPanel();

		timePanel.setWidth("300px");
		timePanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		timePanel.add(new Label("End Time:"));
		mEndTimeLB.setVisibleItemCount(1);
		for (int time = 0; time < CalendarTableView.END_TIMES.length; time++)
			mEndTimeLB.addItem(CalendarTableView.END_TIMES[time]);
		
		mEndTimeLB.setSelectedIndex(CalendarTableView.getEndRow(mItem));
		mEndTimeLB.setWidth("200px");

		timePanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		timePanel.add(mEndTimeLB);

		return timePanel;
	}
	
	private Widget createDayPanel() {
		final HorizontalPanel dayPanel = new HorizontalPanel();
		
		dayPanel.setWidth("150px");
		dayPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		dayPanel.add(new Label("Days:"));
		final VerticalPanel checkBoxPanel = new VerticalPanel();
		for (int dayNum = 0; dayNum < CalendarTableView.DAYS.length; dayNum++) {
			final CheckBox checkBox = new CheckBox(CalendarTableView.DAYS[dayNum]);
			mDayCheckBoxes.add(checkBox);
			checkBoxPanel.add(checkBox);
		}

		for (int dayNum : mItem.getDayNums())
			mDayCheckBoxes.get(dayNum).setValue(true);

		dayPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		dayPanel.add(checkBoxPanel);

		return dayPanel;
	}

	private Widget createButtonPanel() {
		final HorizontalPanel buttonPanel = new HorizontalPanel();

		Button okButton = new Button("OK", new ClickHandler() {
			public void onClick(ClickEvent event) {
				ok();
			}
		});
		okButton.setStyleName("buttonStyle");
		buttonPanel.add(okButton);

		buttonPanel.add(new HTML("&nbsp;&nbsp;"));

		Button cancelButton = new Button("Cancel", new ClickHandler() {
			public void onClick(ClickEvent event) {
				cancel();
			}
		});
		cancelButton.setStyleName("buttonStyle");
		buttonPanel.add(cancelButton);

		return buttonPanel;
	}

	private void populateInstructors() {
		mGreetingService.getInstructors(new AsyncCallback<List<InstructorGWT>>() {
			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Failed to retrieve instructors.");
			}

			@Override
			public void onSuccess(List<InstructorGWT> result) {
				if (result != null) {
					if (result.size() > 10) {
						mInstructorsLB.setVisibleItemCount(result.size());						
					}
					
					for (InstructorGWT instructor : result) {
						mInstructorsLB.addItem(instructor.getFirstName() + " " + instructor.getLastName());
					}
				}
			}
			
		});		
	}

	private void populateLocations() {
		mGreetingService.getLocations(new AsyncCallback<List<LocationGWT>>() {
			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Failed to retrieve instructors.");
			}

			@Override
			public void onSuccess(List<LocationGWT> result) {
				if (result != null) {
					if (result.size() > 10) {
						mLocationsLB.setVisibleItemCount(result.size());						
					}
					
					for (LocationGWT location : result) {
						mLocationsLB.addItem(location.getBuilding() + " " + location.getRoom());
					}
				}
			}
			
		});	
	}
}
