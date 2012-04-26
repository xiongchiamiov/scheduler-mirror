package scheduler.view.web.client.views.resources.instructors;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import scheduler.view.web.client.GreetingServiceAsync;
import scheduler.view.web.shared.DayGWT;
import scheduler.view.web.shared.InstructorGWT;

/**
 * This is a widget to show and modify the time
 * preferences of an instructor
 * @author unknown, modified by Carsten Pfeffer <pfeffer@tzi.de>
 */
public class TimePrefsWidget extends VerticalPanel {
	class TimePrefsCellWidget extends FocusPanel {
		int halfHour, day;
		ListBox list = new ListBox();
		
		TimePrefsCellWidget(int halfHour, int day) {
			this.halfHour = halfHour;
			this.day = day;
			this.add(list);
		}
		
		public void addItems()
		{
			list.addItem("Not Possible");
			list.addItem("Not Preferred");
			list.addItem("Acceptable");
			list.addItem("Preferred");
			
			list.addChangeHandler(new ChangeHandler() {
				@Override
				public void onChange(ChangeEvent event) {
					setCellPreference(list.getSelectedIndex());
					strategy.autoSave();
				}
			});
		}
		
		public void setCellPreference(int desire)
		{
			setPreference(this, desire);
			list.setSelectedIndex(desire);
			String lastStyle = list.getStyleName();
			list.removeStyleName(lastStyle);
			list.addStyleName(styleNames[3 - desire]);
		}
		
		public void addListStyle(String style)
		{
			String lastStyle = list.getStyleName();
			
			if(lastStyle != null) list.removeStyleName(lastStyle);
			
			list.addStyleName(style);
		}
		
		public int getIndex()
		{
			return list.getSelectedIndex();
		}
		
		public void setIndex(int desire)
		{
			list.setSelectedIndex(desire);
		}
	}
	
	public interface Strategy {
		InstructorGWT getSavedInstructor();
		InstructorGWT getInstructor();
		void autoSave();
	}
	
	protected ListBox fromList = new ListBox();
	protected ListBox toList = new ListBox();
	protected ListBox multiSet = new ListBox();
	protected GreetingServiceAsync service;
	protected Strategy strategy;
	
	protected FlexTable timePrefsTable;
	protected FlexTable topStuff;
	
	protected String[] styleNames = {"preferred", "acceptable", "notPreferred", "notQualified"};
	
	protected TimePrefsCellWidget[][] cells;
	protected List<TimePrefsCellWidget> selectedCells;
	protected TimePrefsCellWidget anchorCell;
	protected TimePrefsCellWidget lastSelectedCell;
	protected CheckBox monday = new CheckBox("Monday", true);
	protected CheckBox tuesday = new CheckBox("Tuesday", true);
	protected CheckBox wednesday = new CheckBox("Wednesday", true);
	protected CheckBox thursday = new CheckBox("Thursday", true);
	protected CheckBox friday = new CheckBox("Friday", true);
	
	
	protected final FocusPanel focus = new FocusPanel();
	protected final FocusPanel focusTwo = new FocusPanel();
//	private int documentID;
	protected InstructorGWT instructor;
	protected InstructorGWT savedInstructor;
	
	/**
	 * The following parameters are needed to get and save the time preferences
	 * @param service
	 * @param documentID
	 * @param instructor
	 */
	public TimePrefsWidget(GreetingServiceAsync service,
			int documentID, final InstructorGWT instructor)
	{
		DOM.setElementAttribute(this.monday.getElement(), "id", "mondayCheck");
		DOM.setElementAttribute(this.tuesday.getElement(), "id", "tuesdayCheck");
		DOM.setElementAttribute(this.wednesday.getElement(), "id", "wednesdayCheck");
		DOM.setElementAttribute(this.thursday.getElement(), "id", "thursdayCheck");
		DOM.setElementAttribute(this.friday.getElement(), "id", "fridayCheck");
		
		this.service = service;
		instructor.verify();
//		this.documentID = documentID;
		this.instructor = instructor;
		this.savedInstructor = new InstructorGWT(instructor);
		this.service = service;
		this.strategy = new TimePrefsWidget.Strategy() {
			public InstructorGWT getSavedInstructor() {
				return savedInstructor;
			}

			public InstructorGWT getInstructor() {
				return TimePrefsWidget.this.instructor;
			}

			public void autoSave() {
				save();
			}
		};

		selectedCells = new LinkedList<TimePrefsCellWidget>();

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

	/*void setSelectedCellsContents(int value) {
		for (TimePrefsCellWidget cell : selectedCells)
			setPreference(cell, value);
		//redoColors();
	}*/

	void setPreference(TimePrefsCellWidget cell, int desire) {
		InstructorGWT instructor = strategy.getInstructor();

		Integer dayNum = cell.day;
		DayGWT day = DayGWT.values()[dayNum];
		instructor.gettPrefs()[day.ordinal() + 1][cell.halfHour] = desire;
		instructor.gettPrefs()[day.ordinal() + 1][cell.halfHour + 1] = desire;
		
		assert(getPreference(instructor, cell.halfHour, cell.day + 1) == desire);
		
		assert(cell != null);
		cell.addListStyle(styleNames[3-desire]);
	}

	int getPreference(InstructorGWT ins, int halfHour, int dayNum) {
		DayGWT day = DayGWT.values()[dayNum];
		return ins.gettPrefs()[day.ordinal()][halfHour];
	}
	
	public void setMultiplePreferences()
	{
		for(int j = fromList.getSelectedIndex(); j < toList.getSelectedIndex() + 1; j++)
		{
			if(monday.getValue()) cells[j][0].setCellPreference(multiSet.getSelectedIndex());//setPreference(cells[j][1], multiSet.getSelectedIndex());
			if(tuesday.getValue()) cells[j][1].setCellPreference(multiSet.getSelectedIndex());//setPreference(cells[j][2], multiSet.getSelectedIndex());
			if(wednesday.getValue()) cells[j][2].setCellPreference(multiSet.getSelectedIndex());//setPreference(cells[j][3], multiSet.getSelectedIndex());
			if(thursday.getValue()) cells[j][3].setCellPreference(multiSet.getSelectedIndex());//setPreference(cells[j][4], multiSet.getSelectedIndex());
			if(friday.getValue()) cells[j][4].setCellPreference(multiSet.getSelectedIndex());//setPreference(cells[j][5], multiSet.getSelectedIndex());
		}
	}

	@Override
	protected void onLoad() {
		super.onLoad();
		topStuff = new FlexTable();
		topStuff.setHeight("100%");
		topStuff.setWidth("100%");
		
		monday.setValue(true);
		tuesday.setValue(true);
		wednesday.setValue(true);
		thursday.setValue(true);
		friday.setValue(true);
		
		topStuff.setWidget(0, 0, monday);
		topStuff.setWidget(0, 1, tuesday);
		topStuff.setWidget(0, 2, wednesday);
		topStuff.setWidget(0, 3, thursday);
		topStuff.setWidget(0, 4, friday);
		
		fromList.setName("From: ");
		toList.setName("To: ");
		
		fromList.setTitle("From: ");
		toList.setTitle("To: ");
				
		for (int halfHour = 0; halfHour < 30; halfHour+=2) { // There are 30 half-hours between 7am and 10pm
			int hour = halfHour / 2 + 7; // divide by two to get hours 0-15. Add 7 to get hours 7-22.
			String string = ((hour + 12 - 1) % 12 + 1) + ":" + (halfHour % 2 == 0 ? "00" : "30") + (hour < 12 ? "am" : "pm");
			fromList.addItem(string);
			toList.addItem(string);
		}
		
		multiSet.addItem("Not Possible");
		multiSet.addItem("Not Preferred");
		multiSet.addItem("Acceptable");
		multiSet.addItem("Preferred");
		
		topStuff.setWidget(1, 0, new HTML("From:"));
		topStuff.setWidget(1, 2, new HTML("To:"));
		
		topStuff.getWidget(1, 0).setStyleName("rightness");
		topStuff.getWidget(1, 2).setStyleName("rightness");
		
		topStuff.setWidget(1, 1, fromList);
		topStuff.setWidget(1, 3, toList);
		topStuff.setWidget(0, 5, multiSet);
		
		topStuff.setWidget(1, 4, new HTML(" "));
		topStuff.getWidget(1, 0).setWidth("100px");
		topStuff.getWidget(1, 1).setWidth("100px");
		topStuff.getWidget(1, 2).setWidth("100px");
		topStuff.getWidget(1, 3).setWidth("100px");
		topStuff.getWidget(1, 4).setWidth("100px");

		multiSet.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				setMultiplePreferences();
				strategy.autoSave();
			}
		});
		
		focus.setStyleName("otherCenterness");
		focusTwo.setStyleName("otherCenterness");
		focusTwo.add(topStuff);
		this.setSpacing(10);
		this.setStyleName("centerness");
		add(focusTwo);
		add(focus);
		focus.addKeyDownHandler(new KeyDownHandler() {
			@Override
			public void onKeyDown(KeyDownEvent event) {
				event.preventDefault();

				if (event.getNativeKeyCode() == KeyCodes.KEY_TAB) {
					if (lastSelectedCell != null && lastSelectedCell.day + 1 < 7) {
						clearSelectedCells();
						TimePrefsCellWidget cell = cells[lastSelectedCell.halfHour][lastSelectedCell.day + 1];
						selectCell(cell);
						lastSelectedCell = cell;
					}
				}
			}
		});
		redraw();
	}
	
	public void redraw()
	{
		this.timePrefsTable = new FlexTable();

		focus.add(timePrefsTable);
		this.timePrefsTable.addStyleName("timePreferencesTable");
		this.timePrefsTable.setWidth("100%");
		this.timePrefsTable.setCellSpacing(0);
		this.timePrefsTable.setCellPadding(0);
		DOM.setElementAttribute(this.timePrefsTable.getElement(), "id", "timePrefsTable");
		
		for (int halfHour = 0; halfHour < 30; halfHour+=2) {
			// There are 30 half-hours between 7am and 10pm
			int row = halfHour/2 + 1;
			int hour = halfHour / 2 + 7; // divide by two to get hours 0-15. Add 7 to get hours 7-22.
			String string = ((hour + 12 - 1) % 12 + 1) + ":" + (halfHour % 2 == 0 ? "00" : "30") + (hour < 12 ? "am" : "pm");
			this.timePrefsTable.setText(row, 0, string);
		}
		
		ArrayList<String> days = new ArrayList<String>();
		days.add("Monday");
		days.add("Tuesday");
		days.add("Wednesday");
		days.add("Thursday");
		days.add("Friday");
		for (int day = 0; day < days.size(); day++) {
			HTML html = new HTML(days.get(day));
			html.setStyleName("timePrefs");
			//Widget widget = new Widget(html);
			this.timePrefsTable.setWidget(0, day + 1, html);
			this.timePrefsTable.getWidget(0, day + 1).setStyleName("timePrefs");
		}
		
		
		
		this.cells = new TimePrefsCellWidget[15][days.size()];
		
		final int totalHalfHours = 30;
		final int totalDays = days.size();
		int row = 0;
		for (int halfHour = 0; halfHour < totalHalfHours; halfHour+=2) {
			row++;
			
			for (int dayNum = 0; dayNum < totalDays; dayNum++) {
				int col = dayNum + 1;
				int prefCol = 0;
				
				if(days.get(dayNum).equals("Monday")) prefCol = 0;
				if(days.get(dayNum).equals("Tuesday")) prefCol = 1;
				if(days.get(dayNum).equals("Wednesday")) prefCol = 2;
				if(days.get(dayNum).equals("Thursday")) prefCol = 3;
				if(days.get(dayNum).equals("Friday")) prefCol = 4;

				int desire = this.getPreference(strategy.getInstructor(), halfHour, prefCol + 1);
				if(desire > 3) desire = 3;
				if(desire < 0) desire = 0;
				final TimePrefsCellWidget cell = new TimePrefsCellWidget(halfHour, dayNum);
				cell.addStyleName("desireCell");
				cell.addItems();
				cell.setIndex(desire);
				
				//cell.add(list);
				cell.addListStyle(this.styleNames[3-desire]);
				//cell.add(new HTML(Integer.toString(desire)));
				/*cell.addClickHandler(new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
						cellWidgetClicked(cell, event);
					}
				});*/
				
				/*cell.addMouseDownHandler(new MouseDownHandler() {
					@Override
					public void onMouseDown(MouseDownEvent event) {
						cellWidgetMouseDown(cell, event);
					}
				});
				
				cell.addMouseUpHandler(new MouseUpHandler() {
					@Override
					public void onMouseUp(MouseUpEvent event) {
						cellWidgetMouseUp(cell, event);
						lastSelectedCell = cell;
						focus.setFocus(true);
					}
				});*/
								
				this.timePrefsTable.setWidget(row, col, cell);
				
				this.cells[halfHour/2][dayNum] = cell;
			}
		}
	}
	
	void rowOrColumnSelected(int x, int y)
	{
		System.out.println("X: "+x+", Y: "+y);
	}
	
	void cellWidgetMouseDown(TimePrefsCellWidget cell, MouseDownEvent event)
	{
		event.preventDefault();
		
		if (!event.isControlKeyDown()) {
			clearSelectedCells();
			this.anchorCell = null;
		}
		if (this.anchorCell == null)
			this.anchorCell = cell;
		selectRangeOfCells(this.anchorCell.halfHour, this.anchorCell.day, cell.halfHour, cell.day);
	}
	
	void cellWidgetMouseUp(TimePrefsCellWidget cell, MouseUpEvent event)
	{
		event.preventDefault();
		
		selectRangeOfCells(anchorCell.halfHour, anchorCell.day, cell.halfHour, cell.day);
		this.anchorCell = null;
	}
	
	void CheckBoxClicked(ClickEvent event)
	{
		redraw();
	}

	/*void cellWidgetClicked(CellWidget cell, ClickEvent event) {
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
	}*/

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
				selectCell(this.cells[halfHour][day]);
	}
	
	void setAnchorCell(TimePrefsCellWidget cell) {
		if (this.anchorCell != null) {
			this.anchorCell.removeStyleName("anchorCell");
			this.anchorCell = null;
		}
		
		this.anchorCell = cell;
		this.anchorCell.addStyleName("anchorCell");
	}
	
	void selectCell(TimePrefsCellWidget cell) {
		if (!this.selectedCells.contains(cell)) {
			this.selectedCells.add(cell);
			cell.addStyleName("selectedCell");
		}
	}
	
	void clearSelectedCells() {
		for (TimePrefsCellWidget c : this.selectedCells)
			c.removeStyleName("selectedCell");
			
		this.selectedCells.clear();
	}
	
	void toggleCellSelected(TimePrefsCellWidget cell) {
		if (!this.selectedCells.contains(cell)) {
			this.selectedCells.add(cell);
			cell.addStyleName("selectedCell");
		}
		else {
			this.selectedCells.remove(cell);
			cell.removeStyleName("selectedCell");
		}
	}
	
	void redoColors() {
		for (int halfHour = 0; halfHour < 30; halfHour++) {
//			int hour = halfHour / 2 + 7; // divide by two to get hours 0-15. Add 7 to get hours 7-22.
			
//			Integer time = hour * 60 + halfHour % 2 * 30;
			
			for (int dayNum = 0; dayNum < 5; dayNum++) {
				TimePrefsCellWidget cell = cells[halfHour/2][dayNum];
				if (getPreference(strategy.getInstructor(), halfHour, dayNum) != getPreference(strategy.getSavedInstructor(), halfHour, dayNum))
					cell.addStyleName("changed");
				else
					cell.removeStyleName("changed");
			}
		}
	}
	
	void save() {
		this.service.editInstructor(instructor, new AsyncCallback<Void>() {
			@Override
			public void onFailure(Throwable caught) {
				// popup.hide();
				Window.alert("Error saving instructor: " + caught.getMessage());
			}

			@Override
			public void onSuccess(Void result) {
				savedInstructor = instructor;
				instructor = new InstructorGWT(instructor);
				//redoColors();
			}
		});
	}
	
}
