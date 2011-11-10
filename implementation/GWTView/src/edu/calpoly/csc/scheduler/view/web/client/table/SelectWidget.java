package edu.calpoly.csc.scheduler.view.web.client.table;

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

class SelectWidget extends FocusPanel {
	Getter<String> getter;
	Setter<String> setter;
	boolean editing;
	LinkedHashMap<String, String> options;
	
	SelectWidget(LinkedHashMap<String, String> options, Getter<String> getter, Setter<String> setter) {
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
		
		String currentValue = getter.getValue();
		
		final ListBox listBox = new ListBox();
		int index = 0;
		for (String key : options.keySet()) {
			String value = options.get(key);
			listBox.addItem(key, value);
			if (value.equals(currentValue))
				listBox.setSelectedIndex(index);
			index++;
		}
		add(listBox);
		
		listBox.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				setter.setValue(listBox.getValue(listBox.getSelectedIndex()));
				enterReadingMode();
			}
		});
		listBox.addBlurHandler(new BlurHandler() {
			@Override
			public void onBlur(BlurEvent event) {
				setter.setValue(listBox.getValue(listBox.getSelectedIndex()));
				enterReadingMode();
			}
		});
		listBox.setFocus(true);
		addStyleName("writing");
	}
	
	void enterReadingMode() {
		editing = false;
		clear();
		add(new HTML(getter.getValue()));
		addStyleName("reading");
	}
}