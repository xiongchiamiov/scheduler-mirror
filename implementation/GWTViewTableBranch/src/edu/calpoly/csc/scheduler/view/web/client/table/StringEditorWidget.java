package edu.calpoly.csc.scheduler.view.web.client.table;

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.TextBox;

class StringEditorWidget extends FocusPanel {
	Getter<String> getter;
	Setter<String> setter;
	boolean editing;
	
	StringEditorWidget(Getter<String> getter, Setter<String> setter) {
		this.getter = getter;
		this.setter = setter;
		
		addStyleName("stringcell");
		
		editing = false;
		enterReadingMode();
		addFocusHandler(new FocusHandler() {
			@Override
			public void onFocus(FocusEvent event) {
				enterWritingMode();
			}
		});
	}
	
	void enterWritingMode() {
		assert(!editing);
		clear();
		final TextBox box = new TextBox();
		add(box);
		box.setValue(getter.getValue());
		
		box.addKeyUpHandler(new KeyUpHandler() {
			@Override
			public void onKeyUp(KeyUpEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
					setter.setValue(box.getText());
					enterReadingMode();
				}
			}
		});
		box.addBlurHandler(new BlurHandler() {
			@Override
			public void onBlur(BlurEvent event) {
				setter.setValue(box.getText());
				enterReadingMode();
			}
		});
		box.setFocus(true);
		box.selectAll();
		addStyleName("writing");
	}
	
	void enterReadingMode() {
		editing = false;
		clear();
		add(new HTML(getter.getValue()));
		addStyleName("reading");
	}
}