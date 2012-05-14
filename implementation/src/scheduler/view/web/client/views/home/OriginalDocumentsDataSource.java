package scheduler.view.web.client.views.home;

import java.util.Collection;

import scheduler.view.web.shared.OriginalDocumentGWT;

import com.smartgwt.client.data.DSRequest;
import com.smartgwt.client.data.DSResponse;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.data.fields.DataSourceIntegerField;
import com.smartgwt.client.data.fields.DataSourceTextField;
import com.smartgwt.client.types.DSOperationType;
import com.smartgwt.client.types.DSProtocol;

public class OriginalDocumentsDataSource extends DataSource {
	interface DocumentsStrategy {
		Collection<OriginalDocumentGWT> getAllDocuments();
	}
	
	DocumentsStrategy documentsStrategy;

	public OriginalDocumentsDataSource(DocumentsStrategy documentsStrategy) {
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
		
		Collection<OriginalDocumentGWT> resultDocuments = documentsStrategy.getAllDocuments();
		
		Record[] resultRecords = new Record[resultDocuments.size()];
		int resultRecordIndex = 0;
		for (OriginalDocumentGWT resultDocument : resultDocuments) {
			Record resultRecord = new Record();
			resultRecord.setAttribute("id", resultDocument.getID());
			resultRecord.setAttribute("name", resultDocument.getName());
			resultRecords[resultRecordIndex++] = resultRecord;
		}
		
		DSResponse response = new DSResponse();
		response.setData(resultRecords);
		processResponse(dsRequest.getRequestId(), response);

		return dsRequest;
	}
}
