package scheduler.view.web.client.table.columns;

import com.google.gwt.user.client.ui.HTML;

import scheduler.view.web.client.table.IStaticGetter;
import scheduler.view.web.client.table.OsmTable;
import scheduler.view.web.client.table.OsmTable.Cell;
import scheduler.view.web.client.table.OsmTable.IRowForColumn;
import scheduler.view.web.shared.Identified;

public class StringColumn<ObjectType extends Identified> implements OsmTable.IColumn<ObjectType> {
	protected IStaticGetter<ObjectType, String> getter;
	
	public StringColumn(final IStaticGetter<ObjectType, String> getter) {
		this.getter = getter;
	}

	public Cell createCell(final IRowForColumn<ObjectType> row) {
		Cell result = new Cell();
		result.add(new HTML(getter.getValueForObject(row.getObject())));
		return result;
	}
}
