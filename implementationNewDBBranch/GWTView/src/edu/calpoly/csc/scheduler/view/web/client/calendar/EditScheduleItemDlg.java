package edu.calpoly.csc.scheduler.view.web.client.calendar;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import edu.calpoly.csc.scheduler.view.web.client.GreetingServiceAsync;
import edu.calpoly.csc.scheduler.view.web.shared.ScheduleItemGWT;

/**
 * A dialog that allows the user to edit the instructor, location, and time of a schedule item
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
		mMainPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		
		mMainPanel.add(createTitlePanel());
		mMainPanel.add(new HTML("<p />"));
		mMainPanel.add(createInstructorsPanel());
		mMainPanel.add(createLocationsPanel());
		mMainPanel.add(createDayPanel());
		mMainPanel.add(createStartTimePanel());
		mMainPanel.add(createEndTimePanel());
		mMainPanel.add(new HTML("<p />"));
		mMainPanel.add(createButtonPanel());
		
		setWidget(mMainPanel);
	}
	
	private void cancel() {
		mChangedItem = false;
		hide();
	}
	
	private void ok() {
		mChangedItem = true;
		
		// TODO: update mItem based on user input data
		
		hide();
	}
	
	private Widget createTitlePanel() {
		final HTML titlePanel = new HTML("<center><b>Edit "+mItem.getCourseString()+"</b></center><p>");
		titlePanel.setHeight("30px");
		return titlePanel;
	}
	
	private Widget createInstructorsPanel() {
		final HorizontalPanel instructorsPanel = new HorizontalPanel();
		instructorsPanel.setWidth("300px");
		
		instructorsPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		instructorsPanel.add(new HTML("Instructor:&nbsp;&nbsp;"));
		
		mInstructorsLB.setVisibleItemCount(1);
		mInstructorsLB.setWidth("200px");

		instructorsPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		instructorsPanel.add(mInstructorsLB);
		
		return instructorsPanel;
	}
	
	private Widget createLocationsPanel() {
		final HorizontalPanel locationsPanel = new HorizontalPanel();
		locationsPanel.setWidth("300px;");

		locationsPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		locationsPanel.add(new HTML("Location:&nbsp;&nbsp;"));

		mLocationsLB.setVisibleItemCount(1);
		mLocationsLB.setWidth("200px");

		locationsPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		locationsPanel.add(mLocationsLB);
		
		return locationsPanel;
	}
	
	private Widget createDayPanel()  {
		final HorizontalPanel dayPanel = new HorizontalPanel();

		dayPanel.setWidth("300px;");
		
		dayPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		dayPanel.add(new HTML("Days:&nbsp;&nbsp;"));
		
		final VerticalPanel checkBoxPanel = new VerticalPanel();
		for (int dayNum = 0; dayNum < CalendarTable.DAYS.length; dayNum++) {
			final CheckBox checkBox = new CheckBox(CalendarTable.DAYS[dayNum]);
			mDayCheckBoxes.add(checkBox);
			checkBoxPanel.add(checkBox);
		}
		
		for (int dayNum : mItem.getDayNums())
			mDayCheckBoxes.get(dayNum).setValue(true);
		
		dayPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		dayPanel.add(checkBoxPanel);
		
		return dayPanel;
	}
	
	private Widget createStartTimePanel() {
		final HorizontalPanel timePanel = new HorizontalPanel();
		
		timePanel.setWidth("300px;");
		
		timePanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		timePanel.add(new HTML("Start Time:&nbsp;&nbsp;"));

		mStartTimeLB.setVisibleItemCount(1);
		for (int time = 0; time < CalendarTable.START_TIMES.length; time++)
			mStartTimeLB.addItem(CalendarTable.START_TIMES[time]);
		mStartTimeLB.setSelectedIndex(CalendarTable.getStartRow(mItem));
		mStartTimeLB.setWidth("200px");

		timePanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		timePanel.add(mStartTimeLB);
		
		return timePanel;
	}
	
	private Widget createEndTimePanel() {
		final HorizontalPanel timePanel = new HorizontalPanel();
		
		timePanel.setWidth("300px;");
		
		timePanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		timePanel.add(new HTML("End Time:&nbsp;&nbsp;"));

		mEndTimeLB.setVisibleItemCount(1);
		for (int time = 0; time < CalendarTable.END_TIMES.length; time++)
			mEndTimeLB.addItem(CalendarTable.END_TIMES[time]);
		mEndTimeLB.setSelectedIndex(CalendarTable.getEndRow(mItem));
		mEndTimeLB.setWidth("200px");

		timePanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		timePanel.add(mEndTimeLB);
		
		return timePanel;
	}
	
	private Widget createButtonPanel() {
		final HorizontalPanel buttonPanel = new HorizontalPanel();
		
		buttonPanel.add(new Button("OK", new ClickHandler() {
			public void onClick(ClickEvent event) {
				ok();
			}
		}));
		
		buttonPanel.add(new HTML("&nbsp;&nbsp;"));
		
		buttonPanel.add(new Button("Cancel", new ClickHandler() {
			public void onClick(ClickEvent event) {
				cancel();
			}
		}));
		
		return buttonPanel;
	}
	
	private void populateInstructors(ListBox listBox) {
		
	}
	
	private void populateLocations(ListBox listBox) {
		
	}
	
	
}
