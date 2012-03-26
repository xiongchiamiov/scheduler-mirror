package edu.calpoly.csc.scheduler.view.web.client.table.columns;

import edu.calpoly.csc.scheduler.view.web.client.table.IStaticGetter;
import edu.calpoly.csc.scheduler.view.web.client.table.IStaticSetter;
import edu.calpoly.csc.scheduler.view.web.client.table.IStaticValidator;
import edu.calpoly.csc.scheduler.view.web.client.table.OsmTable;
import edu.calpoly.csc.scheduler.view.web.client.table.IStaticValidator.InputValid;
import edu.calpoly.csc.scheduler.view.web.client.table.IStaticValidator.ValidateResult;
import edu.calpoly.csc.scheduler.view.web.client.table.OsmTable.Cell;
import edu.calpoly.csc.scheduler.view.web.client.table.OsmTable.EditingCell;
import edu.calpoly.csc.scheduler.view.web.client.table.OsmTable.IRowForColumn;
import edu.calpoly.csc.scheduler.view.web.client.table.OsmTable.ReadingCell;
import edu.calpoly.csc.scheduler.view.web.shared.Identified;

public class EditingStringColumn<ObjectType extends Identified> implements OsmTable.IEditingColumn<ObjectType> {
	public interface enterReadingModeCallback {
		void enterReadingMode();
	}
	
	protected IStaticGetter<ObjectType, String> getter;
	protected IStaticSetter<ObjectType, String> setter;
	protected IStaticValidator<ObjectType, String> validator;
	
	public EditingStringColumn(final IStaticGetter<ObjectType, String> getter, IStaticSetter<ObjectType, String> setter, IStaticValidator<ObjectType, String> validator) {
		this.getter = getter;
		this.setter = setter;
		this.validator = validator;
	}

	public Cell createCell(final IRowForColumn<ObjectType> row) {
		return new EditingStringCell();
	}
	
	public void updateFromObject(IRowForColumn<ObjectType> row, ReadingCell rawCell) {
		assert(rawCell instanceof EditingStringCell);
		EditingStringCell cell = (EditingStringCell)rawCell;
		cell.setValue(getter.getValueForObject(row.getObject()));
	}
	
	public void commitToObject(IRowForColumn<ObjectType> row, EditingCell rawCell) {
		assert(rawCell instanceof EditingStringCell);
		EditingStringCell cell = (EditingStringCell)rawCell;
		setter.setValueInObject(row.getObject(), cell.getValue());
	}

	@Override
	public ValidateResult validate(IRowForColumn<ObjectType> row, EditingCell rawCell) {
		assert(rawCell instanceof EditingStringCell);
		EditingStringCell cell = (EditingStringCell)rawCell;
		
		if (validator != null)
			return validator.validate(row.getObject(), cell.getValue());
		return new InputValid();
	}
}
