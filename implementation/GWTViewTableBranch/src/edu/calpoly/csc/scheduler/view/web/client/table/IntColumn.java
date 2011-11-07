package edu.calpoly.csc.scheduler.view.web.client.table;

import java.util.Comparator;

import com.google.gwt.user.client.Window;

// TODO: make the intcolumn handle up and down arrows, thatd be sick.

public class IntColumn<ObjectType> extends StringColumn<ObjectType> {
	public IntColumn(String name, String width, final StaticGetter<ObjectType, Integer> getter, final StaticSetter<ObjectType, Integer> setter) {
		super(name, width, new StaticGetter<ObjectType, String>() {
			public String getValueForObject(ObjectType object) {
				return getter.getValueForObject(object).toString();
			}
		}, new StaticSetter<ObjectType, String>() {
			public void setValueInObject(ObjectType object, String newValue) {
				setter.setValueInObject(object, Integer.parseInt(newValue));
			}
		}, new Comparator<String>() {
			@Override
			public int compare(String o1, String o2) {
				return Integer.parseInt(o1) - Integer.parseInt(o2);
			}
		});
	}
	
	@Override
	public boolean valid(String newValue) {
		try {
			Integer.parseInt(newValue);
			return true;
		}
		catch (NumberFormatException e) {
			Window.alert("Invalid integer: " + newValue);
			return false;
		}
	}
}
