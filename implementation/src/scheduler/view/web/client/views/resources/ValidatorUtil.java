package scheduler.view.web.client.views.resources;

import java.util.ArrayList;
import java.util.Collection;

import scheduler.view.web.shared.CourseGWT;
import scheduler.view.web.shared.InstructorGWT;
import scheduler.view.web.shared.LocationGWT;

import com.smartgwt.client.data.Record;
import com.smartgwt.client.data.RecordList;
import com.smartgwt.client.widgets.grid.ListGridRecord;

public class ValidatorUtil {
	/**
	 * This method is used in CoursesView to highlight a cell based on if it is valid or not.
	 * @param fieldName The column name to validate
	 * @param record The course record to validate
	 * @return True if the column is valid, false otherwise
	 */
	public static boolean isValidCourseType(String fieldName,
			ListGridRecord record) {
		if (record != null) {
			String value = null;
			if (fieldName.equals("maxEnrollment")) {
				value = record.getAttributeAsString("maxEnrollment");
				return value != null && validateNonNegativeInt(value);
			} else if (fieldName.equals("numSections")) {
				value = record.getAttributeAsString("numSections");
				return value != null && validateGreaterThanZeroInt(value);
			} else if (fieldName.equals("wtu")) {
				value = record.getAttributeAsString("wtu");
				return value != null && validateGreaterThanZeroInt(value);
			} else if (fieldName.equals("scu")) {
				value = record.getAttributeAsString("scu");
				return value != null && validateGreaterThanZeroInt(value);
			} else if (fieldName.equals("hoursPerWeek")) {
				value = record.getAttributeAsString("hoursPerWeek");
				return value != null && validateMultipleOfHalf(value);
			} else if (fieldName.equals("catalogNum")) {
				value = record.getAttributeAsString("catalogNum");
				return value != null && validateNotEmpty(value);
			} else if (fieldName.equals("dayCombinations")) {
				value = record.getAttributeAsString("dayCombinations");
				return value != null && value.length() > 0;
			}
			// Default
			else {
				// Isn't a special column, is always valid
				return true;
			}
		}
		return true;
	}

	/**
	 * This method is used in InstructorsView to highlight a cell based on if it is valid or not.
	 * @param fieldName The column name to validate
	 * @param record The instructor record to validate
	 * @return True if the column is valid, false otherwise
	 */
	public static boolean isValidInstructorType(String fieldName,
			ListGridRecord record, RecordList recordList) {
		if (record != null) {

			String value = null;
			if (fieldName.equals("lastName")) {
				value = record.getAttributeAsString("lastName");
				return value != null && validateNotEmpty(value);
			} else if (fieldName.equals("firstName")) {
				value = record.getAttributeAsString("firstName");
				return value != null && validateNotEmpty(value);
			} else if (fieldName.equals("maxWTU")) {
				value = record.getAttributeAsString("maxWTU");
				return value != null && validateGreaterThanZeroInt(value);
			} else if (fieldName.equals("username")) {
				value = record.getAttributeAsString("username");
				return value != null && validateNotEmpty(value)
						&& validateUsername(value, recordList);
			}
			// Default
			else {
				// Isn't a special column, is always valid
				return true;
			}
		}
		return true;
	}

	/**
	 * This method is used in LocationsView to highlight a cell based on if it is valid or not.
	 * @param fieldName The column name to validate
	 * @param record The location record to validate
	 * @return True if the column is valid, false otherwise
	 */
	public static boolean isValidLocationType(String fieldName,
			ListGridRecord record) {
		if (record != null) {
			String value = null;
			if (fieldName.equals("maxOccupancy")) {
				value = record.getAttributeAsString("maxOccupancy");
				return value != null && validateNonNegativeInt(value);
			} else if (fieldName.equals("room")) {
				value = record.getAttributeAsString("room");
				return value != null && validateNotEmpty(value);
			} else if (fieldName.equals("type")) {
				value = record.getAttributeAsString("type");
				return value != null && validateNotEmpty(value);
			}
			// Default
			else {
				// Isn't a special column, is always valid
				return true;
			}
		}
		return true;
	}

	/**
	 * This method is used to validate an entire collection of CourseGWTs.
	 * @param courses The entire collection of CourseGWTs to validate
	 * @return true if the collection is valid, false otherwise
	 */
	public static boolean isValidCourseCollection(Collection<CourseGWT> courses) {
		for (CourseGWT course : courses) {
			if (!isValidCourseGWT(course)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Helper method to validate a single CourseGWT
	 * @param course The CourseGWT to validate
	 * @return true if the CourseGWT is valid, false otherwise
	 */
	public static boolean isValidCourseGWT(CourseGWT course) {
		if (validateNonNegativeInt(course.getMaxEnroll())) {
			if (validateGreaterThanZeroInt(course.getRawNumSections())) {
				if (validateGreaterThanZeroInt(course.getWtu())) {
					if (validateGreaterThanZeroInt(course.getScu())) {
						if (validateGreaterThanZeroInt(course
								.getHalfHoursPerWeek())) {
							if (validateNotEmpty(course.getCatalogNum())) {
								if (course.getDayPatterns().size() > 0)
									return true;
							}
						}
					}
				}
			}
		}
		return false;
	}

	/**
	 * This method is used to validate an entire collection of InstructorGWTs.
	 * @param courses The entire collection of InstructorGWTs to validate
	 * @return true if the collection is valid, false otherwise
	 */
	public static boolean isValidInstructorCollection(
			Collection<InstructorGWT> instructors) {
		// Make sure usernames are unique
		ArrayList<String> usernames = new ArrayList<String>();
		for (InstructorGWT instructor : instructors) {
			String current = instructor.getUsername();
			if (usernames.contains(current)) {
				return false;
			} else {
				usernames.add(current);
			}
		}
		// Validate other fields
		for (InstructorGWT instructor : instructors) {
			if (!isValidInstructorGWT(instructor)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Helper method to validate a single InstructorGWT
	 * @param course The InstructorGWT to validate
	 * @return true if the InstructorGWT is valid, false otherwise
	 */
	public static boolean isValidInstructorGWT(InstructorGWT instructor) {
		if (validateNotEmpty(instructor.getLastName())) {
			if (validateNotEmpty(instructor.getFirstName())) {
				if (validateGreaterThanZeroInt(instructor.getRawMaxWtu())) {
					if (validateNotEmpty(instructor.getUsername())) {
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * This method is used to validate an entire collection of LocationGWTs.
	 * @param courses The entire collection of LocationGWTs to validate
	 * @return true if the collection is valid, false otherwise
	 */
	public static boolean isValidLocationCollection(
			Collection<LocationGWT> locations) {
		for (LocationGWT location : locations) {
			if (!isValidLocationGWT(location)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Helper method to validate a single LocationGWT
	 * @param course The LocationGWT to validate
	 * @return true if the LocationGWT is valid, false otherwise
	 */
	public static boolean isValidLocationGWT(LocationGWT location) {
		if (validateNonNegativeInt(location.getRawMaxOccupancy())) {
			if (validateNotEmpty(location.getRoom())) {
				if (validateNotEmpty(location.getType())) {
					return true;
				}
			}
		}
		return false;
	}

	//Helper methods to validate different types of fields
	/**
	 * Method to validate if a value is an integer that is >= 0
	 * @param cellvalue The value to validate
	 * @return true if the value is an integer greater than or equal to zero, false if it is less than zero
	 */
	private static boolean validateNonNegativeInt(String cellvalue) {
		try {
			int value = Integer.valueOf(cellvalue);
			if (value < 0) {
				return false;
			} else {
				return true;
			}
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * Method to validate if a value is an integer that is > 0
	 * @param cellvalue The value to validate
	 * @return true if the value is an integer greater than zero, false if it is zero or less
	 */
	private static boolean validateGreaterThanZeroInt(String cellvalue) {
		try {
			int value = Integer.valueOf(cellvalue);
			if (value <= 0) {
				return false;
			} else {
				return true;
			}
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * Method to validate if a value is a multiple of .5
	 * @param cellvalue The value to validate
	 * @return true if the value is a multiple of .5, false otherwise
	 */
	private static boolean validateMultipleOfHalf(String cellvalue) {
		try {
			float value = Float.parseFloat(cellvalue);
			float newvalue = (Math.round(value * 2)) / 2.0f;
			if (value > 0.0f && Math.abs(newvalue - value) <= 0.01) {
				return true;
			}
			return false;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * Method to validate if a value is not empty
	 * @param cellValue The value to validate
	 * @return true if the value is not empty, false if it is empty
	 */
	private static boolean validateNotEmpty(String cellValue) {
		if (cellValue.length() <= 0) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * Method to validate if a value is a valid username
	 * @param cellValue The value to validate
	 * @param recordList The entire list of records
	 * @return true if the username is unique, false if there are duplicates
	 */
	private static boolean validateUsername(String cellValue,
			RecordList recordList) {
		assert (cellValue instanceof String);
		String username = (String) cellValue;
		if (username.trim().length() == 0) {
			return false;
		}

		int count = 0;

		for (Record record : recordList.getRange(0, recordList.getLength())) {
			if (username.equals(record.getAttribute("username"))) {
				count++;
			}
			if (count > 1) {
				// It will always have its own name, invalid if there is more
				// than one instance though
				return false;
			}
		}
		return true;
	}

	/**
	 * Method to validate if a value is a valid SCU
	 * @param cellvalue The value to validate
	 * @param recordList The entire list of records that contain the other rows
	 * @return true if the SCU is between 0 and 5
	 */
	private static boolean validateSCUs(String cellvalue, RecordList recordList) {
		try {
			int value = Integer.valueOf(cellvalue);
			if (value < 0 || value > 5) {
				return false;
			} else {
				return true;
			}
		} catch (Exception e) {
			return false;
		}
	}
}
