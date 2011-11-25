package edu.calpoly.csc.scheduler.view.web.client.table;

public interface IStaticGetter<ObjectType, ValType> {
	ValType getValueForObject(ObjectType object);
}