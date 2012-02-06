package edu.calpoly.csc.scheduler.view.web.client.table.columns;

import java.util.LinkedHashMap;
import java.util.Set;

import edu.calpoly.csc.scheduler.view.web.client.table.IStaticGetter;
import edu.calpoly.csc.scheduler.view.web.client.table.IStaticSetter;
import edu.calpoly.csc.scheduler.view.web.client.table.OsmTable;
import edu.calpoly.csc.scheduler.view.web.client.table.OsmTable.Cell;
import edu.calpoly.csc.scheduler.view.web.client.table.OsmTable.EditingCell;
import edu.calpoly.csc.scheduler.view.web.client.table.OsmTable.IRowForColumn;
import edu.calpoly.csc.scheduler.view.web.client.table.OsmTable.ReadingCell;
import edu.calpoly.csc.scheduler.view.web.shared.Identified;

public class EditingMultiselectColumn<ObjectType extends Identified> implements OsmTable.IEditingColumn<ObjectType> {
	private LinkedHashMap<String, String> options;
	protected IStaticGetter<ObjectType, Set<String>> getter;
	protected IStaticSetter<ObjectType, Set<String>> setter;

	public EditingMultiselectColumn(LinkedHashMap<String, String> options, final IStaticGetter<ObjectType, Set<String>> getter, IStaticSetter<ObjectType, Set<String>> setter) {
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

	public EditingMultiselectColumn(String[] options, IStaticGetter<ObjectType, Set<String>> getter, IStaticSetter<ObjectType, Set<String>> setter) {
		this(identityMap(options), getter, setter);
	}
	
	public Cell createCell(final IRowForColumn<ObjectType> row) {
		return new EditingMultiselectCell(options);
	}

	@Override
	public void updateFromObject(IRowForColumn<ObjectType> row, ReadingCell rawCell) {
		assert(rawCell instanceof EditingMultiselectCell);
		EditingMultiselectCell cell = (EditingMultiselectCell)rawCell;
		cell.setValue(getter.getValueForObject(row.getObject()));
	}

	@Override
	public void commitToObject(IRowForColumn<ObjectType> row, EditingCell rawCell) {
		assert(rawCell instanceof EditingMultiselectCell);
		EditingMultiselectCell cell = (EditingMultiselectCell)rawCell;
		setter.setValueInObject(row.getObject(), cell.getValue());
	}
}
