package scheduler.view.web.client.table.columns;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

import scheduler.view.web.client.table.OsmTable;

class ReadingStringCell extends OsmTable.ReadingCell {
	ReadingStringCell() {
		addStyleName("stringcell");
	}
	
	public void setValue(String value) {
		clear();
		add(new HTML(value));
	}
}