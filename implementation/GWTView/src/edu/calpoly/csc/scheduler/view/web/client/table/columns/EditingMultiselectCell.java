package edu.calpoly.csc.scheduler.view.web.client.table.columns;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map.Entry;
import java.util.Set;

import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import edu.calpoly.csc.scheduler.view.web.client.table.OsmTable;
import edu.calpoly.csc.scheduler.view.web.client.table.OsmTable.IRowForCell;

class EditingMultiselectCell extends SimplePanel implements OsmTable.Cell, OsmTable.ReadingCell, OsmTable.EditingCell, OsmTable.ReadingModeAwareCell, OsmTable.EditingModeAwareCell {
	boolean editing;
	LinkedHashMap<String, String> options;
	
	ScrollPanel checkboxesContainer;
	HashMap<String, CheckBox> checkboxesByLabel;
	
	FocusPanel readingLabel;
	
	EditingMultiselectCell(final IRowForCell row, LinkedHashMap<String, String> options) {
		addStyleName("selectcell");
		
		editing = false;
		readingLabel = new FocusPanel();
		readingLabel.add(new HTML("&#160;(blank)"));
		readingLabel.addFocusHandler(new FocusHandler() {
			public void onFocus(FocusEvent event) {
				row.enterEditingMode(EditingMultiselectCell.this);
			}
		});

		this.options = options;
		
		checkboxesContainer = new ScrollPanel();
		checkboxesByLabel = new HashMap<String, CheckBox>();
		
		VerticalPanel vp = new VerticalPanel();
		
		for (Entry<String, String> entry : options.entrySet()) {
			CheckBox cb = new CheckBox();
			cb.setText(entry.getKey());
			checkboxesByLabel.put(entry.getKey(), cb);
			vp.add(cb);
		}
		
		checkboxesContainer.add(vp);
		
		
		addStyleName("writing");
		
		enterReadingMode();
	}
	
	@Override
	public void enterEditingMode() {
		assert(!editing);
		clear();

		add(checkboxesContainer);
	}
	
	private String assembleValuesString() {
		System.out.println("assemmmble!");
		String value = "";
		for (Entry<String, CheckBox> entry : checkboxesByLabel.entrySet()) {
			System.out.println("for entry " + entry.getKey() + " its " + entry.getValue().getValue() + " and " + entry.getValue().getFormValue() + " and " + entry.getValue().isChecked());
			if (entry.getValue().getValue()) {
				if (!value.equals(""))
					value += ", ";
				value += entry.getKey();
			}
		}
		
		if (value.equals(""))
			value = "&#160;";
		return value;
	}
	
	@Override
	public void enterReadingMode() {
		editing = false;
		readingLabel.clear();
		
		readingLabel.add(new HTML(assembleValuesString()));
		clear();
		add(readingLabel);
		addStyleName("reading");
	}
	
	@Override
	public Widget getCellWidget() { return this; }
	
	@Override
	public void focus() {
		if (checkboxesByLabel.size() >= 1)
			checkboxesByLabel.values().iterator().next().setFocus(true);
	}

	public Set<String> getValue() {
		Set<String> result = new LinkedHashSet<String>();
		for (Entry<String, CheckBox> checkboxByLabel : checkboxesByLabel.entrySet())
			if (checkboxByLabel.getValue().getValue())
				result.add(options.get(checkboxByLabel.getKey()));
		return result;
	}
	
	public void setValue(Set<String> wantedValues) {
		for (Entry<String, CheckBox> entry : checkboxesByLabel.entrySet())
			entry.getValue().setValue(wantedValues.contains(entry.getKey()));

		readingLabel.clear();
		readingLabel.add(new HTML(assembleValuesString()));
	}
}