package edu.calpoly.csc.scheduler.view.web.client.table.columns;

import edu.calpoly.csc.scheduler.view.web.client.table.IStaticGetter;
import edu.calpoly.csc.scheduler.view.web.client.table.IStaticSetter;
import edu.calpoly.csc.scheduler.view.web.client.table.IStaticValidator;
import edu.calpoly.csc.scheduler.view.web.client.table.validators.StaticNumberValidator;
import edu.calpoly.csc.scheduler.view.web.shared.Identified;

// TODO: make the intcolumn handle up and down arrows, thatd be sick.

public class IntColumn<ObjectType extends Identified> extends EditingStringColumn<ObjectType> {
	public IntColumn(final IStaticGetter<ObjectType, Integer> getter, final IStaticSetter<ObjectType, Integer> setter, final IStaticValidator<ObjectType, Integer> validator) {
		super(
				new IStaticGetter<ObjectType, String>() {
					public String getValueForObject(ObjectType object) {
						return getter.getValueForObject(object).toString();
					}
				},
				new IStaticSetter<ObjectType, String>() {
					public void setValueInObject(ObjectType object, String newValue) {
						setter.setValueInObject(object, Integer.parseInt(newValue));
					}
				},
				new IStaticValidator<ObjectType, String>() {
					public void validate(ObjectType object, String newValue) throws InvalidValueException {
						new StaticNumberValidator<ObjectType>().validate(object, newValue);
						Integer newNumber = Integer.parseInt(newValue);
						if (validator != null)
							validator.validate(object, newNumber);
					}
				});
	}
}
