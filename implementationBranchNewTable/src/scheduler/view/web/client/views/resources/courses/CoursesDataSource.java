package scheduler.view.web.client.views.resources.courses;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import scheduler.view.web.client.GreetingServiceAsync;
import scheduler.view.web.shared.CourseGWT;
import scheduler.view.web.shared.DayGWT;
import scheduler.view.web.shared.DocumentGWT;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.data.DSRequest;
import com.smartgwt.client.data.DSResponse;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.Record;
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
	
	public CoursesDataSource(GreetingServiceAsync service, DocumentGWT document, String dataSourceID) {
		this.service = service;
		this.document = document;
		
		setDataProtocol(DSProtocol.CLIENTCUSTOM);
		
		this.setAddGlobalId(false);
//		setID(dataSourceID);
		
		DataSourceIntegerField idField = new DataSourceIntegerField("id");
		idField.setHidden(true);
		idField.setPrimaryKey(true);
		
		DataSourceTextField departmentField = new DataSourceTextField("department");
		departmentField.setRequired(true);
		
		DataSourceTextField catalogNumberField = new DataSourceTextField("catalogNumber");
		
		DataSourceTextField nameField = new DataSourceTextField("name");
		
		DataSourceTextField numSectionsField = new DataSourceTextField("numSections");
		
		DataSourceTextField wtuField = new DataSourceTextField("wtu");
		
		DataSourceTextField scuField = new DataSourceTextField("scu");
		
		DataSourceTextField dayCombinationsField = new DataSourceTextField("dayCombinations");
		
		DataSourceTextField hoursPerWeekField = new DataSourceTextField("hoursPerWeek");
		
		DataSourceTextField maxEnrollmentField = new DataSourceTextField("maxEnrollment");
		
		DataSourceTextField courseTypeField = new DataSourceTextField("type");
		
		DataSourceTextField associationsField = new DataSourceTextField("associations");
		
		setFields(departmentField, catalogNumberField, nameField, numSectionsField, wtuField, scuField,
				dayCombinationsField, hoursPerWeekField, maxEnrollmentField, courseTypeField, associationsField);
		
		setClientOnly(true);
	}

	void readCourseIntoResponseRecord(CourseGWT course, Record responseRecord) {
		responseRecord.setAttribute("id", course.getID());
		responseRecord.setAttribute("department", course.getDept());
		responseRecord.setAttribute("catalogNumber", course.getCatalogNum());
		responseRecord.setAttribute("name", course.getCourseName());
		responseRecord.setAttribute("numSections", course.getNumSections());
		responseRecord.setAttribute("wtu", course.getWtu());
		responseRecord.setAttribute("scu", course.getScu());
		responseRecord.setAttribute("dayCombinations", course.getDayPatterns().toString());
		responseRecord.setAttribute("hoursPerWeek", course.getHalfHoursPerWeek());
		responseRecord.setAttribute("maxEnrollment", course.getMaxEnroll());
		responseRecord.setAttribute("type", course.getType());
		responseRecord.setAttribute("associations", "?");
	}
	
	protected void fetch(final DSRequest dsRequest) {
		service.getCoursesForDocument(document.getID(), new AsyncCallback<List<CourseGWT>>() {
			public void onSuccess(List<CourseGWT> result) {
				Record[] responseRecords = new Record[result.size()];
				
				int responseRecordIndex = 0;
				for (CourseGWT course : result) {
					Record responseRecord = new Record();
					readCourseIntoResponseRecord(course, responseRecord);
					responseRecords[responseRecordIndex] = responseRecord;
					responseRecordIndex++;
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
		CourseGWT newCourse = new CourseGWT(
				true,
				dsRequest.getAttribute("name"),
				dsRequest.getAttribute("catalogNumber"),
				dsRequest.getAttribute("department"),
				dsRequest.getAttribute("wtu"),
				dsRequest.getAttribute("scu"),
				dsRequest.getAttribute("numSections"),
				dsRequest.getAttribute("type"),
				dsRequest.getAttribute("maxEnrollment"),
				-1, // lecture ID
				dsRequest.getAttribute("halfHoursPerWeek"),
				new LinkedList<Set<DayGWT>>(), // day combinations
				-1, // id
				dsRequest.getAttributeAsBoolean("tetheredToLecture"),
				new TreeSet<String>() // equipment
				);
		
		service.addCourseToDocument(document.getID(), newCourse, new AsyncCallback<CourseGWT>() {
			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Failed to update course");
			}
			
			@Override
			public void onSuccess(CourseGWT result) {
				Record responseRecord = new Record();
				readCourseIntoResponseRecord(result, responseRecord);
				DSResponse response = new DSResponse();
				response.setData(new Record[] { responseRecord });
				processResponse(dsRequest.getRequestId(), response);
			}
		});
		
	}
	
	@Override
   protected Object transformRequest(final DSRequest dsRequest) {
//		FETCH ADD UPDATE REMOVE VALIDATE
		
		switch (dsRequest.getOperationType()) {
			case FETCH: fetch(dsRequest); break;
			case ADD: add(dsRequest); break;
//			case UPDATE: update(dsRequest); break;
//			case REMOVE: remove(dsRequest); break;
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
}
