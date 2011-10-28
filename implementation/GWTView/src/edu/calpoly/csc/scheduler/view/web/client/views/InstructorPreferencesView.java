package edu.calpoly.csc.scheduler.view.web.client.views;

import java.util.LinkedList;
import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.ScrollPanel;

import edu.calpoly.csc.scheduler.view.web.client.GreetingServiceAsync;

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
	String instructorID;
	FlexTable timePrefsTable;
	
	CellWidget[][] cells;
	List<CellWidget> selectedCells;
	CellWidget anchorCell;
	
	public InstructorPreferencesView(Panel container, GreetingServiceAsync service, String instructorID) {
		this.container = container;
		this.service = service;
		this.instructorID = instructorID;
		
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
		for (CellWidget cell : selectedCells) {
			cell.clear();
			cell.add(new HTML(new Integer(value).toString()));
		}
	}
	
	@Override
	public void onLoad() {
		super.onLoad();

		setWidth("100%");
		setHeight("100%");
		
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
		
		timePrefsTable.setWidth("100%");
		timePrefsTable.setCellSpacing(0);
		
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
			
			for (int day = 0; day < 5; day++) {
				int col = day + 1;
				
				final CellWidget cell = new CellWidget(halfHour, day);
				cell.add(new HTML("0"));
				cell.addClickHandler(new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
						cellWidgetClicked(cell, event);
					}
				});
				
				timePrefsTable.setWidget(row, col, cell);
				
				cells[halfHour][day] = cell;
			}
		}
	}

}
