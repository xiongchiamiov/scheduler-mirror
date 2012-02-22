package edu.calpoly.csc.scheduler.view.web.client.calendar;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import edu.calpoly.csc.scheduler.view.web.client.GreetingServiceAsync;
import edu.calpoly.csc.scheduler.view.web.client.schedule.FiltersViewWidget;
import edu.calpoly.csc.scheduler.view.web.client.schedule.ScheduleTable;
import edu.calpoly.csc.scheduler.view.web.client.views.LoadingPopup;
import edu.calpoly.csc.scheduler.view.web.shared.CourseGWT;
import edu.calpoly.csc.scheduler.view.web.shared.DocumentGWT;
import edu.calpoly.csc.scheduler.view.web.shared.ScheduleItemGWT;
import edu.calpoly.csc.scheduler.view.web.shared.ScheduleItemList;

/**
 * This widget contains the calendar and list of available classes. It also contains the bottom button bar
 * and handles the filter and edit item dialogs.
 * 
 * @author Tyler Yero, Matt Schirle
 */

public class ScheduleEditWidget implements CloseHandler<PopupPanel> {

	private final DocumentGWT mDocument;
	private Map<Integer, CourseGWT> mCourses; 
	
	private GreetingServiceAsync mGreetingService;
	private HashMap<String, ScheduleItemGWT> mSchedItems;
	private ArrayList<ScheduleItemGWT> mCalendarItems= new ArrayList<ScheduleItemGWT>();
	private List<CourseGWT> mAllCourses = new ArrayList<CourseGWT>();
	private VerticalPanel mMainPanel = new VerticalPanel();
	private boolean mIsCourseListCollapsed;

	private final DragAndDropController mDragController = new DragAndDropController(this);
	private CourseListView mAvailableCoursesView = new CourseListView(this, mDragController);
	private CalendarTableView mCalendarTableView = new CalendarTableView(this, mDragController);
	private FiltersViewWidget mFiltersDialog = new FiltersViewWidget();

	private TextBox mSearchBox;
	
	
	public ScheduleEditWidget(GreetingServiceAsync service, DocumentGWT document) {
		mGreetingService = service;
		mDocument = document;

		final LoadingPopup loading = new LoadingPopup();
		loading.show();
		
		layoutBottomButtonBar();
		layoutListBoxAndCalendar();
		
		final Integer documentID = document.getID();

		// Initialize collection of courses
		mGreetingService.getCoursesForDocument(documentID, new AsyncCallback<List<CourseGWT>>() {
			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Failed to retrieve courses.");
				loading.hide();
				return;
			}

			@Override
			public void onSuccess(List<CourseGWT> result) {
				mCourses.clear();
				for (CourseGWT course : result)
					mCourses.put(course.getID(), course);
			}
		});
		
		mGreetingService.getScheduleItems(mDocument.getScheduleID(), 
				new AsyncCallback<Collection<ScheduleItemGWT>>() {
					@Override
					public void onFailure(Throwable caught) {
						Window.alert("Failed to retrieve schedule.");
						loading.hide();
					}

					@Override
					public void onSuccess(Collection<ScheduleItemGWT> result) {
						mCalendarItems = new ArrayList<ScheduleItemGWT>();

						for (ScheduleItemGWT item : result) {
							mCalendarItems.add(item);
						}

//						Collections.sort(mCalendarItems, new ScheduleItemComparator());
						
						// Add the attributes of the retrieved items to the filters list
//						mFiltersDialog.addItems(mCalendarItems);

						// Place schedule items with any previously set filters
//						filterScheduleItems(mSearchBox.getText());
						
						mCalendarTableView.setScheduleItems(mCalendarItems);
						mCalendarTableView.drawTable();
						
						populateAvailableCoursesList();
						
						loading.hide();
					}
				});
		
	}

	/**
	 * Returns this widget in its entirety.
	 * 
	 * @param service
	 *            The server-side service which this widget will contact
	 * @return This widget
	 */
	public Widget getWidget() {

		return mMainPanel;
	}
	
	/**
	 * Displays a dialog that allows the user to edit a schedule item's
	 * instructor, location, and time. When the user clicks "ok" the
	 * dialog will call this ScheduleEditWidget's moveItem method
	 */
	public void editItem(boolean fromList, ScheduleItemGWT originalItem, List<Integer> newDays, int newStartRow) {
		final EditScheduleItemDlg editDlg = new EditScheduleItemDlg(mGreetingService, this, mDragController,
				fromList, originalItem, newDays, newStartRow, mDocument);
		editDlg.center();
	}
	
	/**
	 * Get the name of a course
	 * 
	 * @param courseID The id of the course
	 * @return A string with the department, a space, then the catalog number
	 */
	public String getCourseString(Integer courseID) {
		CourseGWT course = mCourses.get(courseID);
		return course.getDept() + " " + course.getCatalogNum();
	}
	
	public CourseGWT getCourse(Integer courseID) {
		return mCourses.get(courseID);
	}

	/**
	 * Retrieves a schedule items from a generated schedule from the server.
	 */
	private void getScheduleItemsFromServer() {
		final LoadingPopup loading = new LoadingPopup();

		loading.show();
		
		mGreetingService.generateRestOfSchedule(mDocument.getScheduleID(),
				new AsyncCallback<Collection<ScheduleItemGWT>>() {
					public void onFailure(Throwable caught) {
						loading.hide();
						Window.alert("Failed to get schedule: "
								+ caught.toString());
					}

					public void onSuccess(Collection<ScheduleItemGWT> result) {
						if (result != null) {
							// Sort result by start times in ascending order
//							Collections.sort(result, new ScheduleItemComparator());

							// Reset column and row spans, remove any items already placed
							mCalendarItems = new ArrayList<ScheduleItemGWT>();
							for (ScheduleItemGWT item : result) {
								mCalendarItems.add(item);
							}

							// Add the attributes of the retrieved items to the filters list
//							mFiltersDialog.addItems(mCalendarItems);

							// Place schedule items with any previously set filters
//							filterScheduleItems(mSearchBox.getText());

//							mAvailableCourses.setItems(mScheduleItems);
//							mAvailableCourses.drawList();

							mCalendarTableView.setScheduleItems(mCalendarItems);
							mCalendarTableView.drawTable();
							
							populateAvailableCoursesList();

							loading.hide();
						}
					}
				});
	}

	/**
	 * Place schedule items which are not filtered.
	 */
//	private void filterScheduleItems(String search) {
//		ArrayList<String> filtInstructors = mFiltersDialog.getInstructors();
//		ArrayList<String> filtCourses = mFiltersDialog.getCourses();
//		ArrayList<String> filtRooms = mFiltersDialog.getRooms();
//		ArrayList<Integer> filtDays = mFiltersDialog.getDays();
//		ArrayList<Integer> filtTimes = mFiltersDialog.getTimes();
//
//		for (ScheduleItemGWT item : mCalendarItems) {
//			if (filtInstructors.contains(item.getProfessor())
//					&& filtCourses.contains(item.getCourseString())
//					&& filtRooms.contains(item.getRoom())
//					&& item.getSchdItemText().contains(search)) {
//				if (isInFilteredTimeRange(item, filtTimes)) {
//					// scheduleGrid.placeScheduleItem(item, filtDays);
//				}
//			}
//		}
//	}

//	private boolean isInFilteredTimeRange(ScheduleItemGWT item,
//			ArrayList<Integer> filtTimes) {
//		int startTimeRow = ScheduleTable.getRowFromTime(
//				item.getStartTimeHour(), item.startsAfterHalf());
//		int endTimeRow = ScheduleTable.getRowFromTime(item.getEndTimeHour(),
//				item.endsAfterHalf()) - 1;
//
//		if (filtTimes.isEmpty()) {
//			return true;
//		}
//
//		for (Integer timeRow : filtTimes) {
//			if (timeRow >= startTimeRow && timeRow <= endTimeRow) {
//				return true;
//			}
//		}
//
//		return false;
//	}

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
					mAvailableCoursesView.toggle(true);
					mIsCourseListCollapsed = true;
					mCalendarTableView.setLeftOffset(0);
				} else {
					collapseScheduleButton.setText("<");
					mAvailableCoursesView.toggle(false);
					mIsCourseListCollapsed = false;
					mCalendarTableView.setLeftOffset(200);
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
		Window.alert("Sorry, that's not implemented yet");
	}

	/**
	 * Retrieves the course list and adds it to the available courses box
	 */
	private void populateAvailableCoursesList() {
		mGreetingService.getCoursesForDocument(mDocument.getID(), new AsyncCallback<List<CourseGWT>>() {
			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Failed to retrieve courses");
			}
			
			@Override
			public void onSuccess(List<CourseGWT> result) {
				if (result != null) {
					mAllCourses = result;
					
					List<CourseGWT> availableItems = new ArrayList<CourseGWT>();
					
					courseLoop:
					for (CourseGWT course : result) {
						// Don't added courses to available list if they're on the calendar
						for (ScheduleItemGWT item : mCalendarItems) {
							if (item.getCourseID() == course.getID()) {
								continue courseLoop;
							}
						}
						
						availableItems.add(course);
					}
					
					mAvailableCoursesView.setItems(availableItems);
					mAvailableCoursesView.drawList();
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

		boxesAndSchedulePanel.add(mAvailableCoursesView);
		mAvailableCoursesView.drawList();

		mCalendarTableView.setLeftOffset(200);
		boxesAndSchedulePanel.add(mCalendarTableView);

		mMainPanel.add(boxesAndSchedulePanel);
	}

	/**
	 * Called when the filters dialog closes
	 */
	public void onClose(CloseEvent<PopupPanel> event) {
		// TODO
//		filterScheduleItems(mSearchBox.getText());
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
	 * 
	 * @param item The new item that will replace the old item with the same id
	 * @param inSchedule true iff the course is already in the schedule
	 */
	public void updateItem(final ScheduleItemGWT item, final boolean inSchedule) {
		final LoadingPopup loading = new LoadingPopup();
		loading.show();
		
		mGreetingService.updateScheduleItem(item,
				new AsyncCallback<Collection<ScheduleItemGWT>>() {
					@Override
					public void onFailure(Throwable caught) {
						loading.hide();
						Window.alert("Failed to retrieve rescheduled item");
					}

					@Override
					public void onSuccess(Collection<ScheduleItemGWT> result) {
						mCalendarItems = new ArrayList<ScheduleItemGWT>();
						
						for (ScheduleItemGWT schdItem : result) {
							mCalendarItems.add(schdItem);
						}
						
//						Collections.sort(mCalendarItems, new ScheduleItemComparator());
//
//						mFiltersDialog.addItems(mCalendarItems);
//						
//						// Place schedule items with any previously set filters
//						filterScheduleItems(mSearchBox.getText());
//						loading.hide();

						mCalendarTableView.setScheduleItems(mCalendarItems);
						mCalendarTableView.drawTable();
						
						loading.hide();
						
//						if (rescheduledCourses.conflict.length() > 0) {
//							Window.alert(rescheduledCourses.conflict);
//						}
					}
				});
	}

//	public int getSectionsOnSchedule(CourseGWT course) {
//		String dept = course.getDept();
//		String catalogNum = course.getCatalogNum();
//		int count = 0;
//
//		for (ScheduleItemGWT item : mCalendarItems) {
//			if (item.getDept() == dept
//					&& item.getCatalogNum().equals(catalogNum)) {
//				count++;
//			}
//		}
//
//		return count;
//	}

	/* Returns all schedule items retrieved from the model's schedule object */
	public ArrayList<ScheduleItemGWT> getItemsInSchedule() {
		return mCalendarItems;
	}

	public void removeItem(ScheduleItemGWT removed) {
		final LoadingPopup loading = new LoadingPopup();
		loading.show();

		mGreetingService.newRemoveScheduleItem(removed,
				new AsyncCallback<Collection<ScheduleItemGWT>>() {
					@Override
					public void onFailure(Throwable caught) {
						loading.hide();
						Window.alert("Failed to remove item.");
					}

					@Override
					public void onSuccess(Collection<ScheduleItemGWT> result) {
						mCalendarItems = new ArrayList<ScheduleItemGWT>();
						for (ScheduleItemGWT schdItem : result) {
							mCalendarItems.add(schdItem);
						}
//						Collections.sort(mCalendarItems,
//								new ScheduleItemComparator());
//
//						mFiltersDialog.addItems(mCalendarItems);
//						filterScheduleItems(mSearchBox.getText());
						loading.hide();
					}
				});
	}
}
