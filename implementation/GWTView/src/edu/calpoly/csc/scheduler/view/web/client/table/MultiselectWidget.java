package edu.calpoly.csc.scheduler.view.web.client.table;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.ListBox;

class MultiselectWidget extends FocusPanel {
	Getter<Collection<String>> getter;
	Setter<Collection<String>> setter;
	boolean editing;
	LinkedHashMap<String, String> options;
	
	MultiselectWidget(LinkedHashMap<String, String> options, Getter<Collection<String>> getter, Setter<Collection<String>> setter) {
		this.getter = getter;
		this.setter = setter;
		
		addStyleName("selectcell");
		
		editing = false;
		enterReadingMode();
		addFocusHandler(new FocusHandler() {
			@Override
			public void onFocus(FocusEvent event) {
				enterWritingMode();
			}
		});

		this.options = options;
	}
	
	void enterWritingMode() {
		assert(!editing);
		clear();
		
		Collection<String> currentValues = getter.getValue();
		
		final ListBox listBox = new ListBox(true);
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
	
	void enterReadingMode() {
		editing = false;
		clear();
		String joined = "";
		for (String value : getter.getValue())
			joined = (joined.equals("") ? "" : joined + ", ") + value;
		
		add(new HTML(joined + "&#160;"));
		addStyleName("reading");
	}
}