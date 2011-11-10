package edu.calpoly.csc.scheduler.view.web.client.table;

import java.util.Comparator;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;

public class StringColumn<ObjectType extends Comparable<ObjectType>> extends OsmTable.Column<ObjectType> {
	protected StaticGetter<ObjectType, String> getter;
	protected StaticSetter<ObjectType, String> setter;
	
	public StringColumn(String name, String width, final StaticGetter<ObjectType, String> getter, StaticSetter<ObjectType, String> setter, final Comparator<String> sorter) {
		super(name, width, sorter == null ? null : new Comparator<ObjectType>() {
			public int compare(ObjectType o1, ObjectType o2) {
				return sorter.compare(getter.getValueForObject(o1), getter.getValueForObject(o2));
			}
		});
		this.getter = getter;
		this.setter = setter;
	}

	// Checks to see if value is acceptable input. If its not, can alert the user and return false.
	// Feel free to override
	public boolean valid(String newValue) {
		return true;
	}
	
	public Widget createCellWidget(final ObjectType object) {
		if (setter != null) {
			return new StringEditorWidget(new Getter<String>() {
				public String getValue() {
					return getter.getValueForObject(object);
				}
			}, new Setter<String>() {
				public void setValue(String newValue) {
					if (valid(newValue)) {
						setter.setValueInObject(object, newValue);
						objectChanged(object);
					}
				}
			});
		}
		else
			return new HTML(getter.getValueForObject(object));
	}
}
