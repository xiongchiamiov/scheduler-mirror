package edu.calpoly.csc.scheduler.view.web.client.table.columns;

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.TextBox;

import edu.calpoly.csc.scheduler.view.web.client.table.OsmTable;

class EditingStringCell extends OsmTable.EditingCell {
	TextBox editingBox;
	FocusPanel readingLabel;
	
	EditingStringCell() {
		addStyleName("stringcell");
		
		editingBox = new TextBox();
		editingBox.addBlurHandler(new BlurHandler() {
			public void onBlur(BlurEvent event) {
				if (isInEditingMode())
					setInEditingMode(false);
			}
		});
		editingBox.addChangeHandler(new ChangeHandler() {
			public void onChange(ChangeEvent event) {
				System.out.println("editingbox onchange!");
				notifyValueChanged();
			}
		});
		editingBox.addKeyPressHandler(new KeyPressHandler() {
			public void onKeyPress(KeyPressEvent event) {
				System.out.println("editingbox keypress!");
				notifyValueChanged();
			}
		});
		editingBox.addKeyUpHandler(new KeyUpHandler() {
			public void onKeyUp(KeyUpEvent event) {
				System.out.println("editingbox keyup!");
				notifyValueChanged();
			}
		});
		
		readingLabel = new FocusPanel();
		readingLabel.add(new HTML("&#160;(blank)"));
//		readingLabel.addFocusHandler(new FocusHandler() {
//			public void onFocus(FocusEvent event) {
//				row.enterEditingMode(EditingStringCell.this);
//			}
//		});

		add(readingLabel);
		
		this.addKeyPressHandler(new KeyPressHandler() {
			public void onKeyPress(KeyPressEvent event) {
				if (event.getCharCode() == 13) {
					if (isInEditingMode())
						setInEditingMode(false);
				}
				else {
					if (!isInEditingMode()) {
						setInEditingMode(true);
						editingBox.fireEvent(event);
					}
				}
			}
		});
	}
	
	@Override
	public void enteredEditingMode() {
		int width = readingLabel.getOffsetWidth();
		clear();
		editingBox.setWidth(width + "px");
		add(editingBox);
		removeStyleName("reading");
		addStyleName("writing");

		editingBox.selectAll();
		editingBox.setFocus(true);

//		notifySizeChanged();
	}

	@Override
	public void exitedEditingMode() {
		clear();
		add(readingLabel);
		removeStyleName("writing");
		addStyleName("reading");
		
//		notifySizeChanged();
	}
	
//	@Override
//	public void focus() {
//		editingBox.setFocus(true);
//		editingBox.selectAll();
//	}
	
	public String getValue() { return editingBox.getValue(); }
	public void setValue(String value) {
		editingBox.setValue(value);
		readingLabel.clear();
		readingLabel.add(new HTML(value));
	}
}