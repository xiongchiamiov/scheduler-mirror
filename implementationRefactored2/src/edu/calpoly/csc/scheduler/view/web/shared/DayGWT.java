package edu.calpoly.csc.scheduler.view.web.shared;

import java.util.Set;
import java.util.TreeSet;

public enum DayGWT {
	SUNDAY("Sunday", "Su"),
	MONDAY("Monday", "M"),
	TUESDAY("Tuesday", "Tu"),
	WEDNESDAY("Wednesday", "W"),
	THURSDAY("Thursday", "Th"),
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
	
	public static Set<DayGWT> parseDayGWTPattern(String string) {
		Set<DayGWT> result = new TreeSet<DayGWT>();
		for (DayGWT day : values())
			if (string.contains(day.abbreviation))
				result.add(day);
		return result;
	}
	
	public static String dayGWTPatternToString(Set<DayGWT> derp) {
		String result = "";
		for (DayGWT day : derp)
			result += day.abbreviation;
		return result;
	}
}
