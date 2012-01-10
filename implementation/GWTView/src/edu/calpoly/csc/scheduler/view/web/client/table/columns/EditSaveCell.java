package edu.calpoly.csc.scheduler.view.web.client.table.columns;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

import edu.calpoly.csc.scheduler.view.web.client.table.OsmTable;
import edu.calpoly.csc.scheduler.view.web.client.table.OsmTable.EditingCell;
import edu.calpoly.csc.scheduler.view.web.client.table.columns.EditSaveColumn.ClickCallback;

class EditSaveCell<ObjectType> extends SimplePanel implements OsmTable.Cell, EditingCell, OsmTable.ReadingModeAwareCell, OsmTable.EditingModeAwareCell {
	@Override
	public Widget getCellWidget() { return this; }
	
	boolean editing = false;
	final Button enterModeButton;
	final Button exitModeButton;
	final ClickCallback<ObjectType> click;
	final ObjectType object;
	
	EditSaveCell(final ObjectType object, final String enterModeButtonLabel, final String exitModeButtonLabel, final ClickCallback<ObjectType> click) {
		this.object = object;
		this.enterModeButton = new Button(enterModeButtonLabel);
		this.exitModeButton = new Button(exitModeButtonLabel);
		this.click = click;

		enterModeButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				click.enteredMode(object);
			}
		});

		exitModeButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				click.exitedMode(object);
			}
		});
		
		add(enterModeButton);
	}

	@Override
	public void enterEditingMode() {
		assert(!editing);
		editing = true;
		clear();
		add(exitModeButton);
	}

	@Override
	public void enterReadingMode() {
		editing = false;
		clear();
		add(enterModeButton);
	}

	@Override
	public void focus() { }
}
