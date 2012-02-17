package edu.calpoly.csc.scheduler.view.web.client.calendar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.allen_sauer.gwt.dnd.client.drop.DropController;
import com.google.gwt.dev.util.msg.Message;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import edu.calpoly.csc.scheduler.view.web.client.GreetingServiceAsync;
import edu.calpoly.csc.scheduler.view.web.client.HTMLUtilities;
import edu.calpoly.csc.scheduler.view.web.client.calendar.DummySchedule;
import edu.calpoly.csc.scheduler.view.web.client.schedule.FiltersViewWidget;
import edu.calpoly.csc.scheduler.view.web.client.schedule.ScheduleTable;
import edu.calpoly.csc.scheduler.view.web.client.views.LoadingPopup;
import edu.calpoly.csc.scheduler.view.web.shared.CourseGWT;
import edu.calpoly.csc.scheduler.view.web.shared.ScheduleItemGWT;
import edu.calpoly.csc.scheduler.view.web.shared.ScheduleItemList;

/**
 * This widget contains the calendar and list of available classes. It also contains the bottom button bar
 * and handles the filter and edit item dialogs.
 * 
 * @author Tyler Yero, Matt Schirle
 */

public class ScheduleEditWidget implements CloseHandler<PopupPanel> {

	private GreetingServiceAsync mGreetingService;
	private HashMap<String, ScheduleItemGWT> mSchedItems;
	private ArrayList<ScheduleItemGWT> mScheduleItems = new ArrayList<ScheduleItemGWT>();
	private VerticalPanel mMainPanel = new VerticalPanel();
	private boolean mIsCourseListCollapsed;

	private final DragAndDropController mDragController = new DragAndDropController();
	private ScheduleItemListView mAvailableCourses = new ScheduleItemListView(mScheduleItems, this, mDragController);
	private CalendarTableView mScheduleTable = new CalendarTableView(this, mDragController);
	private FiltersViewWidget mFiltersDialog = new FiltersViewWidget();

	private TextBox mSearchBox;
	private ListBox mAvailableCoursesListBox;

	/**
	 * Returns this widget in its entirety.
	 * 
	 * @param service
	 *            The server-side service which this widget will contact
	 * @return This widget
	 */
	public Widget getWidget(GreetingServiceAsync service) {

		mGreetingService = service;
		layoutBottomButtonBar();
		layoutListBoxAndCalendar();

		final LoadingPopup loading = new LoadingPopup();
		loading.show();

		mGreetingService.getSchedule(mSchedItems,
				new AsyncCallback<List<ScheduleItemGWT>>() {
					@Override
					public void onFailure(Throwable caught) {
						Window.alert("Failed to retrieve schedule.");
						loading.hide();
					}

					@Override
					public void onSuccess(List<ScheduleItemGWT> result) {
						mScheduleItems = new ArrayList<ScheduleItemGWT>();

						for (ScheduleItemGWT item : result) {
							mScheduleItems.add(item);
						}

						Collections.sort(mScheduleItems, new ScheduleItemComparator());
						
						// Add the attributes of the retrieved items to the filters list
						mFiltersDialog.addItems(mScheduleItems);

						// Place schedule items with any previously set filters
						filterScheduleItems(mSearchBox.getText());
						
//						mAvailableCourses.setItems(mScheduleItems);
//						mAvailableCourses.drawList();
						
						mScheduleTable.setScheduleItems(mScheduleItems);
						mScheduleTable.drawTable();
						
						loading.hide();
					}
				});

		return mMainPanel;
	}

	/**
	 * Displays a dialog that allows the user to edit a schedule item's
	 * instructor, location, and time
	 */
	public void editItem(ScheduleItemGWT item) {
		final EditScheduleItemDlg editDlg = new EditScheduleItemDlg(
				mGreetingService, item);
		editDlg.center();

	}

	/**
	 * Retrieves a schedule items from a generated schedule from the server.
	 */
	private void getScheduleItemsFromServer() {
		final LoadingPopup loading = new LoadingPopup();

		loading.show();

		List<CourseGWT> includedCourseList = new ArrayList<CourseGWT>();
		List<ScheduleItemGWT> calendarList = mScheduleTable.getScheduleItems();
		
		for (int i = 0; i < calendarList.size(); i++) {
			includedCourseList.add(calendarList.get(i).getCourse());
		}

		mGreetingService.generateSchedule(includedCourseList, mSchedItems,
				new AsyncCallback<List<ScheduleItemGWT>>() {
					public void onFailure(Throwable caught) {
						loading.hide();
						Window.alert("Failed to get schedule: "
								+ caught.toString());
					}

					public void onSuccess(List<ScheduleItemGWT> result) {
						if (result != null) {
							// Sort result by start times in ascending order
							Collections.sort(result,
									new ScheduleItemComparator());

							// Reset column and row spans, remove any items already placed
							mScheduleItems = new ArrayList<ScheduleItemGWT>();
							for (ScheduleItemGWT item : result) {
								mScheduleItems.add(item);
							}

							// Add the attributes of the retrieved items to the filters list
							mFiltersDialog.addItems(mScheduleItems);

							// Place schedule items with any previously set filters
							filterScheduleItems(mSearchBox.getText());

//							mAvailableCourses.setItems(mScheduleItems);
//							mAvailableCourses.drawList();

							mScheduleTable.setScheduleItems(mScheduleItems);
							mScheduleTable.drawTable();

							loading.hide();
						}
					}
				});
	}

	/**
	 * Sets all schedule items retrieved as not placed on the schedule.
	 */
	private void resetIsPlaced() {
		for (ScheduleItemGWT item : mScheduleItems) {
			item.setPlaced(false);
		}
	}

	/**
	 * Place schedule items which are not filtered.
	 */
	private void filterScheduleItems(String search) {
		ArrayList<String> filtInstructors = mFiltersDialog.getInstructors();
		ArrayList<String> filtCourses = mFiltersDialog.getCourses();
		ArrayList<String> filtRooms = mFiltersDialog.getRooms();
		ArrayList<Integer> filtDays = mFiltersDialog.getDays();
		ArrayList<Integer> filtTimes = mFiltersDialog.getTimes();

		for (ScheduleItemGWT item : mScheduleItems) {
			if (filtInstructors.contains(item.getProfessor())
					&& filtCourses.contains(item.getCourseString())
					&& filtRooms.contains(item.getRoom())
					&& item.getSchdItemText().contains(search)) {
				if (isInFilteredTimeRange(item, filtTimes)) {
					// scheduleGrid.placeScheduleItem(item, filtDays);
				}
			}
		}
	}

	private boolean isInFilteredTimeRange(ScheduleItemGWT item,
			ArrayList<Integer> filtTimes) {
		int startTimeRow = ScheduleTable.getRowFromTime(
				item.getStartTimeHour(), item.startsAfterHalf());
		int endTimeRow = ScheduleTable.getRowFromTime(item.getEndTimeHour(),
				item.endsAfterHalf()) - 1;

		if (filtTimes.isEmpty()) {
			return true;
		}

		for (Integer timeRow : filtTimes) {
			if (timeRow >= startTimeRow && timeRow <= endTimeRow) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Contains the method called when the "Generate Schedule" button is
	 * clicked.
	 */
	private class GenerateScheduleClickHandler implements ClickHandler {
		public void onClick(ClickEvent event) {
			getScheduleItemsFromServer();
		}
	}

	/**
	 * Lays out the buttons which will appear on this widget
	 */
	private void layoutBottomButtonBar() {
		/*
		 * Causes this class' onClose method to be called when the filters
		 * dialog is closed
		 */
		mFiltersDialog.addCloseHandler(this);

		FlowPanel bottomButtonFlowPanel = new FlowPanel();
		bottomButtonFlowPanel.addStyleName("floatingScheduleButtonBar");

		final Button collapseScheduleButton = new Button("<");
		collapseScheduleButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				if (!mIsCourseListCollapsed) {
					collapseScheduleButton.setText(">");
					mAvailableCoursesListBox.addStyleName("hiddenCoursesList");
					mAvailableCourses.toggle(true);
					mIsCourseListCollapsed = true;
					mScheduleTable.setLeftOffset(0);
				} else {
					collapseScheduleButton.setText("<");
					mAvailableCoursesListBox
							.removeStyleName("hiddenCoursesList");
					mAvailableCourses.toggle(false);
					mIsCourseListCollapsed = false;
					mScheduleTable.setLeftOffset(200);
				}
			}
		});

		collapseScheduleButton
				.setStyleName("floatingScheduleButtonBarItemLeft");
		bottomButtonFlowPanel.add(collapseScheduleButton);

		Button generateScheduleButton = new Button("Generate Schedule",
				new GenerateScheduleClickHandler());
		generateScheduleButton
				.setStyleName("floatingScheduleButtonBarItemLeft");
		bottomButtonFlowPanel.add(generateScheduleButton);

		generateScheduleButton
				.setStyleName("floatingScheduleButtonBarItemLeft");
		bottomButtonFlowPanel.add(generateScheduleButton);

		Button filterButton = new Button("Publish...", new ClickHandler() {
			public void onClick(ClickEvent event) {
				Window.alert("Not implemented yet.");
			}
		});
		filterButton.setStyleName("floatingScheduleButtonBarItemRight");
		bottomButtonFlowPanel.add(filterButton);

		mSearchBox = new TextBox();
		mSearchBox.addKeyPressHandler(new KeyPressHandler() {
			public void onKeyPress(KeyPressEvent event) {
				if (event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ENTER) {
					search();
				}
			}
		});
		mSearchBox.setStyleName("floatingScheduleButtonBarItemRight");
		bottomButtonFlowPanel.add(mSearchBox);

		Button filtersButton = new Button("Filters", new ClickHandler() {
			public void onClick(ClickEvent event) {
				// Causes the filters dialog to appear in the center of this
				// widget
				mFiltersDialog.center();
			}
		});
		filtersButton.setStyleName("floatingScheduleButtonBarItemRight");
		bottomButtonFlowPanel.add(filtersButton);

		RootPanel.get().add(bottomButtonFlowPanel);
		mMainPanel.add(bottomButtonFlowPanel);
	}

	/**
	 * Displays only schedule items which contain text in the search box
	 */
	private void search() {
		filterScheduleItems(mSearchBox.getText());
	}

	/**
	 * Retrieves the course list and adds it to the available courses box
	 */
	private void addCoursesToListBox() {

		mGreetingService.getCourses(new AsyncCallback<List<CourseGWT>>() {
			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Failed to retrieve courses");
			}

			@Override
			public void onSuccess(List<CourseGWT> result) {
				if (result != null) {

					List<ScheduleItemGWT> availableList = new ArrayList<ScheduleItemGWT>();
					for (CourseGWT course : result) {
						ScheduleItemGWT newItem = new ScheduleItemGWT();
						newItem.setCourse(course);
						
						availableList.add(newItem);
					}
					
					mAvailableCourses.setItems(availableList);
					mAvailableCourses.drawList();
				}
			}
		});
	}

	/**
	 * Lays out the available course listbox and the schedule
	 */
	private void layoutListBoxAndCalendar() {

		HorizontalPanel boxesAndSchedulePanel = new HorizontalPanel();
		boxesAndSchedulePanel.setSpacing(2);
		boxesAndSchedulePanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);

		addCoursesToListBox();

		boxesAndSchedulePanel.add(mAvailableCourses);
		mAvailableCourses.drawList();

		mScheduleTable.setLeftOffset(200);
		boxesAndSchedulePanel.add(mScheduleTable);

		mMainPanel.add(boxesAndSchedulePanel);
	}

	/**
	 * Called when the filters dialog closes
	 */
	public void onClose(CloseEvent<PopupPanel> event) {
		filterScheduleItems(mSearchBox.getText());
	}

	/**
	 * Converts a row to an hour
	 */
	private int getHourFromRow(int row) {
		return (row / 2) + 6 + (row % 2);
	}

	/**
	 * Returns true if a row is at a half hour (i.e. 7:30), false if a row is on
	 * the hour (i.e. 7:00)
	 */
	private boolean rowIsAtHalfHour(int row) {
		return row % 2 == 0;
	}

	/**
	 * Called when a schedule item is dragged to a new position, or when a
	 * course from one of the lists is dropped onto the schedule
	 */
	public void moveItem(final ScheduleItemGWT scheduleItem,
			ArrayList<Integer> days, int row, final boolean inSchedule,
			final boolean fromIncluded) {

		final LoadingPopup loading = new LoadingPopup();
		loading.show();
		final int startHour = getHourFromRow(row);
		final boolean atHalfHour = rowIsAtHalfHour(row);

		CourseGWT course = new CourseGWT();
		course.setDept(scheduleItem.getDept());
		course.setCatalogNum(scheduleItem.getCatalogNum());

		mGreetingService.rescheduleCourse(scheduleItem, days, startHour,
				atHalfHour, inSchedule, mSchedItems,
				new AsyncCallback<ScheduleItemList>() {
					@Override
					public void onFailure(Throwable caught) {
						loading.hide();
						Window.alert("Failed to retrieve rescheduled item");
					}

					@Override
					public void onSuccess(ScheduleItemList rescheduledCourses) {
						
						CourseGWT courseHolder;
						int sectionsIncluded = 0;
						int itemIndex = 0;

						// TODO: Matt, if the course was dragged from the course list
						// the section count needs to be decremented. And the course should be removed
						// if there was only one section.

						if (!inSchedule) {
							
							
							// If the list box contains sections of this course
							if (itemIndex >= 0) {
								// Determine how many it sections it has
								if (sectionsIncluded > 1) {
									// Decrement by one if there is more than one section
								}								
								else {
									// Remove the course if there is only one section
								}

							}
						}

						mScheduleItems = new ArrayList<ScheduleItemGWT>();
						
						for (ScheduleItemGWT schdItem : rescheduledCourses) {
							mScheduleItems.add(schdItem);
						}
						
						Collections.sort(mScheduleItems, new ScheduleItemComparator());

						mFiltersDialog.addItems(mScheduleItems);
						
						// Place schedule items with any previously set filters
						filterScheduleItems(mSearchBox.getText());
						loading.hide();

						mScheduleTable.setScheduleItems(mScheduleItems);
						mScheduleTable.drawTable();
						
						loading.hide();
						
						if (rescheduledCourses.conflict.length() > 0) {
							Window.alert(rescheduledCourses.conflict);
						}
					}
				});
	}

	public int getSectionsOnSchedule(CourseGWT course) {
		String dept = course.getDept();
		String catalogNum = course.getCatalogNum();
		int count = 0;

		for (ScheduleItemGWT item : mScheduleItems) {
			if (item.getDept() == dept
					&& item.getCatalogNum().equals(catalogNum)) {
				count++;
			}
		}

		return count;
	}

	/* Returns all schedule items retrieved from the model's schedule object */
	public ArrayList<ScheduleItemGWT> getItemsInSchedule() {
		return mScheduleItems;
	}

	public void removeItem(ScheduleItemGWT removed) {
		final LoadingPopup loading = new LoadingPopup();
		loading.show();

		mGreetingService.removeScheduleItem(removed, mSchedItems,
				new AsyncCallback<List<ScheduleItemGWT>>() {
					@Override
					public void onFailure(Throwable caught) {
						loading.hide();
						Window.alert("Failed to remove item.");
					}

					@Override
					public void onSuccess(List<ScheduleItemGWT> result) {
						mScheduleItems = new ArrayList<ScheduleItemGWT>();
						for (ScheduleItemGWT schdItem : result) {
							mScheduleItems.add(schdItem);
						}
						Collections.sort(mScheduleItems,
								new ScheduleItemComparator());

						mFiltersDialog.addItems(mScheduleItems);
						filterScheduleItems(mSearchBox.getText());
						loading.hide();
					}
				});
	}

	private class ScheduleItemComparator implements Comparator<ScheduleItemGWT> {

		@Override
		public int compare(ScheduleItemGWT o1, ScheduleItemGWT o2) {
			if (o1.getStartTimeHour() > o2.getStartTimeHour()) {
				return 1;
			} else if (o1.getStartTimeHour() < o2.getStartTimeHour()) {
				return -1;
			} else if (o1.getStartTimeMin() > o2.getStartTimeMin()) {
				return 1;
			} else if (o1.getStartTimeMin() < o2.getStartTimeMin()) {
				return -1;
			}

			return 0;
		}
	};
}
