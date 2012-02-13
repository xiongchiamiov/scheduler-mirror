package edu.calpoly.csc.scheduler.view.web.client.table;

import java.util.Comparator;

public class MemberStringComparator<ObjectType> implements Comparator<ObjectType> {
	IStaticGetter<ObjectType, String> getter;
	public MemberStringComparator(IStaticGetter<ObjectType, String> getter) {
		this.getter = getter;
	}
	public int compare(ObjectType o1, ObjectType o2) {
		return getter.getValueForObject(o1).compareTo(getter.getValueForObject(o2));
	}
}
