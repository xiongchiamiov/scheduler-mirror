package scheduler.view.web.client.views.resources.instructors;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import scheduler.view.web.client.CachedOpenWorkingCopyDocument;
import scheduler.view.web.client.views.resources.ResourceCollection;
import scheduler.view.web.shared.CourseGWT;
import scheduler.view.web.shared.DayGWT;
import scheduler.view.web.shared.InstructorGWT;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.data.DSRequest;
import com.smartgwt.client.data.DSResponse;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.data.fields.DataSourceBooleanField;
import com.smartgwt.client.data.fields.DataSourceIntegerField;
import com.smartgwt.client.data.fields.DataSourceTextField;
import com.smartgwt.client.types.DSProtocol;

public class InstructorsDataSource extends DataSource {
	CachedOpenWorkingCopyDocument document;
	
	public InstructorsDataSource(CachedOpenWorkingCopyDocument document) {
		this.document = document;
		
		setDataProtocol(DSProtocol.CLIENTCUSTOM);
		
		this.setAddGlobalId(false);
//		setID(dataSourceID);
		
		DataSourceIntegerField idField = new DataSourceIntegerField("id");
		idField.setHidden(true);
//		idField.setRequired(true);
		idField.setPrimaryKey(true);

		DataSourceBooleanField schedulableField = new DataSourceBooleanField("isSchedulable");
		
		DataSourceTextField usernameField = new DataSourceTextField("username");

		DataSourceTextField firstNameField = new DataSourceTextField("firstName");

		DataSourceTextField lastNameField = new DataSourceTextField("lastName");

		DataSourceTextField maxWTUField = new DataSourceTextField("maxWTU");
		
		setFields(idField, schedulableField, usernameField, firstNameField, lastNameField, maxWTUField);
		
		setClientOnly(true);
	}

	Record readInstructorIntoRecord(InstructorGWT instructor) {
		Record record = new Record();
		record.setAttribute("id", instructor.getID());
		record.setAttribute("username", instructor.getUsername());
		record.setAttribute("firstName", instructor.getFirstName());
		record.setAttribute("lastName", instructor.getLastName());
		record.setAttribute("maxWTU", instructor.getMaxWtu());
		record.setAttribute("isSchedulable", instructor.isSchedulable());
		return record;
	}

	private static String emptyStringIfNull(String str) {
		if (str == null)
			return "";
		return str;
	}
	
	InstructorGWT readRecordIntoInstructor(Record record) {		
		return new InstructorGWT(
				record.getAttributeAsInt("id"),
				emptyStringIfNull(record.getAttribute("username")),
				emptyStringIfNull(record.getAttribute("firstName")),
				emptyStringIfNull(record.getAttribute("lastName")),
				emptyStringIfNull(record.getAttribute("maxWTU")),
				new int[DayGWT.values().length][48],
				new HashMap<Integer, Integer>(),
				record.getAttribute("isSchedulable").equals("true"));
	}

	protected void fetch(final DSRequest dsRequest) {
		Collection<InstructorGWT> resources = document.getInstructors(true);
		
		Record[] responseRecords = new Record[resources.size()];
		int responseRecordIndex = 0;
		for (InstructorGWT resource : resources)
			responseRecords[responseRecordIndex++] = readInstructorIntoRecord(resource);
		
		DSResponse response = new DSResponse();
		response.setData(responseRecords);
		processResponse(dsRequest.getRequestId(), response);
	}

	protected void add(final DSRequest dsRequest) {
		Record record = dsRequest.getAttributeAsRecord("data");
		InstructorGWT newInstructor = readRecordIntoInstructor(record);
		
		document.addInstructor(newInstructor);
		assert(newInstructor.getID() != null);
		
		DSResponse response = new DSResponse();
		response.setData(new Record[] { readInstructorIntoRecord(newInstructor) });
		assert(response.getData()[0].getAttributeAsInt("id") != null);
		processResponse(dsRequest.getRequestId(), response);
	}
	
	protected void update(final DSRequest dsRequest) {
		Record record = dsRequest.getOldValues();
		
		Record changes = dsRequest.getAttributeAsRecord("data");
		
		assert(changes.getAttributeAsInt("id") == record.getAttributeAsInt("id"));
		if (changes.getAttribute("username") != null)
			record.setAttribute("username", changes.getAttribute("username"));
		if (changes.getAttribute("firstName") != null)
			record.setAttribute("firstName", changes.getAttribute("firstName"));
		if (changes.getAttribute("lastName") != null)
			record.setAttribute("lastName", changes.getAttribute("lastName"));
		if (changes.getAttribute("maxWTU") != null)
			record.setAttribute("maxWTU", changes.getAttribute("maxWTU"));
		if (changes.getAttribute("isSchedulable") != null)
			record.setAttribute("isSchedulable", changes.getAttribute("isSchedulable"));
		
		final InstructorGWT instructor = readRecordIntoInstructor(record);
		
		DSResponse response = new DSResponse();
		response.setData(new Record[] { readInstructorIntoRecord(instructor) });
		processResponse(dsRequest.getRequestId(), response);
	}
	
	protected void remove(final DSRequest dsRequest) {
		final Record record = dsRequest.getAttributeAsRecord("data");
		final InstructorGWT course = readRecordIntoInstructor(record);

		document.deleteInstructor(record.getAttributeAsInt("id"));
		
		DSResponse response = new DSResponse();
		response.setData(new Record[] { readInstructorIntoRecord(course) });
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
