package scheduler.view.web.client.views.home;

import java.util.Collection;
import java.util.LinkedList;

import scheduler.view.web.client.GreetingServiceAsync;
import scheduler.view.web.client.views.home.OriginalDocumentsCacheDataSource.Mode;
import scheduler.view.web.shared.DocumentGWT;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.data.DSRequest;
import com.smartgwt.client.data.DSResponse;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.data.fields.DataSourceIntegerField;
import com.smartgwt.client.data.fields.DataSourceTextField;
import com.smartgwt.client.types.DSOperationType;
import com.smartgwt.client.types.DSProtocol;

public class OriginalDocumentsCacheDataSource extends DataSource {
	enum Mode { LIVE_DOCUMENTS_ONLY, DELETED_DOCUMENTS_ONLY };
	
	OriginalDocumentsCache documentsCache;
	Mode mode;

	public OriginalDocumentsCacheDataSource(OriginalDocumentsCache documentsCache, Mode mode) {
		this.documentsCache = documentsCache;
		this.mode = mode;

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
		
		Collection<DocumentGWT> originalDocuments = documentsCache.getAllDocuments();
		
		System.out.println("transformrequest got originaldocs size " + originalDocuments.size());
		
		Collection<DocumentGWT> resultDocuments;
		
		if (mode == Mode.LIVE_DOCUMENTS_ONLY) {
			Collection<DocumentGWT> liveOriginalDocuments = new LinkedList<DocumentGWT>();
			for (DocumentGWT originalDocument : originalDocuments)
				if (!originalDocument.isTrashed())
					liveOriginalDocuments.add(originalDocument);
			resultDocuments = liveOriginalDocuments;
		}
		else {
			Collection<DocumentGWT> deletedOriginalDocuments = new LinkedList<DocumentGWT>();
			for (DocumentGWT originalDocument : originalDocuments)
				if (originalDocument.isTrashed())
					deletedOriginalDocuments.add(originalDocument);
			resultDocuments = deletedOriginalDocuments;
		}
		
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
		
		return dsRequest;
	}
}
