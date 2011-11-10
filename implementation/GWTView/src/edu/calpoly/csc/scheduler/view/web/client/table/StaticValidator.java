package edu.calpoly.csc.scheduler.view.web.client.table;

public interface StaticValidator<ObjectType, ValueType> {
	class InvalidValueException extends Exception {
		private static final long serialVersionUID = 1L;
		public InvalidValueException(String message) { super(message); }
	}
	
	void validate(ObjectType object, ValueType newValue) throws InvalidValueException;
}
