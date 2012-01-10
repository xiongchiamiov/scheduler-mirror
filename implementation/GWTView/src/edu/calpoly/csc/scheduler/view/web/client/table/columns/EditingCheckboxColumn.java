package edu.calpoly.csc.scheduler.view.web.client.table.columns;

import edu.calpoly.csc.scheduler.view.web.client.table.IStaticGetter;
import edu.calpoly.csc.scheduler.view.web.client.table.IStaticSetter;
import edu.calpoly.csc.scheduler.view.web.client.table.OsmTable;
import edu.calpoly.csc.scheduler.view.web.client.table.OsmTable.Cell;
import edu.calpoly.csc.scheduler.view.web.client.table.OsmTable.EditingCell;
import edu.calpoly.csc.scheduler.view.web.client.table.OsmTable.IRowForColumn;
import edu.calpoly.csc.scheduler.view.web.client.table.OsmTable.ReadingCell;
import edu.calpoly.csc.scheduler.view.web.shared.Identified;

public class EditingCheckboxColumn<ObjectType extends Identified> implements OsmTable.IEditingColumn<ObjectType> {
	public interface enterReadingModeCallback {
		void enterReadingMode();
	}
	
	protected IStaticGetter<ObjectType, Boolean> getter;
	protected IStaticSetter<ObjectType, Boolean> setter;
	
	public EditingCheckboxColumn(final IStaticGetter<ObjectType, Boolean> getter, IStaticSetter<ObjectType, Boolean> setter) {
		this.getter = getter;
		this.setter = setter;
	}

	public Cell createCell(final IRowForColumn<ObjectType> row) {
		return new EditingCheckboxCell(row);
	}
	
	public void updateFromObject(IRowForColumn<ObjectType> row, ReadingCell rawCell) {
		assert(rawCell instanceof EditingCheckboxCell);
		EditingCheckboxCell cell = (EditingCheckboxCell)rawCell;
		cell.setValue(getter.getValueForObject(row.getObject()));
	}
	
	public void commitToObject(IRowForColumn<ObjectType> row, EditingCell rawCell) {
		assert(rawCell instanceof EditingCheckboxCell);
		EditingCheckboxCell cell = (EditingCheckboxCell)rawCell;
		
		setter.setValueInObject(row.getObject(), cell.getValue());
	}
}
