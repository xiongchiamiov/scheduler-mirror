package edu.calpoly.csc.scheduler.view.web.client.table.columns;

import java.util.LinkedHashMap;

import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

import edu.calpoly.csc.scheduler.view.web.client.table.OsmTable;
import edu.calpoly.csc.scheduler.view.web.client.table.OsmTable.IRowForCell;

class EditingSelectCell extends SimplePanel implements OsmTable.Cell, OsmTable.ReadingCell, OsmTable.EditingCell, OsmTable.ReadingModeAwareCell, OsmTable.EditingModeAwareCell {
	boolean editing;
	LinkedHashMap<String, String> options;
	ListBox listBox;
	FocusPanel readingLabel;
	
	EditingSelectCell(final IRowForCell row, LinkedHashMap<String, String> options) {
		addStyleName("selectcell");
		
		editing = false;
		readingLabel = new FocusPanel();
		readingLabel.add(new HTML("&#160;(blank)"));
		readingLabel.addFocusHandler(new FocusHandler() {
			public void onFocus(FocusEvent event) {
				row.enterEditingMode(EditingSelectCell.this);
			}
		});

		this.options = options;
		
		listBox = new ListBox();
		for (String key : options.keySet()) {
			String value = options.get(key);
			listBox.addItem(key, value);
		}
		
		listBox.setFocus(true);
		addStyleName("writing");
		
		enterReadingMode();
	}
	
	@Override
	public void enterEditingMode() {
		assert(!editing);
		clear();

		add(listBox);
	}
	
	@Override
	public void enterReadingMode() {
		editing = false;
		readingLabel.clear();
		
		String value = listBox.getItemText(listBox.getSelectedIndex());
		if (value.equals(""))
			value = "&#160;";
		
		readingLabel.add(new HTML(value));
		clear();
		add(readingLabel);
		addStyleName("reading");
	}
	
	@Override
	public Widget getCellWidget() { return this; }
	
	@Override
	public void focus() {
		listBox.setFocus(true);
	}

	public String getValue() {
		return listBox.getValue(listBox.getSelectedIndex());
	}
	
	private int getIndexForValue(String wantedValue) {
		int index = 0;
		for (String optionValue : options.values()) {
			if (wantedValue.equals(optionValue))
				return index;
			index++;
		}
		assert(false);
		return 0;
	}
	
	public void setValue(String wantedValue) {
		int index = getIndexForValue(wantedValue);
		listBox.setSelectedIndex(index);

		readingLabel.clear();
		String value = listBox.getItemText(listBox.getSelectedIndex());
		if (value.equals(""))
			value = "&#160;";
		readingLabel.add(new HTML(value));
	}
}