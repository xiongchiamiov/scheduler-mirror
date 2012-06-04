package scheduler.view.web.client.views.resources.instructors;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import scheduler.view.web.client.CachedOpenWorkingCopyDocument;
import scheduler.view.web.shared.DayGWT;
import scheduler.view.web.shared.InstructorGWT;

/**
 * This is a widget to show and modify the time
 * preferences of an instructor
 * @author Jacob Juszak, modified by Carsten Pfeffer <pfeffer@tzi.de>
 */
public class TimePrefsWidget extends VerticalPanel {
	/**
	 * This subclass is a a cell for the time preferences
	 */
	class TimePrefsCellWidget extends FocusPanel {
		int halfHour, day;
		ListBox list = new ListBox();
		
		/**
		 * sets the time data
		 * @param halfHour
		 * @param day
		 */
		TimePrefsCellWidget(int halfHour, int day) {
			this.halfHour = halfHour;
			this.day = day;
			this.add(list);
		}
		
		/**
		 * adds the lists labels to the selectbox
		 */
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
		
		/**
		 * sets the preference of this cell
		 * @param desire ("not possible" - 0, "not preferred" - 1 etc...)
		 */
		public void setCellPreference(int desire)
		{
			setPreference(this, desire);
			list.setSelectedIndex(desire);
			String lastStyle = list.getStyleName();
			list.removeStyleName(lastStyle);
			list.addStyleName(styleNames[3 - desire]);
		}
		
		/**
		 * adds a style name
		 * @param style
		 */
		public void addListStyle(String style)
		{
			String lastStyle = list.getStyleName();
			
			if(lastStyle != null) list.removeStyleName(lastStyle);
			
			list.addStyleName(style);
		}
		
		/**
		 * returns the selection index
		 * @return
		 */
		public int getIndex()
		{
			return list.getSelectedIndex();
		}
		
		/**
		 * sets the selection by index ("not possible" - 0, "not preferred" - 1 etc...)
		 * @param desire
		 */
		public void setIndex(int desire)
		{
			list.setSelectedIndex(desire);
		}
	}
	
	/**
	 * basically the data container
	 */
	public interface Strategy {
		InstructorGWT getSavedInstructor();
		InstructorGWT getInstructor();
		void autoSave();
	}
	
	protected ListBox fromList = new ListBox();
	protected ListBox toList = new ListBox();
	protected ListBox multiSet = new ListBox();
	protected CachedOpenWorkingCopyDocument workingCopyDocument;
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
	protected final FocusPanel focusThree = new FocusPanel();
//	private int documentID;
	protected InstructorGWT instructor;
	protected InstructorGWT savedInstructor;
	
	/**
	 * The following parameters are needed to get and save the time preferences
	 * @param workingCopyDocument
	 * @param documentID
	 * @param instructor
	 */
	public TimePrefsWidget(CachedOpenWorkingCopyDocument openDocument, final InstructorGWT instructor)
	{
		DOM.setElementAttribute(this.monday.getElement(), "id", "mondayCheck");
		DOM.setElementAttribute(this.tuesday.getElement(), "id", "tuesdayCheck");
		DOM.setElementAttribute(this.wednesday.getElement(), "id", "wednesdayCheck");
		DOM.setElementAttribute(this.thursday.getElement(), "id", "thursdayCheck");
		DOM.setElementAttribute(this.friday.getElement(), "id", "fridayCheck");
		
		this.workingCopyDocument = openDocument;
		instructor.verify();
//		this.documentID = documentID;
		this.instructor = instructor;
		this.savedInstructor = new InstructorGWT(instructor);
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

	/**
	 * prints the given exception to the UI
	 * @param e
	 */
	static void printException(Throwable e) {
		String st = e.getClass().getName() + ": " + e.getMessage();
		for (StackTraceElement ste : e.getStackTrace())
			st += "<br />" + ste.toString();
		RootPanel.get().clear();
		RootPanel.get().add(new HTML(st));
	}
	
	/**
	 * sets the preference for a certain cell
	 * @param cell
	 * @param desire ("not possible" - 0, "not preferred" - 1 etc...)
	 */
	void setPreference(TimePrefsCellWidget cell, int desire) {
		InstructorGWT instructor = strategy.getInstructor();

		Integer dayNum = cell.day;
		DayGWT day = DayGWT.values()[dayNum];
		instructor.gettPrefs()[day.ordinal() + 1][cell.halfHour + 14] = desire;
		instructor.gettPrefs()[day.ordinal() + 1][cell.halfHour + 15] = desire;
		
		assert(getPreference(instructor, cell.halfHour, cell.day + 1) == desire);
		
		assert(cell != null);
		cell.addListStyle(styleNames[3-desire]);
	}

	/**
	 * returns the preference for a given instructor for a certain time
	 * @param ins
	 * @param halfHour
	 * @param dayNum
	 * @return ("not possible" - 0, "not preferred" - 1 etc...)
	 */
	int getPreference(InstructorGWT ins, int halfHour, int dayNum) {
		DayGWT day = DayGWT.values()[dayNum];
		return ins.gettPrefs()[day.ordinal()][halfHour + 14];
	}
	
	/**
	 * This method is for the top panel which allows you to set mutliple preferences at once
	 */
	public void setMultiplePreferences()
	{
		for(int j = fromList.getSelectedIndex(); j < toList.getSelectedIndex() + 1; j++)
		{
			if(monday.getValue()) cells[j][0].setCellPreference(multiSet.getSelectedIndex());
			if(tuesday.getValue()) cells[j][1].setCellPreference(multiSet.getSelectedIndex());
			if(wednesday.getValue()) cells[j][2].setCellPreference(multiSet.getSelectedIndex());
			if(thursday.getValue()) cells[j][3].setCellPreference(multiSet.getSelectedIndex());
			if(friday.getValue()) cells[j][4].setCellPreference(multiSet.getSelectedIndex());
		}
	}

	@Override
	/**
	 * while the wiidget is loaded, all of the preferences have to be set.
	 */
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
			String otherString = ((hour + 12) % 12 + 1) + ":" + (halfHour % 2 == 0 ? "00" : "30") + (hour < 12 ? "am" : "pm");
			toList.addItem(otherString);
			fromList.addItem(string);
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
		
		ClickHandler handler = new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				setMultiplePreferences();
				strategy.autoSave();
			}
		};
		
		Button multiButton = new Button("Set Preferences", handler);
		
		topStuff.setWidget(1, 5, multiButton);

		/*multiSet.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				setMultiplePreferences();
				strategy.autoSave();
			}
		});*/
		
		HTML instructions = new HTML("&nbspTo change multiple preferences at once follow these steps:" +
									"<br>&nbsp1: Select the boxes of the days you wish to change." +
									"<br>&nbsp2: Choose the range of times using the 'From:' and 'To:' list boxes." +
									"<br>&nbsp3: Choose the preference level you'd like to change them to." +
									"<br>&nbsp4: Hit the 'Set Preferences' button.");
		//topStuff.setWidget(2, 0, instructions);
		focusThree.add(instructions);
		
		focus.setStyleName("otherCenterness");
		focusTwo.setStyleName("otherCenterness");
		focusThree.setStyleName("otherCenterness");
		focusTwo.add(topStuff);
		this.setSpacing(10);
		this.setStyleName("centerness");
		add(focusThree);
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
		firstdraw();
	}
	
	/**
	 * when the widget is first drawn, all of the child objects have to be created
	 */
	public void firstdraw()
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
			HTML string = new HTML("&nbsp" + ((hour + 12 - 1) % 12 + 1) + ":" + (halfHour % 2 == 0 ? "00" : "30") + (hour < 12 ? "am" : "pm"));
			this.timePrefsTable.setWidget(row, 0, string);
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
			
				cell.addListStyle(this.styleNames[3-desire]);
				this.timePrefsTable.setWidget(row, col, cell);
				
				this.cells[halfHour/2][dayNum] = cell;
			}
		}
	}
	
	/**
	 * when the widget should be just redrawn we can't recreate all of the children,
	 * since that would cause ambigious IDs. So we just reset their contents.
	 */
	public void redraw()
	{
		ArrayList<String> days = new ArrayList<String>();
		days.add("Monday");
		days.add("Tuesday");
		days.add("Wednesday");
		days.add("Thursday");
		days.add("Friday");

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
				
				TimePrefsCellWidget cell = new TimePrefsCellWidget(halfHour, dayNum);
				cell.addStyleName("desireCell");
				cell.addItems();
				cell.setIndex(desire);
			
				cell.addListStyle(this.styleNames[3-desire]);
				this.timePrefsTable.setWidget(row, col, cell);
				this.cells[halfHour/2][dayNum] = cell;
			}
		}
	}
	
	/**
	 * just for debugging...
	 * @param x
	 * @param y
	 */
	void rowOrColumnSelected(int x, int y)
	{
		System.out.println("X: "+x+", Y: "+y);
	}
	
	/**
	 * is called when the user changes a preference
	 * @param cell
	 * @param event
	 */
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
	
	/**
	 * is called when the user changes a preference
	 * @param cell
	 * @param event
	 */
	void cellWidgetMouseUp(TimePrefsCellWidget cell, MouseUpEvent event)
	{
		event.preventDefault();
		
		selectRangeOfCells(anchorCell.halfHour, anchorCell.day, cell.halfHour, cell.day);
		this.anchorCell = null;
	}
	
	/**
	 * redraws the children when a preference was changed
	 * @param event
	 */
	void CheckBoxClicked(ClickEvent event)
	{
		redraw();
	}

	/**
	 * This is used for the top panel when the user selects the range of which
	 * times should be quick-edited
	 * @param fromHalfHour
	 * @param fromDay
	 * @param toHalfHour
	 * @param toDay
	 */
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
	
	/**
	 * 
	 * @param cell
	 */
	void setAnchorCell(TimePrefsCellWidget cell) {
		if (this.anchorCell != null) {
			this.anchorCell.removeStyleName("anchorCell");
			this.anchorCell = null;
		}
		
		this.anchorCell = cell;
		this.anchorCell.addStyleName("anchorCell");
	}
	
	/**
	 * is called when the user selects a cell
	 * @param cell
	 */
	void selectCell(TimePrefsCellWidget cell) {
		if (!this.selectedCells.contains(cell)) {
			this.selectedCells.add(cell);
			cell.addStyleName("selectedCell");
		}
	}
	
	/**
	 * resets all selected cells to normal
	 */
	void clearSelectedCells() {
		for (TimePrefsCellWidget c : this.selectedCells)
			c.removeStyleName("selectedCell");
			
		this.selectedCells.clear();
	}
	
	/**
	 * turns a selected cell to unselected and an unselected one to selected
	 * @param cell
	 */
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
	
	/**
	 * refreshes the colors of the child widgets according to the preferences
	 */
	void redoColors() {
		for (int halfHour = 0; halfHour < 30; halfHour++) {
			
			for (int dayNum = 0; dayNum < 5; dayNum++) {
				TimePrefsCellWidget cell = cells[halfHour/2][dayNum];
				if (getPreference(strategy.getInstructor(), halfHour, dayNum) != getPreference(strategy.getSavedInstructor(), halfHour, dayNum))
					cell.addStyleName("changed");
				else
					cell.removeStyleName("changed");
			}
		}
	}
	
	/**
	 * saves the preferences to the working copy
	 */
	void save() {
		System.err.println("TRY TO SAVE TIME PREFERENCE");
		workingCopyDocument.editInstructor(instructor);
	}
	
	/**
	 * Sets the document and the instructor which are connected to the widget.
	 * In admin view instructors are different for each instance of this class,
	 * in instructors view documents are.
	 * @param doc
	 * @param instructor
	 */
	public void setDataSources(CachedOpenWorkingCopyDocument doc, InstructorGWT instructor)
	{
		this.workingCopyDocument = doc;
		instructor.verify();
		this.instructor = instructor;
		this.savedInstructor = new InstructorGWT(instructor);
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
		strategy.getInstructor().verify();
		strategy.getSavedInstructor().verify();
		redraw();
	}
}
