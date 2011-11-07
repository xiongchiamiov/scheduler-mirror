package edu.calpoly.csc.scheduler.view.web.client.table;

public interface StaticGetter<ObjectType, ValType> {
	ValType getValueForObject(ObjectType object);
}