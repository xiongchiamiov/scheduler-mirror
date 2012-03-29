package scheduler.view.web.client.table.columns;

import com.google.gwt.user.client.ui.HTML;

import scheduler.view.web.client.table.IStaticGetter;
import scheduler.view.web.client.table.OsmTable;
import scheduler.view.web.client.table.OsmTable.Cell;
import scheduler.view.web.client.table.OsmTable.IRowForColumn;
import scheduler.view.web.shared.Identified;

public class ConstantStringColumn<ObjectType extends Identified> implements OsmTable.IColumn<ObjectType> {
	protected IStaticGetter<ObjectType, String> getter;
	
	public ConstantStringColumn(final IStaticGetter<ObjectType, String> getter) {
		this.getter = getter;
	}

	public Cell createCell(final IRowForColumn<ObjectType> row) {
		Cell cell = new Cell();
		cell.add(new HTML(getter.getValueForObject(row.getObject())));
		return cell;
	}
}
