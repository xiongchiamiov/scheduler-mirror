package edu.calpoly.csc.scheduler.view.web.client.views;

import java.util.List;
import java.util.Map;

import com.google.gwt.user.client.ui.FlexTable;

import edu.calpoly.csc.scheduler.view.web.client.schedule.ScheduleViewWidget;
import edu.calpoly.csc.scheduler.view.web.shared.ScheduleItemGWT;

public class ScheduleEditTable extends FlexTable {
	
	private final ScheduleViewWidget mScheduleController;
	
	private static final int NUM_TIME_SLOTS = 30;
	private static final int NUM_DAYS = 5;
	
	private static final String TIMES[] = { "7:00am", "7:30am", "8:00am", "8:30am",
		"9:00am", "9:30am", "10:00am", "10:30am", "11:00am", "11:30am",
		"12:00pm", "12:30pm", "1:00pm", "1:30pm", "2:00pm", "2:30pm",
		"3:00pm", "3:30pm", "4:00pm", "4:30pm", "5:00pm", "5:30pm",
		"6:00pm", "6:30pm", "7:00pm", "7:30pm", "8:00pm", "8:30pm",
		"9:00pm", "9:30pm" };
	

	public ScheduleEditTable(ScheduleEditWidget scheduleController) {
		mScheduleController = scheduleController;
	}
	
	/**
	 * Places a schedule item on the schedule
	 * 
	 * @param item The item to be placed
	 * @param filteredDays The days on which the items should not show up
	 */
	public void placeScheduleItem(ScheduleItemGWT item, List<Integer> filteredDays) {
		
	}
	
	public void repaint() {
		
	}
	
	public Map<String, ScheduleItemGWT> getScheduleItems() {
		return null;
	}
	
	public void setScheduleItems(Map<String, ScheduleItemGWT> items) {
		
	}
	
	public void addScheduleItem(ScheduleItemGWT item) {
		
	}
	
	public void setDayFilter(List<Integer> days) {
		
	}
	
	public void setTimeFilter(List<Integer> times) {
		
	}
	
	public void setStringFilter(String str) {
		
	}
	
	public void setRoomFilter(List<String> rooms) {
		
	}
	
	public void setInstructorFilter(List<String> instructors) {
		
	}
	
	public void setCourseFilter(List<String> courses) {
		
	}
}
