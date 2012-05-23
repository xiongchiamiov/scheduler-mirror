package scheduler.view.web.shared;

import java.util.Set;
import java.util.TreeSet;

public enum DayGWT {
	SUNDAY("Sunday", "Su"),
	MONDAY("Monday", "M"),
	TUESDAY("Tuesday", "T"),
	WEDNESDAY("Wednesday", "W"),
	THURSDAY("Thursday", "R"),
	FRIDAY("Friday", "F"),
	SATURDAY("Saturday", "Sa");
	
	public final String name, abbreviation;
	private DayGWT(String name, String abbreviation) {
		this.name = name;
		this.abbreviation = abbreviation;
	}
	
	public static DayGWT parseDayGWT(String string) {
		for (DayGWT day : values())
			if (day.name.equals(string))
				return day;
		assert(false);
		return null;
	}
}
