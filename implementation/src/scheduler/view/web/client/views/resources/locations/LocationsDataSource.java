package scheduler.view.web.client.views.resources.locations;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import scheduler.view.web.client.GreetingServiceAsync;
import scheduler.view.web.client.UnsavedDocumentStrategy;
import scheduler.view.web.shared.DocumentGWT;
import scheduler.view.web.shared.LocationGWT;

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

public class LocationsDataSource extends DataSource {
	
	// private static LocationDataSource instance = null;
	//
	// public static LocationDataSource getInstance() {
	// if (instance == null) {
	// instance = new LocationDataSource("countryDS");
	// }
	// return instance;
	// }
	
	final GreetingServiceAsync service;
	final DocumentGWT document;
	UnsavedDocumentStrategy unsavedDocumentStrategy;
	
	public LocationsDataSource(GreetingServiceAsync service, DocumentGWT document, UnsavedDocumentStrategy unsavedDocumentStrategy) {
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
		
		DataSourceTextField roomField = new DataSourceTextField("username");

		DataSourceTextField typeField = new DataSourceTextField("firstName");

		DataSourceTextField maxOccupancyField = new DataSourceTextField("lastName");

		DataSourceEnumField equipmentField = new DataSourceEnumField("dayCombinations");
		equipmentField.setMultiple(true);
		equipmentField.setValueMap("Projector", "Overhead");

		setFields(idField, schedulableField, roomField, typeField, maxOccupancyField, equipmentField);
		
		setClientOnly(true);
	}

	Record readLocationIntoRecord(LocationGWT location) {
		String[] equipmentsStrings = location.getEquipment().toArray(new String[0]);
		
		Record record = new Record();
		record.setAttribute("id", location.getID());
		record.setAttribute("room", location.getRoom());
		record.setAttribute("type", location.getType());
		record.setAttribute("maxOccupancy", location.getMaxOccupancy());
		record.setAttribute("equipment", equipmentsStrings);
		record.setAttribute("isSchedulable", location.isSchedulable());
		return record;
	}

	LocationGWT readRecordIntoLocation(Record record) {
		String equipmentsCombined = record.getAttributeAsString("equipment");
		Set<String> equipments = new TreeSet<String>();
		if (equipmentsCombined != null && equipmentsCombined.length() > 0) {
			for (String usedEquipment : equipmentsCombined.split(","))
				equipments.add(usedEquipment);
		}
		
		return new LocationGWT(
				record.getAttributeAsInt("id"),
				record.getAttribute("room"),
				record.getAttribute("type"),
				record.getAttribute("maxOccupancy"),
				equipments,
				record.getAttribute("isSchedulable").equals("true"));
	}

	protected void fetch(final DSRequest dsRequest) {
		service.getLocationsForDocument(document.getID(), new AsyncCallback<List<LocationGWT>>() {
			public void onSuccess(List<LocationGWT> result) {
				Record[] responseRecords = new Record[result.size()];
				
				int responseRecordIndex = 0;
				for (LocationGWT location : result) {
					System.out.println("Fetch location result id " + location.getID());
					System.out.println("Fetch record id " + readLocationIntoRecord(location).getAttribute("id"));
					responseRecords[responseRecordIndex++] = readLocationIntoRecord(location);
				}
				
				DSResponse response = new DSResponse();
				response.setData(responseRecords);
				processResponse(dsRequest.getRequestId(), response);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				DSResponse dsResponse = new DSResponse();
				Window.alert("Failed to retrieve locations!");
				processResponse(dsRequest.getRequestId(), dsResponse);
			}
		});
	}
	
	protected void add(final DSRequest dsRequest) {
		Record record = dsRequest.getAttributeAsRecord("data");
		LocationGWT newLocation = readRecordIntoLocation(record);
		
		service.addLocationToDocument(document.getID(), newLocation, new AsyncCallback<LocationGWT>() {
			@Override
			public void onFailure(Throwable caught) {
				DSResponse dsResponse = new DSResponse();
				Window.alert("Failed to update location!");
				processResponse(dsRequest.getRequestId(), dsResponse);
			}
			
			@Override
			public void onSuccess(LocationGWT result) {
				unsavedDocumentStrategy.setDocumentChanged(true);
				DSResponse response = new DSResponse();
				System.out.println("result record id " + result.getID());
				response.setData(new Record[] { readLocationIntoRecord(result) });
				processResponse(dsRequest.getRequestId(), response);
			}
		});
	}
	
	protected void update(final DSRequest dsRequest) {
		Record record = dsRequest.getOldValues();
		
		Record changes = dsRequest.getAttributeAsRecord("data");
		
		assert(changes.getAttributeAsInt("id") == record.getAttributeAsInt("id"));
		if (changes.getAttribute("room") != null)
			record.setAttribute("room", changes.getAttribute("room"));
		if (changes.getAttribute("type") != null)
			record.setAttribute("type", changes.getAttribute("type"));
		if (changes.getAttribute("maxOccupancy") != null)
			record.setAttribute("maxOccupancy", changes.getAttribute("maxOccupancy"));
		if (changes.getAttribute("equipment") != null)
			record.setAttribute("equipment", changes.getAttribute("equipment"));
		if (changes.getAttribute("isSchedulable") != null)
			record.setAttribute("isSchedulable", changes.getAttribute("isSchedulable"));
		
		final LocationGWT location = readRecordIntoLocation(record);
		
		service.editLocation(location, new AsyncCallback<Void>() {
			@Override
			public void onFailure(Throwable caught) {
				DSResponse dsResponse = new DSResponse();
				Window.alert("Failed to update location!");
				processResponse(dsRequest.getRequestId(), dsResponse);
			}
			
			@Override
			public void onSuccess(Void result) {
				unsavedDocumentStrategy.setDocumentChanged(true);
				DSResponse response = new DSResponse();
				response.setData(new Record[] { readLocationIntoRecord(location) });
				processResponse(dsRequest.getRequestId(), response);
			}
		});
	}
	
	protected void remove(final DSRequest dsRequest) {
		final Record record = dsRequest.getAttributeAsRecord("data");
		final LocationGWT location = readRecordIntoLocation(record);

		service.removeLocation(record.getAttributeAsInt("id"), new AsyncCallback<Void>() {
			@Override
			public void onSuccess(Void result) {
				unsavedDocumentStrategy.setDocumentChanged(true);
				DSResponse response = new DSResponse();
				response.setData(new Record[] { readLocationIntoRecord(location) });
				processResponse(dsRequest.getRequestId(), response);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				DSResponse dsResponse = new DSResponse();
				Window.alert("Failed to delete location!");
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
