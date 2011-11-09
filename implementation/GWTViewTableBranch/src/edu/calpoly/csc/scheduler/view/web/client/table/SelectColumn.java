package edu.calpoly.csc.scheduler.view.web.client.table;

import java.util.Comparator;
import java.util.LinkedHashMap;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Widget;

public class SelectColumn<ObjectType extends Comparable<ObjectType>> extends OsmTable.Column<ObjectType> {
	private LinkedHashMap<String, String> options;
	protected StaticGetter<ObjectType, String> getter;
	protected StaticSetter<ObjectType, String> setter;

	public SelectColumn(String name, String width, LinkedHashMap<String, String> options, final StaticGetter<ObjectType, String> getter, StaticSetter<ObjectType, String> setter, final Comparator<String> sorter) {
		super(name, width, sorter == null ? null : new Comparator<ObjectType>() {
			public int compare(ObjectType o1, ObjectType o2) {
				return sorter.compare(getter.getValueForObject(o1), getter.getValueForObject(o2));
			}
		});
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

	public SelectColumn(String name, String width, String[] options, StaticGetter<ObjectType, String> getter, StaticSetter<ObjectType, String> setter, Comparator<String> sorter) {
		this(name, width, identityMap(options), getter, setter, sorter);
	}
	
	public Widget createCellWidget(final ObjectType object) {
		if (!options.values().contains(getter.getValueForObject(object))) {
			Window.alert("value " + getter.getValueForObject(object) + " not in options");
		}
		
		return new SelectWidget(options, new Getter<String>() {
				public String getValue() { return getter.getValueForObject(object); }
			}, new Setter<String>() {
				public void setValue(String newValue) {
					setter.setValueInObject(object, newValue);
					objectChanged(object);
				}
			});
	}
}
