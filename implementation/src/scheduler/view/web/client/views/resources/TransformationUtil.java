package scheduler.view.web.client.views.resources;

import java.util.Set;
import java.util.TreeSet;

import scheduler.view.web.shared.CourseGWT;
import scheduler.view.web.shared.InstructorGWT;
import scheduler.view.web.shared.LocationGWT;
import scheduler.view.web.shared.WeekGWT;

import com.smartgwt.client.data.Record;

public class TransformationUtil {
	public static Record readCourseIntoRecord(CourseGWT course) {
		String[] dayCombinationsStrings = new String[course.getDayPatterns()
				.size()];
		int dayCombinationIndex = 0;
		for (WeekGWT dayCombination : course.getDayPatterns())
			dayCombinationsStrings[dayCombinationIndex++] = dayCombination
					.toString();

		String[] usedEquipmentsStrings = course.getUsedEquipment().toArray(
				new String[0]);

		float hoursPerWeek = Float.parseFloat(course.getHalfHoursPerWeek()) / 2.0f;

		Record record = new Record();
		record.setAttribute("id", course.getID());
		record.setAttribute("isSchedulable", course.isSchedulable());
		record.setAttribute("department", course.getDept());
		record.setAttribute("catalogNumber", course.getCatalogNum());
		record.setAttribute("name", course.getCourseName());
		record.setAttribute("numSections", course.getNumSections());
		record.setAttribute("wtu", course.getWtu());
		record.setAttribute("scu", course.getScu());
		record.setAttribute("dayCombinations", dayCombinationsStrings);
		record.setAttribute("hoursPerWeek", hoursPerWeek);
		record.setAttribute("maxEnrollment", course.getMaxEnroll());
		record.setAttribute("type", course.getType());
		record.setAttribute("usedEquipment", usedEquipmentsStrings);
		record.setAttribute("lectureID", course.getLectureID());
		record.setAttribute("isTethered", course.getTetheredToLecture());
		return record;
	}

	public static CourseGWT readRecordIntoCourse(Record record) {

		String dayCombinationsStringsCombined = record
				.getAttributeAsString("dayCombinations");
		Set<WeekGWT> dayCombinations = new TreeSet<WeekGWT>();
		if (dayCombinationsStringsCombined != null
				&& dayCombinationsStringsCombined.length() > 0) {
			for (String dayCombinationString : dayCombinationsStringsCombined
					.split(","))
				dayCombinations.add(WeekGWT.parse(dayCombinationString));
		}

		String usedEquipmentsCombined = record
				.getAttributeAsString("usedEquipment");
		Set<String> usedEquipments = new TreeSet<String>();
		if (usedEquipmentsCombined != null
				&& usedEquipmentsCombined.length() > 0) {
			for (String usedEquipment : usedEquipmentsCombined.split(","))
				usedEquipments.add(usedEquipment);
		}

		assert (record.getAttribute("type") != null);

		Integer numHalfHoursPerWeek = 0;
		try {
			if (record.getAttribute("hoursPerWeek") != null)
				numHalfHoursPerWeek = Math.round(Float.parseFloat(record
						.getAttribute("hoursPerWeek")) * 2);
		} catch (NumberFormatException e) {
			throw e;
		}

		CourseGWT course = new CourseGWT(
				record.getAttribute("isSchedulable").equals("true"),
				emptyStringIfNull(record.getAttribute("name")),
				emptyStringIfNull(record.getAttribute("catalogNumber")),
				emptyStringIfNull(record.getAttribute("department")),
				emptyStringIfNull(record.getAttribute("wtu")),
				emptyStringIfNull(record.getAttribute("scu")),
				emptyStringIfNull(record.getAttribute("numSections")),
				emptyStringIfNull(record.getAttribute("type")),
				emptyStringIfNull(record.getAttribute("maxEnrollment")),
				Integer.parseInt(record.getAttribute("lectureID")), // lecture
																	// ID
				numHalfHoursPerWeek.toString(),
				dayCombinations, // day combinations
				record.getAttributeAsInt("id"), // id
				"true".equals(record.getAttribute("isTethered")),
				usedEquipments // equipment
		);

		return course;
	}

	public static Record readInstructorIntoRecord(InstructorGWT instructor) {
		Record record = new Record();
		record.setAttribute("id", instructor.getID());
		record.setAttribute("username", instructor.getUsername());
		record.setAttribute("firstName", instructor.getFirstName());
		record.setAttribute("lastName", instructor.getLastName());
		record.setAttribute("maxWTU", instructor.getRawMaxWtu());
		record.setAttribute("isSchedulable", instructor.isSchedulable());
		return record;
	}

	public static InstructorGWT readRecordIntoInstructor(Record record) {
		InstructorGWT instructor = new InstructorGWT();
		instructor.setID(record.getAttributeAsInt("id"));
		instructor.setUsername(emptyStringIfNull(record
				.getAttribute("username")));
		instructor.setFirstName(emptyStringIfNull(record
				.getAttribute("firstName")));
		instructor.setLastName(emptyStringIfNull(record
				.getAttribute("lastName")));
		instructor.setMaxWtu(emptyStringIfNull(record.getAttribute("maxWTU")));
		instructor.setIsSchedulable(record.getAttribute("isSchedulable")
				.equals("true"));
		return instructor;
	}
	
	public static Record readLocationIntoRecord(LocationGWT location) {
		String[] equipmentsStrings = location.getEquipment().toArray(new String[0]);
		
		Record record = new Record();
		record.setAttribute("id", location.getID());
		record.setAttribute("room", location.getRoom());
		record.setAttribute("type", location.getType());
		record.setAttribute("maxOccupancy", location.getRawMaxOccupancy());
		record.setAttribute("equipment", equipmentsStrings);
		record.setAttribute("isSchedulable", location.isSchedulable());
		return record;
	}
	
	public static LocationGWT readRecordIntoLocation(Record record) {
		String equipmentsCombined = record.getAttributeAsString("equipment");
		Set<String> equipments = new TreeSet<String>();
		if (equipmentsCombined != null && equipmentsCombined.length() > 0) {
			for (String usedEquipment : equipmentsCombined.split(","))
				equipments.add(usedEquipment);
		}
		
		return new LocationGWT(
				record.getAttributeAsInt("id"),
				emptyStringIfNull(record.getAttribute("room")),
				emptyStringIfNull(record.getAttribute("type")),
				emptyStringIfNull(record.getAttribute("maxOccupancy")),
				equipments,
				record.getAttribute("isSchedulable").equals("true"));
	}

	private static String emptyStringIfNull(String str) {
		if (str == null)
			return "";
		return str;
	}
}
