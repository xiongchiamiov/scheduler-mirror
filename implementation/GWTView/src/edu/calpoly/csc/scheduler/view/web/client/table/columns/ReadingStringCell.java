package edu.calpoly.csc.scheduler.view.web.client.table.columns;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

import edu.calpoly.csc.scheduler.view.web.client.table.OsmTable;

class ReadingStringCell extends SimplePanel implements OsmTable.Cell, OsmTable.ReadingCell {
	ReadingStringCell() {
		addStyleName("stringcell");
	}
	
	@Override
	public Widget getCellWidget() { return this; }
	
	public void setValue(String value) {
		clear();
		add(new HTML(value));
	}
}