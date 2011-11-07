package edu.calpoly.csc.scheduler.view.web.client.table;

public interface Factory<ObjectType> {
	ObjectType create();
	ObjectType createHistoryFor(ObjectType object);
}