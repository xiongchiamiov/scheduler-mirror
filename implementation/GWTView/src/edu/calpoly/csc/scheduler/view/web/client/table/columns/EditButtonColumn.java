package edu.calpoly.csc.scheduler.view.web.client.table.columns;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Image;

import edu.calpoly.csc.scheduler.view.web.client.table.OsmTable;
import edu.calpoly.csc.scheduler.view.web.client.table.OsmTable.Cell;
import edu.calpoly.csc.scheduler.view.web.client.table.OsmTable.IRowForColumn;
import edu.calpoly.csc.scheduler.view.web.client.table.OsmTable.SimpleCell;
import edu.calpoly.csc.scheduler.view.web.shared.Identified;

public class EditButtonColumn<ObjectType extends Identified> implements OsmTable.IColumn<ObjectType> {
	public interface ClickCallback<ObjectType> {
		void buttonClickedForObject(ObjectType object);
	}
	
	private ClickCallback<ObjectType> clickHandler;
	public final String imagePath;
	
	public EditButtonColumn(String imagePath, ClickCallback<ObjectType> click) {
		this.clickHandler = click;
		this.imagePath = imagePath;
	}
	
	public Cell createCell(final IRowForColumn<ObjectType> row) {
		final Image button = new Image(imagePath);
		button.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				clickHandler.buttonClickedForObject(row.getObject());
			}
		});
		
		return new SimpleCell(button);
	}
}
