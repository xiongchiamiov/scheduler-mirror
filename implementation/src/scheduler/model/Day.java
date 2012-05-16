package scheduler.model;

public enum Day {
	SUNDAY("Sunday", "Su"),
	MONDAY("Monday", "M"),
	TUESDAY("Tuesday", "T"),
	WEDNESDAY("Wednesday", "W"),
	THURSDAY("Thursday", "R"),
	FRIDAY("Friday", "F"),
	SATURDAY("Saturday", "Sa");
	
	public final String name, abbreviation;
	private Day(String name, String abbreviation) {
		this.name = name;
		this.abbreviation = abbreviation;
	}
}
