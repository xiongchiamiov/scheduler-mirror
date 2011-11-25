package edu.calpoly.csc.scheduler.view.web.client.table.columns;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

import edu.calpoly.csc.scheduler.view.web.client.table.IGetter;
import edu.calpoly.csc.scheduler.view.web.client.table.ISetter;
import edu.calpoly.csc.scheduler.view.web.client.table.OsmTable;

class MultiselectCell extends SimplePanel implements OsmTable.EditingCell {
	IGetter<Collection<String>> getter;
	ISetter<Collection<String>> setter;
	boolean editing;
	LinkedHashMap<String, String> options;
	ListBox listBox;
	
	MultiselectCell(LinkedHashMap<String, String> options, IGetter<Collection<String>> getter, ISetter<Collection<String>> setter) {
		this.getter = getter;
		this.setter = setter;
		
		addStyleName("selectcell");
		
		editing = false;
		enterReadingMode();
//		addFocusHandler(new FocusHandler() {
//			@Override
//			public void onFocus(FocusEvent event) {
//				enterEditingMode();
//			}
//		});

		this.options = options;
	}
	
	@Override
	public void enterEditingMode() {
		assert(!editing);
		clear();
		
		Collection<String> currentValues = getter.getValue();
		
		listBox = new ListBox(true);
		int index = 0;
		for (String key : options.keySet()) {
			String value = options.get(key);
			listBox.addItem(key, value);
			if (currentValues.contains(value))
				listBox.setItemSelected(index, true);
			index++;
		}
		add(listBox);
		
		listBox.addBlurHandler(new BlurHandler() {
			@Override
			public void onBlur(BlurEvent event) {
				Collection<String> newValues = new ArrayList<String>();
				for (int index = 0; index < options.size(); index++)
					if (listBox.isItemSelected(index))
						newValues.add(listBox.getValue(index));
				setter.setValue(newValues);
				enterReadingMode();
			}
		});
		listBox.setFocus(true);
		addStyleName("writing");
	}
	
	@Override
	public void enterReadingMode() {
		editing = false;
		clear();
		String joined = "";
		for (String value : getter.getValue())
			joined = (joined.equals("") ? "" : joined + ", ") + value;
		
		add(new HTML(joined + "&#160;"));
		addStyleName("reading");
	}

	@Override
	public Widget getCellWidget() { return this; }
	
	@Override
	public void focus() {
		listBox.setFocus(true);
	}
}