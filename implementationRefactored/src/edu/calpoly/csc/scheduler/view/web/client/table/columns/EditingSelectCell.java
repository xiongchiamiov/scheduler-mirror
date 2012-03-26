package edu.calpoly.csc.scheduler.view.web.client.table.columns;

import java.util.LinkedHashMap;

import com.google.gwt.dom.client.Document;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.ListBox;

import edu.calpoly.csc.scheduler.view.web.client.table.OsmTable;

class EditingSelectCell extends OsmTable.EditingCell {
	LinkedHashMap<String, String> options;
	ListBox listBox;
	FocusPanel readingLabel;
	
	EditingSelectCell(LinkedHashMap<String, String> options) {
		this.options = options;
		
		addStyleName("selectcell");
		
		readingLabel = new FocusPanel();
		add(readingLabel);

		listBox = new ListBox();
		for (String key : options.keySet()) {
			String value = options.get(key);
			listBox.addItem(key, value);
		}
		listBox.setFocus(true);

//		listBox.addFocusHandler(new FocusHandler() {
//			public void onFocus(FocusEvent event) {
//				event.stopPropagation();
//			}
//		});
//		
//		listBox.addClickHandler(new ClickHandler() {
//			public void onClick(ClickEvent event) {
//				event.stopPropagation();
//			}
//		});

		this.addKeyPressHandler(new KeyPressHandler() {
			public void onKeyPress(KeyPressEvent event) {
				if (event.getCharCode() == 13) {
					if (isInEditingMode())
						setInEditingMode(false);
				}
				else {
					if (!isInEditingMode()) {
						setInEditingMode(true);
						listBox.fireEvent(event);
					}
				}
				event.stopPropagation();
			}
		});
		
	}
	
	@Override
	public void enteredEditingMode() {
		clear();

		add(listBox);
		listBox.setFocus(true);
	}
	
	@Override
	public void exitedEditingMode() {
		readingLabel.clear();
		
		String value = listBox.getItemText(listBox.getSelectedIndex());
		if (value.equals(""))
			value = "&#160;";
		
		readingLabel.add(new HTML(value));
		clear();
		add(readingLabel);
		removeStyleName("writing");
		addStyleName("reading");
	}
	
//	@Override
//	public void focus() {
//		listBox.setFocus(true);
//	}

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