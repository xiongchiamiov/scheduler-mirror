package edu.calpoly.csc.scheduler.view.web.client.table;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Widget;

public class CheckboxColumn<ObjectType extends Comparable<ObjectType>> extends OsmTable.Column<ObjectType> {
	private StaticGetter<ObjectType, Boolean> getter;
	private StaticSetter<ObjectType, Boolean> setter;
	
	public CheckboxColumn(String name, String width, StaticGetter<ObjectType, Boolean> getter, StaticSetter<ObjectType, Boolean> setter) {
		super(name, width);
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
