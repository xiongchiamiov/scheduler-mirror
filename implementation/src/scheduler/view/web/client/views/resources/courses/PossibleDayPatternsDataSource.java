package scheduler.view.web.client.views.resources.courses;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import scheduler.view.web.client.CachedOpenWorkingCopyDocument;
import scheduler.view.web.shared.CourseGWT;

import com.smartgwt.client.data.AdvancedCriteria;
import com.smartgwt.client.data.Criterion;
import com.smartgwt.client.data.DSRequest;
import com.smartgwt.client.data.DSResponse;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.data.fields.DataSourceTextField;
import com.smartgwt.client.types.DSProtocol;
import com.smartgwt.client.types.OperatorId;

public class PossibleDayPatternsDataSource extends DataSource {
	public PossibleDayPatternsDataSource() {
		setDataProtocol(DSProtocol.CLIENTCUSTOM);
		
		this.setAddGlobalId(false);

		DataSourceTextField typeField = new DataSourceTextField("type");
		
		DataSourceTextField scuField = new DataSourceTextField("scu");

		DataSourceTextField patternField = new DataSourceTextField("dayPattern");
		patternField.setPrimaryKey(true);

		setFields(typeField, scuField, patternField);
		
		setClientOnly(true);
	}

	Record makeRecord(String typeField, int scuField, String patternField) {
		Record record = new Record();
		record.setAttribute("type", typeField);
		record.setAttribute("scu", scuField);
		record.setAttribute("dayPattern", patternField);
		return record;
	}
	
	protected void fetch(final DSRequest dsRequest) {
		// from https://scheduler.atlassian.net/secure/attachment/10202/DayCombosRequirements.pdf
		
		Record[] records = new Record[] {
				makeRecord("LAB,ACT", 2, "MW"),
				makeRecord("LAB,ACT", 2, "WF"),
				makeRecord("LAB,ACT", 2, "MF"),
				makeRecord("LAB,ACT", 2, "TR"),
				makeRecord("LAB,ACT", 3, "MWF"),
				makeRecord("LAB,ACT", 3, "MW"),
				makeRecord("LAB,ACT", 3, "TR"),
				makeRecord("LAB,ACT", 4, "TR"),
				makeRecord("LAB,ACT", 4, "MW"),
				makeRecord("LAB,ACT", 4, "WF"),
				makeRecord("LAB,ACT", 4, "MF"),
				makeRecord("LAB,ACT", 4, "MTWR"),
				makeRecord("LAB,ACT", 4, "MTWF"),
				makeRecord("LAB,ACT", 4, "MTRF"),
				makeRecord("LAB,ACT", 4, "TWRF"),
				makeRecord("LAB,ACT", 5, "MTWRF"),
				// scu of 0 is interpreted as "doesnt matter" (see PossibleDayPatternsFunction)
				makeRecord("LEC,ACT,SEM", 0, "Tether"),
				makeRecord("LEC,ACT,SEM", 0, "MWF"),
				makeRecord("LEC,ACT,SEM", 0, "MW"),
				makeRecord("LEC,ACT,SEM", 0, "TR"),
				makeRecord("LEC,ACT,SEM", 0, "M"),
				makeRecord("LEC,ACT,SEM", 0, "T"),
				makeRecord("LEC,ACT,SEM", 0, "W"),
				makeRecord("LEC,ACT,SEM", 0, "R"),
				makeRecord("LEC,ACT,SEM", 0, "F")
		};

		DSResponse response = new DSResponse();
		response.setData(records);
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
