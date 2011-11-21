package edu.calpoly.csc.scheduler.view.web.client.table;

import java.util.Comparator;

import edu.calpoly.csc.scheduler.view.web.shared.Identified;

// TODO: make the intcolumn handle up and down arrows, thatd be sick.

public class IntColumn<ObjectType extends Identified> extends StringColumn<ObjectType> {
	public IntColumn(String name, String width, final StaticGetter<ObjectType, Integer> getter, final StaticSetter<ObjectType, Integer> setter, final StaticValidator<ObjectType, Integer> validator) {
		super(name, width,
				new StaticGetter<ObjectType, String>() {
					public String getValueForObject(ObjectType object) {
						return getter.getValueForObject(object).toString();
					}
				},
				new StaticSetter<ObjectType, String>() {
					public void setValueInObject(ObjectType object, String newValue) {
						setter.setValueInObject(object, Integer.parseInt(newValue));
					}
				},
				new Comparator<String>() {
					@Override
					public int compare(String o1, String o2) {
						return Integer.parseInt(o1) - Integer.parseInt(o2);
					}
				},
				new StaticValidator<ObjectType, String>() {
					public void validate(ObjectType object, String newValue) throws InvalidValueException {
						new StaticNumberValidator<ObjectType>().validate(object, newValue);
						Integer newNumber = Integer.parseInt(newValue);
						if (validator != null)
							validator.validate(object, newNumber);
					}
				});
	}
}
