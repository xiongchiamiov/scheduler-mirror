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

	private final GreetingServiceAsync mGreetingService;
	private final ScheduleEditWidget mWidget;
	private final DragAndDropController mDragController;
	
	private final boolean mFromList;
	private final ScheduleItemGWT mOriginalItem;
	private List<Integer> mNewDays;
	private int mNewStartRow;
	private boolean mChangedItem;

	public EditScheduleItemDlg(GreetingServiceAsync service, ScheduleEditWidget widget, DragAndDropController dragController,
			boolean fromList, ScheduleItemGWT item, List<Integer> newDays, int newStartRow) {
		super(false);

		mGreetingService = service;
		mWidget = widget;
		mDragController = dragController;
		
		mFromList = fromList;
		mOriginalItem = item;
		mNewDays = newDays;
		mNewStartRow = newStartRow;
		
		draw();
	}
	
	private void draw() {
		mMainPanel.setWidth("300px");
		mMainPanel.setSpacing(5);
		mMainPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		mMainPanel.setTitle("Edit " + mOriginalItem.getCourseString());
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
		CourseGWT course = mOriginalItem.getCourse();
		String courseName = course.getCourseName();
		
		int insNdx = mInstructorsLB.getSelectedIndex();
		String prof = insNdx >= 0 ? mInstructorsLB.getItemText(insNdx) : null;
		
		String courseDept = course.getDept();
		String courseNum = mOriginalItem.getCatalogNum();
		int section = mOriginalItem.getSection();
		
		ArrayList<Integer> dayNums = new ArrayList<Integer>();
		for (int i = 0; i < mDayCheckBoxes.size(); i++) {
			if (mDayCheckBoxes.get(i).getValue()) {
				dayNums.add(i + 1);
			}
		}
		
		int startHour = getStartHour(mStartTimeLB.getSelectedIndex());
		int startMin = getStartMin(mStartTimeLB.getSelectedIndex());
		int endHour = getEndHour(mEndTimeLB.getSelectedIndex());
		int endMin = getEndMin(mEndTimeLB.getSelectedIndex());
		
		int locNdx = mLocationsLB.getSelectedIndex();
		String room = locNdx >= 0 ? mLocationsLB.getItemText(locNdx) : null;
		
		boolean conflicted = false;
		
		return new ScheduleItemGWT(course, courseName, prof, courseDept, courseNum, 
				section, dayNums, startHour, startMin, endHour, endMin, room, conflicted);
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
		
		mWidget.moveItem(getNewItem(), days, mStartTimeLB.getSelectedIndex(), !mFromList);
	}
	
	private int getStartHour(int row) {
		return row / 2 + 7;
	}

	private int getStartMin(int row) {
		return getEndMin(row)+ 10;
	}

	private int getEndHour(int row) {
		return row / 2 + 7;
	}

	private int getEndMin(int row) {
		return ((row % 2) * 30);
	}
	
	private Widget createTitlePanel() {
		final HTML titlePanel = new HTML("<center><b>Edit " + mOriginalItem.getCourseString() + "</b></center><p>");
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
//		mGreetingService.getInstructors(new AsyncCallback<List<InstructorGWT>>() {
//			@Override
//			public void onFailure(Throwable caught) {
//				Window.alert("Failed to retrieve instructors.");
//			}
//
//			@Override
//			public void onSuccess(List<InstructorGWT> result) {
//				if (result != null) {
//					if (result.size() > 10) {
//						mInstructorsLB.setVisibleItemCount(result.size());						
//					}
//					
//					for (InstructorGWT instructor : result) {
//						mInstructorsLB.addItem(instructor.getFirstName() + " " + instructor.getLastName());
//					}
//				}
//			}
//			
//		});	
	}

	private void populateLocations() {
//		mGreetingService.getLocations(new AsyncCallback<List<LocationGWT>>() {
//			@Override
//			public void onFailure(Throwable caught) {
//				Window.alert("Failed to retrieve instructors.");
//			}
//
//			@Override
//			public void onSuccess(List<LocationGWT> result) {
//				if (result != null) {
//					if (result.size() > 10) {
//						mLocationsLB.setVisibleItemCount(result.size());						
//					}
//					
//					for (LocationGWT location : result) {
//						mLocationsLB.addItem(location.getBuilding() + " " + location.getRoom());
//					}
//				}
//			}
//			
//		});	
	}
}
