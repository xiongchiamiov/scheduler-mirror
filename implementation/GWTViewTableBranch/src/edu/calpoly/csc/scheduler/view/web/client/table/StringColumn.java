package edu.calpoly.csc.scheduler.view.web.client.table;

import java.util.Comparator;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;

public class StringColumn<ObjectType extends Comparable<ObjectType>> extends OsmTable.Column<ObjectType> {
	protected StaticGetter<ObjectType, String> getter;
	protected StaticSetter<ObjectType, String> setter;
	protected Comparator<String> sorter;
	
	public StringColumn(String name, String width, StaticGetter<ObjectType, String> getter, StaticSetter<ObjectType, String> setter, Comparator<String> sorter) {
		super(name, width);
		this.getter = getter;
		this.setter = setter;
		this.sorter = sorter;
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
