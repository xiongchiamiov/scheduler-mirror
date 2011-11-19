package edu.calpoly.csc.scheduler.view.web.client.table;

public interface StaticSetter<ObjectType, ValType> {
	void setValueInObject(ObjectType object, ValType newValue);
}
