package edu.calpoly.csc.scheduler.view.desktop.old_view;

import java.util.ArrayList;

import edu.calpoly.csc.scheduler.model.db.Time;
import edu.calpoly.csc.scheduler.model.schedule.ScheduleItem;

/**
 * CalCell represents each calendar cell in the calendar view
 * CalCell contains a list of scheudleItem
 * for the specific day during the specific time.
 * 
 * @author Sasiluk Ruangrongsorakai
 * 
 */
public class CalCell {
	/** Start time of the cell */
	protected Time startT;
	
	/** End time of the cell */
	protected Time endT;
	
	/** List of ScheduleItem for the specific time*/
	protected ArrayList<ScheduleItem> siList;
	
	CalCell(Time startT, Time endT){
		this.startT = startT;
		this.endT = endT;
		siList = new ArrayList<ScheduleItem>();
	}
	
	/**
	 * Adding a ScheduleItem that matches the specific
	 * start time and end time
	 * 
	 * @param si - the ScheduleItem to be added
	 */
	public void addScheduleItem(ScheduleItem si){
		siList.add(si);
	}
	
	/**
	 * Return a list of ScheduleItem that belongs to
	 * the CalCell
	 * @return list of ScheduleItem
	 */
	public ArrayList<ScheduleItem> getScheduleItemList(){
		return this.siList;
	}
	
	/**
	 * Return the start time of the cell
	 * 
	 * @return cell start time
	 */
	public Time getStartTime(){
		return this.startT;
	}
	
	/**
	 * Return the end time of the cell
	 * 
	 * @return cell end time
	 */
	public Time getEndTime(){
		return this.endT;
	}
}
