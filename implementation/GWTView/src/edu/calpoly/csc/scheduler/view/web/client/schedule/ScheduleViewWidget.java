package edu.calpoly.csc.scheduler.view.web.client.schedule;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.allen_sauer.gwt.dnd.client.drop.DropController;
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
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import edu.calpoly.csc.scheduler.view.web.client.GreetingServiceAsync;
import edu.calpoly.csc.scheduler.view.web.shared.CourseGWT;
import edu.calpoly.csc.scheduler.view.web.shared.ScheduleItemGWT;

/**
 * This class generates a widget that displays a schedule in calendar form. It
 * also has a listbox of available classes and another listbox of classes to be
 * included in the schedule. The user may drag items between these lists and the
 * calendar.
 * 
 * @author Mike McMahon, Tyler Yero
 */
public class ScheduleViewWidget implements CloseHandler<PopupPanel> {
	private GreetingServiceAsync greetingService;
	private ArrayList<ScheduleItemGWT> scheduleItems = new ArrayList<ScheduleItemGWT>();
	private VerticalPanel mainPanel = new VerticalPanel();
	private ScheduleTable scheduleGrid = new ScheduleTable(this);
	private HorizontalPanel interfacePanel = new HorizontalPanel();

	private FiltersViewWidget filtersDialog = new FiltersViewWidget();
	PickupDragController dragController = new PickupDragController(
			RootPanel.get(), false);
	TextBox searchBox;

	private DualListBox dualListBoxCourses;
	private ListBoxDragController listBoxDragController;
	private MouseListBox includedListBox;
	private HorizontalPanel boxesAndSchedulePanel;

	/**
	 * Registers all cells in schedule table as drop targets.
	 */
	private void registerDrops() {
		Iterator<Widget> allCells = scheduleGrid.iterator();
		Widget cell;
		DropController dropController;

		while (allCells.hasNext()) {
			cell = allCells.next();
			if (cell.getClass().equals(ScheduleCell.class)) {
				dropController = new ScheduleCellDropController(
						(ScheduleCell) cell, this, includedListBox);
				dragController.registerDropController(dropController);
				dualListBoxCourses.registerScheduleDrop(dropController);
				dualListBoxCourses.reregisterBoxDrops();
			}
		}
	}

	/**
	 * Retrieves a schedule items from a generated schedule from the server.
	 */
	private void getScheduleItemsFromServer() {
		if (dualListBoxCourses.getIncludedCourses().size() == 0) {
			Window.alert("No courses to schedule");
			return;
		}

		greetingService.getGWTScheduleItems(
				dualListBoxCourses.getIncludedCourses(),
				new AsyncCallback<ArrayList<ScheduleItemGWT>>() {
					public void onFailure(Throwable caught) {
						Window.alert("Failed to get schedule: "
								+ caught.toString());
					}

					public void onSuccess(ArrayList<ScheduleItemGWT> result) {
						if (result != null) {
							// Sort result by start times in ascending order
							Collections.sort(result);

							// Reset column and row spans, remove any items
							// already placed
							resetSchedule();
							scheduleItems = new ArrayList<ScheduleItemGWT>();
							for (ScheduleItemGWT item : result) {
								scheduleItems.add(item);
							}

							// Add the attributes of the retrieved items to the
							// filters list
							filtersDialog.addItems(scheduleItems);

							// Place schedule items with any previously set
							// filters
							filterScheduleItems(searchBox.getText());
						}
					}
				});
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
		scheduleGrid.clear();
		scheduleGrid.resetColumnsOfDays();
		scheduleGrid.resetRowSpans();
		scheduleGrid.resetDayColumnSpans();
		resetIsPlaced();
		scheduleGrid.trimExtraCells();
		scheduleGrid.setTimes();
		scheduleGrid.setDaysOfWeek();
		scheduleGrid.placePanels();
		dragController.unregisterDropControllers();
		dualListBoxCourses.getDragController().unregisterDropControllers();
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
		for (ScheduleItemGWT item : scheduleItems) {
			if (filtInstructors.contains(item.getProfessor())
					&& filtCourses.contains(item.getCourse())
					&& filtRooms.contains(item.getRoom())
					&& item.getSchdItemText().contains(search)) {
				scheduleGrid.placeScheduleItem(item, filtDays);
			}
		}
		registerDrops();
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
	private void layoutInterface() {
		searchBox = new TextBox();
		searchBox.addKeyPressHandler(new KeyPressHandler() {
			public void onKeyPress(KeyPressEvent event) {
				if (event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ENTER) {
					search();
				}
			}
		});
		interfacePanel.add(searchBox);
		interfacePanel.add(new Button("Filters", new ClickHandler() {
			public void onClick(ClickEvent event) {
				// Causes the filters dialog to appear in the center of this
				// widget
				filtersDialog.center();
			}
		}));

		/*
		 * Causes this class' onClose method to be called when the filters
		 * dialog is closed
		 */
		filtersDialog.addCloseHandler(this);
		interfacePanel.add(new Button("Generate Schedule",
				new GenerateScheduleClickHandler()));
		mainPanel.add(interfacePanel);
	}

	/*Displays only schedule items which contain text in the search box*/
	private void search() {
		filterScheduleItems(searchBox.getText());
	}

	/*Retrieves the course list and adds it to the available courses box*/
	private void addCoursesToBoxes() {
		greetingService.getCourses(new AsyncCallback<ArrayList<CourseGWT>>() {
			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Failed to retrieve courses");
				registerDrops();
			}

			@Override
			public void onSuccess(ArrayList<CourseGWT> result) {
				if (result != null) {
					if(result.size() > 10)
					{
					 dualListBoxCourses.setListLength(result.size());
					}
					for (CourseGWT course : result) {
						dualListBoxCourses.addLeft(new CourseListItem(course));
					}

					registerDrops();
				}
			}
		});
	}

	/*Laysout the available and included courses boxes and the schedule*/
	private void layoutBoxesAndSchedule() {
		boxesAndSchedulePanel = new HorizontalPanel();
		boxesAndSchedulePanel.setSpacing(2);
		boxesAndSchedulePanel
				.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		dualListBoxCourses = new DualListBox(10, "10em", 10);
		includedListBox = dualListBoxCourses.getIncludedListBox();
		listBoxDragController = new ListBoxDragController(
				dualListBoxCourses);
		boxesAndSchedulePanel.add(dualListBoxCourses);
		scheduleGrid.layoutDaysAndTimes();
		scheduleGrid.placePanels();
		boxesAndSchedulePanel.add(scheduleGrid);

		// add some items to the list
		addCoursesToBoxes();
		mainPanel.add(boxesAndSchedulePanel);
	}

	/**
	 * Returns this widget in its entirety.
	 * 
	 * @param service
	 *            The server-side service which this widget will contact
	 * @return This widget
	 */
	public Widget getWidget(GreetingServiceAsync service) {
		greetingService = service;
		layoutInterface();
		layoutBoxesAndSchedule();
		return mainPanel;
	}

	/**
	 * Called when the filters dialog closes
	 */
	public void onClose(CloseEvent<PopupPanel> event) {
		filterScheduleItems(searchBox.getText());
	}

	/*Converts a row to an hour*/
	private int getHourFromRow(int row) {
		return (row / 2) + 6 + (row % 2);
	}

	/*Returns true if a row is at a half hour (i.e. 7:30), false if a row is on
	 * the hour (i.e. 7:00)
	 */
	private boolean rowIsAtHalfHour(int row) {
		return row % 2 == 0;
	}

	/*Called when a schedule item is dragged to a new position, or when a
	 * course from one of the lists is dropped onto the schedule
	 */
	public void moveItem(final ScheduleItemGWT scheduleItem,
			ArrayList<Integer> days, int row, boolean inSchedule) {
		final int startHour = getHourFromRow(row);
		final boolean atHalfHour = rowIsAtHalfHour(row);
		greetingService.rescheduleCourse(scheduleItem, days, startHour,
				atHalfHour, inSchedule,
				new AsyncCallback<ArrayList<ScheduleItemGWT>>() {
					@Override
					public void onFailure(Throwable caught) {
						Window.alert("Failed to retrieve rescheduled item");
					}

					@Override
					public void onSuccess(ArrayList<ScheduleItemGWT> rescheduled) {
						if (rescheduled == null) {
							Window.alert("Course could not be rescheduled at time "
									+ startHour + (atHalfHour ? ":30" : ":00"));
						} else {
							scheduleItems = new ArrayList<ScheduleItemGWT>();
							for (ScheduleItemGWT schdItem : rescheduled) {
								scheduleItems.add(schdItem);
							}
							Collections.sort(scheduleItems);
						}
						filtersDialog.addItems(scheduleItems);
						filterScheduleItems(searchBox.getText());
					}
				});
	}

	/*Highlights the border of a row*/
	public void highlightRow(int row) {
		scheduleGrid.getRowFormatter().addStyleName(row, "highlightedBorder");
	}

	/*Unhighlights the border of a row*/
	public void unhighlightRow(int row) {
		scheduleGrid.getRowFormatter()
				.removeStyleName(row, "highlightedBorder");
		scheduleGrid.getRowFormatter().removeStyleName(row - 1,
				"highlightedBorder");
	}

	/*Returns all schedule items retrieved from the model's schedule object*/
	public ArrayList<ScheduleItemGWT> getItemsInSchedule() {
		return scheduleItems;
	}

	/*Returns the drag controller for items on the schedule*/
	public PickupDragController getItemDragController() {
		return dragController;
	}
}
