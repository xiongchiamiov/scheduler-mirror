package edu.calpoly.csc.scheduler.model;

public enum Day {
	SUNDAY("Sunday", "Su"),
	MONDAY("Monday", "M"),
	TUESDAY("Tuesday", "Tu"),
	WEDNESDAY("Wednesday", "W"),
	THURSDAY("Thursday", "Th"),
	FRIDAY("Friday", "F"),
	SATURDAY("Saturday", "Sa");
	
	public final String name, abbreviation;
	private Day(String name, String abbreviation) {
		this.name = name;
		this.abbreviation = abbreviation;
	}
}
