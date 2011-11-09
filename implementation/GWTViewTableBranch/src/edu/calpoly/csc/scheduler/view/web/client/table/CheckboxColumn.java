package edu.calpoly.csc.scheduler.view.web.client.table;

import java.util.Comparator;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Widget;

public class CheckboxColumn<ObjectType extends Comparable<ObjectType>> extends OsmTable.Column<ObjectType> {
	private StaticGetter<ObjectType, Boolean> getter;
	private StaticSetter<ObjectType, Boolean> setter;
	
	private static class CheckboxComparator<ObjectType> implements Comparator<ObjectType> {
		private StaticGetter<ObjectType, Boolean> getter;
			
		public CheckboxComparator(StaticGetter<ObjectType, Boolean> getter) {
			this.getter = getter;
		}

		public int compare(ObjectType a, ObjectType b) {
			boolean aChecked = getter.getValueForObject(a);
			boolean bChecked = getter.getValueForObject(b);
			return (aChecked ? 1 : 0) - (bChecked ? 1 : 0);
		}
	}
	
	public CheckboxColumn(String name, String width, StaticGetter<ObjectType, Boolean> getter, StaticSetter<ObjectType, Boolean> setter) {
		super(name, width, new CheckboxComparator<ObjectType>(getter));
		this.getter = getter;
		this.setter = setter;
	}
	
	public Widget createCellWidget(final ObjectType object) {
		CheckBox box = new CheckBox();
		
		Boolean currentChecked = getter.getValueForObject(object);
		if (currentChecked)
			box.setValue(currentChecked);
		
		box.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				setter.setValueInObject(object, event.getValue());
				objectChanged(object);
			}
		});
		return box;
	}
}
