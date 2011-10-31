package edu.calpoly.csc.scheduler.view.web.shared;

import java.io.Serializable;
import java.util.Vector;

public class WeekGWT implements Serializable {
   private Vector<DayGWT> days;
	
	public Vector<DayGWT> getDays() {
		return days;
	}
	
	public void setDays(Vector<DayGWT> days) {
		this.days = days;
	}
}
