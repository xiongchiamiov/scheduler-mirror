package edu.calpoly.csc.scheduler.view.web.client.table.columns;

import edu.calpoly.csc.scheduler.view.web.client.table.OsmTable;
import edu.calpoly.csc.scheduler.view.web.client.table.OsmTable.Cell;
import edu.calpoly.csc.scheduler.view.web.client.table.OsmTable.EditingCell;
import edu.calpoly.csc.scheduler.view.web.client.table.OsmTable.IRowForColumn;
import edu.calpoly.csc.scheduler.view.web.client.table.OsmTable.ReadingCell;
import edu.calpoly.csc.scheduler.view.web.shared.Identified;

public class EditSaveColumn<ObjectType extends Identified> implements OsmTable.IEditingColumn<ObjectType> {
	public interface ClickCallback<ObjectType> {
		void enteredMode(ObjectType object);
		void exitedMode(ObjectType object);
	}
	
	private ClickCallback<ObjectType> clickHandler;
	private final String enterModeButtonLabel, exitModeButtonLabel;
	
	public EditSaveColumn(String enterModeButtonLabel, String exitModeButtonLabel, ClickCallback<ObjectType> click) {
		this.clickHandler = click;
		this.enterModeButtonLabel = enterModeButtonLabel;
		this.exitModeButtonLabel = exitModeButtonLabel;
	}
	
	public Cell createCell(final IRowForColumn<ObjectType> row) {
		return new EditSaveCell<ObjectType>(row.getObject(), enterModeButtonLabel, exitModeButtonLabel, clickHandler);
	}

	public void updateFromObject(IRowForColumn<ObjectType> row, ReadingCell cell) { }
	public void commitToObject(IRowForColumn<ObjectType> row, EditingCell cell) { }
}
