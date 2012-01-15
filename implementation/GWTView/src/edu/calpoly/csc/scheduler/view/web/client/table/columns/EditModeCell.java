package edu.calpoly.csc.scheduler.view.web.client.table.columns;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

import edu.calpoly.csc.scheduler.view.web.client.table.OsmTable;
import edu.calpoly.csc.scheduler.view.web.client.table.OsmTable.IRowForCell;

class EditModeCell extends SimplePanel implements OsmTable.Cell, OsmTable.ReadingModeAwareCell, OsmTable.EditingModeAwareCell {
	boolean editing;
	IRowForCell row;
	
	Button doneButtonVisibleWhileEditing;
	Button editButtonVisibleWhileReading;
	
	EditModeCell(final IRowForCell row) {
		this.row = row;
		
		editing = false;

		doneButtonVisibleWhileEditing = new Button("Save", new ClickHandler() {
			public void onClick(ClickEvent event) {
				row.enterReadingMode();
				enterReadingMode();
			}
		});
		
		editButtonVisibleWhileReading = new Button("Edit", new ClickHandler() {
			public void onClick(ClickEvent event) {
				row.enterEditingMode(null);
			}
		});
	}
	
	@Override
	public void enterEditingMode() {
		assert(!editing);
		editing = true;
		assert(editing);
		clear();
		add(doneButtonVisibleWhileEditing);
		removeStyleName("reading");
		addStyleName("writing");
	}

	@Override
	public void enterReadingMode() {
		editing = false;
		clear();
		add(editButtonVisibleWhileReading);
		removeStyleName("writing");
		addStyleName("reading");
	}
	
	@Override
	public Widget getCellWidget() { return this; }
}