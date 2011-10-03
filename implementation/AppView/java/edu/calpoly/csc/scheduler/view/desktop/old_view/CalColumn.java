package edu.calpoly.csc.scheduler.view.desktop.old_view;

import java.util.ArrayList;

import edu.calpoly.csc.scheduler.view.desktop.old_view.DaysInWeek.Day;

/**
 * CalColumn represents a column in the calendar view.
 * Each column represents a day in a week.
 * CalColumn contains a list of scheduleItem that
 * belongs to the specific day of the week, stored in
 * CalCell object
 * 
 * @author Sasiluk Ruangrongsorakai
 * 
 */

public class CalColumn {
	/** The day of the Column */
	protected Day day;
	/** List of scheduleItem in the day*/
	protected ArrayList<CalCell> calCellList;
	
	CalColumn(Day day){
		this.day = day;
		calCellList = new ArrayList<CalCell>();
	}
	
	/**
	 * Adding a CalCell box to the Column
	 * 
	 * @param cb - CalCell object containing a list of scheduleItem
	 */
	public void addCalCell(CalCell cb){
		calCellList.add(cb);
	}
	
	/**
	 * Return the day of this column
	 * 
	 * @return day of the column
	 */
	public Day getColumnDay(){
		return this.day;
	}
	
	/**
	 * Return a list of CalCell for this column
	 * 
	 * @return CalCell list
	 */
	public ArrayList<CalCell> getCalCellList(){
		return this.calCellList;
	}
}
