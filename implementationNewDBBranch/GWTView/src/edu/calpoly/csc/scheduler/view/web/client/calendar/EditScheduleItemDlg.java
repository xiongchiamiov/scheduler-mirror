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
		int instructorID = mInstructors.get(mInstructorsLB.getSelectedIndex()).getID(); 
		int locationID = mLocations.get(mLocationsLB.getSelectedIndex()).getID();
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
		
		mWidget.updateItem(getNewItem(), !mFromList);
	}
	
	private int getStartHalfHour(int row) {
		return row + 14;
	}
	
	private int getEndHalfHour(int row) {
		return row + 14;
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
		
		final int maxIndex = mStartTimeLB.getItemCount() - 1;
		mStartTimeLB.setSelectedIndex(mNewStartRow < maxIndex ? mNewStartRow : maxIndex - 1);
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
		
		final int maxIndex = mEndTimeLB.getItemCount() - 1;
		mEndTimeLB.setSelectedIndex(mNewStartRow < maxIndex ? mNewStartRow + 1 : maxIndex);
		mEndTimeLB.setWidth("200px");

		timePanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		timePanel.add(mEndTimeLB);

		return timePanel;
	}
	
	private Widget createDayPanel() {
		final HorizontalPanel dayPanel = new HorizontalPanel();
		
		dayPanel.setWidth("150px");
		dayPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		dayPanel.add(new Label("Days:"));
		final VerticalPanel checkBoxPanel = new VerticalPanel();
		for (int dayNum = 0; dayNum < CalendarTableView.DAYS.length; dayNum++) {
			final CheckBox checkBox = new CheckBox(CalendarTableView.DAYS[dayNum]);
			mDayCheckBoxes.add(checkBox);
			checkBoxPanel.add(checkBox);
		}

		for (int dayNum : mNewDays)
			mDayCheckBoxes.get(dayNum - 1).setValue(true);

		dayPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
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
		mGreetingService.getLocationsForDocument(mDocument.getID(), new AsyncCallback<List<LocationGWT>>() {
			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Failed to retrieve instructors.");
			}

			@Override
			public void onSuccess(List<LocationGWT> result) {
				mLocations = result;
				
				if (result != null) {
					if (result.size() > 10) {
						mLocationsLB.setVisibleItemCount(result.size());						
					}
					
					for (LocationGWT location : result) {
						mLocationsLB.addItem(location.getRoom());
					}
				}
			}
			
		});	
	}
}
