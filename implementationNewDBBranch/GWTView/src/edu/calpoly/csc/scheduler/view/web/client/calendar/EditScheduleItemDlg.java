package edu.calpoly.csc.scheduler.view.web.client.calendar;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
import edu.calpoly.csc.scheduler.view.web.shared.DayGWT;
import edu.calpoly.csc.scheduler.view.web.shared.DocumentGWT;
import edu.calpoly.csc.scheduler.view.web.shared.InstructorGWT;
import edu.calpoly.csc.scheduler.view.web.shared.LocationGWT;
import edu.calpoly.csc.scheduler.view.web.shared.ScheduleItemGWT;

/**
 * A dialog that allows the user to edit the instructor, location, and time of a
 * schedule item
 * 
 * @author Tyler Yero
 */
public class EditScheduleItemDlg extends DialogBox {

	private final VerticalPanel mMainPanel = new VerticalPanel();
	private final ListBox mInstructorsLB = new ListBox(false);
	private final ListBox mLocationsLB = new ListBox(false);
	private final List<CheckBox> mDayCheckBoxes = new ArrayList<CheckBox>();
	private final ListBox mStartTimeLB = new ListBox(false);
	private final ListBox mEndTimeLB = new ListBox(false);
	
	private List<InstructorGWT> mInstructors;
	private List<LocationGWT> mLocations;

	private final GreetingServiceAsync mGreetingService;
	private final ScheduleEditWidget mWidget;
	private final DragAndDropController mDragController;
	
	private final boolean mFromList;
	private final ScheduleItemGWT mOriginalItem;
	private List<Integer> mNewDays;
	private int mNewStartRow;
	private boolean mChangedItem;
	private final DocumentGWT mDocument;

	public EditScheduleItemDlg(GreetingServiceAsync service, ScheduleEditWidget widget, DragAndDropController dragController,
			boolean fromList, ScheduleItemGWT item, DocumentGWT document) {
		this(service, widget, dragController, fromList, item, null, -1, document);
	}
	
	public EditScheduleItemDlg(GreetingServiceAsync service, ScheduleEditWidget widget, DragAndDropController dragController,
			boolean fromList, ScheduleItemGWT item, List<Integer> newDays, int newStartRow, DocumentGWT document) {
		super(false);

		mGreetingService = service;
		mWidget = widget;
		mDragController = dragController;
		
		mFromList = fromList;
		mOriginalItem = item;
		mNewDays = newDays;
		mNewStartRow = newStartRow;
		mDocument = document;
		
		draw();
	}
	
	private void draw() {
		mMainPanel.setWidth("300px");
		mMainPanel.setSpacing(5);
		mMainPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		mMainPanel.setTitle("Edit " + mWidget.getCourseString(mOriginalItem.getCourseID()));
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
	
	public boolean changedItem() { 
		return mChangedItem;
	}

	public ScheduleItemGWT getOriginalItem() {
		return mOriginalItem;
	}
	
	public ScheduleItemGWT getNewItem() {
		int courseID = mOriginalItem.getCourseID();
		
		int instructorID = -1; // Staff
		if (mInstructorsLB.getSelectedIndex() > 0)
			instructorID = mInstructors.get(mInstructorsLB.getSelectedIndex() - 1).getID();
		
		int locationID = -1; // TBA
		if (mLocationsLB.getSelectedIndex() > 0)
			locationID = mLocations.get(mLocationsLB.getSelectedIndex() - 1).getID();
		
		int section = mOriginalItem.getSection();
		
		Set<DayGWT> days = new HashSet<DayGWT>();
		for (int i = 0; i < mDayCheckBoxes.size(); i++)
			if (mDayCheckBoxes.get(i).getValue())
				days.add(DayGWT.values()[i]);
		
		int startHalfHour = getStartHalfHour(mStartTimeLB.getSelectedIndex());
		int endHalfHour = getEndHalfHour(mEndTimeLB.getSelectedIndex());
		
		return new ScheduleItemGWT(mOriginalItem.getID(), courseID, instructorID, locationID, 
				section, days, startHalfHour, endHalfHour, false, false);
	}
	
	private void cancel() {
		mChangedItem = false;
		mDragController.cancelDrop();
		hide();
	}

	private void ok() {
		mChangedItem = true;
		hide();
		
		ArrayList<Integer> days = new ArrayList<Integer>();
		for (int i = 0; i < mDayCheckBoxes.size(); i++) {
			if (mDayCheckBoxes.get(i).getValue()) {
				days.add(i + 1);
			}
		}
		
		if (mFromList)
			mWidget.insertItem(getNewItem());
		else
			mWidget.updateItem(getNewItem());
	}
	
	private int getStartHalfHour(int row) {
		return row + 14;
	}
	
	private int getEndHalfHour(int row) {
		return row + 13;
	}
	
	private Widget createTitlePanel() {
		final HTML titlePanel = new HTML("<center><b>Edit " + mWidget.getCourseString(mOriginalItem.getCourseID()) + "</b></center><p>");
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
		
		if (mNewStartRow >= 0) {
			final int maxIndex = mStartTimeLB.getItemCount() - 1;
			mStartTimeLB.setSelectedIndex(mNewStartRow < maxIndex ? mNewStartRow : maxIndex - 1);
		}
		else
			mStartTimeLB.setSelectedIndex(CalendarTableView.getStartRow(mOriginalItem));
		
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
		
		if (mNewStartRow >= 0) {
			final int maxIndex = mEndTimeLB.getItemCount() - 1;
			mEndTimeLB.setSelectedIndex(mNewStartRow < maxIndex ? mNewStartRow + 1 : maxIndex);
		}
		else 
			mEndTimeLB.setSelectedIndex(CalendarTableView.getEndRow(mOriginalItem) + 1);
		
		mEndTimeLB.setWidth("200px");

		timePanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		timePanel.add(mEndTimeLB);

		return timePanel;
	}
	
	private Widget createDayPanel() {
		final HorizontalPanel dayPanel = new HorizontalPanel();
		
		dayPanel.setWidth("300px");
		dayPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		dayPanel.add(new Label("Days:"));
		
		final VerticalPanel checkBoxPanel = new VerticalPanel();
		checkBoxPanel.setWidth("200px");
		for (DayGWT day : DayGWT.values()) {
			final CheckBox checkBox = new CheckBox(day.name);
			mDayCheckBoxes.add(checkBox);
			checkBoxPanel.add(checkBox);
		}

		if (mNewDays != null) {
			System.out.println("mNewDays != null");
			for (int dayNum : mNewDays)
				mDayCheckBoxes.get(dayNum).setValue(true);
		}
		else {
			for (DayGWT day : mOriginalItem.getDays()) {
				System.out.println("checking day " + day.ordinal());
				mDayCheckBoxes.get(day.ordinal()).setValue(true);
			}
		}

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
		mGreetingService.getInstructorsForDocument(mDocument.getID(), new AsyncCallback<List<InstructorGWT>>() {
			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Failed to retrieve instructors.");
			}

			@Override
			public void onSuccess(List<InstructorGWT> result) {
				mInstructors = result;
				
				mInstructorsLB.addItem("Don't care");
				if (result != null) {
					for (InstructorGWT instructor : result) {
						mInstructorsLB.addItem(instructor.getFirstName() + " " + instructor.getLastName());

						if (mOriginalItem.getLocationID() == instructor.getID())
							mInstructorsLB.setSelectedIndex(mInstructorsLB.getItemCount() - 1);
					}
				}
			}
		});	
	}

	private void populateLocations() {
		mGreetingService.getLocationsForDocument(mDocument.getID(), new AsyncCallback<List<LocationGWT>>() {
			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Failed to retrieve instructors.");
			}

			@Override
			public void onSuccess(List<LocationGWT> result) {
				mLocations = result;

				mLocationsLB.addItem("Don't care");
				if (result != null) {
					for (LocationGWT location : result) {
						mLocationsLB.addItem(location.getRoom());
						
						if (mOriginalItem.getLocationID() == location.getID())
							mLocationsLB.setSelectedIndex(mLocationsLB.getItemCount() - 1);
					}
				}
			}
			
		});	
	}
}
