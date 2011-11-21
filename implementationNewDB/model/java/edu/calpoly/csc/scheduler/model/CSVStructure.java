package edu.calpoly.csc.scheduler.model;


class CSVStructure {
	static final String[] TOP_COMMENTS = new String[] {
		"(This is a CSV file whose contents represent a schedule.)",
		"(It is highly recommended you make a backup before modifying anything.)",
		"(Feel free to modify it, but please do not modify any lines completely contained in parentheses.)"
	};

	static final String SCHEDULE_MARKER = "(Schedule)";
	// Schedule name, etc. goes here
	static final String SCHEDULE_END_MARKER = "(End Schedule)";
	
	static final String COURSES_MARKER = "(Courses)";
	// Courses go here
	static final String COURSES_END_MARKER = "(End Courses)";

	static final String LOCATIONS_MARKER = "(Locations)";
	// Locations go here
	static final String LOCATIONS_END_MARKER = "(End Locations)";
	
	static final String INSTRUCTORS_COURSE_PREFS_MARKER = "(Instructors' Course Preferences)";
	// Course prefs go here
	static final String INSTRUCTORS_COURSE_PREFS_END_MARKER = "(End Instructors' Course Preferences)";

	static final String INSTRUCTORS_TIME_PREFS_MARKER = "(Instructors' Time Preferences)";
	// Time prefs go here
	static final String INSTRUCTORS_TIME_PREFS_END_MARKER = "(End Instructors' Time Preferences)";

	static final String INSTRUCTORS_ITEMS_TAUGHT_MARKER = "(Instructors' Items Taught)";
	// Items taught go here
	static final String INSTRUCTORS_ITEMS_TAUGHT_END_MARKER = "(End Instructors' Items Taught)";

	static final String INSTRUCTORS_MARKER = "(Instructors)";
	// Instructors go here
	static final String INSTRUCTORS_END_MARKER = "(End Instructors)";
	
	static final String SCHEDULE_ITEMS_MARKER = "(Schedule Items)";
	// Schedule items go here
	static final String SCHEDULE_ITEMS_END_MARKER = "(End Schedule Items)";
}
