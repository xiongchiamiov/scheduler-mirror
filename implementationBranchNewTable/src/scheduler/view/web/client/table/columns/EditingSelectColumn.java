package scheduler.view.web.client.table.columns;

import java.util.LinkedHashMap;

import com.google.gwt.user.client.Window;

import scheduler.view.web.client.table.IStaticGetter;
import scheduler.view.web.client.table.IStaticSetter;
import scheduler.view.web.client.table.IStaticValidator.InputInvalid;
import scheduler.view.web.client.table.IStaticValidator.InputValid;
import scheduler.view.web.client.table.IStaticValidator.ValidateResult;
import scheduler.view.web.client.table.OsmTable;
import scheduler.view.web.client.table.OsmTable.Cell;
import scheduler.view.web.client.table.OsmTable.EditingCell;
import scheduler.view.web.client.table.OsmTable.IRowForColumn;
import scheduler.view.web.client.table.OsmTable.ReadingCell;
import scheduler.view.web.shared.Identified;

public class EditingSelectColumn<ObjectType extends Identified> implements OsmTable.IEditingColumn<ObjectType> {
	private LinkedHashMap<String, String> options;
	protected IStaticGetter<ObjectType, String> getter;
	protected IStaticSetter<ObjectType, String> setter;

	public EditingSelectColumn(LinkedHashMap<String, String> options, final IStaticGetter<ObjectType, String> getter, IStaticSetter<ObjectType, String> setter) {
//		super(name, width, sorter == null ? null : new Comparator<ObjectType>() {
//			public int compare(ObjectType o1, ObjectType o2) {
//				return sorter.compare(getter.getValueForObject(o1), getter.getValueForObject(o2));
//			}
//		});
		this.getter = getter;
		this.setter = setter;
		this.options = options;
	}
	
	private static LinkedHashMap<String, String> identityMap(String[] array) {
		LinkedHashMap<String, String> options = new LinkedHashMap<String, String>();
		for (String option : array)
			options.put(option, option);
		return options;
	}

	public EditingSelectColumn(String[] options, IStaticGetter<ObjectType, String> getter, IStaticSetter<ObjectType, String> setter) {
		this(identityMap(options), getter, setter);
	}
	
	public Cell createCell(final IRowForColumn<ObjectType> row) {
		if (!options.values().contains(getter.getValueForObject(row.getObject()))) {
			Window.alert("value " + getter.getValueForObject(row.getObject()) + " not in options");
		}
		
		return new EditingSelectCell(options);
	}

	@Override
	public void updateFromObject(IRowForColumn<ObjectType> row, ReadingCell rawCell) {
		assert(rawCell instanceof EditingSelectCell);
		EditingSelectCell cell = (EditingSelectCell)rawCell;
		cell.setValue(getter.getValueForObject(row.getObject()));
	}

	@Override
	public void commitToObject(IRowForColumn<ObjectType> row, EditingCell rawCell) {
		assert(rawCell instanceof EditingSelectCell);
		EditingSelectCell cell = (EditingSelectCell)rawCell;
		
		setter.setValueInObject(row.getObject(), cell.getValue());
	}

	@Override
	public ValidateResult validate(IRowForColumn<ObjectType> row, EditingCell rawCell) {
		assert(rawCell instanceof EditingSelectCell);
		EditingSelectCell cell = (EditingSelectCell)rawCell;
		
		if (!options.values().contains(cell.getValue()))
			return new InputInvalid(cell.getValue() + " is not in options!");
		
		return new InputValid();
	}
}