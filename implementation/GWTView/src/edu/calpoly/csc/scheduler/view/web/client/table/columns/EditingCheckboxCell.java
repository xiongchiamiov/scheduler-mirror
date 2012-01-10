package edu.calpoly.csc.scheduler.view.web.client.table.columns;

import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

import edu.calpoly.csc.scheduler.view.web.client.table.OsmTable;
import edu.calpoly.csc.scheduler.view.web.client.table.OsmTable.IRowForCell;

class EditingCheckboxCell extends SimplePanel implements OsmTable.Cell, OsmTable.EditingCell, OsmTable.ReadingModeAwareCell, OsmTable.EditingModeAwareCell {
	boolean editing;
	IRowForCell row;
	CheckBox checkbox;
	FocusPanel readingLabel;
	
	EditingCheckboxCell(final IRowForCell row) {
		this.row = row;
		
		addStyleName("stringcell");
		
		editing = false;
		checkbox = new CheckBox();
		
		readingLabel = new FocusPanel();
		readingLabel.add(new HTML("&#160;"));
		readingLabel.addFocusHandler(new FocusHandler() {
			public void onFocus(FocusEvent event) {
				row.enterEditingMode(EditingCheckboxCell.this);
			}
		});
	}
	
	@Override
	public void enterEditingMode() {
		assert(!editing);
		editing = true;
		assert(editing);
		clear();
		add(checkbox);
		addStyleName("writing");
	}

	@Override
	public void enterReadingMode() {
		editing = false;
		clear();
		add(readingLabel);
		addStyleName("reading");
	}
	
	@Override
	public Widget getCellWidget() { return this; }
	
	@Override
	public void focus() {
		checkbox.setFocus(true);
	}
	
	public Boolean getValue() { return checkbox.getValue(); }
	public void setValue(Boolean value) {
		checkbox.setValue(value);
		readingLabel.clear();
		readingLabel.add(new HTML(value ? "Yes" : "No"));
	}
}