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

class EditingCheckboxCell extends OsmTable.EditingCell {
	IRowForCell row;
	CheckBox checkbox;
	FocusPanel readingLabel;
	
	EditingCheckboxCell(final IRowForCell row) {
		this.row = row;
		
		addStyleName("stringcell");
		
		checkbox = new CheckBox();
		
		readingLabel = new FocusPanel();
		readingLabel.add(new HTML("&#160;"));

		addStyleName("writing");

		readingLabel.clear();
		readingLabel.add(new HTML(checkbox.isChecked() ? "Yes" : "No"));
		clear();
		add(readingLabel);
		removeStyleName("writing");
		addStyleName("reading");
	}
	
	@Override
	public void enteredEditingMode() {
		clear();
		add(checkbox);
		removeStyleName("reading");
		addStyleName("writing");
	}

	@Override
	public void exitedEditingMode() {
		readingLabel.clear();
		readingLabel.add(new HTML(checkbox.isChecked() ? "Yes" : "No"));
		clear();
		add(readingLabel);
		removeStyleName("writing");
		addStyleName("reading");
	}
	
//	@Override
//	public void focus() {
//		checkbox.setFocus(true);
//	}
	
	public Boolean getValue() {
		return checkbox.isChecked();
	}
	public void setValue(Boolean value) {
		checkbox.setChecked(value);
		readingLabel.clear();
		readingLabel.add(new HTML(value ? "Yes" : "No"));
	}
}