package edu.calpoly.csc.scheduler.view.web.client.table.columns;

import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashMap;

import edu.calpoly.csc.scheduler.view.web.client.table.IGetter;
import edu.calpoly.csc.scheduler.view.web.client.table.ISetter;
import edu.calpoly.csc.scheduler.view.web.client.table.IStaticGetter;
import edu.calpoly.csc.scheduler.view.web.client.table.IStaticSetter;
import edu.calpoly.csc.scheduler.view.web.client.table.OsmTable;
import edu.calpoly.csc.scheduler.view.web.client.table.OsmTable.Cell;
import edu.calpoly.csc.scheduler.view.web.client.table.OsmTable.IRowForColumn;
import edu.calpoly.csc.scheduler.view.web.shared.Identified;

public class MultiselectColumn<ObjectType extends Identified> implements OsmTable.IColumn<ObjectType> {
	private LinkedHashMap<String, String> options;
	protected IStaticGetter<ObjectType, Collection<String>> getter;
	protected IStaticSetter<ObjectType, Collection<String>> setter;

	public MultiselectColumn(LinkedHashMap<String, String> options, final IStaticGetter<ObjectType, Collection<String>> getter, IStaticSetter<ObjectType, Collection<String>> setter) {
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

	public MultiselectColumn(String[] options, IStaticGetter<ObjectType, Collection<String>> getter, IStaticSetter<ObjectType, Collection<String>> setter) {
		this(identityMap(options), getter, setter);
	}
	
	public Cell createCell(final IRowForColumn<ObjectType> row) {
		return new MultiselectCell(
				options,
				new IGetter<Collection<String>>() {
					public Collection<String> getValue() { return getter.getValueForObject(row.getObject()); }
				},
				new ISetter<Collection<String>>() {
					public void setValue(Collection<String> newValue) {
						setter.setValueInObject(row.getObject(), newValue);
//						rowChanged(object);
					}
				});
	}
}
