package edu.calpoly.csc.scheduler.view.web.client.table.columns;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.CheckBox;

import edu.calpoly.csc.scheduler.view.web.client.table.IStaticGetter;
import edu.calpoly.csc.scheduler.view.web.client.table.IStaticSetter;
import edu.calpoly.csc.scheduler.view.web.client.table.OsmTable;
import edu.calpoly.csc.scheduler.view.web.client.table.OsmTable.Cell;
import edu.calpoly.csc.scheduler.view.web.client.table.OsmTable.IRowForColumn;
import edu.calpoly.csc.scheduler.view.web.client.table.OsmTable.SimpleCell;
import edu.calpoly.csc.scheduler.view.web.shared.Identified;

public class CheckboxColumn<ObjectType extends Identified> implements OsmTable.IColumn<ObjectType> {
	private IStaticGetter<ObjectType, Boolean> getter;
	private IStaticSetter<ObjectType, Boolean> setter;
	
//	private static class CheckboxComparator<ObjectType> implements Comparator<ObjectType> {
//		private IStaticGetter<ObjectType, Boolean> getter;
//			
//		public CheckboxComparator(IStaticGetter<ObjectType, Boolean> getter) {
//			this.getter = getter;
//		}
//
//		public int compare(ObjectType a, ObjectType b) {
//			boolean aChecked = getter.getValueForObject(a);
//			boolean bChecked = getter.getValueForObject(b);
//			return (aChecked ? 1 : 0) - (bChecked ? 1 : 0);
//		}
//	}
	
	public CheckboxColumn(IStaticGetter<ObjectType, Boolean> getter, IStaticSetter<ObjectType, Boolean> setter) {
//		super(new CheckboxComparator<ObjectType>(getter));
		this.getter = getter;
		this.setter = setter;
	}
	
	public Cell createCell(final IRowForColumn<ObjectType> row) {
		CheckBox box = new CheckBox();
		
		Boolean currentChecked = getter.getValueForObject(row.getObject());
		if (currentChecked)
			box.setValue(currentChecked);
		
		box.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				setter.setValueInObject(row.getObject(), event.getValue());
//				rowChanged(object);
			}
		});
		return new SimpleCell(box);
	}
}
