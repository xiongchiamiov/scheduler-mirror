package scheduler.view.web.client.views.resources.courses;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Set;
import java.util.TreeSet;

import scheduler.view.web.client.views.resources.ResourceCollection;
import scheduler.view.web.shared.CourseGWT;
import scheduler.view.web.shared.DayGWT;

import com.smartgwt.client.data.DSRequest;
import com.smartgwt.client.data.DSResponse;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.data.fields.DataSourceBooleanField;
import com.smartgwt.client.data.fields.DataSourceEnumField;
import com.smartgwt.client.data.fields.DataSourceIntegerField;
import com.smartgwt.client.data.fields.DataSourceTextField;
import com.smartgwt.client.types.DSProtocol;

public class CoursesDataSource extends DataSource {
	ResourceCollection<CourseGWT> coursesSource;
	
	public CoursesDataSource(ResourceCollection<CourseGWT> coursesSource) {
		this.coursesSource = coursesSource;
		
		setDataProtocol(DSProtocol.CLIENTCUSTOM);
		
		this.setAddGlobalId(false);
		
		DataSourceIntegerField idField = new DataSourceIntegerField("id");
		idField.setHidden(true);
		idField.setPrimaryKey(true);
		
		DataSourceBooleanField schedulableField = new DataSourceBooleanField("isSchedulable");
		
		DataSourceTextField departmentField = new DataSourceTextField("department");
		
		DataSourceTextField catalogNumberField = new DataSourceTextField("catalogNumber");
		
		DataSourceTextField nameField = new DataSourceTextField("name");
		
		DataSourceTextField numSectionsField = new DataSourceTextField("numSections");
		
		DataSourceTextField wtuField = new DataSourceTextField("wtu");
		
		DataSourceTextField scuField = new DataSourceTextField("scu");
		
		DataSourceEnumField dayCombinationsField = new DataSourceEnumField("dayCombinations");
		dayCombinationsField.setMultiple(true);
		dayCombinationsField.setValueMap("M", "Tu", "W", "Th", "F", "MW", "MF", "WF", "TuTh", "MWF", "TuWThF", "MWThF", "MTuThF", "MTuWTh", "TuThF", "Sa", "Su");
		
		DataSourceTextField hoursPerWeekField = new DataSourceTextField("hoursPerWeek");
		
		DataSourceTextField maxEnrollmentField = new DataSourceTextField("maxEnrollment");
		
		DataSourceEnumField courseTypeField = new DataSourceEnumField("type");
		courseTypeField.setValueMap("LEC", "LAB", "ACT", "DIS", "SEM", "IND");

		DataSourceEnumField usedEquipmentField = new DataSourceEnumField("usedEquipment");
		usedEquipmentField.setMultiple(true);
		usedEquipmentField.setValueMap("Laptop Connectivity", "Overhead", "Smart Room");
		
		DataSourceIntegerField lectureIDField = new DataSourceIntegerField("lectureID");
		
		DataSourceBooleanField isTetheredField = new DataSourceBooleanField("isTethered");
		
		setFields(idField, schedulableField, departmentField, catalogNumberField, nameField, numSectionsField, wtuField, scuField,
				dayCombinationsField, hoursPerWeekField, maxEnrollmentField, courseTypeField, usedEquipmentField, lectureIDField, isTetheredField);
		
		setClientOnly(true);
	}

	Record readCourseIntoRecord(CourseGWT course) {
		String[] dayCombinationsStrings = new String[course.getDayPatterns().size()];
		int dayCombinationIndex = 0;
		for (Set<DayGWT> dayCombination : course.getDayPatterns())
			dayCombinationsStrings[dayCombinationIndex++] = CourseGWT.dayCombinationToString(dayCombination);
		
		String[] usedEquipmentsStrings = course.getUsedEquipment().toArray(new String[0]);
		
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
		record.setAttribute("hoursPerWeek", course.getHalfHoursPerWeek());
		record.setAttribute("maxEnrollment", course.getMaxEnroll());
		record.setAttribute("type", course.getType());
		record.setAttribute("usedEquipment", usedEquipmentsStrings);
		record.setAttribute("lectureID", course.getLectureID());
		record.setAttribute("isTethered", course.getTetheredToLecture());
		return record;
	}

	private static String emptyStringIfNull(String str) {
		if (str == null)
			return "";
		return str;
	}
	
	CourseGWT readRecordIntoCourse(Record record) {

		String dayCombinationsStringsCombined = record.getAttributeAsString("dayCombinations");
		Collection<Set<DayGWT>> dayCombinations = new LinkedList<Set<DayGWT>>();
		if (dayCombinationsStringsCombined != null && dayCombinationsStringsCombined.length() > 0) {
			for (String dayCombinationString : dayCombinationsStringsCombined.split(","))
				dayCombinations.add(CourseGWT.dayCombinationFromString(dayCombinationString));
		}
		
		
		String usedEquipmentsCombined = record.getAttributeAsString("usedEquipment");
		Set<String> usedEquipments = new TreeSet<String>();
		if (usedEquipmentsCombined != null && usedEquipmentsCombined.length() > 0) {
			for (String usedEquipment : usedEquipmentsCombined.split(","))
				usedEquipments.add(usedEquipment);
		}
		
		assert(record.getAttribute("type") != null);
		
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
				Integer.parseInt(record.getAttribute("lectureID")), // lecture ID
				emptyStringIfNull(record.getAttribute("hoursPerWeek")),
				dayCombinations, // day combinations
				record.getAttributeAsInt("id"), // id
				"true".equals(record.getAttribute("isTethered")),
				usedEquipments // equipment
				);
		
		return course;
	}
	

	protected void fetch(final DSRequest dsRequest) {
		Collection<CourseGWT> courses = coursesSource.getAll();
		
		Record[] responseRecords = new Record[courses.size()];
		
		int responseRecordIndex = 0;
		for (CourseGWT course : courses) {
//			System.out.println("Fetch course result id " + course.getID());
//			System.out.println("Fetch record id " + readCourseIntoRecord(course).getAttribute("id"));
			responseRecords[responseRecordIndex++] = readCourseIntoRecord(course);
		}
		
		DSResponse response = new DSResponse();
		response.setData(responseRecords);
		processResponse(dsRequest.getRequestId(), response);
	}
	
	protected void add(final DSRequest dsRequest) {
		Record record = dsRequest.getAttributeAsRecord("data");
		CourseGWT newCourse = readRecordIntoCourse(record);
		
		coursesSource.add(newCourse);
		assert(newCourse.getID() != null);
		
		DSResponse response = new DSResponse();
		response.setData(new Record[] { readCourseIntoRecord(newCourse) });
		assert(response.getData()[0].getAttributeAsInt("id") != null);
		processResponse(dsRequest.getRequestId(), response);
	}
	
	protected void update(final DSRequest dsRequest) {
		Record record = dsRequest.getOldValues();
		
		Record changes = dsRequest.getAttributeAsRecord("data");

		assert(changes.getAttributeAsInt("id") != null);
		assert(record.getAttributeAsInt("id") != null);
		assert(changes.getAttributeAsInt("id") == record.getAttributeAsInt("id"));
		if (changes.getAttribute("department") != null)
			record.setAttribute("department", changes.getAttribute("department"));
		if (changes.getAttribute("catalogNumber") != null)
			record.setAttribute("catalogNumber", changes.getAttribute("catalogNumber"));
		if (changes.getAttribute("name") != null)
			record.setAttribute("name", changes.getAttribute("name"));
		if (changes.getAttribute("numSections") != null)
			record.setAttribute("numSections", changes.getAttribute("numSections"));
		if (changes.getAttribute("wtu") != null)
			record.setAttribute("wtu", changes.getAttribute("wtu"));
		if (changes.getAttribute("scu") != null)
			record.setAttribute("scu", changes.getAttribute("scu"));
		if (changes.getAttribute("dayCombinations") != null)
			record.setAttribute("dayCombinations", changes.getAttribute("dayCombinations"));
		if (changes.getAttribute("hoursPerWeek") != null)
			record.setAttribute("hoursPerWeek", changes.getAttribute("hoursPerWeek"));
		if (changes.getAttribute("maxEnrollment") != null)
			record.setAttribute("maxEnrollment", changes.getAttribute("maxEnrollment"));
		if (changes.getAttribute("type") != null)
			record.setAttribute("type", changes.getAttribute("type"));
		if (changes.getAttribute("usedEquipment") != null)
			record.setAttribute("usedEquipment", changes.getAttribute("usedEquipment"));
		if (changes.getAttribute("associations") != null)
			record.setAttribute("associations", changes.getAttribute("associations"));
		if (changes.getAttribute("isSchedulable") != null)
			record.setAttribute("isSchedulable", changes.getAttribute("isSchedulable"));
		if (changes.getAttribute("isTethered") != null)
			record.setAttribute("isTethered", changes.getAttribute("isTethered"));
		if (changes.getAttribute("lectureID") != null)
			record.setAttribute("lectureID", changes.getAttribute("lectureID"));
		
		
		final CourseGWT course = readRecordIntoCourse(record);
		
		assert(course.getID() != null);
		
//		System.out.println("updating course id " + course.getID() + ": " + course.getDept() + " " + course.getCatalogNum());
		
		coursesSource.edit(course);
		
		DSResponse response = new DSResponse();
		response.setData(new Record[] { readCourseIntoRecord(course) });
		processResponse(dsRequest.getRequestId(), response);
	}
	
	protected void remove(final DSRequest dsRequest) {
		final Record record = dsRequest.getAttributeAsRecord("data");
		final CourseGWT course = readRecordIntoCourse(record);

		coursesSource.delete(record.getAttributeAsInt("id"));
		
		DSResponse response = new DSResponse();
		response.setData(new Record[] { readCourseIntoRecord(course) });
		processResponse(dsRequest.getRequestId(), response);
	}
	
	@Override
   protected Object transformRequest(final DSRequest dsRequest) {
		switch (dsRequest.getOperationType()) {
			case FETCH: fetch(dsRequest); break;
			case ADD: add(dsRequest); break;
			case UPDATE: update(dsRequest); break;
			case REMOVE: remove(dsRequest); break;
		}
		
      return dsRequest;
  }
}
