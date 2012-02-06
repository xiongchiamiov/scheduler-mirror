package edu.calpoly.csc.scheduler.view.web.client.table.columns;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map.Entry;
import java.util.Set;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import edu.calpoly.csc.scheduler.view.web.client.table.OsmTable;
import edu.calpoly.csc.scheduler.view.web.client.table.OsmTable.IRowForCell;

class EditingMultiselectCell extends OsmTable.EditingCell {
	LinkedHashMap<String, String> options;
	
	ScrollPanel checkboxesContainer;
	HashMap<String, CheckBox> checkboxesByLabel;
	
	FocusPanel readingLabel;
	
	EditingMultiselectCell(final IRowForCell row, LinkedHashMap<String, String> options) {
		addStyleName("selectcell");
		
		readingLabel = new FocusPanel();
		readingLabel.add(new HTML("&#160;(blank)"));

		this.options = options;
		
		checkboxesContainer = new ScrollPanel();
		checkboxesByLabel = new HashMap<String, CheckBox>();
		
		VerticalPanel vp = new VerticalPanel();
		
		for (Entry<String, String> entry : options.entrySet()) {
			final CheckBox cb = new CheckBox();
			cb.setText(entry.getKey());
			checkboxesByLabel.put(entry.getKey(), cb);
			vp.add(cb);
		}
		
		checkboxesContainer.add(vp);
		

		removeStyleName("reading");
		addStyleName("writing");

		readingLabel.clear();
		
		readingLabel.add(new HTML(assembleValuesString()));
		clear();
		add(readingLabel);
		addStyleName("reading");
		removeStyleName("writing");
	}
	
	@Override
	public void enteredEditingMode() {
		clear();

		add(checkboxesContainer);
	}
	
	private String assembleValuesString() {
		String value = "";
		for (Entry<String, CheckBox> entry : checkboxesByLabel.entrySet()) {
			if (entry.getValue().isChecked()) {
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
	public void exitedEditingMode() {
		readingLabel.clear();
		
		readingLabel.add(new HTML(assembleValuesString()));
		clear();
		add(readingLabel);
		addStyleName("reading");
		removeStyleName("writing");
	}
	
//	@Override
//	public void focus() {
//		if (checkboxesByLabel.size() >= 1)
//			checkboxesByLabel.values().iterator().next().setFocus(true);
//	}

	public Set<String> getValue() {
		Set<String> result = new LinkedHashSet<String>();
		for (Entry<String, CheckBox> checkboxByLabel : checkboxesByLabel.entrySet())
			if (checkboxByLabel.getValue().isChecked())
				result.add(options.get(checkboxByLabel.getKey()));
		return result;
	}
	
	public void setValue(Set<String> wantedValues) {
		for (Entry<String, CheckBox> entry : checkboxesByLabel.entrySet())
			entry.getValue().setChecked(wantedValues.contains(options.get(entry.getKey())));

		readingLabel.clear();
		readingLabel.add(new HTML(assembleValuesString()));
	}
}