package edu.calpoly.csc.scheduler.view.web.client.table.columns;

import edu.calpoly.csc.scheduler.view.web.client.table.OsmTable;
import edu.calpoly.csc.scheduler.view.web.client.table.OsmTable.Cell;
import edu.calpoly.csc.scheduler.view.web.client.table.OsmTable.IRowForColumn;
import edu.calpoly.csc.scheduler.view.web.shared.Identified;

public class EditModeColumn<ObjectType extends Identified> implements OsmTable.IColumn<ObjectType> {
	public Cell createCell(final IRowForColumn<ObjectType> row) {
		return new EditModeCell(row);
	}
}
