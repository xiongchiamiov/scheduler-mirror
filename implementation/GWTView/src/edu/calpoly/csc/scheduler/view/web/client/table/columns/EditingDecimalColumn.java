package edu.calpoly.csc.scheduler.view.web.client.table.columns;

import edu.calpoly.csc.scheduler.view.web.client.table.IStaticGetter;
import edu.calpoly.csc.scheduler.view.web.client.table.IStaticSetter;
import edu.calpoly.csc.scheduler.view.web.client.table.IStaticValidator;
import edu.calpoly.csc.scheduler.view.web.client.table.validators.StaticDecimalValidator;
import edu.calpoly.csc.scheduler.view.web.shared.Identified;

// TODO: make the intcolumn handle up and down arrows, thatd be sick.

public class EditingDecimalColumn<ObjectType extends Identified> extends EditingStringColumn<ObjectType> {
	public EditingDecimalColumn(final IStaticGetter<ObjectType, Double> getter, final IStaticSetter<ObjectType, Double> setter, final IStaticValidator<ObjectType, Double> validator) {
		super(
				new IStaticGetter<ObjectType, String>() {
					public String getValueForObject(ObjectType object) {
						return getter.getValueForObject(object).toString();
					}
				},
				new IStaticSetter<ObjectType, String>() {
					public void setValueInObject(ObjectType object, String newValue) {
						setter.setValueInObject(object, Double.parseDouble(newValue));
					}
				},
				new IStaticValidator<ObjectType, String>() {
					public void validate(ObjectType object, String newValue) throws InvalidValueException {
						new StaticDecimalValidator<ObjectType>().validate(object, newValue);
						Double newNumber = Double.parseDouble(newValue);
						if (validator != null)
							validator.validate(object, newNumber);
					}
				});
	}
}
