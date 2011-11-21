package edu.calpoly.csc.scheduler.view.web.shared;

import java.io.Serializable;
import java.util.Vector;

public class WeekGWT implements Serializable {
   private Vector<Integer> days;
	
   public WeekGWT(){
	   days = new Vector<Integer>();
   }
   
	public Vector<Integer> getDays() {
		return days;
	}
	
	public void setDays(Vector<Integer> days) {
		this.days = days;
	}
	
	public WeekGWT clone() {
		WeekGWT newWeek = new WeekGWT();
		Vector<Integer> newDays = new Vector<Integer>();
		for (Integer day : days)
			newDays.add(day);
		newWeek.days = newDays;
		return newWeek;
	}
}
