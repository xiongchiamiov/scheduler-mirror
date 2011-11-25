package edu.calpoly.csc.scheduler.view.web.client.table.columns;

import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

import edu.calpoly.csc.scheduler.view.web.client.table.OsmTable;
import edu.calpoly.csc.scheduler.view.web.client.table.OsmTable.IRowForCell;

class EditingStringCell extends SimplePanel implements OsmTable.EditingCell {
	boolean editing;
	IRowForCell row;
	TextBox editingBox;
	FocusPanel readingLabel;
	
	EditingStringCell(final IRowForCell row) {
		this.row = row;
		
		addStyleName("stringcell");
		
		editing = false;
		editingBox = new TextBox();
		editingBox.addKeyUpHandler(new KeyUpHandler() {
			@Override
			public void onKeyUp(KeyUpEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
					row.enterReadingMode();
					enterReadingMode();
				}
			}
		});
		
		readingLabel = new FocusPanel();
		readingLabel.add(new HTML("&#160;"));
		readingLabel.addFocusHandler(new FocusHandler() {
			public void onFocus(FocusEvent event) {
				row.enterEditingMode(EditingStringCell.this);
			}
		});
	}
	
	@Override
	public void enterEditingMode() {
		assert(!editing);
		editing = true;
		assert(editing);
		clear();
		add(editingBox);
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
		editingBox.setFocus(true);
		editingBox.selectAll();
	}
	
	public String getValue() { return editingBox.getValue(); }
	public void setValue(String value) {
		editingBox.setValue(value);
		readingLabel.clear();
		readingLabel.add(new HTML(value));
	}
}