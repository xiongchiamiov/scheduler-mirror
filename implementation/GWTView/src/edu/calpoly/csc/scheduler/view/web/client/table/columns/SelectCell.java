package edu.calpoly.csc.scheduler.view.web.client.table.columns;

import java.util.LinkedHashMap;

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

import edu.calpoly.csc.scheduler.view.web.client.table.IGetter;
import edu.calpoly.csc.scheduler.view.web.client.table.ISetter;
import edu.calpoly.csc.scheduler.view.web.client.table.OsmTable;

class SelectCell extends SimplePanel implements OsmTable.EditingCell {
	IGetter<String> getter;
	ISetter<String> setter;
	boolean editing;
	LinkedHashMap<String, String> options;
	ListBox listBox;
	
	SelectCell(LinkedHashMap<String, String> options, IGetter<String> getter, ISetter<String> setter) {
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
		
		String currentValue = getter.getValue();
		
		listBox = new ListBox();
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
	
	@Override
	public void enterReadingMode() {
		editing = false;
		clear();
		add(new HTML(getter.getValue() + "&#160;"));
		addStyleName("reading");
	}
	
	@Override
	public Widget getCellWidget() { return this; }
	
	@Override
	public void focus() {
		listBox.setFocus(true);
	}
}