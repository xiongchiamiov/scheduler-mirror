package scheduler.view.web.client.table.columns;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Image;

import scheduler.view.web.client.table.OsmTable;
import scheduler.view.web.client.table.OsmTable.Cell;
import scheduler.view.web.client.table.OsmTable.IRowForColumn;
import scheduler.view.web.shared.Identified;

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
		
		Cell result = new Cell();
		result.add(button);
		return result;
	}
}
