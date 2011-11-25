package edu.calpoly.csc.scheduler.view.web.client.table.columns;

import java.util.Comparator;

import com.google.gwt.user.client.ui.HTML;

import edu.calpoly.csc.scheduler.view.web.client.table.IStaticGetter;
import edu.calpoly.csc.scheduler.view.web.client.table.OsmTable;
import edu.calpoly.csc.scheduler.view.web.client.table.OsmTable.Cell;
import edu.calpoly.csc.scheduler.view.web.client.table.OsmTable.IRowForColumn;
import edu.calpoly.csc.scheduler.view.web.client.table.OsmTable.SimpleCell;
import edu.calpoly.csc.scheduler.view.web.shared.Identified;

public class StringColumn<ObjectType extends Identified> implements OsmTable.IColumn<ObjectType> {
	protected IStaticGetter<ObjectType, String> getter;
	
	public StringColumn(String name, String width, final IStaticGetter<ObjectType, String> getter, final Comparator<String> sorter) {
		this.getter = getter;
	}

	public Cell createCell(final IRowForColumn<ObjectType> row) {
		return new SimpleCell(new HTML(getter.getValueForObject(row.getObject())));
	}
}
