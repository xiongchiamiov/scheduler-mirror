package edu.calpoly.csc.scheduler.view.web.client.table;

import java.util.Comparator;

public class MemberIntegerComparator<ObjectType> implements Comparator<ObjectType> {
	IStaticGetter<ObjectType, Integer> getter;
	public MemberIntegerComparator(IStaticGetter<ObjectType, Integer> getter) {
		this.getter = getter;
	}
	public int compare(ObjectType o1, ObjectType o2) {
		return getter.getValueForObject(o1) - getter.getValueForObject(o2);
	}
}
