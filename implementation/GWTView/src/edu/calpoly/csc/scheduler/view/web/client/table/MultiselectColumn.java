package edu.calpoly.csc.scheduler.view.web.client.table;

import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashMap;

import com.google.gwt.user.client.ui.Widget;

public class MultiselectColumn<ObjectType extends Comparable<ObjectType>> extends OsmTable.Column<ObjectType> {
	private LinkedHashMap<String, String> options;
	protected StaticGetter<ObjectType, Collection<String>> getter;
	protected StaticSetter<ObjectType, Collection<String>> setter;

	public MultiselectColumn(String name, String width, LinkedHashMap<String, String> options, final StaticGetter<ObjectType, Collection<String>> getter, StaticSetter<ObjectType, Collection<String>> setter, final Comparator<ObjectType> sorter) {
		super(name, width, sorter);
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

	public MultiselectColumn(String name, String width, String[] options, StaticGetter<ObjectType, Collection<String>> getter, StaticSetter<ObjectType, Collection<String>> setter, Comparator<ObjectType> sorter) {
		this(name, width, identityMap(options), getter, setter, sorter);
	}
	
	public Widget createCellWidget(final ObjectType object) {
		return new MultiselectWidget(
				options,
				new Getter<Collection<String>>() {
					public Collection<String> getValue() { return getter.getValueForObject(object); }
				},
				new Setter<Collection<String>>() {
					public void setValue(Collection<String> newValue) {
						setter.setValueInObject(object, newValue);
						objectChanged(object);
					}
				});
	}
}
