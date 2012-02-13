package edu.calpoly.csc.scheduler.view.web.client.table.columns;

import com.google.gwt.user.client.ui.HTML;

import edu.calpoly.csc.scheduler.view.web.client.table.IStaticGetter;
import edu.calpoly.csc.scheduler.view.web.client.table.OsmTable;
import edu.calpoly.csc.scheduler.view.web.client.table.OsmTable.Cell;
import edu.calpoly.csc.scheduler.view.web.client.table.OsmTable.IRowForColumn;
import edu.calpoly.csc.scheduler.view.web.shared.Identified;

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
