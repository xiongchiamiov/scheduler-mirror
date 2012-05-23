package scheduler.view.web.shared;

import java.io.Serializable;
import java.util.Set;
import java.util.TreeSet;

public class WeekGWT implements Serializable, Comparable<WeekGWT> {
	private static final long serialVersionUID = 1L;
	
	Set<DayGWT> days = new TreeSet<DayGWT>();
	
	public WeekGWT() { }
	
	public WeekGWT(Set<DayGWT> days) {
		this.days = days;
	}

	public Set<DayGWT> getDays() { return this.days; }

	public void setDays(Set<DayGWT> days) { this.days = days; }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.days == null) ? 0 : this.days.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null || getClass() != obj.getClass())
			return false;
		WeekGWT that = (WeekGWT)obj;
		if (!(this.days == null || that.days == null ? this.days == that.days : this.days.equals(that.days)))
			return false;
		return true;
	}
	
	@Override
	public int compareTo(WeekGWT that) {
		if (this.days.size() != that.days.size())
			return this.days.size() - that.days.size();
		for (DayGWT day : DayGWT.values()) {
			boolean inThis = this.days.contains(day);
			boolean inThat = that.days.contains(day);
			if (inThis != inThat)
				return inThis ? -1 : 1;
		}
		return 0;
	}
	
	public static WeekGWT parse(String string) {
		Set<DayGWT> result = new TreeSet<DayGWT>();
		for (DayGWT day : DayGWT.values())
			if (string.contains(day.abbreviation))
				result.add(day);
		return new WeekGWT(result);
	}
	
	public String toString() { return toString(this); }
	
	public static String toString(WeekGWT derp) {
		String result = "";
		for (DayGWT day : DayGWT.values())
			if (derp.getDays().contains(day))
				result += day.abbreviation;
		return result;
	}
}
