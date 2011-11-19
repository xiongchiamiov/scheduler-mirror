package edu.calpoly.csc.scheduler.view.web.shared;

import java.io.Serializable;
import java.util.Vector;

public class WeekGWT implements Serializable {
   private Vector<DayGWT> days;
	
   public WeekGWT(){
	   days = new Vector<DayGWT>();
   }
   
	public Vector<DayGWT> getDays() {
		return days;
	}
	
	public void setDays(Vector<DayGWT> days) {
		this.days = days;
	}
	
	public WeekGWT clone() {
		WeekGWT newWeek = new WeekGWT();
		Vector<DayGWT> newDays = new Vector<DayGWT>();
		for (DayGWT day : days)
			newDays.add(day.clone());
		newWeek.days = newDays;
		return newWeek;
	}
}
