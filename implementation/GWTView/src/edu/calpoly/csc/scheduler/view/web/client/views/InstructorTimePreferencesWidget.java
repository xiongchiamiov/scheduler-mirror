package edu.calpoly.csc.scheduler.view.web.client.views;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.Event;
import com.google.gwt.event.dom.client.HandlesAllMouseEvents;

import edu.calpoly.csc.scheduler.view.web.client.GreetingServiceAsync;
import edu.calpoly.csc.scheduler.view.web.shared.DayGWT;
import edu.calpoly.csc.scheduler.view.web.shared.InstructorGWT;
import edu.calpoly.csc.scheduler.view.web.shared.TimeGWT;
import edu.calpoly.csc.scheduler.view.web.shared.TimePreferenceGWT;

public class InstructorTimePreferencesWidget extends VerticalPanel {
	class CellWidget extends FocusPanel {
		int halfHour, day;
		CellWidget(int halfHour, int day) {
			this.halfHour = halfHour;
			this.day = day;
		}
	}
	
	public interface Strategy {
		InstructorGWT getSavedInstructor();
		InstructorGWT getInstructor();
	}
	
	GreetingServiceAsync service;
	Strategy strategy;
	
	FlexTable timePrefsTable;
	
	CellWidget[][] cells;
	List<CellWidget> selectedCells;
	CellWidget anchorCell;
	
	public InstructorTimePreferencesWidget(GreetingServiceAsync service, Strategy strategy) {
		this.service = service;
		this.strategy = strategy;

		selectedCells = new LinkedList<CellWidget>();

		strategy.getInstructor().verify();
		strategy.getSavedInstructor().verify();
	}

	static void printException(Throwable e) {
		String st = e.getClass().getName() + ": " + e.getMessage();
		for (StackTraceElement ste : e.getStackTrace())
			st += "<br />" + ste.toString();
		RootPanel.get().clear();
		RootPanel.get().add(new HTML(st));
	}

	void setSelectedCellsContents(int value) {
		for (CellWidget cell : selectedCells)
			setPreference(cell, value);
		redoColors();
	}

	void setPreference(CellWidget cell, int desire) {
		InstructorGWT instructor = strategy.getInstructor();
		
		int hour = cell.halfHour / 2 + 7; // divide by two to get hours 0-15. Add 7 to get hours 7-22.
		
		TimeGWT time = new TimeGWT();
		time.setHour(hour);
		time.setMinute(cell.halfHour % 2 * 30);

		DayGWT day = new DayGWT();
		day.setNum(cell.day);

		if (instructor.gettPrefs().get(day) == null) {
			Map<TimeGWT, TimePreferenceGWT> newmap = new HashMap<TimeGWT, TimePreferenceGWT>();
			assert(newmap.get(time) == null);
			instructor.gettPrefs().put(day, newmap);
			assert(instructor.gettPrefs().get(day) == newmap);
			assert(instructor.gettPrefs().get(day).get(time) == null);
		}
		assert(instructor.gettPrefs().get(day) != null);

		if (instructor.gettPrefs().get(day).get(time) == null) {
			TimePreferenceGWT pref = new TimePreferenceGWT();
			pref.setDesire(desire);
			pref.setTime(time);
			instructor.gettPrefs().get(day).put(time, pref);
		}
		assert(instructor.gettPrefs().get(day).get(time) != null);

		instructor.gettPrefs().get(day).get(time).setDesire(desire);
		
		time = new TimeGWT();
		time.setHour(hour);
		time.setMinute(cell.halfHour % 2 * 30);

		day = new DayGWT();
		day.setNum(cell.day);
		
		assert(instructor.gettPrefs().get(day).get(time).getDesire() == desire);
		
		assert(getPreference(instructor, cell.halfHour, cell.day) == desire);
		
		assert(cell != null);
		cell.clear();
		cell.add(new HTML(new Integer(getPreference(instructor, cell.halfHour, cell.day)).toString()));
	}

	int getPreference(InstructorGWT ins, int halfHour, int dayNum) {
		int hour = halfHour / 2 + 7; // divide by two to get hours 0-15. Add 7 to get hours 7-22.
		
		TimeGWT time = new TimeGWT();
		time.setHour(hour);
		time.setMinute(halfHour % 2 * 30);
		
		DayGWT day = new DayGWT();
		day.setNum(dayNum);
		
		if (ins.gettPrefs().get(day) != null && ins.gettPrefs().get(day).get(time) != null)
			return ins.gettPrefs().get(day).get(time).getDesire();
		else
			return 0;
	}

	@Override
	protected void onLoad() {
		super.onLoad();
		
		FocusPanel focus = new FocusPanel();
		add(focus);
		focus.addKeyDownHandler(new KeyDownHandler() {
			@Override
			public void onKeyDown(KeyDownEvent event) {
				event.preventDefault();
				
				int keyCode = event.getNativeKeyCode();
				if (keyCode >= '0' && keyCode <= '9')
					setSelectedCellsContents(keyCode - '0');
			}
		});

		timePrefsTable = new FlexTable();
		focus.add(timePrefsTable);
		
		timePrefsTable.addStyleName("timePreferencesTable");
		timePrefsTable.setWidth("100%");
		timePrefsTable.setCellSpacing(0);
		timePrefsTable.setCellPadding(0);
		
		for (int halfHour = 0; halfHour < 30; halfHour++) { // There are 30 half-hours between 7am and 10pm
			int row = halfHour + 1;
			int hour = halfHour / 2 + 7; // divide by two to get hours 0-15. Add 7 to get hours 7-22.
			String string = ((hour + 12 - 1) % 12 + 1) + ":" + (halfHour % 2 == 0 ? "00" : "30") + (hour < 12 ? "am" : "pm");
			timePrefsTable.setWidget(row, 0, new HTML(string));
		}
		
		String days[] = { "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday" };
		for (int day = 0; day < 7; day++) {
			int col = day + 1;
			timePrefsTable.setWidget(0, col, new HTML(days[day]));
		}

		cells = new CellWidget[30][7];
		
		for (int halfHour = 0; halfHour < 30; halfHour++) {
			int row = halfHour + 1;
			
			for (int dayNum = 0; dayNum < 7; dayNum++) {
				int col = dayNum + 1;

				int desire = this.getPreference(strategy.getInstructor(), halfHour, dayNum);
				final CellWidget cell = new CellWidget(halfHour, dayNum);
				cell.addStyleName("desireCell");
				cell.add(new HTML(Integer.toString(desire)));
				/*cell.addClickHandler(new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
						cellWidgetClicked(cell, event);
					}
				});*/
				
				cell.addMouseDownHandler(new MouseDownHandler() {
					@Override
					public void onMouseDown(MouseDownEvent event) {
						cellWidgetMouseDown(cell, event);
					}
				});
				
				cell.addMouseUpHandler(new MouseUpHandler() {
					@Override
					public void onMouseUp(MouseUpEvent event) {
						cellWidgetMouseUp(cell, event);
					}
				});
				
				timePrefsTable.setWidget(row, col, cell);
				
				cells[halfHour][dayNum] = cell;
			}
		}
	}
	
	void cellWidgetMouseDown(CellWidget cell, MouseDownEvent event)
	{
		if (!event.isControlKeyDown()) {
			clearSelectedCells();
			anchorCell = null;
		}
		if (anchorCell == null)
			anchorCell = cell;
		selectRangeOfCells(anchorCell.halfHour, anchorCell.day, cell.halfHour, cell.day);
	}
	
	void cellWidgetMouseUp(CellWidget cell, MouseUpEvent event)
	{
		selectRangeOfCells(anchorCell.halfHour, anchorCell.day, cell.halfHour, cell.day);
		anchorCell = null;
	}

	void cellWidgetClicked(CellWidget cell, ClickEvent event) {
		if (event.isControlKeyDown()) {
			toggleCellSelected(cell);
			anchorCell = cell;
		}
		else if (event.isShiftKeyDown()) {
			if (anchorCell == null)
				anchorCell = cell;
			else
				selectRangeOfCells(anchorCell.halfHour, anchorCell.day, cell.halfHour, cell.day);
		}
		else if (event.getNativeButton() == NativeEvent.BUTTON_LEFT){
			if (anchorCell == null)
				anchorCell = cell;
			while (event.getNativeButton() == NativeEvent.BUTTON_LEFT){
					selectRangeOfCells(anchorCell.halfHour, anchorCell.day, cell.halfHour, cell.day);
					System.out.println("Got to this spot++++++++++");
			}
		}
		else {
			System.out.println("Got to this spot!!!!!!!!!!");
			clearSelectedCells();
			selectCell(cell);
			anchorCell = cell;
		}
	}

	void selectRangeOfCells(int fromHalfHour, int fromDay, int toHalfHour, int toDay) {
		if (toHalfHour < fromHalfHour) {
			int temp = toHalfHour;
			toHalfHour = fromHalfHour;
			fromHalfHour = temp;
		}
		
		if (toDay < fromDay) {
			int temp = toDay;
			toDay = fromDay;
			fromDay = temp;
		}
		
		for (int halfHour = fromHalfHour; halfHour <= toHalfHour; halfHour++)
			for (int day = fromDay; day <= toDay; day++)
				selectCell(cells[halfHour][day]);
	}
	
	void setAnchorCell(CellWidget cell) {
		if (anchorCell != null) {
			anchorCell.removeStyleName("anchorCell");
			anchorCell = null;
		}
		
		anchorCell = cell;
		anchorCell.addStyleName("anchorCell");
	}
	
	void selectCell(CellWidget cell) {
		if (!selectedCells.contains(cell)) {
			selectedCells.add(cell);
			cell.addStyleName("selectedCell");
		}
	}
	
	void clearSelectedCells() {
		for (CellWidget c : selectedCells)
			c.removeStyleName("selectedCell");
			
		selectedCells.clear();
	}
	
	void toggleCellSelected(CellWidget cell) {
		if (!selectedCells.contains(cell)) {
			selectedCells.add(cell);
			cell.addStyleName("selectedCell");
		}
		else {
			selectedCells.remove(cell);
			cell.removeStyleName("selectedCell");
		}
	}
	
	void redoColors() {
		for (int halfHour = 0; halfHour < 30; halfHour++) {
			int hour = halfHour / 2 + 7; // divide by two to get hours 0-15. Add 7 to get hours 7-22.
			
			TimeGWT time = new TimeGWT();
			time.setHour(hour);
			time.setMinute(halfHour % 2 * 30);
			
			for (int dayNum = 0; dayNum < 7; dayNum++) {
				DayGWT day = new DayGWT();
				day.setNum(dayNum);

				CellWidget cell = cells[halfHour][dayNum];
				if (getPreference(strategy.getInstructor(), halfHour, dayNum) != getPreference(strategy.getSavedInstructor(), halfHour, dayNum))
					cell.addStyleName("changed");
				else
					cell.removeStyleName("changed");
			}
		}
	}
	
}
