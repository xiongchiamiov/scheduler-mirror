package edu.calpoly.csc.scheduler.view.web.client.table;


public class StaticNumberValidator<ObjectType> implements StaticValidator<ObjectType, String> {
	public void validate(ObjectType object, String newValue) throws InvalidValueException {
		try {
			Integer.parseInt(newValue);
		}
		catch (NumberFormatException e) {
			throw new InvalidValueException("Invalid integer: " + newValue);
		}
	}
}
