package scheduler.view.web.client.views.resources.courses;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import scheduler.view.web.client.CachedOpenWorkingCopyDocument;
import scheduler.view.web.client.views.resources.ResourceCollection;
import scheduler.view.web.shared.CourseGWT;

import com.smartgwt.client.data.DSRequest;
import com.smartgwt.client.data.DSResponse;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.data.fields.DataSourceIntegerField;
import com.smartgwt.client.data.fields.DataSourceTextField;
import com.smartgwt.client.types.DSProtocol;

public class LectureOptionsDataSource extends DataSource {
	CachedOpenWorkingCopyDocument document;
	
	public LectureOptionsDataSource(CachedOpenWorkingCopyDocument document) {
		this.document = document;
		
		setDataProtocol(DSProtocol.CLIENTCUSTOM);
		
		this.setAddGlobalId(false);
		
		DataSourceTextField valueField = new DataSourceTextField("valueField");
		
		valueField.setPrimaryKey(true);
		
		DataSourceTextField displayField = new DataSourceTextField("displayField");
		
		setFields(valueField, displayField);
		
		setClientOnly(true);
	}

	protected void fetch(final DSRequest dsRequest) {
		Collection<CourseGWT> courses = document.getCourses();
		
		List<Record> responseRecords = new LinkedList<Record>();
		
		Record noLectureRecord = new Record();
		noLectureRecord.setAttribute("valueField", -1);
		noLectureRecord.setAttribute("displayField", "(none)");
		responseRecords.add(noLectureRecord);
		
		for (CourseGWT course : courses) {
			if (course.getType().equals("LEC")) {
				Record record = new Record();
				record.setAttribute("valueField", course.getID());
				record.setAttribute("displayField", course.getDept() + " " + course.getCatalogNum());
				responseRecords.add(record);
			}
		}
		
		DSResponse response = new DSResponse();
		response.setData(responseRecords.toArray(new Record[0]));
		processResponse(dsRequest.getRequestId(), response);
	}
	
	@Override
   protected Object transformRequest(final DSRequest dsRequest) {
		switch (dsRequest.getOperationType()) {
			case FETCH: fetch(dsRequest); break;
			default: assert(false);
		}
		
      return dsRequest;
  }
}
