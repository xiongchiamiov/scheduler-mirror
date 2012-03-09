package edu.calpoly.csc.scheduler.view.web.client.table.columns;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;

import edu.calpoly.csc.scheduler.view.web.client.table.OsmTable;
import edu.calpoly.csc.scheduler.view.web.client.table.OsmTable.Cell;
import edu.calpoly.csc.scheduler.view.web.client.table.OsmTable.IRowForColumn;
import edu.calpoly.csc.scheduler.view.web.shared.Identified;

public class ButtonColumn<ObjectType extends Identified> implements OsmTable.IColumn<ObjectType> {
	public interface ClickCallback<ObjectType> {
		void buttonClickedForObject(ObjectType object, Button button);
	}
	
	private ClickCallback<ObjectType> clickHandler;
	public final String buttonLabel;
	
	public ButtonColumn(String buttonLabel, ClickCallback<ObjectType> click) {
		this.clickHandler = click;
		this.buttonLabel = buttonLabel;
	}
	
	public Cell createCell(final IRowForColumn<ObjectType> row) {
		final Button button = new Button(buttonLabel);
		button.setStyleName("buttonStyle");
		button.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				clickHandler.buttonClickedForObject(row.getObject(), button);
			}
		});
		
		button.addStyleName("courseTableLabButton");
		
		Cell cell = new Cell();
		cell.add(button);
		return cell;
	}
}
