package edu.calpoly.csc.scheduler.view.web.client.table;

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public abstract class StringColumn<ObjectType> implements OsmTable.Column<ObjectType, String> {
	class EditorWidget extends FocusPanel {
		final ObjectType object;
		boolean editing;
		
		EditorWidget(ObjectType object) {
			this.object = object;
			
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
			box.setValue(getValue(object));
			
			box.addKeyPressHandler(new KeyPressHandler() {
				@Override
				public void onKeyPress(KeyPressEvent event) {
					if (event.getCharCode() == '\n')
						setFocus(false);
				}
			});
			box.addBlurHandler(new BlurHandler() {
				@Override
				public void onBlur(BlurEvent event) {
					setValue(object, box.getText());
					enterReadingMode();
				}
			});
		}
		
		void enterReadingMode() {
			editing = false;
			clear();
			add(new HTML(getValue(object)));
		}
	}
	
	ObjectType object;
	
	public String name;
	public String getName() { return name; }
	public StringColumn(String name) {
		this.name = name;
	}
	
	public Widget createCellWidget(final ObjectType object) {
		return new EditorWidget(object);
	}
}
