package edu.calpoly.csc.scheduler.view.web.client.schedule;

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
import edu.calpoly.csc.scheduler.view.web.client.views.LoadingPopup;
import edu.calpoly.csc.scheduler.view.web.client.views.ScheduleEditTable;
import edu.calpoly.csc.scheduler.view.web.shared.CourseGWT;
import edu.calpoly.csc.scheduler.view.web.shared.ScheduleItemGWT;
import edu.calpoly.csc.scheduler.view.web.shared.ScheduleItemList;

/**
 * 
 * @author Tyler Yero
 */
public class ScheduleEditWidget implements CloseHandler<PopupPanel> {

	private GreetingServiceAsync greetingService;
	private HashMap<String, ScheduleItemGWT> schedItems;
	private ArrayList<ScheduleItemGWT> scheduleItems = new ArrayList<ScheduleItemGWT>();
	private VerticalPanel mainPanel = new VerticalPanel();
	private boolean isCourseListCollapsed;
	// private ScheduleTable scheduleGrid = new ScheduleTable(this);
	private ScheduleEditTable scheduleTable = new ScheduleEditTable(this);	
	private FiltersViewWidget filtersDialog = new FiltersViewWidget();
	
	private TextBox searchBox;
	private ListBox availableCoursesListBox;	

	/**
	 * Returns this widget in its entirety.
	 * 
	 * @param service
	 *            The server-side service which this widget will contact
	 * @return This widget
	 */
	public Widget getWidget(GreetingServiceAsync service) {
		
		greetingService = service;
		layoutBottomButtonBar();
		layoutListBoxAndCalendar();
		
		final LoadingPopup loading = new LoadingPopup();
		loading.show();
		
		greetingService.getSchedule(schedItems,
				new AsyncCallback<List<ScheduleItemGWT>>() {
					@Override
					public void onFailure(Throwable caught) {
						Window.alert("Failed to retrieve schedule.");
						loading.hide();
					}

					@Override
					public void onSuccess(List<ScheduleItemGWT> result) {
						scheduleItems = new ArrayList<ScheduleItemGWT>();
						for (ScheduleItemGWT item : result) {
							scheduleItems.add(item);
						}

						Collections.sort(scheduleItems, new ScheduleItemComparator());
						// Add the attributes of the retrieved items to the
						// filters list
						filtersDialog.addItems(scheduleItems);

						// Place schedule items with any previously set
						// filters
						filterScheduleItems(searchBox.getText());
						loading.hide();
					}

				});

		return mainPanel;
	}

	/**
	 * Retrieves a schedule items from a generated schedule from the server.
	 */
	private void getScheduleItemsFromServer() {
		final LoadingPopup loading = new LoadingPopup();

		// if (dualListBoxCourses.getIncludedCourses().size() == 0) {
		// Window.alert("No courses to schedule");
		// return;
		// }
		loading.show();
		// greetingService.generateSchedule(
		// dualListBoxCourses.getIncludedCourses(), schedItems,
		// new AsyncCallback<List<ScheduleItemGWT>>() {
		// public void onFailure(Throwable caught) {
		// loading.hide();
		// Window.alert("Failed to get schedule: "
		// + caught.toString());
		// }
		//
		// public void onSuccess(List<ScheduleItemGWT> result) {
		// if (result != null) {
		// // Sort result by start times in ascending order
		// Collections.sort(result,
		// new ScheduleItemComparator());
		//
		// // Reset column and row spans, remove any items
		// // already placed
		// resetSchedule();
		// scheduleItems = new ArrayList<ScheduleItemGWT>();
		// for (ScheduleItemGWT item : result) {
		// scheduleItems.add(item);
		// }
		//
		// // Add the attributes of the retrieved items to the
		// // filters list
		// filtersDialog.addItems(scheduleItems);
		//
		// // Place schedule items with any previously set
		// // filters
		// filterScheduleItems(searchBox.getText());
		//
		// dualListBoxCourses.removeAllFromIncluded();
		// loading.hide();
		// }
		// }
		// });
	}

	/**
	 * Sets all schedule items retrieved as not placed on the schedule.
	 */
	private void resetIsPlaced() {
		for (ScheduleItemGWT item : scheduleItems) {
			item.setPlaced(false);
		}
	}

	/**
	 * Remove all placed schedule items, return schedule to blank schedule.
	 */
	private void resetSchedule() {
		// scheduleGrid.clear();
		// scheduleGrid.resetColumnsOfDays();
		// scheduleGrid.resetRowSpans();
		// scheduleGrid.resetDayColumnSpans();
		// resetIsPlaced();
		// scheduleGrid.trimExtraCells();
		// scheduleGrid.setTimes();
		// scheduleGrid.setDaysOfWeek();
		// scheduleGrid.placePanels();
		// dragController.unregisterDropControllers();
		// dualListBoxCourses.getDragController().unregisterDropControllers();
	}

	/**
	 * Place schedule items which are not filtered.
	 */
	private void filterScheduleItems(String search) {
		resetSchedule();
		ArrayList<String> filtInstructors = filtersDialog.getInstructors();
		ArrayList<String> filtCourses = filtersDialog.getCourses();
		ArrayList<String> filtRooms = filtersDialog.getRooms();
		ArrayList<Integer> filtDays = filtersDialog.getDays();
		ArrayList<Integer> filtTimes = filtersDialog.getTimes();

		for (ScheduleItemGWT item : scheduleItems) {
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
	
	private boolean isInFilteredTimeRange(ScheduleItemGWT item, ArrayList<Integer> filtTimes) {
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
		filtersDialog.addCloseHandler(this);

		FlowPanel bottomButtonFlowPanel = new FlowPanel();		
		bottomButtonFlowPanel.addStyleName("floatingScheduleButtonBar");
		
		final Button collapseScheduleButton = new Button("<");
		collapseScheduleButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {	
				if (!isCourseListCollapsed) {
					collapseScheduleButton.setText(">");
					availableCoursesListBox.addStyleName("hiddenCoursesList");
					isCourseListCollapsed = true;
				}
				else {
					collapseScheduleButton.setText("<");
					availableCoursesListBox.removeStyleName("hiddenCoursesList");
					isCourseListCollapsed = false;
				}
		}});
		
		collapseScheduleButton.setStyleName("floatingScheduleButtonBarItemLeft");
		bottomButtonFlowPanel.add(collapseScheduleButton);
		
		Button generateScheduleButton = new Button("Generate Schedule", new GenerateScheduleClickHandler());
		generateScheduleButton.setStyleName("floatingScheduleButtonBarItemLeft");
		bottomButtonFlowPanel.add(generateScheduleButton);
		
		generateScheduleButton.setStyleName("floatingScheduleButtonBarItemLeft");
		bottomButtonFlowPanel.add(generateScheduleButton);
		
		Button filterButton = new Button("Publish...", new ClickHandler() {
			public void onClick(ClickEvent event) {
				Window.alert("Not implemented yet.");
			}
		});
		filterButton.setStyleName("floatingScheduleButtonBarItemRight");
		bottomButtonFlowPanel.add(filterButton);
		
//		Button csvButton = new CSVButton(greetingService).getButton();
//		csvButton.addStyleName("floatingScheduleButtonBarItemRight");
//		bottomButtonFlowPanel.add(csvButton);

		searchBox = new TextBox();
		searchBox.addKeyPressHandler(new KeyPressHandler() {
			public void onKeyPress(KeyPressEvent event) {
				if (event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ENTER) {
					search();
				}
			}
		});
		searchBox.setStyleName("floatingScheduleButtonBarItemRight");
		bottomButtonFlowPanel.add(searchBox);
		
		Button filtersButton = new Button("Filters", new ClickHandler() {
			public void onClick(ClickEvent event) {
				// Causes the filters dialog to appear in the center of this
				// widget
				filtersDialog.center();
			}
		});
		filtersButton.setStyleName("floatingScheduleButtonBarItemRight");
		bottomButtonFlowPanel.add(filtersButton);		

		RootPanel.get().add(bottomButtonFlowPanel);		
		mainPanel.add(bottomButtonFlowPanel);
	}

	/**
	 * Displays only schedule items which contain text in the search box 
	 */
	private void search() {
		filterScheduleItems(searchBox.getText());
	}

	/**
	 * Retrieves the course list and adds it to the available courses box 
	 */
	private void addCoursesToListBox() {
		
		availableCoursesListBox.setVisibleItemCount(10);
		
		greetingService.getCourses(new AsyncCallback<List<CourseGWT>>() {
			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Failed to retrieve courses");
				// registerDrops();
			}

			@Override
			public void onSuccess(List<CourseGWT> result) {
				if (result != null) {
					if (result.size() > 10) {
						availableCoursesListBox.setVisibleItemCount(result.size());
					}
					
					for (CourseGWT course : result) {
						availableCoursesListBox.addItem(course.toString());
					}
				}
			}
		});
	}

	/**
	 *  Lays out the available course listbox and the schedule 
	 */
	private void layoutListBoxAndCalendar() {
		
		HorizontalPanel boxesAndSchedulePanel = new HorizontalPanel();
		boxesAndSchedulePanel.setSpacing(2);
		boxesAndSchedulePanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
				
		availableCoursesListBox = new ListBox();		
		availableCoursesListBox.setStyleName("ScheduleAvailableCoursesList");
		
		addCoursesToListBox();				
		
		boxesAndSchedulePanel.add(availableCoursesListBox);
		
		
//		HTML myButton = new HTML("<div class=\"collapsingButton\"></div>");
//		myButton.addClickHandler(new ClickHandler() {
//			public void onClick(ClickEvent event) {				
//				availableCoursesListBox.addStyleName("hiddenCoursesList");
//				
//		}});
		
//		boxesAndSchedulePanel.add(myButton);
//		
//		Element myButtonContainingTD = HTMLUtilities.getClosestContainingElementOfType(myButton.getElement(), "td");
//		myButtonContainingTD.setClassName("collapsingButtonContainingTD");
		
		// scheduleGrid.layoutDaysAndTimes();
		// scheduleGrid.placePanels();
		boxesAndSchedulePanel.add(scheduleTable);
		
		mainPanel.add(boxesAndSchedulePanel);
	}

	/**
	 * Called when the filters dialog closes
	 */
	public void onClose(CloseEvent<PopupPanel> event) {
		filterScheduleItems(searchBox.getText());
	}

	/**
	 *  Converts a row to an hour 
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
	public void moveItem(final ScheduleItemGWT scheduleItem, ArrayList<Integer> days, int row, final boolean inSchedule, final boolean fromIncluded) {
		
		final LoadingPopup loading = new LoadingPopup();
		loading.show();
		final int startHour = getHourFromRow(row);
		final boolean atHalfHour = rowIsAtHalfHour(row);
		
		CourseGWT course = new CourseGWT();
		course.setDept(scheduleItem.getDept());
		course.setCatalogNum(scheduleItem.getCatalogNum());

		greetingService.rescheduleCourse(scheduleItem, days, startHour, atHalfHour, inSchedule, schedItems, new AsyncCallback<ScheduleItemList>() {
			@Override
			public void onFailure(Throwable caught) {
				loading.hide();
				Window.alert("Failed to retrieve rescheduled item");
			}

			@Override
			public void onSuccess(ScheduleItemList rescheduled) {
				CourseGWT courseHolder;
				int sectionsIncluded, itemIndex;

				// If this course was dragged from a course list
				if (!inSchedule) {
					courseHolder = scheduleItem.getCourse();
					// sectionsIncluded =
					// includedListBox.getSectionsInBox(courseHolder);
					// itemIndex = includedListBox.contains(new
					// CourseListItem(courseHolder, true));
					// If the included list box contains sections of
					// this course
					// if (itemIndex >= 0) {
					// Decrement the section count in the included
					// list box if this course
					// was dragged from the included list box or the
					// number of combined
					// sections on the schedule and in the included
					// list box is equal to the
					// total number of sections
					// if (fromIncluded
					// || sectionsIncluded
					// + getSectionsOnSchedule(courseHolder) ==
					// availableListBox.getSectionsInBox(courseHolder))
					// {
					// // Decrement by one if there is more than
					// // one section
					// if (sectionsIncluded > 1) {
					// courseHolder = ((CourseListItem)
					// includedListBox.getWidget(itemIndex)).getCourse();
					// courseHolder.setNumSections(courseHolder.getNumSections()
					// - 1);
					// includedListBox.setWidget(itemIndex, new
					// CourseListItem(courseHolder, true));
					// }
					// // Remove the course if there is only one
					// // section
					// else {
					// includedListBox.remove(includedListBox.getWidget(itemIndex));
					// }
					// }
					// }
				}

				scheduleItems = new ArrayList<ScheduleItemGWT>();
				for (ScheduleItemGWT schdItem : rescheduled) {
					scheduleItems.add(schdItem);
				}
				Collections.sort(scheduleItems,
						new ScheduleItemComparator());

				filtersDialog.addItems(scheduleItems);
				filterScheduleItems(searchBox.getText());

				loading.hide();

				if (rescheduled.conflict.length() > 0) {
					Window.alert(rescheduled.conflict);
				}
			}
		});
	}

	public int getSectionsOnSchedule(CourseGWT course) {
		String dept = course.getDept();
		String catalogNum = course.getCatalogNum();
		int count = 0;

		for (ScheduleItemGWT item : scheduleItems) {
			if (item.getDept() == dept && item.getCatalogNum().equals(catalogNum)) {
				count++;
			}
		}

		return count;
	}

	/* Returns all schedule items retrieved from the model's schedule object */
	public ArrayList<ScheduleItemGWT> getItemsInSchedule() {
		return scheduleItems;
	}

	public void removeItem(ScheduleItemGWT removed) {
		final LoadingPopup loading = new LoadingPopup();
		loading.show();

		greetingService.removeScheduleItem(removed, schedItems, new AsyncCallback<List<ScheduleItemGWT>>() {
			@Override
			public void onFailure(Throwable caught) {
				loading.hide();
				Window.alert("Failed to remove item.");
			}

			@Override
			public void onSuccess(List<ScheduleItemGWT> result) {
				scheduleItems = new ArrayList<ScheduleItemGWT>();
				for (ScheduleItemGWT schdItem : result) {
					scheduleItems.add(schdItem);
				}
				Collections.sort(scheduleItems,
						new ScheduleItemComparator());

				filtersDialog.addItems(scheduleItems);
				filterScheduleItems(searchBox.getText());
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
