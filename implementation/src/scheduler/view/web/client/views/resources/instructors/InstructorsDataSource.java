package scheduler.view.web.client.views.resources.instructors;

import java.util.HashMap;
import java.util.List;

import scheduler.view.web.client.GreetingServiceAsync;
import scheduler.view.web.client.UnsavedDocumentStrategy;
import scheduler.view.web.shared.DayGWT;
import scheduler.view.web.shared.DocumentGWT;
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
	
	// private static InstructorDataSource instance = null;
	//
	// public static InstructorDataSource getInstance() {
	// if (instance == null) {
	// instance = new InstructorDataSource("countryDS");
	// }
	// return instance;
	// }
	
	final GreetingServiceAsync service;
	final DocumentGWT document;
	UnsavedDocumentStrategy unsavedDocumentStrategy;
	
	public InstructorsDataSource(GreetingServiceAsync service, DocumentGWT document, UnsavedDocumentStrategy unsavedDocumentStrategy) {
		this.service = service;
		this.document = document;
		this.unsavedDocumentStrategy = unsavedDocumentStrategy;
		
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

	InstructorGWT readRecordIntoInstructor(Record record) {		
		return new InstructorGWT(
				record.getAttributeAsInt("id"),
				record.getAttribute("username"),
				record.getAttribute("firstName"),
				record.getAttribute("lastName"),
				record.getAttribute("maxWTU"),
				new int[DayGWT.values().length][48],
				new HashMap<Integer, Integer>(),
				record.getAttributeAsBoolean("isSchedulable"));
	}

	protected void fetch(final DSRequest dsRequest) {
		service.getInstructorsForDocument(document.getID(), new AsyncCallback<List<InstructorGWT>>() {
			public void onSuccess(List<InstructorGWT> result) {
				Record[] responseRecords = new Record[result.size()];
				
				int responseRecordIndex = 0;
				for (InstructorGWT instructor : result) {
					System.out.println("Fetch instructor result id " + instructor.getID());
					System.out.println("Fetch record id " + readInstructorIntoRecord(instructor).getAttribute("id"));
					responseRecords[responseRecordIndex++] = readInstructorIntoRecord(instructor);
				}
				
				DSResponse response = new DSResponse();
				response.setData(responseRecords);
				processResponse(dsRequest.getRequestId(), response);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				DSResponse dsResponse = new DSResponse();
				Window.alert("Failed to retrieve instructors!");
				processResponse(dsRequest.getRequestId(), dsResponse);
			}
		});
	}
	
	protected void add(final DSRequest dsRequest) {
		Record record = dsRequest.getAttributeAsRecord("data");
		InstructorGWT newInstructor = readRecordIntoInstructor(record);
		
		service.addInstructorToDocument(document.getID(), newInstructor, new AsyncCallback<InstructorGWT>() {
			@Override
			public void onFailure(Throwable caught) {
				DSResponse dsResponse = new DSResponse();
				Window.alert("Failed to update instructor!");
				processResponse(dsRequest.getRequestId(), dsResponse);
			}
			
			@Override
			public void onSuccess(InstructorGWT result) {
				unsavedDocumentStrategy.setDocumentChanged(true);
				DSResponse response = new DSResponse();
				System.out.println("result record id " + result.getID());
				response.setData(new Record[] { readInstructorIntoRecord(result) });
				assert(response.getData()[0].getAttributeAsInt("id") != null);
				processResponse(dsRequest.getRequestId(), response);
			}
		});
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
		
		final InstructorGWT instructor = readRecordIntoInstructor(record);
		
		service.editInstructor(instructor, new AsyncCallback<Void>() {
			@Override
			public void onFailure(Throwable caught) {
				DSResponse dsResponse = new DSResponse();
				Window.alert("Failed to update instructor!");
				processResponse(dsRequest.getRequestId(), dsResponse);
			}
			
			@Override
			public void onSuccess(Void result) {
				unsavedDocumentStrategy.setDocumentChanged(true);
				DSResponse response = new DSResponse();
				response.setData(new Record[] { readInstructorIntoRecord(instructor) });
				processResponse(dsRequest.getRequestId(), response);
			}
		});
	}
	
	protected void remove(final DSRequest dsRequest) {
		final Record record = dsRequest.getAttributeAsRecord("data");
		final InstructorGWT instructor = readRecordIntoInstructor(record);

		service.removeInstructor(record.getAttributeAsInt("id"), new AsyncCallback<Void>() {
			@Override
			public void onSuccess(Void result) {
				unsavedDocumentStrategy.setDocumentChanged(true);
				DSResponse response = new DSResponse();
				response.setData(new Record[] { readInstructorIntoRecord(instructor) });
				processResponse(dsRequest.getRequestId(), response);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				DSResponse dsResponse = new DSResponse();
				Window.alert("Failed to delete instructor!");
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
}
