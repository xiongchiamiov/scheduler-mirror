package edu.calpoly.csc.scheduler.view.web.client.calendar;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
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
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import edu.calpoly.csc.scheduler.view.web.client.GreetingServiceAsync;
import edu.calpoly.csc.scheduler.view.web.client.schedule.FiltersViewWidget;
import edu.calpoly.csc.scheduler.view.web.client.views.LoadingPopup;
import edu.calpoly.csc.scheduler.view.web.shared.CourseGWT;
import edu.calpoly.csc.scheduler.view.web.shared.DayGWT;
import edu.calpoly.csc.scheduler.view.web.shared.DocumentGWT;
import edu.calpoly.csc.scheduler.view.web.shared.ScheduleItemGWT;

/**
 * This widget contains the calendar and list of available classes. It also
 * contains the bottom button bar and handles the filter and edit item dialogs.
 * 
 * @author Tyler Yero, Matt Schirle
 */

public class ScheduleEditWidget implements CloseHandler<PopupPanel> {

	private final DocumentGWT mDocument;
	private Map<Integer, CourseGWT> mCourses = new HashMap<Integer, CourseGWT>();

	private GreetingServiceAsync mGreetingService;
	private ArrayList<ScheduleItemGWT> mCalendarItems = new ArrayList<ScheduleItemGWT>();
	private VerticalPanel mMainPanel = new VerticalPanel();
	private boolean mIsCourseListCollapsed;

	private final DragAndDropController mDragController = new DragAndDropController(this);
	private CourseListView mAvailableCoursesView = new CourseListView(this, mDragController);
	private CalendarTableView mCalendarTableView = new CalendarTableView(this, mDragController);
	private ListTableView mListTableView = new ListTableView(this);

	private FiltersViewWidget mFiltersDialog = new FiltersViewWidget();

	private DisplayType mCurrentDisplayType = DisplayType.Calendar;
	private TextBox mSearchBox;
	HorizontalPanel mBoxesAndSchedulePanel;
	final Button mCollapseScheduleButton = new Button("<");

	public enum DisplayType {
		Calendar,
		List
	}
	
	public ScheduleEditWidget(GreetingServiceAsync service, DocumentGWT document) {
		mGreetingService = service;
		mDocument = document;

		final LoadingPopup loading = new LoadingPopup();
		loading.show();

		layoutBottomButtonBar();
		layoutListBoxAndCalendar();

		final Integer documentID = document.getID();

		// Initialize collection of courses
		mGreetingService.getCoursesForDocument(documentID,
				new AsyncCallback<List<CourseGWT>>() {
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

						// Collections.sort(mCalendarItems, new
						// ScheduleItemComparator());

						// Add the attributes of the retrieved items to the
						// filters list
						// mFiltersDialog.addItems(mCalendarItems);

						// Place schedule items with any previously set filters
						// filterScheduleItems(mSearchBox.getText());

						mCalendarTableView.setScheduleItems(mCalendarItems);
						mCalendarTableView.drawTable();

						mListTableView.setScheduleItems(mCalendarItems);
						mListTableView.drawList();

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

	public void editItem(CourseGWT course) {
		Set<DayGWT> days = new HashSet<DayGWT>();
		ScheduleItemGWT item = new ScheduleItemGWT(-1, course.getID(), -1, -1,
				-1, days, 0, 0, false, false);
		editItem(true, item, null, -1);
	}

	/**
	 * Displays a dialog that allows the user to edit a schedule item's
	 * instructor, location, and time. When the user clicks "ok" the dialog will
	 * call this ScheduleEditWidget's moveItem method
	 */
	public void editItem(boolean fromList, ScheduleItemGWT originalItem,
			List<Integer> newDays, int newStartRow) {
		final EditScheduleItemDlg editDlg = new EditScheduleItemDlg(
				mGreetingService, this, mDragController, fromList,
				originalItem, newDays, newStartRow, mDocument);
		editDlg.center();
	}

	/**
	 * Get the name of a course
	 * 
	 * @param courseID
	 *            The id of the course
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
							// Collections.sort(result, new
							// ScheduleItemComparator());

							// Reset column and row spans, remove any items
							// already placed
							mCalendarItems = new ArrayList<ScheduleItemGWT>();
							for (ScheduleItemGWT item : result) {
								mCalendarItems.add(item);
							}

							// Add the attributes of the retrieved items to the
							// filters list
							// mFiltersDialog.addItems(mCalendarItems);

							// Place schedule items with any previously set
							// filters
							// filterScheduleItems(mSearchBox.getText());

							// mAvailableCourses.setItems(mScheduleItems);
							// mAvailableCourses.drawList();

							mCalendarTableView.setScheduleItems(mCalendarItems);
							mCalendarTableView.drawTable();

							mListTableView.setScheduleItems(mCalendarItems);
							mListTableView.drawList();

							populateAvailableCoursesList();

							loading.hide();
						}
					}
				});
	}

	/**
	 * Place schedule items which are not filtered.
	 */
	// private void filterScheduleItems(String search) {
	// ArrayList<String> filtInstructors = mFiltersDialog.getInstructors();
	// ArrayList<String> filtCourses = mFiltersDialog.getCourses();
	// ArrayList<String> filtRooms = mFiltersDialog.getRooms();
	// ArrayList<Integer> filtDays = mFiltersDialog.getDays();
	// ArrayList<Integer> filtTimes = mFiltersDialog.getTimes();
	//
	// for (ScheduleItemGWT item : mCalendarItems) {
	// if (filtInstructors.contains(item.getProfessor())
	// && filtCourses.contains(item.getCourseString())
	// && filtRooms.contains(item.getRoom())
	// && item.getSchdItemText().contains(search)) {
	// if (isInFilteredTimeRange(item, filtTimes)) {
	// // scheduleGrid.placeScheduleItem(item, filtDays);
	// }
	// }
	// }
	// }

	// private boolean isInFilteredTimeRange(ScheduleItemGWT item,
	// ArrayList<Integer> filtTimes) {
	// int startTimeRow = ScheduleTable.getRowFromTime(
	// item.getStartTimeHour(), item.startsAfterHalf());
	// int endTimeRow = ScheduleTable.getRowFromTime(item.getEndTimeHour(),
	// item.endsAfterHalf()) - 1;
	//
	// if (filtTimes.isEmpty()) {
	// return true;
	// }
	//
	// for (Integer timeRow : filtTimes) {
	// if (timeRow >= startTimeRow && timeRow <= endTimeRow) {
	// return true;
	// }
	// }
	//
	// return false;
	// }

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

		
		mCollapseScheduleButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				if (!mIsCourseListCollapsed) {
					mCollapseScheduleButton.setText(">");
					mAvailableCoursesView.toggle(true);
					mIsCourseListCollapsed = true;
					mCalendarTableView.setLeftOffset(0);
					mListTableView.setLeftOffset(0);
				} else {
					mCollapseScheduleButton.setText("<");
					mAvailableCoursesView.toggle(false);
					mIsCourseListCollapsed = false;
					mCalendarTableView.setLeftOffset(200);
					mListTableView.setLeftOffset(200);
				}
			}
		});

		mCollapseScheduleButton.setStyleName("floatingScheduleButtonBarItemLeft");
		bottomButtonFlowPanel.add(mCollapseScheduleButton);

		Button generateScheduleButton = new Button("Generate Schedule", new GenerateScheduleClickHandler());
		generateScheduleButton.setStyleName("floatingScheduleButtonBarItemLeft");
		bottomButtonFlowPanel.add(generateScheduleButton);

		generateScheduleButton.setStyleName("floatingScheduleButtonBarItemLeft");
		bottomButtonFlowPanel.add(generateScheduleButton);

		final ListBox listBoxViewSelector = new ListBox(false);
		listBoxViewSelector.addItem("Calendar View");
		listBoxViewSelector.addItem("List View");
		listBoxViewSelector.addChangeHandler(new ChangeHandler() {
			public void onChange(ChangeEvent event) {

				int selectedIndex = listBoxViewSelector.getSelectedIndex();
				if (selectedIndex == 0) {
					mCurrentDisplayType = DisplayType.Calendar;
					mBoxesAndSchedulePanel.remove(mListTableView);			
					mBoxesAndSchedulePanel.add(mCalendarTableView);

				}
				else {
					mCurrentDisplayType = DisplayType.List;
					mBoxesAndSchedulePanel.remove(mCalendarTableView);			
					mBoxesAndSchedulePanel.add(mListTableView);
				}
				
				// TODO: change to correct view display
			}
		});

		listBoxViewSelector.setStyleName("floatingScheduleButtonBarItemRight");
		bottomButtonFlowPanel.add(listBoxViewSelector);

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
		mGreetingService.getCoursesForDocument(mDocument.getID(),
				new AsyncCallback<List<CourseGWT>>() {
					@Override
					public void onFailure(Throwable caught) {
						Window.alert("Failed to retrieve courses");
					}

					@Override
					public void onSuccess(List<CourseGWT> result) {
						if (result != null) {
							mCourses.clear();
							for (CourseGWT course : result)
								mCourses.put(course.getID(), course);

							List<CourseGWT> availableItems = new ArrayList<CourseGWT>();

							for (ScheduleItemGWT item : mCalendarItems) {
								CourseGWT course = mCourses.get(item
										.getCourseID());
								course.setNumSections(""
										+ (course.getNumSections() - 1));
							}

							for (CourseGWT course : mCourses.values())
								if (course.isValid()
										&& course.getNumSections() > 0)
									availableItems.add(course);

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
		mBoxesAndSchedulePanel = new HorizontalPanel();
		mBoxesAndSchedulePanel.setSpacing(2);
		mBoxesAndSchedulePanel
				.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);

		mBoxesAndSchedulePanel.add(mAvailableCoursesView);
		mAvailableCoursesView.drawList();

		mCalendarTableView.setLeftOffset(200);
		mListTableView.setLeftOffset(200);

		if (mCurrentDisplayType == DisplayType.Calendar) {
			mBoxesAndSchedulePanel.add(mCalendarTableView);			
		}
		else if (mCurrentDisplayType == DisplayType.List) {
			mBoxesAndSchedulePanel.add(mListTableView);
		}
		
		mMainPanel.add(mBoxesAndSchedulePanel);		
	}

	/**
	 * Called when the filters dialog closes
	 */
	public void onClose(CloseEvent<PopupPanel> event) {
		// TODO
		// filterScheduleItems(mSearchBox.getText());
	}

	/**
	 * Called when a schedule item is dragged to a new position, or when a
	 * course from one of the lists is dropped onto the schedule
	 * 
	 * @param item
	 *            The new item that will replace the old item with the same id
	 */
	public void updateItem(final ScheduleItemGWT item) {
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

						// Collections.sort(mCalendarItems, new
						// ScheduleItemComparator());
						//
						// mFiltersDialog.addItems(mCalendarItems);
						//
						// // Place schedule items with any previously set
						// filters
						// filterScheduleItems(mSearchBox.getText());
						// loading.hide();

						mCalendarTableView.setScheduleItems(mCalendarItems);
						mCalendarTableView.drawTable();

						mListTableView.setScheduleItems(mCalendarItems);
						mListTableView.drawList();

						populateAvailableCoursesList();

						loading.hide();

						// if (rescheduledCourses.conflict.length() > 0) {
						// Window.alert(rescheduledCourses.conflict);
						// }
					}
				});
	}

	/**
	 * Called when a course from the available courses list is dropped onto the
	 * schedule
	 * 
	 * @param item
	 *            The new item that will be added to the schedule
	 */
	public void insertItem(final ScheduleItemGWT item) {
		final LoadingPopup loading = new LoadingPopup();
		loading.show();

		mGreetingService.insertScheduleItem(mDocument.getScheduleID(), item,
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

						// Collections.sort(mCalendarItems, new
						// ScheduleItemComparator());
						//
						// mFiltersDialog.addItems(mCalendarItems);
						//
						// // Place schedule items with any previously set
						// filters
						// filterScheduleItems(mSearchBox.getText());
						// loading.hide();

						mCalendarTableView.setScheduleItems(mCalendarItems);
						mCalendarTableView.drawTable();

						mListTableView.setScheduleItems(mCalendarItems);
						mListTableView.drawList();

						populateAvailableCoursesList();

						loading.hide();

						// if (rescheduledCourses.conflict.length() > 0) {
						// Window.alert(rescheduledCourses.conflict);
						// }
					}
				});
	}

	// public int getSectionsOnSchedule(CourseGWT course) {
	// String dept = course.getDept();
	// String catalogNum = course.getCatalogNum();
	// int count = 0;
	//
	// for (ScheduleItemGWT item : mCalendarItems) {
	// if (item.getDept() == dept
	// && item.getCatalogNum().equals(catalogNum)) {
	// count++;
	// }
	// }
	//
	// return count;
	// }

	/* Returns all schedule items retrieved from the model's schedule object */
	public ArrayList<ScheduleItemGWT> getItemsInSchedule() {
		return mCalendarItems;
	}

	/**
	 * Called when a course from the calendar is dropped onto available courses
	 * list
	 * 
	 * @param item
	 *            The new item that will be removed from the schedule
	 */
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
						// Collections.sort(mCalendarItems,
						// new ScheduleItemComparator());
						//
						// mFiltersDialog.addItems(mCalendarItems);
						// filterScheduleItems(mSearchBox.getText());

						mCalendarTableView.setScheduleItems(mCalendarItems);
						mCalendarTableView.drawTable();

						mListTableView.setScheduleItems(mCalendarItems);
						mListTableView.drawList();

						populateAvailableCoursesList();

						loading.hide();
					}
				});
	}
}
