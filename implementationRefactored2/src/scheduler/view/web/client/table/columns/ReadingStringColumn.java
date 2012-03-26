package scheduler.view.web.client.table.columns;

import scheduler.view.web.client.table.IStaticGetter;
import scheduler.view.web.client.table.OsmTable;
import scheduler.view.web.client.table.OsmTable.Cell;
import scheduler.view.web.client.table.OsmTable.IRowForColumn;
import scheduler.view.web.client.table.OsmTable.ReadingCell;
import scheduler.view.web.shared.Identified;

public class ReadingStringColumn<ObjectType extends Identified> implements OsmTable.IReadingColumn<ObjectType> {
	protected IStaticGetter<ObjectType, String> getter;
	
	public ReadingStringColumn(final IStaticGetter<ObjectType, String> getter) {
		this.getter = getter;
	}

	public Cell createCell(final IRowForColumn<ObjectType> row) {
		return new ReadingStringCell();
	}

	@Override
	public void updateFromObject(IRowForColumn<ObjectType> row, ReadingCell rawCell) {
		assert(rawCell instanceof ReadingStringCell);
		ReadingStringCell cell = (ReadingStringCell)rawCell;
		cell.setValue(getter.getValueForObject(row.getObject()));
	}
}
