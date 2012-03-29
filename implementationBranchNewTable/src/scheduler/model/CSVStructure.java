package scheduler.model;


class CSVStructure {
   static final String[] TOP_COMMENTS = new String[] {
      "This is a CSV file whose contents represent a schedule.",
      "It is highly recommended you make a backup before modifying anything.",
      "Feel free to modify it but please do not modify any lines starting with a pound sign \"#\"."
   };

   static final String SCHEDULE_MARKER = "Schedule";
   // Schedule name, etc. goes here
   static final String SCHEDULE_END_MARKER = "End Schedule";
   
   static final String COURSES_MARKER = "Courses";
   // Courses go here
   static final String COURSES_END_MARKER = "End Courses";

   static final String LOCATIONS_MARKER = "Locations";
   // Locations go here
   static final String LOCATIONS_END_MARKER = "End Locations";

   static final String INSTRUCTORS_COURSE_PREFS_MARKER = "Instructors' Course Preferences";
   static final String INSTRUCTOR_COURSE_PREFS_MARKER = "Instructor's Course Preferences";
   // Course prefs go here
   static final String INSTRUCTOR_COURSE_PREFS_END_MARKER = "End Instructor's Course Preferences";
   static final String INSTRUCTORS_COURSE_PREFS_END_MARKER = "End Instructors' Course Preferences";

   static final String ALL_INSTRUCTORS_TIME_PREFS_MARKER = "Instructors' Time Preferences";
   static final String SINGLE_INSTRUCTOR_TIME_PREFS_MARKER = "Instructor's Time Preferences";
   // Time prefs go here
   static final String SINGLE_INSTRUCTOR_TIME_PREFS_END_MARKER = "End Instructor's Time Preferences";
   static final String ALL_INSTRUCTORS_TIME_PREFS_END_MARKER = "End Instructors' Time Preferences";

   static final String INSTRUCTORS_MARKER = "Instructors";
   // Instructors go here
   static final String INSTRUCTORS_END_MARKER = "End Instructors";
   
   static final String SCHEDULE_ITEMS_MARKER = "Schedule Items";
   // Schedule items go here
   static final String SCHEDULE_ITEMS_END_MARKER = "End Schedule Items";

   static final String CONFLICTING_SCHEDULE_ITEMS_MARKER = "Conflicting Schedule Items";
   static final String CONFLICTING_SCHEDULE_ITEMS_END_MARKER = "End Conflicting Schedule Items";
}