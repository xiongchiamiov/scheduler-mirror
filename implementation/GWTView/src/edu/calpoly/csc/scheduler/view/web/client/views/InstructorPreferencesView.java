package edu.calpoly.csc.scheduler.view.web.client.views;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.calpoly.csc.scheduler.view.web.client.GreetingServiceAsync;
import edu.calpoly.csc.scheduler.view.web.shared.DayGWT;
import edu.calpoly.csc.scheduler.view.web.shared.InstructorGWT;
import edu.calpoly.csc.scheduler.view.web.shared.TimeGWT;
import edu.calpoly.csc.scheduler.view.web.shared.TimePreferenceGWT;

public class InstructorPreferencesView extends ScrollPanel {
	class CellWidget extends FocusPanel {
		int halfHour, day;
		CellWidget(int halfHour, int day) {
			this.halfHour = halfHour;
			this.day = day;
		}
	}
	
	Panel container;
	GreetingServiceAsync service;
	InstructorGWT instructor;
	InstructorGWT savedInstructor;
	FlexTable timePrefsTable;
	
	CellWidget[][] cells;
	List<CellWidget> selectedCells;
	CellWidget anchorCell;
	
	public InstructorPreferencesView(Panel container, GreetingServiceAsync service, InstructorGWT instructor) {
		this.container = container;
		this.service = service;
		this.instructor = instructor;
		this.savedInstructor = instructor.clone();
		
		selectedCells = new LinkedList<CellWidget>();
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
		else {
			clearSelectedCells();
			selectCell(cell);
			anchorCell = cell;
		}
	}
	
	void setSelectedCellsContents(int value) {
		for (CellWidget cell : selectedCells)
			setPreference(cell.halfHour, cell.day, value);
	}
	
	void setPreference(int halfHour, int dayNum, int desire) {
		try {
			int hour = halfHour / 2 + 7; // divide by two to get hours 0-15. Add 7 to get hours 7-22.
			
//			Window.alert("derp1");
			TimeGWT time = new TimeGWT();
			time.setHour(hour);
			time.setMinute(halfHour % 2 * 30);

//			Window.alert("derp2");
			DayGWT day = new DayGWT();
			day.setNum(dayNum);

//			Window.alert("derp3");
			if (instructor.gettPrefs().get(day) == null) {
//				Window.alert("derp3.1");
				Map<TimeGWT, TimePreferenceGWT> newmap = new HashMap<TimeGWT, TimePreferenceGWT>();
//				Window.alert("derp3.15");
				assert(newmap.get(time) == null);
//				Window.alert("derp3.2");
				instructor.gettPrefs().put(day, newmap);
//				Window.alert("derp3.3");
				assert(instructor.gettPrefs().get(day) == newmap);
//				Window.alert("derp3.4");
				assert(instructor.gettPrefs().get(day).get(time) == null);
//				Window.alert("derp3.5");
//				assert(false);
			}
//			Window.alert("derp4");
			assert(instructor.gettPrefs().get(day) != null);

//			Window.alert("derp5");
			if (instructor.gettPrefs().get(day).get(time) == null) {
//				Window.alert("derp5.5");
				TimePreferenceGWT pref = new TimePreferenceGWT();
				pref.setDesire(0);
				pref.setTime(time);
//				Window.alert("derp5.6");
				instructor.gettPrefs().get(day).put(time, pref);
//				Window.alert("derp5.7");
			}
//			Window.alert("derp1");
			assert(instructor.gettPrefs().get(day).get(time) != null);

//			Window.alert("derp6");
			instructor.gettPrefs().get(day).get(time).setDesire(desire);
//			Window.alert("derp7");
			assert(instructor.gettPrefs().get(day).get(time).getDesire() == desire);
//			Window.alert("derp1");
			
			CellWidget cell = cells[halfHour][dayNum];
//			Window.alert("derp8");
			assert(cell != null);
			cell.clear();
			cell.add(new HTML(new Integer(desire).toString()));
//			Window.alert("derp9");
		}
		catch (Exception e) {
			printException(e);
		}
	}
	
	static void printException(Throwable e) {
		String st = e.getClass().getName() + ": " + e.getMessage();
		for (StackTraceElement ste : e.getStackTrace())
			st += "<br />" + ste.toString();
		RootPanel.get().clear();
		RootPanel.get().add(new HTML(st));
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
	
	void redoColors() {
		for (int halfHour = 0; halfHour < 30; halfHour++) {
			int hour = halfHour / 2 + 7; // divide by two to get hours 0-15. Add 7 to get hours 7-22.
			
			TimeGWT time = new TimeGWT();
			time.setHour(hour);
			time.setMinute(halfHour % 2 * 30);
			
			for (int dayNum = 0; dayNum < 5; dayNum++) {
				DayGWT day = new DayGWT();
				day.setNum(dayNum);

				CellWidget cell = cells[halfHour][dayNum];
				if (getPreference(instructor, halfHour, dayNum) != getPreference(savedInstructor, halfHour, dayNum))
					cell.addStyleName("changed");
				else
					cell.removeStyleName("changed");
			}
		}
	}
	
	@Override
	public void onLoad() {
		super.onLoad();

		setWidth("100%");
		setHeight("100%");
		
		VerticalPanel vp = new VerticalPanel();
		add(vp);
		
		vp.add(new Button("Save", new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				service.saveInstructor(instructor, new AsyncCallback<Void>() {
					@Override
					public void onFailure(Throwable caught) {
						Window.alert("Error saving instructor.");
						printException(caught);
					}

					@Override
					public void onSuccess(Void result) {
						savedInstructor = instructor;
						instructor = instructor.clone();
						redoColors();
					}
				});
			}
		}));
		
		FocusPanel focus = new FocusPanel();
		vp.add(focus);
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
		
		for (int halfHour = 0; halfHour <= 30; halfHour++) { // There are 30 half-hours between 7am and 10pm
			int row = halfHour + 1;
			int hour = halfHour / 2 + 7; // divide by two to get hours 0-15. Add 7 to get hours 7-22.
			String string = hour % 12 + ":" + (halfHour == 0 ? "00" : "30") + (hour < 12 ? "am" : "pm");
			timePrefsTable.setWidget(row, 0, new HTML(string));
		}
		
		String days[] = { "Monday", "Tuesday", "Wednesday", "Thursday", "Friday" };
		for (int day = 0; day < 5; day++) {
			int col = day + 1;
			timePrefsTable.setWidget(0, col, new HTML(days[day]));
		}

		cells = new CellWidget[30][5];
		
		for (int halfHour = 0; halfHour < 30; halfHour++) {
			int row = halfHour + 1;
			
			for (int dayNum = 0; dayNum < 5; dayNum++) {
				int col = dayNum + 1;

				int desire = this.getPreference(instructor, halfHour, dayNum);
				final CellWidget cell = new CellWidget(halfHour, dayNum);
				cell.addStyleName("desireCell");
				cell.add(new HTML(Integer.toString(desire)));
				cell.addClickHandler(new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
						cellWidgetClicked(cell, event);
					}
				});
				
				timePrefsTable.setWidget(row, col, cell);
				
				cells[halfHour][dayNum] = cell;
			}
		}
	}

}
