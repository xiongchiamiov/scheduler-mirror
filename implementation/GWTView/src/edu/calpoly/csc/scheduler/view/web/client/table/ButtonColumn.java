package edu.calpoly.csc.scheduler.view.web.client.table;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Widget;

import edu.calpoly.csc.scheduler.view.web.shared.Identified;

public class ButtonColumn<ObjectType extends Identified> extends OsmTable.Column<ObjectType> {
	public interface ClickCallback<ObjectType> {
		void buttonClickedForObject(ObjectType object, Button button);
		String initialLabel(ObjectType object);
	}
	
	private ClickCallback<ObjectType> clickHandler;
	
	public ButtonColumn(String name, String width, ClickCallback<ObjectType> click) {
		super(name, width, null);
		this.clickHandler = click;
	}
	
	public Widget createCellWidget(final ObjectType object) {
		final Button button = new Button(clickHandler.initialLabel(object));
		button.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				clickHandler.buttonClickedForObject(object, button);
			}
		});
		
		return button;
	}
}
