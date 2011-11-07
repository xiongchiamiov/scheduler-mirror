package edu.calpoly.csc.scheduler.view.web.client.table;

import java.util.Comparator;
import java.util.LinkedHashMap;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Widget;

public class SelectColumn<ObjectType> extends OsmTable.ValueColumn<ObjectType, String> {
	private LinkedHashMap<String, String> options;

	public SelectColumn(String name, String width, LinkedHashMap<String, String> options, StaticGetter<ObjectType, String> getter, StaticSetter<ObjectType, String> setter, Comparator<String> sorter) {
		super(name, width, getter, setter, sorter);
		this.options = options;
	}

	public SelectColumn(String name, String width, String[] options, StaticGetter<ObjectType, String> getter, StaticSetter<ObjectType, String> setter, Comparator<String> sorter) {
		super(name, width, getter, setter, sorter);
		this.options = new LinkedHashMap<String, String>();
		for (String option : options)
			this.options.put(option, option);
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
