package scheduler.view.web.client.views.home;

import java.util.Collection;

import scheduler.view.web.shared.DocumentGWT;

import com.smartgwt.client.data.DSRequest;
import com.smartgwt.client.data.DSResponse;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.data.fields.DataSourceIntegerField;
import com.smartgwt.client.data.fields.DataSourceTextField;
import com.smartgwt.client.types.DSOperationType;
import com.smartgwt.client.types.DSProtocol;

public class DocumentsDataSource extends DataSource {
	interface DocumentsStrategy {
		Collection<DocumentGWT> getAllDocuments();
	}
	
	DocumentsStrategy documentsStrategy;

	public DocumentsDataSource(DocumentsStrategy documentsStrategy) {
		this.documentsStrategy = documentsStrategy;

		setDataProtocol(DSProtocol.CLIENTCUSTOM);
		
		this.setAddGlobalId(false);

		DataSourceIntegerField idField = new DataSourceIntegerField("id");
		idField.setHidden(true);
		idField.setRequired(true);
		idField.setPrimaryKey(true);
		
		DataSourceTextField nameField = new DataSourceTextField("name");

		setFields(idField, nameField);
		
		setClientOnly(true);
	}

	@Override
   protected Object transformRequest(final DSRequest dsRequest) {
		assert(dsRequest.getOperationType() == DSOperationType.FETCH);
		
		System.out.println("DocumentsDataSource transformRequest()");
		
		Collection<DocumentGWT> resultDocuments = documentsStrategy.getAllDocuments();
		
		Record[] resultRecords = new Record[resultDocuments.size()];
		int resultRecordIndex = 0;
		for (DocumentGWT resultDocument : resultDocuments) {
			Record resultRecord = new Record();
			resultRecord.setAttribute("id", resultDocument.getID());
			resultRecord.setAttribute("name", resultDocument.getName());
			resultRecords[resultRecordIndex++] = resultRecord;
		}
		
		DSResponse response = new DSResponse();
		response.setData(resultRecords);
		processResponse(dsRequest.getRequestId(), response);

		System.out.println("End DocumentsDataSource transformRequest()");
		
		return dsRequest;
	}
}
