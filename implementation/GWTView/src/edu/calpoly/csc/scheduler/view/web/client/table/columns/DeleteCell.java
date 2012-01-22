package edu.calpoly.csc.scheduler.view.web.client.table.columns;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

import edu.calpoly.csc.scheduler.view.web.client.table.OsmTable;
import edu.calpoly.csc.scheduler.view.web.client.table.OsmTable.IRowForCell;

class DeleteCell extends SimplePanel implements OsmTable.Cell, OsmTable.EditingModeAwareCell {
	boolean editing;
	IRowForCell row;
	
	Button deleteButton;
	
	DeleteCell(final IRowForCell row) {
		this.row = row;
		
		editing = false;
		
		deleteButton = new Button("Delete", new ClickHandler() {
			public void onClick(ClickEvent event) {
				row.delete();
			}
		});
		
		add(deleteButton);
	}
	
	@Override
	public void enterEditingMode() {
		assert(!editing);
		editing = true;
		deleteButton.setEnabled(false);
	}

	@Override
	public void exitEditingMode() {
		editing = false;
		deleteButton.setEnabled(true);
	}
	
	@Override
	public Widget getCellWidget() { return this; }
}