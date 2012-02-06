package edu.calpoly.csc.scheduler.view.web.client.table.validators;

import edu.calpoly.csc.scheduler.view.web.client.table.IStaticValidator;


public class StaticDecimalValidator<ObjectType> implements IStaticValidator<ObjectType, String> {
	public void validate(ObjectType object, String newValue) throws InvalidValueException {
		try {
			Double.parseDouble(newValue);
		}
		catch (NumberFormatException e) {
			throw new InvalidValueException("Invalid integer: " + newValue);
		}
	}
}
