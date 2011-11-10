package edu.calpoly.csc.scheduler.view.web.client.table;

import java.util.Comparator;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;

import edu.calpoly.csc.scheduler.view.web.client.table.StaticValidator.InvalidValueException;

public class StringColumn<ObjectType extends Comparable<ObjectType>> extends OsmTable.Column<ObjectType> {
	protected StaticGetter<ObjectType, String> getter;
	protected StaticSetter<ObjectType, String> setter;
	protected StaticValidator<ObjectType, String> validator;
	
	public StringColumn(String name, String width, final StaticGetter<ObjectType, String> getter, StaticSetter<ObjectType, String> setter, final Comparator<String> sorter, StaticValidator<ObjectType, String> validator) {
		super(name, width, sorter == null ? null : new Comparator<ObjectType>() {
			public int compare(ObjectType o1, ObjectType o2) {
				return sorter.compare(getter.getValueForObject(o1), getter.getValueForObject(o2));
			}
		});
		this.getter = getter;
		this.setter = setter;
		this.validator = validator;
	}

	public Widget createCellWidget(final ObjectType object) {
		if (setter != null) {
			return new StringEditorWidget(new Getter<String>() {
				public String getValue() {
					return getter.getValueForObject(object);
				}
			}, new Setter<String>() {
				public void setValue(String newValue) {
					try {
						if (validator != null)
							validator.validate(object, newValue);
						setter.setValueInObject(object, newValue);
						objectChanged(object);
					}
					catch (InvalidValueException ex) {
						Window.alert(ex.getMessage());
					}
				}
			});
		}
		else
			return new HTML(getter.getValueForObject(object));
	}
}
