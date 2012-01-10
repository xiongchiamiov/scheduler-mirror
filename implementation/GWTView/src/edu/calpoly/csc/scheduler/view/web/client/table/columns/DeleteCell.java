package edu.calpoly.csc.scheduler.view.web.client.table.columns;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

import edu.calpoly.csc.scheduler.view.web.client.table.OsmTable;
import edu.calpoly.csc.scheduler.view.web.client.table.OsmTable.IRowForCell;

class DeleteCell extends SimplePanel implements OsmTable.Cell, OsmTable.EditingModeAwareCell, OsmTable.ReadingModeAwareCell {
	boolean editing;
	IRowForCell row;
	
	FocusPanel editingIcon;
	FocusPanel readingIcon;
	
	DeleteCell(final IRowForCell row) {
		this.row = row;
		
		editing = false;

		editingIcon = new FocusPanel();
		editingIcon.addStyleName("delete icon disabled");
		
		readingIcon = new FocusPanel();
		readingIcon.addStyleName("delete icon enabled");
		readingIcon.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				row.delete();
			}
		});
	}
	
	@Override
	public void enterEditingMode() {
		assert(!editing);
		editing = true;
		assert(editing);
		clear();
		add(editingIcon);
		addStyleName("writing");
	}

	@Override
	public void enterReadingMode() {
		editing = false;
		clear();
		add(readingIcon);
		addStyleName("reading");
	}
	
	@Override
	public Widget getCellWidget() { return this; }
}