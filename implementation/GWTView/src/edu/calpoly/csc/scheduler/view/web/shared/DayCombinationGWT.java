package edu.calpoly.csc.scheduler.view.web.shared;

import java.io.Serializable;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;

import edu.calpoly.csc.scheduler.model.schedule.Day;

public class DayCombinationGWT implements Serializable {
   private Set<Integer> days;
	
   public DayCombinationGWT(){
	   days = new TreeSet<Integer>();
   }
   
	public Set<Integer> getDays() {
		return days;
	}
	
	public void setDays(Set<Integer> days) {
		this.days = days;
	}
	
	public DayCombinationGWT clone() {
		DayCombinationGWT newWeek = new DayCombinationGWT();
		Set<Integer> newDays = new TreeSet<Integer>();
		for (Integer day : days)
			newDays.add(day);
		newWeek.days = newDays;
		return newWeek;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((days == null) ? 0 : days.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DayCombinationGWT other = (DayCombinationGWT) obj;
		if (days == null) {
			if (other.days != null)
				return false;
		} else if (!toString().equals(other.toString()))
			return false;
		return true;
	}
	
	public String toString() {
		String result = "";
		if (days.contains(0))
			result += "Su";
		if (days.contains(1))
			result += "M";
		if (days.contains(2))
			result += "Tu";
		if (days.contains(3))
			result += "W";
		if (days.contains(4))
			result += "Th";
		if (days.contains(5))
			result += "F";
		if (days.contains(6))
			result += "Sa";
		return result;
	}
	
	public static DayCombinationGWT fromString(String string) {
		DayCombinationGWT days = new DayCombinationGWT();
		if (string.contains("Su"))
			days.days.add(0);
		if (string.contains("M"))
			days.days.add(1);
		if (string.contains("Tu"))
			days.days.add(2);
		if (string.contains("W"))
			days.days.add(3);
		if (string.contains("Th"))
			days.days.add(4);
		if (string.contains("F"))
			days.days.add(5);
		if (string.contains("Sa"))
			days.days.add(6);
		return days;
	}
}
