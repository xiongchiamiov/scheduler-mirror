package scheduler.view.web.client.views.resources.courses;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import scheduler.view.web.client.GreetingServiceAsync;
import scheduler.view.web.client.UnsavedDocumentStrategy;
import scheduler.view.web.shared.CourseGWT;
import scheduler.view.web.shared.DayGWT;
import scheduler.view.web.shared.DocumentGWT;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
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
	
	// private static CourseDataSource instance = null;
	//
	// public static CourseDataSource getInstance() {
	// if (instance == null) {
	// instance = new CourseDataSource("countryDS");
	// }
	// return instance;
	// }
	
	final GreetingServiceAsync service;
	final DocumentGWT document;
	UnsavedDocumentStrategy unsavedDocumentStrategy;
	GetAllRecordsStrategy getAllRecordsStrategy;
	
	public CoursesDataSource(GreetingServiceAsync service, DocumentGWT document, UnsavedDocumentStrategy unsavedDocumentStrategy, GetAllRecordsStrategy getAllRecordsStrategy) {
		this.service = service;
		this.document = document;
		this.unsavedDocumentStrategy = unsavedDocumentStrategy;
		this.getAllRecordsStrategy = getAllRecordsStrategy;
		
		setDataProtocol(DSProtocol.CLIENTCUSTOM);
		
		this.setAddGlobalId(false);
//		setID(dataSourceID);
		
		DataSourceIntegerField idField = new DataSourceIntegerField("id");
		idField.setHidden(true);
//		idField.setRequired(true);
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
		courseTypeField.setValueMap("LEC", "LAB", "ACT", "DIS", "SEM");

		DataSourceEnumField usedEquipmentField = new DataSourceEnumField("usedEquipment");
		usedEquipmentField.setMultiple(true);
		usedEquipmentField.setValueMap("Projector", "Computers");
		
//		DataSourceEnumField associationsField = new DataSourceEnumField("associations");
//		associationsField.setValueMap("?");
//		associationsField.setMultiple(true);
		
		DataSourceTextField associationsField = new DataSourceTextField("associations");
		
		setFields(idField, schedulableField, departmentField, catalogNumberField, nameField, numSectionsField, wtuField, scuField,
				dayCombinationsField, hoursPerWeekField, maxEnrollmentField, courseTypeField, usedEquipmentField, associationsField);
		
		setClientOnly(true);
	}

	String dayCombinationToString(Set<DayGWT> dayCombination) {
		String result = "";
		if (dayCombination.contains(DayGWT.MONDAY))
			result += "M";
		if (dayCombination.contains(DayGWT.TUESDAY))
			result += "Tu";
		if (dayCombination.contains(DayGWT.WEDNESDAY))
			result += "W";
		if (dayCombination.contains(DayGWT.THURSDAY))
			result += "Th";
		if (dayCombination.contains(DayGWT.FRIDAY))
			result += "F";
		if (dayCombination.contains(DayGWT.SATURDAY))
			result += "Sa";
		if (dayCombination.contains(DayGWT.SUNDAY))
			result += "Su";
		assert(result.length() > 0);
		return result;
	}
	
	Record readCourseIntoRecord(CourseGWT course) {
		String[] dayCombinationsStrings = new String[course.getDayPatterns().size()];
		int dayCombinationIndex = 0;
		for (Set<DayGWT> dayCombination : course.getDayPatterns())
			dayCombinationsStrings[dayCombinationIndex++] = dayCombinationToString(dayCombination);
		
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
//		record.setAttribute("associations", "?");
		if(course.getLectureID() != -1)
		{
			record.setAttribute("associations", "LecID: " + course.getLectureID() + " Tethered? " + course.getTetheredToLecture());
		}
		return record;
	}

	Set<DayGWT> dayCombinationFromString(String string) {
		Set<DayGWT> result = new TreeSet<DayGWT>();
		if (string.contains("M"))
			result.add(DayGWT.MONDAY);
		if (string.contains("Tu")) // A 'T', as long as its not followed by an h
			result.add(DayGWT.TUESDAY);
		if (string.contains("W"))
			result.add(DayGWT.WEDNESDAY);
		if (string.contains("Th"))
			result.add(DayGWT.THURSDAY);
		if (string.contains("F"))
			result.add(DayGWT.FRIDAY);
		if (string.contains("Sa"))
			result.add(DayGWT.SATURDAY);
		if (string.contains("Su"))
			result.add(DayGWT.SUNDAY);
		assert(result.size() > 0);
		return result;
	}
	
	CourseGWT readRecordIntoCourse(Record record) {
		System.out.println("new record id " + record.getAttribute("id"));

		String dayCombinationsStringsCombined = record.getAttributeAsString("dayCombinations");
		Collection<Set<DayGWT>> dayCombinations = new LinkedList<Set<DayGWT>>();
		if (dayCombinationsStringsCombined != null && dayCombinationsStringsCombined.length() > 0) {
			for (String dayCombinationString : dayCombinationsStringsCombined.split(","))
				dayCombinations.add(dayCombinationFromString(dayCombinationString));
		}
		
		
		String usedEquipmentsCombined = record.getAttributeAsString("usedEquipment");
		Set<String> usedEquipments = new TreeSet<String>();
		if (usedEquipmentsCombined != null && usedEquipmentsCombined.length() > 0) {
			for (String usedEquipment : usedEquipmentsCombined.split(","))
				usedEquipments.add(usedEquipment);
		}
		
		String associations = record.getAttributeAsString("associations");
		System.out.println("Association: " + associations);
		int lectureID = -1;
		boolean isTethered = false;
		System.out.println("Type before assoc: " + record.getAttributeAsString("type"));
		if(record.getAttributeAsString("type") != null && record.getAttributeAsString("type").equals("LAB"))
		{
			System.out.println("Type is Lab, associating things");
			if(associations != null && associations.length() > 0)
			{
				//TODO: validate this next part
				String[] split = associations.split(" ");
				String dept = "";
				String catalogNum = "";
				String tethered = "";
				if(split.length > 0)
				{
					dept = split[0];
					if(split.length > 1)
					{
						catalogNum = split[1];
						if(split.length > 2)
						{
							tethered = split[2];
						}
					}
				}
				System.out.println("DEPT: " + dept);
				System.out.println("CATALOGNUM: " + catalogNum);
				System.out.println("TETHERED: " + tethered);
				Record[] records = getAllRecordsStrategy.getAllRecords();
				for(Record r : records)
				{
					System.out.println("Got a record");
					if(r.getAttributeAsString("type").equals("LEC"))
					{
						System.out.println("Is a lecture");
						if(r.getAttributeAsString("department").equals(dept))
						{
							System.out.println("Department match, cat num: " + r.getAttributeAsString("catalogNumber"));
							if(r.getAttributeAsString("catalogNumber").equals(catalogNum))
							{
								//Match
								System.out.println("Found a lecture match");
								lectureID = r.getAttributeAsInt("id");
							}
						}
					}
				}
				isTethered = tethered.equals("(tethered)");
			}
		}
		System.out.println("Tethered is " + isTethered);
		
		
		assert(record.getAttribute("type") != null);
		
		CourseGWT course = new CourseGWT(
				record.getAttributeAsBoolean("isSchedulable"),
				record.getAttribute("name"),
				record.getAttribute("catalogNumber"),
				record.getAttribute("department"),
				record.getAttribute("wtu"),
				record.getAttribute("scu"),
				record.getAttribute("numSections"),
				record.getAttribute("type"),
				record.getAttribute("maxEnrollment"),
				lectureID, // lecture ID
				record.getAttribute("hoursPerWeek"),
				dayCombinations, // day combinations
				record.getAttributeAsInt("id"), // id
				isTethered,
				usedEquipments // equipment
				);
		
		return course;
	}

	protected void fetch(final DSRequest dsRequest) {
		service.getCoursesForDocument(document.getID(), new AsyncCallback<List<CourseGWT>>() {
			public void onSuccess(List<CourseGWT> result) {
				Record[] responseRecords = new Record[result.size()];
				
				int responseRecordIndex = 0;
				for (CourseGWT course : result) {
					System.out.println("Fetch course result id " + course.getID());
					System.out.println("Fetch record id " + readCourseIntoRecord(course).getAttribute("id"));
					responseRecords[responseRecordIndex++] = readCourseIntoRecord(course);
				}
				
				DSResponse response = new DSResponse();
				response.setData(responseRecords);
				processResponse(dsRequest.getRequestId(), response);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				DSResponse dsResponse = new DSResponse();
				Window.alert("Failed to retrieve courses!");
				processResponse(dsRequest.getRequestId(), dsResponse);
			}
		});
	}
	
	protected void add(final DSRequest dsRequest) {
		Record record = dsRequest.getAttributeAsRecord("data");
		CourseGWT newCourse = readRecordIntoCourse(record);
		
		System.out.println("new course id " + newCourse.getID());
		
		service.addCourseToDocument(document.getID(), newCourse, new AsyncCallback<CourseGWT>() {
			@Override
			public void onFailure(Throwable caught) {
				DSResponse dsResponse = new DSResponse();
				Window.alert("Failed to update course!");
				processResponse(dsRequest.getRequestId(), dsResponse);
			}
			
			@Override
			public void onSuccess(CourseGWT result) {
				unsavedDocumentStrategy.setDocumentChanged(true);
				DSResponse response = new DSResponse();
				System.out.println("result record id " + result.getID());
				response.setData(new Record[] { readCourseIntoRecord(result) });
				assert(response.getData()[0].getAttributeAsInt("id") != null);
				processResponse(dsRequest.getRequestId(), response);
			}
		});
	}
	
	protected void update(final DSRequest dsRequest) {
		Record record = dsRequest.getOldValues();
		
		Record changes = dsRequest.getAttributeAsRecord("data");
		
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
		if (changes.getAttributeAsBoolean("isSchedulable") != null)
			record.setAttribute("isSchedulable", changes.getAttributeAsBoolean("isSchedulable"));
		
		final CourseGWT course = readRecordIntoCourse(record);
		
		System.out.println("updating course id " + course.getID());
		
		service.editCourse(course, new AsyncCallback<Void>() {
			@Override
			public void onFailure(Throwable caught) {
				DSResponse dsResponse = new DSResponse();
				Window.alert("Failed to update course!");
				processResponse(dsRequest.getRequestId(), dsResponse);
			}
			
			@Override
			public void onSuccess(Void result) {
				unsavedDocumentStrategy.setDocumentChanged(true);
				DSResponse response = new DSResponse();
				response.setData(new Record[] { readCourseIntoRecord(course) });
				processResponse(dsRequest.getRequestId(), response);
			}
		});
	}
	
	protected void remove(final DSRequest dsRequest) {
		final Record record = dsRequest.getAttributeAsRecord("data");
		final CourseGWT course = readRecordIntoCourse(record);

		service.removeCourse(record.getAttributeAsInt("id"), new AsyncCallback<Void>() {
			@Override
			public void onSuccess(Void result) {
				unsavedDocumentStrategy.setDocumentChanged(true);
				DSResponse response = new DSResponse();
				response.setData(new Record[] { readCourseIntoRecord(course) });
				processResponse(dsRequest.getRequestId(), response);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				DSResponse dsResponse = new DSResponse();
				Window.alert("Failed to delete course!");
				processResponse(dsRequest.getRequestId(), dsResponse);
			}
		});
	}
	
	@Override
   protected Object transformRequest(final DSRequest dsRequest) {
//		FETCH ADD UPDATE REMOVE VALIDATE
		
		switch (dsRequest.getOperationType()) {
			case FETCH: fetch(dsRequest); break;
			case ADD: add(dsRequest); break;
			case UPDATE: update(dsRequest); break;
			case REMOVE: remove(dsRequest); break;
		}
		
      return dsRequest;
  }
//		
//		setID(id);
//		setRecordXPath("/List/country");
//		DataSourceIntegerField pkField = new DataSourceIntegerField("pk");
//		pkField.setHidden(true);
//		pkField.setPrimaryKey(true);
//		
//		DataSourceTextField countryCodeField = new DataSourceTextField("countryCode", "Code");
//		countryCodeField.setRequired(true);
//		
//		DataSourceTextField countryNameField = new DataSourceTextField("countryName", "Country");
//		countryNameField.setRequired(true);
//		
//		DataSourceTextField capitalField = new DataSourceTextField("capital", "Capital");
//		DataSourceTextField governmentField = new DataSourceTextField("government", "Government", 500);
//		
//		DataSourceBooleanField memberG8Field = new DataSourceBooleanField("member_g8", "G8");
//		
//		DataSourceTextField continentField = new DataSourceTextField("continent", "Continent");
//		continentField.setValueMap("Europe", "Asia", "North America", "Australia/Oceania", "South America", "Africa");
//		
//		DataSourceDateField independenceField = new DataSourceDateField("independence", "Nationhood");
//		DataSourceFloatField areaField = new DataSourceFloatField("area", "Area (km)");
//		DataSourceIntegerField populationField = new DataSourceIntegerField("population", "Population");
//		DataSourceFloatField gdpField = new DataSourceFloatField("gdp", "GDP ($M)");
//		DataSourceLinkField articleField = new DataSourceLinkField("article", "Info");
//		
//		setFields(pkField, countryCodeField, countryNameField, capitalField, governmentField,
//				memberG8Field, continentField, independenceField, areaField, populationField,
//				gdpField, articleField);
		
//		setDataURL("ds/test_data/country.data.xml");
//		setClientOnly(true);
	
	
	
	public interface GetAllRecordsStrategy
	{
		public Record[] getAllRecords();
	}
}
