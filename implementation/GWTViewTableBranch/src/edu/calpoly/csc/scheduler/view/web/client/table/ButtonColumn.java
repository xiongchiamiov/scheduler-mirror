package edu.calpoly.csc.scheduler.view.web.client.table;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Widget;

public class ButtonColumn<ObjectType extends Comparable<ObjectType>> extends OsmTable.Column<ObjectType> {
	public interface ClickCallback<ObjectType> {
		void buttonClickedForObject(ObjectType object);
	}
	
	private ClickCallback<ObjectType> clickHandler;
	private String buttonLabel;
	
	public ButtonColumn(String name, String width, String buttonLabel, ClickCallback<ObjectType> click) {
		super(name, width, null);
		this.clickHandler = click;
		this.buttonLabel = buttonLabel;
	}
	
	public Widget createCellWidget(final ObjectType object) {
		return new Button(buttonLabel, new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				clickHandler.buttonClickedForObject(object);
			}
		});
	}
}
